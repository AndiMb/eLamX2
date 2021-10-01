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
import de.elamx.micromechanics.Matrix;
import java.util.HashMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andreas
 */
public class MatrixLoadSaveImpl {

    public void load(Element eLamXElement) {
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("matrices").item(0);
        if (nNode == null){
            return;
        }
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element matrixs = (Element) nNode;
            NodeList list = matrixs.getElementsByTagName("matrix");
            if (list.getLength() > 0) {
                for (int ii = 0; ii < list.getLength(); ii++) {
                    org.w3c.dom.Node matrixNode = list.item(ii);
                    if (matrixNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element matrixElem = (Element) matrixNode;
                        String uuid = matrixElem.getAttribute("uuid");
                        String name = matrixElem.getAttribute("name");
                        String classname = matrixElem.getAttribute("class");
                        if (classname.equals(Matrix.class.getName())) {
                            double E = Double.parseDouble(getTagValue("E", matrixElem));
                            double nue = Double.parseDouble(getTagValue("nue", matrixElem));
                            double rho = Double.parseDouble(getTagValue("rho", matrixElem));
                            double alpha = Double.parseDouble(getTagValue("alpha", matrixElem));
                            double beta = Double.parseDouble(getTagValue("beta", matrixElem));
                            Matrix mat = new Matrix(uuid, name, E, nue, rho, true);
                            mat.setAlpha(alpha);
                            mat.setBeta(beta);
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
        HashMap<String, Matrix> matrixMap = new HashMap<>();
        for (Matrix m : eLamXLookup.getDefault().lookupAll(Matrix.class)){
            matrixMap.put(m.getUUID(), m);
        }
        
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("matrices").item(0);
        Element matrixs = null;
        if (nNode != null && nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            matrixs = (Element) nNode;
        }
        if (matrixs == null){
            matrixs = doc.createElement("matrices");
            eLamXElement.appendChild(matrixs);
        }
        if (matrixs != null) {
            NodeList list = matrixs.getElementsByTagName("matrix");
            if (list.getLength() > 0) {
                for (int ii = list.getLength() - 1; ii > -1; ii--) {
                    org.w3c.dom.Node matrixNode = list.item(ii);
                    if (matrixNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element matrixElem = (Element) matrixNode;
                        Matrix matrix = matrixMap.get(matrixElem.getAttribute("uuid"));
                        if (matrix == null){ // wenn ein Material im Programmspeicher nicht vorhanden ist, aber noch in der Datei, dann lösche in Datei
                            matrixs.removeChild(matrixNode);
                        }else{ // wenn Material sowohl in Datei als auch im Speicher vorhanden, dann überschreibe alle Daten in der Datei
                            matrixElem.setAttribute("class", Matrix.class.getName());
                            matrixElem.setAttribute("name", matrix.getName());
                            matrixElem.setAttribute("uuid", matrix.getUUID());
                            setTagValue("E", Double.toString(matrix.getE()), matrixElem);
                            setTagValue("nue", Double.toString(matrix.getNue()), matrixElem);
                            setTagValue("rho", Double.toString(matrix.getRho()), matrixElem);
                            setTagValue("alpha", Double.toString(matrix.getAlpha()), matrixElem);
                            setTagValue("beta", Double.toString(matrix.getBeta()), matrixElem);
                            matrixMap.remove(matrix.getUUID());
                        }
                    }
                }
            }
            // wenn neue Materialien im Speicher aber nicht in der Datei, dann schreibe in Datei
            if (!matrixMap.isEmpty()){
                for (String uuid : matrixMap.keySet()){
                    Matrix dm = matrixMap.get(uuid);
                    
                    Element matrix = doc.createElement("matrix");
                    Attr attr = doc.createAttribute("class");
                    attr.setValue(Matrix.class.getName());
                    matrix.setAttributeNode(attr);
                    
                    attr = doc.createAttribute("name");
                    attr.setValue(dm.getName());
                    matrix.setAttributeNode(attr);
                    
                    attr = doc.createAttribute("uuid");
                    attr.setValue(dm.getUUID());
                    matrix.setAttributeNode(attr);
                    
                    addValue(doc, "E", Double.toString(dm.getE()), matrix);
                    addValue(doc, "nue", Double.toString(dm.getNue()), matrix);
                    addValue(doc, "rho", Double.toString(dm.getRho()), matrix);
                    addValue(doc, "alpha", Double.toString(dm.getAlpha()), matrix);
                    addValue(doc, "beta", Double.toString(dm.getBeta()), matrix);
                    
                    matrixs.appendChild(matrix);
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
