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
package de.elamx.clt.plateui.deformation;

import de.elamx.clt.plate.DeformationInput;
import de.elamx.clt.plate.Mechanical.PointLoad;
import de.elamx.clt.plate.Mechanical.SurfaceLoad_const_full;
import de.elamx.clt.plate.Mechanical.TransverseLoad;
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

        NodeList deformationList = laminateElement.getElementsByTagName("deformation");
        if (deformationList.getLength() > 0) {
            for (int ii = deformationList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node DeformationNode = deformationList.item(ii);
                if (DeformationNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element DeformationElement = (Element) DeformationNode;
                    String name = DeformationElement.getAttribute("name");

                    DeformationInput input = loadInput(DeformationElement);

                    DeformationModuleData data = new DeformationModuleData(laminate, input);
                    data.setName(name);

                    laminate.getLookup().add(data);
                }
            }
        }
    }

    public static DeformationInput loadInput(Element DeformationElement) {

        DeformationInput input = new DeformationInput();

        input.setLength(Double.parseDouble(getTagValue("length", DeformationElement)));
        input.setWidth(Double.parseDouble(getTagValue("width", DeformationElement)));

        input.setBcx(Integer.parseInt(getTagValue("bcx", DeformationElement)));
        input.setBcy(Integer.parseInt(getTagValue("bcy", DeformationElement)));

        input.setM(Integer.parseInt(getTagValue("m", DeformationElement)));
        input.setN(Integer.parseInt(getTagValue("n", DeformationElement)));

        input.setWholeD(Boolean.parseBoolean(getTagValue("wholed", DeformationElement)));
        
        String sMaxDispl = getTagValue("maxDisplacement", DeformationElement);
        input.setMaxDisplacementInZ(sMaxDispl == null ? 0.0 : Double.parseDouble(sMaxDispl));

        loadPointLoad(DeformationElement, input);

        loadSurfaceLoad_const_full(DeformationElement, input);

        LoadSaveStiffeners.loadStiffeners(DeformationElement, input);

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

        NodeList list = laminateElement.getElementsByTagName("deformation");
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                laminateElement.removeChild(list.item(ii));
            }
        }

        Collection<? extends DeformationModuleData> col = laminate.getLookup().lookupAll(DeformationModuleData.class);

        if (col.isEmpty()) {
            return;
        }

        for (DeformationModuleData data : col) {
            DeformationInput input = data.getDeformationInput();

            Element dataElement;
            dataElement = doc.createElement("deformation");
            Attr attr = doc.createAttribute("name");
            attr.setValue(data.getName());
            dataElement.setAttributeNode(attr);
            
            storeInput(doc, dataElement, input);

            LoadSaveStiffeners.storeStiffeners(doc, dataElement, input);

            laminateElement.appendChild(dataElement);
        }
    }

    public static void storeInput(Document doc, Element dataElement, DeformationInput input) {

        addValue(doc, "length", Double.toString(input.getLength()), dataElement);
        addValue(doc, "width", Double.toString(input.getWidth()), dataElement);

        addValue(doc, "bcx", Integer.toString(input.getBcx()), dataElement);
        addValue(doc, "bcy", Integer.toString(input.getBcy()), dataElement);

        addValue(doc, "m", Integer.toString(input.getM()), dataElement);
        addValue(doc, "n", Integer.toString(input.getN()), dataElement);

        addValue(doc, "wholed", Boolean.toString(input.isWholeD()), dataElement);
        
        addValue(doc, "maxDisplacement", Double.toString(input.getMaxDisplacementInZ()), dataElement);

        for (TransverseLoad load : input.getLoads()) {
            if (load instanceof PointLoad) {
                savePointLoad(doc, dataElement, (PointLoad) load);
            } else if (load instanceof SurfaceLoad_const_full) {
                saveSurfaceLoad_const_full(doc, dataElement, (SurfaceLoad_const_full) load);
            }
        }

    }

    private static void savePointLoad(Document doc, Element dataElement, PointLoad pl) {

        Element plElem = doc.createElement("pointload");

        Attr attr = doc.createAttribute("name");
        attr.setValue(pl.getName());
        plElem.setAttributeNode(attr);

        addValue(doc, "xposition", Double.toString(pl.getX()), plElem);
        addValue(doc, "yposition", Double.toString(pl.getY()), plElem);
        addValue(doc, "force", Double.toString(pl.getForce()), plElem);

        dataElement.appendChild(plElem);
    }

    private static void loadPointLoad(Element DeformationElement, DeformationInput input) {

        NodeList plList = DeformationElement.getElementsByTagName("pointload");
        if (plList.getLength() > 0) {
            for (int jj = 0; jj < plList.getLength(); jj++) {
                org.w3c.dom.Node plNode = plList.item(jj);
                if (plNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element plElem = (Element) plNode;
                    String name = plElem.getAttribute("name");
                    double xposition = Double.parseDouble(getTagValue("xposition", plElem));
                    double yposition = Double.parseDouble(getTagValue("yposition", plElem));
                    double force = Double.parseDouble(getTagValue("force", plElem));

                    PointLoad pl = new PointLoad(name, xposition, yposition, force);

                    input.addLoad(pl);
                }
            }
        }
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }

    private static void saveSurfaceLoad_const_full(Document doc, Element dataElement, SurfaceLoad_const_full pl) {

        Element plElem = doc.createElement("surfaceLoad_const_full");

        Attr attr = doc.createAttribute("name");
        attr.setValue(pl.getName());
        plElem.setAttributeNode(attr);

        addValue(doc, "force", Double.toString(pl.getForce()), plElem);

        dataElement.appendChild(plElem);
    }

    private static void loadSurfaceLoad_const_full(Element DeformationElement, DeformationInput input) {

        NodeList plList = DeformationElement.getElementsByTagName("surfaceLoad_const_full");
        if (plList.getLength() > 0) {
            for (int jj = 0; jj < plList.getLength(); jj++) {
                org.w3c.dom.Node plNode = plList.item(jj);
                if (plNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element plElem = (Element) plNode;
                    String name = plElem.getAttribute("name");
                    double force = Double.parseDouble(getTagValue("force", plElem));

                    SurfaceLoad_const_full pl = new SurfaceLoad_const_full(name, force);

                    input.addLoad(pl);
                }
            }
        }
    }
}
