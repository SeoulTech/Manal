package com.dforensic.plugin.manal.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
