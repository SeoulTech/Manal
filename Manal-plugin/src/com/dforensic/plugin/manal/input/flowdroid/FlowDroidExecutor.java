package com.dforensic.plugin.manal.input.flowdroid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.dforensic.plugin.manal.model.ApiDescriptor;
import com.dforensic.plugin.manal.model.ProjectProperties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.xmlpull.v1.XmlPullParserException;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.IInfoflow.CallgraphAlgorithm;
import soot.jimple.infoflow.InfoflowResults.SinkInfo;
import soot.jimple.infoflow.InfoflowResults.SourceInfo;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.AndroidSourceSinkManager.LayoutMatchingMode;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

public class FlowDroidExecutor {

	public interface FlowDroidCallback {
		public void onExecutionDone();
	}
	
	/**
	 * path to apk-file
	 */
	private String mApkPath = null;
	/**
	 * path to android-dir (path/android-platforms/)
	 */
	private String mAndroidSdkPath = null;

	private class FlowDroidResultsAvailableHandler implements
			ResultsAvailableHandler {
		private final BufferedWriter wr;

		private FlowDroidResultsAvailableHandler() {
			this.wr = null;
		}

		private FlowDroidResultsAvailableHandler(BufferedWriter wr) {
			this.wr = wr;
		}	

		private void print(String string) {
			try {
				System.out.println(string);
				if (wr != null)
					wr.write(string + "\n");
			} catch (IOException ex) {
				// ignore
			}
		}

		@Override
		public void onResultsAvailable(IInfoflowCFG cfg, InfoflowResults results) {
			if (mSinks == null) {
				mSinks = new ArrayList<ApiDescriptor>();
			}
			// Dump the results
			if (results == null) {
				print("No results found.");
			} else {
				for (SinkInfo sink : results.getResults().keySet()) {
					ApiDescriptor sinkDesc = new ApiDescriptor(sink);
					sinkDesc.setSootMethod(cfg.getMethodOf(sink.getContext()));
					mSinks.add(sinkDesc);
					print("Found a flow to sink " + sink
							+ ", from the following sources:");
					for (SourceInfo source : results.getResults().get(sink)) {
						ApiDescriptor sourceDesc = new ApiDescriptor(source);
						sourceDesc.setSootMethod(cfg.getMethodOf(source.getContext()));
						// sourceDesc.addDependency(sinkDesc);
						sinkDesc.addDependency(sourceDesc);
						// mSources.add(sourceDesc);
						print("\t- "
								+ source.getSource()
								+ " (in "
								+ cfg.getMethodOf(source.getContext())
										.getSignature() + ")");
						if (source.getPath() != null
								&& !source.getPath().isEmpty())
							print("\t\ton Path " + source.getPath());
					}
				}
			}
		}			
	}
	
	private List<ApiDescriptor> mSinks = null;
	// not use: there would be many repeating sources.	
	// make a UUID: signature + class + line
	// private List<ApiDescriptor> mSources = null;
	
	private FlowDroidCallback mFlowDroidCallback = null;

	private String command;
	private boolean generate = false;

	private int timeout = -1;
	private int sysTimeout = -1;

	private boolean stopAfterFirstFlow = false;
	private boolean implicitFlows = false;
	private boolean staticTracking = false; // true;
	private boolean enableCallbacks = false; // true;
	private boolean enableExceptions = false; // true;
	private int accessPathLength = 5;
	private LayoutMatchingMode layoutMatchingMode = LayoutMatchingMode.MatchSensitiveOnly;
	private boolean flowSensitiveAliasing = false; // true;
	private boolean computeResultPaths = true;
	private boolean aggressiveTaintWrapper = false;

	private CallgraphAlgorithm callgraphAlgorithm = CallgraphAlgorithm.AutomaticSelection;

	private static boolean DEBUG = false;
	
	public void registerFlowDroidCallback(FlowDroidCallback cb) {
		mFlowDroidCallback = cb;
	}
	
	public void unregisterFlowDroidCallback(FlowDroidCallback cb) {
		mFlowDroidCallback = null;
	}
	
	public List<ApiDescriptor> getDiscoveredSinks() {
		return mSinks;
	}

	public String callgraphAlgorithmToString(CallgraphAlgorithm algorihm) {
		switch (algorihm) {
		case AutomaticSelection:
			return "AUTO";
		case VTA:
			return "VTA";
		case RTA:
			return "RTA";
		default:
			return "unknown";
		}
	}

	public String layoutMatchingModeToString(LayoutMatchingMode mode) {
		switch (mode) {
		case NoMatch:
			return "NONE";
		case MatchSensitiveOnly:
			return "PWD";
		case MatchAll:
			return "ALL";
		default:
			return "unknown";
		}
	}

	private InfoflowResults runAnalysis(final String fileName,
			final String androidJar) {
		try {
			final long beforeRun = System.nanoTime();

			final SetupApplication app = new SetupApplication(androidJar,
					fileName);

			app.setStopAfterFirstFlow(stopAfterFirstFlow);
			app.setEnableImplicitFlows(implicitFlows);
			app.setEnableStaticFieldTracking(staticTracking);
			app.setEnableCallbacks(enableCallbacks);
			app.setEnableExceptionTracking(enableExceptions);
			app.setAccessPathLength(accessPathLength);
			app.setLayoutMatchingMode(layoutMatchingMode);
			app.setFlowSensitiveAliasing(flowSensitiveAliasing);
			app.setComputeResultPaths(computeResultPaths);
		
			final EasyTaintWrapper taintWrapper;
			if (new File("../soot-infoflow/EasyTaintWrapperSource.txt")
					.exists())
				taintWrapper = new EasyTaintWrapper(
						"../soot-infoflow/EasyTaintWrapperSource.txt");
			else
				taintWrapper = new EasyTaintWrapper(
						"EasyTaintWrapperSource.txt");
			taintWrapper.setAggressiveMode(aggressiveTaintWrapper);
			app.setTaintWrapper(taintWrapper);
			app.calculateSourcesSinksEntrypoints("SourcesAndSinks.txt");

			if (DEBUG) {
				app.printEntrypoints();
				app.printSinks();
				app.printSources();
			}

			System.out.println("Running data flow analysis...");
			final InfoflowResults res = app
					.runInfoflow(new FlowDroidResultsAvailableHandler());
			System.out.println("Analysis has run for "
					+ (System.nanoTime() - beforeRun) / 1E9 + " seconds");
			if (mFlowDroidCallback != null) {
				mFlowDroidCallback.onExecutionDone();
			}
			return res;
		} catch (IOException ex) {
			System.err.println("Could not read file: " + ex.getMessage());
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} catch (XmlPullParserException ex) {
			System.err.println("Could not parse xml: " + ex.getMessage());
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		
		
	}

	public void execute() {
		
		mApkPath = ProjectProperties.getApkNameVal();
		mAndroidSdkPath = ProjectProperties.getAndroidPathVal();
		
		if (mApkPath == null) {
			mApkPath = new String(
					"D:\\Workspaces\\SsSDK\\contest_dev\\Manal\\PhoneDataLeakTest\\bin\\PhoneDataLeakTest.apk");
		}
		if (mAndroidSdkPath == null) {
			mAndroidSdkPath = new String(
					"D:\\Workspaces\\android-sdk\\platforms");
		}
		
		if (mSinks != null) {
			mSinks.clear();
			mSinks = null;
		}

		File apkFile = new File(mApkPath);
		String extension = apkFile.getName().substring(
				apkFile.getName().lastIndexOf("."));
		if (apkFile.isDirectory() || !extension.equalsIgnoreCase(".apk")) {
			System.err.println("Invalid input file format: " + extension);
			return;
		}

		runAnalysis(mApkPath, mAndroidSdkPath);

		System.gc();
	}

	public void setApkPath(String path) {
		mApkPath = path;
	}

	public void setAndroidSdkPath(String path) {
		mAndroidSdkPath = path;
	}

}
