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
package de.elamx.clt.pressurevesselui;

import de.elamx.clt.CLTRefreshListener;
import de.elamx.clt.CLT_Calculator;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.Loads;
import de.elamx.clt.calculation.CalculationModuleData;
import de.elamx.clt.calculation.LayerResultContainer;
import de.elamx.clt.calculation.calc.ResultTableModel;
import de.elamx.clt.pressurevessel.PressureVesselInput;
import de.elamx.core.GlobalProperties;
import de.elamx.core.RawDataExportService;
import de.elamx.laminate.Laminat;
import de.elamx.utilities.AutoRowHeightTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "CLT_PressureVesselTopComponent",
        iconBase="de/elamx/clt/pressurevesselui/resources/pressurevessel.png"
)
public final class CLT_PressureVesselTopComponent extends TopComponent implements LookupListener, CLTRefreshListener, PropertyChangeListener {
    
    private final InstanceContent ic = new InstanceContent();
    
    private final PressureVesselModuleData data;
    private final CLT_Laminate clt_lam;
    private final Lookup.Result<PressureVesselModuleData> result;
    public final static Set<PressureVesselModuleData> uniquePressereVesselData = new HashSet<>();
    private CLT_LayerResult[] layerResults;
    private final ResultTableModel tabModel = new ResultTableModel();
    
    DecimalFormat df_Forces = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);
    DecimalFormat df_Strains = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRAIN);

    private final JPopupMenu popupMenu;
    private final AbstractLookup lu = new AbstractLookup(ic);

    public CLT_PressureVesselTopComponent(PressureVesselModuleData data) {
        this.data = data;
        setName(this.data.getName() + " - " + this.data.getLaminat().getName());
        setToolTipText(NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "HINT_CLT_PressureVesselTopComponent"));
        data.getLaminat().addPropertyChangeListener(this);
        CLT_Laminate tClt_lam = data.getLaminat().getLookup().lookup(CLT_Laminate.class);
        if (tClt_lam == null) {
            clt_lam = new CLT_Laminate(data.getLaminat());
        } else {
            clt_lam = tClt_lam;
        }
        clt_lam.addCLTRefreshListener(this);
        initComponents();
        associateLookup(Lookups.fixed(data, data.getLaminat(), new RawDataExportImpl()));
        table.setMinimumSize(new Dimension(300, 0));
        for (MouseWheelListener mwl : jScrollPane2.getMouseWheelListeners()) {
            jScrollPane2.removeMouseWheelListener(mwl);
        }
        stressRadioButton.setSelected(tabModel.isShowStresses());
        
        result = data.getLaminat().getLookup().lookupResult(PressureVesselModuleData.class);
        result.addLookupListener(this);
        data.getPressureVesselInput().addPropertyChangeListener(this);
        data.addPropertyChangeListener(this);
        refreshed();
        recalc();

        popupMenu = Utilities.actionsToPopup(Utilities.actionsForPath("eLamXActions/LayerResultContainer").toArray(new Action[0]), lu);

        MouseListener popupListener = new PopupListener();
        // Add the listener to the JTable:
        table.addMouseListener(popupListener);
        // Add the listener specifically to the header:
        table.getTableHeader().addMouseListener(popupListener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        stressstraingroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        epsaxField = new javax.swing.JFormattedTextField(df_Strains);
        epsradField = new javax.swing.JFormattedTextField(df_Strains);
        jPanel3 = new javax.swing.JPanel();
        radiusLabel = new javax.swing.JLabel();
        radiusField = new javax.swing.JFormattedTextField(df_Forces);
        radiusField.setValue(data.getPressureVesselInput().getRadius());
        radiusField.addPropertyChangeListener("value", this);
        pressureLabel = new javax.swing.JLabel();
        pressureField = new javax.swing.JFormattedTextField(df_Forces);
        pressureField.setValue(data.getPressureVesselInput().getPressure());
        pressureField.addPropertyChangeListener("value", this);
        radiusTypeLabel = new javax.swing.JLabel();
        radiusTypeBox = new javax.swing.JComboBox<>();
        radiusTypeBox.addItem(NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.radiusTypeBox.item0"));
        radiusTypeBox.addItem(NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.radiusTypeBox.item1"));
        radiusTypeBox.addItem(NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.radiusTypeBox.item2"));

        switch (data.getPressureVesselInput().getRadiusType()){
            case PressureVesselInput.RADIUSTYPE_INNER:
            radiusTypeBox.setSelectedIndex(0);
            break;
            case PressureVesselInput.RADIUSTYPE_MEAN:
            radiusTypeBox.setSelectedIndex(1);
            break;
            case PressureVesselInput.RADIUSTYPE_OUTER:
            radiusTypeBox.setSelectedIndex(2);
            break;
            default:
            radiusTypeBox.setSelectedIndex(1);
        }
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
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
        jPanel4 = new javax.swing.JPanel();
        stressRadioButton = new javax.swing.JRadioButton();
        strainRadioButton = new javax.swing.JRadioButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.jLabel1.text")); // NOI18N

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.jPanel6.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.jLabel3.text")); // NOI18N

        epsaxField.setColumns(8);
        epsaxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        epsaxField.setText(org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.jFormattedTextField2.text")); // NOI18N

        epsradField.setColumns(8);
        epsradField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        epsradField.setText(org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.epsradField.text")); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(epsaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(epsradField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(243, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(epsaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(epsradField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.jPanel3.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(radiusLabel, org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.radiusLabel.text")); // NOI18N

        radiusField.setColumns(8);
        radiusField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(pressureLabel, org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.pressureLabel.text")); // NOI18N

        pressureField.setColumns(8);
        pressureField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.openide.awt.Mnemonics.setLocalizedText(radiusTypeLabel, org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.radiusTypeLabel.text")); // NOI18N

        radiusTypeBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radiusTypeBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(pressureLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pressureField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radiusLabel)
                            .addComponent(radiusTypeLabel))
                        .addGap(44, 44, 44)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(radiusTypeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(radiusField))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radiusLabel)
                    .addComponent(radiusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radiusTypeLabel)
                    .addComponent(radiusTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pressureLabel)
                    .addComponent(pressureField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.jPanel2.border.title"))); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        table.setModel(tabModel);
        jScrollPane2.setViewportView(table);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        stressstraingroup.add(stressRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(stressRadioButton, org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.stressRadioButton.text")); // NOI18N
        stressRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stressRadioButtonActionPerformed(evt);
            }
        });
        jPanel4.add(stressRadioButton);

        stressstraingroup.add(strainRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(strainRadioButton, org.openide.util.NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.strainRadioButton.text")); // NOI18N
        strainRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strainRadioButtonActionPerformed(evt);
            }
        });
        jPanel4.add(strainRadioButton);

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void stressRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stressRadioButtonActionPerformed
        tabModel.setShowStresses(stressRadioButton.isSelected());
    }//GEN-LAST:event_stressRadioButtonActionPerformed

    private void strainRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strainRadioButtonActionPerformed
        tabModel.setShowStresses(stressRadioButton.isSelected());
    }//GEN-LAST:event_strainRadioButtonActionPerformed

    private void radiusTypeBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radiusTypeBoxItemStateChanged
        switch (radiusTypeBox.getSelectedIndex()) {
            case 0:
                data.getPressureVesselInput().setRadiusType(PressureVesselInput.RADIUSTYPE_INNER);
                break;
            case 1:
                data.getPressureVesselInput().setRadiusType(PressureVesselInput.RADIUSTYPE_MEAN);
                break;
            case 2:
                data.getPressureVesselInput().setRadiusType(PressureVesselInput.RADIUSTYPE_OUTER);
                break;
            default:
                break;
        }
    }//GEN-LAST:event_radiusTypeBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField epsaxField;
    private javax.swing.JFormattedTextField epsradField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JFormattedTextField pressureField;
    private javax.swing.JLabel pressureLabel;
    private javax.swing.JFormattedTextField radiusField;
    private javax.swing.JLabel radiusLabel;
    private javax.swing.JComboBox<String> radiusTypeBox;
    private javax.swing.JLabel radiusTypeLabel;
    private javax.swing.JRadioButton strainRadioButton;
    private javax.swing.JRadioButton stressRadioButton;
    private javax.swing.ButtonGroup stressstraingroup;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        data.getPressureVesselInput().removePropertyChangeListener(this);
        data.getLaminat().removePropertyChangeListener(this);
        data.removePropertyChangeListener(this);
        clt_lam.removeCLTRefreshListener(this);
        uniquePressereVesselData.remove(data);
    }

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
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof JFormattedTextField && evt.getPropertyName().equals("value")) {
            data.getPressureVesselInput().setPressure(((Number)pressureField.getValue()).doubleValue());
            data.getPressureVesselInput().setRadius(((Number)radiusField.getValue()).doubleValue());
        } else if (evt.getSource() instanceof Laminat && evt.getPropertyName().equals(Laminat.PROP_NAME)
                || evt.getSource() instanceof CalculationModuleData && evt.getPropertyName().equals(CalculationModuleData.PROP_NAME)) {
            setName(this.data.getName() + " - " + this.data.getLaminat().getName());
        } else if (evt.getSource() instanceof PressureVesselInput) {
            recalc();
        }
    }

    private void recalc() {
        data.getPressureVesselInput().removePropertyChangeListener(this);
        
        Loads loads = data.getPressureVesselInput().getLoad(clt_lam.getTges());
        
        CLT_Calculator.determineValues(clt_lam, loads, data.getPressureVesselInput().getStrains(), data.getPressureVesselInput().isUseStrains());
        
        layerResults = CLT_Calculator.getLayerResults_radial(
                clt_lam, 
                loads, 
                data.getPressureVesselInput().getStrains(), 
                data.getPressureVesselInput().getMeanRadius(clt_lam.getTges()));
        data.getPressureVesselInput().addPropertyChangeListener(this);
        tabModel.setLayerResults(layerResults);
        epsaxField.setValue(data.getPressureVesselInput().getStrains().getEpsilon_x());
        epsradField.setValue(data.getPressureVesselInput().getStrains().getEpsilon_y());
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (!result.allInstances().contains(data)) {
            this.close();
        }
    }

    @Override
    public void refreshed() {
        recalc();
    }

    /**
     * Default Renderers
     *
     */
    class NumberRenderer extends CLT_PressureVesselTopComponent.ObjectRenderer {

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
        Color failedColor[] = new Color[]{new Color(255, 170, 170, 255), new Color(240, 155, 155, 255)};
        Color failedSelColor[] = new Color[2];
        Color selectionColor[] = new Color[2];

        {
            // we'll use a translucent version of the table's default
            // selection color to paint selections
            Color oldCol = table.getSelectionBackground();
            selectionColor[0] = new Color(oldCol.getRed(), oldCol.getGreen(), oldCol.getBlue(), 255);
            failedSelColor[0] = new Color(Math.min(oldCol.getRed() + failedColor[0].getRed(), 255),
                                          Math.min(oldCol.getGreen() + failedColor[0].getGreen(), 100),
                                          Math.min(oldCol.getBlue() + failedColor[0].getBlue(), 100),
                                          255);
            selectionColor[1] = new Color(oldCol.getRed() < 15 ? oldCol.getRed()+15 : oldCol.getRed()-15, 
                                          oldCol.getGreen() < 15 ? oldCol.getGreen()+15 : oldCol.getGreen()-15, 
                                          oldCol.getBlue() < 15 ? oldCol.getBlue()+15 : oldCol.getBlue()-15,
                                          255);
            failedSelColor[1] = new Color(Math.min(oldCol.getRed() + failedColor[1].getRed(), 255),
                    Math.min(oldCol.getGreen() + failedColor[1].getGreen(), 85),
                    Math.min(oldCol.getBlue() + failedColor[1].getBlue(), 85),
                    255);

            // need to be non-opaque since we'll be translucent
            setOpaque(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // save the selected state since we'll need it when painting
            this.isSelected = isSelected;
            this.isFailed = layerResults == null ? false : layerResults[row / 2].isFailed();
            this.index = row / 2 % 2;
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        // since DefaultTableCellRenderer is really just a JLabel, we can override
        // paintComponent to paint the translucent selection when necessary
        @Override
        public void paintComponent(Graphics g) {
            if (isSelected) {
                if (isFailed) {
                    g.setColor(failedSelColor[index]);
                } else {
                    g.setColor(selectionColor[index]);
                }
            } else {
                if (isFailed) {
                    g.setColor(failedColor[index]);
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

    private class RawDataExportImpl implements RawDataExportService {

        @Override
        public void export(FileWriter fw) {
            ResultWriter.writeResults(fw, data.getLaminat(), data.getPressureVesselInput(), layerResults);
        }

        @Override
        public String getFileExtension() {
            return "txt";
        }

    }

}
