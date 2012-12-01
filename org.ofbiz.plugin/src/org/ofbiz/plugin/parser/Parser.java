/**
 * Copyright 2008 Anders Hessellund 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id: Parser.java,v 1.1 2008/01/17 18:48:15 hessellund Exp $
 */
package org.ofbiz.plugin.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.ofbiz.plugin.Plugin;
import org.ofbiz.plugin.ofbiz.Component;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class Parser {
	
	protected IFile file;
	
	public void processDocument(XmlPullParser xpp, IFile file) 
	throws CoreException, XmlPullParserException, IOException {
		assert file != null && file.exists();
		this.file = file;
		Reader reader = 
			new BufferedReader(
				new InputStreamReader(
					file.getContents()));
		preProcess();
		processDocument(xpp, reader);
		postProcess();
	}
	
	public void processDocument(XmlPullParser xpp, Reader reader)
	throws XmlPullParserException, IOException {
		assert xpp != null;
		assert reader != null;
		xpp.setInput(reader);
		int eventType = xpp.getEventType();
		do {
			if (eventType == XmlPullParser.START_TAG) {
				processStartElement(xpp);
			} else if (eventType == XmlPullParser.END_TAG) {
				processEndElement(xpp);
			} 
			eventType = xpp.next();
		} while (eventType != XmlPullParser.END_DOCUMENT);
		cleanup();
	}

	protected void processStartElement(XmlPullParser xpp) { /* no processing */ }

	protected void processEndElement(XmlPullParser xpp) { /* no processing */ }
	
	protected void cleanup()  { /* no cleanup */ }
	
	protected void preProcess() { /* no pre processing */ }
	
	protected void postProcess() { /* no post processing */ }
	
	protected IMarker createMarker(int lineno, String name) {
		if (!Plugin.USE_MARKERS) return null;
		try {
			IMarker marker = file.createMarker(getMarkerType());
			marker.setAttribute(IMarker.LINE_NUMBER, lineno);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			marker.setAttribute("name", name);
			//Plugin.debug("marker created for "+name+" in file "+file.getFullPath());
			return marker;
		} catch (CoreException e) {
			Plugin.logError("Unable to create marker for "+file.getName(), e);
			return null;
		}
	}
	
//	protected IMarker createMarker(XmlPullParser xpp, HasXmlDefinition hasXmlDefinition, String markerKey, String attributeName) {
//		if (!Plugin.USE_MARKERS) {
//			return null;
//		}
//		hasXmlDefinition.setFile(file);
//		hasXmlDefinition.setMarkerKey(markerKey);
//		try {
//			IMarker marker = file.createMarker(getMarkerType());
//			if (xpp == null) {
//				marker.setAttribute(IMarker.CHAR_START, 0);
//				marker.setAttribute(IMarker.CHAR_END, 0);
//			} else {
//				String positionDescription = xpp.getPositionDescription();
//				String[] split = positionDescription.substring(positionDescription.lastIndexOf("@") + 1).split(":");
//				marker.setAttribute(IMarker.LINE_NUMBER, xpp.getLineNumber());
//				marker.setAttribute(IMarker.CHAR_START, Integer.valueOf(split[0]));
//				marker.setAttribute(IMarker.CHAR_END, Integer.valueOf(split[1]));
//			}
//			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
//			marker.setAttribute("name", markerKey);
//			//Plugin.debug("marker created for "+name+" in file "+file.getFullPath());
//			return marker;
//		} catch (CoreException e) {
//			Plugin.logError("Unable to create marker for "+file.getName(), e);
//			return null;
//		}
//	}
//	

	/** retrieves model file for either an entity-resource or a service-resource */
	protected IFile getResourceFile(String location, Component component) {
		IResource res = component.getFolder().findMember(location);
		return (IFile) res;
	
	}
	protected abstract String getMarkerType();

	public IFile getFile() {
		return file;
	}
	
}