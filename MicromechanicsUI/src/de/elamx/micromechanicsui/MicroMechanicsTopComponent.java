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
package de.elamx.micromechanicsui;

import de.elamx.core.GlobalProperties;
import de.elamx.core.RawDataExportService;
import de.elamx.core.SnapshotService;
import de.elamx.laminate.eLamXLookup;
import de.elamx.micromechanics.Fiber;
import de.elamx.micromechanics.Matrix;
import de.elamx.micromechanics.models.ManualInputDummyModel;
import de.elamx.micromechanics.models.MicroMechModel;
import de.elamx.utilities.AutoRowHeightTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.eLamXChartPanel;
import org.jfree.eLamXNumberTickUnitSource;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.elamx.micromechanicsui//MicroMechanics//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "MicroMechanicsTopComponent",
        iconBase="de/elamx/micromechanicsui/resources/micromechanics.png", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.elamx.micromechanicsui.MicroMechanicsTopComponent")
@ActionReferences({
    @ActionReference(path = "Menu/Window" , position = 334 ),
    @ActionReference(path = "Toolbars/eLamX_Modules_General", position = 200)
})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MicroMechanicsAction",
        preferredID = "MicroMechanicsTopComponent"
)
public final class MicroMechanicsTopComponent extends TopComponent implements PropertyChangeListener{

    private final Lookup.Result<Fiber> fiberResult;
    private final Lookup.Result<Matrix> matrixResult;
    private final MicroMechModel[] mmModels;
    private final boolean[] show;
    private final Color[] colors;
    private XYSeriesCollection dataset = null;
    private JFreeChart chart = null;
    private ChartPanel chartPanel;
    private AttributedString captionX;

    public MicroMechanicsTopComponent() {

        Font font = (new JLabel()).getFont();
        
        captionX = new AttributedString("\u03C6 [%]");
        captionX.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionX.addAttribute(TextAttribute.SIZE, font.getSize());
        initComponents();
        setName(NbBundle.getMessage(MicroMechanicsTopComponent.class, "CTL_MicroMechanicsTopComponent"));
        setToolTipText(NbBundle.getMessage(MicroMechanicsTopComponent.class, "HINT_MicroMechanicsTopComponent"));
        associateLookup(Lookups.fixed(new MicroMechanicSnapshot(), new RawDataExport()));
        fiberResult = eLamXLookup.getDefault().lookupResult(Fiber.class);
        fiberResult.addLookupListener(new FiberLookupListener());
        matrixResult = eLamXLookup.getDefault().lookupResult(Matrix.class);
        matrixResult.addLookupListener(new MatrixLookupListener());

        updateFiberComboBox();
        updateMatrixComboBox();

        Lookup lkp = Lookups.forPath("elamx/micromechmodel");
        Collection<? extends MicroMechModel> c = lkp.lookupAll(MicroMechModel.class);
        mmModels = new MicroMechModel[c.size() - 1];
        colors = new Color[mmModels.length];
        int ii = 0;
        for (MicroMechModel mmm : c) {
            if (mmm instanceof ManualInputDummyModel) {
                continue;
            }
            colors[ii] = mmm.getColor();
            mmModels[ii++] = mmm;
        }
        show = new boolean[mmModels.length];

        initChart();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        rightPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        fiberBox = new javax.swing.JComboBox<Fiber>();
        matrixBox = new javax.swing.JComboBox<Matrix>();
        jPanel2 = new javax.swing.JPanel();
        EParButton = new javax.swing.JRadioButton();
        ENorButton = new javax.swing.JRadioButton();
        Nue12Button = new javax.swing.JRadioButton();
        GButton = new javax.swing.JRadioButton();
        mmModelPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new AutoRowHeightTable();
        jTable1.setDefaultRenderer(Color.class, new ColorRenderer(true));

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MicroMechanicsTopComponent.class, "FiberMatrixPanel.title"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fiberBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(matrixBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fiberBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(matrixBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MicroMechanicsTopComponent.class, "PropertyPanel"))); // NOI18N

        buttonGroup1.add(EParButton);
        EParButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(EParButton, org.openide.util.NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanicsTopComponent.EParButton.text")); // NOI18N
        EParButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EParButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(ENorButton);
        org.openide.awt.Mnemonics.setLocalizedText(ENorButton, org.openide.util.NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanicsTopComponent.ENorButton.text")); // NOI18N
        ENorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ENorButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(Nue12Button);
        org.openide.awt.Mnemonics.setLocalizedText(Nue12Button, org.openide.util.NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanicsTopComponent.Nue12Button.text")); // NOI18N
        Nue12Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nue12ButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(GButton);
        org.openide.awt.Mnemonics.setLocalizedText(GButton, org.openide.util.NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanicsTopComponent.GButton.text")); // NOI18N
        GButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EParButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ENorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Nue12Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EParButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Nue12Button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ENorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mmModelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MicroMechanicsTopComponent.class, "MMModelPanel.title"))); // NOI18N
        mmModelPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 100));

        jTable1.setModel(new MMModelTableModel());
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout mmModelPanelLayout = new javax.swing.GroupLayout(mmModelPanel);
        mmModelPanel.setLayout(mmModelPanelLayout);
        mmModelPanelLayout.setHorizontalGroup(
            mmModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mmModelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        mmModelPanelLayout.setVerticalGroup(
            mmModelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mmModelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rightPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mmModelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mmModelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
        );

        add(rightPanel, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void EParButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EParButtonActionPerformed
        displayModels();
    }//GEN-LAST:event_EParButtonActionPerformed

    private void ENorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ENorButtonActionPerformed
        displayModels();
    }//GEN-LAST:event_ENorButtonActionPerformed

    private void Nue12ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nue12ButtonActionPerformed
        displayModels();
    }//GEN-LAST:event_Nue12ButtonActionPerformed

    private void GButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GButtonActionPerformed
        displayModels();
    }//GEN-LAST:event_GButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton ENorButton;
    private javax.swing.JRadioButton EParButton;
    private javax.swing.JRadioButton GButton;
    private javax.swing.JRadioButton Nue12Button;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<Fiber> fiberBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<Matrix> matrixBox;
    private javax.swing.JPanel mmModelPanel;
    private javax.swing.JPanel rightPanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        ComboBoxModel<Fiber> model = fiberBox.getModel();
        for (int ii = 0; ii < model.getSize(); ii++){
            model.getElementAt(ii).removePropertyChangeListener(this);
        }
        ComboBoxModel<Matrix> model2 = matrixBox.getModel();
        for (int ii = 0; ii < model2.getSize(); ii++){
            model2.getElementAt(ii).removePropertyChangeListener(this);
        }
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

    private void initChart() {
        
        dataset = new XYSeriesCollection();

        //dataset.addSeries(new XYSeries(NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanics.caption")));
        chart = ChartFactory.createXYLineChart(
                "", // chart title
                NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanicsChart.xaxis.caption"), // x axis label
                NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanicsChart.yaxis.caption"), // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                true // urls
        );

        chart.getXYPlot().getDomainAxis().setRange(0.0, 100.0);
        chart.getXYPlot().getDomainAxis().setLowerMargin(0.0);
        chart.getXYPlot().getDomainAxis().setUpperMargin(0.0);
        chart.getXYPlot().getDomainAxis().setAttributedLabel(captionX);
        chart.getXYPlot().getDomainAxis().setStandardTickUnits(new eLamXNumberTickUnitSource());
        
        chart.getXYPlot().getRangeAxis().setStandardTickUnits(new eLamXNumberTickUnitSource());

        chartPanel = new eLamXChartPanel(chart);
        
        setYCaption();

        this.add(chartPanel, BorderLayout.CENTER);
    }
    
    private void setYCaption(){
        if (chart == null) {
            return;
        }
        
        Font font = chart.getXYPlot().getDomainAxis().getLabelFont();

        AttributedString captionY = new AttributedString("");
        if (EParButton.isSelected()) {
            captionY = new AttributedString("E\u2225");
            captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 2);
            captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 2);
            captionY.addAttribute(TextAttribute.SIZE, font.getSize());
        } else if (ENorButton.isSelected()) {
            captionY = new AttributedString("E\u22A5");
            captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 2);
            captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 2);
            captionY.addAttribute(TextAttribute.SIZE, font.getSize());
        } else if (Nue12Button.isSelected()) {
            captionY = new AttributedString("\u03BD\u2225\u22A5");
            captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 3);
            captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 3);
            captionY.addAttribute(TextAttribute.SIZE, font.getSize());
        } else if (GButton.isSelected()) {
            captionY = new AttributedString("G\u2225\u22A5");
            captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 3);
            captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 3);
            captionY.addAttribute(TextAttribute.SIZE, font.getSize());
        }
        chart.getXYPlot().getRangeAxis().setAttributedLabel(captionY);
    }

    private void displayModels() {

        setYCaption();
        
        if (dataset == null) {
            return;
        }

        dataset.removeAllSeries();
        Fiber fiber = (Fiber) fiberBox.getSelectedItem();
        Matrix matrix = (Matrix) matrixBox.getSelectedItem();

        if (matrix == null || fiber == null) {
            return;
        }

        XYPlot plot = chart.getXYPlot();
        int seriesCounter = 0;
        for (int ii = 0; ii < mmModels.length; ii++) {
            if (!show[ii]) {
                continue;
            }
            MicroMechModel mmm = mmModels[ii];
            XYSeries series = new XYSeries(mmm.getDisplayName());

            if (EParButton.isSelected()) {
                for (int jj = 0; jj < 101; jj++) {
                    series.add(jj, mmm.getE11(fiber, matrix, jj / 100.0));
                }
            } else if (ENorButton.isSelected()) {
                for (int jj = 0; jj < 101; jj++) {
                    series.add(jj, mmm.getE22(fiber, matrix, jj / 100.0));
                }
            }
            if (Nue12Button.isSelected()) {
                for (int jj = 0; jj < 101; jj++) {
                    series.add(jj, mmm.getNue12(fiber, matrix, jj / 100.0));
                }
            }
            if (GButton.isSelected()) {
                for (int jj = 0; jj < 101; jj++) {
                    series.add(jj, mmm.getG12(fiber, matrix, jj / 100.0));
                }
            }
            dataset.addSeries(series);
            plot.getRenderer().setSeriesPaint(seriesCounter++, colors[ii]);
        }
    }

    private void updateFiberComboBox() {
        ComboBoxModel<Fiber> model = fiberBox.getModel();
        for (int ii = 0; ii < model.getSize(); ii++){
            model.getElementAt(ii).removePropertyChangeListener(this);
        }
        ArrayList<Fiber> fibers = new ArrayList<>();
        for (Fiber mat : fiberResult.allInstances()) {
            fibers.add(mat);
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(fibers, new Comparator<Fiber>() {
            @Override
            public int compare(Fiber o1, Fiber o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        DefaultComboBoxModel<Fiber> fiberModel = new DefaultComboBoxModel<>(fibers.toArray(new Fiber[fibers.size()]));
        fiberBox.setModel(fiberModel);
        model = fiberBox.getModel();
        for (int ii = 0; ii < model.getSize(); ii++){
            model.getElementAt(ii).addPropertyChangeListener(this);
        }
        displayModels();
    }

    private void updateMatrixComboBox() {
        ComboBoxModel<Matrix> model = matrixBox.getModel();
        for (int ii = 0; ii < model.getSize(); ii++){
            model.getElementAt(ii).removePropertyChangeListener(this);
        }
        ArrayList<Matrix> matrices = new ArrayList<>();
        for (Matrix mat : matrixResult.allInstances()) {
            matrices.add(mat);
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(matrices, new Comparator<Matrix>() {
            @Override
            public int compare(Matrix o1, Matrix o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        DefaultComboBoxModel<Matrix> matrixModel = new DefaultComboBoxModel<>(matrices.toArray(new Matrix[matrices.size()]));
        matrixBox.setModel(matrixModel);
        model = matrixBox.getModel();
        for (int ii = 0; ii < model.getSize(); ii++){
            model.getElementAt(ii).addPropertyChangeListener(this);
        }
        displayModels();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        displayModels();
    }

    private class FiberLookupListener implements LookupListener {

        @Override
        public void resultChanged(LookupEvent ev) {
            updateFiberComboBox();
        }
    }

    private class MatrixLookupListener implements LookupListener {

        @Override
        public void resultChanged(LookupEvent ev) {
            updateMatrixComboBox();
        }
    }

    private class MMModelTableModel extends AbstractTableModel {

        private final String[] columnNames;

        @SuppressWarnings("empty-statement")
        public MMModelTableModel() {

            String nameCap  = NbBundle.getMessage(MicroMechanicsTopComponent.class, "CLT_MicroMechanicsTopComponent.table.name.caption");
            String colorCap = NbBundle.getMessage(MicroMechanicsTopComponent.class, "CLT_MicroMechanicsTopComponent.table.color.caption");;
            String showCap  = NbBundle.getMessage(MicroMechanicsTopComponent.class, "CLT_MicroMechanicsTopComponent.table.show.caption");;

            columnNames = new String[]{nameCap, colorCap, showCap};
        }

        /*
         * Returns the number of columns
         *
         * @returns int
         */
        @Override
        public int getColumnCount() {
            int col = 0;
            if (columnNames != null) {
                col = columnNames.length;
            }
            return col;
        }

        /*
         * Returns the number of rows
         *
         * @returns int
         */
        @Override
        public int getRowCount() {
            if (mmModels == null) {
                return 0;
            }
            return mmModels.length;
        }

        /*
         * Returns the column name
         *
         * @params col the column to query
         */
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        /*
         * Returns the value at a desired position
         *
         * @params row the row
         * @params col the column
         * @returns Object
         */
        @Override
        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return mmModels[row].getDisplayName();
                case 1:
                    return mmModels[row].getColor();
                case 2:
                    return show[row];
                default:
                    return null;
            }
        }

        /*
         * Returns the class of the Column
         *
         * @params col the column to query
         * @returns Class
         */
        @Override
        public Class<?> getColumnClass(int col) {
            return getValueAt(0, col).getClass();
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (column == 2) {
                show[row] = ((Boolean) aValue);
                displayModels();
            }
        }

        /*
         * Returns true if the cell is editable, else false
         *
         * @params rowindex the desired row
         * @params columnindex the desired column
         */
        @Override
        public boolean isCellEditable(int rowindex, int columnindex) {
            return columnindex == 2;
        }
    }

    public class ColorRenderer extends JLabel implements TableCellRenderer {

        Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;

        @SuppressWarnings("this-escape")
        public ColorRenderer(boolean isBordered) {
            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object color,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            
            Color newColor = (Color) color;
            setBackground(newColor);
            if (isBordered) {
                if (isSelected) {
                    if (selectedBorder == null) {
                        selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                table.getSelectionBackground());
                    }
                    setBorder(selectedBorder);
                } else {
                    if (unselectedBorder == null) {
                        unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                table.getBackground());
                    }
                    setBorder(unselectedBorder);
                }
            }
            return this;
        }
    }

    private class MicroMechanicSnapshot implements SnapshotService {

        @Override
        public void saveSnapshot(File file) {
            try {
                ChartUtils.saveChartAsJPEG(file, chart, chartPanel.getSize().width, chartPanel.getSize().height);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private class RawDataExport implements RawDataExportService {

        @Override
        public void export(FileWriter fw) {
            try {
                DecimalFormat df_phi = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE);
                DecimalFormat df_value;
                if (Nue12Button.isSelected()) {
                    df_value = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_POISSONRATIO);
                } else {
                    df_value = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STIFFNESS);
                }
                String ls = System.getProperty("line.separator");
                fw.write("Phi [%]");

                int numSeries = dataset.getSeriesCount();
                for (int ii = 0; ii < numSeries; ii++) {
                    fw.write("\t" + dataset.getSeries(ii).getKey().toString());
                }
                fw.write(ls);
                int numRows = dataset.getSeries(0).getItemCount();
                for (int row = 0; row < numRows; row++) {
                    fw.write(df_phi.format(dataset.getSeries(0).getX(row)));
                    for (int ii = 0; ii < numSeries; ii++) {
                        fw.write("\t" + df_value.format(dataset.getSeries(ii).getY(row)));
                    }
                    fw.write(ls);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public String getFileExtension() {
            return "txt";
        }
    }
}
