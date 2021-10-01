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
package de.elamx.failureview3d;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.hint.TransparencyType;
import de.elamx.fileview.nodes.DefaultMaterialNode;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.failure.Criterion;
import de.elamx.utilities.AutoRowHeightTable;
import de.view3d.View3DProperties;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.elamx.failureview3d//FailureView3DPropertiesTopComponent//EN",
        autostore = false)
@TopComponent.Description(preferredID = "FailureView3DPropertiesTopComponent",
        iconBase = "de/elamx/failureview3d/resources/puck.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = false)
@ActionID(category = "Window", id = "de.elamx.failureview3d.FailureView3DPropertiesTopComponent")
@ActionReference(path = "Menu/Window", position = 333)
@TopComponent.OpenActionRegistration(displayName = "#CTL_FailureView3DPropertiesTopComponentAction",
        preferredID = "FailureView3DPropertiesTopComponent")
public final class FailureView3DPropertiesTopComponent extends TopComponent implements PropertyChangeListener {

    private DefaultMaterial material;
    private DefaultMaterialNode[] matNodes;
    private HashMap<String, Mesh> critMap = new HashMap<>();
    private boolean[] show;
    private Criterion[] criteria;
    private double maxValue = 0.0;
    private boolean redraw = true;

    public FailureView3DPropertiesTopComponent() {
        initComponents();
        prepareData();
        setName(NbBundle.getMessage(FailureView3DPropertiesTopComponent.class, "CTL_FailureView3DPropertiesTopComponent"));
        setToolTipText(NbBundle.getMessage(FailureView3DPropertiesTopComponent.class, "HINT_FailureView3DPropertiesTopComponent"));

        View3DProperties.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(this, View3DProperties.getDefault()));

        material = new DefaultMaterial("1", NbBundle.getMessage(FailureView3DPropertiesTopComponent.class, "CTL_FailureView3DPropertiesTopComponent.Material.name"), 141000.0, 10000.0, 0.28, 4500.0, 1.65, false);
        material.setRParTen(NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).getDouble("FailureView3DPropertiesTopComponent.RParTen", 1500.0));
        material.setRParCom(NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).getDouble("FailureView3DPropertiesTopComponent.RParCom", 1000.0));
        material.setRNorTen(NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).getDouble("FailureView3DPropertiesTopComponent.RNorTen", 180.0));
        material.setRNorCom(NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).getDouble("FailureView3DPropertiesTopComponent.RNorCom", 240.0));
        material.setRShear(NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).getDouble("FailureView3DPropertiesTopComponent.RShear", 150.0));

        material.addPropertyChangeListener(WeakListeners.propertyChange(this, material));

        matNodes = new DefaultMaterialNode[]{new DefaultMaterialNode(material) {
            @Override
            protected Sheet createSheet() {
                Sheet s = super.createSheet();
                s.remove("GeneralProperties");
                s.remove("StiffnessProperties");
                s.remove("HygrothermalProperties");
                return s;
            }
        }
        };
        propertySheet1.setNodes(matNodes);
        setFailurePoints();
    }

    public void setMaterial(LayerMaterial mat) {
        material.setRParTen(mat.getRParTen());
        material.setRParCom(mat.getRParCom());
        material.setRNorTen(mat.getRNorTen());
        material.setRNorCom(mat.getRNorCom());
        material.setRShear(mat.getRShear());

        for (String key : mat.getAdditionalValueKeySet()) {
            material.putAdditionalValue(key, mat.getAdditionalValue(key));
        }
    }

    private void prepareData() {
        Lookup lkp = Lookups.forPath("elamx/failurecriteria");
        
        ArrayList<Criterion> cArrayList = new ArrayList<>(lkp.lookupAll(Criterion.class));
        Collections.sort(cArrayList, new Comparator<Criterion>() {
            @Override
            public int compare(Criterion o1, Criterion o2) {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
            }
        });
        
        criteria = new Criterion[cArrayList.size()];
        int ii = 0;
        for (Criterion crit : cArrayList) {
            criteria[ii++] = crit;
        }
        show = ((FailureView3DTopComponent) WindowManager.getDefault().findTopComponent("FailureView3DTopComponent")).getShow();
        if (show == null || show.length != criteria.length) {
            show = new boolean[criteria.length];
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new AutoRowHeightTable();
        jTable1.setDefaultRenderer(Color.class, new ColorRenderer(true));
        Integer fontSize = (Integer) UIManager.get("customFontSize");
        if (fontSize != null) {
            jTable1.setRowHeight(fontSize);
        }
        propertySheet1 = new org.openide.explorer.propertysheet.PropertySheet();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTable1.setModel(new failureTableModel());
        jScrollPane1.setViewportView(jTable1);

        jSplitPane1.setTopComponent(jScrollPane1);
        jSplitPane1.setRightComponent(propertySheet1);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private org.openide.explorer.propertysheet.PropertySheet propertySheet1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        if (redraw) {
            redraw();
            redraw = false;
        }
    }

    @Override
    public void componentClosed() {
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (FailureView3DPropertiesTopComponent.this.isOpened()) {
                    redraw();
                } else {
                    redraw = true;
                }
            }
        });
        NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).putDouble("FailureView3DPropertiesTopComponent.RParTen", material.getRParTen());
        NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).putDouble("FailureView3DPropertiesTopComponent.RParCom", material.getRParCom());
        NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).putDouble("FailureView3DPropertiesTopComponent.RNorTen", material.getRNorTen());
        NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).putDouble("FailureView3DPropertiesTopComponent.RNorCom", material.getRNorCom());
        NbPreferences.forModule(FailureView3DPropertiesTopComponent.class).putDouble("FailureView3DPropertiesTopComponent.RShear", material.getRShear());
    }

    private void redraw() {
        critMap.clear();
        checkCriterionMap();
        setFailurePoints();
        displayCriteria();
    }

    private void checkCriterionMap() {
        for (int ii = 0; ii < show.length; ii++) {
            if (show[ii] && !critMap.containsKey(criteria[ii].getClass().getName())) {
                Mesh shape = criteria[ii].getAsMesh(material, View3DProperties.getDefault().getNetQuality());
                setCriterionAppearance(shape, criteria[ii].getColor());
                critMap.put(criteria[ii].getClass().getName(), shape);
            }
        }
    }

    private void setFailurePoints() {
        ArrayList<Vector3> points = new ArrayList<>(6);
        maxValue = material.getRParTen();
        maxValue = Math.max(maxValue, material.getRParCom());
        maxValue = Math.max(maxValue, material.getRNorTen());
        maxValue = Math.max(maxValue, material.getRNorCom());
        maxValue = Math.max(maxValue, material.getRShear());
        points.add(new Vector3((float) (material.getRParTen() / maxValue), 0.0f, 0.0f));
        points.add(new Vector3(-(float) (material.getRParCom() / maxValue), 0.0f, 0.0f));
        points.add(new Vector3(0.0f, (float) (material.getRNorTen() / maxValue), 0.0f));
        points.add(new Vector3(0.0f, -(float) (material.getRNorCom() / maxValue), 0.0f));
        points.add(new Vector3(0.0f, 0.0f, (float) (material.getRShear() / maxValue)));
        points.add(new Vector3(0.0f, 0.0f, -(float) (material.getRShear() / maxValue)));
        ((FailureView3DTopComponent) WindowManager.getDefault().findTopComponent("FailureView3DTopComponent")).setPoints(points);
    }

    private void displayCriteria() {
        ArrayList<Mesh> list = new ArrayList<>(critMap.size());
        for (int ii = 0; ii < show.length; ii++) {
            if (show[ii]) {
                list.add(critMap.get(criteria[ii].getClass().getName()));
            }
        }
        ((FailureView3DTopComponent) WindowManager.getDefault().findTopComponent("FailureView3DTopComponent")).setMesh(list, 1.0 / maxValue);
        //((FailureView3DTopComponent) WindowManager.getDefault().findTopComponent("FailureView3DTopComponent")).setShow(show);
    }

    private class failureTableModel extends AbstractTableModel {

        private String[] columnNames;

        public failureTableModel() {

            String nameCap = NbBundle.getMessage(FailureView3DPropertiesTopComponent.class, "CLT_FailureView3DPropertiesTopComponent.table.name.caption");
            String colorCap = NbBundle.getMessage(FailureView3DPropertiesTopComponent.class, "CLT_FailureView3DPropertiesTopComponent.table.color.caption");;
            String showCap = NbBundle.getMessage(FailureView3DPropertiesTopComponent.class, "CLT_FailureView3DPropertiesTopComponent.table.show.caption");;

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
            if (criteria == null) {
                return 0;
            }
            return criteria.length;
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
                    return criteria[row].getDisplayName();
                case 1:
                    return criteria[row].getColor();
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
                checkCriterionMap();
                displayCriteria();
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

    public static void setCriterionAppearance(Mesh mesh, Color color) {
        
        mesh.setSolidColor(new ColorRGBA(color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f, 0.8f));
        // Add a material state
        final MaterialState ms = new MaterialState();
        // Pull diffuse color for front from mesh color
        ms.setColorMaterial(MaterialState.ColorMaterial.Diffuse);
        ms.setColorMaterialFace(MaterialState.MaterialFace.Front);
        // Set shininess for front and back
        ms.setShininess(MaterialState.MaterialFace.Front, 100);
        mesh.setRenderState(ms);

        BlendState blend = new BlendState();
        blend.setBlendEnabled(true);
        mesh.setRenderState(blend);

        mesh.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
        mesh.getSceneHints().setTransparencyType(TransparencyType.TwoPass);
    }
}
