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

import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import de.elamx.clt.plate.BucklingInput;
import de.elamx.clt.plate.BucklingResult;
import de.elamx.clt.plate.view3d.BucklingPlate;
import de.elamx.core.GlobalProperties;
import de.view3d.View3D;
import de.view3d.View3DProperties;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author Andreas Hauffe
 */
public class ResultPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private final BucklingModuleData data;
    private final View3D view3D;
    private final static DecimalFormat DF = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);
    private final static DecimalFormat DFEIGVAL = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_EIGENVALUE);
    private BucklingPlate plate;
    private View3D.AdditionalGeometryButton bcButton;

    public ResultPanel() {
        this(null, null);
    }

    /**
     * Creates new form ResultPanel
     */
    public ResultPanel(BucklingModuleData data, View3D view3D) {
        this.data = data;
        this.view3D = view3D;
        if (view3D != null) {
            this.view3D.addAdditionalButtonBar(getToolBar(view3D));
        }
        if (data != null) {
            this.data.addPropertyChangeListener(BucklingModuleData.PROP_RESULT, this);
        }
        View3DProperties.getDefault().addPropertyChangeListener(this);
        initComponents();
    }

    private JToolBar getToolBar(View3D view3D) {
        JToolBar bar = new JToolBar();

        bcButton = new View3D.AdditionalGeometryButton(view3D);
        bcButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bcButton.setIcon(ImageUtilities.loadImageIcon("de/elamx/clt/plateui/resources/force.png", false));
        bcButton.setToolTipText(NbBundle.getMessage(ResultPanel.class, "forceButton.tip"));
        bcButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                NbPreferences.forModule(ResultPanel.class).putBoolean("Buckling.ResultPanel.bcButton.selected", bcButton.isSelected());
            } 
        });
        bcButton.setSelected(NbPreferences.forModule(ResultPanel.class).getBoolean("Buckling.ResultPanel.bcButton.selected", true));

        bar.add(bcButton);
        
        final JToggleButton legendButton = new JToggleButton();
        legendButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        legendButton.setIcon(ImageUtilities.loadImageIcon("de/elamx/clt/plateui/resources/legend24.png", false));
        legendButton.setToolTipText(NbBundle.getMessage(ResultPanel.class, "legendButton.tip"));
        legendButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ResultPanel.this.view3D.setShowHud(legendButton.isSelected());
                NbPreferences.forModule(ResultPanel.class).putBoolean("Buckling.ResultPanel.legendButton.selected", legendButton.isSelected());
            } 
        });
        legendButton.setSelected(NbPreferences.forModule(ResultPanel.class).getBoolean("Buckling.ResultPanel.legendButton.selected", true));
        this.view3D.setShowHud(legendButton.isSelected());
        
        bar.add(legendButton);
        return bar;
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
        eigenvalueComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        zScaleSpinner = new javax.swing.JSpinner();
        nxLabel = new javax.swing.JLabel();
        nyLabel = new javax.swing.JLabel();
        nxyLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.jLabel5.text")); // NOI18N

        eigenvalueComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                eigenvalueComboBoxItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.jLabel6.text")); // NOI18N

        zScaleSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.1d, 50.0d, 0.1d));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(zScaleSpinner);
        DecimalFormat format = editor.getFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(GlobalProperties.getDefault().getActualLocale()));
        zScaleSpinner.setEditor(editor);
        zScaleSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zScaleSpinnerStateChanged(evt);
            }
        });

        nxLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(nxLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.nxLabel.text")); // NOI18N
        nxLabel.setToolTipText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.nxLabel.toolTipText")); // NOI18N
        nxLabel.setText(DF.format(0.0));

        nyLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(nyLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.nyLabel.text")); // NOI18N
        nyLabel.setText(DF.format(0.0));

        nxyLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(nxyLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.nxyLabel.text")); // NOI18N
        nxyLabel.setText(DF.format(0.0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zScaleSpinner)
                    .addComponent(eigenvalueComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nxyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nxLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nxLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nxyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eigenvalueComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(zScaleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void eigenvalueComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_eigenvalueComboBoxItemStateChanged
        updateView3D(false);
    }//GEN-LAST:event_eigenvalueComboBoxItemStateChanged

    private void zScaleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zScaleSpinnerStateChanged
        view3D.setZScale(((Number) zScaleSpinner.getValue()).doubleValue());
    }//GEN-LAST:event_zScaleSpinnerStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> eigenvalueComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel nxLabel;
    private javax.swing.JLabel nxyLabel;
    private javax.swing.JLabel nyLabel;
    private javax.swing.JSpinner zScaleSpinner;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (data == null) {
            return;
        }
        BucklingResult result = data.getResult();
        if (result == null) {
        } else {
            if (!evt.getPropertyName().equals(View3DProperties.PROP_NETQUALITY)) {
                double[] ncrit = result.getN_crit();
                nxLabel.setText(DF.format(ncrit[0]));
                nyLabel.setText(DF.format(ncrit[1]));
                nxyLabel.setText(DF.format(ncrit[2]));
                DefaultComboBoxModel<String> cModel = new DefaultComboBoxModel<>();
                double[] eigenvalues = result.getEigenvalues_();
                for (int ii = 0; ii < eigenvalues.length; ii++) {
                    cModel.addElement("" + (ii + 1) + ". " + DFEIGVAL.format(eigenvalues[ii]));
                }
                eigenvalueComboBox.setModel(cModel);
                plate = new BucklingPlate( (BucklingInput)data.getResult().getInput(), data.getResult());
            }
            updateView3D(true);
        }
    }

    private void updateView3D(boolean reinit) {
        if (plate == null || view3D == null || data == null || data.getResult() == null) {
            return;
        }
        plate.setEigenvectorNumber(eigenvalueComboBox.getSelectedIndex());
        List<Mesh> shapes = plate.getShapes(reinit);
        view3D.setShape3D(shapes, 1.0);
        //view3D.setAdditionalGeometry(plate.getUndeformedWithBC());
        Node group = new Node();
        for (Node g : plate.getUndeformedWithBC()) {
            group.attachChild(g);
        }
        bcButton.setGeo(group);

        BucklingInput input = (BucklingInput)data.getResult().getInput();
        
        String[] boundary_cond = new String[]{"SS", "CC", "CF", "FF", "SC", "SF"};
        
        JLabel l = new JLabel("<html><b>" + NbBundle.getMessage(ResultPanel.class, "CTL_BucklingTopComponent") + "</b><br>"+
                NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel1.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS).format(input.getLength()) + "<br>" +
                NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel2.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS).format(input.getWidth()) + "<br>" + 
                NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel3.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + boundary_cond[input.getBcx()] + "<br>" + 
                NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel4.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + boundary_cond[input.getBcy()] + "<br>" + 
                NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel5.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE).format(input.getNx()) + "<br>" +
                NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel6.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " +  GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE).format(input.getNy()) + "<br>" +
                NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel7.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " +  GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE).format(input.getNxy()) + "<br>" +
                NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel8.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + input.getM() + "<br>" +
                NbBundle.getMessage(ResultPanel.class, "ResultPanel.jLabel5.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + DFEIGVAL.format(data.getResult().getEigenvalues_()[eigenvalueComboBox.getSelectedIndex()]) + "<br>" +
                "</html>");
        l.setFont(nxLabel.getFont());
        //Border used as padding 
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), paddingBorder));
        l.setSize(l.getPreferredSize());

        BufferedImage img = new BufferedImage(
                l.getWidth(),
                l.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        l.paint(img.getGraphics());

        view3D.setHUDImage(img);
    }

    public void cleanup() {
        if (data != null) {
            this.data.removePropertyChangeListener(BucklingModuleData.PROP_RESULT, this);
        }
        View3DProperties.getDefault().removePropertyChangeListener(this);
    }
}
