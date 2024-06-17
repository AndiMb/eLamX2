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
package de.elamx.reducedinput.dataobjects;

/**
 *
 * @author Florian Dexl
 */
public class BucklingData extends ReducedInputDataObject {

    private String loadcase = null;

    private Double length;
    private Double width;
    private Integer bcx;
    private Integer bcy;
    private Integer n;

    public BucklingData(String name) {
        super(name);
    }

    public String getLoadcase() {
        return loadcase;
    }

    public void setLoadcase(String loadcase) {
        this.loadcase = loadcase;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Integer getBcx() {
        return bcx;
    }

    public void setBcx(Integer bcx) {
        this.bcx = bcx;
    }

    public Integer getBcy() {
        return bcy;
    }

    public void setBcy(Integer bcy) {
        this.bcy = bcy;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }
}
