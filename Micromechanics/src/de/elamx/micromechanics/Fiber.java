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
 * Hier ist derzeit eine Vererbung der DefaultMaterial-Klasse NICHT möglich. 
 * Dies würde bei einem Lookup-Aufruf sowohl alle wirklichen DefaultMaterial-
 * Objekte liefern, als auch alle Subklassen. Damit ist eine saubere Trennung
 * nicht mehr gegeben.
 * 
 * Somit muss diese Klasse leider neu implementiert werden.
 * 
 * @author Andreas Hauffe
 */
public class Fiber extends Material{
    
    private static final int UPDATE_PRIORITY = 50;

    // E-Modul in Faserrichtung
    private double Epar  = 0.0;
    public static final String PROP_EPAR = "Epar";
    
    // E-Modul quer zur Faserrichtung
    private double Enor  = 0.0;
    public static final String PROP_ENOR = "Enor"; 
    
    // Querkontraktionszahl (nu12/Epar = nue21/Enor - also im Normalfall der größere Wert)
    private double nue12 = 0.0;
    public static final String PROP_NUE12 = "nue12";
    
    // Schubmodul
    private double G     = 0.0;
    public static final String PROP_G = "G";
    
    // Schubmodul 13
    private double G13   = 0.0;
    public static final String PROP_G13 = "G13";
    
    // Schubmodul 23
    private double G23   = 0.0;
    public static final String PROP_G23 = "G23";
    
    // Dichte
    private double rho   = 0.0;
    public static final String PROP_RHO = "rho";

    // hygrothermale Eigenschaften
    // Wärmeausdehnungskoeffizient in Faserrichtung
    private double alphaTPar = 0.0;
    public static final String PROP_ALPHATPAR = "alphaTPar";
    
    // Wärmeausdehnungskoeffizient quer zur Faserrichtung
    private double alphaTNor = 0.0;
    public static final String PROP_ALPHATNOR = "alphaTNor";
    
    // Quellausdehnungskoeffizient in Faserrichtung
    private double betaPar   = 0.0;
    public static final String PROP_BETAPAR = "betaPar";
    
    // Quellausdehnungskoeffizient quer zur Faserrichtung
    private double betaNor   = 0.0;
    public static final String PROP_BETANOR = "betaNor";

    public Fiber(String uid, String name, double Epar, double Enor, double nue12, double G, double rho, boolean addToLookup) {
        super(uid, name, addToLookup);
        this.Epar  = Epar;
        this.Enor  = Enor;
        this.nue12 = nue12;
        this.G     = G;
        this.rho   = rho;
    }
    
    /**
     * Setzen des E-Moduls in Faserrichtung E<sub>||</sub> des Materials.
     * @param Epar E-Modul in Faserrichtung E<sub>||</sub>
     */
    public void setEpar(double Epar){
        double oldEpar = this.Epar;
        this.Epar = Epar;
        firePropertyChange(PROP_EPAR, oldEpar, Epar);
    }
    /**
     * Liefert den E-Modul in Faserrichtung E<sub>||</sub> des Materials.
     * @return E-Modul in Faserrichtung E<sub>||</sub>
     */
    @Override
    public double getEpar(){return Epar;}

    /**
     * Setzen des E-Moduls quer zur Faserrichtung E<sub>&perp;</sub> des Materials.
     * @param Enor E-Modul quer zur Faserrichtung E<sub>&perp;</sub>
     */
    public void setEnor(double Enor){
        double oldEnor = this.Enor;
        this.Enor = Enor;
        firePropertyChange(PROP_ENOR, oldEnor, this.Enor);
    }
    /**
     * Liefert den E-Modul quer zur Faserrichtung E<sub>&perp;</sub> des Materials.
     * @return E-Modul quer zur Faserrichtung E<sub>&perp;</sub>
     */
    @Override
    public double getEnor(){return Enor;}

    /**
     * Setzen der Querkontraktionszahl &nu;<sub>12</sub> des Materials. Dabei gilt folgende
     * Beziehung<br />
     * &nu;<sub>12</sub> * E<sub>&perp;</sub> = &nu;<sub>21</sub> * E<sub>||</sub>
     * @param nue Querkontraktionszahl &nu;<sub>12</sub>
     */
    public void setNue12(double nue){
        double oldNue12 = this.nue12;
        nue12 = nue;
        firePropertyChange(PROP_NUE12, oldNue12, nue12);
    }
    /**
     * Liefert die Querkontraktionszahl &nu;<sub>12</sub> des Materials. Dabei gilt folgende
     * Beziehung<br />
     * &nu;<sub>12</sub> * E<sub>&perp;</sub> = &nu;<sub>21</sub> * E<sub>||</sub>
     * @return die Querkontraktionszahl &nu;<sub>12</sub> des Materials
     */
    @Override
    public double getNue12(){return nue12;}

    /**
     * Setzen des Schubmoduls G<sub>||&perp;</sub> des Materials.
     * @param G Schubmoduls G<sub>||&perp;</sub>
     */
    public void setG(double G){
        double oldG = this.G;
        this.G = G;
        firePropertyChange(PROP_G, oldG, this.G);
    }
    /**
     * Liefert den Schubmoduls G<sub>||&perp;</sub> des Materials.
     * @return Schubmodul G<sub>||&perp;</sub> des Materials
     */
    @Override
    public double getG(){return G;}

    /**
     * Setzen der transversalen Schubsteifigkeit G<sub>||&perp;</sub> des Materials.
     * @param G13 transversale Schubsteifigkeit G<sub>||&perp;</sub> des Materials
     */
    public void setG13(double G13){
        double oldG13 = this.G13;
        this.G13 = G13;
        firePropertyChange(PROP_G13, oldG13, this.G13);
    }
    /**
     * Liefert die transversale Schubsteifigkeit G<sub>||&perp;</sub> des Materials.
     * @return transversale Schubsteifigkeit G<sub>||&perp;</sub> des Materials
     */
    @Override
    public double getG13(){return G13;}

    /**
     * Setzen der transversalen Schubsteifigkeit G<sub>&perp;&perp;</sub> des Materials.
     * @param G23 transversale Schubsteifigkeit G<sub>&perp;&perp;</sub> des Materials
     */
    public void setG23(double G23){
        double oldG23 = this.G23;
        this.G23 = G23;
        firePropertyChange(PROP_G23, oldG23, this.G23);
    }
    /**
     * Liefert die transversale Schubsteifigkeit G<sub>&perp;&perp;</sub> des Materials.
     * @return transversale Schubsteifigkeit G<sub>&perp;&perp;</sub> des Materials
     */
    @Override
    public double getG23(){return G23;}

    /**
     * Setzen der Dichte &rho; des Materials.
     * @param rho Dichte &rho; des Materials
     */
    public void setRho(double rho){
        double oldRho = this.rho;
        this.rho = rho;
        firePropertyChange(PROP_RHO, oldRho, this.rho);
    }
    /**
     * Liefert die Dichte &rho des Materials
     * @return Dichte &rho des Materials
     */
    @Override
    public double getRho(){return rho;}

    /**
     * Liefert den Wärmeausdehnungskoeffizient in Faserrichtung &alpha;<sub>||</sub>
     * @return Wärmeausdehnungskoeffizient in Faserrichtung &alpha;<sub>||</sub>
     */
    @Override
    public double getAlphaTPar() {
        return alphaTPar;
    }
    /**
     * Setzen des Wärmeausdehnungskoeffizient in Faserrichtung &alpha;<sub>||</sub>
     * @param alphaTPar Wärmeausdehnungskoeffizient in Faserrichtung &alpha;<sub>||</sub>
     */
    public void setAlphaTPar(double alphaTPar) {
        double oldAlphaTPar = this.alphaTPar;
        this.alphaTPar = alphaTPar;
        firePropertyChange(PROP_ALPHATPAR, oldAlphaTPar, this.alphaTPar);
    }

    /**
     * Liefert den Wärmeausdehnungskoeffizient quer zur Faserrichtung &alpha;<sub>&perp;</sub>
     * @return Wärmeausdehnungskoeffizient quer zur Faserrichtung &alpha;<sub>&perp;</sub>
     */
    @Override
    public double getAlphaTNor() {
        return alphaTNor;
    }
    /**
     * Setzen des Wärmeausdehnungskoeffizient quer zur Faserrichtung &alpha;<sub>&perp;</sub>
     * @param alphaTNor Wärmeausdehnungskoeffizient quer zur Faserrichtung &alpha;<sub>&perp;</sub>
     */
    public void setAlphaTNor(double alphaTNor) {
        double oldAlphaTNor = this.alphaTNor;
        this.alphaTNor = alphaTNor;
        firePropertyChange(PROP_ALPHATNOR, oldAlphaTNor, this.alphaTNor);
    }

    /**
     * Liefert den Quellausdehnungskoeffizient in Faserrichtung &beta;<sub>||</sub>
     * @return Quellausdehnungskoeffizient in Faserrichtung &beta;<sub>||</sub>
     */
    @Override
    public double getBetaPar() {
        return betaPar;
    }
    /**
     * Setzen des Quellausdehnungskoeffizient in Faserrichtung &beta;<sub>||</sub>
     * @param betaPar Quellausdehnungskoeffizient in Faserrichtung &beta;<sub>||</sub>
     */
    public void setBetaPar(double betaPar) {
        double oldBetaPar = this.betaPar;
        this.betaPar = betaPar;
        firePropertyChange(PROP_BETAPAR, oldBetaPar, betaPar);
    }

    /**
     * Liefert den Quellausdehnungskoeffizient quer zur Faserrichtung &beta;<sub>&perp;</sub>
     * @return Quellausdehnungskoeffizient quer zur Faserrichtung &beta;<sub>&perp;</sub>
     */
    @Override
    public double getBetaNor() {
        return betaNor;
    }
    /**
     * Setzen des Quellausdehnungskoeffizient quer zur Faserrichtung &beta;<sub>&perp;</sub>
     * @param betaNor Quellausdehnungskoeffizient quer zur Faserrichtung &beta;<sub>&perp;</sub>
     */
    public void setBetaNor(double betaNor) {
        double oldBetaNor = this.betaNor;
        this.betaNor = betaNor;
        firePropertyChange(PROP_BETANOR, oldBetaNor, betaNor);
    }

    /**
     * Vergleicht die Eigenschaften des übergegeben Material-Objekts mit den eigenen Werten.
     * @param material Material-Objekt mit dem verglichen werden soll.
     * @return Falls alle Eigenschaften gleich sind <CODE>true</CODE> sonst <CODE>false</CODE>
     */
    @Override
    public boolean isEqual(Material material) {
        if (!(material instanceof Fiber)) return false;
        return (Epar  == material.getEpar() &&
                Enor  == material.getEnor() &&
                nue12 == material.getNue12() &&
                G     == material.getG() &&
                rho   == material.getRho() &&
                G13   == material.getG13() &&
                G23   == material.getG23() &&
                alphaTNor == material.getAlphaTNor() &&
                alphaTPar == material.getAlphaTPar() &&
                betaNor == material.getBetaNor() &&
                betaPar == material.getBetaPar());
    }

    /**
     * Erzeugt ein Kopie des Materialobjektes. Auch die das Strengthobjekt wird
     * als Kopie hinzugefügt. Damit sind alle Daten vollkommen unabhängig von
     * den alten Daten.
     * @return Kopie des Materials
     */
    @Override
    public Material getCopy(){
        Fiber mat = new Fiber(UUID.randomUUID().toString(),
                                                  getName(),
                                                  Epar,
                                                  Enor,
                                                  nue12,
                                                  G,
                                                  rho, 
                                                  true);

        mat.setG13(G13);
        mat.setG23(G23);
        mat.setAlphaTPar(alphaTPar);
        mat.setAlphaTNor(alphaTNor);
        mat.setBetaPar(betaPar);
        mat.setBetaNor(betaNor);

        return mat;
    }

    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;    
    }

    @Override
    public double getRParTen() {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getRParCom() {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getRNorTen() {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getRNorCom() {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getRShear() {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
