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
package de.elamx.core.propertyeditor;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.beans.PropertyEditor;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.EventListenerList;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author raedel
 */
public class IntegerSpinnerPropertyEditorSupport extends IntegerPropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    protected InplaceEditor editor = null;
    protected SpinnerNumberModel model;

    public IntegerSpinnerPropertyEditorSupport(Object source, SpinnerNumberModel model) {
        super(source);
        this.model = model;
    }

    public IntegerSpinnerPropertyEditorSupport(SpinnerNumberModel model) {
        this.model = model;
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        if (editor == null) {
            editor = new IntegerInplaceEditor(model);
        }
        return editor;
    }

    protected static class IntegerInplaceEditor implements InplaceEditor {

        private final JSpinner spinner;
        private PropertyEditor editor = null;
        private PropertyModel model;
        private boolean connecting = false;
        private FocusListener focusListener = new MyFocusListener();

        public IntegerInplaceEditor(SpinnerNumberModel model) {
            this.spinner = new JSpinner(model);
            ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().addFocusListener(focusListener);
            Component[] comps = spinner.getComponents();
            for (Component component : comps) {
                component.addFocusListener(focusListener);
            }
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
            return spinner;
        }

        @Override
        public void clear() {
            //avoid memory leaks:
            editor = null;
            model = null;
        }

        @Override
        public Object getValue() {
            return spinner.getValue();
        }

        @Override
        public void setValue(Object object) {
            try {
                spinner.setValue(object);
            } catch (IllegalArgumentException e) {
                spinner.setValue(null);
            }
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        @Override
        public void reset() {
            Integer i = (Integer) editor.getValue();
            if (i != null) {
                setValue(i);
            }
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

        @Override
        public void setPropertyModel(PropertyModel propertyModel) {
            this.model = propertyModel;
        }

        @Override
        public boolean isKnownComponent(Component component) {
            return component == spinner || spinner.isAncestorOf(component);
        }

        private EventListenerList listenerList = new EventListenerList();

        @Override
        public void addActionListener(ActionListener l) {
            listenerList.add(ActionListener.class, l);
        }

        @Override
        public void removeActionListener(ActionListener l) {
            listenerList.remove(ActionListener.class, l);
        }

        /**
         * This protected field is implementation specific. Do not access
         * directly or override. Use the accessor methods instead.
         *
         * @see #setActionCommand
         * @see #getActionCommand
         */
        private String actionCommand = "spinnerFocusChanged";

        /**
         * Returns the action command that is included in the event sent to
         * action listeners.
         *
         * @return the string containing the "command" that is sent to action
         * listeners.
         */
        public String getActionCommand() {
            return actionCommand;
        }

        /**
         * Sets the action command that should be included in the event sent to
         * action listeners.
         *
         * @param aCommand a string containing the "command" that is sent to
         * action listeners; the same listener can then do different things
         * depending on the command it receives
         */
        public void setActionCommand(String aCommand) {
            actionCommand = aCommand;
        }

        // Flag to ensure that infinite loops do not occur with ActionEvents.
        private boolean firingActionEvent = false;

        /**
         * Overridden to not fire changes is an event is called inside the
         * connect method
         */
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
                if (!firingActionEvent) {
                    // Set flag to ensure that an infinite loop is not created
                    firingActionEvent = true;
                    ActionEvent e = null;
                    // Guaranteed to return a non-null array
                    Object[] listeners = listenerList.getListenerList();
                    long mostRecentEventTime = EventQueue.getMostRecentEventTime();
                    int modifiers = 0;
                    AWTEvent currentEvent = EventQueue.getCurrentEvent();
                    if (currentEvent instanceof InputEvent) {
                        modifiers = ((InputEvent) currentEvent).getModifiersEx();
                    } else if (currentEvent instanceof ActionEvent) {
                        modifiers = ((ActionEvent) currentEvent).getModifiers();
                    }
                    // Process the listeners last to first, notifying
                    // those that are interested in this event
                    for (int i = listeners.length - 2; i >= 0; i -= 2) {
                        if (listeners[i] == ActionListener.class) {
                            // Lazily create the event:
                            if (e == null) {
                                e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                        getActionCommand(),
                                        mostRecentEventTime, modifiers);
                            }
                            ((ActionListener) listeners[i + 1]).actionPerformed(e);
                        }
                    }
                    firingActionEvent = false;
                }
            }
        }

        private class MyFocusListener implements FocusListener {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                    IntegerInplaceEditor.this.setActionCommand(InplaceEditor.COMMAND_SUCCESS);
                    IntegerInplaceEditor.this.fireActionEvent();
            }

        }
    }
}
