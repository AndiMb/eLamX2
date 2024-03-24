/*
 *  This program developed in Java is based on the netbeans platform and is used
 *  to design and to analyse composite structures by means of analytical and 
 *  numerical methods.
 * 
 *  Further information can be found here:
 *  http://www.elamx.de
 *    
 *  Copyright (C) 2021 Technische Universität Dresden - Andreas Hauffe
 * 
 *  This file is part of eLamX².
 *
 *  eLamX² is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  eLamX² is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with eLamX².  If not, see <http://www.gnu.org/licenses/>.
 */
package de.elamx.clt.plateui.stiffenerui;

import de.elamx.clt.plate.Input;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.clt.plateui.stiffenerui.StiffenerDefinitionService.Property;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas Hauffe
 */
public class LoadSaveStiffeners {

    public static void loadStiffeners(Element elem, Input input) {
        load(elem, input);
    }

    private static void load(Element elem, Input input) {
        NodeList propList = elem.getElementsByTagName("Stiffener");
        if (propList.getLength() > 0) {

            HashMap<String, StiffenerDefinitionService> serviceMap = new HashMap<>();
            for (StiffenerDefinitionService s: Lookup.getDefault().lookupAll(StiffenerDefinitionService.class)){
                serviceMap.put(s.getClass().getName(), s);
            }
            
            for (int jj = 0; jj < propList.getLength(); jj++) {
                org.w3c.dom.Node propNode = propList.item(jj);
                if (propNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element propElem = (Element) propNode;
                    String className = propElem.getAttribute("classname");
                    
                    Object sObject = serviceMap.get(className);
                    
                    if (sObject == null){
                        continue;
                    }
                    
                    StiffenerDefinitionService service = (StiffenerDefinitionService)((StiffenerDefinitionService)sObject).getCopy();
                    
                    String propName = propElem.getAttribute("name");
                    double position = Double.parseDouble(getTagValue("position", propElem));
                    int direction = Integer.parseInt(getTagValue("direction", propElem));
                    
                    service.setName(propName);
                    service.setPosition(position);
                    service.setDirection(direction);
                    
                    for (Property p : service.getPropertyDefinitions()) {
                        if (p.getCl().getName().equals(double.class.getName())) {

                            try {
                                String methodeName = "set";
                                methodeName += p.getName().substring(0, 1).toUpperCase();
                                if (p.getName().length() > 1) {
                                    methodeName += p.getName().substring(1);
                                }

                                java.lang.reflect.Method method = service.getClass().getMethod(methodeName, double.class);
                                
                                if (method == null){
                                    continue;
                                }

                                String value = getTagValue(p.getName(), propElem);
                                
                                double val = 0.0;
                                if (value != null){
                                    val = Double.parseDouble(value);
                                }
                                method.invoke(service, val);
                            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                                Exceptions.printStackTrace(e);
                            }
                        }
                    }
                    input.addStiffenerProperty(service);
                }
            }
        }
    }

    public static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag);
        if (nlList.getLength() == 0) {
            return null;
        }
        nlList = nlList.item(0).getChildNodes();

        org.w3c.dom.Node nValue = nlList.item(0);

        return nValue.getNodeValue();
    }

    public static void storeStiffeners(Document doc, Element dataElement, Input input) {
        for (StiffenerProperties props : input.getStiffenerProperties()) {
            store(doc, dataElement, (StiffenerDefinitionService) props);
        }
    }

    private static void store(Document doc, Element dataElement, StiffenerDefinitionService serv) {

        Element propElem = doc.createElement("Stiffener");

        Attr attr = doc.createAttribute("name");
        attr.setValue(serv.getName());
        propElem.setAttributeNode(attr);
        attr = doc.createAttribute("classname");
        attr.setValue(serv.getClass().getName());
        propElem.setAttributeNode(attr);

        addValue(doc, "position", Double.toString(serv.getPosition()), propElem);
        addValue(doc, "direction", Integer.toString(serv.getDirection()), propElem);

        for (Property p : serv.getPropertyDefinitions()) {
            if (p.getCl().getName().equals(double.class.getName())) {

                java.lang.reflect.Method method = null;
                try {
                    String methodeName = "get";
                    methodeName += p.getName().substring(0, 1).toUpperCase();
                    if (p.getName().length() > 1) {
                        methodeName += p.getName().substring(1);
                    }

                    method = serv.getClass().getMethod(methodeName);
                } catch (SecurityException | NoSuchMethodException e) {
                    Exceptions.printStackTrace(e);
                }
                if (method == null){
                    continue;
                }

                try {
                    addValue(doc, p.getName(), ((Double) method.invoke(serv)).toString(), propElem);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }

        dataElement.appendChild(propElem);
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
}
