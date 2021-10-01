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
package de.elamx.clt.calculation;

import de.elamx.clt.CLT_Input;
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

        NodeList calculationList = laminateElement.getElementsByTagName("calculation");
        if (calculationList.getLength() > 0) {
            for (int ii = calculationList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node calculationNode = calculationList.item(ii);
                if (calculationNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element calculationElement = (Element) calculationNode;
                    String name = calculationElement.getAttribute("name");

                    CLT_Input dataHolder = loadInput(calculationElement);

                    CalculationModuleData data = new CalculationModuleData(laminate, dataHolder);
                    data.setName(name);

                    laminate.getLookup().add(data);
                }
            }
        }
    }

    public static CLT_Input loadInput(Element calculationElement) {
        CLT_Input dataHolder = new CLT_Input();

        dataHolder.getLoad().setN_x(Double.parseDouble(getTagValue("n_x", calculationElement)));
        dataHolder.getLoad().setN_y(Double.parseDouble(getTagValue("n_y", calculationElement)));
        dataHolder.getLoad().setN_xy(Double.parseDouble(getTagValue("n_xy", calculationElement)));
        dataHolder.getLoad().setM_x(Double.parseDouble(getTagValue("m_x", calculationElement)));
        dataHolder.getLoad().setM_y(Double.parseDouble(getTagValue("m_y", calculationElement)));
        dataHolder.getLoad().setM_xy(Double.parseDouble(getTagValue("m_xy", calculationElement)));

        dataHolder.getLoad().setDeltaT(Double.parseDouble(getTagValue("deltat", calculationElement)));
        dataHolder.getLoad().setDeltaH(Double.parseDouble(getTagValue("deltah", calculationElement)));

        dataHolder.getStrains().setEpsilon_x(Double.parseDouble(getTagValue("epsilon_x", calculationElement)));
        dataHolder.getStrains().setEpsilon_y(Double.parseDouble(getTagValue("epsilon_y", calculationElement)));
        dataHolder.getStrains().setGamma_xy(Double.parseDouble(getTagValue("gamma_xy", calculationElement)));
        dataHolder.getStrains().setKappa_x(Double.parseDouble(getTagValue("kappa_x", calculationElement)));
        dataHolder.getStrains().setKappa_y(Double.parseDouble(getTagValue("kappa_y", calculationElement)));
        dataHolder.getStrains().setKappa_xy(Double.parseDouble(getTagValue("kappa_xy", calculationElement)));

        boolean[] boolA = new boolean[6];
        for (int jj = 0; jj < boolA.length; jj++) {
            boolA[jj] = Boolean.parseBoolean(getTagValue("useStrain" + jj, calculationElement));
        }

        dataHolder.setUseStrains(boolA);

        return dataHolder;
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

        Collection<? extends CalculationModuleData> col = laminate.getLookup().lookupAll(CalculationModuleData.class);

        if (col.isEmpty()) {
            return;
        }

        NodeList list = laminateElement.getElementsByTagName("calculation");
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                laminateElement.removeChild(list.item(ii));
            }
        }

        for (CalculationModuleData data : col) {
            CLT_Input dataHolder = data.getDataHolder();

            Element dataElement;
            dataElement = doc.createElement("calculation");
            Attr attr = doc.createAttribute("name");
            attr.setValue(data.getName());
            dataElement.setAttributeNode(attr);
            
            storeInput(doc, dataElement, dataHolder);

            laminateElement.appendChild(dataElement);
        }
    }
    
    public static void storeInput(Document doc, Element dataElement, CLT_Input dataHolder){

            addValue(doc, "n_x", Double.toString(dataHolder.getLoad().getN_x()), dataElement);
            addValue(doc, "n_y", Double.toString(dataHolder.getLoad().getN_y()), dataElement);
            addValue(doc, "n_xy", Double.toString(dataHolder.getLoad().getN_xy()), dataElement);
            addValue(doc, "m_x", Double.toString(dataHolder.getLoad().getM_x()), dataElement);
            addValue(doc, "m_y", Double.toString(dataHolder.getLoad().getM_y()), dataElement);
            addValue(doc, "m_xy", Double.toString(dataHolder.getLoad().getM_xy()), dataElement);

            addValue(doc, "deltat", Double.toString(dataHolder.getLoad().getDeltaT()), dataElement);
            addValue(doc, "deltah", Double.toString(dataHolder.getLoad().getDeltaH()), dataElement);

            boolean[] boolA = dataHolder.isUseStrains();
            for (int ii = 0; ii < boolA.length; ii++) {
                addValue(doc, "useStrain" + ii, Boolean.toString(boolA[ii]), dataElement);
            }

            addValue(doc, "epsilon_x", Double.toString(dataHolder.getStrains().getEpsilon_x()), dataElement);
            addValue(doc, "epsilon_y", Double.toString(dataHolder.getStrains().getEpsilon_y()), dataElement);
            addValue(doc, "gamma_xy", Double.toString(dataHolder.getStrains().getGamma_xy()), dataElement);
            addValue(doc, "kappa_x", Double.toString(dataHolder.getStrains().getKappa_x()), dataElement);
            addValue(doc, "kappa_y", Double.toString(dataHolder.getStrains().getKappa_y()), dataElement);
            addValue(doc, "kappa_xy", Double.toString(dataHolder.getStrains().getKappa_xy()), dataElement);
        
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
}
