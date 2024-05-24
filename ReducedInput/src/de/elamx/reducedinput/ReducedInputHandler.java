/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.elamx.reducedinput;

import de.elamx.*;
import de.elamx.clt.*;
import de.elamx.clt.calculation.CalculationModuleData;
import de.elamx.laminate.DataLayer;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.failure.Criterion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Florian Dexl
 */
public class ReducedInputHandler extends DefaultHandler {

    private StringBuilder elementValue;

    private eLamXLookup lookup;

    private HashMap<String, DefaultMaterial> materialNames = new HashMap<>();
    private HashMap<DefaultMaterial, Double> materialThicknesses = new HashMap<>();
    private HashMap<DefaultMaterial, Criterion> materialCriteria = new HashMap<>();
    private HashMap<String, Criterion> criterionMap = new HashMap<>();

    private List<LaminateData> laminateData;

    private String currentProcess;

    private static final String ARG_NAME = "name";

    private static final String KEY_MATERIAL = "material";
    private static final String KEY_LAMINATE = "laminate";
    private static final String KEY_LAYER = "layer";
    private static final String KEY_CALCULATION = "calculation";

    private MaterialData materialData;
    private LayerData layerData;

    private Laminat laminate;

    private CalculationData calculation;

    @Override
    public void startDocument() throws SAXException {
        criterionMap.clear();
        Lookup critLookup = Lookups.forPath("elamx/failurecriteria");
        for (Criterion c : critLookup.lookupAll(Criterion.class)) {
            criterionMap.put(c.getClass().getName(), c);
        }

        lookup = eLamXLookup.getDefault();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        String name;
        // Reset elementValue
        elementValue = new StringBuilder();

        switch (qName.toLowerCase()) {
            case "materials":
                currentProcess = KEY_MATERIAL;
                break;
            case KEY_MATERIAL:
                name = attr.getValue(ARG_NAME);
                materialData = new MaterialData(name);
                break;
            case KEY_LAMINATE:
                currentProcess = KEY_LAMINATE;
                name = attr.getValue(ARG_NAME);
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
            case KEY_CALCULATION:
                currentProcess = KEY_CALCULATION;
                name = attr.getValue(ARG_NAME);
                calculation = new CalculationData(name);
                break;
        }
    }

    @Override
    public void endElement(String uri, String lName, String qName) throws SAXException {
        switch (currentProcess) {
            case KEY_MATERIAL:
                processMaterial(qName.toLowerCase());
                break;
            case KEY_LAYER:
                processLayer(qName.toLowerCase());
                break;
            case KEY_CALCULATION:
                processCalculation(qName.toLowerCase());
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
                break;
        }
    }

    private void processCalculation(String qName) {
        switch (qName) {
            case "n_x":
                calculation.setN_x(Double.valueOf(elementValue.toString()));
                break;
            case "n_y":
                calculation.setN_y(Double.valueOf(elementValue.toString()));
                break;
            case "n_xy":
                calculation.setN_xy(Double.valueOf(elementValue.toString()));
                break;
            case "m_x":
                calculation.setM_x(Double.valueOf(elementValue.toString()));
                break;
            case "m_y":
                calculation.setM_y(Double.valueOf(elementValue.toString()));
                break;
            case "m_xy":
                calculation.setM_xy(Double.valueOf(elementValue.toString()));
                break;
            case "deltat":
                calculation.setDelta_t(Double.valueOf(elementValue.toString()));
                break;
            case "deltah":
                calculation.setDelta_h(Double.valueOf(elementValue.toString()));
                break;
            case KEY_CALCULATION:
                CLT_Input inputData = new CLT_Input();
                inputData.getLoad().setN_x(calculation.getN_x());
                inputData.getLoad().setN_y(calculation.getN_y());
                inputData.getLoad().setN_xy(calculation.getN_xy());
                inputData.getLoad().setM_x(calculation.getM_x());
                inputData.getLoad().setM_y(calculation.getM_y());
                inputData.getLoad().setM_xy(calculation.getM_xy());
                inputData.getLoad().setDeltaH(calculation.getDelta_h());
                inputData.getLoad().setDeltaT(calculation.getDelta_t());
                boolean[] useStrains = {false, false, false, false, false, false};
                inputData.setUseStrains(useStrains);
                CalculationModuleData calcModuleData = new CalculationModuleData(laminate, inputData);
                calcModuleData.setName(calculation.getName());
                laminate.getLookup().add(calcModuleData);
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

        private Double n_x;
        private Double n_y;
        private Double n_xy;
        private Double m_x;
        private Double m_y;
        private Double m_xy;
        private Double delta_t;
        private Double delta_h;

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

    }
}
