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
package de.elamx.clt.plateui.buckling;

import de.elamx.clt.plate.BucklingInput;
import de.elamx.core.GlobalProperties;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class InputPanel extends javax.swing.JPanel implements ChangeListener, PropertyChangeListener, ItemListener {

    private static final String[] boundary_cond = new String[]{"SS", "CC", "CF", "FF", "SC", "SF"};
    private static final DecimalFormat lengthFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);
    private static final DecimalFormat forceFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);
    private ImageIcon[] bc_icons;
    private final BucklingInput input;

    public InputPanel() {
        this(null);
    }

    /**
     * Creates new form InputPanel
     */
    @SuppressWarnings("this-escape")
    public InputPanel(BucklingModuleData data) {
        bc_icons = new ImageIcon[boundary_cond.length];
        Image image;
        for (int i = 0; i < boundary_cond.length; i++) {
            image = ImageUtilities.loadImage("de/elamx/clt/plateui/resources/" + boundary_cond[i] + ".png");
            if (image != null) {
                bc_icons[i] = new ImageIcon(image);
                bc_icons[i].setDescription(boundary_cond[i]);
            }
        }
        initComponents();
        this.input = data != null ? data.getBucklingInput() : null;

        if (input != null) {
            lengthField.setValue(input.getLength());
            lengthField.addPropertyChangeListener("value", this);
            widthField.setValue(input.getWidth());
            widthField.addPropertyChangeListener("value", this);
            bcxComboBox.setSelectedIndex(input.getBcx());
            bcxComboBox.addItemListener(this);
            bcyComboBox.setSelectedIndex(input.getBcy());
            bcyComboBox.addItemListener(this);
            nxField.setValue(input.getNx());
            nxField.addPropertyChangeListener("value", this);
            nyField.setValue(input.getNy());
            nyField.addPropertyChangeListener("value", this);
            nxyField.setValue(input.getNxy());
            nxyField.addPropertyChangeListener("value", this);
            if (input.isWholeD() && (!input.isDtilde())) {
                DoriginalRadioButton.setSelected(true);
                woD16D26RadioButton.setSelected(false);
                DtildeRadioButton.setSelected(false);
            } else if ((!input.isWholeD()) && (!input.isDtilde())) {
                DoriginalRadioButton.setSelected(false);
                woD16D26RadioButton.setSelected(true);
                DtildeRadioButton.setSelected(false);
            } else if (input.isWholeD() && input.isDtilde()) {
                DoriginalRadioButton.setSelected(false);
                woD16D26RadioButton.setSelected(false);
                DtildeRadioButton.setSelected(true);
            } else {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(de.elamx.clt.plateui.buckling.InputPanel.class, "Warning.baddmatrixoptioncombination"), NotifyDescriptor.WARNING_MESSAGE));
                DoriginalRadioButton.setSelected(true);
                woD16D26RadioButton.setSelected(false);
                DtildeRadioButton.setSelected(false);
                input.setWholeD(true);
                input.setDtilde(false);
            }
            DoriginalRadioButton.addItemListener(this);
            woD16D26RadioButton.addItemListener(this);
            DtildeRadioButton.addItemListener(this);
            termsSpinner.setValue(input.getN());
            termsSpinner.addChangeListener(this);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        DmatrixOptionsButtonGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        Integer[] intArray = new Integer[boundary_cond.length];
        for (int i = 0; i < boundary_cond.length; i++) {
            intArray[i] = i;
        }
        bcxComboBox = new ImageJComboBox(intArray);
        bcxComboBox.setRenderer(new ComboBoxRenderer());
        bcyComboBox = new ImageJComboBox(intArray);
        bcyComboBox.setRenderer(new ComboBoxRenderer());
        termsSpinner = new javax.swing.JSpinner();
        lengthField = new javax.swing.JFormattedTextField();
        widthField = new javax.swing.JFormattedTextField();
        nxField = new javax.swing.JFormattedTextField();
        nyField = new javax.swing.JFormattedTextField();
        nxyField = new javax.swing.JFormattedTextField();
        DmatrixOptionsPanel = new javax.swing.JPanel();
        DoriginalRadioButton = new javax.swing.JRadioButton();
        woD16D26RadioButton = new javax.swing.JRadioButton();
        DtildeRadioButton = new javax.swing.JRadioButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel8.text")); // NOI18N

        termsSpinner.setModel(new javax.swing.SpinnerNumberModel(10, 1, 20, 1));

        lengthField.setColumns(8);
        lengthField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(lengthFormat)));
        lengthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        lengthField.setText(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.lengthField.text")); // NOI18N

        widthField.setColumns(8);
        widthField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(lengthFormat)));
        widthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        widthField.setText(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.widthField.text")); // NOI18N

        nxField.setColumns(8);
        nxField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(forceFormat)));
        nxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        nxField.setText(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.nxField.text")); // NOI18N

        nyField.setColumns(8);
        nyField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(forceFormat)));
        nyField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        nyField.setText(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.nyField.text")); // NOI18N

        nxyField.setColumns(8);
        nxyField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(forceFormat)));
        nxyField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        nxyField.setText(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.nxyField.text")); // NOI18N

        DmatrixOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.DmatrixOptionsPanel.border.title"))); // NOI18N

        DmatrixOptionsButtonGroup.add(DoriginalRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(DoriginalRadioButton, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.DoriginalRadioButton.text")); // NOI18N

        DmatrixOptionsButtonGroup.add(woD16D26RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(woD16D26RadioButton, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.woD16D26RadioButton.text")); // NOI18N

        DmatrixOptionsButtonGroup.add(DtildeRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(DtildeRadioButton, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.DtildeRadioButton.text")); // NOI18N

        javax.swing.GroupLayout DmatrixOptionsPanelLayout = new javax.swing.GroupLayout(DmatrixOptionsPanel);
        DmatrixOptionsPanel.setLayout(DmatrixOptionsPanelLayout);
        DmatrixOptionsPanelLayout.setHorizontalGroup(
            DmatrixOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DmatrixOptionsPanelLayout.createSequentialGroup()
                .addGroup(DmatrixOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DtildeRadioButton)
                    .addComponent(woD16D26RadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DoriginalRadioButton))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        DmatrixOptionsPanelLayout.setVerticalGroup(
            DmatrixOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DmatrixOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DoriginalRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(woD16D26RadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DtildeRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(nxyField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nyField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nxField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bcyComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bcxComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(widthField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lengthField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(termsSpinner)))
                    .addComponent(DmatrixOptionsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lengthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(widthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(bcxComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(bcyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nxyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(termsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(DmatrixOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        DmatrixOptionsPanel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.DmatrixOptionsPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup DmatrixOptionsButtonGroup;
    private javax.swing.JPanel DmatrixOptionsPanel;
    private javax.swing.JRadioButton DoriginalRadioButton;
    private javax.swing.JRadioButton DtildeRadioButton;
    private javax.swing.JComboBox<Integer> bcxComboBox;
    private javax.swing.JComboBox<Integer> bcyComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JFormattedTextField lengthField;
    private javax.swing.JFormattedTextField nxField;
    private javax.swing.JFormattedTextField nxyField;
    private javax.swing.JFormattedTextField nyField;
    private javax.swing.JSpinner termsSpinner;
    private javax.swing.JFormattedTextField widthField;
    private javax.swing.JRadioButton woD16D26RadioButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object o = evt.getSource();
        if (o == lengthField) {
            input.setLength(((Number) lengthField.getValue()).doubleValue());
        } else if (o == widthField) {
            input.setWidth(((Number) widthField.getValue()).doubleValue());
        } else if (o == nxField) {
            input.setNx(((Number) nxField.getValue()).doubleValue());
        } else if (o == nyField) {
            input.setNy(((Number) nyField.getValue()).doubleValue());
        } else if (o == nxyField) {
            input.setNxy(((Number) nxyField.getValue()).doubleValue());
        }
    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
        Object o = evt.getSource();
        if (o == bcxComboBox) {
            input.setBcx(bcxComboBox.getSelectedIndex());
        } else if (o == bcyComboBox) {
            input.setBcy(bcyComboBox.getSelectedIndex());
        } else if ((o == DoriginalRadioButton) || (o == woD16D26RadioButton) || (o == DtildeRadioButton)) {
            input.setWholeD(!woD16D26RadioButton.isSelected());
            input.setDtilde(DtildeRadioButton.isSelected());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        input.setM(((Number) termsSpinner.getValue()).intValue());
        input.setN(((Number) termsSpinner.getValue()).intValue());
    }

    private static class ImageJComboBox extends JComboBox<Integer> {

        private String type;
        private boolean layingOut = false;
        private int widestLengh = 150;

        public ImageJComboBox(Integer[] objs) {
            super(objs);
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
            String pet = boundary_cond[selectedIndex];


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
