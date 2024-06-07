/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.elamx.reducedinput;

import de.elamx.reducedinput.dataobjects.LoadCaseData;
import de.elamx.reducedinput.dataobjects.BucklingData;
import de.elamx.reducedinput.dataobjects.LayerData;
import de.elamx.reducedinput.dataobjects.CalculationData;
import de.elamx.reducedinput.dataobjects.MaterialData;
import de.elamx.clt.CLT_Input;
import de.elamx.clt.calculation.CalculationModuleData;
import de.elamx.clt.plate.BucklingInput;
import de.elamx.clt.plateui.buckling.BucklingModuleData;
import de.elamx.laminate.DataLayer;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.failure.Criterion;
import java.util.HashMap;
import java.util.UUID;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Dient dem Einlesen einer reduzierten Eingabedatei. Im Gegensatz zum Einlesen
 * der konventionellen eLamX²-Datei basiert die Klasse auf dem SAX-Parser zum
 * Einlesen von xml-Dateien. Im Gegensatz zum DOM-Parser (genutzt zum Einlesen
 * der konventionellen eLamX²-Datei) setzt dieser ein sequentielles Einlesen der
 * xml-Eingabedatei um. Dies ist an dieser Stelle essentiell, da die Logik der
 * reduzierten Eingabedatei potentiell auf der Reihenfolge der Datenblöcke
 * beruht.
 *
 * @author Florian Dexl
 */
public class ReducedInputHandler extends DefaultHandler {

    private StringBuilder elementValue;

    private HashMap<String, DefaultMaterial> materialNames;
    private HashMap<String, LoadCaseData> loadCaseNames;
    private HashMap<DefaultMaterial, Double> materialThicknesses;
    private HashMap<DefaultMaterial, Criterion> materialCriteria;
    private HashMap<DefaultMaterial, DefaultMaterial> bucklingMaterials;
    private HashMap<String, Criterion> criterionMap;

    private String currentProcess;
    private String currentSubProcess;

    private static final String ARG_NAME = "name";

    private static final String KEY_MATERIAL = "material";
    private static final String KEY_LAMINATE = "laminate";
    private static final String KEY_LAYER = "layer";
    private static final String KEY_LOADCASE = "loadcase";
    private static final String KEY_CALCULATION = "calculation";
    private static final String KEY_BUCKLING = "buckling";

    private MaterialData materialData;
    private MaterialData materialDataBuckling;
    private LayerData layerData;

    private Laminat laminate;
    private Laminat bucklingLaminate;

    private boolean createBucklingLaminate;

    private LoadCaseData loadcase;

    private CalculationData calculation;
    private BucklingData buckling;

    @Override
    public void startDocument() throws SAXException {
        initialize();
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        String name;
        // Reset elementValue
        elementValue = new StringBuilder();

        if (currentProcess == null) {
            switch (qName.toLowerCase()) {
                case KEY_MATERIAL:
                    currentProcess = KEY_MATERIAL;
                    name = attr.getValue(ARG_NAME);
                    materialData = new MaterialData(name);
                    materialDataBuckling = null;
                    break;
                case KEY_LAMINATE:
                    name = attr.getValue(ARG_NAME);
                    createBucklingLaminate = false;
                    bucklingLaminate = null;
                    laminate = new Laminat(UUID.randomUUID().toString(), name, true);
                    laminate.setOffset(Double.parseDouble(attr.getValue("offset")));
                    laminate.setSymmetric(Boolean.parseBoolean(attr.getValue("symmetric")));
                    laminate.setWithMiddleLayer(Boolean.parseBoolean(attr.getValue("with_middle_layer")));
                    laminate.setInvertZ(Boolean.parseBoolean(attr.getValue("invert_z")));
                    break;
                case KEY_LAYER:
                    currentProcess = KEY_LAYER;
                    name = attr.getValue(ARG_NAME);
                    layerData = new LayerData(name);
                    break;
                case KEY_LOADCASE:
                    currentProcess = KEY_LOADCASE;
                    name = attr.getValue(ARG_NAME);
                    loadcase = new LoadCaseData(name);
                    break;
                case KEY_CALCULATION:
                    currentProcess = KEY_CALCULATION;
                    name = attr.getValue(ARG_NAME);
                    calculation = new CalculationData(name);
                    break;
                case KEY_BUCKLING:
                    currentProcess = KEY_BUCKLING;
                    name = attr.getValue(ARG_NAME);
                    buckling = new BucklingData(name);
                    buckling.setWholeD(Boolean.parseBoolean(attr.getValue("whole_d")));
                    buckling.setdTilde(Boolean.parseBoolean(attr.getValue("d_tilde")));
                    if (createBucklingLaminate && (bucklingLaminate == null)) {
                        bucklingLaminate = laminate.getCopy(true);
                        for (Layer l : bucklingLaminate.getOriginalLayers()) {
                            DefaultMaterial originalMaterial = ((DefaultMaterial) l.getMaterial());
                            if (bucklingMaterials.containsKey(originalMaterial)) {
                                ((DataLayer) l).setMaterial(bucklingMaterials.get(originalMaterial));
                            }
                        }
                        String bucklingLaminateName = laminate.getName().concat(" Buckling");
                        bucklingLaminate.setName(bucklingLaminateName);
                    }
                    break;
            }
        } else {
            if (currentProcess == KEY_MATERIAL) {
                switch (qName.toLowerCase()) {
                    case KEY_BUCKLING:
                        currentSubProcess = KEY_BUCKLING;
                        String bucklingMaterialName = materialData.getName().concat(" Buckling");
                        materialDataBuckling = new MaterialData(bucklingMaterialName);
                        break;
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String lName, String qName) throws SAXException {
        if (currentProcess != null) {
            switch (currentProcess) {
                case KEY_MATERIAL:
                    if (currentSubProcess == KEY_BUCKLING) {
                        processBucklingMaterial(qName.toLowerCase());
                    } else {
                        processMaterial(qName.toLowerCase());
                    }
                    break;
                case KEY_LAYER:
                    processLayer(qName.toLowerCase());
                    break;
                case KEY_LOADCASE:
                    processLoadCase(qName.toLowerCase());
                    break;
                case KEY_CALCULATION:
                    processCalculation(qName.toLowerCase());
                    break;
                case KEY_BUCKLING:
                    processBuckling(qName.toLowerCase());
                    break;
            }
        }
    }

    private void processBucklingMaterial(String qName) {
        switch (qName) {
            case "epar":
                materialDataBuckling.setEpar(Double.valueOf(elementValue.toString()));
                break;
            case "enor":
                materialDataBuckling.setEnor(Double.valueOf(elementValue.toString()));
                break;
            case "nue12":
                materialDataBuckling.setNue12(Double.valueOf(elementValue.toString()));
                break;
            case "g":
                materialDataBuckling.setG(Double.valueOf(elementValue.toString()));
                break;
            case "g13":
                materialDataBuckling.setG13(Double.valueOf(elementValue.toString()));
                break;
            case "g23":
                materialDataBuckling.setG23(Double.valueOf(elementValue.toString()));
                break;
            case KEY_BUCKLING:
                currentSubProcess = null;
                break;
        }
    }

    private void processMaterial(String qName) {
        switch (qName) {
            case "epar":
                materialData.setEpar(Double.valueOf(elementValue.toString()));
                break;
            case "enor":
                materialData.setEnor(Double.valueOf(elementValue.toString()));
                break;
            case "nue12":
                materialData.setNue12(Double.valueOf(elementValue.toString()));
                break;
            case "g":
                materialData.setG(Double.valueOf(elementValue.toString()));
                break;
            case "g13":
                materialData.setG13(Double.valueOf(elementValue.toString()));
                break;
            case "g23":
                materialData.setG23(Double.valueOf(elementValue.toString()));
                break;
            case "rho":
                materialData.setRho(Double.valueOf(elementValue.toString()));
                break;
            case "rparten":
                materialData.setRParTen(Double.valueOf(elementValue.toString()));
                break;
            case "rparcom":
                materialData.setRParCom(Double.valueOf(elementValue.toString()));
                break;
            case "rnorten":
                materialData.setRNorTen(Double.valueOf(elementValue.toString()));
                break;
            case "rnorcom":
                materialData.setRNorCom(Double.valueOf(elementValue.toString()));
                break;
            case "rshear":
                materialData.setRShear(Double.valueOf(elementValue.toString()));
                break;
            case "thickness":
                materialData.setThickness(Double.valueOf(elementValue.toString()));
                break;
            case "criterion":
                materialData.setCriterion(criterionMap.get(elementValue.toString()));
                break;
            case KEY_MATERIAL:
                DefaultMaterial material = new DefaultMaterial(UUID.randomUUID().toString(), materialData.getName(), materialData.getEpar(), materialData.getEnor(), materialData.getNue12(), materialData.getG(), materialData.getRho(), true);
                if (materialData.getG13() != null) {
                    material.setG13(materialData.getG13());
                }
                if (materialData.getG23() != null) {
                    material.setG23(materialData.getG23());
                }
                if (materialData.getRParTen() != null) {
                    material.setRParTen(materialData.getRParTen());
                }
                if (materialData.getRParCom() != null) {
                    material.setRParCom(materialData.getRParCom());
                }
                if (materialData.getRNorTen() != null) {
                    material.setRNorTen(materialData.getRNorTen());
                }
                if (materialData.getRNorCom() != null) {
                    material.setRNorCom(materialData.getRNorCom());
                }
                if (materialData.getRShear() != null) {
                    material.setRShear(materialData.getRShear());
                }
                materialNames.put(materialData.getName(), material);
                materialThicknesses.put(material, materialData.getThickness());
                materialCriteria.put(material, materialData.getCriterion());

                if (materialDataBuckling != null) {
                    DefaultMaterial bMaterial = new DefaultMaterial(UUID.randomUUID().toString(), "", 0.0, 0.0, 0.0, 0.0, 0.0, true);
                    bMaterial = material.copyValues(bMaterial);
                    bMaterial.setName(materialDataBuckling.getName());
                    if (materialDataBuckling.getEpar() != null) {
                        bMaterial.setEpar(materialDataBuckling.getEpar());
                    }
                    if (materialDataBuckling.getEnor() != null) {
                        bMaterial.setEnor(materialDataBuckling.getEnor());
                    }
                    if (materialDataBuckling.getNue12() != null) {
                        bMaterial.setNue12(materialDataBuckling.getNue12());
                    }
                    if (materialDataBuckling.getG() != null) {
                        bMaterial.setG(materialDataBuckling.getG());
                    }
                    if (materialDataBuckling.getG13() != null) {
                        bMaterial.setG13(materialDataBuckling.getG13());
                    }
                    if (materialDataBuckling.getG23() != null) {
                        bMaterial.setG23(materialDataBuckling.getG23());
                    }
                    bucklingMaterials.put(material, bMaterial);
                    materialDataBuckling = null;
                }
                currentProcess = null;
                break;
        }
    }

    private void processLayer(String qName) {
        switch (qName) {
            case "thickness":
                layerData.setThickness(Double.valueOf(elementValue.toString()));
                break;
            case "angle":
                layerData.setAngle(Double.valueOf(elementValue.toString()));
                break;
            case "material":
                layerData.setMaterialName(elementValue.toString());
                break;
            case "criterion":
                layerData.setCriterion(criterionMap.get(elementValue.toString()));
                break;
            case KEY_LAYER:
                DefaultMaterial material = materialNames.get(layerData.getMaterialName());
                if (bucklingMaterials.containsKey(material)) {
                    createBucklingLaminate = true;
                }
                double thickness;
                if (layerData.getThickness() != null) {
                    thickness = layerData.getThickness();
                } else {
                    thickness = materialThicknesses.get(material);
                }
                Criterion criterion;
                if (layerData.getCriterion() != null) {
                    criterion = layerData.getCriterion();
                } else {
                    criterion = materialCriteria.get(material);
                }
                DataLayer layer = new DataLayer(UUID.randomUUID().toString(), layerData.getName(), material, layerData.getAngle(), thickness);
                if (criterion != null) {
                    layer.setCriterion(criterion);
                }
                laminate.addLayer(layer);
                currentProcess = null;
                break;
        }
    }

    private void processLoadCase(String qName) {
        switch (qName) {
            case "n_x":
                loadcase.setN_x(Double.valueOf(elementValue.toString()));
                break;
            case "n_y":
                loadcase.setN_y(Double.valueOf(elementValue.toString()));
                break;
            case "n_xy":
                loadcase.setN_xy(Double.valueOf(elementValue.toString()));
                break;
            case "m_x":
                loadcase.setM_x(Double.valueOf(elementValue.toString()));
                break;
            case "m_y":
                loadcase.setM_y(Double.valueOf(elementValue.toString()));
                break;
            case "m_xy":
                loadcase.setM_xy(Double.valueOf(elementValue.toString()));
                break;
            case "deltat":
                loadcase.setDelta_t(Double.valueOf(elementValue.toString()));
                break;
            case "deltah":
                loadcase.setDelta_h(Double.valueOf(elementValue.toString()));
                break;
            case "ul_factor":
                loadcase.setUl_factor(Double.valueOf(elementValue.toString()));
                break;
            case KEY_LOADCASE:
                loadCaseNames.put(loadcase.getName(), loadcase);
                currentProcess = null;
                break;
        }
    }

    private void processCalculation(String qName) {
        switch (qName) {
            case "loadcase":
                calculation.setLoadcase(elementValue.toString());
                break;
            case KEY_CALCULATION:
                CLT_Input inputData = new CLT_Input();
                LoadCaseData lc = loadCaseNames.get(calculation.getLoadcase());
                inputData.getLoad().setN_x(lc.getN_x());
                inputData.getLoad().setN_y(lc.getN_y());
                inputData.getLoad().setN_xy(lc.getN_xy());
                inputData.getLoad().setM_x(lc.getM_x());
                inputData.getLoad().setM_y(lc.getM_y());
                inputData.getLoad().setM_xy(lc.getM_xy());
                inputData.getLoad().setDeltaH(lc.getDelta_h());
                inputData.getLoad().setDeltaT(lc.getDelta_t());
                boolean[] useStrains = {false, false, false, false, false, false};
                inputData.setUseStrains(useStrains);
                CalculationModuleData calcModuleData = new CalculationModuleData(laminate, inputData);
                calcModuleData.setName(calculation.getName());
                laminate.getLookup().add(calcModuleData);
                currentProcess = null;
                break;
        }
    }

    private void processBuckling(String qName) {
        switch (qName) {
            case "loadcase":
                buckling.setLoadcase(elementValue.toString());
                break;
            case "bcx":
                buckling.setBcx(Integer.valueOf(elementValue.toString()));
                break;
            case "bcy":
                buckling.setBcy(Integer.valueOf(elementValue.toString()));
                break;
            case "m":
                buckling.setM(Integer.valueOf(elementValue.toString()));
                break;
            case "n":
                buckling.setN(Integer.valueOf(elementValue.toString()));
                break;
            case "length":
                buckling.setLength(Double.valueOf(elementValue.toString()));
                break;
            case "width":
                buckling.setWidth(Double.valueOf(elementValue.toString()));
                break;
            case KEY_BUCKLING:
                LoadCaseData lc = loadCaseNames.get(buckling.getLoadcase());
                Laminat lam;
                if (bucklingLaminate != null) {
                    lam = bucklingLaminate;
                } else {
                    lam = laminate;
                }
                BucklingInput inputData = new BucklingInput(buckling.getLength(), buckling.getWidth(), lc.getN_x(), lc.getN_y(), lc.getN_xy(), buckling.getWholeD(), buckling.getdTilde(), buckling.getBcx(), buckling.getBcy(), buckling.getM(), buckling.getN());
                BucklingModuleData buckModuleData = new BucklingModuleData(lam, inputData);
                buckModuleData.setName(buckling.getName());
                lam.getLookup().add(buckModuleData);
                currentProcess = null;
                break;
        }
    }

    private void initialize() {
        materialNames = new HashMap<>();
        loadCaseNames = new HashMap<>();
        materialThicknesses = new HashMap<>();
        materialCriteria = new HashMap<>();
        bucklingMaterials = new HashMap<>();
        criterionMap = new HashMap<>();

        currentProcess = null;
        currentSubProcess = null;

        criterionMap.clear();
        Lookup critLookup = Lookups.forPath("elamx/failurecriteria");
        for (Criterion c : critLookup.lookupAll(Criterion.class)) {
            criterionMap.put(c.getClass().getName(), c);
        }
    }
}
