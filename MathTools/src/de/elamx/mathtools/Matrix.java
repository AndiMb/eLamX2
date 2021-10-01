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
package de.elamx.mathtools;

/**
 *
 * @author raedel
 */
public abstract class Matrix {

    protected int r_, c_;

    public Matrix(int rows, int columns) {
        r_ = rows;
        c_ = columns;
    }

    /**
     * Gibt die Zeilenanzahl der Matrix zurück.
     *
     * @return Spalten- bzw. Zeilenanzahl
     */
    public int getRows() {
        return r_;
    }

    /**
     * Gibt die Spaltennanzahl der Matrix zurück.
     *
     * @return Spalten- bzw. Zeilenanzahl
     */
    public int getColumns() {
        return c_;
    }

    /**
     * Gibt ein String-Objekt zurück, welches die Matrix repräsentiert.
     *
     * @return String
     */
    @Override
    public abstract String toString();
}
