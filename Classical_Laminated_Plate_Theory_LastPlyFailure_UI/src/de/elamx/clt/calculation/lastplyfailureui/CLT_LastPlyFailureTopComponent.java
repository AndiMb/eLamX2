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
package de.elamx.clt.calculation.lastplyfailureui;

import de.elamx.clt.*;
import de.elamx.clt.calculation.LayerResultContainer;
import de.elamx.clt.calculation.calc.ResultTableModel;
import de.elamx.core.GlobalProperties;
import de.elamx.laminate.Laminat;
import de.elamx.utilities.AutoRowHeightTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
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
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "CLT_LastPlyFailureTopComponentTopComponent",
        iconBase = "de/elamx/clt/calculation/lastplyfailureui/resources/LastPlyFailureAnalysis.png"
)
public final class CLT_LastPlyFailureTopComponent extends TopComponent implements LookupListener, CLTRefreshListener, PropertyChangeListener {

    private final InstanceContent ic = new InstanceContent();

    private final LastPlyFailureModuleData data;
    private final CLT_Laminate clt_lam;
    private final Lookup.Result<LastPlyFailureModuleData> result;
    public final static Set<LastPlyFailureModuleData> uniqueLastPlyFailureData = new HashSet<>();
    private CLT_LastPlyFailureResult lpfResult;
    private CLT_LayerResult[] actLayerResults;
    private int actualIterationNumber = 0;
    private int maxIterationNumber = -1;
    private final ResultTableModel tabModel = new ResultTableModel();

    DecimalFormat df_Forces = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);
    DecimalFormat df_RF = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_RESERVE_FACTOR);
    DecimalFormat df_Strain = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRAIN);
    DecimalFormat df_Double = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_DOUBLE);
    DecimalFormat df_SmallDouble = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_SMALL_DOUBLE);

    private final JPopupMenu popupMenu;
    private final AbstractLookup lu = new AbstractLookup(ic);

    private XYSeriesCollection minResDataset = null;
    private XYSeriesCollection actRFminDataset = null;
    private JFreeChart chart = null;
    private ChartPanel chartPanel;

    private final int chartIterOffset = 0;

    public CLT_LastPlyFailureTopComponent(LastPlyFailureModuleData data) {
        this.data = data;
        setName(this.data.getName() + " - " + this.data.getLaminat().getName());
        setToolTipText(NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "HINT_CLT_PressureVesselTopComponent"));
        data.getLaminat().addPropertyChangeListener(this);
        CLT_Laminate tClt_lam = data.getLaminat().getLookup().lookup(CLT_Laminate.class);
        if (tClt_lam == null) {
            clt_lam = new CLT_Laminate(data.getLaminat());
        } else {
            clt_lam = tClt_lam;
        }
        clt_lam.addCLTRefreshListener(this);
        initComponents();
        associateLookup(Lookups.fixed(data, data.getLaminat()));
        table.setMinimumSize(new Dimension(300, 0));
        for (MouseWheelListener mwl : jScrollPane1.getMouseWheelListeners()) {
            jScrollPane1.removeMouseWheelListener(mwl);
        }

        result = data.getLaminat().getLookup().lookupResult(LastPlyFailureModuleData.class);
        result.addLookupListener(this);
        data.addPropertyChangeListener(this);

        popupMenu = Utilities.actionsToPopup(Utilities.actionsForPath("eLamXActions/LayerResultContainer").toArray(new Action[0]), lu);

        MouseListener popupListener = new PopupListener();
        // Add the listener to the JTable:
        table.addMouseListener(popupListener);
        // Add the listener specifically to the header:
        table.getTableHeader().addMouseListener(popupListener);

        initChart();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel12 = new javax.swing.JLabel();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new AutoRowHeightTable(){

            @Override
            public Dimension getPreferredScrollableViewportSize() {
                Dimension dim = super.getPreferredScrollableViewportSize();
                // here we return the pref height
                dim.height = getPreferredSize().height;
                return dim;
            }
        };

        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // set our custom renderer on the JTable
        ObjectRenderer or = new ObjectRenderer();
        or.setHorizontalAlignment(JLabel.RIGHT);
        table.setDefaultRenderer(Object.class, or);
        table.setDefaultRenderer(Number.class, new NumberRenderer());
        table.setDefaultRenderer(Double.class, new DoubleRenderer());
        inputResultPanel = new javax.swing.JPanel();
        inputPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nxField = new javax.swing.JFormattedTextField(df_Forces) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        nxField.setValue(data.getLastPlyFailureInput().getLoad().getN_x());
        nxField.addPropertyChangeListener("value", this);
        jLabel2 = new javax.swing.JLabel();
        nyField = new javax.swing.JFormattedTextField(df_Forces) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        nyField.setValue(data.getLastPlyFailureInput().getLoad().getN_y());
        nyField.addPropertyChangeListener("value", this);
        jLabel3 = new javax.swing.JLabel();
        nxyField = new javax.swing.JFormattedTextField(df_Forces) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        nxyField.setValue(data.getLastPlyFailureInput().getLoad().getN_xy());
        nxyField.addPropertyChangeListener("value", this);
        jLabel4 = new javax.swing.JLabel();
        mxyField = new javax.swing.JFormattedTextField(df_Forces) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        mxyField.setValue(data.getLastPlyFailureInput().getLoad().getM_xy());
        mxyField.addPropertyChangeListener("value", this);
        jLabel5 = new javax.swing.JLabel();
        mxField = new javax.swing.JFormattedTextField(df_Forces) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        mxField.setValue(data.getLastPlyFailureInput().getLoad().getM_x());
        mxField.addPropertyChangeListener("value", this);
        jLabel6 = new javax.swing.JLabel();
        myField = new javax.swing.JFormattedTextField(df_Forces) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        myField.setValue(data.getLastPlyFailureInput().getLoad().getM_y());
        myField.addPropertyChangeListener("value", this);
        calculationButton = new javax.swing.JButton();
        ResultPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        iterationField = new javax.swing.JTextField();
        RFminField = new javax.swing.JTextField();
        failureTypeField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        layerNumberField = new javax.swing.JTextField();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        chartHolderPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jaField = new javax.swing.JFormattedTextField(df_Double) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        jaField.setValue(data.getLastPlyFailureInput().getJ_a());
        jaField.addPropertyChangeListener("value", this);
        DegradeAllOnFibreFailureBox = new javax.swing.JCheckBox();
        DegradeAllOnFibreFailureBox.setSelected(data.getLastPlyFailureInput().isDegradeAllOnFibreFailure());
        jLabel13 = new javax.swing.JLabel();
        degradFactorField = new javax.swing.JFormattedTextField(df_SmallDouble) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        degradFactorField.setValue(data.getLastPlyFailureInput().getDegradationFactor());
        degradFactorField.addPropertyChangeListener("value", this);
        jLabel14 = new javax.swing.JLabel();
        epscritField = new javax.swing.JFormattedTextField(df_Strain) {
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.isTemporary()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    selectAll();
                });
            }
        };
        epscritField.setValue(data.getLastPlyFailureInput().getEpsilon_crit());
        epscritField.addPropertyChangeListener("value", this);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel12.text")); // NOI18N

        setLayout(new java.awt.BorderLayout());

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.tablePanel.border.title"))); // NOI18N

        table.setModel(tabModel);
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
        );

        add(tablePanel, java.awt.BorderLayout.CENTER);

        inputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.inputPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel1.text")); // NOI18N

        nxField.setColumns(8);
        nxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel2.text")); // NOI18N

        nyField.setColumns(8);
        nyField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel3.text")); // NOI18N

        nxyField.setColumns(8);
        nxyField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel4.text")); // NOI18N

        mxyField.setColumns(8);
        mxyField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel5.text")); // NOI18N

        mxField.setColumns(8);
        mxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel6.text")); // NOI18N

        myField.setColumns(8);
        myField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout inputPanelLayout = new javax.swing.GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mxyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nxyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nxyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mxyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(calculationButton, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.calculationButton.text")); // NOI18N
        calculationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculationButtonActionPerformed(evt);
            }
        });

        ResultPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.ResultPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel9.text")); // NOI18N

        iterationField.setEditable(false);
        iterationField.setColumns(8);
        iterationField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        iterationField.setText(org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.iterationField.text")); // NOI18N

        RFminField.setEditable(false);
        RFminField.setColumns(8);
        RFminField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        RFminField.setText(org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.RFminField.text")); // NOI18N

        failureTypeField.setEditable(false);
        failureTypeField.setColumns(8);
        failureTypeField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        failureTypeField.setText(org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.failureTypeField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel10.text")); // NOI18N

        layerNumberField.setEditable(false);
        layerNumberField.setColumns(8);
        layerNumberField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        layerNumberField.setText(org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.layerNumberField.text")); // NOI18N

        javax.swing.GroupLayout ResultPanelLayout = new javax.swing.GroupLayout(ResultPanel);
        ResultPanel.setLayout(ResultPanelLayout);
        ResultPanelLayout.setHorizontalGroup(
            ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(failureTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RFminField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(layerNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iterationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ResultPanelLayout.setVerticalGroup(
            ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(iterationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(layerNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RFminField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(failureTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(previousButton, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.previousButton.text")); // NOI18N
        previousButton.setEnabled(false);
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(nextButton, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.nextButton.text")); // NOI18N
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        chartHolderPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel11.text")); // NOI18N

        jaField.setColumns(8);
        jaField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(DegradeAllOnFibreFailureBox, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.DegradeAllOnFibreFailureBox.text")); // NOI18N
        DegradeAllOnFibreFailureBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DegradeAllOnFibreFailureBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel13.text")); // NOI18N

        degradFactorField.setColumns(8);
        degradFactorField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "CLT_LastPlyFailureTopComponent.jLabel14.text")); // NOI18N

        epscritField.setColumns(8);
        epscritField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DegradeAllOnFibreFailureBox)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(degradFactorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(epscritField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jaField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(degradFactorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(epscritField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DegradeAllOnFibreFailureBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout inputResultPanelLayout = new javax.swing.GroupLayout(inputResultPanel);
        inputResultPanel.setLayout(inputResultPanelLayout);
        inputResultPanelLayout.setHorizontalGroup(
            inputResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(calculationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(inputResultPanelLayout.createSequentialGroup()
                        .addComponent(previousButton)
                        .addGap(84, 84, 84)
                        .addComponent(nextButton))
                    .addComponent(ResultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartHolderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addContainerGap())
        );
        inputResultPanelLayout.setVerticalGroup(
            inputResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chartHolderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(inputResultPanelLayout.createSequentialGroup()
                        .addGroup(inputResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(inputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ResultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(inputResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(calculationButton)
                            .addComponent(previousButton)
                            .addComponent(nextButton))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(inputResultPanel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void calculationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculationButtonActionPerformed
        data.getLastPlyFailureInput().removePropertyChangeListener(this);

        lpfResult = CLT_Calculator.determineValuesLastPlyFailure(
                clt_lam,
                data.getLastPlyFailureInput().getLoad(),
                data.getLastPlyFailureInput().getStrain(),
                data.getLastPlyFailureInput().isUseStrains(),
                data.getLastPlyFailureInput().getDegradationFactor(),
                data.getLastPlyFailureInput().getEpsilon_crit(),
                data.getLastPlyFailureInput().getJ_a(),
                data.getLastPlyFailureInput().isDegradeAllOnFibreFailure()
        );

        data.getLastPlyFailureInput().addPropertyChangeListener(this);
        actualIterationNumber = 0;
        maxIterationNumber = lpfResult.getLayerResult().length - 1;
        setIterationResults(actualIterationNumber);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                minResDataset.removeAllSeries();
                minResDataset.addSeries(new XYSeries(NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "LastPlyFailureChart.yaxis.caption")));
                int intNum = chartIterOffset;
                for (Double rfMin : lpfResult.getRf_min()) {
                    minResDataset.getSeries(0).add(intNum++, rfMin);
                }
                actRFminDataset.getSeries(0).remove(0);
                actRFminDataset.getSeries(0).add(chartIterOffset, lpfResult.getRf_min()[0]);
            }
        });
    }//GEN-LAST:event_calculationButtonActionPerformed

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        actualIterationNumber = Math.max(0, actualIterationNumber - 1);
        setIterationResults(actualIterationNumber);
    }//GEN-LAST:event_previousButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        actualIterationNumber = Math.min(maxIterationNumber, actualIterationNumber + 1);
        setIterationResults(actualIterationNumber);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void DegradeAllOnFibreFailureBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DegradeAllOnFibreFailureBoxActionPerformed
        data.getLastPlyFailureInput().setDegradeAllOnFibreFailure(DegradeAllOnFibreFailureBox.isSelected());
    }//GEN-LAST:event_DegradeAllOnFibreFailureBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox DegradeAllOnFibreFailureBox;
    private javax.swing.JTextField RFminField;
    private javax.swing.JPanel ResultPanel;
    private javax.swing.JButton calculationButton;
    private javax.swing.JPanel chartHolderPanel;
    private javax.swing.JFormattedTextField degradFactorField;
    private javax.swing.JFormattedTextField epscritField;
    private javax.swing.JTextField failureTypeField;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JPanel inputResultPanel;
    private javax.swing.JTextField iterationField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField jaField;
    private javax.swing.JTextField layerNumberField;
    private javax.swing.JFormattedTextField mxField;
    private javax.swing.JFormattedTextField mxyField;
    private javax.swing.JFormattedTextField myField;
    private javax.swing.JButton nextButton;
    private javax.swing.JFormattedTextField nxField;
    private javax.swing.JFormattedTextField nxyField;
    private javax.swing.JFormattedTextField nyField;
    private javax.swing.JButton previousButton;
    private javax.swing.JTable table;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        data.getLaminat().removePropertyChangeListener(this);
        data.removePropertyChangeListener(this);
        clt_lam.removeCLTRefreshListener(this);
        uniqueLastPlyFailureData.remove(data);
    }

    @Override
    protected void componentDeactivated() {
        super.componentDeactivated();
        JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
        mainFrame.getRootPane().removePropertyChangeListener("defaultButton", defaultButtonListener);
        mainFrame.getRootPane().setDefaultButton(null);
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
        mainFrame.getRootPane().setDefaultButton(calculationButton);
        nxField.requestFocus();
        mainFrame.getRootPane().addPropertyChangeListener("defaultButton", defaultButtonListener);
    }

    private final PropertyChangeListener defaultButtonListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
            mainFrame.getRootPane().removePropertyChangeListener("defaultButton", defaultButtonListener);
            mainFrame.getRootPane().setDefaultButton(calculationButton);
            mainFrame.getRootPane().addPropertyChangeListener("defaultButton", defaultButtonListener);
        }
    };

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
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
    public void resultChanged(LookupEvent ev) {
        if (!result.allInstances().contains(data)) {
            this.close();
        }
    }

    @Override
    public void refreshed() {
    }

    private void setIterationResults(int iter) {
        if (iter >= 0 && iter <= maxIterationNumber) {
            actLayerResults = lpfResult.getLayerResult()[iter];
            tabModel.setLayerResults(actLayerResults);

            nextButton.setEnabled(iter < maxIterationNumber);
            previousButton.setEnabled(iter > 0);

            iterationField.setText(Integer.toString(iter + chartIterOffset));
            layerNumberField.setText(Integer.toString(lpfResult.getLayerNumber()[iter]));
            RFminField.setText(df_RF.format(lpfResult.getRf_min()[iter]));
            failureTypeField.setText(lpfResult.getFailureType()[iter]);

            if (!actRFminDataset.getSeries(0).isEmpty()) {
                actRFminDataset.getSeries(0).remove(0);
            }
            actRFminDataset.getSeries(0).add(iter + chartIterOffset, lpfResult.getRf_min()[iter]);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Laminat && evt.getPropertyName().equals(Laminat.PROP_NAME)
                || evt.getSource() instanceof LastPlyFailureModuleData && evt.getPropertyName().equals(LastPlyFailureModuleData.PROP_NAME)) {
            setName(this.data.getName() + " - " + this.data.getLaminat().getName());
        } else if (evt.getPropertyName().equals("value")) {
            if (evt.getSource() == nxField) {
                data.getLastPlyFailureInput().getLoad().setN_x(((Number) nxField.getValue()).doubleValue());
            } else if (evt.getSource() == nyField) {
                data.getLastPlyFailureInput().getLoad().setN_y(((Number) nyField.getValue()).doubleValue());
            } else if (evt.getSource() == nxyField) {
                data.getLastPlyFailureInput().getLoad().setN_xy(((Number) nxyField.getValue()).doubleValue());
            } else if (evt.getSource() == mxField) {
                data.getLastPlyFailureInput().getLoad().setM_x(((Number) mxField.getValue()).doubleValue());
            } else if (evt.getSource() == myField) {
                data.getLastPlyFailureInput().getLoad().setM_y(((Number) myField.getValue()).doubleValue());
            } else if (evt.getSource() == mxyField) {
                data.getLastPlyFailureInput().getLoad().setM_xy(((Number) mxyField.getValue()).doubleValue());
            } else if (evt.getSource() == jaField) {
                data.getLastPlyFailureInput().setJ_a(((Number) jaField.getValue()).doubleValue());
            } else if (evt.getSource() == degradFactorField) {
                data.getLastPlyFailureInput().setDegradationFactor(((Number) degradFactorField.getValue()).doubleValue());
            } else if (evt.getSource() == epscritField) {
                data.getLastPlyFailureInput().setEpsilon_crit(((Number) epscritField.getValue()).doubleValue());
            }
        }
    }

    private void initChart() {

        minResDataset = new XYSeriesCollection();
        actRFminDataset = new XYSeriesCollection();
        actRFminDataset.addSeries(new XYSeries(NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "LastPlyFailureChart.yaxis.caption")));

        chart = ChartFactory.createXYLineChart(
                "", // chart title
                NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "LastPlyFailureChart.xaxis.caption"), // x axis label
                NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "LastPlyFailureChart.yaxis.caption"), // y axis label
                minResDataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                true // urls
        );

        chart.getXYPlot().setDataset(1, actRFminDataset);

        chart.getXYPlot().getDomainAxis().setLowerMargin(0.0);
        chart.getXYPlot().getDomainAxis().setUpperMargin(0.0);
        chart.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        Font font = chart.getXYPlot().getDomainAxis().getLabelFont();
        AttributedString captionY = new AttributedString(NbBundle.getMessage(CLT_LastPlyFailureTopComponent.class, "LastPlyFailureChart.yaxis.caption"));
        captionY.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 2);
        captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 2, 11);
        captionY.addAttribute(TextAttribute.SIZE, font.getSize());
        chart.getXYPlot().getRangeAxis().setAttributedLabel(captionY);

        chart.getXYPlot().getRangeAxis().setStandardTickUnits(new eLamXNumberTickUnitSource());

        chart.getXYPlot().getRenderer(0).setSeriesPaint(0, Color.BLACK);

        final XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(false, true);
        chart.getXYPlot().setRenderer(1, renderer2);
        chart.getXYPlot().getRenderer(1).setSeriesPaint(0, Color.RED);

        chartPanel = new eLamXChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(10, 10));
        chartPanel.setMinimumSize(new Dimension(10, 10));

        chartHolderPanel.add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * Default Renderers
     *
     */
    class NumberRenderer extends CLT_LastPlyFailureTopComponent.ObjectRenderer {

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
        boolean isZFB_Failed = false;
        boolean isFB_Failed = false;
        int index = 0;
        Color bgColor[] = new Color[]{new Color(255, 255, 255, 255), new Color(240, 240, 240, 255)};
        Color ZFB_failedColor[] = new Color[]{new Color(255, 160, 0, 255), new Color(240, 145, 0, 255)};
        Color FB_failedColor[] = new Color[]{new Color(255, 100, 100, 255), new Color(240, 85, 85, 255)};
        Color ZFB_failedSelColor[] = new Color[2];
        Color FB_failedSelColor[] = new Color[2];
        Color selectionColor[] = new Color[2];

        {
            // we'll use a translucent version of the table's default
            // selection color to paint selections
            Color oldCol = table.getSelectionBackground();
            selectionColor[0] = new Color(oldCol.getRed(), oldCol.getGreen(), oldCol.getBlue(), 255);
            ZFB_failedSelColor[0] = new Color(Math.min(oldCol.getRed() + ZFB_failedColor[0].getRed(), 255),
                    Math.min(oldCol.getGreen() + ZFB_failedColor[0].getGreen(), 100),
                    Math.min(oldCol.getBlue() + ZFB_failedColor[0].getBlue(), 100),
                    255);
            FB_failedSelColor[0] = new Color(Math.min(oldCol.getRed() + FB_failedColor[0].getRed(), 255),
                    Math.min(oldCol.getGreen() + FB_failedColor[0].getGreen(), 100),
                    Math.min(oldCol.getBlue() + FB_failedColor[0].getBlue(), 100),
                    255);
            selectionColor[1] = new Color(oldCol.getRed() < 15 ? oldCol.getRed() + 15 : oldCol.getRed() - 15,
                    oldCol.getGreen() < 15 ? oldCol.getGreen() + 15 : oldCol.getGreen() - 15,
                    oldCol.getBlue() < 15 ? oldCol.getBlue() + 15 : oldCol.getBlue() - 15,
                    255);
            ZFB_failedSelColor[1] = new Color(Math.min(oldCol.getRed() + ZFB_failedColor[1].getRed(), 255),
                    Math.min(oldCol.getGreen() + ZFB_failedColor[1].getGreen(), 85),
                    Math.min(oldCol.getBlue() + ZFB_failedColor[1].getBlue(), 85),
                    255);
            FB_failedSelColor[1] = new Color(Math.min(oldCol.getRed() + FB_failedColor[1].getRed(), 255),
                    Math.min(oldCol.getGreen() + FB_failedColor[1].getGreen(), 85),
                    Math.min(oldCol.getBlue() + FB_failedColor[1].getBlue(), 85),
                    255);

            // need to be non-opaque since we'll be translucent
            setOpaque(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // save the selected state since we'll need it when painting
            this.isSelected = isSelected;
            this.isZFB_Failed = lpfResult == null ? false : lpfResult.getZfw_fail()[actualIterationNumber][row / 2];
            this.isFB_Failed = lpfResult == null ? false : lpfResult.getFb_fail()[actualIterationNumber][row / 2];
            this.index = row / 2 % 2;
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        // since DefaultTableCellRenderer is really just a JLabel, we can override
        // paintComponent to paint the translucent selection when necessary
        @Override
        public void paintComponent(Graphics g) {
            if (isSelected) {
                if (isFB_Failed) {
                    g.setColor(FB_failedSelColor[index]);
                } else if (isZFB_Failed) {
                    g.setColor(ZFB_failedSelColor[index]);
                } else {
                    g.setColor(selectionColor[index]);
                }
            } else {
                if (isFB_Failed) {
                    g.setColor(FB_failedColor[index]);
                } else if (isZFB_Failed) {
                    g.setColor(ZFB_failedColor[index]);
                } else {
                    g.setColor(bgColor[index]);
                }
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    };

    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Point point = e.getPoint();
            int currentRow = table.rowAtPoint(point);
            table.setRowSelectionInterval(currentRow, currentRow);
            int selectedRow = table.getSelectedRow();
            LayerResultContainer res = ((ResultTableModel) table.getModel()).getLayerResultContainerForRow(selectedRow);
            ic.set(Collections.singleton(res), null);
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
