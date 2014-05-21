package com.dforensic.plugin.manal.perspective;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

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

	@Override
	public void createInitialLayout(IPageLayout layout) {
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		IEditorPart editor = page.getActiveEditor();
		// http://stackoverflow.com/questions/977528/how-do-i-programmatically-resize-an-eclipse-viewpart
}

}
