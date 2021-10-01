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

import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.eLamXLookup;
import java.util.ArrayList;
import java.util.HashMap;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = LoadSaveHook.class, position = 200)
public class DefaultMaterialLoadSaveImpl implements LoadSaveHook {

    @Override
    public void load(Element eLamXElement) {
        readDefaultMaterials(eLamXElement, true);
    }
    
    public static ArrayList<DefaultMaterial> readDefaultMaterials(Element eLamXElement, boolean addToLookup){
        
        ArrayList<DefaultMaterial> materialsList = new ArrayList<>();
        
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("materials").item(0);
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element materials = (Element) nNode;
            NodeList list = materials.getElementsByTagName("material");
            if (list.getLength() > 0) {
                for (int ii = 0; ii < list.getLength(); ii++) {
                    org.w3c.dom.Node materialNode = list.item(ii);
                    if (materialNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element materialElem = (Element) materialNode;
                        String uuid = materialElem.getAttribute("uuid");
                        String name = materialElem.getAttribute("name");
                        String classname = materialElem.getAttribute("class");
                        if (classname.equals(DefaultMaterial.class.getName())) {
                            double Epar = Double.parseDouble(getTagValue("Epar", materialElem));
                            double Enor = Double.parseDouble(getTagValue("Enor", materialElem));
                            double nue12 = Double.parseDouble(getTagValue("nue12", materialElem));
                            double G = Double.parseDouble(getTagValue("G", materialElem));
                            String val = getTagValue("G13", materialElem);
                            double G13 = 0.0;
                            if (val != null){
                                G13 = Double.parseDouble(val);
                            }
                            val = getTagValue("G23", materialElem);
                            double G23 = 0.0;
                            if (val != null){
                                G23 = Double.parseDouble(val);
                            }
                            double rho = Double.parseDouble(getTagValue("rho", materialElem));
                            double alphaTPar = Double.parseDouble(getTagValue("alphaTPar", materialElem));
                            double alphaTNor = Double.parseDouble(getTagValue("alphaTNor", materialElem));
                            double betaPar = Double.parseDouble(getTagValue("betaPar", materialElem));
                            double betaNor = Double.parseDouble(getTagValue("betaNor", materialElem));
                            double RParTen = Double.parseDouble(getTagValue("RParTen", materialElem));
                            double RParCom = Double.parseDouble(getTagValue("RParCom", materialElem));
                            double RNorTen = Double.parseDouble(getTagValue("RNorTen", materialElem));
                            double RNorCom = Double.parseDouble(getTagValue("RNorCom", materialElem));
                            double RShear = Double.parseDouble(getTagValue("RShear", materialElem));
                            DefaultMaterial mat = new DefaultMaterial(uuid, name, Epar, Enor, nue12, G, rho, addToLookup);
                            mat.setG13(G13);
                            mat.setG23(G23);
                            mat.setAlphaTPar(alphaTPar);
                            mat.setAlphaTNor(alphaTNor);
                            mat.setBetaPar(betaPar);
                            mat.setBetaNor(betaNor);
                            mat.setRParTen(RParTen);
                            mat.setRParCom(RParCom);
                            mat.setRNorTen(RNorTen);
                            mat.setRNorCom(RNorCom);
                            mat.setRShear(RShear);
                            for (String key : mat.getAdditionalValueKeySet()) {
                                String stringVal = getTagValue(key, materialElem);
                                if (stringVal == null) {
                                    continue;
                                }
                                mat.putAdditionalValue(key, Double.parseDouble(stringVal));
                            }
                            materialsList.add(mat);
                        }
                    }
                }
            }
        }
        
        return materialsList;
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
        HashMap<String, DefaultMaterial> materialMap = new HashMap<>();
        for (DefaultMaterial m : eLamXLookup.getDefault().lookupAll(DefaultMaterial.class)) {
            materialMap.put(m.getUUID(), m);
        }

        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("materials").item(0);
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element materials = (Element) nNode;
            int size = materials.getChildNodes().getLength();
            NodeList list = materials.getElementsByTagName("material");
            if (list.getLength() > 0) {
                for (int ii = list.getLength() - 1; ii > -1; ii--) {
                    org.w3c.dom.Node materialNode = list.item(ii);
                    if (materialNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element materialElem = (Element) materialNode;
                        DefaultMaterial dm = materialMap.get(materialElem.getAttribute("uuid"));
                        if (dm == null) { // wenn ein Material im Programmspeicher nicht vorhanden ist, aber noch in der Datei, dann lösche in Datei
                            if (materialElem.getAttribute("class").equals(DefaultMaterial.class.getName())) {
                                materials.removeChild(materialNode);
                            }
                        } else { // wenn Material sowohl in Datei als auch im Speicher vorhanden, dann überschreibe alle Daten in der Datei
                            materialElem.setAttribute("class", DefaultMaterial.class.getName());
                            materialElem.setAttribute("name", dm.getName());
                            materialElem.setAttribute("uuid", dm.getUUID());
                            setTagValue("Epar", Double.toString(dm.getEpar()), materialElem);
                            setTagValue("Enor", Double.toString(dm.getEnor()), materialElem);
                            setTagValue("nue12", Double.toString(dm.getNue12()), materialElem);
                            setTagValue("G", Double.toString(dm.getG()), materialElem);
                            if (getTagValue("G13", materialElem) != null){
                                setTagValue("G13", Double.toString(dm.getG13()), materialElem);
                            }else{
                                addValue(doc, "G13", Double.toString(dm.getG13()), materialElem);
                            }
                            if (getTagValue("G23", materialElem) != null){
                                setTagValue("G23", Double.toString(dm.getG23()), materialElem);
                            }else{
                                addValue(doc, "G23", Double.toString(dm.getG23()), materialElem);
                            }
                            setTagValue("rho", Double.toString(dm.getRho()), materialElem);
                            setTagValue("alphaTPar", Double.toString(dm.getAlphaTPar()), materialElem);
                            setTagValue("alphaTNor", Double.toString(dm.getAlphaTNor()), materialElem);
                            setTagValue("betaPar", Double.toString(dm.getBetaPar()), materialElem);
                            setTagValue("betaNor", Double.toString(dm.getBetaNor()), materialElem);
                            setTagValue("RParTen", Double.toString(dm.getRParTen()), materialElem);
                            setTagValue("RParCom", Double.toString(dm.getRParCom()), materialElem);
                            setTagValue("RNorTen", Double.toString(dm.getRNorTen()), materialElem);
                            setTagValue("RNorCom", Double.toString(dm.getRNorCom()), materialElem);
                            setTagValue("RShear", Double.toString(dm.getRShear()), materialElem);
                            for (String key : dm.getAdditionalValueKeySet()) {
                                String stringVal = getTagValue(key, materialElem);
                                if (stringVal == null) {
                                    addValue(doc, key, Double.toString(dm.getAdditionalValue(key)), materialElem);
                                } else {
                                    setTagValue(key, Double.toString(dm.getAdditionalValue(key)), materialElem);
                                }
                            }
                            materialMap.remove(dm.getUUID());
                        }
                    }
                }
            }
            // wenn neue Materialien im Speicher aber nicht in der Datei, dann schreibe in Datei
            if (!materialMap.isEmpty()) {
                for (String uuid : materialMap.keySet()) {
                    DefaultMaterial dm = materialMap.get(uuid);

                    Element material = doc.createElement("material");
                    Attr attr = doc.createAttribute("class");
                    attr.setValue(DefaultMaterial.class.getName());
                    material.setAttributeNode(attr);

                    attr = doc.createAttribute("name");
                    attr.setValue(dm.getName());
                    material.setAttributeNode(attr);

                    attr = doc.createAttribute("uuid");
                    attr.setValue(dm.getUUID());
                    material.setAttributeNode(attr);

                    addValue(doc, "Epar", Double.toString(dm.getEpar()), material);
                    addValue(doc, "Enor", Double.toString(dm.getEnor()), material);
                    addValue(doc, "nue12", Double.toString(dm.getNue12()), material);
                    addValue(doc, "G", Double.toString(dm.getG()), material);
                    addValue(doc, "G13", Double.toString(dm.getG13()), material);
                    addValue(doc, "G23", Double.toString(dm.getG23()), material);
                    addValue(doc, "rho", Double.toString(dm.getRho()), material);
                    addValue(doc, "alphaTPar", Double.toString(dm.getAlphaTPar()), material);
                    addValue(doc, "alphaTNor", Double.toString(dm.getAlphaTNor()), material);
                    addValue(doc, "betaPar", Double.toString(dm.getBetaPar()), material);
                    addValue(doc, "betaNor", Double.toString(dm.getBetaNor()), material);
                    addValue(doc, "RParTen", Double.toString(dm.getRParTen()), material);
                    addValue(doc, "RParCom", Double.toString(dm.getRParCom()), material);
                    addValue(doc, "RNorTen", Double.toString(dm.getRNorTen()), material);
                    addValue(doc, "RNorCom", Double.toString(dm.getRNorCom()), material);
                    addValue(doc, "RShear", Double.toString(dm.getRShear()), material);
                    for (String key : dm.getAdditionalValueKeySet()) {
                        String stringVal = getTagValue(key, material);
                        if (stringVal == null) {
                            addValue(doc, key, Double.toString(dm.getAdditionalValue(key)), material);
                        } else {
                            setTagValue(key, Double.toString(dm.getAdditionalValue(key)), material);
                        }
                    }

                    materials.appendChild(material);
                }
            }
        }
    }

    private static void setTagValue(String sTag, String value, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        org.w3c.dom.Node nValue = nlList.item(0);

        nValue.setNodeValue(value);
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }

}
