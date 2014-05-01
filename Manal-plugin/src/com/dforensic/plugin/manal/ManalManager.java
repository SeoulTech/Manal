package com.dforensic.plugin.manal;

import java.util.List;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import soot.jimple.infoflow.InfoflowResults.SinkInfo;

import com.dforensic.plugin.manal.input.flowdroid.FlowDroidExecutor;
import com.dforensic.plugin.manal.input.flowdroid.FlowDroidExecutor.FlowDroidCallback;
import com.dforensic.plugin.manal.model.ApiDescriptor;
import com.dforensic.plugin.manal.views.SuspectCodeEd;
import com.dforensic.plugin.manal.views.SuspectListVw;

public class ManalManager {

	private SuspectListVw mSuspectListVw = null; 
	private SuspectCodeEd mSuspectCodeEd = null;
	
	private FlowDroidExecutor mFdExecutor = null;
	private List<ApiDescriptor> mDiscoveredSinks = null;

	private FlowDroidCallback mFdCallback = new FlowDroidCallback() {

		@Override
		public void onExecutionDone() {
			mDiscoveredSinks = mFdExecutor.getDiscoveredSinks();
			displaySinks();
		}

	};
	
	public ManalManager() {
		mSuspectCodeEd = new SuspectCodeEd();
		
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        IWorkbenchPage page = win.getActivePage();

	    mSuspectListVw = (SuspectListVw) page.findView(SuspectListVw.ID);
	    mSuspectListVw.registerApiDescriptorSelection(new SuspectListVw.ApiDescriptorSelection() {
			
			@Override
			public void onApiDescriptorSelected(ApiDescriptor apiDesc) {
				mSuspectCodeEd.displayCode(apiDesc);
			}
		});
	}

	public void searchSuspiciousApi() {
		mFdExecutor = new FlowDroidExecutor();
		mFdExecutor.registerFlowDroidCallback(mFdCallback);
		mFdExecutor.execute();
	}

	public void displaySinks() {
	    mSuspectListVw.setSinks(mDiscoveredSinks);
		mSuspectListVw.showSinks();
	}

	public List<ApiDescriptor> getDiscoveredSinks() {
		return mDiscoveredSinks;
	}

}
