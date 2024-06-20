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
public class LoadCaseData extends ReducedInputDataObject {

    private Double n_x;
    private Double n_y;
    private Double n_xy;
    private Double m_x;
    private Double m_y;
    private Double m_xy;
    private Double delta_t;
    private Double delta_h;
    private Double ul_factor;

    public LoadCaseData(String name) {
        super(name);
    }

    public Double getN_x() {
        return n_x;
    }

    public void setN_x(Double n_x) {
        this.n_x = n_x;
    }

    public Double getN_y() {
        return n_y;
    }

    public void setN_y(Double n_y) {
        this.n_y = n_y;
    }

    public Double getN_xy() {
        return n_xy;
    }

    public void setN_xy(Double n_xy) {
        this.n_xy = n_xy;
    }

    public Double getM_x() {
        return m_x;
    }

    public void setM_x(Double m_x) {
        this.m_x = m_x;
    }

    public Double getM_y() {
        return m_y;
    }

    public void setM_y(Double m_y) {
        this.m_y = m_y;
    }

    public Double getM_xy() {
        return m_xy;
    }

    public void setM_xy(Double m_xy) {
        this.m_xy = m_xy;
    }

    public Double getDelta_t() {
        return delta_t;
    }

    public void setDelta_t(Double delta_t) {
        this.delta_t = delta_t;
    }

    public Double getDelta_h() {
        return delta_h;
    }

    public void setDelta_h(Double delta_h) {
        this.delta_h = delta_h;
    }

    public Double getUl_factor() {
        return ul_factor;
    }

    public void setUl_factor(Double ul_factor) {
        this.ul_factor = ul_factor;
    }

}
