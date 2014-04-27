package com.dforensic.plugin.manal.views.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

import com.dforensic.plugin.manal.ManalManager;
import com.dforensic.plugin.manal.views.SuspectListVw;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SuspectSearchHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SuspectSearchHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ManalManager manager = new ManalManager();
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
