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
package de.elamx.clt.calculation.polar;

import de.elamx.clt.CLTRefreshListener;
import de.elamx.clt.CLT_Laminate;
import de.elamx.core.GlobalProperties;
import de.elamx.core.RawDataExportService;
import de.elamx.core.SnapshotService;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import de.elamx.mathtools.MatrixTools;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.eLamXChartPanel;
import org.jfree.eLamXChartTheme;
import org.jfree.eLamXNumberTickUnitSource;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "CLT_PolarChartTopComponent",
        iconBase = "de/elamx/clt/calculation/resources/polarchart.png"
)
public final class CLT_PolarChartTopComponent extends TopComponent implements LookupListener, CLTRefreshListener, PropertyChangeListener {

    public final static Set<Laminat> uniqueLaminates = new HashSet<Laminat>();
    private final Laminat laminat;
    private final CLT_Laminate clt_lam;
    private final Lookup.Result<Laminat> result;
    private final boolean[] showATerm;
    private PolarPlot plot;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    private final Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE, Color.GREEN};
    private final BasicStroke basicStrokeAMat = new BasicStroke(1.0f);
    private final BasicStroke basicStrokeBMat = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{8.0f, 8.0f}, 0.0f);
    private final BasicStroke basicStrokeDMat = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4.0f, 3.0f}, 0.0f);

    public CLT_PolarChartTopComponent(Laminat laminat) {
        this.laminat = laminat;
        this.laminat.addPropertyChangeListener(this);
        CLT_Laminate tClt_lam = laminat.getLookup().lookup(CLT_Laminate.class);
        if (tClt_lam == null) {
            clt_lam = new CLT_Laminate(laminat);
        } else {
            clt_lam = tClt_lam;
        }
        clt_lam.addCLTRefreshListener(this);
        result = eLamXLookup.getDefault().lookupResult(Laminat.class);
        result.addLookupListener(this);
        showATerm = new boolean[]{NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.A11", true),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.A12", true),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.A22", true),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.A66", true),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.B11", false),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.B12", false),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.B22", false),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.B66", false),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.D11", false),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.D12", false),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.D22", false),
            NbPreferences.forModule(CLT_PolarChartTopComponent.class).getBoolean("CLT_PolarChartTopComponent.D66", false)};
        initComponents();
        setName(NbBundle.getMessage(CLT_PolarChartTopComponent.class, "CTL_CLT_PolarChartTopComponent", laminat.getName()));
        setToolTipText(NbBundle.getMessage(CLT_PolarChartTopComponent.class, "HINT_CLT_PolarChartTopComponent"));
        associateLookup(Lookups.fixed(laminat, new PolarSnapshot(), new RawDataExport()));
        this.initChart();
        setValues();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        laminat.removePropertyChangeListener(this);
        clt_lam.removeCLTRefreshListener(this);
        uniqueLaminates.remove(laminat);
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (!result.allInstances().contains(laminat)) {
            this.close();
        }
    }
    XYSeries[] series = new XYSeries[12];

    private void initChart() {
        XYDataset dataset = createDataset();
        chart = createChart(dataset);

        chartPanel = new eLamXChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 600));
        JPopupMenu menu = new JPopupMenu();

        JMenuItem a11 = new JCheckBoxMenuItem("<html>A<sub>11</sub></html>");
        a11.setSelected(showATerm[0]);
        a11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[0] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.A11", showATerm[0]);
            }
        });
        menu.add(a11);

        JMenuItem a12 = new JCheckBoxMenuItem("<html>A<sub>12</sub></html>");
        a12.setSelected(showATerm[1]);
        a12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[1] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.A12", showATerm[1]);
            }
        });
        menu.add(a12);

        JMenuItem a22 = new JCheckBoxMenuItem("<html>A<sub>22</sub></html>");
        a22.setSelected(showATerm[2]);
        a22.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[2] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.A22", showATerm[2]);
            }
        });
        menu.add(a22);

        JMenuItem a66 = new JCheckBoxMenuItem("<html>A<sub>66</sub></html>");
        a66.setSelected(showATerm[3]);
        a66.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[3] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.A66", showATerm[3]);
            }
        });
        menu.add(a66);

        JMenuItem b11 = new JCheckBoxMenuItem("<html>B<sub>11</sub></html>");
        b11.setSelected(showATerm[4]);
        b11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[4] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.B11", showATerm[4]);
            }
        });
        menu.add(b11);

        JMenuItem b12 = new JCheckBoxMenuItem("<html>B<sub>12</sub></html>");
        b12.setSelected(showATerm[5]);
        b12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[5] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.B12", showATerm[5]);
            }
        });
        menu.add(b12);

        JMenuItem b22 = new JCheckBoxMenuItem("<html>B<sub>22</sub></html>");
        b22.setSelected(showATerm[6]);
        b22.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[6] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.B22", showATerm[6]);
            }
        });
        menu.add(b22);

        JMenuItem b66 = new JCheckBoxMenuItem("<html>B<sub>66</sub></html>");
        b66.setSelected(showATerm[7]);
        b66.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[7] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.B66", showATerm[7]);
            }
        });
        menu.add(b66);

        JMenuItem d11 = new JCheckBoxMenuItem("<html>D<sub>11</sub></html>");
        d11.setSelected(showATerm[8]);
        d11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[8] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.D11", showATerm[8]);
            }
        });
        menu.add(d11);

        JMenuItem d12 = new JCheckBoxMenuItem("<html>D<sub>12</sub></html>");
        d12.setSelected(showATerm[9]);
        d12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[9] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.D12", showATerm[9]);
            }
        });
        menu.add(d12);

        JMenuItem d22 = new JCheckBoxMenuItem("<html>D<sub>22</sub></html>");
        d22.setSelected(showATerm[10]);
        d22.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[10] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.D22", showATerm[10]);
            }
        });
        menu.add(d22);

        JMenuItem d66 = new JCheckBoxMenuItem("<html>D<sub>66</sub></html>");
        d66.setSelected(showATerm[11]);
        d66.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showATerm[11] = ((JCheckBoxMenuItem) event.getSource()).isSelected();
                updateDataset();
                NbPreferences.forModule(CLT_PolarChartTopComponent.class).putBoolean("CLT_PolarChartTopComponent.D66", showATerm[11]);
            }
        });
        menu.add(d66);

        chartPanel.setPopupMenu(menu);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

        updateDataset();
    }
    private XYSeriesCollection data;

    private XYDataset createDataset() {
        data = new XYSeriesCollection();
        series[0] = new XYSeries("A11");
        series[1] = new XYSeries("A12");
        series[2] = new XYSeries("A22");
        series[3] = new XYSeries("A66");
        series[4] = new XYSeries("B11");
        series[5] = new XYSeries("B12");
        series[6] = new XYSeries("B22");
        series[7] = new XYSeries("B66");
        series[8] = new XYSeries("D11");
        series[9] = new XYSeries("D12");
        series[10] = new XYSeries("D22");
        series[11] = new XYSeries("D66");
        for (XYSeries s : series) {
            data.addSeries(s);
        }
        return data;
    }

    private void updateDataset() {
        data.removeAllSeries();
        int index = 0;
        for (int ii = 0; ii < showATerm.length; ii++) {
            if (showATerm[ii]) {
                data.addSeries(series[ii]);
                ((DefaultPolarItemRenderer) plot.getRenderer()).setSeriesPaint(index, colors[ii]);
                if (ii <= 3) {
                    ((DefaultPolarItemRenderer) plot.getRenderer()).setSeriesStroke(index, basicStrokeAMat);
                }else if (ii >= 4 && ii <= 7) {
                    ((DefaultPolarItemRenderer) plot.getRenderer()).setSeriesStroke(index, basicStrokeBMat);
                }else if (ii >= 8) {
                    ((DefaultPolarItemRenderer) plot.getRenderer()).setSeriesStroke(index, basicStrokeDMat);
                }
                index++;
            }
        }
    }

    private XYSeries getDistribution(CLT_Laminate laminate, XYSeries series, int index) {
        double deltaAngle = 1.0;
        int number = (int) (360 / deltaAngle);

        if (index <= 4) {
            // A-Matrix
            double[][] distribution = MatrixTools.getMatrixComponentsOverAngle(laminate.getAMatrix(), deltaAngle);
            for (int i = 0; i < number; i++) {
                series.add(distribution[0][i], distribution[index][i]);
            }
        } else if (index >= 5 && index <= 8) {
            // B-Matrix
            double[][] distribution = MatrixTools.getMatrixComponentsOverAngle(laminate.getBMatrix(), deltaAngle);
            for (int i = 0; i < number; i++) {
                series.add(distribution[0][i], distribution[index - 4][i]);
            }
        } else if (index >= 9) {

            // D-Matrix
            double[][] distribution = MatrixTools.getMatrixComponentsOverAngle(laminate.getDMatrix(), deltaAngle);
            for (int i = 0; i < number; i++) {
                series.add(distribution[0][i], distribution[index - 8][i]);
            }
        }
        return series;
    }

    private JFreeChart createChart(XYDataset dataset) {

        plot = new PolarPlot() {
            @Override
            protected List<NumberTick> refreshAngleTicks() {
                List<NumberTick> ticks = new ArrayList<>();
                for (double currentTickVal = 0.0; currentTickVal < 360.0;
                        currentTickVal += this.getAngleTickUnit().getSize()) {
                    double tp = 360.0 - currentTickVal;

                    double sign = Math.signum(tp);
                    while (Math.abs(tp) > 90.0) {
                        tp -= sign * 180.0;
                    }

                    NumberTick tick = new NumberTick(currentTickVal,
                            this.getAngleTickUnit().valueToString(tp),
                            TextAnchor.CENTER, TextAnchor.CENTER, 0.0);
                    ticks.add(tick);
                }
                return ticks;
            }
        };

        plot.setDataset(dataset);
        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setTickLabelInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        rangeAxis.setStandardTickUnits(new eLamXNumberTickUnitSource());
        plot.setAxis(rangeAxis);
        plot.setRenderer(new DefaultPolarItemRenderer());
        JFreeChart chart = new JFreeChart("", eLamXChartTheme.getInstance().getExtraLargeFont(), plot, true);

        // Formatierung
        ChartFactory.getChartTheme().apply(chart);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(GlobalProperties.getDefault().getActualLocale());
        NumberFormat format = new DecimalFormat(GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE).toPattern() + "°", symbols);

        plot.setAngleTickUnit(new NumberTickUnit(PolarPlot.DEFAULT_ANGLE_TICK_UNIT_SIZE, format));
        DefaultPolarItemRenderer renderer = new DefaultPolarItemRenderer();
        renderer.setShapesVisible(false);
        renderer.setSeriesPaint(0, Color.RED);

        plot.setRenderer(renderer);

        plot.setRadiusGridlinesVisible(true);
        plot.setRadiusMinorGridlinesVisible(false);
        /*plot.setRadiusGridlineStroke(new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        plot.setAngleGridlineStroke( new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        
        plot.setDrawingSupplier(new DefaultDrawingSupplier(){

            @Override
            public Stroke getNextOutlineStroke() {
                return new BasicStroke(8.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
            }

            @Override
            public Stroke getNextStroke() {
                return new BasicStroke(8.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
            }
            
        });*/

        return chart;
    }

    public void setValues() {
        for (int ii = 0; ii < series.length; ii++) {
            series[ii].setNotify(false);
            series[ii].clear();
            getDistribution(clt_lam, series[ii], ii + 1);
            series[ii].setNotify(true);
        }
    }

    @Override
    public void refreshed() {
        setValues();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Laminat.PROP_NAME)) {
            setName(NbBundle.getMessage(CLT_PolarChartTopComponent.class, "CTL_CLT_PolarChartTopComponent", laminat.getName()));
        }
    }

    private class PolarSnapshot implements SnapshotService {

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
                String ls = System.getProperty("line.separator");
                String[] caption = new String[]{"A11", "A12", "A22", "A66", "B11", "B12", "B22", "B66", "D11", "D12", "D22", "D66"};
                fw.write("Angle");
                for (int ii = 0; ii < showATerm.length; ii++) {
                    if (showATerm[ii]) {
                        fw.write("\t" + caption[ii]);
                    }
                }
                fw.write(ls);

                DecimalFormat df_angle = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE);
                DecimalFormat df_stiff = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STIFFNESS);
                int numRows = series[0].getItemCount();
                for (int row = 0; row < numRows; row++) {
                    fw.write(df_angle.format(series[0].getX(row)));
                    for (int ii = 0; ii < showATerm.length; ii++) {
                        if (showATerm[ii]) {
                            fw.write("\t" + df_stiff.format(series[ii].getY(row)));
                        }
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
