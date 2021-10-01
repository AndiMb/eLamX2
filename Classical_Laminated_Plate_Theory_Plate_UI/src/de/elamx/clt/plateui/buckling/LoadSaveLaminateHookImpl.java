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
package de.elamx.clt.plateui.buckling;

import de.elamx.clt.plate.BucklingInput;
import de.elamx.clt.plateui.stiffenerui.LoadSaveStiffeners;
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

    @Override
    public void load(Element laminateElement, Laminat laminate) {

        NodeList bucklingList = laminateElement.getElementsByTagName("buckling");
        if (bucklingList.getLength() > 0) {
            for (int ii = bucklingList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node BucklingNode = bucklingList.item(ii);
                if (BucklingNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element BucklingElement = (Element) BucklingNode;
                    String name = BucklingElement.getAttribute("name");

                    BucklingInput input = loadInput(BucklingElement);

                    BucklingModuleData data = new BucklingModuleData(laminate, input);
                    data.setName(name);

                    laminate.getLookup().add(data);
                }
            }
        }
    }

    public static BucklingInput loadInput(Element BucklingElement) {
        BucklingInput input = new BucklingInput();

        input.setNx(Double.parseDouble(getTagValue("n_x", BucklingElement)));
        input.setNy(Double.parseDouble(getTagValue("n_y", BucklingElement)));
        input.setNxy(Double.parseDouble(getTagValue("n_xy", BucklingElement)));

        input.setLength(Double.parseDouble(getTagValue("length", BucklingElement)));
        input.setWidth(Double.parseDouble(getTagValue("width", BucklingElement)));

        input.setBcx(Integer.parseInt(getTagValue("bcx", BucklingElement)));
        input.setBcy(Integer.parseInt(getTagValue("bcy", BucklingElement)));

        input.setM(Integer.parseInt(getTagValue("m", BucklingElement)));
        input.setN(Integer.parseInt(getTagValue("n", BucklingElement)));

        input.setWholeD(Boolean.parseBoolean(getTagValue("wholed", BucklingElement)));

        LoadSaveStiffeners.loadStiffeners(BucklingElement, input);

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

        NodeList list = laminateElement.getElementsByTagName("buckling");
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                laminateElement.removeChild(list.item(ii));
            }
        }

        Collection<? extends BucklingModuleData> col = laminate.getLookup().lookupAll(BucklingModuleData.class);

        if (col.isEmpty()) {
            return;
        }

        for (BucklingModuleData data : col) {
            BucklingInput input = data.getBucklingInput();

            Element dataElement;
            dataElement = doc.createElement("buckling");
            Attr attr = doc.createAttribute("name");
            attr.setValue(data.getName());
            dataElement.setAttributeNode(attr);
            
            storeInput(doc, dataElement, input);

            LoadSaveStiffeners.storeStiffeners(doc, dataElement, input);

            laminateElement.appendChild(dataElement);
        }
    }

    public static void storeInput(Document doc, Element dataElement, BucklingInput input) {

        addValue(doc, "n_x", Double.toString(input.getNx()), dataElement);
        addValue(doc, "n_y", Double.toString(input.getNy()), dataElement);
        addValue(doc, "n_xy", Double.toString(input.getNxy()), dataElement);

        addValue(doc, "length", Double.toString(input.getLength()), dataElement);
        addValue(doc, "width", Double.toString(input.getWidth()), dataElement);

        addValue(doc, "bcx", Integer.toString(input.getBcx()), dataElement);
        addValue(doc, "bcy", Integer.toString(input.getBcy()), dataElement);

        addValue(doc, "m", Integer.toString(input.getM()), dataElement);
        addValue(doc, "n", Integer.toString(input.getN()), dataElement);

        addValue(doc, "wholed", Boolean.toString(input.isWholeD()), dataElement);
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
}
