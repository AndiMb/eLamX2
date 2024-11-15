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

import de.elamx.core.propertyeditor.DensityPropertyEditorSupport;
import de.elamx.core.propertyeditor.DoublePropertyEditorSupport;
import de.elamx.core.propertyeditor.HygrothermCoeffPropertyEditorSupport;
import de.elamx.core.propertyeditor.PoissonRatioPropertyEditorSupport;
import de.elamx.core.propertyeditor.YoungsModulusPropertyEditorSupport;
import de.elamx.laminate.Material;
import de.elamx.micromechanics.Fiber;
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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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
 *
 * @author Andreas Hauffe
 */
public class FibreNode extends AbstractNode implements PropertyChangeListener {
    
    public static final String PROP_SET_GENERAL = "GeneralProperties";
    public static final String PROP_SET_STIFFNESS = "StiffnessProperties";
    public static final String PROP_SET_HYGROTHERMAL = "HygrothermalProperties";
    
    private static final ResourceBundle bundle = NbBundle.getBundle(FibreNode.class);
    
    private final Fiber material;

    @SuppressWarnings("this-escape")
    public FibreNode(Fiber material) {
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
        generalProp.setDisplayName(bundle.getString("FibreNode.GENERALPROPERTIES"));

        Sheet.Set stiffProp = Sheet.createPropertiesSet();
        stiffProp.setName(PROP_SET_STIFFNESS);
        stiffProp.setDisplayName(bundle.getString("FibreNode.STIFFNESSPROPERTIES"));

        Sheet.Set hygtProp = Sheet.createPropertiesSet();
        hygtProp.setName(PROP_SET_HYGROTHERMAL);
        hygtProp.setDisplayName(bundle.getString("FibreNode.HYGROTHERMALPROPERTIES"));
        
        try {
            myPropRef<String> nameProp  = new myPropRef<>(material, String.class, Fiber.PROP_NAME);
            nameProp.setName(Fiber.PROP_NAME);
            nameProp.setDisplayName(bundle.getString("FibreNode.NAME"));
            nameProp.setShortDescription(bundle.getString("FibreNode.NAME.description"));
            generalProp.put(nameProp);
            
            myPropRef<Double>rhoProp  = new myPropRef<>(material, double.class, Fiber.PROP_RHO);
            rhoProp.setName(Fiber.PROP_RHO);
            rhoProp.setDisplayName(bundle.getString("FibreNode.DENSITY"));
            rhoProp.setShortDescription(bundle.getString("FibreNode.DENSITY.description"));
            rhoProp.setHtmlName(bundle.getString("FibreNode.DENSITY.html"));
            rhoProp.setPropertyEditorClass(DensityPropertyEditorSupport.class);
            generalProp.put(rhoProp);
            
            myPropRef<Double>e1Prop  = new myPropRef<>(material, double.class, Fiber.PROP_EPAR);
            e1Prop.setName(Fiber.PROP_EPAR);
            e1Prop.setDisplayName(bundle.getString("FibreNode.EPARALLEL"));
            e1Prop.setShortDescription(bundle.getString("FibreNode.EPARALLEL.description"));
            e1Prop.setHtmlName(bundle.getString("FibreNode.EPARALLEL.html"));
            e1Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(e1Prop);
            
            myPropRef<Double>e2Prop  = new myPropRef<>(material, double.class, Fiber.PROP_ENOR);
            e2Prop.setName(Fiber.PROP_ENOR);
            e2Prop.setDisplayName(bundle.getString("FibreNode.EPERPENDICULAR"));
            e2Prop.setShortDescription(bundle.getString("FibreNode.EPERPENDICULAR.description"));
            e2Prop.setHtmlName(bundle.getString("FibreNode.EPERPENDICULAR.html"));
            e2Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(e2Prop);
            
            myPropRef<Double>nue12Prop  = new myPropRef<>(material, double.class, Fiber.PROP_NUE12);
            nue12Prop.setName(Fiber.PROP_NUE12);
            nue12Prop.setDisplayName(bundle.getString("FibreNode.POISSONRATIO"));
            nue12Prop.setShortDescription(bundle.getString("FibreNode.POISSONRATIO.description"));
            nue12Prop.setHtmlName(bundle.getString("FibreNode.POISSONRATIO.html"));
            nue12Prop.setPropertyEditorClass(PoissonRatioPropertyEditorSupport.class);
            stiffProp.put(nue12Prop);
            
            myPropRef<Double>gProp  = new myPropRef<>(material, double.class, Fiber.PROP_G);
            gProp.setName(Fiber.PROP_G);
            gProp.setDisplayName(bundle.getString("FibreNode.SHEARMODULUS"));
            gProp.setShortDescription(bundle.getString("FibreNode.SHEARMODULUS.description"));
            gProp.setHtmlName(bundle.getString("FibreNode.SHEARMODULUS.html"));
            gProp.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(gProp);
            
            myPropRef<Double>alpha1Prop  = new myPropRef<>(material, double.class, Fiber.PROP_ALPHATPAR);
            alpha1Prop.setName(Fiber.PROP_ALPHATPAR);
            alpha1Prop.setDisplayName(bundle.getString("FibreNode.ALPHAPARALLEL"));
            alpha1Prop.setShortDescription(bundle.getString("FibreNode.ALPHAPARALLEL.description"));
            alpha1Prop.setHtmlName(bundle.getString("FibreNode.ALPHAPARALLEL.html"));
            alpha1Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(alpha1Prop);
            
            myPropRef<Double>alpha2Prop  = new myPropRef<>(material, double.class, Fiber.PROP_ALPHATNOR);
            alpha2Prop.setName(Fiber.PROP_ALPHATNOR);
            alpha2Prop.setDisplayName(bundle.getString("FibreNode.ALPHAPERPENDICULAR"));
            alpha2Prop.setShortDescription(bundle.getString("FibreNode.ALPHAPERPENDICULAR.description"));
            alpha2Prop.setHtmlName(bundle.getString("FibreNode.ALPHAPERPENDICULAR.html"));
            alpha2Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(alpha2Prop);
                        
            myPropRef<Double>beta1Prop  = new myPropRef<>(material, double.class, Fiber.PROP_BETAPAR);
            beta1Prop.setName(Fiber.PROP_BETAPAR);
            beta1Prop.setDisplayName(bundle.getString("FibreNode.BETAPARALLEL"));
            beta1Prop.setShortDescription(bundle.getString("FibreNode.BETAPARALLEL.description"));
            beta1Prop.setHtmlName(bundle.getString("FibreNode.BETAPARALLEL.html"));
            beta1Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(beta1Prop);
            
            myPropRef<Double>beta2Prop  = new myPropRef<>(material, double.class, Fiber.PROP_BETANOR);
            beta2Prop.setName(Fiber.PROP_BETANOR);
            beta2Prop.setDisplayName(bundle.getString("FibreNode.BETAPERPENDICULAR"));
            beta2Prop.setShortDescription(bundle.getString("FibreNode.BETAPERPENDICULAR.description"));
            beta2Prop.setHtmlName(bundle.getString("FibreNode.BETAPERPENDICULAR.html"));
            beta2Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(beta2Prop);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        sheet.put(generalProp);
        sheet.put(stiffProp);
        sheet.put(hygtProp);
        
        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Material.PROP_NAME)) {
            this.fireDisplayNameChange((String)evt.getOldValue(), (String)evt.getNewValue());
            return;
        }
        if (evt.getPropertyName().equals(Fiber.PROP_EPAR) || evt.getPropertyName().equals(Fiber.PROP_ENOR))  {
            if (((double)evt.getNewValue() != (double)evt.getOldValue()) && ((double)evt.getNewValue() < 0.)) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(FibreNode.class, "Warning.negativeelasticmodulus"), NotifyDescriptor.WARNING_MESSAGE));
            }
        }
        if (evt.getPropertyName().equals(Fiber.PROP_NUE12)) {
            if (((double)evt.getNewValue() != (double)evt.getOldValue()) && ((double)evt.getNewValue() < 0.)) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(FibreNode.class, "Warning.negativepoissonratio"), NotifyDescriptor.WARNING_MESSAGE));
            }
        }
        if (evt.getPropertyName().equals(Fiber.PROP_G) || evt.getPropertyName().equals(Fiber.PROP_G13) || evt.getPropertyName().equals(Fiber.PROP_G23)) {
            if (((double)evt.getNewValue() != (double)evt.getOldValue()) && ((double)evt.getNewValue() < 0.)) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(FibreNode.class, "Warning.negativeshearmodulus"), NotifyDescriptor.WARNING_MESSAGE));
            }
        }
        this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
    
    @Override
    public Action[] getActions(boolean context) {
        List<Action> laminateActions = new ArrayList<>();
        laminateActions.addAll(Utilities.actionsForPath("eLamXActions/Fibre"));
        return laminateActions.toArray(new Action[laminateActions.size()]);
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage ("de/elamx/micromechanicsui/resources/fibre.png");
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("Material", "de.elamx.micromechanicsui.actions.PropertiesAction");
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
}
