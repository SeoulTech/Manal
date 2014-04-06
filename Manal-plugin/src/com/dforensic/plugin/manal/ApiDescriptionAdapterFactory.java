package com.dforensic.plugin.manal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.dforensic.plugin.manal.model.ApiDescriptor;

public class ApiDescriptionAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if ((adapterType == IPropertySource.class) &&
				(adaptableObject instanceof ApiDescriptor)) {
			return new ApiDescriptionPropertySource((ApiDescriptor) adaptableObject);
		}
		
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
