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
package de.elamx.clt.springinui;

import de.elamx.clt.springin.SpringInInput;
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
    
    public static final String ELEMENT_TAG = "springIn";
    public static final String NAME_TAG = "name";

    @Override
    public void load(Element springInElement, Laminat laminate) {

        NodeList springInList = springInElement.getElementsByTagName(ELEMENT_TAG);
        if (springInList.getLength() > 0) {
            for (int ii = springInList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node SpringInNode = springInList.item(ii);
                if (SpringInNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element SpringInElement = (Element) SpringInNode;
                    String name = SpringInElement.getAttribute(NAME_TAG);

                    SpringInInput input = new SpringInInput();
                    
                    input.setAlphat_thick(Double.parseDouble(getTagValue(SpringInInput.PROP_ALPHAT_THICK, SpringInElement)));
                    input.setAngle(Double.parseDouble(getTagValue(SpringInInput.PROP_ANGLE, SpringInElement)));
                    input.setBaseTemp(Double.parseDouble(getTagValue(SpringInInput.PROP_BASETEMP, SpringInElement)));
                    input.setHardeningTemp(Double.parseDouble(getTagValue(SpringInInput.PROP_HARDENINGTEMP, SpringInElement)));
                    input.setRadius(Double.parseDouble(getTagValue(SpringInInput.PROP_RADIUS, SpringInElement)));
                    input.setUseAutoCalcAlphat_thick(Boolean.parseBoolean(getTagValue(SpringInInput.PROP_USEAUTOCALCALPHAT_THICK, SpringInElement)));
                    input.setZeroDegAsCircumDir(Boolean.parseBoolean(getTagValue(SpringInInput.PROP_ZERODEGASCIRCUMDIR, SpringInElement)));
                    
                    LoadSaveSpringInModel.loadSpringInModel(SpringInElement, input);
                    
                    SpringInModuleData data = new SpringInModuleData(laminate, input);
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
    public void store(Document doc, Element springInElement, Laminat laminate) {

        Collection<? extends SpringInModuleData> col = laminate.getLookup().lookupAll(SpringInModuleData.class);

        if (col.isEmpty()) {
            return;
        }

        NodeList list = springInElement.getElementsByTagName(ELEMENT_TAG);
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                springInElement.removeChild(list.item(ii));
            }
        }

        for (SpringInModuleData data : col) {
            SpringInInput input = data.getSpringInInput();

            Element dataElement;
            dataElement = doc.createElement(ELEMENT_TAG);
            Attr attr = doc.createAttribute(NAME_TAG);
            attr.setValue(data.getName());
            dataElement.setAttributeNode(attr);
            
            addValue(doc, SpringInInput.PROP_ALPHAT_THICK, Double.toString(input.getAlphat_thick()), dataElement);
            addValue(doc, SpringInInput.PROP_ANGLE, Double.toString(input.getAngle()), dataElement);
            addValue(doc, SpringInInput.PROP_BASETEMP, Double.toString(input.getBaseTemp()), dataElement);
            addValue(doc, SpringInInput.PROP_HARDENINGTEMP, Double.toString(input.getHardeningTemp()), dataElement);
            addValue(doc, SpringInInput.PROP_RADIUS, Double.toString(input.getRadius()), dataElement);
            addValue(doc, SpringInInput.PROP_USEAUTOCALCALPHAT_THICK, Boolean.toString(input.isUseAutoCalcAlphat_thick()), dataElement);
            addValue(doc, SpringInInput.PROP_ZERODEGASCIRCUMDIR, Boolean.toString(input.isZeroDegAsCircumDir()), dataElement);
            
            LoadSaveSpringInModel.storeSpringInModel(doc, dataElement, input);

            springInElement.appendChild(dataElement);
        }
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
    
}
