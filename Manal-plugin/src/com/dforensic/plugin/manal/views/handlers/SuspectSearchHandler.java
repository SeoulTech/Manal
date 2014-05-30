package com.dforensic.plugin.manal.views.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.dforensic.plugin.manal.ManalManager;
import com.dforensic.plugin.manal.model.ProjectProperties;
import com.dforensic.plugin.manal.views.SuspectListVw;
import com.dforensic.plugin.manal.views.myProgressBar;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SuspectSearchHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	private Shell shell;
	public SuspectSearchHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    this.shell = window.getShell();
		if (window != null)
	    {
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable)
	        {
	            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
	            try {
					ProjectProperties.setApkNameVal(
							project.getPersistentProperty(new QualifiedName(ProjectProperties.QUALIFIER,
									ProjectProperties.getApkNameKey())));
					ProjectProperties.setPrjNameVal(
							project.getPersistentProperty(new QualifiedName(ProjectProperties.QUALIFIER,
									ProjectProperties.getPrjNameKey())));
					ProjectProperties.setAndroidPathVal(
							project.getPersistentProperty(new QualifiedName(ProjectProperties.QUALIFIER,
									ProjectProperties.getAndroidPathKey())));
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
		
		ManalManager manager = new ManalManager();
		/*
		Display  display  =  PlatformUI.getWorkbench().getDisplay();
        Shell  shell  =  new  Shell(display);
        shell.setLayout(new  GridLayout());
        shell.setSize(150,60);
        ProgressBar  pb  =  new  ProgressBar(shell,  SWT.HORIZONTAL  |  SWT.SMOOTH);
        pb.setLayoutData(new  GridData(GridData.FILL_HORIZONTAL));
        pb.setMinimum(0);
        pb.setMaximum(30);
        
        new myProgressBar(display,pb).start();
        shell.open();
        while  (!shell.isDisposed())  {
            if  (!display.readAndDispatch())  {
                    //display.sleep();
            	manager.searchSuspiciousApi();
            }
        }*/
      
		manager.searchSuspiciousApi();
		
		/*
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
	    IWorkbenchPage page = window.getActivePage();
	    SuspectListVw view = (SuspectListVw) page.findView(SuspectListVw.ID);
		view.openJavaSourceEditor();
	    MessageDialog.openInformation(
				window.getShell(),
				"Manal",
				"Hello, Eclipse world");
		*/		
		return null;
	}
}
