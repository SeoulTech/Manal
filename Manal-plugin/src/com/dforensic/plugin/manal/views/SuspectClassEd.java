package com.dforensic.plugin.manal.views;

import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import com.dforensic.plugin.manal.model.ApiDescriptor;

public class SuspectClassEd {

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
