package com.dforensic.plugin.manal.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * 
 * Refer to the following article
 * http://www.ibm.com/developerworks/ru/library/os-eclipse-custwiz/
 * 
 * @author Zeoo
 * 
 */
public class NewManalProjectWizard extends Wizard implements INewWizard,
		IExecutableExtension {

	private WizardManalPropertiesPage propertiesPage;

	private IWorkbench workbench;
	private IProject project;

	@Override
	public void addPages() {
		propertiesPage = new WizardManalPropertiesPage();
		addPage(propertiesPage);

		super.addPages();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performFinish() {
		boolean res = importProject();

		return res;
	}

	private boolean importProject() {
		String baseDir = propertiesPage.getDecompiledSourceDirectoryName();

		if (baseDir != null) {
			File prjDir = new File(baseDir);

			try {
				IProjectDescription description = ResourcesPlugin
						.getWorkspace()
						.loadProjectDescription(
								new Path(prjDir.getAbsolutePath() + "/.project"));
				project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(description.getName());
				project.create(description, null);
				project.open(null);

				return true;

				//				IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
				//					public String queryOverwrite(String file) {
				//						return ALL;
				//					}
				//				};
				//
				//				ImportOperation importOperation = new ImportOperation(
				//						project.getFullPath(), new File(baseDir),
				//						FileSystemStructureProvider.INSTANCE, overwriteQuery);
				//				importOperation.setCreateContainerStructure(false);
				//				importOperation.run(new NullProgressMonitor());
			} catch (CoreException e) {
				System.err.println("Not valid project to be opened.");
				return false;
			}
		} else {
			System.err
					.println("Directory to import a project is not specified.");
			return false;
		}
	}

	/*
	private void openPerspective(String perspectiveID) {
		IWorkbenchWindow window = MainPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		try {
			PlatformUI.getWorkbench().showPerspective(perspectiveID, window);
		} catch (WorkbenchException e) {
			System.out.println("ERROR! Unable to open Perspective");
			MessageDialog.openError(window.getShell(),
					"Error Opening Perspective",
					"Could not open Perspective with ID: " + perspectiveID);
		}
	}
	*/

}
