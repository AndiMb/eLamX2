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
package de.elamx.laminateditor;

import de.elamx.laminate.DataLayer;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.eLamXLookup;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class MaterialProperty extends PropertySupport.ReadWrite<LayerMaterial> {

    DataLayer layer;

    public MaterialProperty(DataLayer layer) {
        super(DataLayer.PROP_MATERIAL, LayerMaterial.class, NbBundle.getMessage(LayerNodeFactory.class, "LayerNode.Material"), NbBundle.getMessage(LayerNodeFactory.class, "LayerNode.Material.description"));
        this.layer = layer;
    }

    @Override
    public LayerMaterial getValue() throws IllegalAccessException, InvocationTargetException {
        return layer.getMaterial();
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new MaterialPropertyEditorSupport(layer);
    }

    @Override
    public void setValue(LayerMaterial newValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        layer.setMaterial(newValue);
    }

    private class MaterialPropertyEditorSupport extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

        private final DataLayer layer;

        private MaterialPropertyEditorSupport(DataLayer layer) {
            this.layer = layer;
        }

        @Override
        public String getAsText() {
            String s = ((LayerMaterial) getValue()).getName();
            if (s == null) {
                return "No Material Set";
            }
            return s;
        }

        @Override
        public void setAsText(String s) {
            //setValue(s);
        }

        @Override
        public void attachEnv(PropertyEnv env) {
            env.registerInplaceEditorFactory(this);
        }
        private InplaceEditor ed = null;

        @Override
        public InplaceEditor getInplaceEditor() {
            if (ed == null) {
                ed = new Inplace(layer);
            }
            return ed;
        }
    }

    private class Inplace extends JComboBox<LayerMaterial> implements InplaceEditor {

        private PropertyEditor editor = null;
        private final DataLayer layer;
        private final ItemListener myItemList = new MyItemListener();
        private boolean connecting = false;

        private Inplace(final DataLayer layer) {
            this.layer = layer;
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            connecting = true;
            editor = propertyEditor;
            reset();
            connecting = false;
        }

        @Override
        public JComponent getComponent() {
            return this;
        }

        @Override
        public void clear() {
            editor = null;
            model = null;
        }

        @Override
        public Object getValue() {
            repaint();
            updateUI();
            ((JComponent) getParent()).requestFocus();
            //updateUI();
            //repaint();
            return getSelectedItem();
        }

        @Override
        public void setValue(Object object) {
            setSelectedItem(object);
            repaint();
            updateUI();
            layer.setMaterial((LayerMaterial) getSelectedItem());
            ((JComponent) getParent()).requestFocus();
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        @Override
        public void reset() {
            ArrayList<LayerMaterial> materials = new ArrayList<>();
            for (LayerMaterial mat : eLamXLookup.getDefault().lookupAll(LayerMaterial.class)) {
                materials.add(mat);
            }
            // Sortieren der Liste nach den Name der Materialien
            Collections.sort(materials, new Comparator<LayerMaterial>() {
                @Override
                public int compare(LayerMaterial o1, LayerMaterial o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            removeItemListener(myItemList);
            removeAllItems();
            for (LayerMaterial m : materials) {
                addItem(m);
            }
            LayerMaterial m = (LayerMaterial) editor.getValue();
            if (m != null) {
                setSelectedItem(m);
            }
            addItemListener(myItemList);
        }

        @Override
        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

        @Override
        public PropertyModel getPropertyModel() {
            return model;
        }
        private PropertyModel model;

        @Override
        public void setPropertyModel(PropertyModel propertyModel) {
            this.model = propertyModel;
        }

        @Override
        public boolean isKnownComponent(Component component) {
            return component == this || isAncestorOf(component);
        }

        /**
         * Overridden to not fire changes is an event is called inside the
         * connect method
         */
        @Override
        public void fireActionEvent() {
            if (connecting || editor == null) {
                return;
            } else {
                if (editor == null) {
                    return;
                }
                if ("comboBoxEdited".equals(getActionCommand())) {
                    setActionCommand(COMMAND_SUCCESS);
                }
                super.fireActionEvent();
            }
        }

        private class MyItemListener implements ItemListener {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Inplace.this.setActionCommand(InplaceEditor.COMMAND_SUCCESS);
                    Inplace.this.fireActionEvent();
                }
            }
        }
    }
}
