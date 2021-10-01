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
package de.elamx.micromechanicsui.nodes.properties;

import de.elamx.micromechanics.MicroMechanicMaterial;
import de.elamx.micromechanics.models.ManualInputDummyModel;
import de.elamx.micromechanics.models.MicroMechModel;
import de.elamx.micromechanics.models.Mischungsregel_m;
import de.elamx.micromechanicsui.nodes.MicroMechanicMaterialNode;
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
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class RhoMicroMechModelProperty extends PropertySupport.ReadWrite<MicroMechModel> {

    MicroMechanicMaterial mmMaterial;

    String htmlName = null;

    boolean showHtml = Boolean.getBoolean("nb.useSwingHtmlRendering");

    public RhoMicroMechModelProperty(MicroMechanicMaterial material) {
        super(MicroMechanicMaterial.PROP_RHOMODEL, MicroMechModel.class, NbBundle.getMessage(MicroMechanicMaterialNode.class, "MicroMechanicMaterialNode.RhoMicroMechModel"), NbBundle.getMessage(MicroMechanicMaterialNode.class, "MicroMechanicMaterialNode.RhoMicroMechModel.description"));
        this.htmlName = NbBundle.getMessage(MicroMechanicMaterialNode.class, "MicroMechanicMaterialNode.RhoMicroMechModel.html");
        this.mmMaterial = material;
        this.setName(MicroMechanicMaterial.PROP_RHOMODEL);
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

    @Override
    public MicroMechModel getValue() throws IllegalAccessException, InvocationTargetException {
        return mmMaterial.getRhoModel();
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new RhoMechModelPropertyEditorSupport(mmMaterial);
    }

    @Override
    public void setValue(MicroMechModel model) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        mmMaterial.setRhoModel(model);
    }

    private class RhoMechModelPropertyEditorSupport extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

        private final MicroMechanicMaterial mmMaterial;

        private RhoMechModelPropertyEditorSupport(MicroMechanicMaterial mmMaterial) {
            this.mmMaterial = mmMaterial;
        }

        @Override
        public String getAsText() {
            String s = ((MicroMechModel) getValue()).getDisplayName();
            if (s == null) {
                return "No Micro Mechanic Model Set";
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
                ed = new Inplace(mmMaterial);
            }
            return ed;
        }
    }

    private class Inplace extends JComboBox<MicroMechModel> implements InplaceEditor {

        private PropertyEditor editor = null;
        private final MicroMechanicMaterial mmMaterial;
        private final ItemListener myItemList = new MyItemListener();
        private boolean connecting = false;

        private Inplace(final MicroMechanicMaterial mmMaterial) {
            this.mmMaterial = mmMaterial;
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
            mmMaterial.setRhoModel((MicroMechModel) getSelectedItem());
            ((JComponent) getParent()).requestFocus();
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        @Override
        public void reset() {

            Lookup lkp = Lookups.forPath("elamx/micromechmodel");
            ArrayList<MicroMechModel> mmModel = new ArrayList<>();
            for (MicroMechModel mmm : lkp.lookupAll(MicroMechModel.class)) {
                mmModel.add(mmm);
            }
            // Sortieren der Liste nach den Name der Versagenskriterien
            Collections.sort(mmModel, new Comparator<MicroMechModel>() {
                @Override
                public int compare(MicroMechModel o1, MicroMechModel o2) {
                    return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
                }
            });
            removeItemListener(myItemList);
            removeAllItems();
            for (MicroMechModel m : mmModel) {
                if (m.getClass().equals(Mischungsregel_m.class) || m.getClass().equals(ManualInputDummyModel.class)){
                    addItem(m);
                }
            }
            MicroMechModel m = (MicroMechModel) editor.getValue();
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
