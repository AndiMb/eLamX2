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

import de.elamx.clt.optimization.OptimizationInput;
import de.elamx.clt.optimization.OptimizationResult;
import de.elamx.clt.optimization.Optimizer;
import de.elamx.clt.optimizationui.actions.OptimizationEditWizardAction;
import de.elamx.core.GlobalProperties;
import de.elamx.core.LaminatStringGenerator;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.optimization.MRFC_ModuleDataGenerator;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.eLamXChartPanel;
import org.jfree.eLamXNumberTickUnitSource;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "Optimization1TopComponent",
        iconBase = "de/elamx/clt/optimizationui/resources/optimization.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
public final class OptimizationTopComponent extends TopComponent implements PropertyChangeListener, LookupListener {

    private static final RequestProcessor RP = new RequestProcessor("Interruptible", 1, true);
    private volatile RequestProcessor.Task last;

    public final static Set<OptimizationModuleData> uniqueOptimizationModuleData = new HashSet<OptimizationModuleData>();

    private NumberFormat thicknessFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);
    private NumberFormat angleFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE);

    private final OptimizationModuleData data;
    
    private final Lookup.Result<OptimizationModuleData> result;
    
    private Laminat lastLaminat = null;
    
    protected static final AtomicInteger atomicLaminateCounter = new AtomicInteger(0);
    
    private XYSeriesCollection numLayersDataset = null;
    private XYSeriesCollection minResFacDataset = null;
    private JFreeChart chart = null;
    private ChartPanel chartPanel;

    public OptimizationTopComponent(OptimizationModuleData data) {
        this.data = data;
        initComponents();
        setName(NbBundle.getMessage(OptimizationTopComponent.class, "CTL_Optimization1TopComponent"));
        setToolTipText(NbBundle.getMessage(OptimizationTopComponent.class, "HINT_Optimization1TopComponent"));
        updateInputSummaryLabels();
        data.getOptimizationInput().addPropertyChangeListener(data);
        result = eLamXLookup.getDefault().lookupResult(OptimizationModuleData.class);
        result.addLookupListener(this);
        data.getOptimizationInput().addPropertyChangeListener(this);
        data.addPropertyChangeListener(this);
        
        initChart();
    }

    private void updateInputSummaryLabels() {
        OptimizationInput input = data.getOptimizationInput();

        materialLabel.setText(input.getMaterial().getName());
        failureCriterionLabel.setText(input.getCriterion().getDisplayName());
        thicknessLabel.setText(thicknessFormat.format(input.getThickness()));
        symmetryLabel.setText(NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.symmetry.label." + input.isSymmetricLaminat()));
        StringBuilder stringBuild = new StringBuilder();
        stringBuild.append("<html>");
        for (MinimalReserveFactorCalculator mrfc : input.getCalculators()) {
            stringBuild.append("(");
            stringBuild.append(mrfc.getHtmlString());
            stringBuild.append(") ");
        }
        stringBuild.append("</html>");
        constraintsLabel.setText(stringBuild.toString());
        algorithmLabel.setText(data.getOptimizer().getName());

        String anglesString;
        switch (data.getAngleType()) {
            case OptimizationVisualPanel1.INC_45:
                anglesString = NbBundle.getMessage(OptimizationTopComponent.class, "AngleType.increment45.text");
                break;
            case OptimizationVisualPanel1.INC_15:
                anglesString = NbBundle.getMessage(OptimizationTopComponent.class, "AngleType.increment15.text");
                break;
            case OptimizationVisualPanel1.INC_10:
                anglesString = NbBundle.getMessage(OptimizationTopComponent.class, "AngleType.increment10.text");
                break;
            case OptimizationVisualPanel1.INC_5:
                anglesString = NbBundle.getMessage(OptimizationTopComponent.class, "AngleType.increment05.text");
                break;
            default:
                anglesString = addAngles(NbBundle.getMessage(OptimizationTopComponent.class, "AngleType.userDefined.text"), input.getAngles());
        }
        anglesLabel.setText(anglesString);
    }

    private String addAngles(String anglesString, double[] angles) {
        anglesString += " (";
        for (int ii = 0; ii < angles.length - 1; ii++) {
            anglesString += angleFormat.format(angles[ii]) + "/";
        }
        anglesString += angleFormat.format(angles[angles.length - 1]) + ")";
        return anglesString;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        failureCriterionLabel = new javax.swing.JLabel();
        materialLabel = new javax.swing.JLabel();
        thicknessLabel = new javax.swing.JLabel();
        anglesLabel = new javax.swing.JLabel();
        symmetryLabel = new javax.swing.JLabel();
        constraintsLabel = new javax.swing.JLabel();
        algorithmLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        optimizationButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        resultSummaryPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        numConstEvalLabel = new javax.swing.JLabel();
        numLayersLabel = new javax.swing.JLabel();
        minResFacLabel = new javax.swing.JLabel();
        bestLaminateLabel = new javax.swing.JLabel();
        chartHolderPanel = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "InputSummaryPanel.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(failureCriterionLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.failureCriterionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(materialLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.materialLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(thicknessLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.thicknessLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(anglesLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.anglesLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(symmetryLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.symmetryLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(constraintsLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.constraintsLabel.text")); // NOI18N
        constraintsLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.openide.awt.Mnemonics.setLocalizedText(algorithmLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.algorithmLabel.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(failureCriterionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(materialLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(thicknessLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(anglesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(symmetryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(constraintsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(algorithmLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(materialLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(failureCriterionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(thicknessLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(anglesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(symmetryLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(constraintsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(algorithmLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(optimizationButton, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.optimizationButton.text")); // NOI18N
        optimizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optimizationButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(editButton, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.editButton.text")); // NOI18N
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        resultSummaryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "resultSummaryPanel.titel"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel10.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.jLabel11.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(numConstEvalLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.numConstEvalLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(numLayersLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.numLayersLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(minResFacLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.minResFacLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bestLaminateLabel, org.openide.util.NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationTopComponent.bestLaminateLabel.text")); // NOI18N
        bestLaminateLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout resultSummaryPanelLayout = new javax.swing.GroupLayout(resultSummaryPanel);
        resultSummaryPanel.setLayout(resultSummaryPanelLayout);
        resultSummaryPanelLayout.setHorizontalGroup(
            resultSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultSummaryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(resultSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numConstEvalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(numLayersLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(minResFacLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bestLaminateLabel))
                .addContainerGap())
        );
        resultSummaryPanelLayout.setVerticalGroup(
            resultSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultSummaryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(bestLaminateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(resultSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(minResFacLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(resultSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(numLayersLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(resultSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(numConstEvalLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chartHolderPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultSummaryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(optimizationButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(chartHolderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optimizationButton)
                    .addComponent(cancelButton)
                    .addComponent(editButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultSummaryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartHolderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void optimizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optimizationButtonActionPerformed
        if (data.getOptimizer().onlySymmetricLaminates() && !data.getOptimizationInput().isSymmetricLaminat()) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(OptimizationTopComponent.class, "Error.symmetry"), NotifyDescriptor.ERROR_MESSAGE));
            return;
        }

        optimizationButton.setEnabled(false);
        editButton.setEnabled(false);
        final ProgressHandle ph = ProgressHandle.createSystemHandle(NbBundle.getMessage(OptimizationTopComponent.class, "Task.optimization"), null);

        last = RP.post(new Runnable() {
            @Override
            public void run() {
                ph.start();
                numLayersDataset.removeAllSeries();
                numLayersDataset.addSeries(new XYSeries(NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationChart.yaxis.caption")));
                minResFacDataset.removeAllSeries();
                minResFacDataset.addSeries(new XYSeries(NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationChart.yaxis2.caption")));
                for (int ii = 0; ii < 1; ii++) {
                    Optimizer optimizer = data.getOptimizer().getInstance(data.getOptimizationInput());
                    OptimizationResult result = optimizer.getResult();
                    result.addPropertyChangeListener(new OptimizationResultListener());
                    optimizer.optimize(false);
                }
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        optimizationButton.setEnabled(true);
                        editButton.setEnabled(true);
                        exportBestLaminat();
                        lastLaminat = null;
                        ph.finish();
                    }
                });
            }
        });
    }//GEN-LAST:event_optimizationButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (last != null) {
            last.cancel();
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        (new OptimizationEditWizardAction(data)).actionPerformed(evt);
    }//GEN-LAST:event_editButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel algorithmLabel;
    private javax.swing.JLabel anglesLabel;
    private javax.swing.JLabel bestLaminateLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel chartHolderPanel;
    private javax.swing.JLabel constraintsLabel;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel failureCriterionLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel materialLabel;
    private javax.swing.JLabel minResFacLabel;
    private javax.swing.JLabel numConstEvalLabel;
    private javax.swing.JLabel numLayersLabel;
    private javax.swing.JButton optimizationButton;
    private javax.swing.JPanel resultSummaryPanel;
    private javax.swing.JLabel symmetryLabel;
    private javax.swing.JLabel thicknessLabel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        if (last != null) {
            last.cancel();
        }
        result.removeLookupListener(this);
        data.getOptimizationInput().removePropertyChangeListener(this);
        data.removePropertyChangeListener(this);
        uniqueOptimizationModuleData.remove(data);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateInputSummaryLabels();
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (!result.allInstances().contains(data)) {
            this.close();
        }
    }
    
    private void exportBestLaminat(){
        Laminat laminat = lastLaminat;
        
        if (laminat == null){
            return;
        }
        
        Collection<? extends MRFC_ModuleDataGenerator> dataGens = Lookup.getDefault().lookupAll(MRFC_ModuleDataGenerator.class);

        laminat = laminat.getCopy(false);
        
        laminat.setName(NbBundle.getMessage(Optimizer.class, "Optimized_Laminate") + " " + atomicLaminateCounter.incrementAndGet());

        boolean test;
        for (MinimalReserveFactorCalculator calcs : data.getOptimizationInput().getCalculators()) {
            for (MRFC_ModuleDataGenerator gen : dataGens) {
                test = gen.generateELamXModuleData(laminat, calcs);
                if (test) {
                    break;
                }
            }
        }
        
        eLamXLookup.getDefault().add(laminat);
    }

    private void initChart() {
        
        numLayersDataset = new XYSeriesCollection();
        minResFacDataset = new XYSeriesCollection();

        //dataset.addSeries(new XYSeries(NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanics.caption")));
        chart = ChartFactory.createXYLineChart(
                "", // chart title
                NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationChart.xaxis.caption"), // x axis label
                NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationChart.yaxis.caption"), // y axis label
                numLayersDataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                true // urls
        );

        //chart.getXYPlot().getDomainAxis().setRange(0.0, 100.0);
        chart.getXYPlot().getDomainAxis().setLowerMargin(0.0);
        chart.getXYPlot().getDomainAxis().setUpperMargin(0.0);
        chart.getXYPlot().getDomainAxis().setStandardTickUnits(new eLamXNumberTickUnitSource());
        
        chart.getXYPlot().getRangeAxis().setStandardTickUnits(new eLamXNumberTickUnitSource());
        
        chart.getXYPlot().setDataset(1, minResFacDataset);
        chart.getXYPlot().mapDatasetToRangeAxis(1, 1);
        final NumberAxis axis2 = new NumberAxis(NbBundle.getMessage(OptimizationTopComponent.class, "OptimizationChart.yaxis2.caption"));
        axis2.setStandardTickUnits(new eLamXNumberTickUnitSource());
        axis2.setAutoRangeIncludesZero(false);
        axis2.setLabelFont(chart.getXYPlot().getRangeAxis().getLabelFont());
        axis2.setTickLabelFont(chart.getXYPlot().getRangeAxis().getTickLabelFont());
        chart.getXYPlot().setRangeAxis(1, axis2);
        
        chart.getXYPlot().getRenderer(0).setSeriesPaint(0, Color.BLACK);
        
        final XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
        chart.getXYPlot().setRenderer(1, renderer2);
        chart.getXYPlot().getRenderer(1).setSeriesPaint(0, Color.GREEN);

        chartPanel = new eLamXChartPanel(chart);

        chartHolderPanel.add(chartPanel, BorderLayout.CENTER);
    }

    private class OptimizationResultListener implements PropertyChangeListener {

        DecimalFormat dfRF = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_RESERVE_FACTOR);
        //long lastChangeTime = 0;
        //long delta = 100;

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    OptimizationResult result = (OptimizationResult) evt.getSource();
                    lastLaminat = result.getBestLaminate();
                    numConstEvalLabel.setText("" + result.getNumberOfContraintEvaluations());
                    numLayersLabel.setText("" + result.getBestLaminate().getNumberofLayers());
                    bestLaminateLabel.setText("<html>" + LaminatStringGenerator.getLaminatAsHTMLString(result.getBestLaminate()) + "</html>");
                    minResFacLabel.setText(dfRF.format(result.getMinReserveFactor()));
                    numLayersDataset.getSeries(0).add(result.getNumberOfContraintEvaluations(), result.getBestLaminate().getNumberofLayers());
                    minResFacDataset.getSeries(0).add(result.getNumberOfContraintEvaluations(), result.getMinReserveFactor());
                }
            });
        }

    }
}
