package com.dforensic.plugin.manal;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.dforensic.plugin.manal.model.ApiDescriptor;

public class ApiDescriptionPropertySource implements IPropertySource {

	private final ApiDescriptor apiDescription;

	public ApiDescriptionPropertySource(ApiDescriptor apiDesc) {
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
			new TextPropertyDescriptor("return_type", "Return Type"),
			new TextPropertyDescriptor("signature", "Signature"),
		};
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals("method_name")) {
			return apiDescription.getMethodName();
		} else if (id.equals("return_type")) {
			return apiDescription.getReturnType();
		} else if (id.equals("signature")) {
			return apiDescription.getSignature();
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
		} else if (id.equals("return_type")) {
			apiDescription.setReturnType(s);
		} else if (id.equals("signature")) {
			apiDescription.setSignature(s);
		}
	}

}
