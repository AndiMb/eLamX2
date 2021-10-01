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
package de.elamx.clt.plate;

import de.elamx.clt.CLT_Laminate;

/**
 *
 * @author Andreas Hauffe
 */
public class BucklingResult extends Result{
    
    private double[]     n_crit;
    private double       smallestPositiveEigenValue;
    private double[]     eigenvalues_;
    private double[][][] eigenvectors_;
    
    public BucklingResult(CLT_Laminate laminate, BucklingInput input){
        super(laminate, input.copy());
    }

    public double[] getN_crit() {
        return n_crit;
    }

    protected void setN_crit(double[] n_crit) {
        this.n_crit = n_crit;
    }

    public double getSmallestPositiveEigenValue() {
        return smallestPositiveEigenValue;
    }

    public void setSmallestPositiveEigenValue(double smallestPositiveEigenValue) {
        this.smallestPositiveEigenValue = smallestPositiveEigenValue;
    }
    
    public void setEigenForms(double[] eigenvalues, double[][][] eigenvectors){
        eigenvalues_  = eigenvalues;
        eigenvectors_ = eigenvectors;
    }

    public double[] getEigenvalues_() {
        return eigenvalues_;
    }

    public double[][][] getEigenvectors_() {
        return eigenvectors_;
    }
}
