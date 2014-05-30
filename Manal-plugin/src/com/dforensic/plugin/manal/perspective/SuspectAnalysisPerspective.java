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
