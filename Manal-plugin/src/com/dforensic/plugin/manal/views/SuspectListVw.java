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
package com.dforensic.plugin.manal.views;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.dforensic.plugin.manal.model.ApiDescriptor;

/**
 * Draws a view to display found sinks and sources.
 * Refer to articles about UI elements in eclipse:
 * http://www.vogella.com/tutorials/EclipseJFace/article.html
 * http://www.vogella.com/tutorials/SWT/article.html
 * http://www.vogella.com/tutorials/EclipseJFaceTable/article.html
 * 
 * @author Zeoo
 *
 */
public class SuspectListVw extends ViewPart {
	public static final String ID = "com.dforensic.plugin.manal.views.SuspectList";

	public interface ApiDescriptorSelection {
		public abstract void onApiDescriptorSelected(ApiDescriptor apiDesc);
	}
	
	private LeakSourcesVw leakSourcesVw;

	// private TableViewer mViewer;
	private ListViewer mSinksListVw;

	// private SuspectSearch mParser;

	private List<ApiDescriptor> mSinks = null;

	private ApiDescriptorSelection mApiDescSelectioin = null;

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
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        IWorkbenchPage page = win.getActivePage();
		
		leakSourcesVw = (LeakSourcesVw) page.findView(LeakSourcesVw.ID);
		
		/*
		mParser = new SuspectSearch();

		XmlManager xmlManager = new XmlManager();
		xmlManager.readApiDescriptor("suspicious_api_in.xml");
		List<ApiDescriptor> parsedApi = xmlManager.getParsedApi();
		System.out.println(">>parsed API:\n\n");
		for (ApiDescriptor desc : parsedApi) {
			System.out.println(desc.toString());
		}

		mParser.setSuspectApi(parsedApi);
		mParser.run();

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		getSite().setSelectionProvider(viewer);
		viewer.setInput(getElements());
		*/
		mSinksListVw = new ListViewer(parent);
		
		mSinksListVw.setContentProvider(new ArrayContentProvider());
		
		// viewer.setLabelProvider(new ViewLabelProvider());
		// getSite().setSelectionProvider(mSinksListVw);

		mSinksListVw
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) mSinksListVw
								.getSelection();
						ApiDescriptor firstElement = (ApiDescriptor) selection
								.getFirstElement();
						if (mApiDescSelectioin != null) {
							mApiDescSelectioin.onApiDescriptorSelected(firstElement);
						}
						if (firstElement != null) {
							leakSourcesVw.setSources(firstElement.getDependencyList());
							leakSourcesVw.showSources();
						}
					}
				});
		
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// mViewer.getControl().setFocus();
		mSinksListVw.getControl().setFocus();
	}

	public void setSinks(List<ApiDescriptor> sinks) {
		mSinks = sinks;
	}

	public void showSinks() {
		if (mSinks != null) {
			mSinksListVw.setInput(getElements());  //substring으로 잘라서 파라미터 함수 이름 split 구분.
			
			/*
			for (ApiDescriptor sink : mSinks) {
				if (sink != null) {
					SinkInfo sinkInfo = sink.getSinkInfo();
					if (sinkInfo != null) {
						System.out.println("Discovered sink: " + sinkInfo);
						// Show in the list
					} else {
						System.err
								.println("Discovered sink does not include information.");
					}
				} else {
					System.err.println("Discovered sink is NULL.");
				}
			}
			*/
		} else {
			System.err.println("Nothing to show, sinks are not initialized.");
		}
	}
	
	private ApiDescriptor[] getElements() {
		if (mSinks != null) {
			return mSinks.toArray(new ApiDescriptor[mSinks.size()]);
		} else {
			System.err.println("No sinks to display. It is NULL.");
			return null;
		}
	}

	//	// Build up a simple data model
	//	// TODO start parsing here
	//	private ApiDescriptor[] getElements() {
	//		if (mParser != null) {
	//			List<ApiDescriptor> methods = mParser.getMethodDescriptions();
	//			if (methods != null) {
	//				return methods.toArray(new ApiDescriptor[methods.size()]);
	//			} else {
	//				System.out.println(">>error: Methods are not extracted. NULL.");
	//				return null;
	//			}
	//		} else {
	//			System.out.println(">>error: Parser is not initialized.");
	//			return null;
	//		}
	//		/*
	//		 * ApiDescription[] apiDescAr = new ApiDescription[2]; ApiDescription
	//		 * apiDesc = new ApiDescription(); apiDesc.setMethodName("openFile");
	//		 * apiDescAr[0] = apiDesc; apiDesc = new ApiDescription();
	//		 * apiDesc.setMethodName("connectHttp"); apiDescAr[1] = apiDesc; return
	//		 * apiDescAr;
	//		 */
	//	}

	public void registerApiDescriptorSelection(ApiDescriptorSelection listener) {
		mApiDescSelectioin = listener;
	}

	public void unregisterApiDescriptorSelection(ApiDescriptorSelection listener) {
		mApiDescSelectioin = null;
	}

}