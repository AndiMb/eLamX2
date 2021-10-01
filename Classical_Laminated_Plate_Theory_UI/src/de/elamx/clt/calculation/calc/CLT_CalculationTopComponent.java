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
package de.elamx.clt.calculation.calc;

import de.elamx.clt.CLTRefreshListener;
import de.elamx.clt.CLT_Calculator;
import de.elamx.clt.CLT_Input;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.calculation.CalculationModuleData;
import de.elamx.clt.calculation.LayerResultContainer;
import de.elamx.clt.calculation.ssdialog.StressStrainDialogPanel;
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
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Action;
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

@TopComponent.Description(
        preferredID = "CLT_CalculationTopComponent",
        iconBase = "de/elamx/clt/calculation/resources/kcalc.png"
)
public final class CLT_CalculationTopComponent extends TopComponent implements LookupListener, CLTRefreshListener, PropertyChangeListener {

    private final InstanceContent ic = new InstanceContent();

    public final static Set<CalculationModuleData> uniqueCalculationData = new HashSet<CalculationModuleData>();
    private final CalculationModuleData data;
    private final CLT_Laminate clt_lam;
    private final Lookup.Result<CalculationModuleData> result;
    private final ResultTableModel tabModel = new ResultTableModel();
    private CLT_LayerResult[] layerResults;
    private final StressStrainDialogPanel ssDiaPanel;

    private final JPopupMenu popupMenu;
    private final AbstractLookup lu = new AbstractLookup(ic);
    
    public CLT_CalculationTopComponent(CalculationModuleData data) {
        this.data = data;
        setName(this.data.getName() + " - " + this.data.getLaminat().getName());
        setToolTipText(NbBundle.getMessage(CLT_CalculationTopComponent.class, "HINT_CLT_CalculationTopComponent"));
        data.getLaminat().addPropertyChangeListener(this);
        CLT_Laminate tClt_lam = data.getLaminat().getLookup().lookup(CLT_Laminate.class);
        if (tClt_lam == null) {
            clt_lam = new CLT_Laminate(data.getLaminat());
        } else {
            clt_lam = tClt_lam;
        }
        clt_lam.addCLTRefreshListener(this);
        ssDiaPanel = new StressStrainDialogPanel(clt_lam);
        initComponents();
        table.setMinimumSize(new Dimension(300, 0));
        for (MouseWheelListener mwl : jScrollPane2.getMouseWheelListeners()) {
            jScrollPane2.removeMouseWheelListener(mwl);
        }
        stressstraingroup.add(strainRadioButton);
        stressstraingroup.add(stressRadioButton);
        stressRadioButton.setSelected(tabModel.isShowStresses());
        result = data.getLaminat().getLookup().lookupResult(CalculationModuleData.class);
        result.addLookupListener(this);
        data.getDataHolder().addPropertyChangeListener(this);
        data.addPropertyChangeListener(this);
        associateLookup(Lookups.fixed(data, data.getLaminat(), new RawDataExportImpl()));
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new CalculationPanel(data.getDataHolder(), ssDiaPanel);
        jPanel3 = new javax.swing.JPanel();
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

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(null);
        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);
        ((CalculationPanel)jPanel1).init();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CLT_CalculationTopComponent.class, "CLT_CalculationTopComponent.jPanel3.border.title"))); // NOI18N
        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setWheelScrollingEnabled(false);

        table.setModel(tabModel);
        jScrollPane2.setViewportView(table);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        stressRadioButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(stressRadioButton, org.openide.util.NbBundle.getMessage(CLT_CalculationTopComponent.class, "CLT_CalculationTopComponent.stressRadioButton.text")); // NOI18N
        stressRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stressRadioButtonActionPerformed(evt);
            }
        });
        jPanel4.add(stressRadioButton);

        org.openide.awt.Mnemonics.setLocalizedText(strainRadioButton, org.openide.util.NbBundle.getMessage(CLT_CalculationTopComponent.class, "CLT_CalculationTopComponent.strainRadioButton.text")); // NOI18N
        strainRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strainRadioButtonActionPerformed(evt);
            }
        });
        jPanel4.add(strainRadioButton);

        jPanel3.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(jPanel2);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void stressRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stressRadioButtonActionPerformed
        tabModel.setShowStresses(stressRadioButton.isSelected());
    }//GEN-LAST:event_stressRadioButtonActionPerformed

    private void strainRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strainRadioButtonActionPerformed
        tabModel.setShowStresses(stressRadioButton.isSelected());
    }//GEN-LAST:event_strainRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
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
        ((CalculationPanel)jPanel1).close();
        data.getDataHolder().removePropertyChangeListener(this);
        data.getLaminat().removePropertyChangeListener(this);
        data.removePropertyChangeListener(this);
        clt_lam.removeCLTRefreshListener(this);
        tabModel.clear();
        uniqueCalculationData.remove(data);
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
    public void resultChanged(LookupEvent ev) {
        if (!result.allInstances().contains(data)) {
            this.close();
        }
    }

    private void recalc() {
        data.getDataHolder().removePropertyChangeListener(this);
        CLT_Calculator.determineValues(clt_lam, data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), data.getDataHolder().isUseStrains());
        layerResults = CLT_Calculator.getLayerResults(data.getLaminat().getLookup().lookup(CLT_Laminate.class), data.getDataHolder().getLoad(), data.getDataHolder().getStrains());
        data.getDataHolder().addPropertyChangeListener(this);
        tabModel.setLayerResults(layerResults);
        ssDiaPanel.setResults(layerResults);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Laminat && evt.getPropertyName().equals(Laminat.PROP_NAME)
                || evt.getSource() instanceof CalculationModuleData && evt.getPropertyName().equals(CalculationModuleData.PROP_NAME)) {
            setName(this.data.getName() + " - " + this.data.getLaminat().getName());
        } else if (evt.getSource() instanceof CLT_Input) {
            recalc();
        }
    }

    @Override
    public void refreshed() {
        ((CalculationPanel) jPanel1).setABDMatrix(clt_lam.getAMatrix(), clt_lam.getBMatrix(), clt_lam.getDMatrix());
        recalc();
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

    private class RawDataExportImpl implements RawDataExportService {

        @Override
        public void export(FileWriter fw) {
            ResultWriter.writeResults(fw, data.getLaminat(), data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), layerResults);
        }

        @Override
        public String getFileExtension() {
            return "txt";
        }

    }

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
