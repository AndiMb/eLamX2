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
package de.elamx.clt.optimizationui;

import de.elamx.core.ELamXDecimalFormat;
import de.elamx.core.GlobalProperties;
import de.elamx.core.LaminateStringParser;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.Puck;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

public final class OptimizationVisualPanel1 extends JPanel {

    public final static int INC_45 = 0;
    public final static int INC_15 = 1;
    public final static int INC_10 = 2;
    public final static int INC_5 = 4;
    public final static int INC_USER = 8;

    private final Lookup.Result<LayerMaterial> materialResult;
    private final Lookup.Result<Criterion> failureResult;
    
    private static final DecimalFormat thicknessFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);

    /**
     * Creates new form OptimizationVisualPanel1
     */
    public OptimizationVisualPanel1() {
        initComponents();
        materialResult = eLamXLookup.getDefault().lookupResult(LayerMaterial.class);
        Lookup lkp = Lookups.forPath("elamx/failurecriteria");
        failureResult = lkp.lookupResult(Criterion.class);

        updateMaterialComboBox();
        updateFailureComboBox();
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.name");
    }

    private void updateMaterialComboBox() {
        ArrayList<LayerMaterial> materials = new ArrayList<>();
        for (LayerMaterial mat : materialResult.allInstances()) {
            materials.add(mat);
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(materials, new Comparator<LayerMaterial>() {
            @Override
            public int compare(LayerMaterial o1, LayerMaterial o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        DefaultComboBoxModel<LayerMaterial> materialModel = new DefaultComboBoxModel<>(materials.toArray(new LayerMaterial[materials.size()]));
        materialComboBox.setModel(materialModel);
    }

    private void updateFailureComboBox() {
        ArrayList<Criterion> criteria = new ArrayList<>();
        for (Criterion crit : failureResult.allInstances()) {
            criteria.add(crit);
        }
        // Sortieren der Liste nach den Name der Versagenskriterien
        Collections.sort(criteria, new Comparator<Criterion>() {
            @Override
            public int compare(Criterion o1, Criterion o2) {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
            }
        });
        Object selectedItem = failureComboBox.getSelectedItem();
        if (selectedItem == null) {
            selectedItem = Lookups.forPath("elamx/failurecriteria").lookup(Puck.class);
        }
        DefaultComboBoxModel<Criterion> critModel = new DefaultComboBoxModel<>(criteria.toArray(new Criterion[criteria.size()]));
        failureComboBox.setModel(critModel);
        failureComboBox.setSelectedItem(selectedItem);
    }

    public Double getThickness() {
        ParsePosition pos = new ParsePosition(0);
        double thickness = thicknessFormat.parse(thicknessField.getText(), pos).doubleValue();
        if (pos.getErrorIndex() != -1 || pos.getIndex() != thicknessField.getText().length()) {
            return null;
        }
        return thickness;
    }

    public void setThickness(Double thickness) {
        thicknessField.setValue(thickness);
    }

    public LayerMaterial getMaterial() {
        return (LayerMaterial) materialComboBox.getSelectedItem();
    }

    public void setMaterial(LayerMaterial material) {
        materialComboBox.setSelectedItem(material);
    }

    public Criterion getCriterion() {
        return (Criterion) failureComboBox.getSelectedItem();
    }

    public void setCriterion(Criterion criterion) {
        failureComboBox.setSelectedItem(criterion);
    }

    public int getAngleType() {
        if (inc45RadioButton.isSelected()) {
            return INC_45;
        } else if (inc15RadioButton.isSelected()) {
            return INC_15;
        } else if (inc10RadioButton.isSelected()) {
            return INC_10;
        } else if (inc5RadioButton.isSelected()) {
            return INC_5;
        } else if (userDefinedRadioButton.isSelected()) {
            return INC_USER;
        }
        return -1;
    }

    public void setAngleType(int type) {
        if (type == INC_45) {
            inc45RadioButton.setSelected(true);
        } else if (type == INC_15) {
            inc15RadioButton.setSelected(true);
        } else if (type == INC_10) {
            inc10RadioButton.setSelected(true);
        } else if (type == INC_5) {
            inc5RadioButton.setSelected(true);
        } else if (type == INC_USER) {
            userDefinedRadioButton.setSelected(true);
        }
    }

    public double[] getAngles() {
        final double[] angles;
        if (userDefinedRadioButton.isSelected()) {
            angles = LaminateStringParser.parseStackingSequence(anglesField.getText());
        } else if (inc45RadioButton.isSelected()) {
            angles = new double[]{-45.0, 0.0, 45.0, 90.0};
        } else if (inc15RadioButton.isSelected()) {
            angles = new double[]{-75.0, -60.0, -45.0, -30.0, -15.0, 0.0, 15.0, 30.0, 45.0, 60.0, 75.0, 90.0};
        } else if (inc10RadioButton.isSelected()) {
            angles = new double[]{-80.0, -70.0, -60.0, -50.0, -40.0, -30.0, -20.0, -10.0, 0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0};
        } else if (inc5RadioButton.isSelected()) {
            angles = new double[]{-85.0, -80.0, -75.0, -70.0, -65.0, -60.0, -55.0, -50.0, -45.0, -40.0, -35.0, -30.0, -25.0, -20.0, -15.0, -10.0, -5.0,
                0.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0, 55.0, 60.0, 65.0, 70.0, 75.0, 80.0, 85.0, 90.0};
        } else {
            angles = null;
        }
        return angles;
    }

    public void setAngles(double angles[]) {
        ELamXDecimalFormat df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE);

        StringBuilder sb = new StringBuilder();
        if (angles.length > 0) {
            sb.append(df.format(angles[0]));
            for (int ii = 1; ii < angles.length; ii++) {
                sb.append("/");
                sb.append(df.format(angles[ii]));
            }
        }

        anglesField.setText(sb.toString());
    }

    public boolean getSymmetry() {
        return symmetryCheckBox.isSelected();
    }
    
    public void setSymmetry(boolean symmetry){
        symmetryCheckBox.setSelected(symmetry);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        anglesButtonGroup = new javax.swing.ButtonGroup();
        materialLabel = new javax.swing.JLabel();
        materialComboBox = new javax.swing.JComboBox<>();
        failureLabel = new javax.swing.JLabel();
        failureComboBox = new javax.swing.JComboBox<>();
        thicknessLabel = new javax.swing.JLabel();
        thicknessField = new javax.swing.JFormattedTextField();
        anglesLabel = new javax.swing.JLabel();
        userDefinedRadioButton = new javax.swing.JRadioButton();
        anglesField = new javax.swing.JTextField();
        inc45RadioButton = new javax.swing.JRadioButton();
        inc10RadioButton = new javax.swing.JRadioButton();
        inc15RadioButton = new javax.swing.JRadioButton();
        inc5RadioButton = new javax.swing.JRadioButton();
        symmetricLaminatLabel = new javax.swing.JLabel();
        symmetryCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(materialLabel, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.materialLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(failureLabel, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.failureLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(thicknessLabel, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.thicknessLabel.text")); // NOI18N

        thicknessField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(thicknessFormat)));
        thicknessField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        thicknessField.setText(org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.thicknessField.text")); // NOI18N
        thicknessField.setValue(0.125);

        org.openide.awt.Mnemonics.setLocalizedText(anglesLabel, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.anglesLabel.text")); // NOI18N

        anglesButtonGroup.add(userDefinedRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(userDefinedRadioButton, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.userDefinedRadioButton.text")); // NOI18N

        anglesField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        anglesField.setText(org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.anglesField.text")); // NOI18N

        anglesButtonGroup.add(inc45RadioButton);
        inc45RadioButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(inc45RadioButton, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.inc45RadioButton.text")); // NOI18N

        anglesButtonGroup.add(inc10RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(inc10RadioButton, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.inc10RadioButton.text")); // NOI18N

        anglesButtonGroup.add(inc15RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(inc15RadioButton, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.inc15RadioButton.text")); // NOI18N

        anglesButtonGroup.add(inc5RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(inc5RadioButton, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.inc5RadioButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(symmetricLaminatLabel, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.symmetricLaminatLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(symmetryCheckBox, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.symmetryCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(302, 302, 302)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inc10RadioButton)
                            .addComponent(inc5RadioButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(materialLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(failureLabel)
                                .addComponent(thicknessLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(anglesLabel, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(symmetricLaminatLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(thicknessField)
                            .addComponent(failureComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(materialComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(userDefinedRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(anglesField))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(symmetryCheckBox)
                                    .addComponent(inc45RadioButton)
                                    .addComponent(inc15RadioButton))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(materialLabel)
                    .addComponent(materialComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(failureLabel)
                    .addComponent(failureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thicknessLabel)
                    .addComponent(thicknessField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anglesLabel)
                    .addComponent(userDefinedRadioButton)
                    .addComponent(anglesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inc45RadioButton)
                    .addComponent(inc10RadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inc15RadioButton)
                    .addComponent(inc5RadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(symmetryCheckBox)
                    .addComponent(symmetricLaminatLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup anglesButtonGroup;
    private javax.swing.JTextField anglesField;
    private javax.swing.JLabel anglesLabel;
    private javax.swing.JComboBox<Criterion> failureComboBox;
    private javax.swing.JLabel failureLabel;
    private javax.swing.JRadioButton inc10RadioButton;
    private javax.swing.JRadioButton inc15RadioButton;
    private javax.swing.JRadioButton inc45RadioButton;
    private javax.swing.JRadioButton inc5RadioButton;
    private javax.swing.JComboBox<LayerMaterial> materialComboBox;
    private javax.swing.JLabel materialLabel;
    private javax.swing.JLabel symmetricLaminatLabel;
    private javax.swing.JCheckBox symmetryCheckBox;
    private javax.swing.JFormattedTextField thicknessField;
    private javax.swing.JLabel thicknessLabel;
    private javax.swing.JRadioButton userDefinedRadioButton;
    // End of variables declaration//GEN-END:variables

}
