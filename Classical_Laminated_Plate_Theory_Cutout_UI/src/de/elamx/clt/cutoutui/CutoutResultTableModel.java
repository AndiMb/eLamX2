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

import javax.swing.table.DefaultTableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author raedel
 */
public class CutoutResultTableModel extends DefaultTableModel{
    
    public static final String CLASS_PREFIX = "CutoutResultTableModel.";
    
    private String[] columnNames = null;
    
    public CutoutResultTableModel() {
        
        String id = CutoutResultTableModel.CLASS_PREFIX + "Table.caption.value";
        String value = NbBundle.getMessage(this.getClass(), id);
        id = CutoutResultTableModel.CLASS_PREFIX + "Table.caption.min";
        String min = NbBundle.getMessage(this.getClass(), id);
        id = CutoutResultTableModel.CLASS_PREFIX + "Table.caption.max";
        String max = NbBundle.getMessage(this.getClass(), id);
        
        columnNames = new String[] {value,min,max};
    }
    
    /**
     * Returns the number of columns
     *
     * @returns int
     */
    @Override
    public int getColumnCount () {
        int col = 0;
        if (columnNames != null){
            col = columnNames.length;
        }
        return col;
    }
    
    /**
     * Returns the column name
     *
     * @params col the column to query
     */
    @Override
    public String getColumnName (int col) {
        return columnNames[col];
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
    
}
