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

import de.elamx.core.GlobalProperties;
import de.elamx.utilities.AutoRowHeightTable;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author raedel
 */
public class CutoutTable extends AutoRowHeightTable{
    
    private Class<?> editingClass;
    
    public CutoutTable(){
        super(null, null, null);
    }
    
    public CutoutTable (final Object[][] rowData, final Object[] columnNames) {
        super(rowData, columnNames);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        editingClass = null;
        int modelColumn = convertColumnIndexToModel(column);
        if (modelColumn == 1 || modelColumn == 2) {
            Class<?> rowClass = getModel().getValueAt(row, modelColumn).getClass();
            return getDefaultRenderer(rowClass);
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        editingClass = null;
        int modelColumn = convertColumnIndexToModel(column);
        if (modelColumn == 1) {
            editingClass = getModel().getValueAt(row, modelColumn).getClass();
            return new NumberCellEditor();
        } else {
            return super.getCellEditor(row, column);
        }
    }
    //  This method is also invoked by the editor when the value in the editor
    //  component is saved in the TableModel. The class was saved when the
    //  editor was invoked so the proper class can be created.

    @Override
    public Class<?> getColumnClass(int column) {
        return editingClass != null ? editingClass : super.getColumnClass(column);
    }
    
    
    class NumberCellEditor extends DefaultCellEditor {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NumberCellEditor(){
        super(new JFormattedTextField());
    }

    @Override
     public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField editor = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

        if (value!=null){
            DecimalFormat numberFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_DISPLACEMENT);;
            editor.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
            Number num = (Number) value;  
            String text = numberFormat.format(num);
            editor.setHorizontalAlignment(SwingConstants.RIGHT);
            editor.setText(text);
        }
        return editor;
    }

    @Override
    public Object getCellEditorValue() {
        // get content of textField
        String str = (String) super.getCellEditorValue();
        if (str == null) {
            return null;
        }

        if (str.length() == 0) {
            return null;
        }

        // try to parse a number
        try {
            ParsePosition pos = new ParsePosition(0);
            Number n = NumberFormat.getInstance().parse(str, pos);
            if (pos.getIndex() != str.length()) {
                throw new ParseException("parsing incomplete", pos.getIndex());
            }

            // return an instance of column class
            return n.floatValue();

        } catch (ParseException pex) {
            throw new RuntimeException(pex);
        }
    }
    }
}
