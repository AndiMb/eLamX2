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
package de.elamx.laminatfailurebody;

import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class LaminateTableModel extends DefaultTableModel{

    private static final String CLASS_PREFIX = "LaminateTableModel.";

    private ArrayList<Layer> layers = new ArrayList<>();
    private ArrayList<Color> layerColor = new ArrayList<>();

    private String[] columnNames = null;

    /** Creates a new instance of TableModel_LT */
    public LaminateTableModel() {
        String id = LaminateTableModel.CLASS_PREFIX + "Table.caption.number";
        String number = NbBundle.getMessage(LaminateTableModel.class, id);
        id = LaminateTableModel.CLASS_PREFIX + "Table.caption.name";
        String name = NbBundle.getMessage(LaminateTableModel.class, id);
        id = LaminateTableModel.CLASS_PREFIX + "Table.caption.angle";
        String angle = NbBundle.getMessage(LaminateTableModel.class, id);
        id = LaminateTableModel.CLASS_PREFIX + "Table.caption.color";
        String color = NbBundle.getMessage(LaminateTableModel.class, id);
        columnNames = new String[] { number,
                                     name,
                                     angle,
                                     color};
    }

    /*
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

    /*
     * Returns the number of rows
     *
     * @returns int
     */
    @Override
    public int getRowCount () {
        int row = 0;
        if (layers != null){
            row = layers.size();
        }
        return row;
    }

    /*
     * Returns the column name
     *
     * @params col the column to query
     */
    @Override
    public String getColumnName (int col) {
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
    public Object getValueAt (int row, int col) {
        switch (col ) {
            case 0:
                return row + 1;
            case 1:
                return layers.get(row).getName();
            case 2:
                return layers.get(row).getAngle();
            case 3:
                return layerColor.get(row);
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
    public boolean isCellEditable (int rowindex, int columnindex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {

    }

    public void setLaminate (Laminat laminate, ArrayList<Color> layerColor) {
        this.layers = laminate.getAllLayers();
        this.layerColor = layerColor;
        this.fireTableDataChanged();
    }
}

