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
package de.elamx.utilities;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Andreas
 */
public class AutoRowHeightTable extends JTable {

    public AutoRowHeightTable() {
        super();
    }

    public AutoRowHeightTable(TableModel dm) {
        super(dm);
    }

    public AutoRowHeightTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public AutoRowHeightTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public AutoRowHeightTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    private boolean needCalcRowHeight = true;

    /**
     * Calculate the height of rows based on the current font. This is done when
     * the first paint occurs, to ensure that a valid Graphics object is
     * available.
     */
    private void calcRowHeight(Graphics g) {
        int rowHeight;

        //Derive a row height to accomodate the font and expando icon
        Font f = getFont();
        FontMetrics fm = g.getFontMetrics(f);
        rowHeight = fm.getHeight() + 3;

        //Clear the flag
        needCalcRowHeight = false;

                //Set row height.  If displayable, this will generate a new call
        //to paint()
        setRowHeight(rowHeight);
    }

    /**
     * Paint the table. After the super.paint() call, calls paintMargin() to
     * fill in the left edge with the appropriate color, and then calls
     * paintExpandableSets() to paint the property sets, which are not painted
     * by the default painting methods because they need to be painted across
     * two rows.
     */
    @Override
    public void paint(Graphics g) {
        if (needCalcRowHeight) {
            calcRowHeight(g);

            return;
        }

        super.paint(g);
    }

}
