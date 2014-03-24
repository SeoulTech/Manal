package com.dforensic.plugin.manal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.dforensic.plugin.manal.model.ApiDescription;

public class ApiDescriptionAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if ((adapterType == IPropertySource.class) &&
				(adaptableObject instanceof ApiDescription)) {
			return new ApiDescriptionPropertySource((ApiDescription) adaptableObject);
		}
		
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
