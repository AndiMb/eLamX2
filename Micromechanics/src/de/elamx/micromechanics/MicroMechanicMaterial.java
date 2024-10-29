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

import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.Material;
import de.elamx.micromechanics.models.ManualInputDummyModel;
import de.elamx.micromechanics.models.MicroMechModel;
import de.elamx.micromechanics.models.Mischungsregel_m;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.UUID;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

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
public class MicroMechanicMaterial extends LayerMaterial implements PropertyChangeListener {
    
    private static final int UPDATE_PRIORITY = 100;

    private Fiber fibre;
    public static final String PROP_FIBRE = "fibre";

    private Matrix matrix;
    public static final String PROP_MATRIX = "matrix";

    private double phi;
    public static final String PROP_PHI = "phi";

    private MicroMechModel rhoModel;
    public static final String PROP_RHOMODEL = "rhoModel";

    private MicroMechModel EParModel;
    public static final String PROP_EPARMODEL = "EParModel";

    private MicroMechModel ENorModel;
    public static final String PROP_ENORMODEL = "ENorModel";
    
    private MicroMechModel nue12Model;
    public static final String PROP_NUE12MODEL = "nue12Model";

    private MicroMechModel GModel;
    public static final String PROP_GMODEL = "GModel";

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
    

    // Zugfestigkeit in Faserrichtung
    private double RParTen = 0.0;
    public static final String PROP_RPARTEN = "RParTen";
    
    // Druckfestigkeit in Faserrichtung
    private double RParCom = 0.0;
    public static final String PROP_RPARCOM = "RParCom";
    
    // Zugfestigkeit quer zur Faserrichtung
    private double RNorTen = 0.0;
    public static final String PROP_RNORTEN = "RNorTen";
    
    // Druckestigkeit quer zur Faserrichtung
    private double RNorCom = 0.0;
    public static final String PROP_RNORCOM = "RNorCom";
    
    // Schubfestigkeit in der Ebene
    private double RShear  = 0.0;
    public static final String PROP_RSHEAR = "RShear";

    @SuppressWarnings("this-escape")
    public MicroMechanicMaterial(String uid, String name, Fiber fibre, Matrix matrix, double phi, boolean addToLookup) {
        super(uid, name, addToLookup);
        this.fibre = fibre;
        this.fibre.addPropertyChangeListener(this);
        this.matrix = matrix;
        this.matrix.addPropertyChangeListener(this);
        this.phi = phi;
        
        if (this.EParModel == null) {
            Lookup lkp = Lookups.forPath("elamx/micromechmodel");
            Collection<? extends MicroMechModel> c = lkp.lookupAll(MicroMechModel.class);
            for (MicroMechModel mmm : c) {
                if (mmm.getClass().equals(Mischungsregel_m.class)) {
                    this.rhoModel = mmm;
                    this.EParModel = mmm;
                    this.ENorModel = mmm;
                    this.nue12Model = mmm;
                    this.GModel = mmm;
                    break;
                }
            }
        }
    }
    
    /**
     * Liefert das mikromechanische Modell für die Dichte.
     *
     * @return  Das mikromechanische Modell für die Dichte
     */
    public MicroMechModel getRhoModel() {
        return rhoModel;
    }

    /**
     * Setzen des mikromechanischen Modells für die Dichte.
     * Falls <CODE>null</CODE> übergeben wird, wird beim Aufruf von 
     * <CODE>getRho()</CODE> der über <CODE>setRho()</CODE>
     * gesetzte Wert verwendet.
     *
     * @param rhoModel Das mikromechanische Modell für die Dichte.
     */
    public void setRhoModel(MicroMechModel rhoModel) {
        MicroMechModel oldrhoModel = this.rhoModel;
        this.rhoModel = rhoModel;
        firePropertyChange(PROP_RHOMODEL, oldrhoModel, rhoModel);
    }
    
    /**
     * Liefert das mikromechanische Modell für die Steifigkeit in Faserrichtung.
     *
     * @return  Das mikromechanische Modell für die Steifigkeit in Faserrichtung
     */
    public MicroMechModel getEParModel() {
        return EParModel;
    }

    /**
     * Setzen des mikromechanischen Modells für die Steifigkeit in Faserrichtung.
     * Falls <CODE>null</CODE> übergeben wird, wird beim Aufruf von 
     * <CODE>getEpar()</CODE> der über <CODE>setEpar()</CODE>
     * gesetzte Wert verwendet.
     *
     * @param EParModel Das mikromechanische Modell für die Steifigkeit in Faserrichtung.
     */
    public void setEParModel(MicroMechModel EParModel) {
        MicroMechModel oldEParModel = this.EParModel;
        this.EParModel = EParModel;
        firePropertyChange(PROP_EPARMODEL, oldEParModel, EParModel);
    }
    
    /**
     * Liefert das mikromechanische Modell für die Steifigkeit quer zur
     * Faserrichtung.
     *
     * @return  Das mikromechanische Modell für die Steifigkeit quer zur
     * Faserrichtung
     */
    public MicroMechModel getENorModel() {
        return ENorModel;
    }

    /**
     * Setzen des mikromechanischen Modells für die Steifigkeit quer zur
     * Faserrichtung.
     * Falls <CODE>null</CODE> übergeben wird, wird beim Aufruf von 
     * <CODE>getEnor()</CODE> der über <CODE>setEnor()</CODE>
     * gesetzte Wert verwendet.
     *
     * @param ENorModel Das mikromechanische Modell für die Steifigkeit quer zur
     * Faserrichtung.
     */
    public void setENorModel(MicroMechModel ENorModel) {
        MicroMechModel oldENorModel = this.ENorModel;
        this.ENorModel = ENorModel;
        firePropertyChange(PROP_ENORMODEL, oldENorModel, ENorModel);
    }
    
    /**
     * Liefert das mikromechanische Modell für die Querkontraktionszahl.
     *
     * @return  Das mikromechanische Modell für die Querkontraktionszahl
     */
    public MicroMechModel getNue12Model() {
        return nue12Model;
    }

    /**
     * Setzen des mikromechanischen Modells für die Querkontraktionszahl.
     * Falls <CODE>null</CODE> übergeben wird, wird beim Aufruf von 
     * <CODE>getNue12()</CODE> der über <CODE>setNue12()</CODE>
     * gesetzte Wert verwendet.
     *
     * @param nue12Model Das mikromechanische Modell für die Querkontraktionszahl
     */
    public void setNue12Model(MicroMechModel nue12Model) {
        MicroMechModel oldnue12Model = this.nue12Model;
        this.nue12Model = nue12Model;
        firePropertyChange(PROP_NUE12MODEL, oldnue12Model, nue12Model);
    }
    
    /**
     * Liefert das mikromechanische Modell für den Schubmodul.
     *
     * @return  Das mikromechanische Modell für den Schubmodul
     */
    public MicroMechModel getGModel() {
        return GModel;
    }

    /**
     * Setzen des mikromechanischen Modells für den Schubmodul.
     * Falls <CODE>null</CODE> übergeben wird, wird beim Aufruf von 
     * <CODE>getG()</CODE> der über <CODE>setG()</CODE>
     * gesetzte Wert verwendet.
     *
     * @param GModel Das mikromechanische Modell für den Schubmodul.
     */
    public void setGModel(MicroMechModel GModel) {
        MicroMechModel oldGModel = this.GModel;
        this.GModel = GModel;
        firePropertyChange(PROP_GMODEL, oldGModel, GModel);
    }

    /**
     * Liefert den Faservolumengehalt zurück. Der Wert liegt zwischen 0 und 1.0.
     *
     * @return Faservolumengehalt
     */
    public double getPhi() {
        return phi;
    }

    /**
     * Setzen des Faservolumengehalts. Dieser sollte zwischen 0 (0%) und 1 
     * (100%) liegen. Falls der Werte kleiner bzw. größer als die genannten 
     * Grenzen ist, wird er auf den Grenzwert gesetzt.
     *
     * @param phi Faservolumengehalt
     */
    public void setPhi(double phi) {
        phi = Math.max(phi, 0.0);
        phi = Math.min(phi, 1.0);
        double oldPhi = this.phi;
        this.phi = phi;
        firePropertyChange(PROP_PHI, oldPhi, phi);
    }

    /**
     * Liefert das Matrix-Objekt zurück.
     *
     * @return Matrix-Objekt
     */
    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * Setzen des Matrix-Objekts
     *
     * @param matrix neues Matrix-Objekt
     */
    public void setMatrix(Matrix matrix) {
        this.matrix.removePropertyChangeListener(this);
        Matrix oldMatrix = this.matrix;
        this.matrix = matrix;
        this.matrix.addPropertyChangeListener(this);
        firePropertyChange(PROP_MATRIX, oldMatrix, matrix);
    }

    /**
     * Liefert das Faser-Objekt zurück.
     *
     * @return Faser-Objekt
     */
    public Fiber getFibre() {
        return fibre;
    }

    /**
     * Setzen des Faser-Objekts.
     *
     * @param fibre Faser-Objekt
     */
    public void setFibre(Fiber fibre) {
        this.fibre.removePropertyChangeListener(this);
        Fiber oldFibre = this.fibre;
        this.fibre = fibre;
        this.fibre.addPropertyChangeListener(this);
        firePropertyChange(PROP_FIBRE, oldFibre, fibre);
    }
    /**
     * Liefert die Dichte &rho des Materials
     * @return Dichte &rho des Materials
     */
    @Override
    public double getRho(){
        return rhoModel instanceof ManualInputDummyModel ? rho : rhoModel.getRho(fibre, matrix, phi);
    }
    
    @Override
    public double getEpar(){
        return EParModel instanceof ManualInputDummyModel ? Epar : EParModel.getE11(fibre, matrix, phi);
    }
    
    @Override
    public double getEnor(){
        return ENorModel instanceof ManualInputDummyModel ? Enor : ENorModel.getE22(fibre, matrix, phi);
    }
    
    @Override
    public double getNue12(){
        return nue12Model instanceof ManualInputDummyModel ? nue12 : nue12Model.getNue12(fibre, matrix, phi);
    }
    
    @Override
    public double getG(){
        return GModel instanceof ManualInputDummyModel ? G : GModel.getG12(fibre, matrix, phi);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Matrix){
            this.firePropertyChange(PROP_MATRIX, null, matrix);
        }else if (evt.getSource() instanceof Fiber){
            this.firePropertyChange(PROP_FIBRE, null, fibre);
        }
    }
    
    /**
     * Setzen des E-Moduls in Faserrichtung E<sub>||</sub> des Materials.
     * @param Epar E-Modul in Faserrichtung E<sub>||</sub>
     */
    public void setEpar(double Epar){
        double oldEpar = this.Epar;
        this.Epar = Epar;
        firePropertyChange(PROP_EPAR, oldEpar, Epar);
        if ((Epar != oldEpar) && (Epar < 0.)) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(MicroMechanicMaterial.class, "Warning.negativeelasticmodulus"), NotifyDescriptor.WARNING_MESSAGE));
        }
    }

    /**
     * Setzen des E-Moduls quer zur Faserrichtung E<sub>&perp;</sub> des Materials.
     * @param Enor E-Modul quer zur Faserrichtung E<sub>&perp;</sub>
     */
    public void setEnor(double Enor){
        double oldEnor = this.Enor;
        this.Enor = Enor;
        firePropertyChange(PROP_ENOR, oldEnor, this.Enor);
        if ((Enor != oldEnor) && (Enor < 0.)) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(MicroMechanicMaterial.class, "Warning.negativeelasticmodulus"), NotifyDescriptor.WARNING_MESSAGE));
        }
    }

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
        if ((nue != oldNue12) && (nue < 0.)) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(MicroMechanicMaterial.class, "Warning.negativepoissonratio"), NotifyDescriptor.WARNING_MESSAGE));
        }
    }

    /**
     * Setzen des Schubmoduls G<sub>||&perp;</sub> des Materials.
     * @param G Schubmoduls G<sub>||&perp;</sub>
     */
    public void setG(double G){
        double oldG = this.G;
        this.G = G;
        firePropertyChange(PROP_G, oldG, this.G);
        if ((G != oldG) && (G < 0.)) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(MicroMechanicMaterial.class, "Warning.negativeshearmodulus"), NotifyDescriptor.WARNING_MESSAGE));
        }
    }

    /**
     * Setzen der transversalen Schubsteifigkeit G<sub>||&perp;</sub> des Materials.
     * @param G13 transversale Schubsteifigkeit G<sub>||&perp;</sub> des Materials
     */
    public void setG13(double G13){
        double oldG13 = this.G13;
        this.G13 = G13;
        firePropertyChange(PROP_G13, oldG13, this.G13);
        if ((G13 != oldG13) && (G13 < 0.)) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(MicroMechanicMaterial.class, "Warning.negativeshearmodulus"), NotifyDescriptor.WARNING_MESSAGE));
        }
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
        if ((G23 != oldG23) && (G23 < 0.)) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(MicroMechanicMaterial.class, "Warning.negativeshearmodulus"), NotifyDescriptor.WARNING_MESSAGE));
        }
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
     * Setzen der Zugfestigkeit in Faserrichtung R<sub>||,z</sub> der Schicht.
     * @param RParTen Zugfestigkeit in Faserrichtung R<sub>||,z</sub> der Schicht.
     */
    public void   setRParTen(double RParTen){
        double oldRParTen = this.RParTen;
        this.RParTen = RParTen;
        firePropertyChange(PROP_RPARTEN, oldRParTen, this.RParTen);
    }
    /**
     * Liefert die Zugfestigkeit in Faserrichtung R<sub>||,z</sub> der Schicht.
     * @return Zugfestigkeit in Faserrichtung R<sub>||,z</sub> der Schicht.
     */
    @Override
    public double getRParTen(){return RParTen;}

    /**
     * Setzen der Druckfestigkeit in Faserrichtung R<sub>||,d</sub> der Schicht. Dieser Wert muss positiv sein!
     * @param RParCom Druckfestigkeit in Faserrichtung R<sub>||,d</sub> der Schicht.
     */
    public void   setRParCom(double RParCom){
        double oldRParCom = this.RParCom;
        this.RParCom = Math.abs(RParCom);
        firePropertyChange(PROP_RPARCOM, oldRParCom, this.RParCom);
    }
    /**
     * Liefert die Druckfestigkeit in Faserrichtung R<sub>||,d</sub> der Schicht.
     * @return Druckfestigkeit in Faserrichtung R<sub>||,d</sub> der Schicht. (ist positiv)
     */
    @Override
    public double getRParCom(){return RParCom;}

    /**
     * Setzen der Zugfestigkeit quer zur Faserrichtung R<sub>&perp;,z</sub> der Schicht.
     * @param RNorTen Zugfestigkeit quer zur Faserrichtung R<sub>&perp;,z</sub> der Schicht.
     */
    public void   setRNorTen(double RNorTen){
        double oldRNorTen = this.RNorTen;
        this.RNorTen = RNorTen;
        firePropertyChange(PROP_RNORTEN, oldRNorTen, this.RNorTen);
    }
    /**
     * Liefert die Zugfestigkeit quer zur Faserrichtung R<sub>&perp;,z</sub> der Schicht.
     * @return Zugfestigkeit quer zur Faserrichtung R<sub>&perp;,z</sub> der Schicht. (ist positiv)
     */
    @Override
    public double getRNorTen(){return RNorTen;}

    /**
     * Setzen der Druckfestigkeit quer zur Faserrichtung R<sub>&perp;,d</sub> der Schicht. Dieser Wert muss positiv sein!
     * @param RNorCom Druckfestigkeit quer zur Faserrichtung R<sub>&perp;,d</sub> der Schicht.
     */
    public void   setRNorCom(double RNorCom){
        double oldRNorCom = this.RNorCom;
        this.RNorCom = Math.abs(RNorCom);
        firePropertyChange(PROP_RNORCOM, oldRNorCom, this.RNorCom);
    }
    /**
     * Liefert die Druckfestigkeit quer zur Faserrichtung R<sub>&perp;,d</sub> der Schicht.
     * @return Druckfestigkeit quer zur Faserrichtung R<sub>&perp;,d</sub> der Schicht. (ist positiv)
     */
    @Override
    public double getRNorCom(){return RNorCom;}

    /**
     * Setzen der Schubfestigkeit R<sub>||&perp;</sub> der Schicht.
     * @param RShear Schubfestigkeit R<sub>||&perp;</sub> der Schicht
     */
    public void   setRShear(double RShear){
        double oldRShear = this.RShear;
        this.RShear = Math.abs(RShear);
        firePropertyChange(PROP_RSHEAR, oldRShear, this.RShear);
    }
    /**
     * Liefert die Schubfestigkeit R<sub>||&perp;</sub> der Schicht.
     * @return Schubfestigkeit R<sub>||&perp;</sub> der Schicht.
     */
    @Override
    public double getRShear(){return RShear;}

    /**
     * Vergleicht die Eigenschaften des übergegeben Material-Objekts mit den eigenen Werten.
     * @param material Material-Objekt mit dem verglichen werden soll.
     * @return Falls alle Eigenschaften gleich sind <CODE>true</CODE> sonst <CODE>false</CODE>
     */
    @Override
    public boolean isEqual(Material material) {
        if (!(material instanceof MicroMechanicMaterial)) return false;
        if (!(Epar  == material.getEpar() &&
            Enor  == material.getEnor() &&
            nue12 == material.getNue12() &&
            G     == material.getG() &&
            rho   == material.getRho() &&
            RParTen == material.getRParTen() &&
            RParCom == material.getRParCom() &&
            RNorTen == material.getRNorTen() &&
            RNorCom == material.getRNorCom() &&
            RShear  == material.getRShear())){
                return false;
        }
        if (material.getAdditionalValueKeySet().size() != getAdditionalValueKeySet().size()) return false;
        for(String key : getAdditionalValueKeySet()){
            if (material.getAdditionalValue(key) != getAdditionalValue(key)){
                return false;
            }
        }
        return true;
    }

    /**
     * Erzeugt ein Kopie des Materialobjektes. Auch die das Strengthobjekt wird
     * als Kopie hinzugefügt. Damit sind alle Daten vollkommen unabhängig von
     * den alten Daten.
     * @return Kopie des Materials
     */
    @Override
    public Material getCopy(){
        MicroMechanicMaterial mat = new MicroMechanicMaterial(UUID.randomUUID().toString(),
                                                  getName(),
                                                  fibre,
                                                  matrix,
                                                  phi,
                                                  true);

        mat.setEpar(Epar);
        mat.setEnor(Enor);
        mat.setNue12(nue12);
        mat.setG(G);
        mat.setRho(rho);
        mat.setAlphaTPar(alphaTPar);
        mat.setAlphaTNor(alphaTNor);
        mat.setBetaPar(betaPar);
        mat.setBetaNor(betaNor);
        mat.setRNorCom(RNorCom);
        mat.setRNorTen(RNorTen);
        mat.setRParCom(RParCom);
        mat.setRParTen(RParTen);
        mat.setRShear(RShear);
        mat.setEParModel(EParModel);
        mat.setENorModel(ENorModel);
        mat.setNue12Model(nue12Model);
        mat.setGModel(GModel);
        mat.setRhoModel(rhoModel);
        for(String key : getAdditionalValueKeySet()){
            mat.putAdditionalValue(key, getAdditionalValue(key));
        }

        return mat;
    }

    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;    
    }
    

}
