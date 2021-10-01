/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview.nodes;

import de.elamx.core.GlobalProperties;
import de.elamx.core.propertyeditor.DensityPropertyEditorSupport;
import de.elamx.core.propertyeditor.DoublePropertyEditorSupport;
import de.elamx.core.propertyeditor.HygrothermCoeffPropertyEditorSupport;
import de.elamx.core.propertyeditor.PoissonRatioPropertyEditorSupport;
import de.elamx.core.propertyeditor.YieldStressPropertyEditorSupport;
import de.elamx.core.propertyeditor.YoungsModulusPropertyEditorSupport;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Material;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
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
 * Knoten für ein DefaultMaterial.
 *
 * @author Andreas Hauffe
 */
public class DefaultMaterialNode extends AbstractNode implements PropertyChangeListener {

    public static final String PROP_SET_GENERAL = "GeneralProperties";
    public static final String PROP_SET_STIFFNESS = "StiffnessProperties";
    public static final String PROP_SET_HYGROTHERMAL = "HygrothermalProperties";
    public static final String PROP_SET_STRENGTH = "StrengthProperties";
    public static final String PROP_SET_ADDITIONAL = "AdditionalProperties";

    private static final ResourceBundle bundle = NbBundle.getBundle(DefaultMaterialNode.class);

    private final DefaultMaterial material;

    public DefaultMaterialNode(DefaultMaterial material) {
        super(Children.LEAF, Lookups.singleton(material));
        this.material = material;
        material.addPropertyChangeListener(WeakListeners.propertyChange(this, material));
    }

    @Override
    public String getDisplayName() {
        return material.getName();
    }

    public String[] getAllPropertySets() {
        return new String[]{PROP_SET_GENERAL, PROP_SET_STIFFNESS, PROP_SET_HYGROTHERMAL, PROP_SET_STRENGTH, PROP_SET_ADDITIONAL};
    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName(PROP_SET_GENERAL);
        generalProp.setDisplayName(bundle.getString("DefaultMaterialNode.GENERALPROPERTIES"));

        Sheet.Set stiffProp = Sheet.createPropertiesSet();
        stiffProp.setName(PROP_SET_STIFFNESS);
        stiffProp.setDisplayName(bundle.getString("DefaultMaterialNode.STIFFNESSPROPERTIES"));

        Sheet.Set hygtProp = Sheet.createPropertiesSet();
        hygtProp.setName(PROP_SET_HYGROTHERMAL);
        hygtProp.setDisplayName(bundle.getString("DefaultMaterial.HYGROTHERMALPROPERTIES"));

        Sheet.Set strengthProp = Sheet.createPropertiesSet();
        strengthProp.setName(PROP_SET_STRENGTH);
        strengthProp.setDisplayName(bundle.getString("DefaultMaterial.STRENGTHPROPERTIES"));

        Sheet.Set additionalProp = Sheet.createPropertiesSet();
        additionalProp.setName(PROP_SET_ADDITIONAL);
        additionalProp.setDisplayName(bundle.getString("DefaultMaterial.ADDITIONALPROPERTIES"));

        try {
            myPropRef<String> nameProp = new myPropRef<>(material, String.class, DefaultMaterial.PROP_NAME);
            nameProp.setName(DefaultMaterial.PROP_NAME);
            nameProp.setDisplayName(bundle.getString("DefaultMaterial.NAME"));
            nameProp.setShortDescription(bundle.getString("DefaultMaterial.NAME.description"));
            generalProp.put(nameProp);

            myPropRef<Double> rhoProp = new myPropRef<>(material, double.class, DefaultMaterial.PROP_RHO);
            rhoProp.setName(DefaultMaterial.PROP_RHO);
            rhoProp.setDisplayName(bundle.getString("DefaultMaterial.DENSITY"));
            rhoProp.setShortDescription(bundle.getString("DefaultMaterial.DENSITY.description"));
            rhoProp.setHtmlName(bundle.getString("DefaultMaterial.DENSITY.html"));
            rhoProp.setPropertyEditorClass(DensityPropertyEditorSupport.class);
            generalProp.put(rhoProp);

            myPropRef<Double> e1Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_EPAR);
            e1Prop.setName(DefaultMaterial.PROP_EPAR);
            e1Prop.setDisplayName(bundle.getString("DefaultMaterial.EPARALLEL"));
            e1Prop.setShortDescription(bundle.getString("DefaultMaterial.EPARALLEL.description"));
            e1Prop.setHtmlName(bundle.getString("DefaultMaterial.EPARALLEL.html"));
            e1Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(e1Prop);

            myPropRef<Double> e2Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_ENOR);
            e2Prop.setName(DefaultMaterial.PROP_ENOR);
            e2Prop.setDisplayName(bundle.getString("DefaultMaterial.EPERPENDICULAR"));
            e2Prop.setShortDescription(bundle.getString("DefaultMaterial.EPERPENDICULAR.description"));
            e2Prop.setHtmlName(bundle.getString("DefaultMaterial.EPERPENDICULAR.html"));
            e2Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(e2Prop);

            myPropRef<Double> nue12Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_NUE12);
            nue12Prop.setName(DefaultMaterial.PROP_NUE12);
            nue12Prop.setDisplayName(bundle.getString("DefaultMaterial.POISSONRATIO"));
            nue12Prop.setShortDescription(bundle.getString("DefaultMaterial.POISSONRATIO.description"));
            nue12Prop.setHtmlName(bundle.getString("DefaultMaterial.POISSONRATIO.html"));
            nue12Prop.setPropertyEditorClass(PoissonRatioPropertyEditorSupport.class);
            stiffProp.put(nue12Prop);

            myPropRef<Double> gProp = new myPropRef<>(material, double.class, DefaultMaterial.PROP_G);
            gProp.setName(DefaultMaterial.PROP_G);
            gProp.setDisplayName(bundle.getString("DefaultMaterial.SHEARMODULUS"));
            gProp.setShortDescription(bundle.getString("DefaultMaterial.SHEARMODULUS.description"));
            gProp.setHtmlName(bundle.getString("DefaultMaterial.SHEARMODULUS.html"));
            gProp.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
            stiffProp.put(gProp);
            
            if (GlobalProperties.getDefault().isShowTransShear()){
                myPropRef<Double> g13Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_G13);
                g13Prop.setName(DefaultMaterial.PROP_G13);
                g13Prop.setDisplayName(bundle.getString("DefaultMaterial.SHEARMODULUS13"));
                g13Prop.setShortDescription(bundle.getString("DefaultMaterial.SHEARMODULUS13.description"));
                g13Prop.setHtmlName(bundle.getString("DefaultMaterial.SHEARMODULUS13.html"));
                g13Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
                stiffProp.put(g13Prop);
                
                myPropRef<Double> g23Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_G23);
                g23Prop.setName(DefaultMaterial.PROP_G23);
                g23Prop.setDisplayName(bundle.getString("DefaultMaterial.SHEARMODULUS23"));
                g23Prop.setShortDescription(bundle.getString("DefaultMaterial.SHEARMODULUS23.description"));
                g23Prop.setHtmlName(bundle.getString("DefaultMaterial.SHEARMODULUS23.html"));
                g23Prop.setPropertyEditorClass(YoungsModulusPropertyEditorSupport.class);
                stiffProp.put(g23Prop);
            }

            myPropRef<Double> alpha1Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_ALPHATPAR);
            alpha1Prop.setName(DefaultMaterial.PROP_ALPHATPAR);
            alpha1Prop.setDisplayName(bundle.getString("DefaultMaterial.ALPHAPARALLEL"));
            alpha1Prop.setShortDescription(bundle.getString("DefaultMaterial.ALPHAPARALLEL.description"));
            alpha1Prop.setHtmlName(bundle.getString("DefaultMaterial.ALPHAPARALLEL.html"));
            alpha1Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(alpha1Prop);

            myPropRef<Double> alpha2Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_ALPHATNOR);
            alpha2Prop.setName(DefaultMaterial.PROP_ALPHATNOR);
            alpha2Prop.setDisplayName(bundle.getString("DefaultMaterial.ALPHAPERPENDICULAR"));
            alpha2Prop.setShortDescription(bundle.getString("DefaultMaterial.ALPHAPERPENDICULAR.description"));
            alpha2Prop.setHtmlName(bundle.getString("DefaultMaterial.ALPHAPERPENDICULAR.html"));
            alpha2Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(alpha2Prop);

            myPropRef<Double> beta1Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_BETAPAR);
            beta1Prop.setName(DefaultMaterial.PROP_BETAPAR);
            beta1Prop.setDisplayName(bundle.getString("DefaultMaterial.BETAPARALLEL"));
            beta1Prop.setShortDescription(bundle.getString("DefaultMaterial.BETAPARALLEL.description"));
            beta1Prop.setHtmlName(bundle.getString("DefaultMaterial.BETAPARALLEL.html"));
            beta1Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(beta1Prop);

            myPropRef<Double> beta2Prop = new myPropRef<>(material, double.class, DefaultMaterial.PROP_BETANOR);
            beta2Prop.setName(DefaultMaterial.PROP_BETANOR);
            beta2Prop.setDisplayName(bundle.getString("DefaultMaterial.BETAPERPENDICULAR"));
            beta2Prop.setShortDescription(bundle.getString("DefaultMaterial.BETAPERPENDICULAR.description"));
            beta2Prop.setHtmlName(bundle.getString("DefaultMaterial.BETAPERPENDICULAR.html"));
            beta2Prop.setPropertyEditorClass(HygrothermCoeffPropertyEditorSupport.class);
            hygtProp.put(beta2Prop);

            myPropRef<Double> rpartenProp = new myPropRef<>(material, double.class, DefaultMaterial.PROP_RPARTEN);
            rpartenProp.setName(DefaultMaterial.PROP_RPARTEN);
            rpartenProp.setDisplayName(bundle.getString("DefaultMaterial.RPARTEN"));
            rpartenProp.setShortDescription(bundle.getString("DefaultMaterial.RPARTEN.description"));
            rpartenProp.setHtmlName(bundle.getString("DefaultMaterial.RPARTEN.html"));
            rpartenProp.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rpartenProp);

            myPropRef<Double> rparcomProp = new myPropRef<>(material, double.class, DefaultMaterial.PROP_RPARCOM);
            rparcomProp.setName(DefaultMaterial.PROP_RPARCOM);
            rparcomProp.setDisplayName(bundle.getString("DefaultMaterial.RPARCOM"));
            rparcomProp.setShortDescription(bundle.getString("DefaultMaterial.RPARCOM.description"));
            rparcomProp.setHtmlName(bundle.getString("DefaultMaterial.RPARCOM.html"));
            rparcomProp.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rparcomProp);

            myPropRef<Double> rnortenProp = new myPropRef<>(material, double.class, DefaultMaterial.PROP_RNORTEN);
            rnortenProp.setName(DefaultMaterial.PROP_RNORTEN);
            rnortenProp.setDisplayName(bundle.getString("DefaultMaterial.RNORTEN"));
            rnortenProp.setShortDescription(bundle.getString("DefaultMaterial.RNORTEN.description"));
            rnortenProp.setHtmlName(bundle.getString("DefaultMaterial.RNORTEN.html"));
            rnortenProp.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rnortenProp);

            myPropRef<Double> rnorcomProp = new myPropRef<>(material, double.class, DefaultMaterial.PROP_RNORCOM);
            rnorcomProp.setName(DefaultMaterial.PROP_RNORCOM);
            rnorcomProp.setDisplayName(bundle.getString("DefaultMaterial.RNORCOM"));
            rnorcomProp.setShortDescription(bundle.getString("DefaultMaterial.RNORCOM.description"));
            rnorcomProp.setHtmlName(bundle.getString("DefaultMaterial.RNORCOM.html"));
            rnorcomProp.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rnorcomProp);

            myPropRef<Double> rshear = new myPropRef<>(material, double.class, DefaultMaterial.PROP_RSHEAR);
            rshear.setName(DefaultMaterial.PROP_RSHEAR);
            rshear.setDisplayName(bundle.getString("DefaultMaterial.RSHEAR"));
            rshear.setShortDescription(bundle.getString("DefaultMaterial.RSHEAR.description"));
            rshear.setHtmlName(bundle.getString("DefaultMaterial.RSHEAR.html"));
            rshear.setPropertyEditorClass(YieldStressPropertyEditorSupport.class);
            strengthProp.put(rshear);

            Set<String> addValKeySet = material.getAdditionalValueKeySet();
            MaterialProperty[] mPs = new MaterialProperty[addValKeySet.size()];

            int index = 0;
            for (String key : addValKeySet) {
                mPs[index++] = new MaterialProperty(material, key);
            }

            Arrays.sort(mPs, new Comparator<MaterialProperty>() {
                @Override
                public int compare(MaterialProperty o1, MaterialProperty o2) {
                    String name1 = o1.getHtmlDisplayName();
                    String name2 = o2.getHtmlDisplayName();

                    String n1 = name1.substring(name1.lastIndexOf("("), name1.lastIndexOf(")"));
                    String n2 = name2.substring(name2.lastIndexOf("("), name2.lastIndexOf(")"));

                    return n1.equals(n2) ? name1.compareTo(name2) : n1.compareTo(n2);
                }

            });

            for (MaterialProperty mP : mPs) {
                additionalProp.put(mP);
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
            this.fireDisplayNameChange((String) evt.getOldValue(), (String) evt.getNewValue());
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
        return ImageUtilities.loadImage("de/elamx/fileview/resources/material.png");
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("Material", "de.elamx.fileview.PropertiesAction");
    }

    public class MaterialProperty extends PropertySupport.ReadWrite<Double> {

        Material material;
        String propName;
        boolean showHtml = Boolean.getBoolean("nb.useSwingHtmlRendering");

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

            if (newVal > maxVal) {
                newVal = maxVal;
            } else if (newVal < minVal) {
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

        public void setCanWrite(boolean write) {
            this.canWrite = write;
        }

        @Override
        public boolean canWrite() {
            return canWrite & super.canWrite();
        }
    }

    public class myPropRef<T> extends PropertySupport.Reflection<T> {

        String htmlName = null;

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

        public void setCanWrite(boolean write) {
            this.canWrite = write;
        }

        @Override
        public boolean canWrite() {
            return canWrite & super.canWrite();
        }
    }
}
