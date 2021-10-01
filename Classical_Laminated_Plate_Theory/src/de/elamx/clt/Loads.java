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
package de.elamx.clt;

/**
 *
 * @author Andreas Hauffe
 */
public class Loads {

    private double n_x;
    private double n_y;
    private double n_xy;
    private double m_x;
    private double m_y;
    private double m_xy;
    private double deltaT;
    private double deltaH;
    
    private double nT_x;
    private double nT_y;
    private double nT_xy;
    private double mT_x;
    private double mT_y;
    private double mT_xy;

    /**
     * Get the value of deltaH
     *
     * @return the value of deltaH
     */
    public double getDeltaH() {
        return deltaH;
    }

    /**
     * Set the value of deltaH
     *
     * @param deltaH new value of deltaH
     */
    public void setDeltaH(double deltaH) {
        this.deltaH = deltaH;
    }


    /**
     * Get the value of deltaT
     *
     * @return the value of deltaT
     */
    public double getDeltaT() {
        return deltaT;
    }

    /**
     * Set the value of deltaT
     *
     * @param deltaT new value of deltaT
     */
    public void setDeltaT(double deltaT) {
        this.deltaT = deltaT;
    }


    /**
     * Get the value of m_xy
     *
     * @return the value of m_xy
     */
    public double getM_xy() {
        return m_xy;
    }

    /**
     * Set the value of m_xy
     *
     * @param m_xy new value of m_xy
     */
    public void setM_xy(double m_xy) {
        this.m_xy = m_xy;
    }


    /**
     * Get the value of m_y
     *
     * @return the value of m_y
     */
    public double getM_y() {
        return m_y;
    }

    /**
     * Set the value of m_y
     *
     * @param m_y new value of m_y
     */
    public void setM_y(double m_y) {
        this.m_y = m_y;
    }


    /**
     * Get the value of m_x
     *
     * @return the value of m_x
     */
    public double getM_x() {
        return m_x;
    }

    /**
     * Set the value of m_x
     *
     * @param m_x new value of m_x
     */
    public void setM_x(double m_x) {
        this.m_x = m_x;
    }


    /**
     * Get the value of n_xy
     *
     * @return the value of n_xy
     */
    public double getN_xy() {
        return n_xy;
    }

    /**
     * Set the value of n_xy
     *
     * @param n_xy new value of n_xy
     */
    public void setN_xy(double n_xy) {
        this.n_xy = n_xy;
    }


    /**
     * Get the value of n_y
     *
     * @return the value of n_y
     */
    public double getN_y() {
        return n_y;
    }

    /**
     * Set the value of n_y
     *
     * @param n_y new value of n_y
     */
    public void setN_y(double n_y) {
        this.n_y = n_y;
    }


    /**
     * Get the value of n_x
     *
     * @return the value of n_x
     */
    public double getN_x() {
        return n_x;
    }

    /**
     * Set the value of n_x
     *
     * @param n_x new value of n_x
     */
    public void setN_x(double n_x) {
        this.n_x = n_x;
    }
    
    public double[] getForceMomentAsVector(){
        return new double[]{n_x, n_y, n_xy, m_x, m_y, m_xy};
    }

    public double getnT_x() {
        return nT_x;
    }

    public double getnT_y() {
        return nT_y;
    }

    public double getnT_xy() {
        return nT_xy;
    }

    public double getmT_x() {
        return mT_x;
    }

    public double getmT_y() {
        return mT_y;
    }

    public double getmT_xy() {
        return mT_xy;
    }
    
    public void setHygrothermalForcesAsVector(double[] forces){
        nT_x  = forces[0];
        nT_y  = forces[1];
        nT_xy = forces[2];
        mT_x  = forces[3];
        mT_y  = forces[4];
        mT_xy = forces[5];
    }
}
