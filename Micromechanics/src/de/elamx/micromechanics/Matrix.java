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
package de.elamx.micromechanics;

import de.elamx.laminate.Material;
import java.util.UUID;

/**
 *
 * @author Andreas Hauffe
 */
public class Matrix extends Material{
    
    private static final int UPDATE_PRIORITY = 50;
    
    private double E;

    public static final String PROP_E = "E";
    
    private double nue;

    public static final String PROP_NUE = "nue";
    
    private double G;

    public static final String PROP_G = "G";
    
    private double alpha;

    public static final String PROP_ALPHA = "alpha";
    
    private double beta;

    public static final String PROP_BETA = "beta";
    
    private double rho;

    public static final String PROP_RHO = "rho";

    public Matrix(String uid, String name, double E, double nue, double rho, boolean addToLookup) {
        super(uid, name, addToLookup);
        this.E = E;
        this.nue = nue;
        this.G = E / (2 * (1.0 + nue));
        this.rho = rho;
    }

    /**
     * Set the value of rho
     *
     * @param rho new value of rho
     */
    public void setRho(double rho) {
        double oldRho = this.rho;
        this.rho = rho;
        firePropertyChange(PROP_RHO, oldRho, rho);
    }

    /**
     * Set the value of beta
     *
     * @param beta new value of beta
     */
    public void setBeta(double beta) {
        double oldBeta = this.beta;
        this.beta = beta;
        firePropertyChange(PROP_BETA, oldBeta, beta);
    }

    /**
     * Set the value of alpha
     *
     * @param alpha new value of alpha
     */
    public void setAlpha(double alpha) {
        double oldAlpha = this.alpha;
        this.alpha = alpha;
        firePropertyChange(PROP_ALPHA, oldAlpha, alpha);
    }

    /**
     * Set the value of G
     *
     * @param G new value of G
     */
    public void setG(double G) {
        /*double oldG = this.G;
        this.G = G;
        firePropertyChange(PROP_G, oldG, G);*/
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Set the value of nue
     *
     * @param nue new value of nue
     */
    public void setNue(double nue) {
        double oldNue = this.nue;
        this.nue = nue;
        firePropertyChange(PROP_NUE, oldNue, nue);
        double oldG = G;
        G = E / (2 * (1.0 + nue));
        firePropertyChange(PROP_G, oldG, G);
    }

    /**
     * Set the value of E
     *
     * @param E new value of E
     */
    public void setE(double E) {
        double oldE = this.E;
        this.E = E;
        firePropertyChange(PROP_E, oldE, E);
        double oldG = G;
        G = E / (2 * (1.0 + nue));
        firePropertyChange(PROP_G, oldG, G);
    }

    public double getE() {
        return E;
    }

    public double getNue() {
        return nue;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    @Override
    public double getEpar() {
        return E;
    }

    @Override
    public double getEnor() {
        return E;
    }

    @Override
    public double getNue12() {
        return nue;
    }

    @Override
    public double getG() {
        return G;
    }

    @Override
    public double getG13() {
        return G;
    }

    @Override
    public double getG23() {
        return G;
    }

    @Override
    public double getRho() {
        return rho;
    }

    @Override
    public double getAlphaTPar() {
        return alpha;
    }

    @Override
    public double getAlphaTNor() {
        return alpha;
    }

    @Override
    public double getBetaPar() {
        return beta;
    }

    @Override
    public double getBetaNor() {
        return beta;
    }

    @Override
    public boolean isEqual(Material material) {
        if (!(material instanceof Matrix)) return false;
        return (E  == material.getEpar() &&
                nue == material.getNue12() &&
                G     == material.getG() &&
                rho   == material.getRho() &&
                alpha == material.getAlphaTPar() &&
                beta == material.getBetaPar());
    }

    @Override
    public Material getCopy() {
        Matrix mat = new Matrix(UUID.randomUUID().toString(),
                                                  getName(),
                                                  E,
                                                  nue,
                                                  rho, 
                                                  true);

        mat.setAlpha(alpha);
        mat.setBeta(beta);
        return mat;
    }

    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;    
    }

    @Override
    public double getRParTen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getRParCom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getRNorTen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getRNorCom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getRShear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
