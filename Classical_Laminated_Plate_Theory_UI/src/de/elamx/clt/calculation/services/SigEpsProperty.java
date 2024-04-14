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
package de.elamx.clt.calculation.services;

import de.elamx.clt.CLT_Input;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
public class SigEpsProperty extends PropertySupport.ReadWrite<Integer> {

    private final CLT_Input input;
    private final int index;

    private String htmlName = null;
    private final String n;
    private final String e;

    boolean showHtml = Boolean.getBoolean("nb.useSwingHtmlRendering");

    @SuppressWarnings("this-escape")
    public SigEpsProperty(CLT_Input input, int index) {
        super(CLT_Input.PROP_USESTRAINS + index, Integer.class, NbBundle.getMessage(SigEpsProperty.class, "SigEpsProperty.UseStrain" + index), NbBundle.getMessage(SigEpsProperty.class, "SigEpsProperty.UseStrain" + index + ".description"));
        this.htmlName = NbBundle.getMessage(SigEpsProperty.class, "SigEpsProperty.UseStrain"  + index + ".html");
        this.input = input;
        this.index = index;
        this.setName(CLT_Input.PROP_USESTRAINS + index);
        n = NbBundle.getMessage(SigEpsProperty.class, "SigEpsProperty.UseStrain" + index + ".force");
        e = NbBundle.getMessage(SigEpsProperty.class, "SigEpsProperty.UseStrain" + index + ".strain");
    }
    
    

    @Override
    public String getDisplayName() {
        return showHtml && htmlName != null ? htmlName : super.getDisplayName();
    }

    @Override
    public String getHtmlDisplayName() {
        return showHtml ? htmlName : null;
    }

    @Override
    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
        return input.isUseStrains(index) ? 1 : 0;
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new UseStrainPropertyEditorSupport(input);
    }

    @Override
    public void setValue(Integer use) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        input.setUseStrains(index, (use != 0));
    }

    private class UseStrainPropertyEditorSupport extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

        private final CLT_Input mmMaterial;
        private JLabel renderer = new JLabel();

        private UseStrainPropertyEditorSupport(CLT_Input mmMaterial) {
            this.mmMaterial = mmMaterial;
        }

        @Override
        public String getAsText() {
            return (Integer)getValue() == 0 ? n : e;
        }

        @Override
        public void setAsText(String s) {
            //setValue(s);
        }

        @Override
        public void attachEnv(PropertyEnv env) {
            env.registerInplaceEditorFactory(this);
            env.getFeatureDescriptor().setValue("htmlDisplayValue", getAsText());
        }
        private InplaceEditor ed = null;

        @Override
        public InplaceEditor getInplaceEditor() {
            if (ed == null) {
                ed = new Inplace(mmMaterial);
            }
            return ed;
        }

        @Override
        public boolean isPaintable() {
            return true; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void paintValue(Graphics gfx, Rectangle box) {
            renderer.setBounds(box);
            renderer.setText(getAsText());
            renderer.paint(gfx);
        }
        
        
    }

    private class Inplace extends JComboBox<String> implements InplaceEditor {

        private PropertyEditor editor = null;
        private final CLT_Input input;
        private final ItemListener myItemList = new MyItemListener();
        private boolean connecting = false;

        private Inplace(final CLT_Input input) {
            this.input = input;
            addItem(n);
            addItem(e);
            this.setSelectedIndex(input.isUseStrains(index) ? 1 : 0);
            addItemListener(myItemList);
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            connecting = true;
            editor = propertyEditor;
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
            //repaint();
            //updateUI();
            ((JComponent) getParent()).requestFocus();
            return getSelectedIndex();
        }

        @Override
        public void setValue(Object object) {
            setSelectedItem(object);
            //repaint();
            //updateUI();
            input.setUseStrains(index, (this.getSelectedIndex() != 0));
            ((JComponent) getParent()).requestFocus();
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
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

        @Override
        public void reset() {
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
