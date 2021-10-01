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
package de.elamx.micromechanicsui;

import de.elamx.laminate.eLamXLookup;
import de.elamx.micromechanics.Fiber;
import java.util.HashMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas
 */
public class FibreLoadSaveImpl {

    public void load(Element eLamXElement) {
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("fibres").item(0);
        if (nNode == null){
            return;
        }
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element fibres = (Element) nNode;
            NodeList list = fibres.getElementsByTagName("fibre");
            if (list.getLength() > 0) {
                for (int ii = 0; ii < list.getLength(); ii++) {
                    org.w3c.dom.Node fibreNode = list.item(ii);
                    if (fibreNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element fibreElem = (Element) fibreNode;
                        String uuid = fibreElem.getAttribute("uuid");
                        String name = fibreElem.getAttribute("name");
                        String classname = fibreElem.getAttribute("class");
                        if (classname.equals(Fiber.class.getName())) {
                            double Epar = Double.parseDouble(getTagValue("Epar", fibreElem));
                            double Enor = Double.parseDouble(getTagValue("Enor", fibreElem));
                            double nue12 = Double.parseDouble(getTagValue("nue12", fibreElem));
                            double G = Double.parseDouble(getTagValue("G", fibreElem));
                            String val = getTagValue("G13", fibreElem);
                            double G13 = 0.0;
                            if (val != null){
                                G13 = Double.parseDouble(val);
                            }
                            val = getTagValue("G23", fibreElem);
                            double G23 = 0.0;
                            if (val != null){
                                G23 = Double.parseDouble(val);
                            }
                            double rho = Double.parseDouble(getTagValue("rho", fibreElem));
                            double alphaTPar = Double.parseDouble(getTagValue("alphaTPar", fibreElem));
                            double alphaTNor = Double.parseDouble(getTagValue("alphaTNor", fibreElem));
                            double betaPar = Double.parseDouble(getTagValue("betaPar", fibreElem));
                            double betaNor = Double.parseDouble(getTagValue("betaNor", fibreElem));
                            Fiber mat = new Fiber(uuid, name, Epar, Enor, nue12, G, rho, true);
                            mat.setG13(G13);
                            mat.setG23(G23);
                            mat.setAlphaTPar(alphaTPar);
                            mat.setAlphaTNor(alphaTNor);
                            mat.setBetaPar(betaPar);
                            mat.setBetaNor(betaNor);
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

    public void store(Document doc, Element eLamXElement) {
        HashMap<String, Fiber> fiberMap = new HashMap<>();
        for (Fiber m : eLamXLookup.getDefault().lookupAll(Fiber.class)){
            fiberMap.put(m.getUUID(), m);
        }
        
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("fibres").item(0);
        Element fibres = null;
        if (nNode != null && nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            fibres = (Element) nNode;
        }
        if (fibres == null){
            fibres = doc.createElement("fibres");
            eLamXElement.appendChild(fibres);
        }
        if (fibres != null) {
            NodeList list = fibres.getElementsByTagName("fibre");
            if (list.getLength() > 0) {
                for (int ii = list.getLength() - 1; ii > -1; ii--) {
                    org.w3c.dom.Node fibreNode = list.item(ii);
                    if (fibreNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element fibreElem = (Element) fibreNode;
                        Fiber fibre = fiberMap.get(fibreElem.getAttribute("uuid"));
                        if (fibre == null){ // wenn ein Material im Programmspeicher nicht vorhanden ist, aber noch in der Datei, dann lösche in Datei
                            fibres.removeChild(fibreNode);
                        }else{ // wenn Material sowohl in Datei als auch im Speicher vorhanden, dann überschreibe alle Daten in der Datei
                            fibreElem.setAttribute("class", Fiber.class.getName());
                            fibreElem.setAttribute("name", fibre.getName());
                            fibreElem.setAttribute("uuid", fibre.getUUID());
                            setTagValue("Epar", Double.toString(fibre.getEpar()), fibreElem);
                            setTagValue("Enor", Double.toString(fibre.getEnor()), fibreElem);
                            setTagValue("nue12", Double.toString(fibre.getNue12()), fibreElem);
                            setTagValue("G", Double.toString(fibre.getG()), fibreElem);
                            if (getTagValue("G13", fibreElem) != null){
                                setTagValue("G13", Double.toString(fibre.getG13()), fibreElem);
                            }else{
                                addValue(doc, "G13", Double.toString(fibre.getG13()), fibreElem);
                            }
                            if (getTagValue("G23", fibreElem) != null){
                                setTagValue("G23", Double.toString(fibre.getG23()), fibreElem);
                            }else{
                                addValue(doc, "G23", Double.toString(fibre.getG23()), fibreElem);
                            }
                            setTagValue("rho", Double.toString(fibre.getRho()), fibreElem);
                            setTagValue("alphaTPar", Double.toString(fibre.getAlphaTPar()), fibreElem);
                            setTagValue("alphaTNor", Double.toString(fibre.getAlphaTNor()), fibreElem);
                            setTagValue("betaPar", Double.toString(fibre.getBetaPar()), fibreElem);
                            setTagValue("betaNor", Double.toString(fibre.getBetaNor()), fibreElem);
                            fiberMap.remove(fibre.getUUID());
                        }
                    }
                }
            }
            // wenn neue Materialien im Speicher aber nicht in der Datei, dann schreibe in Datei
            if (!fiberMap.isEmpty()){
                for (String uuid : fiberMap.keySet()){
                    Fiber dm = fiberMap.get(uuid);
                    
                    Element fibre = doc.createElement("fibre");
                    Attr attr = doc.createAttribute("class");
                    attr.setValue(Fiber.class.getName());
                    fibre.setAttributeNode(attr);
                    
                    attr = doc.createAttribute("name");
                    attr.setValue(dm.getName());
                    fibre.setAttributeNode(attr);
                    
                    attr = doc.createAttribute("uuid");
                    attr.setValue(dm.getUUID());
                    fibre.setAttributeNode(attr);
                    
                    addValue(doc, "Epar", Double.toString(dm.getEpar()), fibre);
                    addValue(doc, "Enor", Double.toString(dm.getEnor()), fibre);
                    addValue(doc, "nue12", Double.toString(dm.getNue12()), fibre);
                    addValue(doc, "G", Double.toString(dm.getG()), fibre);
                    addValue(doc, "G13", Double.toString(dm.getG13()), fibre);
                    addValue(doc, "G23", Double.toString(dm.getG23()), fibre);
                    addValue(doc, "rho", Double.toString(dm.getRho()), fibre);
                    addValue(doc, "alphaTPar", Double.toString(dm.getAlphaTPar()), fibre);
                    addValue(doc, "alphaTNor", Double.toString(dm.getAlphaTNor()), fibre);
                    addValue(doc, "betaPar", Double.toString(dm.getBetaPar()), fibre);
                    addValue(doc, "betaNor", Double.toString(dm.getBetaNor()), fibre);
                    
                    fibres.appendChild(fibre);
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
