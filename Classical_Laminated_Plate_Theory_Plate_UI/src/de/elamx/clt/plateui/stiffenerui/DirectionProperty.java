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
package de.elamx.clt.plateui.stiffenerui;

import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
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
public class DirectionProperty extends PropertySupport.ReadWrite<Integer> {

    StiffenerProperties props;
    
    public DirectionProperty(StiffenerProperties props) {
        super(StiffenerProperties.PROP_DIRECTION, Integer.class, NbBundle.getMessage(DirectionProperty.class, "StiffenerDefinitionNode.Direction"), NbBundle.getMessage(DirectionProperty.class, "StiffenerDefinitionNode.Direction.description"));
        this.props = props;
    }

    @Override
    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
        return props.getDirection();
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new DirectionPropertyEditorSupport(props);
    }

    @Override
    public void setValue(Integer newValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        props.setDirection(newValue);
    }

    private class DirectionPropertyEditorSupport extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

        private final StiffenerProperties props;

        private DirectionPropertyEditorSupport(StiffenerProperties props) {
            this.props = props;
        }

        @Override
        public String getAsText() {
            String s = null;
            switch (props.getDirection()) {
                case StiffenerProperties.X_DIRECTION:
                    s = StiffenerProperties.X_DIRECTION_STRING;
                    break;
                case StiffenerProperties.Y_DIRECTION:
                    s = StiffenerProperties.Y_DIRECTION_STRING;
                    break;
            }
            if (s == null) {
                return "No Direction Set";
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
                ed = new Inplace(props);
            }
            return ed;
        }
    }

    private class Inplace extends JComboBox<String> implements InplaceEditor {
        
        private PropertyEditor editor = null;
        private final StiffenerProperties props;
        private final ItemListener myItemList = new MyItemListener();
        private boolean connecting = false;


        private Inplace(final StiffenerProperties props) {
            this.props = props;
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
            int value = 0;
            switch (getSelectedIndex()) {
                case 0:
                    value = StiffenerProperties.X_DIRECTION; break;
                case 1:
                    value = StiffenerProperties.Y_DIRECTION; break;
            }
            return value;
        }

        @Override
        public void setValue(Object object) {
            setSelectedItem(object);
            repaint();
            updateUI();
            switch (getSelectedIndex()) {
                case 0:
                    props.setDirection(StiffenerProperties.X_DIRECTION); break;
                case 1:
                    props.setDirection(StiffenerProperties.Y_DIRECTION); break;
            }
            ((JComponent) getParent()).requestFocus();
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        @Override
        public void reset() {
            removeItemListener(myItemList);
            removeAllItems();
            addItem(StiffenerProperties.X_DIRECTION_STRING);
            addItem(StiffenerProperties.Y_DIRECTION_STRING);
            switch (((Integer)editor.getValue())) {
                case StiffenerProperties.X_DIRECTION:
                    setSelectedIndex(0); break;
                case StiffenerProperties.Y_DIRECTION:
                    setSelectedIndex(1); break;
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
