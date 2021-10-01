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
package de.elamx.clt.plateui.buckling.optimizationservices;

import de.elamx.clt.plate.Input;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import static javax.swing.SwingConstants.CENTER;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.nodes.PropertySupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class BoundaryConditionPropertyEditorSupport extends PropertySupport.ReadWrite<Integer> {

    public static final int BOUND_BX = 0;
    public static final int BOUND_BY = 1;

    private static final String[] BOUNDARY_COND = new String[]{"SS", "CC", "CF", "FF", "SC", "SF"};

    private final Input input;

    private final ImageIcon[] bc_icons;

    boolean showHtml = Boolean.getBoolean("nb.useSwingHtmlRendering");

    private int bound = BOUND_BX;

    public BoundaryConditionPropertyEditorSupport(Input input, int bound) {
        super(bound == BOUND_BX ? Input.PROP_BCX : Input.PROP_BCY,
                Integer.class,
                NbBundle.getMessage(BoundaryConditionPropertyEditorSupport.class, bound == BOUND_BX ? "Input.bx" : "Input.by"),
                NbBundle.getMessage(BoundaryConditionPropertyEditorSupport.class, bound == BOUND_BX ? "Input.bx.description" : "Input.by.description"));
        this.input = input;
        this.bound = bound;
        bc_icons = new ImageIcon[BOUNDARY_COND.length];
        Image image;
        for (int i = 0; i < BOUNDARY_COND.length; i++) {
            image = ImageUtilities.loadImage("de/elamx/clt/plateui/resources/" + BOUNDARY_COND[i] + ".png");
            if (image != null) {
                bc_icons[i] = new ImageIcon(image);
                bc_icons[i].setDescription(BOUNDARY_COND[i]);
            }
        }
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new UseStrainPropertyEditorSupport(input);
    }

    @Override
    public Integer getValue() throws IllegalAccessException, InvocationTargetException {
        return bound == BOUND_BX ? input.getBcx() : input.getBcy();
    }

    @Override
    public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (bound == BOUND_BX) {
            input.setBcx(val);
        } else {
            input.setBcy(val);
        }
    }

    private class UseStrainPropertyEditorSupport extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

        private final Input input;

        private UseStrainPropertyEditorSupport(Input mmMaterial) {
            this.input = mmMaterial;
        }

        @Override
        public String getAsText() {
            return bound == BOUND_BX ? BOUNDARY_COND[input.getBcx()] : BOUNDARY_COND[input.getBcy()];
        }

        @Override
        public void attachEnv(PropertyEnv env) {
            env.registerInplaceEditorFactory(this);
        }
        private InplaceEditor ed = null;

        @Override
        public InplaceEditor getInplaceEditor() {
            if (ed == null) {
                ed = new Inplace(input);
            }
            return ed;
        }
    }

    private class Inplace extends JComboBox<Integer> implements InplaceEditor {

        private String type;
        private boolean layingOut = false;
        private final int widestLengh = 150;

        private PropertyEditor pEditor = null;
        private final Input input;
        private final ItemListener myItemList = new MyItemListener();
        private boolean connecting = false;

        private Inplace(final Input input) {
            this.input = input;
            for (int ii = 0; ii < BOUNDARY_COND.length; ii++) {
                addItem(ii);
            }
            if (bound == BOUND_BX){
            this.setSelectedIndex(input.getBcx());
            }else{
            this.setSelectedIndex(input.getBcy());
            }
            addItemListener(myItemList);
            setRenderer(new ComboBoxRenderer());
        }

        @Override
        public Dimension getSize() {
            Dimension dim = super.getSize();
            if (!layingOut) {
                dim.width = Math.max(widestLengh, dim.width);
            }
            return dim;
        }

        @Override
        public void doLayout() {
            try {
                layingOut = true;
                super.doLayout();
            } finally {
                layingOut = false;
            }
        }

        public String getType() {
            return type;
        }

        public void setType(String t) {
            type = t;
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            connecting = true;
            pEditor = propertyEditor;
            connecting = false;
        }

        @Override
        public JComponent getComponent() {
            return this;
        }

        @Override
        public void clear() {
            pEditor = null;
            model = null;
        }

        @Override
        public Object getValue() {
            ((JComponent) getParent()).requestFocus();
            return getSelectedIndex();
        }

        @Override
        public void setValue(Object object) {
            setSelectedItem(object);
            if (bound == BOUND_BX){
            input.setBcx(this.getSelectedIndex());
            }else{
            input.setBcy(this.getSelectedIndex());
            }
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
            return pEditor;
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
            if (connecting || pEditor == null) {
                return;
            } else {
                if (pEditor == null) {
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

    class ComboBoxRenderer extends JLabel
            implements ListCellRenderer<Integer> {

        private Font uhOhFont;

        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        @Override
        public Component getListCellRendererComponent(
                JList<? extends Integer> list,
                Integer value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)
            int selectedIndex = value;

            //Set the icon and text.  If icon was null, say so.
            ImageIcon icon = bc_icons[selectedIndex];
            String pet = BOUNDARY_COND[selectedIndex];

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());

            }

            setIcon(icon);
            if (index == -1) {
                setIcon(null);
            }
            if (icon != null) {
                setText(pet);
                setFont(list.getFont());
            } else {
                setUhOhText(pet + " (no image)",
                        list.getFont());
            }
            this.setHorizontalAlignment(JLabel.LEFT);

            return this;
        }

        //Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { //lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            setFont(uhOhFont);
            setText(uhOhText);
        }
    }
}
