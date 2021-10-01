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
package de.elamx.carpetplots;

import de.elamx.core.GlobalProperties;
import de.elamx.core.RawDataExportService;
import de.elamx.core.SnapshotService;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.eLamXLookup;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.eLamXChartPanel;
import org.jfree.eLamXNumberTickUnitSource;
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
@TopComponent.Description(
        preferredID = "MaterialCarpetPlotTopComponent",
        iconBase="de/elamx/carpetplots/resources/carpetplot.png", 
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
public final class MaterialCarpetPlotTopComponent extends TopComponent implements PropertyChangeListener, LookupListener {

    public final static Set<LayerMaterial> uniqueLaminates = new HashSet<LayerMaterial>();
    private final LayerMaterial material;
    private final Lookup.Result<LayerMaterial> materialResult;
    private XYSeriesCollection dataset = null;
    private JFreeChart chart = null;
    private ChartPanel chartPanel;
    private final CarpetPlotCalculator calculator;
    private final static DecimalFormat DF = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_DOUBLE);

    public MaterialCarpetPlotTopComponent(LayerMaterial material) {
        this.material = material;
        this.material.addPropertyChangeListener(this);
        calculator = new CarpetPlotCalculator(material);

        materialResult = eLamXLookup.getDefault().lookupResult(LayerMaterial.class);
        materialResult.addLookupListener(this);

        initComponents();
        setName(NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "CTL_MaterialCarpetPlotTopComponent", material.getName()));
        setToolTipText(NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "HINT_MaterialCarpetPlotTopComponent"));
        associateLookup(Lookups.fixed(material, new CarpetSnapshot(), new RawDataExport()));
        initChart();
        setData();
    }

    private void initChart() {
        //Font font = (new JLabel()).getFont();
        
        dataset = new XYSeriesCollection();

        //dataset.addSeries(new XYSeries(NbBundle.getMessage(MicroMechanicsTopComponent.class, "MicroMechanics.caption")));
        chart = ChartFactory.createXYLineChart(
                NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "CarpetPlot.title"), // chart title
                NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "CarpetPlot.xaxis.caption"), // x axis label
                NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "CarpetPlot.yaxis.caption"), // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                true // urls
        );

        chart.getXYPlot().getDomainAxis().setRange(0.0, 100.0);
        chart.getXYPlot().getDomainAxis().setLowerMargin(0.0);
        chart.getXYPlot().getDomainAxis().setUpperMargin(0.0);

        chart.getXYPlot().getRangeAxis().setLowerMargin(0.0);
        chart.getXYPlot().getRangeAxis().setUpperMargin(0.0);

        chartPanel = new eLamXChartPanel(chart);

        ((AbstractRenderer)chart.getXYPlot().getRenderer()).setAutoPopulateSeriesPaint(false);
        chart.getXYPlot().getRenderer().setDefaultPaint(Color.BLACK);

        this.add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        exRadioButton = new javax.swing.JRadioButton();
        nuexyRadioButton = new javax.swing.JRadioButton();
        gxyRadioButton = new javax.swing.JRadioButton();

        setLayout(new java.awt.BorderLayout());

        buttonGroup1.add(exRadioButton);
        exRadioButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(exRadioButton, org.openide.util.NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "MaterialCarpetPlotTopComponent.exRadioButton.text")); // NOI18N
        exRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exRadioButtonActionPerformed(evt);
            }
        });
        jPanel1.add(exRadioButton);

        buttonGroup1.add(nuexyRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(nuexyRadioButton, org.openide.util.NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "MaterialCarpetPlotTopComponent.nuexyRadioButton.text")); // NOI18N
        nuexyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuexyRadioButtonActionPerformed(evt);
            }
        });
        jPanel1.add(nuexyRadioButton);

        buttonGroup1.add(gxyRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(gxyRadioButton, org.openide.util.NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "MaterialCarpetPlotTopComponent.gxyRadioButton.text")); // NOI18N
        gxyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gxyRadioButtonActionPerformed(evt);
            }
        });
        jPanel1.add(gxyRadioButton);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void exRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exRadioButtonActionPerformed
        setData();
    }//GEN-LAST:event_exRadioButtonActionPerformed

    private void nuexyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuexyRadioButtonActionPerformed
        setData();
    }//GEN-LAST:event_nuexyRadioButtonActionPerformed

    private void gxyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gxyRadioButtonActionPerformed
        setData();
    }//GEN-LAST:event_gxyRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton exRadioButton;
    private javax.swing.JRadioButton gxyRadioButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton nuexyRadioButton;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        uniqueLaminates.remove(material);
        material.removePropertyChangeListener(this);
        materialResult.removeLookupListener(this);
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
        if (evt.getPropertyName().equals(LayerMaterial.PROP_NAME)) {
            setName(NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "CTL_MaterialCarpetPlotTopComponent", material.getName()));
        } else {
            setData();
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (!materialResult.allInstances().contains(material)) {
            this.close();
        }
    }

    private void setData() {
        dataset.removeAllSeries();
        XYPlot plot = chart.getXYPlot();
        for (Object a : chart.getXYPlot().getAnnotations()) {
            plot.removeAnnotation((XYTextAnnotation) a);
        }
        Font font = plot.getDomainAxis().getLabelFont();
        double delta;
        int type;
        AttributedString captionY = new AttributedString("");
        boolean withAnnotation;
        if (gxyRadioButton.isSelected()){
            type = CarpetPlotCalculator.VAL_GXY;
            delta = 0.0;
            captionY = new AttributedString("Gxy");
            captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 3);
            captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 3);
            captionY.addAttribute(TextAttribute.SIZE, font.getSize());
            withAnnotation = false;
        }else if (nuexyRadioButton.isSelected()){
            type = CarpetPlotCalculator.VAL_NUEXY;
            delta = material.getNue12() * 0.015;
            captionY = new AttributedString("\u03BDxy");
            captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 3);
            captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 3);
            captionY.addAttribute(TextAttribute.SIZE, font.getSize());
            withAnnotation = true;
        }else{
            type = CarpetPlotCalculator.VAL_EX;
            delta = material.getEpar() * 0.015;
            captionY = new AttributedString("Ex");
            captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 2);
            captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 2);
            captionY.addAttribute(TextAttribute.SIZE, font.getSize());
            withAnnotation = true;
        }
        plot.getRangeAxis().setAttributedLabel(captionY);
        
        font = plot.getDomainAxis().getTickLabelFont();
        for (int ii = 0; ii < 10; ii++) {
            XYSeries series = new XYSeries(DF.format(0.1 * ii * 100.0));
            double[][] data = calculator.getChartData(0.1 * ii, 100, type, true);

            for (int jj = 0; jj < data[0].length; jj++) {
                series.add(data[0][jj] * 100, data[1][jj]);
            }
            dataset.addSeries(series);

            if (withAnnotation){
                XYTextAnnotation annotation = new XYTextAnnotation(DF.format(0.1 * ii * 100.0), 2.0, data[1][0] - delta);
                annotation.setFont(font);
                chart.getXYPlot().addAnnotation(annotation);
            }
        }
        
        XYSeries seriesBound = new XYSeries(NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "CarpetPlot.Ratio90isZero.caption"));
        double[][] data = calculator.getChartData(0.0, 100, type, false);
        for (int jj = 0; jj < data[0].length; jj++) {
            seriesBound.add(data[0][jj] * 100, data[1][jj]);
        }
        dataset.addSeries(seriesBound);

        if (withAnnotation){
            XYTextAnnotation annotation = new XYTextAnnotation(NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "CarpetPlot.zaxis.caption"), 6.0, delta);
            annotation.setFont(font);
            chart.getXYPlot().addAnnotation(annotation);
        }
        
        ((NumberAxis)plot.getRangeAxis()).setStandardTickUnits(new eLamXNumberTickUnitSource());
        ((NumberAxis)plot.getDomainAxis()).setStandardTickUnits(new eLamXNumberTickUnitSource());
    }

    private class CarpetSnapshot implements SnapshotService {

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
                DecimalFormat df_value = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STIFFNESS);
                String ls = System.getProperty("line.separator");
                
                int numSeries = dataset.getSeriesCount();
                for (int ii = 0; ii < numSeries-1; ii++) {
                    fw.write("0° -> " + dataset.getSeries(ii).getKey().toString() + " %");
                    fw.write(ls);
                    fw.write("+/-45° [%]");
                    fw.write("\t" + NbBundle.getMessage(MaterialCarpetPlotTopComponent.class, "CarpetPlot.yaxis.caption"));
                    fw.write(ls);
                    int numRows = dataset.getSeries(ii).getItemCount();
                    
                    for (int row = 0; row < numRows; row++) {
                        fw.write(DF.format(dataset.getSeries(ii).getX(row)) + "\t" + df_value.format(dataset.getSeries(ii).getY(row)));
                        fw.write(ls);
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
