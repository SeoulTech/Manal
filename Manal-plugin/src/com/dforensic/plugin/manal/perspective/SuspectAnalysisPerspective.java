package com.dforensic.plugin.manal.perspective;

import org.eclipse.ui.IFolderLayout;
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

	public static final String SUSPECT_ANAL_PERSP_ID = "com.dforensic.plugin.manal.perspective.SuspectAnalysis";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		// IWorkbenchPage page =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// IEditorPart editor = page.getActiveEditor();
		// http://stackoverflow.com/questions/977528/how-do-i-programmatically-resize-an-eclipse-viewpart
		
		
		//layout.addView("org.eclipse.jdt.ui.PackageExplorer", IPageLayout.LEFT,(float) 0.3f, layout.getEditorArea());	
		//layout.addView("org.eclipse.ui.editors", IPageLayout.RIGHT, 0.55f, layout.getEditorArea());
		//layout.addView("left", IPageLayout.LEFT, 0.2f, layout.getEditorArea());
		//layout.addView("right", IPageLayout.RIGHT, 0.01f, layout.getEditorArea());
		//layout.addView("com.dforensic.plugin.manal.views.SuspectListVw", IPageLayout.RIGHT, 0.15f, layout.getEditorArea());	
		//right.addView(IPageLayout.ID_EDITOR_AREA);
		//right.addView("com.dforensic.plugin.manal.views.SuspectListVw");
		//layout.getViewLayout("com.dforensic.plugin.manal.views.SuspectListVw").setMoveable(false);
		//layout.getViewLayout(IPageLayout.ID_EDITOR_AREA).setMoveable(false);
		
		layout.setEditorAreaVisible(true);
		
		
		
	}

}
