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
import de.elamx.micromechanics.Matrix;
import de.elamx.micromechanics.MicroMechanicMaterial;
import de.elamx.micromechanics.models.MicroMechModel;
import de.elamx.micromechanics.models.Mischungsregel_m;
import java.util.HashMap;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas Hauffe
 */
public class MicroMechanicMaterialLoadSaveImpl {

    public void load(Element eLamXElement) {
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("materials").item(0);
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element materials = (Element) nNode;
            NodeList list = materials.getElementsByTagName("material");
            if (list.getLength() > 0) {
                HashMap<String, Fiber> fibreMap = new HashMap<>();
                for (Fiber f : eLamXLookup.getDefault().lookupAll(Fiber.class)) {
                    fibreMap.put(f.getUUID(), f);
                }
                HashMap<String, Matrix> matrixMap = new HashMap<>();
                for (Matrix m : eLamXLookup.getDefault().lookupAll(Matrix.class)) {
                    matrixMap.put(m.getUUID(), m);
                }
                Lookup mmModelLookup = Lookups.forPath("elamx/micromechmodel");
                HashMap<String, MicroMechModel> mmModelMap = new HashMap<>();
                for (MicroMechModel mmm : mmModelLookup.lookupAll(MicroMechModel.class)) {
                    mmModelMap.put(mmm.getClass().getName(), mmm);
                }
                for (int ii = 0; ii < list.getLength(); ii++) {
                    org.w3c.dom.Node materialNode = list.item(ii);
                    if (materialNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element materialElem = (Element) materialNode;
                        String uuid = materialElem.getAttribute("uuid");
                        String name = materialElem.getAttribute("name");
                        String classname = materialElem.getAttribute("class");
                        if (classname.equals(MicroMechanicMaterial.class.getName())) {

                            String UID = getTagValue("fibre", materialElem);
                            Fiber fibre = fibreMap.get(UID);
                            if (fibre == null) {
                                continue;
                            }
                            UID = getTagValue("matrix", materialElem);
                            Matrix matrix = matrixMap.get(UID);
                            if (matrix == null) {
                                continue;
                            }

                            double phi = Double.parseDouble(getTagValue("phi", materialElem));
                            /*
                            * ACHTUNG: Wenn ein mikromechanisches Modell gegeben ist, werden hier nicht
                            * eventuell zuvor manuell eingegebene Werte gespeichert, sondern die 
                            * berechneten des mikromech. Model.
                            */
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

                            MicroMechModel EParModel = null;
                            try {
                                EParModel = mmModelMap.get(getTagValue("Epar_micromechmodel", materialElem));
                            } catch (NullPointerException ex) {
                            }
                            if (EParModel == null) {
                                EParModel = mmModelMap.get(Mischungsregel_m.class.getName());
                            }

                            MicroMechModel ENorModel = null;
                            try {
                                ENorModel = mmModelMap.get(getTagValue("Enor_micromechmodel", materialElem));
                            } catch (NullPointerException ex) {
                            }
                            if (ENorModel == null) {
                                ENorModel = mmModelMap.get(Mischungsregel_m.class.getName());
                            }

                            MicroMechModel Nue12Model = null;
                            try {
                                Nue12Model = mmModelMap.get(getTagValue("Nue12_micromechmodel", materialElem));
                            } catch (NullPointerException ex) {
                            }
                            if (Nue12Model == null) {
                                Nue12Model = mmModelMap.get(Mischungsregel_m.class.getName());
                            }

                            MicroMechModel GModel = null;
                            try {
                                GModel = mmModelMap.get(getTagValue("G_micromechmodel", materialElem));
                            } catch (NullPointerException ex) {
                            }
                            if (GModel == null) {
                                GModel = mmModelMap.get(Mischungsregel_m.class.getName());
                            }
                            MicroMechanicMaterial mat = new MicroMechanicMaterial(uuid, name, fibre, matrix, phi, true);
                            mat.setEpar(Epar);
                            mat.setEnor(Enor);
                            mat.setNue12(nue12);
                            mat.setG(G);
                            mat.setG13(G13);
                            mat.setG23(G23);
                            mat.setRho(rho);
                            mat.setAlphaTPar(alphaTPar);
                            mat.setAlphaTNor(alphaTNor);
                            mat.setBetaPar(betaPar);
                            mat.setBetaNor(betaNor);
                            mat.setRParTen(RParTen);
                            mat.setRParCom(RParCom);
                            mat.setRNorTen(RNorTen);
                            mat.setRNorCom(RNorCom);
                            mat.setRShear(RShear);
                            mat.setEParModel(EParModel);
                            mat.setENorModel(ENorModel);
                            mat.setNue12Model(Nue12Model);
                            mat.setGModel(GModel);
                            for (String key : mat.getAdditionalValueKeySet()) {
                                String stringVal = getTagValue(key, materialElem);
                                if (stringVal == null) {
                                    continue;
                                }
                                mat.putAdditionalValue(key, Double.parseDouble(stringVal));
                            }
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
        HashMap<String, MicroMechanicMaterial> materialMap = new HashMap<>();
        for (MicroMechanicMaterial m : eLamXLookup.getDefault().lookupAll(MicroMechanicMaterial.class)) {
            materialMap.put(m.getUUID(), m);
        }

        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("materials").item(0);
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element materials = (Element) nNode;
            NodeList list = materials.getElementsByTagName("material");
            if (list.getLength() > 0) {
                for (int ii = list.getLength() - 1; ii > -1; ii--) {
                    org.w3c.dom.Node materialNode = list.item(ii);
                    if (materialNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element materialElem = (Element) materialNode;
                        MicroMechanicMaterial mmm = materialMap.get(materialElem.getAttribute("uuid"));
                        if (mmm == null) { // wenn ein Material im Programmspeicher nicht vorhanden ist, aber noch in der Datei, dann lösche in Datei
                            if (materialElem.getAttribute("class").equals(MicroMechanicMaterial.class.getName())) {
                                materials.removeChild(materialNode);
                            }
                        } else { // wenn Material sowohl in Datei als auch im Speicher vorhanden, dann überschreibe alle Daten in der Datei
                            materialElem.setAttribute("class", MicroMechanicMaterial.class.getName());
                            materialElem.setAttribute("name", mmm.getName());
                            materialElem.setAttribute("uuid", mmm.getUUID());
                            setTagValue("fibre", mmm.getFibre().getUUID(), materialElem);
                            setTagValue("matrix", mmm.getMatrix().getUUID(), materialElem);
                            setTagValue("phi", Double.toString(mmm.getPhi()), materialElem);
                            setTagValue("Epar", Double.toString(mmm.getEpar()), materialElem);
                            setTagValue("Enor", Double.toString(mmm.getEnor()), materialElem);
                            setTagValue("nue12", Double.toString(mmm.getNue12()), materialElem);
                            setTagValue("G", Double.toString(mmm.getG()), materialElem);
                            if (getTagValue("G13", materialElem) != null){
                                setTagValue("G13", Double.toString(mmm.getG13()), materialElem);
                            }else{
                                addValue(doc, "G13", Double.toString(mmm.getG13()), materialElem);
                            }
                            if (getTagValue("G23", materialElem) != null){
                                setTagValue("G23", Double.toString(mmm.getG23()), materialElem);
                            }else{
                                addValue(doc, "G23", Double.toString(mmm.getG23()), materialElem);
                            }
                            setTagValue("rho", Double.toString(mmm.getRho()), materialElem);
                            setTagValue("alphaTPar", Double.toString(mmm.getAlphaTPar()), materialElem);
                            setTagValue("alphaTNor", Double.toString(mmm.getAlphaTNor()), materialElem);
                            setTagValue("betaPar", Double.toString(mmm.getBetaPar()), materialElem);
                            setTagValue("betaNor", Double.toString(mmm.getBetaNor()), materialElem);
                            setTagValue("RParTen", Double.toString(mmm.getRParTen()), materialElem);
                            setTagValue("RParCom", Double.toString(mmm.getRParCom()), materialElem);
                            setTagValue("RNorTen", Double.toString(mmm.getRNorTen()), materialElem);
                            setTagValue("RNorCom", Double.toString(mmm.getRNorCom()), materialElem);
                            setTagValue("RShear", Double.toString(mmm.getRShear()), materialElem);
                            setTagValue("Epar_micromechmodel", mmm.getEParModel().getClass().getName(), materialElem);
                            setTagValue("Enor_micromechmodel", mmm.getENorModel().getClass().getName(), materialElem);
                            setTagValue("Nue12_micromechmodel", mmm.getNue12Model().getClass().getName(), materialElem);
                            setTagValue("G_micromechmodel", mmm.getGModel().getClass().getName(), materialElem);
                            for (String key : mmm.getAdditionalValueKeySet()) {
                                String stringVal = getTagValue(key, materialElem);
                                if (stringVal == null) {
                                    addValue(doc, key, Double.toString(mmm.getAdditionalValue(key)), materialElem);
                                } else {
                                    setTagValue(key, Double.toString(mmm.getAdditionalValue(key)), materialElem);
                                }
                            }
                            materialMap.remove(mmm.getUUID());
                        }
                    }
                }
            }
            // wenn neue Materialien im Speicher aber nicht in der Datei, dann schreibe in Datei
            if (!materialMap.isEmpty()) {
                for (String uuid : materialMap.keySet()) {
                    MicroMechanicMaterial mmm = materialMap.get(uuid);

                    Element material = doc.createElement("material");
                    Attr attr = doc.createAttribute("class");
                    attr.setValue(MicroMechanicMaterial.class.getName());
                    material.setAttributeNode(attr);

                    attr = doc.createAttribute("name");
                    attr.setValue(mmm.getName());
                    material.setAttributeNode(attr);

                    attr = doc.createAttribute("uuid");
                    attr.setValue(mmm.getUUID());
                    material.setAttributeNode(attr);

                    addValue(doc, "fibre", mmm.getFibre().getUUID(), material);
                    addValue(doc, "matrix", mmm.getMatrix().getUUID(), material);
                    addValue(doc, "phi", Double.toString(mmm.getPhi()), material);
                    addValue(doc, "Epar", Double.toString(mmm.getEpar()), material);
                    addValue(doc, "Enor", Double.toString(mmm.getEnor()), material);
                    addValue(doc, "nue12", Double.toString(mmm.getNue12()), material);
                    addValue(doc, "G", Double.toString(mmm.getG()), material);
                    addValue(doc, "G13", Double.toString(mmm.getG13()), material);
                    addValue(doc, "G23", Double.toString(mmm.getG23()), material);
                    addValue(doc, "rho", Double.toString(mmm.getRho()), material);
                    addValue(doc, "alphaTPar", Double.toString(mmm.getAlphaTPar()), material);
                    addValue(doc, "alphaTNor", Double.toString(mmm.getAlphaTNor()), material);
                    addValue(doc, "betaPar", Double.toString(mmm.getBetaPar()), material);
                    addValue(doc, "betaNor", Double.toString(mmm.getBetaNor()), material);
                    addValue(doc, "RParTen", Double.toString(mmm.getRParTen()), material);
                    addValue(doc, "RParCom", Double.toString(mmm.getRParCom()), material);
                    addValue(doc, "RNorTen", Double.toString(mmm.getRNorTen()), material);
                    addValue(doc, "RNorCom", Double.toString(mmm.getRNorCom()), material);
                    addValue(doc, "RShear", Double.toString(mmm.getRShear()), material);
                    addValue(doc, "Epar_micromechmodel", mmm.getEParModel().getClass().getName(), material);
                    addValue(doc, "Enor_micromechmodel", mmm.getENorModel().getClass().getName(), material);
                    addValue(doc, "Nue12_micromechmodel", mmm.getNue12Model().getClass().getName(), material);
                    addValue(doc, "G_micromechmodel", mmm.getGModel().getClass().getName(), material);
                    for (String key : mmm.getAdditionalValueKeySet()) {
                        String stringVal = getTagValue(key, material);
                        if (stringVal == null) {
                            addValue(doc, key, Double.toString(mmm.getAdditionalValue(key)), material);
                        } else {
                            setTagValue(key, Double.toString(mmm.getAdditionalValue(key)), material);
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
