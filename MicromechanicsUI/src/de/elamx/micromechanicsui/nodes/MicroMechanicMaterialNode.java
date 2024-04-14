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
package de.elamx.micromechanicsui.nodes;

import de.elamx.core.GlobalProperties;
import de.elamx.core.propertyeditor.DensityPropertyEditorSupport;
import de.elamx.core.propertyeditor.DoublePropertyEditorSupport;
import de.elamx.core.propertyeditor.FiberVolumeFractionPropertyEditorSupport;
import de.elamx.core.propertyeditor.HygrothermCoeffPropertyEditorSupport;
import de.elamx.core.propertyeditor.PoissonRatioPropertyEditorSupport;
import de.elamx.core.propertyeditor.YieldStressPropertyEditorSupport;
import de.elamx.core.propertyeditor.YoungsModulusPropertyEditorSupport;
import de.elamx.laminate.Material;
import de.elamx.micromechanics.MicroMechanicMaterial;
import de.elamx.micromechanics.models.ManualInputDummyModel;
import de.elamx.micromechanicsui.nodes.properties.ENorMicroMechModelProperty;
import de.elamx.micromechanicsui.nodes.properties.EParMicroMechModelProperty;
import de.elamx.micromechanicsui.nodes.properties.FibreProperty;
import de.elamx.micromechanicsui.nodes.properties.GMicroMechModelProperty;
import de.elamx.micromechanicsui.nodes.properties.MatrixProperty;
import de.elamx.micromechanicsui.nodes.properties.Nue12MicroMechModelProperty;
import de.elamx.micromechanicsui.nodes.properties.RhoMicroMechModelProperty;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 * Knoten für ein MicroMechanicMaterialNode.
 * 
 * @author Andreas Hauffe
 */
public class MicroMechanicMaterialNode extends AbstractNode implements PropertyChangeListener {
    
    public static final String PROP_SET_GENERAL = "GeneralProperties";
    public static final String PROP_SET_STIFFNESS = "StiffnessProperties";
    public static final String PROP_SET_HYGROTHERMAL = "HygrothermalProperties";
    public static final String PROP_SET_STRENGTH = "StrengthProperties";
    public static final String PROP_SET_ADDITIONAL = "AdditionalProperties";
    
    private static final ResourceBundle bundle = NbBundle.getBundle(MicroMechanicMaterialNode.class);
    
    private final MicroMechanicMaterial material;

    @SuppressWarnings("this-escape")
    public MicroMechanicMaterialNode(MicroMechanicMaterial material) {
        super(Children.LEAF, Lookups.singleton(material));
        this.material = material;
        material.addPropertyChangeListener(WeakListeners.propertyChange(this, material));
    }

    @Override
    public String getDisplayName() {
        return material.getName();
    }

    @Override
    protected Sheet createSheet() {
        
        Sheet sheet = Sheet.createDefault();
        
        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName(PROP_SET_GENERAL);
        generalProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.GENERALPROPERTIES"));

        Sheet.Set stiffProp = Sheet.createPropertiesSet();
        stiffProp.setName(PROP_SET_STIFFNESS);
        stiffProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.STIFFNESSPROPERTIES"));

        Sheet.Set hygtProp = Sheet.createPropertiesSet();
        hygtProp.setName(PROP_SET_HYGROTHERMAL);
        hygtProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.HYGROTHERMALPROPERTIES"));

        Sheet.Set strengthProp = Sheet.createPropertiesSet();
        strengthProp.setName(PROP_SET_STRENGTH);
        strengthProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.STRENGTHPROPERTIES"));

        Sheet.Set additionalProp = Sheet.createPropertiesSet();
        additionalProp.setName(PROP_SET_ADDITIONAL);
        additionalProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.ADDITIONALPROPERTIES"));
        
        try {
            myPropRef<String> nameProp  = new myPropRef<>(material, String.class, MicroMechanicMaterial.PROP_NAME);
            nameProp.setName(MicroMechanicMaterial.PROP_NAME);
            nameProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.NAME"));
            nameProp.setShortDescription(bundle.getString("MicroMechanicMaterialNode.NAME.description"));
            generalProp.put(nameProp);
            
            myPropRef<Double>phiProp  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_PHI);
            phiProp.setName(MicroMechanicMaterial.PROP_PHI);
            phiProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.PHI"));
            phiProp.setShortDescription(bundle.getString("MicroMechanicMaterialNode.PHI.description"));
            phiProp.setHtmlName(bundle.getString("MicroMechanicMaterialNode.PHI.html"));
            phiProp.setPropertyEditorClass(FiberVolumeFractionPropertyEditorSupport.class);
            generalProp.put(phiProp);
            
            generalProp.put(new FibreProperty(material));
            generalProp.put(new MatrixProperty(material));
            
            generalProp.put(new RhoMicroMechModelProperty(material));
            
            myPropRef<Double>rhoProp  = new RhoPropRef<Double>(material, double.class, MicroMechanicMaterial.PROP_RHO){
                @Override
                public String getShortDescription() {
                    return material.getRhoModel().getRhoHTMLDescription();
                }
            };
            rhoProp.setName(MicroMechanicMaterial.PROP_RHO);
            rhoProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.DENSITY"));
            rhoProp.setShortDescription(bundle.getString("MicroMechanicMaterialNode.DENSITY.description"));
            rhoProp.setHtmlName(bundle.getString("MicroMechanicMaterialNode.DENSITY.html"));
            rhoProp.setPropertyEditorClass(DensityPropertyEditorSupport.class);
            generalProp.put(rhoProp);
            
            stiffProp.put(new EParMicroMechModelProperty(material));
            
            myPropRef<Double>e1Prop  = new EParPropRef<Double>(material, double.class, MicroMechanicMaterial.PROP_EPAR){
                @Override
                public String getShortDescription() {
                    return material.getEParModel().getE11HTMLDescription();
                }
            };
            e1Prop.setName(MicroMechanicMaterial.PROP_EPAR);
            e1Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.EPARALLEL"));
            e1Prop.setShortDescription(PROP_NAME);
            e1Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.EPARALLEL.html"));
            e1Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(e1Prop);
            
            stiffProp.put(new ENorMicroMechModelProperty(material));
            
            myPropRef<Double>e2Prop  = new ENorPropRef<Double>(material, double.class, MicroMechanicMaterial.PROP_ENOR){
                @Override
                public String getShortDescription() {
                    return material.getENorModel().getE22HTMLDescription();
                }
            };
            e2Prop.setName(MicroMechanicMaterial.PROP_ENOR);
            e2Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.EPERPENDICULAR"));
            e2Prop.setShortDescription(bundle.getString("MicroMechanicMaterialNode.EPERPENDICULAR.description"));
            e2Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.EPERPENDICULAR.html"));
            e2Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(e2Prop);
            
            stiffProp.put(new Nue12MicroMechModelProperty(material));
            
            myPropRef<Double>nue12Prop  = new Nue12PropRef<Double>(material, double.class, MicroMechanicMaterial.PROP_NUE12){
                @Override
                public String getShortDescription() {
                    return material.getNue12Model().getNue12HTMLDescription();
                }
            };
            nue12Prop.setName(MicroMechanicMaterial.PROP_NUE12);
            nue12Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.POISSONRATIO"));
            nue12Prop.setShortDescription(bundle.getString("MicroMechanicMaterialNode.POISSONRATIO.description"));
            nue12Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.POISSONRATIO.html"));
            nue12Prop.setPropertyEditorClass(PoissonRatioPropertyEditorSupport.class);
            stiffProp.put(nue12Prop);
            
            stiffProp.put(new GMicroMechModelProperty(material));
            
            myPropRef<Double>gProp  = new GPropRef<Double>(material, double.class, MicroMechanicMaterial.PROP_G){
                @Override
                public String getShortDescription() {
                    return material.getGModel().getG12HTMLDescription();
                }
            };
            gProp.setName(MicroMechanicMaterial.PROP_G);
            gProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS"));
            //gProp.setShortDescription(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS.description"));
            gProp.setHtmlName(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS.html"));
            gProp.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(gProp);
            
            if (GlobalProperties.getDefault().isShowTransShear()){
                myPropRef<Double> g13Prop = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_G13);
                g13Prop.setName(MicroMechanicMaterial.PROP_G13);
                g13Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS13"));
                g13Prop.setShortDescription(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS13.description"));
                g13Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS13.html"));
                g13Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
                stiffProp.put(g13Prop);
                
                myPropRef<Double> g23Prop = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_G23);
                g23Prop.setName(MicroMechanicMaterial.PROP_G23);
                g23Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS23"));
                g23Prop.setShortDescription(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS23.description"));
                g23Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.SHEARMODULUS23.html"));
                g23Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
                stiffProp.put(g23Prop);
            }
            
            myPropRef<Double>alpha1Prop  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_ALPHATPAR);
            alpha1Prop.setName(MicroMechanicMaterial.PROP_ALPHATPAR);
            alpha1Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.ALPHAPARALLEL"));
            alpha1Prop.setShortDescription(bundle.getString("MicroMechanicMaterialNode.ALPHAPARALLEL.description"));
            alpha1Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.ALPHAPARALLEL.html"));
            alpha1Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(alpha1Prop);
            
            myPropRef<Double>alpha2Prop  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_ALPHATNOR);
            alpha2Prop.setName(MicroMechanicMaterial.PROP_ALPHATNOR);
            alpha2Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.ALPHAPERPENDICULAR"));
            alpha2Prop.setShortDescription(bundle.getString("MicroMechanicMaterialNode.ALPHAPERPENDICULAR.description"));
            alpha2Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.ALPHAPERPENDICULAR.html"));
            alpha2Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(alpha2Prop);
                        
            myPropRef<Double>beta1Prop  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_BETAPAR);
            beta1Prop.setName(MicroMechanicMaterial.PROP_BETAPAR);
            beta1Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.BETAPARALLEL"));
            beta1Prop.setShortDescription(bundle.getString("MicroMechanicMaterialNode.BETAPARALLEL.description"));
            beta1Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.BETAPARALLEL.html"));
            beta1Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(beta1Prop);
            
            myPropRef<Double>beta2Prop  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_BETANOR);
            beta2Prop.setName(MicroMechanicMaterial.PROP_BETANOR);
            beta2Prop.setDisplayName(bundle.getString("MicroMechanicMaterialNode.BETAPERPENDICULAR"));
            beta2Prop.setShortDescription(bundle.getString("MicroMechanicMaterialNode.BETAPERPENDICULAR.description"));
            beta2Prop.setHtmlName(bundle.getString("MicroMechanicMaterialNode.BETAPERPENDICULAR.html"));
            beta2Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(beta2Prop);
            
            myPropRef<Double>rpartenProp  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_RPARTEN);
            rpartenProp.setName(MicroMechanicMaterial.PROP_RPARTEN);
            rpartenProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.RPARTEN"));
            rpartenProp.setShortDescription(bundle.getString("MicroMechanicMaterialNode.RPARTEN.description"));
            rpartenProp.setHtmlName(bundle.getString("MicroMechanicMaterialNode.RPARTEN.html"));
            rpartenProp.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rpartenProp);
            
            myPropRef<Double>rparcomProp  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_RPARCOM);
            rparcomProp.setName(MicroMechanicMaterial.PROP_RPARCOM);
            rparcomProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.RPARCOM"));
            rparcomProp.setShortDescription(bundle.getString("MicroMechanicMaterialNode.RPARCOM.description"));
            rparcomProp.setHtmlName(bundle.getString("MicroMechanicMaterialNode.RPARCOM.html"));
            rparcomProp.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rparcomProp);
            
            myPropRef<Double>rnortenProp  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_RNORTEN);
            rnortenProp.setName(MicroMechanicMaterial.PROP_RNORTEN);
            rnortenProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.RNORTEN"));
            rnortenProp.setShortDescription(bundle.getString("MicroMechanicMaterialNode.RNORTEN.description"));
            rnortenProp.setHtmlName(bundle.getString("MicroMechanicMaterialNode.RNORTEN.html"));
            rnortenProp.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rnortenProp);
            
            myPropRef<Double>rnorcomProp  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_RNORCOM);
            rnorcomProp.setName(MicroMechanicMaterial.PROP_RNORCOM);
            rnorcomProp.setDisplayName(bundle.getString("MicroMechanicMaterialNode.RNORCOM"));
            rnorcomProp.setShortDescription(bundle.getString("MicroMechanicMaterialNode.RNORCOM.description"));
            rnorcomProp.setHtmlName(bundle.getString("MicroMechanicMaterialNode.RNORCOM.html"));
            rnorcomProp.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rnorcomProp);
            
            myPropRef<Double>rshear  = new myPropRef<>(material, double.class, MicroMechanicMaterial.PROP_RSHEAR);
            rshear.setName(MicroMechanicMaterial.PROP_RSHEAR);
            rshear.setDisplayName(bundle.getString("MicroMechanicMaterialNode.RSHEAR"));
            rshear.setShortDescription(bundle.getString("MicroMechanicMaterialNode.RSHEAR.description"));
            rshear.setHtmlName(bundle.getString("MicroMechanicMaterialNode.RSHEAR.html"));
            rshear.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rshear);
            
            for (String key : material.getAdditionalValueKeySet()){
                additionalProp.put(new MaterialProperty(material, key));
            }
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        sheet.put(generalProp);
        sheet.put(stiffProp);
        sheet.put(hygtProp);
        sheet.put(strengthProp);
        sheet.put(additionalProp);
        
        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Material.PROP_NAME)) {
            this.fireDisplayNameChange((String)evt.getOldValue(), (String)evt.getNewValue());
            return;
        }
        this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
    
    @Override
    public Action[] getActions(boolean context) {
        List<Action> laminateActions = new ArrayList<>();
        laminateActions.addAll(Utilities.actionsForPath("eLamXActions/Material"));
        return laminateActions.toArray(new Action[laminateActions.size()]);
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage ("de/elamx/micromechanicsui/resources/mmmaterial.png");
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("Material", "de.elamx.fileview.PropertiesAction");
    }

    public class MaterialProperty extends PropertySupport.ReadWrite<Double> {

        Material material;
        String   propName;
        boolean  showHtml = Boolean.getBoolean("nb.useSwingHtmlRendering");
        
        boolean canWrite = true;

        public MaterialProperty(Material material, String propName) {
            //super(NbBundle.getMessage(LayerNode.class, "LayerNode.Material"), Material.class, NbBundle.getMessage(LayerNode.class, "LayerNode.Material"), NbBundle.getMessage(LayerNode.class, "LayerNode.Material.description"));
            super(propName, double.class, material.getAdditionalValueDisplayName(propName), material.getAdditionalValueDescription(propName));
            this.material = material;
            this.propName = propName;
        }

        @Override
        public Double getValue() throws IllegalAccessException, InvocationTargetException {
            return material.getAdditionalValue(propName);
        }

        @Override
        public void setValue(Double newValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            double newVal = newValue;
            double maxVal = material.getAdditionalValueMaxValue(propName);
            double minVal = material.getAdditionalValueMinValue(propName);
            
            if (newVal > maxVal){
                newVal = maxVal;
            }else if (newVal < minVal){
                newVal = minVal;
            }
            material.putAdditionalValue(propName, newVal);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return new DoublePropertyEditorSupport();
        }

        @Override
        public String getDisplayName() {
            return showHtml ? material.getAdditionalValueHtmlName(propName) : super.getDisplayName();
        }
        @Override
        public String getHtmlDisplayName() {
            return showHtml ? material.getAdditionalValueHtmlName(propName) : null;
        }
        
        public void setCanWrite(boolean write){
            this.canWrite = write;
        }
        
        @Override
        public boolean canWrite(){
            return canWrite & super.canWrite();
        }
    }
    
    public class myPropRef<T> extends PropertySupport.Reflection<T>{
        
        String   htmlName = null;
        
        boolean  showHtml = Boolean.getBoolean("nb.useSwingHtmlRendering");
        
        boolean canWrite = true;

        public myPropRef(Object instance, Class<T> valueType, Method getter, Method setter) {
            super(instance, valueType, getter, setter);
        }

        public myPropRef(Object instance, Class<T> valueType, String getter, String setter) throws NoSuchMethodException {
            super(instance, valueType, getter, setter);
        }

        public myPropRef(Object instance, Class<T> valueType, String property) throws NoSuchMethodException {
            super(instance, valueType, property);
        }

        @Override
        public String getDisplayName() {
            return showHtml && htmlName != null ? htmlName : super.getDisplayName();
        }
        
        @Override
        public String getHtmlDisplayName() {
            return showHtml ? htmlName : null;
        }

        public void setHtmlName(String htmlName) {
            this.htmlName = htmlName;
        }
        
        public void setCanWrite(boolean write){
            this.canWrite = write;
        }
        
        @Override
        public boolean canWrite(){
            return canWrite & super.canWrite();
        }
    }
    
    public class RhoPropRef<T> extends myPropRef<T>{
        
        private final MicroMechanicMaterial material;

        public RhoPropRef(MicroMechanicMaterial instance, Class<T> valueType, Method getter, Method setter) {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public RhoPropRef(MicroMechanicMaterial instance, Class<T> valueType, String getter, String setter) throws NoSuchMethodException {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public RhoPropRef(MicroMechanicMaterial instance, Class<T> valueType, String property) throws NoSuchMethodException {
            super(instance, valueType, property);
            material = instance;
        }
        
        @Override
        public boolean canWrite(){
            return (material.getRhoModel() instanceof ManualInputDummyModel) & super.canWrite();
        }
        
    }
    
    public class EParPropRef<T> extends myPropRef<T>{
        
        private final MicroMechanicMaterial material;

        public EParPropRef(MicroMechanicMaterial instance, Class<T> valueType, Method getter, Method setter) {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public EParPropRef(MicroMechanicMaterial instance, Class<T> valueType, String getter, String setter) throws NoSuchMethodException {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public EParPropRef(MicroMechanicMaterial instance, Class<T> valueType, String property) throws NoSuchMethodException {
            super(instance, valueType, property);
            material = instance;
        }
        
        @Override
        public boolean canWrite(){
            return (material.getEParModel() instanceof ManualInputDummyModel) & super.canWrite();
        }
        
    }
    
    public class ENorPropRef<T> extends myPropRef<T>{
        
        private final MicroMechanicMaterial material;

        public ENorPropRef(MicroMechanicMaterial instance, Class<T> valueType, Method getter, Method setter) {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public ENorPropRef(MicroMechanicMaterial instance, Class<T> valueType, String getter, String setter) throws NoSuchMethodException {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public ENorPropRef(MicroMechanicMaterial instance, Class<T> valueType, String property) throws NoSuchMethodException {
            super(instance, valueType, property);
            material = instance;
        }
        
        @Override
        public boolean canWrite(){
            return (material.getENorModel() instanceof ManualInputDummyModel) & super.canWrite();
        }
        
    }
    
    public class Nue12PropRef<T> extends myPropRef<T>{
        
        private final MicroMechanicMaterial material;

        public Nue12PropRef(MicroMechanicMaterial instance, Class<T> valueType, Method getter, Method setter) {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public Nue12PropRef(MicroMechanicMaterial instance, Class<T> valueType, String getter, String setter) throws NoSuchMethodException {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public Nue12PropRef(MicroMechanicMaterial instance, Class<T> valueType, String property) throws NoSuchMethodException {
            super(instance, valueType, property);
            material = instance;
        }
        
        @Override
        public boolean canWrite(){
            return (material.getNue12Model() instanceof ManualInputDummyModel) & super.canWrite();
        }
        
    }
    
    public class GPropRef<T> extends myPropRef<T>{
        
        private final MicroMechanicMaterial material;

        public GPropRef(MicroMechanicMaterial instance, Class<T> valueType, Method getter, Method setter) {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public GPropRef(MicroMechanicMaterial instance, Class<T> valueType, String getter, String setter) throws NoSuchMethodException {
            super(instance, valueType, getter, setter);
            material = instance;
        }

        public GPropRef(MicroMechanicMaterial instance, Class<T> valueType, String property) throws NoSuchMethodException {
            super(instance, valueType, property);
            material = instance;
        }
        
        @Override
        public boolean canWrite(){
            return (material.getGModel() instanceof ManualInputDummyModel) & super.canWrite();
        }
        
    }
}
