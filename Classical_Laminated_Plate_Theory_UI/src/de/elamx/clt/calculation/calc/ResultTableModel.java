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

import de.elamx.clt.CLT_Layer;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.calculation.LayerResultContainer;
import de.elamx.core.GlobalProperties;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class ResultTableModel extends DefaultTableModel {

    DecimalFormat dfStresses = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRESS);
    DecimalFormat dfStrains = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRAIN);
    DecimalFormat dfAngle = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE);
    DecimalFormat dfThickness = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);
    DecimalFormat dfRF = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_RESERVE_FACTOR);
    private static final String CLASS_PREFIX = "ResultTableModel.";
    private CLT_LayerResult[] layerResults;
    private String[] columnNames = null;
    private boolean showStresses = true;
    private String[] stressColumnNames = null;
    private String[] strainColumnNames = null;
    
    private HashMap<CLT_Layer, LayerResultContainer> containerMap = new HashMap<>();

    /**
     * Creates a new instance of TableModel_LT
     */
    public ResultTableModel() {
        String id = ResultTableModel.CLASS_PREFIX + "Table.caption.number";
        String number = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.name";
        String name = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.angle";
        String angle = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.zm";
        String zm = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.z";
        String z = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.sigmapar";
        String sigmapar = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.sigmanor";
        String sigmanor = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.tau";
        String tau = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.epsilonpar";
        String epsilonpar = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.epsilonnor";
        String epsilonnor = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.gamma";
        String gamma = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.failurecriteria";
        String criterion = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.minrf";
        String minrf = NbBundle.getMessage(ResultTableModel.class, id);
        id = ResultTableModel.CLASS_PREFIX + "Table.caption.minrfname";
        String minrfname = NbBundle.getMessage(ResultTableModel.class, id);
        columnNames = new String[]{number,
            name,
            angle,
            zm,
            z,
            sigmapar,
            sigmanor,
            tau,
            criterion,
            minrf,
            minrfname};
        stressColumnNames = new String[]{
            sigmapar,
            sigmanor,
            tau};
        strainColumnNames = new String[]{
            epsilonpar,
            epsilonnor,
            gamma};
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
        int row = 0;
        if (layerResults != null) {
            row = 2 * layerResults.length;
        }
        return row;
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
        int layInd = row / 2;
        int off = row % 2;
        if (off == 0) {
            switch (col) {
                case 0:
                    return Integer.toString(layInd + 1);
                case 1:
                    return layerResults[layInd].getLayer().getName();
                case 2:
                    return dfAngle.format(layerResults[layInd].getLayer().getAngle());
                case 3:
                    return dfThickness.format(layerResults[layInd].getClt_layer().getZm());
                case 4:
                    return dfThickness.format(layerResults[layInd].getClt_layer().getZm()+layerResults[layInd].getLayer().getThickness()/2.0);
                case 5:
                    return showStresses ? dfStresses.format(layerResults[layInd].getSss_upper().getStress()[0]) : dfStrains.format(layerResults[layInd].getSss_upper().getStrain()[0]);
                case 6:
                    return showStresses ? dfStresses.format(layerResults[layInd].getSss_upper().getStress()[1]) : dfStrains.format(layerResults[layInd].getSss_upper().getStrain()[1]);
                case 7:
                    return showStresses ? dfStresses.format(layerResults[layInd].getSss_upper().getStress()[2]) : dfStrains.format(layerResults[layInd].getSss_upper().getStrain()[2]);
                case 8:
                    return layerResults[layInd].getLayer().getCriterion().getDisplayName();
                case 9:
                    return dfRF.format(layerResults[layInd].getRr_upper().getMinimalReserveFactor());
                case 10:
                    return layerResults[layInd].getRr_upper().getFailureName();
                default:
                    return 0.0;
            }
        } else {
            switch (col) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
                case 3:
                    return "";
                case 4:
                    return dfThickness.format(layerResults[layInd].getClt_layer().getZm()-layerResults[layInd].getLayer().getThickness()/2.0);
                case 5:
                    return showStresses ? dfStresses.format(layerResults[layInd].getSss_lower().getStress()[0]) : dfStrains.format(layerResults[layInd].getSss_lower().getStrain()[0]);
                case 6:
                    return showStresses ? dfStresses.format(layerResults[layInd].getSss_lower().getStress()[1]) : dfStrains.format(layerResults[layInd].getSss_lower().getStrain()[1]);
                case 7:
                    return showStresses ? dfStresses.format(layerResults[layInd].getSss_lower().getStress()[2]) : dfStrains.format(layerResults[layInd].getSss_lower().getStrain()[2]);
                case 8:
                    return layerResults[layInd].getLayer().getCriterion().getDisplayName();
                case 9:
                    return dfRF.format(layerResults[layInd].getRr_lower().getMinimalReserveFactor());
                case 10:
                    return layerResults[layInd].getRr_lower().getFailureName();
                default:
                    return 0.0;
            }
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

    /*
     * Returns true if the cell is editable, else false
     *
     * @params rowindex the desired row
     * @params columnindex the desired column
     */
    @Override
    public boolean isCellEditable(int rowindex, int columnindex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
    }

    public boolean isShowStresses() {
        return showStresses;
    }

    public void setShowStresses(boolean showStresses) {
        this.showStresses = showStresses;
        if (this.showStresses) {
            System.arraycopy(stressColumnNames, 0, columnNames, 5, stressColumnNames.length);
        } else {
            System.arraycopy(strainColumnNames, 0, columnNames, 5, strainColumnNames.length);
        }
        //this.fireTableDataChanged();
        this.fireTableStructureChanged();
    }

    public void setLayerResults(CLT_LayerResult[] results) {
        this.layerResults = results;
        HashMap<CLT_Layer, LayerResultContainer> newMap = new HashMap<>();
        for (CLT_LayerResult cLT_LayerResult : results) {
            LayerResultContainer l = containerMap.remove(cLT_LayerResult.getClt_layer());
            if (l == null){
                newMap.put(cLT_LayerResult.getClt_layer(), new LayerResultContainer(cLT_LayerResult));
            }else{
                l.setLayerResult(cLT_LayerResult);
                newMap.put(cLT_LayerResult.getClt_layer(), l);
            }
        }
        for (LayerResultContainer resCont : containerMap.values()) {
            resCont.setLayerResult(null);
        }
        containerMap = newMap;
        this.fireTableDataChanged();
    }
    
    /*public CLT_LayerResult getLayerResultAtRow(int index){
        return layerResults[index/2];
    }*/
    
    public LayerResultContainer getLayerResultContainerForRow(int index){
        CLT_Layer l = layerResults[index/2].getClt_layer();
        return containerMap.get(l);
    }
    
    public void clear(){
        for (LayerResultContainer resCont : containerMap.values()) {
            resCont.setLayerResult(null);
        }
    }
}
