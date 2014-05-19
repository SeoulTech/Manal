package com.dforensic.plugin.manal.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import java.io.*;

import org.eclipse.ui.*;
import com.dforensic.plugin.manal.model.dataApkandProject;

public class ManalNewWizard extends Wizard implements INewWizard {
	private ManalNewWizardPage page;
	private ISelection selection;
	private static String apkName;
	private static String projectName;
	private dataApkandProject dap; 
	public ManalNewWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new ManalNewWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		System.out.println("test");
	/*	IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {*/
					try {
						doFinish(containerName, fileName/*monitor*/);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	/*			} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}*/
		return true;
	}

	private void doFinish(String containerName,String fileName/*IProgressMonitor monitor*/)throws CoreException {
		// create a sample file
		//monitor.beginTask("Creating " + fileName, 2);
		/*IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}*/
		
	/*	IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		*/
		
		//monitor.worked(1);
		//monitor.setTaskName("Opening file for editing...");
	/*	getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});*/
		//monitor.worked(1);
	
	//	this.apkName = containerName;
	//	this.projectName = fileName;
		
		dap = new dataApkandProject();
		dap.setApkName(containerName);
		dap.setProjName(fileName);
		System.out.println("apkName: " +dap.getApkName());
		System.out.println("projname: " + dap.getProjName());  // create project directory. make workplace. automatically import decompliation.
	}

	private InputStream openContentStream() {
		String contents =
			"This is the initial file contents for manal.";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "Manal", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}