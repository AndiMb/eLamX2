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

import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.calculation.dmatrix.DMatrixPanel;
import de.elamx.clt.plate.BucklingInput;
import de.elamx.clt.plate.dmatrix.DMatrixService;
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
import java.util.Collection;
import javax.swing.DefaultComboBoxModel;
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
import org.openide.util.Lookup;
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
    private final BucklingModuleData data;
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
        this.data = data;
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
            setDMatrixComboBox();
            dMatrixComboBox.setSelectedItem(input.getDMatrixService());
            dMatrixComboBox.addItemListener(this);
            termsSpinnerX.setValue(input.getM());
            termsSpinnerX.addChangeListener(this);
            termsSpinnerY.setValue(input.getN());
            termsSpinnerY.addChangeListener(this);
        }
    }
    
    private void setDMatrixComboBox() {
        Collection<? extends DMatrixService> dMatrixServices = Lookup.getDefault().lookupAll(DMatrixService.class);
        DefaultComboBoxModel<DMatrixService> dMatModel = new DefaultComboBoxModel<>(dMatrixServices.toArray(DMatrixService[]::new));
        dMatrixComboBox.setModel(dMatModel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Integer[] intArray = new Integer[boundary_cond.length];
        for (int i = 0; i < boundary_cond.length; i++) {
            intArray[i] = i;
        }
        bcxComboBox = new ImageJComboBox(intArray);
        bcxComboBox.setRenderer(new ComboBoxRenderer());
        bcyComboBox = new ImageJComboBox(intArray);
        bcyComboBox.setRenderer(new ComboBoxRenderer());
        termsSpinnerX = new javax.swing.JSpinner();
        termsSpinnerY = new javax.swing.JSpinner();
        lengthField = new javax.swing.JFormattedTextField();
        widthField = new javax.swing.JFormattedTextField();
        nxField = new javax.swing.JFormattedTextField();
        nyField = new javax.swing.JFormattedTextField();
        nxyField = new javax.swing.JFormattedTextField();
        DmatrixOptionsPanel = new javax.swing.JPanel();
        showDMatrixButton = new javax.swing.JButton();
        dMatrixComboBox = new javax.swing.JComboBox<>();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel9.text")); // NOI18N

        termsSpinnerX.setModel(new javax.swing.SpinnerNumberModel(10, 1, 20, 1));

        termsSpinnerY.setModel(new javax.swing.SpinnerNumberModel(10, 1, 20, 1));

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

        org.openide.awt.Mnemonics.setLocalizedText(showDMatrixButton, org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.showDMatrixButton.text")); // NOI18N
        showDMatrixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showDMatrixButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DmatrixOptionsPanelLayout = new javax.swing.GroupLayout(DmatrixOptionsPanel);
        DmatrixOptionsPanel.setLayout(DmatrixOptionsPanelLayout);
        DmatrixOptionsPanelLayout.setHorizontalGroup(
            DmatrixOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DmatrixOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DmatrixOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dMatrixComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showDMatrixButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        DmatrixOptionsPanelLayout.setVerticalGroup(
            DmatrixOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DmatrixOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dMatrixComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showDMatrixButton)
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
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(nxyField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nyField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nxField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bcyComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bcxComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(widthField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lengthField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(termsSpinnerX)
                            .addComponent(termsSpinnerY)))
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
                    .addComponent(termsSpinnerX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(termsSpinnerY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(DmatrixOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel8.AccessibleContext.accessibleName")); // NOI18N
        jLabel8.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel8.AccessibleContext.accessibleDescription")); // NOI18N
        jLabel9.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel9.AccessibleContext.accessibleName")); // NOI18N
        jLabel9.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel9.AccessibleContext.accessibleDescription")); // NOI18N
        DmatrixOptionsPanel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.DmatrixOptionsPanel.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(InputPanel.class, "InputPanel.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void showDMatrixButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDMatrixButtonActionPerformed
        DMatrixService dMatServ = dMatrixComboBox.getItemAt(dMatrixComboBox.getSelectedIndex());
        
        double [][] dmat = dMatServ.getDMatrix(data.getLaminat().getLookup().lookup(CLT_Laminate.class));
        
        NotifyDescriptor nd = new NotifyDescriptor(
                new DMatrixPanel(dmat, dMatServ.getName(), dMatServ.getShortName()),
                NbBundle.getBundle(InputPanel.class).getString("OpenDMatAction.Title"), 
                NotifyDescriptor.DEFAULT_OPTION, 
                NotifyDescriptor.PLAIN_MESSAGE, 
                new Object[] { NotifyDescriptor.OK_OPTION }, 
                NotifyDescriptor.OK_OPTION);
        DialogDisplayer.getDefault().notify(nd);
    }//GEN-LAST:event_showDMatrixButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel DmatrixOptionsPanel;
    private javax.swing.JComboBox<Integer> bcxComboBox;
    private javax.swing.JComboBox<Integer> bcyComboBox;
    private javax.swing.JComboBox<DMatrixService> dMatrixComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JFormattedTextField lengthField;
    private javax.swing.JFormattedTextField nxField;
    private javax.swing.JFormattedTextField nxyField;
    private javax.swing.JFormattedTextField nyField;
    private javax.swing.JButton showDMatrixButton;
    private javax.swing.JSpinner termsSpinnerX;
    private javax.swing.JSpinner termsSpinnerY;
    private javax.swing.JFormattedTextField widthField;
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
        } else if (o == dMatrixComboBox) {
            input.setDMatrixService(dMatrixComboBox.getItemAt(dMatrixComboBox.getSelectedIndex()));
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        input.setM(((Number) termsSpinnerX.getValue()).intValue());
        input.setN(((Number) termsSpinnerY.getValue()).intValue());
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

    public static String getBoundaryConditionString(int bc){
        return boundary_cond[bc];
    }
}
