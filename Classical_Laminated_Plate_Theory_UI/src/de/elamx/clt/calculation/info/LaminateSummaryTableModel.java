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
package de.elamx.clt.calculation.info;

import de.elamx.core.GlobalProperties;
import de.elamx.laminate.LaminateSummary;
import java.text.DecimalFormat;
import javax.swing.table.DefaultTableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author raedel
 */
public class LaminateSummaryTableModel extends DefaultTableModel{
    
    private static final String CLASS_PREFIX = "LaminateSummaryTableModel.";
    
    DecimalFormat dfAngle_     = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE);
    DecimalFormat dfThickness_ = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);
    DecimalFormat dfPercent_   = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_PERCENT);
    
    private LaminateSummary lamsum_ = null;
    private String[] columnNames_ = null;
    
    public LaminateSummaryTableModel() {
        
        String id = LaminateSummaryTableModel.CLASS_PREFIX + "Table.caption.angle";
        String angle = NbBundle.getMessage(this.getClass(), id);
        id = LaminateSummaryTableModel.CLASS_PREFIX + "Table.caption.sumti";
        String sumti = NbBundle.getMessage(this.getClass(), id);
        id = LaminateSummaryTableModel.CLASS_PREFIX + "Table.caption.percti";
        String percti = NbBundle.getMessage(this.getClass(), id);
        
        columnNames_ = new String[] { angle,sumti,percti};
    }
    
    /**
     * Returns the number of columns
     *
     * @returns int
     */
    @Override
    public int getColumnCount () {
        int col = 0;
        if (columnNames_ != null){
            col = columnNames_.length;
        }
        return col;
    }
    
    /**
     * Returns the number of rows
     *
     * @returns int
     */
    @Override
    public int getRowCount () {
        int row = 0;
        if (lamsum_ != null){
            row = lamsum_.getNumberOfRows();
        }
        return row;
    }
    
    /**
     * Returns the column name
     *
     * @params col the column to query
     */
    @Override
    public String getColumnName (int col) {
        return columnNames_[col];
    }
    
    /**
     * Returns the value at a desired position
     *
     * @params row the row
     * @params col the column
     * @returns Object
     */
    @Override
    public Object getValueAt (int row, int col) {
        switch (col ) {
            case 0:  return dfAngle_.format(lamsum_.get(row, col));
            case 1:  return dfThickness_.format(lamsum_.get(row, col));
            case 2:  return dfPercent_.format(lamsum_.get(row, col));
            default: return 0.0;
        }
    }
    
    /*
     * Returns the class of the Column
     *
     * @params col the column to query
     * @returns Class
     */
    @Override
    public Class<?> getColumnClass (int col) {
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
    
    public void setValues(LaminateSummary lamsum){
        lamsum_ = lamsum;
        fireTableDataChanged();
    }
    
}
