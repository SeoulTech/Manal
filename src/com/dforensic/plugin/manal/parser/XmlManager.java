package com.dforensic.plugin.manal.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class XmlManager {
    
    /** Components of the API description in the xml. */
    static final String ROOT_TAG = "data_tag";
    static final String METHOD_TAG = "method_tag";
    static final String PACKAGE_TAG = "package_tag";
    static final String CLASS_TAG = "class_tag";
    static final String METHOD_TYPE_TAG = "type_tag";
    static final String RETURN_TYPE_TAG = "return_tag";
    static final String OVERLOADING_TAG = "overloading_tag";
    static final String SET_ATTR = "set_attr";
    static final String PARAMETER_TAG = "parameter_tag";
    static final String NAME_ATTR = "name_attr";
    static final String TYPE_ATTR = "type_attr";
    static final String POS_ATTR = "pos_attr";

    private String mPath = "platform:/plugin/com.dforensic.plugin.manal/files/";

    public XmlManager() {
        // TODO Auto-generated constructor stub
    }

    public void readApiDescription(String xmlName) {
        try {
            URL url = new URL(mPath + xmlName);
            InputStream inputStream = url.openConnection().getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
