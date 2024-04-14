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
package de.elamx.clt.pressurevesselui;

import de.elamx.clt.pressurevessel.PressureVesselInput;
import de.elamx.filesupport.LoadSaveLaminateHook;
import de.elamx.laminate.Laminat;
import java.util.Collection;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = LoadSaveLaminateHook.class)
public class LoadSaveLaminateHookImpl implements LoadSaveLaminateHook {
    
    public static final String ELEMENT_TAG = "pressurevessel";
    public static final String NAME_TAG = "name";
    
    public static final String PRESSURE_TAG = "pressure";
    public static final String RADIUS_TAG = "radius";
    public static final String RADIUSTYPE_TAG = "radiustype";

    @Override
    public void load(Element pressureVesselElement, Laminat laminate) {

        NodeList pressurevesselList = pressureVesselElement.getElementsByTagName(ELEMENT_TAG);
        if (pressurevesselList.getLength() > 0) {
            for (int ii = pressurevesselList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node pressureVesselNode = pressurevesselList.item(ii);
                if (pressureVesselNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element PressureVesselElement = (Element) pressureVesselNode;
                    String name = PressureVesselElement.getAttribute(NAME_TAG);

                    PressureVesselInput input = loadInput(PressureVesselElement);
                    
                    PressureVesselModuleData data = new PressureVesselModuleData(laminate, input);
                    data.setName(name);

                    laminate.getLookup().add(data);
                }
            }
        }
    }

    public static PressureVesselInput loadInput(Element PressureVesselElement) {
        PressureVesselInput input = new PressureVesselInput();
                    
        input.setPressure(Double.parseDouble(getTagValue(PRESSURE_TAG, PressureVesselElement)));
        input.setRadius(Double.parseDouble(getTagValue(RADIUS_TAG, PressureVesselElement)));
        input.setRadiusType(Integer.parseInt(getTagValue(RADIUSTYPE_TAG, PressureVesselElement)));

        return input;
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

    @Override
    public void store(Document doc, Element cutoutElement, Laminat laminate) {

        Collection<? extends PressureVesselModuleData> col = laminate.getLookup().lookupAll(PressureVesselModuleData.class);

        if (col.isEmpty()) {
            return;
        }

        NodeList list = cutoutElement.getElementsByTagName(ELEMENT_TAG);
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                cutoutElement.removeChild(list.item(ii));
            }
        }

        for (PressureVesselModuleData data : col) {
            PressureVesselInput input = data.getPressureVesselInput();

            Element dataElement;
            dataElement = doc.createElement(ELEMENT_TAG);
            Attr attr = doc.createAttribute(NAME_TAG);
            attr.setValue(data.getName());
            dataElement.setAttributeNode(attr);
            
            storeInput(doc, dataElement, input);

            cutoutElement.appendChild(dataElement);
        }
    }
    
    public static void storeInput(Document doc, Element dataElement, PressureVesselInput input){
        addValue(doc, PRESSURE_TAG, Double.toString(input.getPressure()), dataElement);
        addValue(doc, RADIUS_TAG, Double.toString(input.getRadius()), dataElement);
        addValue(doc, RADIUSTYPE_TAG, Integer.toString(input.getRadiusType()), dataElement);
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
}
