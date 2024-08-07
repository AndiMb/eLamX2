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
package de.elamx.clt.plateui.stiffenerui.wizard;

import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.core.GlobalProperties;
import java.text.DecimalFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Andreas Hauffe
 */
public class GeneralStiffenerInputPanel extends javax.swing.JPanel {
    
    private static final DecimalFormat lengthFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);

    /**
     * Creates new form GeneralStiffenerInputPanel
     */
    @SuppressWarnings("this-escape")
    public GeneralStiffenerInputPanel() {
        initComponents();
        directionComboBox.setModel(new DefaultComboBoxModel<>(new String[] { StiffenerProperties.X_DIRECTION_STRING, StiffenerProperties.Y_DIRECTION_STRING }));
    }
    
    public void setStiffenerName(String name){
        nameField.setText(name);
    }
    
    public String getStiffenerName(){
        return nameField.getText();
    }
    
    public void setDirection(int dir){
        if (dir == StiffenerProperties.X_DIRECTION){
            directionComboBox.setSelectedIndex(0);
        }else{
            directionComboBox.setSelectedIndex(1);
        }
        
    }
    
    public int getDirection(){
        if (directionComboBox.getSelectedIndex() == 0){
            return StiffenerProperties.X_DIRECTION;
        }else{
            return StiffenerProperties.Y_DIRECTION;
        }
    }
    
    public void setPosition(Number pos){
        positionField.setValue(pos);
    }
    
    public Number getPosition(){
        return (Number)positionField.getValue();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        directionLabel = new javax.swing.JLabel();
        positionLabel = new javax.swing.JLabel();
        directionComboBox = new javax.swing.JComboBox<>();
        nameField = new javax.swing.JTextField();
        positionField = new javax.swing.JFormattedTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GeneralStiffenerInputPanel.class, "GeneralStiffenerInputPanel.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(GeneralStiffenerInputPanel.class, "GeneralStiffenerInputPanel.nameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(directionLabel, org.openide.util.NbBundle.getMessage(GeneralStiffenerInputPanel.class, "GeneralStiffenerInputPanel.directionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(positionLabel, org.openide.util.NbBundle.getMessage(GeneralStiffenerInputPanel.class, "GeneralStiffenerInputPanel.positionLabel.text")); // NOI18N

        nameField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        nameField.setText(org.openide.util.NbBundle.getMessage(GeneralStiffenerInputPanel.class, "GeneralStiffenerInputPanel.nameField.text")); // NOI18N

        positionField.setColumns(10);
        positionField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(lengthFormat)));
        positionField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        positionField.setText(org.openide.util.NbBundle.getMessage(GeneralStiffenerInputPanel.class, "GeneralStiffenerInputPanel.positionField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(directionLabel)
                    .addComponent(nameLabel)
                    .addComponent(positionLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(positionField)
                    .addComponent(nameField)
                    .addComponent(directionComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(directionLabel)
                    .addComponent(directionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(positionLabel)
                    .addComponent(positionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> directionComboBox;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JFormattedTextField positionField;
    private javax.swing.JLabel positionLabel;
    // End of variables declaration//GEN-END:variables
}
