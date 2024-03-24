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
package de.elamx.clt.plateui.deformation;

import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import de.elamx.clt.plate.DeformationInput;
import de.elamx.clt.plate.DeformationResult;
import de.elamx.clt.plate.view3d.DeformationPlate;
import de.elamx.clt.plateui.buckling.BucklingModuleData;
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

    private final DeformationModuleData data;
    private final View3D view3D;
    private DeformationPlate plate;
    private View3D.AdditionalGeometryButton bcButton;

    public ResultPanel() {
        this(null, null);
    }

    /**
     * Creates new form ResultPanel
     */
    public ResultPanel(DeformationModuleData data, View3D view3D) {
        this.data = data;
        this.view3D = view3D;
        if (view3D != null) {
            this.view3D.addAdditionalButtonBar(getToolBar(view3D));
        }
        if (data != null) {
            this.data.addPropertyChangeListener(DeformationModuleData.PROP_RESULT, this);
        }
        View3DProperties.getDefault().addPropertyChangeListener(this);
        initComponents();
        initComboBoxes();
        checkComboBoxes();
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
                NbPreferences.forModule(ResultPanel.class).putBoolean("Deformation.ResultPanel.bcButton.selected", bcButton.isSelected());
            } 
        });
        bcButton.setSelected(NbPreferences.forModule(ResultPanel.class).getBoolean("Deformation.ResultPanel.bcButton.selected", true));

        bar.add(bcButton);

        final JToggleButton legendButton = new JToggleButton();
        legendButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        legendButton.setIcon(ImageUtilities.loadImageIcon("de/elamx/clt/plateui/resources/legend24.png", false));
        legendButton.setToolTipText(NbBundle.getMessage(ResultPanel.class, "legendButton.tip"));
        legendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResultPanel.this.view3D.setShowHud(legendButton.isSelected());
                NbPreferences.forModule(ResultPanel.class).putBoolean("Deformation.ResultPanel.legendButton.selected", legendButton.isSelected());
            }
        });
        legendButton.setSelected(NbPreferences.forModule(de.elamx.clt.plateui.buckling.ResultPanel.class).getBoolean("Buckling.ResultPanel.legendButton.selected", true));
        this.view3D.setShowHud(legendButton.isSelected());

        bar.add(legendButton);

        return bar;
    }

    private void initComboBoxes() {
        String[] resultTypes = new String[]{
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultType.displacement_z"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultType.local_strain_x"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultType.local_strain_y"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultType.local_strain_xy"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultType.local_stress_x"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultType.local_stress_y"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultType.local_stress_xy"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultType.min_reserve_factor")
        };
        resultTypeComboBox.setModel(new DefaultComboBoxModel<>(resultTypes));

        String[] positions = new String[]{
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.position.upper"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.position.middle"),
            NbBundle.getMessage(ResultPanel.class, "ResultPanel.position.lower")
        };
        positionComboBox.setModel(new DefaultComboBoxModel<>(positions));

        updateLayerNumberComboBox();
    }

    private void updateLayerNumberComboBox() {
        if (data == null || data.getResult() == null) {
            layerNumberComboBox.setModel(new DefaultComboBoxModel<String>());
        } else {

            int layerNumber = data.getResult().getLaminate().getCLTLayers().length;
            String[] numbers = new String[layerNumber];
            for (int ii = 0; ii < layerNumber; ii++) {
                numbers[ii] = "" + (ii + 1);
            }
            layerNumberComboBox.setModel(new DefaultComboBoxModel<>(numbers));
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

        minMaxLabel = new javax.swing.JLabel();
        minLabel = new javax.swing.JLabel();
        maxLabel = new javax.swing.JLabel();
        resultTypeLabel = new javax.swing.JLabel();
        minValueLabel = new javax.swing.JLabel();
        maxValueLabel = new javax.swing.JLabel();
        resultTypeComboBox = new javax.swing.JComboBox<>();
        zScalingLabel = new javax.swing.JLabel();
        zScaleSpinner = new javax.swing.JSpinner();
        layerNumberLabel = new javax.swing.JLabel();
        layerNumberComboBox = new javax.swing.JComboBox<>();
        PositionLabel = new javax.swing.JLabel();
        positionComboBox = new javax.swing.JComboBox<>();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(minMaxLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.minMaxLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(minLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.minLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(maxLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.maxLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(resultTypeLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultTypeLabel.text")); // NOI18N

        minValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(minValueLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.minValueLabel.text")); // NOI18N
        minValueLabel.setMaximumSize(new java.awt.Dimension(50, 100));
        minValueLabel.setPreferredSize(new java.awt.Dimension(50, 15));

        maxValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(maxValueLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.maxValueLabel.text")); // NOI18N
        maxValueLabel.setMaximumSize(new java.awt.Dimension(50, 100));
        maxValueLabel.setPreferredSize(new java.awt.Dimension(50, 15));

        resultTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                resultTypeComboBoxItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(zScalingLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.zScalingLabel.text")); // NOI18N

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

        org.openide.awt.Mnemonics.setLocalizedText(layerNumberLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.layerNumberLabel.text")); // NOI18N

        layerNumberComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                layerNumberComboBoxItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(PositionLabel, org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.PositionLabel.text")); // NOI18N

        positionComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                positionComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minMaxLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultTypeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(minLabel)
                            .addComponent(maxLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(minValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(resultTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zScalingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zScaleSpinner)
                    .addComponent(layerNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(layerNumberComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PositionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(positionComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(minMaxLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minLabel)
                    .addComponent(minValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxLabel)
                    .addComponent(maxValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(layerNumberLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(layerNumberComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PositionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(positionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(zScalingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(zScaleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void zScaleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zScaleSpinnerStateChanged
        view3D.setZScale(((Number) zScaleSpinner.getValue()).doubleValue());
    }//GEN-LAST:event_zScaleSpinnerStateChanged

    private void resultTypeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_resultTypeComboBoxItemStateChanged
        updateView3D(false);
    }//GEN-LAST:event_resultTypeComboBoxItemStateChanged

    private void layerNumberComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_layerNumberComboBoxItemStateChanged
        updateView3D(false);
    }//GEN-LAST:event_layerNumberComboBoxItemStateChanged

    private void positionComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_positionComboBoxItemStateChanged
        updateView3D(false);
    }//GEN-LAST:event_positionComboBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel PositionLabel;
    private javax.swing.JComboBox<String> layerNumberComboBox;
    private javax.swing.JLabel layerNumberLabel;
    private javax.swing.JLabel maxLabel;
    private javax.swing.JLabel maxValueLabel;
    private javax.swing.JLabel minLabel;
    private javax.swing.JLabel minMaxLabel;
    private javax.swing.JLabel minValueLabel;
    private javax.swing.JComboBox<String> positionComboBox;
    private javax.swing.JComboBox<String> resultTypeComboBox;
    private javax.swing.JLabel resultTypeLabel;
    private javax.swing.JSpinner zScaleSpinner;
    private javax.swing.JLabel zScalingLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        checkComboBoxes();
        if (data == null) {
            return;
        }
        DeformationResult result = data.getResult();
        if (result == null) {
        } else {
            if (!evt.getPropertyName().equals(View3DProperties.PROP_NETQUALITY)) {
                plate = new DeformationPlate((DeformationInput)data.getResult().getInput(), data.getResult());
            }
            updateLayerNumberComboBox();
            updateView3D(true);
        }
    }

    private void checkComboBoxes() {
        if (data == null || data.getResult() == null) {
            resultTypeComboBox.setEnabled(false);
            layerNumberComboBox.setEnabled(false);
            positionComboBox.setEnabled(false);
        } else {
            resultTypeComboBox.setEnabled(true);
            if (resultTypeComboBox.getSelectedIndex() == 0) {
                layerNumberComboBox.setEnabled(false);
                positionComboBox.setEnabled(false);
            } else {
                if (layerNumberComboBox.getModel().getSize() > 0) {
                    layerNumberComboBox.setEnabled(true);
                }
                positionComboBox.setEnabled(true);
            }
        }
    }

    private void updateView3D(boolean reinit) {
        checkComboBoxes();
        if (plate == null || view3D == null || data == null || data.getResult() == null) {
            return;
        }

        plate.setLayerNumber(layerNumberComboBox.getSelectedIndex());

        int resultType;
        switch (resultTypeComboBox.getSelectedIndex()) {
            case 0:
                resultType = DeformationPlate.DISPLACEMENT_Z;
                break;
            case 1:
                resultType = DeformationPlate.LOCAL_STRAIN_X;
                break;
            case 2:
                resultType = DeformationPlate.LOCAL_STRAIN_Y;
                break;
            case 3:
                resultType = DeformationPlate.LOCAL_STRAIN_XY;
                break;
            case 4:
                resultType = DeformationPlate.LOCAL_STRESS_X;
                break;
            case 5:
                resultType = DeformationPlate.LOCAL_STRESS_Y;
                break;
            case 6:
                resultType = DeformationPlate.LOCAL_STRESS_XY;
                break;
            case 7:
                resultType = DeformationPlate.MIN_RESERVE_FACTOR;
                break;
            default:
                resultType = DeformationPlate.DISPLACEMENT_Z;
        }

        plate.setResultType(resultType);

        int position;
        switch (positionComboBox.getSelectedIndex()) {
            case 0:
                position = DeformationPlate.UPPER;
                break;
            case 1:
                position = DeformationPlate.MIDDLE;
                break;
            case 2:
                position = DeformationPlate.LOWER;
                break;
            default:
                position = DeformationPlate.MIDDLE;
        }

        plate.setPosition(position);

        List<Mesh> shapes = plate.getShapes(reinit);
        view3D.setShape3D(shapes, 1.0);
        Node group = new Node();
        for (Node g : plate.getUndeformedWithBC()) {
            group.attachChild(g);
        }
        bcButton.setGeo(group);
        String[] minMax = getMinMaxAsString(plate.getMaxminvec());
        
        minValueLabel.setText(minMax[0]);
        maxValueLabel.setText(minMax[1]);

        DeformationInput input = (DeformationInput)data.getResult().getInput();

        String[] boundary_cond = new String[]{"SS", "CC", "CF", "FF", "SC", "SF"};

        JLabel l = new JLabel("<html><b>" + NbBundle.getMessage(ResultPanel.class, "CTL_DeformationTopComponent") + "</b><br>"
                + NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel1.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS).format(input.getLength()) + "<br>"
                + NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel2.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS).format(input.getWidth()) + "<br>"
                + NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel3.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + boundary_cond[input.getBcx()] + "<br>"
                + NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel4.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + boundary_cond[input.getBcy()] + "<br>"
                + NbBundle.getMessage(ResultPanel.class, "InputPanel.jLabel8.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + input.getM() + "<br>"
                + NbBundle.getMessage(ResultPanel.class, "ResultPanel.resultTypeLabel.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + resultTypeComboBox.getSelectedItem().toString().replaceAll("<html>", "").replaceAll("</html>", "") + "<br>"
                + (resultTypeComboBox.getSelectedIndex() != 0
                ? NbBundle.getMessage(ResultPanel.class, "ResultPanel.layerNumberLabel.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + layerNumberComboBox.getSelectedItem().toString().replaceAll("<html>", "").replaceAll("</html>", "") + "<br>"
                + NbBundle.getMessage(ResultPanel.class, "ResultPanel.PositionLabel.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + positionComboBox.getSelectedItem().toString().replaceAll("<html>", "").replaceAll("</html>", "") + "<br>"
                : "")
                + NbBundle.getMessage(ResultPanel.class, "ResultPanel.minLabel.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + minMax[0] + "<br>"
                + NbBundle.getMessage(ResultPanel.class, "ResultPanel.maxLabel.text").replaceAll("<html>", "").replaceAll("</html>", "") + " " + minMax[1] + "<br>"
                + "</html>");
        l.setFont(minMaxLabel.getFont());
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

    private String[] getMinMaxAsString(double[] minMaxValues) {
        DecimalFormat df;

        switch (resultTypeComboBox.getSelectedIndex()) {
            case 0:
                df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_DISPLACEMENT);
                break;
            case 1:
            case 2:
            case 3:
                df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRAIN);
                break;
            case 4:
            case 5:
            case 6:
                df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRESS);
                break;
            case 7:
                df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRESS);
                break;
            default:
                df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_DISPLACEMENT);
                break;
        }
        return new String[]{df.format(minMaxValues[0]), df.format(minMaxValues[1])};
    }

    public void cleanup() {
        if (data != null) {
            this.data.removePropertyChangeListener(BucklingModuleData.PROP_RESULT, this);
        }
        View3DProperties.getDefault().removePropertyChangeListener(this);
    }
}
