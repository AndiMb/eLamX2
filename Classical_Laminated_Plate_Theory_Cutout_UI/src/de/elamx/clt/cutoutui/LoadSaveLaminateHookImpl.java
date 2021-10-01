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
package de.elamx.clt.cutoutui;

import de.elamx.clt.cutout.CutoutInput;
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
 * @author raedel
 */
@ServiceProvider(service = LoadSaveLaminateHook.class)
public class LoadSaveLaminateHookImpl implements LoadSaveLaminateHook {
    
    public static final String ELEMENT_TAG = "cutout";
    public static final String NAME_TAG = "name";
    
    public static final String NXX_TAG = "n_xx";
    public static final String NYY_TAG = "n_yy";
    public static final String NXY_TAG = "n_xy";
    
    public static final String MXX_TAG = "m_xx";
    public static final String MYY_TAG = "m_yy";
    public static final String MXY_TAG = "m_xy";
    
    public static final String VAL_TAG = "val";

    @Override
    public void load(Element cutoutElement, Laminat laminate) {

        NodeList cutoutList = cutoutElement.getElementsByTagName(ELEMENT_TAG);
        if (cutoutList.getLength() > 0) {
            for (int ii = cutoutList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node CutoutNode = cutoutList.item(ii);
                if (CutoutNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element CutoutElement = (Element) CutoutNode;
                    String name = CutoutElement.getAttribute(NAME_TAG);

                    CutoutInput input = new CutoutInput();
                    
                    input.setNXX(Double.parseDouble(getTagValue(NXX_TAG, CutoutElement)));
                    input.setNYY(Double.parseDouble(getTagValue(NYY_TAG, CutoutElement)));
                    input.setNXY(Double.parseDouble(getTagValue(NXY_TAG, CutoutElement)));
                    
                    input.setMXX(Double.parseDouble(getTagValue(MXX_TAG, CutoutElement)));
                    input.setMYY(Double.parseDouble(getTagValue(MYY_TAG, CutoutElement)));
                    input.setMXY(Double.parseDouble(getTagValue(MXY_TAG, CutoutElement)));
                    
                    input.setValues(Integer.parseInt(getTagValue(VAL_TAG, CutoutElement)));
                    
                    LoadSaveCutoutGeometry.loadCutoutGeometry(CutoutElement, input);
                    
                    CutoutModuleData data = new CutoutModuleData(laminate, input);
                    data.setName(name);

                    laminate.getLookup().add(data);
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

    @Override
    public void store(Document doc, Element cutoutElement, Laminat laminate) {

        Collection<? extends CutoutModuleData> col = laminate.getLookup().lookupAll(CutoutModuleData.class);

        if (col.isEmpty()) {
            return;
        }

        NodeList list = cutoutElement.getElementsByTagName(ELEMENT_TAG);
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                cutoutElement.removeChild(list.item(ii));
            }
        }

        for (CutoutModuleData data : col) {
            CutoutInput input = data.getCutoutInput();

            Element dataElement;
            dataElement = doc.createElement(ELEMENT_TAG);
            Attr attr = doc.createAttribute(NAME_TAG);
            attr.setValue(data.getName());
            dataElement.setAttributeNode(attr);
            
            addValue(doc, NXX_TAG, Double.toString(input.getNXX()), dataElement);
            addValue(doc, NYY_TAG, Double.toString(input.getNYY()), dataElement);
            addValue(doc, NXY_TAG, Double.toString(input.getNXY()), dataElement);
            
            addValue(doc, MXX_TAG, Double.toString(input.getMXX()), dataElement);
            addValue(doc, MYY_TAG, Double.toString(input.getMYY()), dataElement);
            addValue(doc, MXY_TAG, Double.toString(input.getMXY()), dataElement);
            
            addValue(doc, VAL_TAG, Integer.toString(input.getValues()), dataElement);
            
            LoadSaveCutoutGeometry.storeCutoutGeometry(doc, dataElement, input);

            cutoutElement.appendChild(dataElement);
        }
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
    
}
