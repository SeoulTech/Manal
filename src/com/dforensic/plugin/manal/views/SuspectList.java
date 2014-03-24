package com.dforensic.plugin.manal.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.dforensic.plugin.manal.model.ApiDescription;

public class SuspectList extends ViewPart {
	public static final String ID = "com.dforensic.plugin.manal.views.SuspectList";

	private TableViewer viewer;

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			ApiDescription apiDesc = (ApiDescription) obj;
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
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		getSite().setSelectionProvider(viewer);
		viewer.setInput(getElements());

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	// Build up a simple data model
	// TODO start parsing here
	private ApiDescription[] getElements() {
		ApiDescription[] apiDescAr = new ApiDescription[2];
		ApiDescription apiDesc = new ApiDescription();
		apiDesc.setMethodName("openFile");
		apiDescAr[0] = apiDesc;
		apiDesc = new ApiDescription();
		apiDesc.setMethodName("connectHttp");
		apiDescAr[1] = apiDesc;
		return apiDescAr;
	}
}