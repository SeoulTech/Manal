package com.dforensic.plugin.manal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.dforensic.plugin.manal.model.ApiDescriptor;

public class ApiDescriptionWorkbenchAdapter implements IWorkbenchAdapter {

	@Override
	public Object[] getChildren(Object o) {
		String[] values = new String[1];
		ApiDescriptor apiDesc = (ApiDescriptor) o;
		values[0] = apiDesc.getMethodName();
		return values;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

}
