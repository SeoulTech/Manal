package com.dforensic.plugin.manal.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * 
 * The class defines a perspective for Manal.
 * 
 * Refer to the development of perspectives.
 * http://www.eclipse.org/articles/using-perspectives/PerspectiveArticle.html
 * 
 * @author Zeoo
 *
 */
public class SuspectAnalysisPerspective implements IPerspectiveFactory {
	
	public static final String SUSPECT_ANAL_PERSP_ID = 
			"com.dforensic.plugin.manal.perspective.SuspectAnalysis";

	@Override
	public void createInitialLayout(IPageLayout layout) {
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		IEditorPart editor = page.getActiveEditor();
		// http://stackoverflow.com/questions/977528/how-do-i-programmatically-resize-an-eclipse-viewpart
}

}
