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

import de.elamx.laminate.Layer;
import de.elamx.laminate.failure.Criterion;
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
public class CriterionProperty extends PropertySupport.ReadWrite<Criterion> {

    Layer layer;

    public CriterionProperty(Layer layer) {
        super(Layer.PROP_CRITERION, Criterion.class, NbBundle.getMessage(LayerNodeFactory.class, "LayerNode.Criterion"), NbBundle.getMessage(LayerNodeFactory.class, "LayerNode.Criterion.description"));
        this.layer = layer;
    }

    @Override
    public Criterion getValue() throws IllegalAccessException, InvocationTargetException {
        return layer.getCriterion();
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new CriterionPropertyEditorSupport(layer);
    }

    @Override
    public void setValue(Criterion newValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        layer.setCriterion(newValue);
    }

    private class CriterionPropertyEditorSupport extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

        private final Layer layer;

        private CriterionPropertyEditorSupport(Layer layer) {
            this.layer = layer;
        }

        @Override
        public String getAsText() {
            String s = ((Criterion) getValue()).getDisplayName();
            if (s == null) {
                return "No Criterion Set";
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

    private class Inplace extends JComboBox<Criterion> implements InplaceEditor {

        private PropertyEditor editor = null;
        private final Layer layer;
        private final ItemListener myItemList = new MyItemListener();
        private boolean connecting = false;

        private Inplace(final Layer layer) {
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
            layer.setCriterion((Criterion) getSelectedItem());
            ((JComponent) getParent()).requestFocus();
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        @Override
        public void reset() {

            Lookup lkp = Lookups.forPath("elamx/failurecriteria");
            ArrayList<Criterion> criteria = new ArrayList<>();
            for (Criterion crit : lkp.lookupAll(Criterion.class)) {
                criteria.add(crit);
            }
            // Sortieren der Liste nach den Name der Versagenskriterien
            Collections.sort(criteria, new Comparator<Criterion>() {
                @Override
                public int compare(Criterion o1, Criterion o2) {
                    return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
                }
            });
            removeItemListener(myItemList);
            removeAllItems();
            for (Criterion m : criteria) {
                addItem(m);
            }
            Criterion m = (Criterion) editor.getValue();
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
