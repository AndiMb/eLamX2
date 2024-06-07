/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.elamx.reducedinput;

import de.elamx.clt.*;
import de.elamx.clt.calculation.CalculationModuleData;
import de.elamx.clt.plate.BucklingInput;
import de.elamx.clt.plateui.buckling.BucklingModuleData;
import de.elamx.laminate.DataLayer;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.failure.Criterion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Dient dem Einlesen einer reduzierten Eingabedatei.
 * Im Gegensatz zum Einlesen der konventionellen eLamX²-Datei basiert die Klasse
 * auf dem SAX-Parser zum Einlesen von xml-Dateien. Im Gegensatz zum DOM-Parser
 * (genutzt zum Einlesen der konventionellen eLamX²-Datei) setzt dieser ein
 * sequentielles Einlesen der xml-Eingabedatei um. Dies ist an dieser Stelle
 * essentiell, da die Logik der reduzierten Eingabedatei potentiell auf der
 * Reihenfolge der Datenblöcke beruht.
 * @author Florian Dexl
 */
public class ReducedInputHandler extends DefaultHandler {

    private StringBuilder elementValue;

    private HashMap<String, DefaultMaterial> materialNames = new HashMap<>();
    private HashMap<String, LoadCaseData> loadCaseNames = new HashMap<>();
    private HashMap<DefaultMaterial, Double> materialThicknesses = new HashMap<>();
    private HashMap<DefaultMaterial, Criterion> materialCriteria = new HashMap<>();
    private HashMap<DefaultMaterial, DefaultMaterial> bucklingMaterials = new HashMap<>();
    private HashMap<String, Criterion> criterionMap = new HashMap<>();

    private String currentProcess = null;
    private String currentSubProcess = null;

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
        criterionMap.clear();
        Lookup critLookup = Lookups.forPath("elamx/failurecriteria");
        for (Criterion c : critLookup.lookupAll(Criterion.class)) {
            criterionMap.put(c.getClass().getName(), c);
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
                        for (Layer l: bucklingLaminate.getOriginalLayers()) {
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
                        name = attr.getValue(ARG_NAME);
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
                System.out.println("NEW CALCULATION WITH LOADCASE: " + calculation.getLoadcase());
                CLT_Input inputData = new CLT_Input();
                LoadCaseData lc = loadCaseNames.get(calculation.getLoadcase());
                System.out.println("LOADCASE FOUND: " + lc.getName());
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
                System.out.println("NEW BUCKLING WITH LOADCASE: " + buckling.getLoadcase());
                LoadCaseData lc = loadCaseNames.get(buckling.getLoadcase());
                Laminat lam;
                System.out.println("LOADCASE FOUND: " + lc.getName());
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

    @Override
    public void endDocument() {
        System.out.println("READ DOCUMENT FINISHED");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    private class MaterialData {

        private String name;

        private Double thickness = null;
        private Criterion Criterion = null;

        private Double Epar = null;
        private Double Enor = null;
        private Double nue12 = null;
        private Double G = null;
        private Double G13 = null;
        private Double G23 = null;
        private Double rho = null;

        private Double RParTen = null;
        private Double RParCom = null;
        private Double RNorTen = null;
        private Double RNorCom = null;
        private Double RShear = null;

        private Double FMCmuesp = null;

        public MaterialData() {
        }

        public MaterialData(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getThickness() {
            return thickness;
        }

        public void setThickness(Double thickness) {
            this.thickness = thickness;
        }

        public Double getEpar() {
            return Epar;
        }

        public void setEpar(Double Epar) {
            this.Epar = Epar;
        }

        public Double getEnor() {
            return Enor;
        }

        public void setEnor(Double Enor) {
            this.Enor = Enor;
        }

        public Double getNue12() {
            return nue12;
        }

        public void setNue12(Double nue12) {
            this.nue12 = nue12;
        }

        public Double getG() {
            return G;
        }

        public void setG(Double G) {
            this.G = G;
        }

        public Double getRho() {
            return rho;
        }

        public void setRho(Double rho) {
            this.rho = rho;
        }

        public Double getRParTen() {
            return RParTen;
        }

        public void setRParTen(Double RParTen) {
            this.RParTen = RParTen;
        }

        public Double getRParCom() {
            return RParCom;
        }

        public void setRParCom(Double RParCom) {
            this.RParCom = RParCom;
        }

        public Double getRNorTen() {
            return RNorTen;
        }

        public void setRNorTen(Double RNorTen) {
            this.RNorTen = RNorTen;
        }

        public Double getRNorCom() {
            return RNorCom;
        }

        public void setRNorCom(Double RNorCom) {
            this.RNorCom = RNorCom;
        }

        public Double getRShear() {
            return RShear;
        }

        public void setRShear(Double RShear) {
            this.RShear = RShear;
        }

        public Double getFMCmuesp() {
            return FMCmuesp;
        }

        public void setFMCmuesp(Double FMCmuesp) {
            this.FMCmuesp = FMCmuesp;
        }

        public Criterion getCriterion() {
            return Criterion;
        }

        public void setCriterion(Criterion Criterion) {
            this.Criterion = Criterion;
        }

        public Double getG13() {
            return G13;
        }

        public void setG13(Double G13) {
            this.G13 = G13;
        }

        public Double getG23() {
            return G23;
        }

        public void setG23(Double G23) {
            this.G23 = G23;
        }

        
    }

    private class LaminateData {

        private String name;

        private Double offset;

        private boolean symmetric;
        private boolean withMiddleLayer;

        private final ArrayList<LayerData> layers = new ArrayList<>();

        public LaminateData() {
        }

        public LaminateData(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getOffset() {
            return offset;
        }

        public void setOffset(Double offset) {
            this.offset = offset;
        }

        public boolean isSymmetric() {
            return symmetric;
        }

        public void setSymmetric(boolean symmetric) {
            this.symmetric = symmetric;
        }

        public boolean isWithMiddleLayer() {
            return withMiddleLayer;
        }

        public void setWithMiddleLayer(boolean withMiddleLayer) {
            this.withMiddleLayer = withMiddleLayer;
        }

        public void addLayer(LayerData layerData) {
            this.layers.add(layerData);
        }

        public ArrayList<LayerData> getLayers() {
            return layers;
        }
    }

    private class LayerData {

        private String name;

        private Double thickness;
        private Double angle;
        private String materialName;

        private Criterion criterion = null;

        public LayerData() {
        }

        public LayerData(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getThickness() {
            return thickness;
        }

        public void setThickness(Double thickness) {
            this.thickness = thickness;
        }

        public Double getAngle() {
            return angle;
        }

        public void setAngle(Double angle) {
            this.angle = angle;
        }

        public String getMaterialName() {
            return materialName;
        }

        public void setMaterialName(String materialName) {
            this.materialName = materialName;
        }

        public Criterion getCriterion() {
            return criterion;
        }

        public void setCriterion(Criterion criterion) {
            this.criterion = criterion;
        }

    }

    private class CalculationData {

        private String name;

        private String loadcase;

        public CalculationData() {
        }

        public CalculationData(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLoadcase() {
            return loadcase;
        }

        public void setLoadcase(String loadcase) {
            this.loadcase = loadcase;
        }
    }

    private class BucklingData {

        private String name;

        private String loadcase = null;

        private Double length;
        private Double width;
        private Integer bcx;
        private Integer bcy;
        private Integer m;
        private Integer n;
        private Boolean wholeD;
        private Boolean dTilde;

        public BucklingData() {
        }

        public BucklingData(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLoadcase() {
            return loadcase;
        }

        public void setLoadcase(String loadcase) {
            this.loadcase = loadcase;
        }

        public Double getLength() {
            return length;
        }

        public void setLength(Double length) {
            this.length = length;
        }

        public Double getWidth() {
            return width;
        }

        public void setWidth(Double width) {
            this.width = width;
        }

        public Integer getBcx() {
            return bcx;
        }

        public void setBcx(Integer bcx) {
            this.bcx = bcx;
        }

        public Integer getBcy() {
            return bcy;
        }

        public void setBcy(Integer bcy) {
            this.bcy = bcy;
        }

        public Integer getM() {
            return m;
        }

        public void setM(Integer m) {
            this.m = m;
        }

        public Integer getN() {
            return n;
        }

        public void setN(Integer n) {
            this.n = n;
        }

        public Boolean getWholeD() {
            return wholeD;
        }

        public void setWholeD(Boolean wholeD) {
            this.wholeD = wholeD;
        }

        public Boolean getdTilde() {
            return dTilde;
        }

        public void setdTilde(Boolean dTilde) {
            this.dTilde = dTilde;
        }
    }
    
    private class LoadCaseData {

        private String name;

        private Double n_x;
        private Double n_y;
        private Double n_xy;
        private Double m_x;
        private Double m_y;
        private Double m_xy;
        private Double delta_t;
        private Double delta_h;
        private Double ul_factor;

        public LoadCaseData() {
        }

        public LoadCaseData(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getN_x() {
            return n_x;
        }

        public void setN_x(Double n_x) {
            this.n_x = n_x;
        }

        public Double getN_y() {
            return n_y;
        }

        public void setN_y(Double n_y) {
            this.n_y = n_y;
        }

        public Double getN_xy() {
            return n_xy;
        }

        public void setN_xy(Double n_xy) {
            this.n_xy = n_xy;
        }

        public Double getM_x() {
            return m_x;
        }

        public void setM_x(Double m_x) {
            this.m_x = m_x;
        }

        public Double getM_y() {
            return m_y;
        }

        public void setM_y(Double m_y) {
            this.m_y = m_y;
        }

        public Double getM_xy() {
            return m_xy;
        }

        public void setM_xy(Double m_xy) {
            this.m_xy = m_xy;
        }

        public Double getDelta_t() {
            return delta_t;
        }

        public void setDelta_t(Double delta_t) {
            this.delta_t = delta_t;
        }

        public Double getDelta_h() {
            return delta_h;
        }

        public void setDelta_h(Double delta_h) {
            this.delta_h = delta_h;
        }

        public Double getUl_factor() {
            return ul_factor;
        }

        public void setUl_factor(Double ul_factor) {
            this.ul_factor = ul_factor;
        }

        
    }
}
