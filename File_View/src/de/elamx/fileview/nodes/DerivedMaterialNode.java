/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview.nodes;

import de.elamx.core.propertyeditor.DoublePropertyEditorSupport;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.DerivedMaterial;
import de.elamx.laminate.Material;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 * Knoten für ein DefaultMaterial.
 *
 * @author Andreas Hauffe
 */
public class DerivedMaterialNode extends AbstractNode implements PropertyChangeListener {

    public static final String PROP_SET_GENERAL = "GeneralProperties";
    public static final String PROP_SET_STIFFNESS = "StiffnessProperties";
    public static final String PROP_SET_HYGROTHERMAL = "HygrothermalProperties";
    public static final String PROP_SET_STRENGTH = "StrengthProperties";
    public static final String PROP_SET_ADDITIONAL = "AdditionalProperties";

    private static final ResourceBundle bundle = NbBundle.getBundle(DerivedMaterialNode.class);

    private final DerivedMaterial material;

    public DerivedMaterialNode(DerivedMaterial material) {
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

            /*myPropRef<Double> rhoProp = new myPropRef<>(material, double.class, DefaultMaterial.PROP_RHO);
            rhoProp.setName(DefaultMaterial.PROP_RHO);
            rhoProp.setDisplayName(bundle.getString("DefaultMaterial.DENSITY"));
            rhoProp.setShortDescription(bundle.getString("DefaultMaterial.DENSITY.description"));
            rhoProp.setHtmlName(bundle.getString("DefaultMaterial.DENSITY.html"));
            rhoProp.setPropertyEditorClass(DensityPropertyEditorSupport.class);
            generalProp.put(rhoProp);

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
            }*/
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

    /*@Override
    public Action[] getActions(boolean context) {
        List<Action> laminateActions = new ArrayList<>();
        laminateActions.addAll(Utilities.actionsForPath("eLamXActions/Material"));
        return laminateActions.toArray(new Action[laminateActions.size()]);
    }*/

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/elamx/fileview/resources/material.png");
    }

    /*@Override
    public Action getPreferredAction() {
        return Actions.forID("Material", "de.elamx.fileview.PropertiesAction");
    }*/

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
