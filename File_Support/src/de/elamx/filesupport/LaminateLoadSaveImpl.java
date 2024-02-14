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
package de.elamx.filesupport;

import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.Puck;
import java.util.ArrayList;
import java.util.HashMap;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service=LoadSaveHook.class, position = 1000)
public class LaminateLoadSaveImpl implements LoadSaveHook{

    @Override
    public void load(Element eLamXElement) {
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("laminates").item(0);
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element laminatesNode = (Element) nNode;

            NodeList laminateList = laminatesNode.getElementsByTagName("laminate");
            if (laminateList.getLength() > 0) {
                Lookup critLookup = Lookups.forPath("elamx/failurecriteria");
                HashMap<String, Criterion> criterionMap = new HashMap<>();
                for (Criterion c: critLookup.lookupAll(Criterion.class)){
                    criterionMap.put(c.getClass().getName(), c);
                }
                HashMap<String, LayerMaterial> materialMap = new HashMap<>();
                for (LayerMaterial m : eLamXLookup.getDefault().lookupAll(LayerMaterial.class)) {
                    materialMap.put(m.getUUID(), m);
                }
                for (int ii = laminateList.getLength() - 1; ii > -1; ii--) {
                    org.w3c.dom.Node laminateNode = laminateList.item(ii);
                    if (laminateNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element laminateElem = (Element) laminateNode;
                        String uuid = laminateElem.getAttribute("uuid");
                        String name = laminateElem.getAttribute("name");
                        boolean symmetric = Boolean.parseBoolean(laminateElem.getAttribute("symmetric"));
                        boolean wmL       = Boolean.parseBoolean(laminateElem.getAttribute("with_middle_layer"));
                        boolean invertZ   = Boolean.parseBoolean(laminateElem.getAttribute("invert_z"));
                        String sOffset = laminateElem.getAttribute("offset");
                        double  offset    = 0.0;
                        if (sOffset != null && !sOffset.isEmpty()){
                            offset = Double.parseDouble(sOffset);
                        }

                        Laminat laminate = new Laminat(uuid, name);
                        laminate.setSymmetric(symmetric);
                        laminate.setWithMiddleLayer(wmL);
                        laminate.setInvertZ(invertZ);
                        laminate.setOffset(offset);

                        NodeList layerList = laminateElem.getElementsByTagName("layer");
                        if (layerList.getLength() > 0) {
                            for (int jj = 0; jj < layerList.getLength(); jj++) {
                                org.w3c.dom.Node layerNode = layerList.item(jj);
                                if (layerNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                    Element layerElem = (Element) layerNode;
                                    uuid = layerElem.getAttribute("uuid");
                                    name = layerElem.getAttribute("name");

                                    double angle = Double.parseDouble(getTagValue("angle", layerElem));
                                    double thickness = Double.parseDouble(getTagValue("thickness", layerElem));
                                    String materialUID = getTagValue("material", layerElem);
                                    LayerMaterial material = materialMap.get(materialUID);
                                    if (material == null) {
                                        continue;
                                    }
                                    Criterion criterion = null;
                                    try{
                                        criterion = criterionMap.get(getTagValue("criterion", layerElem));
                                    }catch (NullPointerException ex){
                                    }
                                    if (criterion == null) {
                                        criterion = criterionMap.get(Puck.class.getName());
                                    }
                                    Layer layer = new Layer(uuid, name, material, angle, thickness, criterion);
                                    laminate.addLayer(layer);
                                }
                            }
                        }
                        
                        for (LoadSaveLaminateHook lsh : Lookup.getDefault().lookupAll(LoadSaveLaminateHook.class)) {
                            lsh.load(laminateElem, laminate);
                        }
                    }
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
    public void store(Document doc, Element eLamXElement) {
        
        ArrayList<Laminat> laminatesList = new ArrayList<>();
        laminatesList.addAll(eLamXLookup.getDefault().lookupAll(Laminat.class));
        
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("laminates").item(0);
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element laminates = (Element) nNode;
            
            NodeList list = laminates.getElementsByTagName("laminate");
            if (list.getLength() > 0){
                for (int ii = list.getLength()-1; ii > -1 ; ii--){
                    laminates.removeChild(list.item(ii));
                }
            }
            
            for (Laminat lam : laminatesList) {
                Element laminate = doc.createElement("laminate");
                Attr attr = doc.createAttribute("uuid");
                attr.setValue(lam.getUUID());
                laminate.setAttributeNode(attr);
                
                attr = doc.createAttribute("name");
                attr.setValue(lam.getName());
                laminate.setAttributeNode(attr);
                
                attr = doc.createAttribute("symmetric");
                attr.setValue(Boolean.toString(lam.isSymmetric()));
                laminate.setAttributeNode(attr);
                
                attr = doc.createAttribute("with_middle_layer");
                attr.setValue(Boolean.toString(lam.isWithMiddleLayer()));
                laminate.setAttributeNode(attr);
                
                attr = doc.createAttribute("invert_z");
                attr.setValue(Boolean.toString(lam.isInvertZ()));
                laminate.setAttributeNode(attr);
                
                attr = doc.createAttribute("offset");
                attr.setValue(Double.toString(lam.getOffset()));
                laminate.setAttributeNode(attr);
                
                for (Layer lay : lam.getLayers()){
                    Element layer = doc.createElement("layer");
                    attr = doc.createAttribute("uuid");
                    attr.setValue(lay.getUUID());
                    layer.setAttributeNode(attr);
                    
                    attr = doc.createAttribute("name");
                    attr.setValue(lay.getName());
                    layer.setAttributeNode(attr);
                    
                    addValue(doc, "thickness", Double.toString(lay.getThickness()), layer);
                    addValue(doc, "angle", Double.toString(lay.getAngle()), layer);
                    addValue(doc, "material", lay.getMaterial().getUUID(), layer);
                    addValue(doc, "criterion", lay.getCriterion().getClass().getName(), layer);
                    
                    laminate.appendChild(layer);
                }
                
                for (LoadSaveLaminateHook lsh : Lookup.getDefault().lookupAll(LoadSaveLaminateHook.class)) {
                    lsh.store(doc, laminate, lam);
                }
                
                laminates.appendChild(laminate);
            }
        }
    }
    
    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
}
