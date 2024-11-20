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
import de.elamx.micromechanics.Matrix;
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
public class MatrixNode extends AbstractNode implements PropertyChangeListener {
    
    public static final String PROP_SET_GENERAL = "GeneralProperties";
    public static final String PROP_SET_STIFFNESS = "StiffnessProperties";
    public static final String PROP_SET_HYGROTHERMAL = "HygrothermalProperties";
    
    private static final ResourceBundle bundle = NbBundle.getBundle(MatrixNode.class);
    
    private final Matrix matrix;

    @SuppressWarnings("this-escape")
    public MatrixNode(Matrix material) {
        super(Children.LEAF, Lookups.singleton(material));
        this.matrix = material;
        material.addPropertyChangeListener(WeakListeners.propertyChange(this, material));
    }

    @Override
    public String getDisplayName() {
        return matrix.getName();
    }

    @Override
    protected Sheet createSheet() {
        
        Sheet sheet = Sheet.createDefault();
        
        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName(PROP_SET_GENERAL);
        generalProp.setDisplayName(bundle.getString("MatrixNode.GENERALPROPERTIES"));

        Sheet.Set stiffProp = Sheet.createPropertiesSet();
        stiffProp.setName(PROP_SET_STIFFNESS);
        stiffProp.setDisplayName(bundle.getString("MatrixNode.STIFFNESSPROPERTIES"));

        Sheet.Set hygtProp = Sheet.createPropertiesSet();
        hygtProp.setName(PROP_SET_HYGROTHERMAL);
        hygtProp.setDisplayName(bundle.getString("MatrixNode.HYGROTHERMALPROPERTIES"));
        
        try {
            myPropRef<String> nameProp  = new myPropRef<>(matrix, String.class, Matrix.PROP_NAME);
            nameProp.setName(Matrix.PROP_NAME);
            nameProp.setDisplayName(bundle.getString("MatrixNode.NAME"));
            nameProp.setShortDescription(bundle.getString("MatrixNode.NAME.description"));
            generalProp.put(nameProp);
            
            myPropRef<Double>rhoProp  = new myPropRef<>(matrix, double.class, Matrix.PROP_RHO);
            rhoProp.setName(Matrix.PROP_RHO);
            rhoProp.setDisplayName(bundle.getString("MatrixNode.DENSITY"));
            rhoProp.setShortDescription(bundle.getString("MatrixNode.DENSITY.description"));
            rhoProp.setHtmlName(bundle.getString("MatrixNode.DENSITY.html"));
            rhoProp.setPropertyEditorClass(DensityPropertyEditorSupport.class);
            generalProp.put(rhoProp);
            
            myPropRef<Double>e1Prop  = new myPropRef<>(matrix, double.class, Matrix.PROP_E);
            e1Prop.setName(Matrix.PROP_E);
            e1Prop.setDisplayName(bundle.getString("MatrixNode.E"));
            e1Prop.setShortDescription(bundle.getString("MatrixNode.E.description"));
            e1Prop.setHtmlName(bundle.getString("MatrixNode.E.html"));
            e1Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(e1Prop);
            
            myPropRef<Double>nue12Prop  = new myPropRef<>(matrix, double.class, Matrix.PROP_NUE);
            nue12Prop.setName(Matrix.PROP_NUE);
            nue12Prop.setDisplayName(bundle.getString("MatrixNode.POISSONRATIO"));
            nue12Prop.setShortDescription(bundle.getString("MatrixNode.POISSONRATIO.description"));
            nue12Prop.setHtmlName(bundle.getString("MatrixNode.POISSONRATIO.html"));
            nue12Prop.setPropertyEditorClass(PoissonRatioPropertyEditorSupport.class);
            stiffProp.put(nue12Prop);
            
            myPropRef<Double>gProp  = new myPropRef<>(matrix, double.class, Matrix.PROP_G);
            gProp.setName(Matrix.PROP_G);
            gProp.setDisplayName(bundle.getString("MatrixNode.SHEARMODULUS"));
            gProp.setShortDescription(bundle.getString("MatrixNode.SHEARMODULUS.description"));
            gProp.setHtmlName(bundle.getString("MatrixNode.SHEARMODULUS.html"));
            gProp.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            gProp.setCanWrite(false);
            stiffProp.put(gProp);
            
            myPropRef<Double>alpha1Prop  = new myPropRef<>(matrix, double.class, Matrix.PROP_ALPHA);
            alpha1Prop.setName(Matrix.PROP_ALPHA);
            alpha1Prop.setDisplayName(bundle.getString("MatrixNode.ALPHA"));
            alpha1Prop.setShortDescription(bundle.getString("MatrixNode.ALPHA.description"));
            alpha1Prop.setHtmlName(bundle.getString("MatrixNode.ALPHA.html"));
            alpha1Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(alpha1Prop);
                        
            myPropRef<Double>beta1Prop  = new myPropRef<>(matrix, double.class, Matrix.PROP_BETA);
            beta1Prop.setName(Matrix.PROP_BETA);
            beta1Prop.setDisplayName(bundle.getString("MatrixNode.BETA"));
            beta1Prop.setShortDescription(bundle.getString("MatrixNode.BETA.description"));
            beta1Prop.setHtmlName(bundle.getString("MatrixNode.BETA.html"));
            beta1Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(beta1Prop);
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
        if (evt.getPropertyName().equals(Matrix.PROP_E))  {
            if (((double)evt.getNewValue() != (double)evt.getOldValue()) && ((double)evt.getNewValue() < 0.)) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(MatrixNode.class, "Warning.negativeelasticmodulus"), NotifyDescriptor.WARNING_MESSAGE));
            }
        }
        if (evt.getPropertyName().equals(Matrix.PROP_NUE)) {
            if (((double)evt.getNewValue() != (double)evt.getOldValue()) && ((double)evt.getNewValue() < 0.)) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(MatrixNode.class, "Warning.negativepoissonratio"), NotifyDescriptor.WARNING_MESSAGE));
            }
        }
        this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
    
    @Override
    public Action[] getActions(boolean context) {
        List<Action> laminateActions = new ArrayList<>();
        laminateActions.addAll(Utilities.actionsForPath("eLamXActions/Matrix"));
        return laminateActions.toArray(new Action[laminateActions.size()]);
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage ("de/elamx/micromechanicsui/resources/matrix.png");
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
        
        String  htmlName = null;
        
        boolean showHtml = Boolean.getBoolean("nb.useSwingHtmlRendering");
        
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
