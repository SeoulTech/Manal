package com.dforensic.plugin.manal.input.flowdroid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.dforensic.plugin.manal.model.ApiDescriptor;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
					mSinks.add(sinkDesc);
					print("Found a flow to sink " + sink
							+ ", from the following sources:");
					for (SourceInfo source : results.getResults().get(sink)) {
						ApiDescriptor sourceDesc = new ApiDescriptor(source);
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
	private boolean staticTracking = true;
	private boolean enableCallbacks = true;
	private boolean enableExceptions = true;
	private int accessPathLength = 5;
	private LayoutMatchingMode layoutMatchingMode = LayoutMatchingMode.MatchSensitiveOnly;
	private boolean flowSensitiveAliasing = true;
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

	/*
	private static boolean parseAdditionalOptions(String[] args) {
		int i = 2;
		while (i < args.length) {
			if (args[i].equalsIgnoreCase("--timeout")) {
				timeout = Integer.valueOf(args[i + 1]);
				i += 2;
			} else if (args[i].equalsIgnoreCase("--systimeout")) {
				sysTimeout = Integer.valueOf(args[i + 1]);
				i += 2;
			} else if (args[i].equalsIgnoreCase("--singleflow")) {
				stopAfterFirstFlow = true;
				i++;
			} else if (args[i].equalsIgnoreCase("--implicit")) {
				implicitFlows = true;
				i++;
			} else if (args[i].equalsIgnoreCase("--nostatic")) {
				staticTracking = false;
				i++;
			} else if (args[i].equalsIgnoreCase("--aplength")) {
				accessPathLength = Integer.valueOf(args[i + 1]);
				i += 2;
			} else if (args[i].equalsIgnoreCase("--cgalgo")) {
				String algo = args[i + 1];
				if (algo.equalsIgnoreCase("AUTO"))
					callgraphAlgorithm = CallgraphAlgorithm.AutomaticSelection;
				else if (algo.equalsIgnoreCase("VTA"))
					callgraphAlgorithm = CallgraphAlgorithm.VTA;
				else if (algo.equalsIgnoreCase("RTA"))
					callgraphAlgorithm = CallgraphAlgorithm.RTA;
				else {
					System.err.println("Invalid callgraph algorithm");
					return false;
				}
				i += 2;
			} else if (args[i].equalsIgnoreCase("--nocallbacks")) {
				enableCallbacks = false;
				i++;
			} else if (args[i].equalsIgnoreCase("--noexceptions")) {
				enableExceptions = false;
				i++;
			} else if (args[i].equalsIgnoreCase("--layoutmode")) {
				String algo = args[i + 1];
				if (algo.equalsIgnoreCase("NONE"))
					layoutMatchingMode = LayoutMatchingMode.NoMatch;
				else if (algo.equalsIgnoreCase("PWD"))
					layoutMatchingMode = LayoutMatchingMode.MatchSensitiveOnly;
				else if (algo.equalsIgnoreCase("ALL"))
					layoutMatchingMode = LayoutMatchingMode.MatchAll;
				else {
					System.err.println("Invalid layout matching mode");
					return false;
				}
				i += 2;
			} else if (args[i].equalsIgnoreCase("--aliasflowins")) {
				flowSensitiveAliasing = false;
				i++;
			} else if (args[i].equalsIgnoreCase("--nopaths")) {
				computeResultPaths = false;
				i++;
			} else if (args[i].equalsIgnoreCase("--aggressivetw")) {
				aggressiveTaintWrapper = false;
				i++;
			} else
				i++;
		}
		return true;
	}

	private static boolean validateAdditionalOptions() {
		if (timeout > 0 && sysTimeout > 0) {
			return false;
		}
		return true;
	}

	private static void runAnalysisTimeout(final String fileName,
			final String androidJar) {
		FutureTask<InfoflowResults> task = new FutureTask<InfoflowResults>(
				new Callable<InfoflowResults>() {

					@Override
					public InfoflowResults call() throws Exception {

						final BufferedWriter wr = new BufferedWriter(
								new FileWriter("_out_"
										+ new File(fileName).getName() + ".txt"));
						try {
							final long beforeRun = System.nanoTime();
							wr.write("Running data flow analysis...\n");
							final InfoflowResults res = runAnalysis(fileName,
									androidJar);
							wr.write("Analysis has run for "
									+ (System.nanoTime() - beforeRun) / 1E9
									+ " seconds\n");

							wr.flush();
							return res;
						} finally {
							if (wr != null)
								wr.close();
						}
					}

				});
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(task);

		try {
			System.out.println("Running infoflow task...");
			task.get(timeout, TimeUnit.MINUTES);
		} catch (ExecutionException e) {
			System.err
					.println("Infoflow computation failed: " + e.getMessage());
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.err.println("Infoflow computation timed out: "
					+ e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("Infoflow computation interrupted: "
					+ e.getMessage());
			e.printStackTrace();
		}

		// Make sure to remove leftovers
		executor.shutdown();
	}

	private static void runAnalysisSysTimeout(final String fileName,
			final String androidJar) {
		String classpath = System.getProperty("java.class.path");
		String javaHome = System.getProperty("java.home");
		String executable = "/usr/bin/timeout";
		String[] command = new String[] { executable, "-s", "KILL",
				sysTimeout + "m", javaHome + "/bin/java", "-cp", classpath,
				"soot.jimple.infoflow.android.TestApps.Test", fileName,
				androidJar,
				stopAfterFirstFlow ? "--singleflow" : "--nosingleflow",
				implicitFlows ? "--implicit" : "--noimplicit",
				staticTracking ? "--static" : "--nostatic", "--aplength",
				Integer.toString(accessPathLength), "--cgalgo",
				callgraphAlgorithmToString(callgraphAlgorithm),
				enableCallbacks ? "--callbacks" : "--nocallbacks",
				enableExceptions ? "--exceptions" : "--noexceptions",
				"--layoutmode", layoutMatchingModeToString(layoutMatchingMode),
				flowSensitiveAliasing ? "--aliasflowsens" : "--aliasflowins",
				computeResultPaths ? "--paths" : "--nopaths",
				aggressiveTaintWrapper ? "--aggressivetw" : "--nonaggressivetw" };
		System.out.println("Running command: " + executable + " " + command);
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectOutput(new File("_out_" + new File(fileName).getName()
					+ ".txt"));
			pb.redirectError(new File("err_" + new File(fileName).getName()
					+ ".txt"));
			Process proc = pb.start();
			proc.waitFor();
		} catch (IOException ex) {
			System.err.println("Could not execute timeout command: "
					+ ex.getMessage());
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			System.err.println("Process was interrupted: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	*/

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

	/*
	private static void printUsage() {
		System.out
				.println("FlowDroid (c) Secure Software Engineering Group @ EC SPRIDE");
		System.out.println();
		System.out
				.println("Incorrect arguments: [0] = apk-file, [1] = android-jar-directory");
		System.out.println("Optional further parameters:");
		System.out.println("\t--TIMEOUT n Time out after n seconds");
		System.out
				.println("\t--SYSTIMEOUT n Hard time out (kill process) after n seconds, Unix only");
		System.out.println("\t--SINGLEFLOW Stop after finding first leak");
		System.out.println("\t--IMPLICIT Enable implicit flows");
		System.out.println("\t--NOSTATIC Disable static field tracking");
		System.out.println("\t--NOEXCEPTIONS Disable exception tracking");
		System.out.println("\t--APLENGTH n Set access path length to n");
		System.out.println("\t--CGALGO x Use callgraph algorithm x");
		System.out.println("\t--NOCALLBACKS Disable callback analysis");
		System.out
				.println("\t--LAYOUTMODE x Set UI control analysis mode to x");
		System.out
				.println("\t--ALIASFLOWINS Use a flow insensitive alias search");
		System.out.println("\t--NOPATHS Do not compute result paths");
		System.out
				.println("\t--AGGRESSIVETW Use taint wrapper in aggressive mode");
		System.out.println();
		System.out.println("Supported callgraph algorithms: AUTO, RTA, VTA");
		System.out.println("Supported layout mode algorithms: NONE, PWD, ALL");
	}
	*/

	public void execute() {
		// mApkPath = new String(
		// 		"D:\\Documents\\Research\\eclipse_plugin\\Manal\\PhoneDataLeakTest\\bin\\PhoneDataLeakTest.apk");
		// mAndroidSdkPath = new String(
		//		"D:\\Documents\\LGE MC 5PM 1PL\\android-sdk_r10-windows\\platforms");
		mApkPath = new String(
				"D:\\Workspaces\\SsSDK\\contest_dev\\Manal\\PhoneDataLeakTest\\bin\\PhoneDataLeakTest.apk");
		mAndroidSdkPath = new String(
				"D:\\Workspaces\\android-sdk\\platforms");
		
		if (mSinks != null) {
			mSinks.clear();
			mSinks = null;
		}
		/*
		if (mSources != null) {
			mSources.clear();
			mSources = null;
		}
		*/

		// start with cleanup:
		// TODO when store output
		/*
		BufferedReader outputDir = ResUtils.openProjectFile(OUTPUT_PATH + "JimpleOutput");
		if (outputDir.isDirectory()) {
			boolean success = true;
			for (File f : outputDir.listFiles()) {
				success = success && f.delete();
			}
			if (!success) {
				System.err.println("Cleanup of output directory " + outputDir
						+ " failed!");
			}
			outputDir.delete();
		}
		*/

		File apkFile = new File(mApkPath);
		String extension = apkFile.getName().substring(
				apkFile.getName().lastIndexOf("."));
		if (apkFile.isDirectory() || !extension.equalsIgnoreCase(".apk")) {
			System.err.println("Invalid input file format: " + extension);
			return;
		}

		// Run the analysis
		// TODO until do optimization
		// run with default parameters
		/*
		if (timeout > 0)
			runAnalysisTimeout(mApkPath, mAndroidSdkPath);
		else if (sysTimeout > 0)
			runAnalysisSysTimeout(mApkPath, mAndroidSdkPath);
		else
		*/
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
