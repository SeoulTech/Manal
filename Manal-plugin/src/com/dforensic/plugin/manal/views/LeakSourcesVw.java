package com.dforensic.plugin.manal.views;

import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.dforensic.plugin.manal.model.ApiDescriptor;
import com.dforensic.plugin.manal.parser.SuspectSearch;
import com.dforensic.plugin.manal.parser.XmlManager;

import org.eclipse.jdt.ui.JavaUI;

import soot.jimple.infoflow.InfoflowResults.SinkInfo;

/**
 * Draws a view to display sources related
 * to a selected sink.
 *  
 * @author Zeoo
 *
 */
public class LeakSourcesVw extends ViewPart {
	public static final String ID = "com.dforensic.plugin.manal.views.LeakSources";

//	public interface ApiDescriptorSelection {
//		public abstract void onApiDescriptorSelected(ApiDescriptor apiDesc);
//	}

	// private TableViewer mViewer;
	private ListViewer mSourcesListVw;

	// private SuspectSearch mParser;

	private List<ApiDescriptor> mSources = null;

//	private ApiDescriptorSelection mApiDescSelectioin = null;

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			ApiDescriptor apiDesc = (ApiDescriptor) obj;
			return apiDesc.getMethodName();
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		mSourcesListVw = new ListViewer(parent);
		
		mSourcesListVw.setContentProvider(new ArrayContentProvider());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		mSourcesListVw.getControl().setFocus();
	}

	public void setSources(List<ApiDescriptor> sources) {
		mSources = sources;
	}

	public void showSources() {
		if (mSources != null) {
			mSourcesListVw.setInput(getElements());
		} else {
			System.err.println("Nothing to show, sinks are not initialized.");
		}
	}
	
	private ApiDescriptor[] getElements() {
		if (mSources != null) {
			return mSources.toArray(new ApiDescriptor[mSources.size()]);
		} else {
			System.err.println("No sinks to display. It is NULL.");
			return null;
		}
	}

//	public void registerApiDescriptorSelection(ApiDescriptorSelection listener) {
//		mApiDescSelectioin = listener;
//	}

//	public void unregisterApiDescriptorSelection(ApiDescriptorSelection listener) {
//		mApiDescSelectioin = null;
//	}

}