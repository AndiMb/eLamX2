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
package de.elamx.clt.plateui.vibration;

import de.elamx.clt.plate.VibrationInput;
import de.elamx.clt.plate.dmatrix.DMatrixService;
import de.elamx.clt.plate.dmatrix.SpecialOrthotropicDMatrixServiceImpl;
import de.elamx.clt.plate.dmatrix.StandardDMatrixServiceImpl;
import static de.elamx.clt.plateui.deformation.LoadSaveLaminateHookImpl.getTagValue;
import de.elamx.clt.plateui.stiffenerui.LoadSaveStiffeners;
import de.elamx.filesupport.LoadSaveLaminateHook;
import de.elamx.laminate.Laminat;
import java.util.Collection;
import java.util.HashMap;
import org.openide.util.Lookup;
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

    @Override
    public void load(Element laminateElement, Laminat laminate) {
        
        HashMap<String, DMatrixService> dMatServMap = new HashMap<>();
        for (DMatrixService c : Lookup.getDefault().lookupAll(DMatrixService.class)) {
            dMatServMap.put(c.getClass().getName(), c);
        }

        NodeList vibrationList = laminateElement.getElementsByTagName("vibration");
        if (vibrationList.getLength() > 0) {
            for (int ii = vibrationList.getLength() - 1; ii > -1; ii--) {
                org.w3c.dom.Node VibrationNode = vibrationList.item(ii);
                if (VibrationNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element VibrationElement = (Element) VibrationNode;
                    String name = VibrationElement.getAttribute("name");

                    VibrationInput input = new VibrationInput();
                    
                    input.setLength(Double.parseDouble(getTagValue("length", VibrationElement)));
                    input.setWidth(Double.parseDouble(getTagValue("width", VibrationElement)));
                    
                    input.setBcx(Integer.parseInt(getTagValue("bcx", VibrationElement)));
                    input.setBcy(Integer.parseInt(getTagValue("bcy", VibrationElement)));
                    
                    input.setM(Integer.parseInt(getTagValue("m", VibrationElement)));
                    input.setN(Integer.parseInt(getTagValue("n", VibrationElement)));

                    /*
                    Die Prüfung auf wholed ist ausschließlich zur Abwärtskompatibilität.
                    */
                    String value = getTagValue("wholed", VibrationElement);
                    if (value == null){
                        DMatrixService dMatService = null;
                        try{
                            dMatService = dMatServMap.get(getTagValue("dmatrixservice", VibrationElement));
                        }catch (NullPointerException ex){
                        }
                        if (dMatService == null) {
                            dMatService = dMatServMap.get(StandardDMatrixServiceImpl.class.getName());
                        }
                        input.setDMatrixService(dMatService);
                    }else{
                        boolean wholeD = Boolean.parseBoolean(value);
                        if (wholeD){
                            input.setDMatrixService(dMatServMap.get(StandardDMatrixServiceImpl.class.getName()));
                        }else{
                            input.setDMatrixService(dMatServMap.get(SpecialOrthotropicDMatrixServiceImpl.class.getName()));
                        }
                    }

                    LoadSaveStiffeners.loadStiffeners(VibrationElement, input);
                    
                    VibrationModuleData data = new VibrationModuleData(laminate, input);
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
    public void store(Document doc, Element laminateElement, Laminat laminate) {

        NodeList list = laminateElement.getElementsByTagName("vibration");
        if (list.getLength() > 0) {
            for (int ii = list.getLength() - 1; ii > -1; ii--) {
                laminateElement.removeChild(list.item(ii));
            }
        }

        Collection<? extends VibrationModuleData> col = laminate.getLookup().lookupAll(VibrationModuleData.class);

        if (col.isEmpty()) {
            return;
        }

        for (VibrationModuleData data : col) {
            VibrationInput input = data.getVibrationInput();

            Element dataElement;
            dataElement = doc.createElement("vibration");
            Attr attr = doc.createAttribute("name");
            attr.setValue(data.getName());
            dataElement.setAttributeNode(attr);
            
            addValue(doc, "length", Double.toString(input.getLength()), dataElement);
            addValue(doc, "width", Double.toString(input.getWidth()), dataElement);
            
            addValue(doc, "bcx", Integer.toString(input.getBcx()), dataElement);
            addValue(doc, "bcy", Integer.toString(input.getBcy()), dataElement);
            
            addValue(doc, "m", Integer.toString(input.getM()), dataElement);
            addValue(doc, "n", Integer.toString(input.getN()), dataElement);
            
            addValue(doc, "dmatrixservice", input.getDMatrixService().getClass().getName(), dataElement);
                
            LoadSaveStiffeners.storeStiffeners(doc, dataElement, input);

            laminateElement.appendChild(dataElement);
        }
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
    
}
