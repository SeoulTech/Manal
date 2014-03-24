package com.dforensic.plugin.manal;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.dforensic.plugin.manal.model.ApiDescription;

public class ApiDescriptionPropertySource implements IPropertySource {

	private final ApiDescription apiDescription;

	public ApiDescriptionPropertySource(ApiDescription apiDesc) {
		apiDescription = apiDesc;
	}
	
	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
			new TextPropertyDescriptor("method_name", "Method Name"),	
		};
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals("method_name")) {
			return apiDescription.getMethodName();
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		String s= (String) value;
		if (id.equals("method_name")) {
			apiDescription.setMethodName(s);
		}

	}

}
