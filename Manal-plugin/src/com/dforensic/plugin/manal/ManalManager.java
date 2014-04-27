package com.dforensic.plugin.manal;

import java.util.List;

import soot.jimple.infoflow.InfoflowResults.SinkInfo;

import com.dforensic.plugin.manal.input.flowdroid.FlowDroidExecutor;
import com.dforensic.plugin.manal.input.flowdroid.FlowDroidExecutor.FlowDroidCallback;
import com.dforensic.plugin.manal.model.ApiDescriptor;

public class ManalManager {

	private FlowDroidExecutor mFdExecutor = null;
	private List<ApiDescriptor> mDiscoveredSinks = null;

	private FlowDroidCallback mFdCallback = new FlowDroidCallback() {

		@Override
		public void onExecutionDone() {
			mDiscoveredSinks = mFdExecutor.getDiscoveredSinks();
			displaySinks();
		}

	};

	public void searchSuspiciousApi() {
		mFdExecutor = new FlowDroidExecutor();
		mFdExecutor.registerFlowDroidCallback(mFdCallback);
		mFdExecutor.execute();
	}

	public void displaySinks() {
		if (mDiscoveredSinks != null) {
			for (ApiDescriptor sink : mDiscoveredSinks) {
				if (sink != null) {
					SinkInfo sinkInfo = sink.getSinkInfo();
					if (sinkInfo != null) {
						System.out.println("Discovered sink: " + sinkInfo);
					} else {
						System.err
								.println("Discovered sink does not include information.");
					}
				} else {
					System.err.println("Discovered sink is NULL.");
				}
			}
		} else {
			System.err.println("No sinks discovered to display.");
		}
	}

	public List<ApiDescriptor> getDiscoveredSinks() {
		return mDiscoveredSinks;
	}

}
