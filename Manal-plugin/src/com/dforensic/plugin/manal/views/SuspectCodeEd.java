package com.dforensic.plugin.manal.views;

import java.util.List;

import org.eclipse.jdt.core.IBuffer.ITextEditCapability;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

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
