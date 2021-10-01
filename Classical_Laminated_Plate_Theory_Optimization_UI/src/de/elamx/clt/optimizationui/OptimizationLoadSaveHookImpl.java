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
package de.elamx.clt.optimizationui;

import de.elamx.clt.optimization.OptimizationInput;
import de.elamx.clt.optimization.Optimizer;
import de.elamx.filesupport.LoadSaveHook;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.optimization.MRFC_ModuleDataGenerator;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.util.ArrayList;
import java.util.Collection;
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
@ServiceProvider(service = LoadSaveHook.class, position = 2000)
public class OptimizationLoadSaveHookImpl implements LoadSaveHook {

    @Override
    public void load(Element eLamXElement) {
        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("optimizations").item(0);
        if (nNode == null) {
            return;
        }
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element optimizationsNode = (Element) nNode;

            NodeList optimizationList = optimizationsNode.getElementsByTagName("optimization");
            if (optimizationList.getLength() > 0) {
                Lookup critLookup = Lookups.forPath("elamx/failurecriteria");
                HashMap<String, Criterion> criterionMap = new HashMap<>();
                for (Criterion c : critLookup.lookupAll(Criterion.class)) {
                    criterionMap.put(c.getClass().getName(), c);
                }
                HashMap<String, LayerMaterial> materialMap = new HashMap<>();
                for (LayerMaterial m : eLamXLookup.getDefault().lookupAll(LayerMaterial.class)) {
                    materialMap.put(m.getUUID(), m);
                }
                HashMap<String, Optimizer> optimizerMap = new HashMap<>();
                for (Optimizer optiAlgo : Lookup.getDefault().lookupAll(Optimizer.class)) {
                    optimizerMap.put(optiAlgo.getClass().getName(), optiAlgo);
                }
                Collection<? extends MRFC_ModuleDataGenerator> moduleDataGens = Lookup.getDefault().lookupAll(MRFC_ModuleDataGenerator.class);
                HashMap<String, MRFC_ModuleDataGenerator> genMap = new HashMap<>();
                for (MRFC_ModuleDataGenerator g : moduleDataGens) {
                    genMap.put(g.getCalculatorClassName(), g);
                }
                for (int ii = optimizationList.getLength() - 1; ii > -1; ii--) {
                    org.w3c.dom.Node optimizationNode = optimizationList.item(ii);
                    if (optimizationNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element optimizationElem = (Element) optimizationNode;
                        String name = optimizationElem.getAttribute("name");

                        int angletype = Integer.parseInt(getTagValue("angletype", optimizationElem));
                        String optimizerClassName = getTagValue("optimizer", optimizationElem);

                        double thickness = Double.parseDouble(getTagValue("thickness", optimizationElem));
                        String materialUUID = getTagValue("material", optimizationElem);
                        String criterionClassName = getTagValue("criterion", optimizationElem);
                        boolean symmetricLaminat = Boolean.parseBoolean(getTagValue("symmetriclaminat", optimizationElem));

                        LayerMaterial material = materialMap.get(materialUUID);
                        if (material == null) {
                            continue;
                        }
                        Criterion criterion = null;
                        try {
                            criterion = criterionMap.get(criterionClassName);
                        } catch (NullPointerException ex) {
                        }
                        if (criterion == null) {
                            continue;
                        }
                        Optimizer optimizer = null;
                        try {
                            optimizer = optimizerMap.get(optimizerClassName);
                        } catch (NullPointerException ex) {
                        }
                        if (optimizer == null) {
                            continue;
                        }

                        ArrayList<MinimalReserveFactorCalculator> mrfcArrayList = new ArrayList<>();
                        NodeList mrfcList = optimizationElem.getElementsByTagName("minimalReserverFactorCalculator");
                        for (int jj = mrfcList.getLength() - 1; jj > -1; jj--) {
                            org.w3c.dom.Node mrfcNode = mrfcList.item(jj);
                            if (mrfcNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                Element mrfcElem = (Element) mrfcNode;
                                String classname = mrfcElem.getAttribute("classname");
                                
                                mrfcArrayList.add(genMap.get(classname).load(mrfcElem));
                            }
                        }

                        double[] angles = null;
                        org.w3c.dom.Node anglesNode = optimizationElem.getElementsByTagName("angles").item(0);
                        if (anglesNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element anglesElement = (Element) anglesNode;
                            int number = Integer.parseInt(anglesElement.getAttribute("number"));
                            angles = new double[number];
                            for (int jj = 0; jj < number; jj++) {
                                angles[jj] = Double.parseDouble(getTagValue("angle" + jj, anglesElement));
                            }
                        }
                        
                        if (angles == null){
                            continue;
                        }

                        OptimizationInput input = new OptimizationInput(angles, thickness, material, criterion, mrfcArrayList, symmetricLaminat);

                        OptimizationModuleData data = new OptimizationModuleData(input, true);
                        data.setName(name);
                        data.setAngleType(angletype);
                        data.setOptimizer(optimizer);
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
        HashMap<String, OptimizationModuleData> optiMap = new HashMap<>();
        for (OptimizationModuleData m : eLamXLookup.getDefault().lookupAll(OptimizationModuleData.class)) {
            optiMap.put(m.getUUID(), m);
        }

        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("optimizations").item(0);
        if (nNode == null) {
            Element anglesElem = doc.createElement("optimizations");
            eLamXElement.appendChild(anglesElem);
            nNode = anglesElem;
        }
        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            Element optimizations = (Element) nNode;

            NodeList list = optimizations.getElementsByTagName("optimization");
            if (list.getLength() > 0) {
                for (int ii = list.getLength() - 1; ii > -1; ii--) {
                    optimizations.removeChild(list.item(ii));
                }
            }

            Collection<? extends OptimizationModuleData> col = eLamXLookup.getDefault().lookupAll(OptimizationModuleData.class);

            if (col.isEmpty()) {
                return;
            }

            Collection<? extends MRFC_ModuleDataGenerator> moduleDataGens = Lookup.getDefault().lookupAll(MRFC_ModuleDataGenerator.class);
            HashMap<String, MRFC_ModuleDataGenerator> genMap = new HashMap<>();
            for (MRFC_ModuleDataGenerator g : moduleDataGens) {
                genMap.put(g.getCalculatorClassName(), g);
            }

            for (OptimizationModuleData data : col) {
                OptimizationInput input = data.getOptimizationInput();

                Element dataElement;
                dataElement = doc.createElement("optimization");
                Attr attr = doc.createAttribute("name");
                attr.setValue(data.getName());
                dataElement.setAttributeNode(attr);

                addValue(doc, "angletype", Integer.toString(data.getAngleType()), dataElement);
                addValue(doc, "optimizer", data.getOptimizer().getClass().getName(), dataElement);

                addValue(doc, "thickness", Double.toString(input.getThickness()), dataElement);
                addValue(doc, "material", input.getMaterial().getUUID(), dataElement);
                addValue(doc, "criterion", input.getCriterion().getClass().getName(), dataElement);
                addValue(doc, "symmetriclaminat", Boolean.toString(input.isSymmetricLaminat()), dataElement);

                double[] angles = input.getAngles();

                Element anglesElem = doc.createElement("angles");
                attr = doc.createAttribute("number");
                attr.setValue(Integer.toString(angles.length));
                anglesElem.setAttributeNode(attr);
                dataElement.appendChild(anglesElem);

                for (int ii = 0; ii < angles.length; ii++) {
                    addValue(doc, "angle" + ii, Double.toString(angles[ii]), anglesElem);
                }

                for (MinimalReserveFactorCalculator mrfc : input.getCalculators()) {

                    Element calcElem = doc.createElement("minimalReserverFactorCalculator");
                    attr = doc.createAttribute("classname");
                    attr.setValue(mrfc.getClass().getName());
                    calcElem.setAttributeNode(attr);

                    MRFC_ModuleDataGenerator gen = genMap.get(mrfc.getClass().getName());

                    gen.store(doc, calcElem, mrfc);

                    dataElement.appendChild(calcElem);

                }
                optimizations.appendChild(dataElement);
            }
        }
    }

    private static void addValue(Document doc, String eName, String value, Element eElement) {
        Element newElem = doc.createElement(eName);
        newElem.appendChild(doc.createTextNode(value));
        eElement.appendChild(newElem);
    }
}
