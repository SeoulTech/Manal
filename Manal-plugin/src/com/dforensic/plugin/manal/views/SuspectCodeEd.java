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
package com.dforensic.plugin.manal.views;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;

import soot.jimple.infoflow.InfoflowResults.SinkInfo;

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
				SuspectSearch search = new SuspectSearch();
				search.getJavaElementForApi(apiDesc);
				openJavaSourceEditor(apiDesc);
			} else {
				System.err.println("No content for the method to display." +
						" SinkInfo is NULL.");
			}
		} else {
			System.err.println("Code can not be displayed for the non-existing method." +
					" ApiDescriptor is NULL.");
		}
	}
	
	public void openJavaSourceEditor(ApiDescriptor api) {
		if (api != null) {
			CompilationUnit cu = api.getCompilatioinUnit();
			if (cu != null) {
				try {
					// According to the guide
					// http://eclipsesnippets.blogspot.kr/2008/06/programmatically-opening-editor.html
					ITextEditor editor = (ITextEditor)JavaUI.openInEditor(cu.getJavaElement());
					int line = api.getLineNumFromSoot();
					
					if (line > 0) {
						IDocument document= editor.getDocumentProvider().getDocument(editor.getEditorInput());
						editor.selectAndReveal(document.getLineOffset(line - 1), document.getLineLength(line-1));
					} else {
						System.err.println("Not valid line number. It is not positive.");
					}
				} catch (PartInitException | JavaModelException | BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.err.println("Can't open java editor. ApiDescriptor is NULL.");
		}
	}
	
	private void searchSuspect() {
		
	}
	
}
