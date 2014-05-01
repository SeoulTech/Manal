package com.dforensic.plugin.manal.views;

import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import soot.jimple.infoflow.InfoflowResults.SinkInfo;
import soot.tagkit.LineNumberTag;

import com.dforensic.plugin.manal.model.ApiDescriptor;
import com.dforensic.plugin.manal.parser.SuspectSearch;

public class SuspectCodeEd {

	private SuspectSearch mSuspectSearch = null;
	
	public SuspectCodeEd() {
		mSuspectSearch = new SuspectSearch();
	}
	
	public void displayCode(ApiDescriptor apiDesc) {
		if (apiDesc != null) {
			SinkInfo sinkInfo = apiDesc.getSinkInfo();
			if (sinkInfo != null) {
				sinkInfo.getContext();
				if (context.hasTag("LineNumberTag"))
	                sb.append(" on line ").append(((LineNumberTag)context.getTag("LineNumberTag")).getLineNumber());

			} else {
				System.err.println("No content for the method to display." +
						" SinkInfo is NULL.");
			}
		} else {
			System.err.println("Code can not be displayed for the non-existing method." +
					" ApiDescriptor is NULL.");
		}
	}
	
	public void openJavaSourceEditor() {
		/*
		List<ApiDescriptor> apiList = mParser.getMethodDescriptions();
		if ((apiList != null) && !apiList.isEmpty()) {
			CompilationUnit cu = apiList.get(0).getCompilatioinUnit();
			if (cu != null) {
				try {
					JavaUI.openInEditor(cu.getJavaElement());
				} catch (PartInitException | JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		*/
	}
	
	private void searchSuspect() {
		
	}
	
}
