package com.dforensic.plugin.manal.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.dforensic.plugin.manal.model.ApiDescriptor;
import com.dforensic.plugin.manal.model.ApiDescriptor.MethodType;
import com.dforensic.plugin.manal.model.ApiDescriptor.ParameterDescriptor;

/**
 * Class to parse an xml with description
 * of suspicious API.
 * 
 * TODO store extracted API from a source
 *      into an xml.
 *      
 * Refer to
 * 	http://www.vogella.com/tutorials/JavaXML/article.html
 *  http://blog.vogella.com/2010/07/06/reading-resources-from-plugin/
 * 
 * @author Zeoo
 *
 */
public class XmlManager {

	/** Components of the API description in the xml. */
	private static final String ROOT_TAG = "data";
	private static final String METHOD_TAG = "method";
	private static final String PACKAGE_TAG = "package";
	private static final String CLASS_TAG = "class";
	private static final String METHOD_TYPE_TAG = "type";
	private static final String RETURN_TYPE_TAG = "return";
	private static final String OVERLOADING_TAG = "overloading";
	private static final String SET_ATTR = "set";
	private static final String PARAMETER_TAG = "parameter";
	private static final String NAME_ATTR = "name";
	private static final String TYPE_ATTR = "type";
	private static final String POS_ATTR = "pos";

	private String mPath = "platform:/plugin/com.dforensic.plugin.manal/files/";

	private List<ApiDescriptor> mParsedApi = null;

	public XmlManager() {
		// TODO Auto-generated constructor stub
	}

	public void readApiDescriptor(String xmlName) {
		try {
			URL url = new URL(mPath + xmlName);
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));

			parseXml(in);

			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseXml(BufferedReader in) {
		mParsedApi = new ArrayList<ApiDescriptor>();
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

			List<ApiDescriptor> apiDescOver = null;
			// Same items for overloaded methods.
			String methodName = null;
			String packageName = null;
			String className = null;
			MethodType methodType = MethodType.NORMAL;
			String returnType = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart()
							.equals(METHOD_TAG)) {
						apiDescOver = new ArrayList<ApiDescriptor>();
						Iterator<Attribute> attributes = startElement
								.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString()
									.equals(NAME_ATTR)) {
								methodName = attribute.getValue();
							}
						}
					}

					if (startElement.getName().getLocalPart()
							.equals(PACKAGE_TAG)) {
						event = eventReader.nextEvent();
						packageName = event.asCharacters().getData();
					}

					if (startElement.getName().getLocalPart().equals(CLASS_TAG)) {
						event = eventReader.nextEvent();
						className = event.asCharacters().getData();
					}

					if (startElement.getName().getLocalPart()
							.equals(METHOD_TYPE_TAG)) {
						event = eventReader.nextEvent();
						String tp = event.asCharacters().getData();
						if ("constructor".equals(tp)) {
							methodType = MethodType.CONSTRUCTOR;
						}
					}

					if (startElement.getName().getLocalPart()
							.equals(RETURN_TYPE_TAG)) {
						event = eventReader.nextEvent();
						returnType = event.asCharacters().getData();
					}

					if (startElement.getName().getLocalPart()
							.equals(OVERLOADING_TAG)) {
						apiDescOver.add(new ApiDescriptor());
					}

					if (startElement.getName().getLocalPart()
							.equals(PARAMETER_TAG)) {
						Iterator<Attribute> attributes = startElement
								.getAttributes();
						String paramName = null;
						String paramType = null;
						int paramPos = -1;
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString()
									.equals(NAME_ATTR)) {
								paramName = attribute.getValue();
							}
							if (attribute.getName().toString()
									.equals(TYPE_ATTR)) {
								paramType = attribute.getValue();
							}
							if (attribute.getName().toString().equals(POS_ATTR)) {
								String val = attribute.getValue();
								try {
									paramPos = Integer.valueOf(val);
								} catch (NumberFormatException e) {
									e.printStackTrace();
									paramPos = -1;
								}
							}
						}
						int size = apiDescOver.size();
						if (size > 0) {
							ApiDescriptor locApiDesc = apiDescOver
									.get(size - 1);
							if (locApiDesc != null) {
								ParameterDescriptor paramDesc = locApiDesc.new ParameterDescriptor(
										paramName, paramType, paramPos);
								locApiDesc.addParam(paramDesc);
							} else {
								System.out
										.println(">>error: ApiDescriptor is NULL. "
												+ "The parameter is not added.");
							}
						} else {
							System.out
									.println(">>error: parameters are parsed bethore"
											+ " a descriptor of a method is created.");
						}
					}
				}

				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart()
							.equals(METHOD_TAG)) {
						if (apiDescOver != null) {
							for (ApiDescriptor desc : apiDescOver) {
								desc.setClassName(className);
								desc.setMethodName(methodName);
								desc.setMethodType(methodType);
								desc.setReturnType(returnType);
								mParsedApi.add(desc);
							}
						} else {
							System.out.println(">>inf: no parameters were extracted.");
							ApiDescriptor desc = new ApiDescriptor();
							desc.setPackageName(packageName);
							desc.setClassName(className);
							desc.setMethodName(methodName);
							desc.setMethodType(methodType);
							desc.setReturnType(returnType);
							mParsedApi.add(desc);
						}
						packageName = null;
						className = null;
						methodType = MethodType.NORMAL;
						returnType = null;
						apiDescOver = null;
					}
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	public List<ApiDescriptor> getParsedApi() {
		return mParsedApi;
	}

}
