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
package de.elamx.clt.cutoutui;

import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.cutout.CutoutGeometry;
import de.elamx.clt.cutout.CutoutResult;
import de.elamx.core.GlobalProperties;
import de.elamx.utilities.BoundsPopupMenuListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.AttributedString;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.jfree.XYPlotI;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.eLamXChartTheme;
import org.jfree.eLamXNumberTickUnitSource;
import org.openide.util.NbBundle;

/**
 *
 * @author raedel
 */
public class ResultPanel extends javax.swing.JPanel implements PropertyChangeListener, ActionListener, ItemListener {

    private static final double X_AXIS_OVERLAP = 1.05;
    private static final double Y_AXIS_OVERLAP = X_AXIS_OVERLAP;

    public static final String ALPHA_KEY = "\u03B1";
    public static final String NXX_KEY = "nxx";
    public static final String NYY_KEY = "nyy";
    public static final String NXY_KEY = "nxy";
    public static final String NALPHA_KEY = "n\u03B1";
    public static final String MXX_KEY = "mxx";
    public static final String MYY_KEY = "myy";
    public static final String MXY_KEY = "mxy";
    public static final String MALPHA_KEY = "m\u03B1";
    public static final String GEO_KEY = NbBundle.getMessage(ResultPanel.class, "ResultPanel.diag.series.cutoutgeometry");
    public static final String POS_KEY = NbBundle.getMessage(ResultPanel.class, "ResultPanel.diag.series.positive");
    public static final String NEG_KEY = NbBundle.getMessage(ResultPanel.class, "ResultPanel.diag.series.negative");

    private static final String diagtype_alpha = NbBundle.getMessage(ResultPanel.class, "ResultPanel.diag.type.alpha.name");
    private static final String diagtype_shape = NbBundle.getMessage(ResultPanel.class, "ResultPanel.diag.type.shape.name");
    private static final String diagtype_geometry = NbBundle.getMessage(ResultPanel.class, "ResultPanel.diag.type.geometry.name");
    private static final String[] diagtypes = new String[]{diagtype_alpha, diagtype_shape, diagtype_geometry};

    private final CutoutResultTableModel cutoutResultModel = new CutoutResultTableModel();

    private XYSeriesCollection paintdataset = null;
    private final XYSeriesCollection alldataset = new XYSeriesCollection();
    private CutoutModuleData data;
    private JFreeChart chart;

    private final XYSeries nxxseries = new XYSeries(NXX_KEY);
    private final XYSeries nyyseries = new XYSeries(NYY_KEY);
    private final XYSeries nxyseries = new XYSeries(NXY_KEY);
    private final XYSeries nalphaseries = new XYSeries(NALPHA_KEY);
    private final XYSeries mxxseries = new XYSeries(MXX_KEY);
    private final XYSeries myyseries = new XYSeries(MYY_KEY);
    private final XYSeries mxyseries = new XYSeries(MXY_KEY);
    private final XYSeries malphaseries = new XYSeries(MALPHA_KEY);

    CLT_Laminate laminat;

    /**
     * Creates new customizer ResultPanel
     */
    public ResultPanel() {
        this(null, null);
    }

    public ResultPanel(CutoutModuleData data, JFreeChart chart) {
        this.data = data;
        this.chart = chart;

        if (data != null) {
            data.addPropertyChangeListener(CutoutModuleData.PROP_RESULT, this);
        }
        initComponents();
        buttonAvailable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        resultButtonGroup = new javax.swing.ButtonGroup();
        displayButtonGroup = new javax.swing.ButtonGroup();
        resultTypeCombo = new javax.swing.JComboBox<String>();
        BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false); 
        resultTypeCombo.addPopupMenuListener( listener );
        nxxRadioButton = new javax.swing.JRadioButton();
        nyyRadioButton = new javax.swing.JRadioButton();
        nxyRadioButton = new javax.swing.JRadioButton();
        mxxRadioButton = new javax.swing.JRadioButton();
        myyRadioButton = new javax.swing.JRadioButton();
        mxyRadioButton = new javax.swing.JRadioButton();
        nalphaRadioButton = new javax.swing.JRadioButton();
        malphaRadioButton = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTable = new de.elamx.clt.cutoutui.CutoutTable(){

            @Override
            public Dimension getPreferredScrollableViewportSize() {
                Dimension dim = super.getPreferredScrollableViewportSize();
                // here we return the pref height
                dim.height = getPreferredSize().height;
                return dim;
            }
        };
        ObjectRenderer or = new ObjectRenderer();
        or.setHorizontalAlignment(JLabel.LEFT);
        resultTable.setDefaultRenderer(Object.class, or);
        resultTable.setDefaultRenderer(Number.class, new NumberRenderer());
        resultTable.setDefaultRenderer(Double.class, new DoubleRenderer());
        radialRButton = new javax.swing.JRadioButton();
        normalRButton = new javax.swing.JRadioButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.border.title"))); // NOI18N

        resultTypeCombo.setModel(new DefaultComboBoxModel<String>(diagtypes));

        resultButtonGroup.add(nxxRadioButton);
        nxxRadioButton.setText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.nxxRadioButton.text")); // NOI18N

        resultButtonGroup.add(nyyRadioButton);
        nyyRadioButton.setText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.nyyRadioButton.text")); // NOI18N

        resultButtonGroup.add(nxyRadioButton);
        nxyRadioButton.setText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.nxyRadioButton.text")); // NOI18N

        resultButtonGroup.add(mxxRadioButton);
        mxxRadioButton.setText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.mxxRadioButton.text")); // NOI18N

        resultButtonGroup.add(myyRadioButton);
        myyRadioButton.setText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.myyRadioButton.text")); // NOI18N

        resultButtonGroup.add(mxyRadioButton);
        mxyRadioButton.setText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.mxyRadioButton.text")); // NOI18N

        resultButtonGroup.add(nalphaRadioButton);
        nalphaRadioButton.setText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.nalphaRadioButton.text")); // NOI18N

        resultButtonGroup.add(malphaRadioButton);
        malphaRadioButton.setText(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.malphaRadioButton.text")); // NOI18N

        resultTable.setModel(cutoutResultModel);
        jScrollPane1.setViewportView(resultTable);

        displayButtonGroup.add(radialRButton);
        radialRButton.setSelected(true);
        radialRButton.setText("radial");

        displayButtonGroup.add(normalRButton);
        normalRButton.setText("normal");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultTypeCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nalphaRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nxyRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nyyRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nxxRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(myyRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(mxxRadioButton)
                            .addComponent(malphaRadioButton)
                            .addComponent(mxyRadioButton)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(radialRButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(normalRButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(resultTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nxxRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nyyRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nxyRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mxxRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(myyRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mxyRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nalphaRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(malphaRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radialRButton)
                    .addComponent(normalRButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup displayButtonGroup;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton malphaRadioButton;
    private javax.swing.JRadioButton mxxRadioButton;
    private javax.swing.JRadioButton mxyRadioButton;
    private javax.swing.JRadioButton myyRadioButton;
    private javax.swing.JRadioButton nalphaRadioButton;
    private javax.swing.JRadioButton normalRButton;
    private javax.swing.JRadioButton nxxRadioButton;
    private javax.swing.JRadioButton nxyRadioButton;
    private javax.swing.JRadioButton nyyRadioButton;
    private javax.swing.JRadioButton radialRButton;
    private javax.swing.ButtonGroup resultButtonGroup;
    private javax.swing.JTable resultTable;
    private javax.swing.JComboBox<String> resultTypeCombo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CutoutResult result = data.getResult();
        if (result != null) {
            updateResults();
            updateChart();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        updateChart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateChart();
    }

    private void updateChart() {
        clearChart();
        if (resultTypeCombo.getSelectedItem().equals(diagtype_alpha)) {
            buttonAvailable(true);
            displayButtonAvailable(false);
            drawResultAngle();
        } else if (resultTypeCombo.getSelectedItem().equals(diagtype_shape)) {
            buttonAvailable(true);
            displayButtonAvailable(true);
            if (radialRButton.isSelected()) {
                drawResultContour();
            } else {
                drawResultContour2();
            }
        } else if (resultTypeCombo.getSelectedItem().equals(diagtype_geometry)) {
            buttonAvailable(false);
            displayButtonAvailable(false);
            drawCutoutGeometry();
        }
    }

    private void buttonAvailable(boolean val) {
        malphaRadioButton.setEnabled(val);
        mxxRadioButton.setEnabled(val);
        mxyRadioButton.setEnabled(val);
        myyRadioButton.setEnabled(val);
        nalphaRadioButton.setEnabled(val);
        nxxRadioButton.setEnabled(val);
        nxyRadioButton.setEnabled(val);
        nyyRadioButton.setEnabled(val);
    }
    
    private void displayButtonAvailable(boolean val){
        radialRButton.setEnabled(val);
        normalRButton.setEnabled(val);
    }

    private void updateResults() {

        alldataset.removeAllSeries();
        nxxseries.clear();
        nyyseries.clear();
        nxyseries.clear();
        nalphaseries.clear();
        mxxseries.clear();
        myyseries.clear();
        mxyseries.clear();
        malphaseries.clear();

        double[] angles = data.getResult().getAlpha();

        for (int ii = 0; ii < data.getResult().getNxx().length; ii++) {
            nxxseries.add(angles[ii], data.getResult().getNxx()[ii]);
            nyyseries.add(angles[ii], data.getResult().getNyy()[ii]);
            nxyseries.add(angles[ii], data.getResult().getNxy()[ii]);
            nalphaseries.add(angles[ii], data.getResult().getNAlpha()[ii]);
            mxxseries.add(angles[ii], data.getResult().getMxx()[ii]);
            myyseries.add(angles[ii], data.getResult().getMyy()[ii]);
            mxyseries.add(angles[ii], data.getResult().getMxy()[ii]);
            malphaseries.add(angles[ii], data.getResult().getMAlpha()[ii]);
        }

        alldataset.addSeries(nxxseries);
        alldataset.addSeries(nyyseries);
        alldataset.addSeries(nxyseries);
        alldataset.addSeries(nalphaseries);
        alldataset.addSeries(mxxseries);
        alldataset.addSeries(myyseries);
        alldataset.addSeries(mxyseries);
        alldataset.addSeries(malphaseries);

        CutoutResultTableModel ctm = ((CutoutResultTableModel) resultTable.getModel());
        ctm.setRowCount(0);

        ctm.addRow(new Object[]{NbBundle.getMessage(ResultPanel.class, "ResultPanel.nxxRadioButton.text"), alldataset.getSeries(NXX_KEY).getMinY(), alldataset.getSeries(NXX_KEY).getMaxY()});
        ctm.addRow(new Object[]{NbBundle.getMessage(ResultPanel.class, "ResultPanel.nyyRadioButton.text"), alldataset.getSeries(NYY_KEY).getMinY(), alldataset.getSeries(NYY_KEY).getMaxY()});
        ctm.addRow(new Object[]{NbBundle.getMessage(ResultPanel.class, "ResultPanel.nxyRadioButton.text"), alldataset.getSeries(NXY_KEY).getMinY(), alldataset.getSeries(NXY_KEY).getMaxY()});
        ctm.addRow(new Object[]{NbBundle.getMessage(ResultPanel.class, "ResultPanel.mxxRadioButton.text"), alldataset.getSeries(MXX_KEY).getMinY(), alldataset.getSeries(MXX_KEY).getMaxY()});
        ctm.addRow(new Object[]{NbBundle.getMessage(ResultPanel.class, "ResultPanel.myyRadioButton.text"), alldataset.getSeries(MYY_KEY).getMinY(), alldataset.getSeries(MYY_KEY).getMaxY()});
        ctm.addRow(new Object[]{NbBundle.getMessage(ResultPanel.class, "ResultPanel.mxyRadioButton.text"), alldataset.getSeries(MXY_KEY).getMinY(), alldataset.getSeries(MXY_KEY).getMaxY()});
    }

    private void clearChart() {
        if (paintdataset != null) {
            paintdataset.removeAllSeries();
        } else {
            paintdataset = new XYSeriesCollection();
        }
        chart.getXYPlot().clearAnnotations();
        chart.getXYPlot().clearDomainAxes();
        chart.getXYPlot().clearRangeAxes();
    }

    /**
     * Zeichnet das Ergebnis in ein Diagramm über Theta.
     *
     * @param Punkte Ergebnis das dargestellt werden soll
     * @param name Name des Ergebnis
     */
    public void drawResultAngle() {

        String title;
        XYSeries series;

        if (nxxRadioButton.isSelected()) {
            series = nxxseries;
            title = NXX_KEY;
        } else if (nyyRadioButton.isSelected()) {
            series = nyyseries;
            title = NYY_KEY;
        } else if (nxyRadioButton.isSelected()) {
            series = nxyseries;
            title = NXY_KEY;
        } else if (nalphaRadioButton.isSelected()) {
            series = nalphaseries;
            title = NALPHA_KEY;
        } else if (mxxRadioButton.isSelected()) {
            series = mxxseries;
            title = MXX_KEY;
        } else if (myyRadioButton.isSelected()) {
            series = myyseries;
            title = MYY_KEY;
        } else if (mxyRadioButton.isSelected()) {
            series = mxyseries;
            title = MXY_KEY;
        } else if (malphaRadioButton.isSelected()) {
            series = malphaseries;
            title = MALPHA_KEY;
        } else {
            series = nxxseries;
            title = NXX_KEY;
            nxxRadioButton.setSelected(true);
        }

        double maxy, miny;
        if (Math.abs(series.getMaxY()) > Math.abs(series.getMinY())) {
            maxy = series.getMaxY() * Y_AXIS_OVERLAP;
            miny = -series.getMaxY() * Y_AXIS_OVERLAP;
        } else if (Math.abs(series.getMaxY()) < Math.abs(series.getMinY())) {
            maxy = -series.getMinY() * Y_AXIS_OVERLAP;
            miny = series.getMinY() * Y_AXIS_OVERLAP;
        } else {
            maxy = series.getMaxY() + 1.0;
            miny = series.getMinY() - 1.0;
        }

        ((XYPlotI) chart.getXYPlot()).useQuadraticDataArea(false);
        ((XYPlotI) chart.getXYPlot()).useEqualAxes(false);
        ((XYPlotI) chart.getXYPlot()).useEqualTicks(false);
        
        chart.getXYPlot().setDomainAxis(new NumberAxis());
        chart.getXYPlot().setRangeAxis(new NumberAxis());
        
        eLamXChartTheme.getInstance().apply(chart);
        
        Font font = chart.getXYPlot().getDomainAxis().getTickLabelFont();

        AttributedString captionX = new AttributedString("\u03B1 [°]");         // alpha
        captionX.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionX.addAttribute(TextAttribute.SIZE, font.getSize());

        AttributedString captionY = new AttributedString(title);
        captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, title.length());
        captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionY.addAttribute(TextAttribute.SIZE, font.getSize());

        chart.getXYPlot().getDomainAxis().setRange(0.0, 360.0);
        chart.getXYPlot().getDomainAxis().setLowerMargin(0.0);
        chart.getXYPlot().getDomainAxis().setUpperMargin(0.0);
        chart.getXYPlot().getDomainAxis().setAttributedLabel(captionX);

        chart.getXYPlot().getRangeAxis().setRange(miny, maxy);
        chart.getXYPlot().getRangeAxis().setLowerMargin(0.0);
        chart.getXYPlot().getRangeAxis().setUpperMargin(0.0);
        chart.getXYPlot().getRangeAxis().setAttributedLabel(captionY);
        ((NumberAxis)chart.getXYPlot().getRangeAxis()).setStandardTickUnits(new eLamXNumberTickUnitSource());
        paintdataset.addSeries(series);
        chart.getXYPlot().setDataset(paintdataset);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLACK);
        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke((float) 1.5));
        chart.getXYPlot().getRenderer().setDefaultToolTipGenerator(new StandardXYToolTipGenerator());

        chart.removeLegend();
    }

    /**
     * Zeichnet das Ergebnis entlang der Ausschnittkontur.
     *
     * Dazu wird die Lochkontur berechnet und zum Ortsvektor an jedem Punkt der
     * Lochkontur der Wert der anzuzeigenden Größe dazuaddiert.
     */
    public void drawResultContour() {

        CutoutGeometry cg = data.getResult().getInput().getCutoutGeometry();
        double[][] geoxy = cg.getSimplifiedGeometry(data.getResult().getAlpha());
        XYSeries series;

        if (nxxRadioButton.isSelected()) {
            series = nxxseries;
        } else if (nyyRadioButton.isSelected()) {
            series = nyyseries;
        } else if (nxyRadioButton.isSelected()) {
            series = nxyseries;
        } else if (nalphaRadioButton.isSelected()) {
            series = nalphaseries;
        } else if (mxxRadioButton.isSelected()) {
            series = mxxseries;
        } else if (myyRadioButton.isSelected()) {
            series = myyseries;
        } else if (mxyRadioButton.isSelected()) {
            series = mxyseries;
        } else if (malphaRadioButton.isSelected()) {
            series = malphaseries;
        } else {
            series = nxxseries;
            nxxRadioButton.setSelected(true);
        }

        // CutoutGeometry-Daten
        XYSeries cgseries = new XYSeries(GEO_KEY, false);
        XYSeries valseries;
        ArrayList<XYSeries> posSeries = new ArrayList<>();
        ArrayList<XYSeries> negSeries = new ArrayList<>();

        // Renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        int posseriescount = 0;
        int negseriescount = 0;
        boolean lastpos;
        for (int ii = 0; ii < data.getResult().getNxx().length; ii++) {
            cgseries.add(geoxy[0][ii], geoxy[1][ii]);
        }

        if (series.getY(0).doubleValue() > 0.0) {
            valseries = new XYSeries(POS_KEY, false);
            posseriescount++;
            lastpos = true;
        } else {
            valseries = new XYSeries(NEG_KEY, false);
            negseriescount++;
            lastpos = false;
        }

        // scale values to cutout geometry
        double scale;
        if (cgseries.getMaxX() > cgseries.getMaxY()) {
            scale = cgseries.getMaxX() / Math.max(Math.abs(series.getMaxY()), Math.abs(series.getMinY()));
        } else {
            scale = cgseries.getMaxY() / Math.max(Math.abs(series.getMaxY()), Math.abs(series.getMinY()));
        }

        addValue(valseries, geoxy[0][0], geoxy[1][0], series.getX(0).doubleValue(), series.getY(0).doubleValue(), scale);

        // add values
        for (int ii = 1; ii < data.getResult().getNxx().length; ii++) {
            if (series.getY(ii).doubleValue() >= 0.0 && !lastpos) {              // Wert positiv, vorher negativ
                addValue(valseries, geoxy[0][ii], geoxy[1][ii], series.getX(ii).doubleValue(), 0.0, scale);
                negSeries.add(valseries);
                if (posseriescount == 0) {
                    valseries = new XYSeries(POS_KEY, false);
                } else {
                    valseries = new XYSeries((POS_KEY + posseriescount), false);
                }
                addValue(valseries, geoxy[0][ii - 1], geoxy[1][ii - 1], series.getX(ii - 1).doubleValue(), 0.0, scale);
                posseriescount++;
                lastpos = true;
            } else if (series.getY(ii).doubleValue() < 0.0 && lastpos) {          // Wert negativ, vorher positiv
                addValue(valseries, geoxy[0][ii], geoxy[1][ii], series.getX(ii).doubleValue(), 0.0, scale);
                posSeries.add(valseries);
                if (negseriescount == 0) {
                    valseries = new XYSeries(NEG_KEY, false);
                } else {
                    valseries = new XYSeries((NEG_KEY + posseriescount), false);
                }
                addValue(valseries, geoxy[0][ii - 1], geoxy[1][ii - 1], series.getX(ii - 1).doubleValue(), 0.0, scale);
                negseriescount++;
                lastpos = false;
            }
            addValue(valseries, geoxy[0][ii], geoxy[1][ii], series.getX(ii).doubleValue(), series.getY(ii).doubleValue(), scale);
        }

        if (lastpos) {
            posSeries.add(valseries);
        } else {
            negSeries.add(valseries);
        }

        // Max-Dimension
        double max = cgseries.getMaxX();                                       // kann nicht kleiner als 0 werden, da |neg| werte
        double maxy = cgseries.getMaxY();
        for (XYSeries s : posSeries) {
            if (s.getMaxX() > max) {
                max = s.getMaxX();
            }
            if (s.getMaxY() > maxy) {
                maxy = s.getMaxY();
            }
        }
        for (XYSeries s : negSeries) {
            if (s.getMaxX() > max) {
                max = s.getMaxX();
            }
            if (s.getMaxY() > maxy) {
                maxy = s.getMaxY();
            }
        }

        if (maxy > max) {
            max = maxy;
        }
        max *= X_AXIS_OVERLAP;
        
        chart.getXYPlot().setDomainAxis(new NumberAxis());
        chart.getXYPlot().setRangeAxis(new NumberAxis());
        
        eLamXChartTheme.getInstance().apply(chart);
        
        Font font = chart.getXYPlot().getDomainAxis().getTickLabelFont();

        // Chart-Preferences
        AttributedString captionX = new AttributedString("x");
        captionX.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionX.addAttribute(TextAttribute.SIZE, font.getSize());

        AttributedString captionY = new AttributedString("y");
        captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionY.addAttribute(TextAttribute.SIZE, font.getSize());

        ((XYPlotI) chart.getXYPlot()).useQuadraticDataArea(false);
        ((XYPlotI) chart.getXYPlot()).useEqualAxes(true);
        ((XYPlotI) chart.getXYPlot()).useEqualTicks(true);

        chart.getXYPlot().getDomainAxis().setAxisLineVisible(false);
        chart.getXYPlot().getDomainAxis().setTickMarksVisible(false);
        chart.getXYPlot().getDomainAxis().setTickLabelsVisible(false);
        chart.getXYPlot().getDomainAxis().setAttributedLabel(captionX);
        ((NumberAxis)chart.getXYPlot().getDomainAxis()).setStandardTickUnits(new eLamXNumberTickUnitSource());

        chart.getXYPlot().getRangeAxis().setAxisLineVisible(false);
        chart.getXYPlot().getRangeAxis().setTickMarksVisible(false);
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible(false);
        chart.getXYPlot().getRangeAxis().setAttributedLabel(captionY);
        ((NumberAxis)chart.getXYPlot().getRangeAxis()).setStandardTickUnits(new eLamXNumberTickUnitSource());

        // Serien hinzufuegen
        paintdataset.addSeries(cgseries);
        for (XYSeries s : posSeries) {
            paintdataset.addSeries(s);
            renderer.setSeriesPaint(paintdataset.getSeriesCount() - 1, Color.blue);
            renderer.setSeriesShapesVisible(paintdataset.getSeriesCount() - 1, false);
            renderer.setSeriesVisibleInLegend(paintdataset.getSeriesCount() - 1, false);
        }
        for (XYSeries s : negSeries) {
            paintdataset.addSeries(s);
            renderer.setSeriesPaint(paintdataset.getSeriesCount() - 1, Color.red);
            renderer.setSeriesShapesVisible(paintdataset.getSeriesCount() - 1, false);
            renderer.setSeriesVisibleInLegend(paintdataset.getSeriesCount() - 1, false);
        }

        renderer.setSeriesPaint(paintdataset.getSeriesIndex(cgseries.getKey()), Color.BLACK);
        renderer.setSeriesShapesVisible(paintdataset.getSeriesIndex(cgseries.getKey()), false);

        // erste Serien + und - in Legende anzeigen
        if (!posSeries.isEmpty()) {
            renderer.setSeriesVisibleInLegend(paintdataset.getSeriesIndex(posSeries.get(0).getKey()), true);
        }
        if (!negSeries.isEmpty()) {
            renderer.setSeriesVisibleInLegend(paintdataset.getSeriesIndex(negSeries.get(0).getKey()), true);
        }

        chart.getXYPlot().setRenderer(paintdataset.getSeriesIndex(cgseries.getKey()), renderer);
        chart.getXYPlot().setDataset(paintdataset);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLACK);
        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke((float) 1.5));

        LegendTitle lt = new LegendTitle(chart.getXYPlot());
        chart.removeLegend();
        chart.addLegend(lt);
        chart.getLegend().setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
        chart.getLegend().setFrame(new LineBorder());
        chart.getLegend().setPosition(RectangleEdge.BOTTOM);
        chart.getLegend().setItemFont(font);

    }

    private void addValue(XYSeries s, double geox, double geoy, double angle, double val, double scale) {
        s.add(geox + Math.abs(val) * Math.cos(Math.toRadians(angle)) * scale,
                geoy + Math.abs(val) * Math.sin(Math.toRadians(angle)) * scale);
    }

    /**
     * Zeichnet das Ergebnis entlang der Ausschnittkontur.
     *
     * Dazu wird die Lochkontur berechnet und zum Ortsvektor an jedem Punkt der
     * Lochkontur der Wert der anzuzeigenden Größe dazuaddiert.
     */
    public void drawResultContour2() {

        CutoutGeometry cg = data.getResult().getInput().getCutoutGeometry();
        double[][] geoxy = cg.getSimplifiedGeometry(data.getResult().getAlpha());
        XYSeries series;

        if (nxxRadioButton.isSelected()) {
            series = nxxseries;
        } else if (nyyRadioButton.isSelected()) {
            series = nyyseries;
        } else if (nxyRadioButton.isSelected()) {
            series = nxyseries;
        } else if (nalphaRadioButton.isSelected()) {
            series = nalphaseries;
        } else if (mxxRadioButton.isSelected()) {
            series = mxxseries;
        } else if (myyRadioButton.isSelected()) {
            series = myyseries;
        } else if (mxyRadioButton.isSelected()) {
            series = mxyseries;
        } else if (malphaRadioButton.isSelected()) {
            series = malphaseries;
        } else {
            series = nxxseries;
            nxxRadioButton.setSelected(true);
        }

        // CutoutGeometry-Daten
        XYSeries cgseries = new XYSeries(GEO_KEY, false);

        // Renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        for (int ii = 0; ii < data.getResult().getNxx().length; ii++) {
            cgseries.add(geoxy[0][ii], geoxy[1][ii]);
        }

        // scale values to cutout geometry
        double scale;
        if (cgseries.getMaxX() > cgseries.getMaxY()) {
            scale = cgseries.getMaxX() / Math.max(Math.abs(series.getMaxY()), Math.abs(series.getMinY()));
        } else {
            scale = cgseries.getMaxY() / Math.max(Math.abs(series.getMaxY()), Math.abs(series.getMinY()));
        }

        XYSeries posSerie = new XYSeries(POS_KEY, false);
        XYSeries negSerie = new XYSeries(NEG_KEY, false);

        double[] values;

        int numVals = geoxy[0].length;
        double val = series.getY(0).doubleValue();
        values = getValueNormal(geoxy[0][numVals - 1], geoxy[1][numVals - 1], geoxy[0][0], geoxy[1][0], geoxy[0][1], geoxy[1][1], val, scale);
        if (val >= 0.0) {
            posSerie.add(values[0], values[1]);
            negSerie.add(geoxy[0][0], geoxy[1][0]);
        } else {
            posSerie.add(geoxy[0][0], geoxy[1][0]);
            negSerie.add(values[0], values[1]);
        }

        for (int ii = 1; ii < numVals - 1; ii++) {
            val = series.getY(ii).doubleValue();
            values = getValueNormal(geoxy[0][ii - 1], geoxy[1][ii - 1], geoxy[0][ii], geoxy[1][ii], geoxy[0][ii + 1], geoxy[1][ii + 1], val, scale);
            if (val >= 0.0) {
                posSerie.add(values[0], values[1]);
                negSerie.add(geoxy[0][ii], geoxy[1][ii]);
            } else {
                posSerie.add(geoxy[0][ii], geoxy[1][ii]);
                negSerie.add(values[0], values[1]);
            }
        }

        val = series.getY(numVals - 1).doubleValue();
        values = getValueNormal(geoxy[0][numVals - 2], geoxy[1][numVals - 2], geoxy[0][numVals - 1], geoxy[1][numVals - 1], geoxy[0][0], geoxy[1][0], val, scale);
        if (val >= 0.0) {
            posSerie.add(values[0], values[1]);
            negSerie.add(geoxy[0][numVals - 1], geoxy[1][numVals - 1]);
        } else {
            posSerie.add(geoxy[0][numVals - 1], geoxy[1][numVals - 1]);
            negSerie.add(values[0], values[1]);
        }

        // Max-Dimension
        double max = cgseries.getMaxX();                                       // kann nicht kleiner als 0 werden, da |neg| werte
        double maxy = cgseries.getMaxY();
        if (posSerie.getMaxX() > max) {
            max = posSerie.getMaxX();
        }
        if (posSerie.getMaxY() > maxy) {
            maxy = posSerie.getMaxY();
        }
        if (negSerie.getMaxX() > max) {
            max = negSerie.getMaxX();
        }
        if (negSerie.getMaxY() > maxy) {
            maxy = negSerie.getMaxY();
        }

        if (maxy > max) {
            max = maxy;
        }
        max *= X_AXIS_OVERLAP;
        
        chart.getXYPlot().setDomainAxis(new NumberAxis());
        chart.getXYPlot().setRangeAxis(new NumberAxis());
        
        eLamXChartTheme.getInstance().apply(chart);
        
        Font font = chart.getXYPlot().getDomainAxis().getTickLabelFont();

        // Chart-Preferences
        AttributedString captionX = new AttributedString("x");
        captionX.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionX.addAttribute(TextAttribute.SIZE, font.getSize());

        AttributedString captionY = new AttributedString("y");
        captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionY.addAttribute(TextAttribute.SIZE, font.getSize());

        ((XYPlotI) chart.getXYPlot()).useQuadraticDataArea(false);
        ((XYPlotI) chart.getXYPlot()).useEqualAxes(true);
        ((XYPlotI) chart.getXYPlot()).useEqualTicks(true);

        chart.getXYPlot().getDomainAxis().setAxisLineVisible(false);
        chart.getXYPlot().getDomainAxis().setTickMarksVisible(false);
        chart.getXYPlot().getDomainAxis().setTickLabelsVisible(false);
        chart.getXYPlot().getDomainAxis().setAttributedLabel(captionX);
        ((NumberAxis)chart.getXYPlot().getDomainAxis()).setStandardTickUnits(new eLamXNumberTickUnitSource());

        chart.getXYPlot().getRangeAxis().setAxisLineVisible(false);
        chart.getXYPlot().getRangeAxis().setTickMarksVisible(false);
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible(false);
        chart.getXYPlot().getRangeAxis().setAttributedLabel(captionY);
        ((NumberAxis)chart.getXYPlot().getRangeAxis()).setStandardTickUnits(new eLamXNumberTickUnitSource());

        // Serien hinzufuegen
        paintdataset.addSeries(cgseries);
        paintdataset.addSeries(posSerie);
        renderer.setSeriesPaint(paintdataset.getSeriesCount() - 1, Color.blue);
        renderer.setSeriesShapesVisible(paintdataset.getSeriesCount() - 1, false);
        paintdataset.addSeries(negSerie);
        renderer.setSeriesPaint(paintdataset.getSeriesCount() - 1, Color.red);
        renderer.setSeriesShapesVisible(paintdataset.getSeriesCount() - 1, false);

        renderer.setSeriesPaint(paintdataset.getSeriesIndex(cgseries.getKey()), Color.BLACK);
        renderer.setSeriesShapesVisible(paintdataset.getSeriesIndex(cgseries.getKey()), false);

        chart.getXYPlot().setRenderer(paintdataset.getSeriesIndex(cgseries.getKey()), renderer);
        chart.getXYPlot().setDataset(paintdataset);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLACK);
        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke((float) 1.5));

        LegendTitle lt = new LegendTitle(chart.getXYPlot());
        chart.removeLegend();
        chart.addLegend(lt);
        chart.getLegend().setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
        chart.getLegend().setFrame(new LineBorder());
        chart.getLegend().setPosition(RectangleEdge.BOTTOM);
        chart.getLegend().setItemFont(font);

    }

    private double[] getValueNormal(double geox_before, double geoy_before, double geox, double geoy, double geox_after, double geoy_after, double val, double scale) {
        double dx = geox_after - geox_before;
        double dy = geoy_after - geoy_before;

        double abs = Math.sqrt(dx * dx + dy * dy);

        dx /= abs;
        dy /= abs;

        double[] ret = new double[2];
        ret[0] = geox + Math.abs(val) * dy * scale;
        ret[1] = geoy + Math.abs(val) * -dx * scale;

        return ret;
    }

    /**
     * Zeichnet den ersten Quadranten der CutoutGeometry.
     *
     * Wird aus den Koeffizienten der Geometrie berechnet.
     *
     * z=x+i*y=R*(zeta+sum_k(m_k/zeta^k)) (1) Ukadgaonker
     */
    private void drawCutoutGeometry() {

        CutoutGeometry cg = data.getResult().getInput().getCutoutGeometry();
        cg.calcScaledComplexGeometry();
        double[][] geo = cg.getScaledComplexGeometry();
        XYSeries geoseries = new XYSeries(GEO_KEY, false);
        for (double[] geo1 : geo) {
            geoseries.add(geo1[0], geo1[1]);
        }

        XYSeries anglearrowseries = new XYSeries("arrow", false);
        double arrowangle = Math.toRadians(20.0);
        double[] arrowtip = cg.getScaledComplexCoordinates(arrowangle);
        anglearrowseries.add(0.0, 0.0);
        anglearrowseries.add(arrowtip[0], arrowtip[1]);

        ((XYPlotI) chart.getXYPlot()).useQuadraticDataArea(true);
        ((XYPlotI) chart.getXYPlot()).useEqualAxes(true);
        ((XYPlotI) chart.getXYPlot()).useEqualTicks(true);

        chart.getXYPlot().setDomainAxis(new org.jfree.chart.axis.NumberAxis());
        chart.getXYPlot().setRangeAxis(new org.jfree.chart.axis.NumberAxis());
        
        eLamXChartTheme.getInstance().apply(chart);
        
        Font font = chart.getXYPlot().getDomainAxis().getTickLabelFont();
        
        AttributedString captionX = new AttributedString(NbBundle.getMessage(ResultPanel.class, "ResultPanel.diag.axislabel.xmaxdim"));
        //captionX.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionX.addAttribute(TextAttribute.SIZE, font.getSize());

        AttributedString captionY = new AttributedString(NbBundle.getMessage(ResultPanel.class, "ResultPanel.diag.axislabel.ymaxdim"));
        //captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 1);
        captionY.addAttribute(TextAttribute.SIZE, font.getSize());
        chart.getXYPlot().getDomainAxis().setRange(0.0, X_AXIS_OVERLAP);
        chart.getXYPlot().getDomainAxis().setLowerMargin(0.0);
        chart.getXYPlot().getDomainAxis().setUpperMargin(0.0);
        chart.getXYPlot().getDomainAxis().setAttributedLabel(captionX);
        ((NumberAxis) (chart.getXYPlot().getDomainAxis())).setTickUnit(new NumberTickUnit(0.05, NumberFormat.getNumberInstance(GlobalProperties.getDefault().getActualLocale())));
        
        chart.getXYPlot().getRangeAxis().setRange(0, Y_AXIS_OVERLAP);
        chart.getXYPlot().getRangeAxis().setLowerMargin(0.0);
        chart.getXYPlot().getRangeAxis().setUpperMargin(0.0);
        chart.getXYPlot().getRangeAxis().setAttributedLabel(captionY);
        ((NumberAxis) (chart.getXYPlot().getRangeAxis())).setTickUnit(new NumberTickUnit(0.05, NumberFormat.getNumberInstance(GlobalProperties.getDefault().getActualLocale())));

        paintdataset.addSeries(geoseries);
        paintdataset.addSeries(anglearrowseries);
        chart.getXYPlot().setDataset(paintdataset);

        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLACK);
        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke((float) 1.5));

        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.BLACK);
        chart.getXYPlot().getRenderer().setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));

        XYPointerAnnotation arrow = new XYPointerAnnotation("", arrowtip[0], arrowtip[1], Math.PI - arrowangle);
        arrow.setTipRadius(0.0);
        arrow.setBaseRadius(1.0);
        chart.getXYPlot().addAnnotation(arrow);

        XYTextAnnotation arrowLabel = new XYTextAnnotation(ALPHA_KEY, arrowtip[0] / 2.0, arrowtip[1] / 4.0);
        arrowLabel.setFont(font);
        chart.getXYPlot().addAnnotation(arrowLabel);

        chart.removeLegend();
    }

    public void cleanup() {
        if (data != null) {
            data.removePropertyChangeListener(CutoutModuleData.PROP_RESULT, this);
        }
    }

    public void initListeners() {
        nxxRadioButton.addActionListener(this);
        nyyRadioButton.addActionListener(this);
        nxyRadioButton.addActionListener(this);
        nalphaRadioButton.addActionListener(this);
        mxxRadioButton.addActionListener(this);
        myyRadioButton.addActionListener(this);
        mxyRadioButton.addActionListener(this);
        malphaRadioButton.addActionListener(this);
        
        radialRButton.addActionListener(this);
        normalRButton.addActionListener(this);

        resultTypeCombo.addItemListener(this);
    }

    /**
     * Default Renderers
     *
     */
    class NumberRenderer extends ObjectRenderer {

        public NumberRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
        }
    }

    class DoubleRenderer extends NumberRenderer {

        NumberFormat formatter;

        public DoubleRenderer() {
            super();
        }

        @Override
        public void setValue(Object value) {
            if (formatter == null) {
                formatter = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_DOUBLE);
            }
            setText((value == null) ? "" : formatter.format(value));
        }
    }

    class ObjectRenderer extends DefaultTableCellRenderer.UIResource {

        boolean isSelected = false;
        boolean isFailed = false;
        int index = 0;
        Color bgColor[] = new Color[]{new Color(255, 255, 255, 255), new Color(240, 240, 240, 255)};
        Color selectionColor[] = new Color[2];

        {
            // we'll use a translucent version of the table's default
            // selection color to paint selections
            Color oldCol = resultTable.getSelectionBackground();
            selectionColor[0] = new Color(oldCol.getRed(), oldCol.getGreen(), oldCol.getBlue(), 255);
            selectionColor[1] = new Color(oldCol.getRed() < 15 ? oldCol.getRed()+15 : oldCol.getRed()-15, 
                                          oldCol.getGreen() < 15 ? oldCol.getGreen()+15 : oldCol.getGreen()-15, 
                                          oldCol.getBlue() < 15 ? oldCol.getBlue()+15 : oldCol.getBlue()-15,
                                          255);
            // need to be non-opaque since we'll be translucent
            setOpaque(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // save the selected state since we'll need it when painting
            this.isSelected = isSelected;
            this.index = row % 2;
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        // since DefaultTableCellRenderer is really just a JLabel, we can override
        // paintComponent to paint the translucent selection when necessary
        @Override
        public void paintComponent(Graphics g) {
            if (isSelected) {
                g.setColor(selectionColor[index]);
            } else {
                g.setColor(bgColor[index]);
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }
}
