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
package de.elamx.clt.calculation.lastplyfailureui;

import de.elamx.clt.calculation.lastplyfailure.LastPlyFailureInput;
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

    public static final String ELEMENT_TAG = "lastplyfailure";
    public static final String NAME_TAG = "name";

    @Override
    public void load(Element laminateElement, Laminat laminate) {
        NodeList lastPlyFailureList = laminateElement.getElementsByTagName(ELEMENT_TAG);
        if (lastPlyFailureList.getLength() > 0) {
            for (int ii = lastPlyFailureList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node lastPlyFailureNode = lastPlyFailureList.item(ii);
                if (lastPlyFailureNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element lastPlyFailureElement = (Element) lastPlyFailureNode;
                    String name = lastPlyFailureElement.getAttribute(NAME_TAG);

                    LastPlyFailureInput input = loadInput(lastPlyFailureElement);

                    LastPlyFailureModuleData data = new LastPlyFailureModuleData(laminate, input);
                    data.setName(name);

                    laminate.getLookup().add(data);
                }
            }
        }
    }
    
    public static LastPlyFailureInput loadInput(Element LastPlyFailureElement) {
        LastPlyFailureInput input = new LastPlyFailureInput();
                    
        input.getLoad().setN_x(Double.parseDouble(getTagValue("n_x", LastPlyFailureElement)));
        input.getLoad().setN_y(Double.parseDouble(getTagValue("n_y", LastPlyFailureElement)));
        input.getLoad().setN_xy(Double.parseDouble(getTagValue("n_xy", LastPlyFailureElement)));
        input.getLoad().setM_x(Double.parseDouble(getTagValue("m_x", LastPlyFailureElement)));
        input.getLoad().setM_y(Double.parseDouble(getTagValue("m_y", LastPlyFailureElement)));
        input.getLoad().setM_xy(Double.parseDouble(getTagValue("m_xy", LastPlyFailureElement)));

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
    public void store(Document doc, Element laminateElement, Laminat laminate) {
        Collection<? extends LastPlyFailureModuleData> col = laminate.getLookup().lookupAll(LastPlyFailureModuleData.class);

        if (col.isEmpty()) {
            return;
        }

        NodeList list = laminateElement.getElementsByTagName(ELEMENT_TAG);
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                laminateElement.removeChild(list.item(ii));
            }
        }

        for (LastPlyFailureModuleData data : col) {
            LastPlyFailureInput input = data.getLastPlyFailureInput();

            Element dataElement;
            dataElement = doc.createElement(ELEMENT_TAG);
            Attr attr = doc.createAttribute(NAME_TAG);
            attr.setValue(data.getName());
            dataElement.setAttributeNode(attr);

            storeInput(doc, dataElement, input);

            laminateElement.appendChild(dataElement);
        }
    }

    private static void storeInput(Document doc, Element dataElement, LastPlyFailureInput input) {
        addValue(doc, "n_x", Double.toString(input.getLoad().getN_x()), dataElement);
        addValue(doc, "n_y", Double.toString(input.getLoad().getN_y()), dataElement);
        addValue(doc, "n_xy", Double.toString(input.getLoad().getN_xy()), dataElement);
        addValue(doc, "m_x", Double.toString(input.getLoad().getM_x()), dataElement);
        addValue(doc, "m_y", Double.toString(input.getLoad().getM_y()), dataElement);
        addValue(doc, "m_xy", Double.toString(input.getLoad().getM_xy()), dataElement);
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
}
