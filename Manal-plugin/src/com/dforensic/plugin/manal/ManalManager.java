/*
 *  <Manal project is an eclipse plugin for the automation of malware analysis.>
 *  Copyright (C) <2014>  <Nikolay Akatyev, Hojun Son>
 *  This file is part of Manal project.
 *
 *  Manal project is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *  Manal project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Manal project. If not, see <http://www.gnu.org/licenses/>.
 *  
 *  Contact information of contributors:
 *  - Nikolay Akatyev: nikolay.akatyev@gmail.com
 *  - Hojun Son: smuoon4680@gmail.com
 */
package com.dforensic.plugin.manal;

import java.util.List;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

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
