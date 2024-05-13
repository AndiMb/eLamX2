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

import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.mathtools.MatrixTools;
import java.util.ArrayList;

/**
 * Diese Klasse repräsentiert ein gesamten Laminat bestehend aus 
 * Einzelschichten. Diese werden durch <CODE>Layer</CODE>-Objekte definiert. 
 * Diese Klasse stellt alle Eigenschaften eine Laminats zur Verfügung, wie
 * z.B. die ABD-Matrix und die Ingenieurskonstanten.<br><br>
 * Intern wird das Laminat in einem <CODE>ArrayList&lt;Layer&gt;</CODE> der 
 * Einzelschichten gespeichert. 
 * 
 * @author Andreas Hauffe - TU-Dresden - 10.11.2008
 */
public class CLT_Laminate extends CLT_Object{
    
    private static final double EPS = 0.000000000001;
    
    private final Laminat laminat;

    private final double[][] A      = new double[3][3];  // A-Matrix
    private final double[][] B      = new double[3][3];  // B-Matrix
    private final double[][] D      = new double[3][3];  // D-Matrix
    private final double[][] ABD    = new double[6][6];  // ABD-Matrix
    private double[][] ABDInv = new double[6][6];  // Inverse ABD-Matrix
    private double[][] DwithZeroD12D16 = new double[3][3]; // D-Matrix mit Nulleinträgen für D12 und D16
    private double[][] Dtilde = new double[3][3];  // Dtilde-Matrix für Stabilitätsanalyse
    private double  tges      = 0.0;               // Gesamtdicke des Laminats
    private boolean isSym     = false;             // Flag, ob das Laminat symmetrisch aufgebaut ist

    // Dimensionslose Parameter der D-Matrix
    private double beta_D;                           // Seydel's orthotropy parameter
    private double nu_D;                             // Transverse contraction parameter
    private double gamma_D, delta_D;                 // Anisotropy parameters
    
    //private double I0, I1, I2;                     // Mass moments of inertia
    
    private CLT_Layer[] layers;
    
    /** 
     * Konstruktor eines neues Laminats. Übergeben wird dazu der Lagenaufbau als
     * <CODE>ArrayList&lt;Layer&gt;</CODE>, der die einzelnen <CODE>Layer</CODE>-Objekte enthält.
     * Es wird daraufhin direkt die ABD-Matrix berechnet. Die Lagen müssen darin
     * so sortiert sein, dass die Lage mit dem Index 0 bei t<sub>ges</sub>/2 liegt
     * und die Lage mit dem Index [AnzahlderLagen] bei -t<sub>ges</sub>/2 endet. 
     * @param laminat <CODE>ArrayList&lt;Layer&gt;</CODE>, der die einzelnen <CODE>Layer</CODE>-Objekte enthält
     */
    @SuppressWarnings("this-escape")
    public CLT_Laminate(Laminat laminat) {
        this.laminat = laminat;
        refresh();
        this.laminat.getLookup().add(this);
    }
    
    protected Laminat getLaminat(){
        return laminat;
    }

    /**
     * Alle notwendigen Daten des Laminates werden neu berechnet.
     * Bisher wird nur die ABD-Matrix neu bestimmt.
     */
    @Override
    public final void refresh(){
        initCLTLayers();
        calcABD();
        calcDwithZeroD12D16();
        calcDtilde();
        calculateNonDimensionalParameters();
    }
    
    public CLT_Layer[] getCLTLayers(){
        return layers;
    }
    
    private void calcABD(){
        
        int numLayers = layers.length;
        
        double[][] Qglo;
        double[] zm = new double[numLayers];
        double t;
        CLT_Layer layer;
        
        tges = 0.0;
        for (CLT_Layer l : layers) {
            tges += l.getLayer().getThickness();
        }
        double z0 = tges / 2.0 + laminat.getOffset();
        double zold = z0;
        for (int i = 0; i < numLayers; i++){
            layer = layers[i];
            t     = layer.getLayer().getThickness();
            zm[i] = zold - t/2.0;
            layer.setZm(zm[i]);
            zold -= t; 
        }
        double temp;
        for (int m = 0; m < 3; m++){
            for (int n = 0; n < 3; n++){
                A[m][n] = 0.0;
                B[m][n] = 0.0;
                D[m][n] = 0.0;
            }
        }
        for (int i = 0; i < numLayers; i++){
            layer = layers[i];
            Qglo  = layer.getQMatGlobal();
            t     = layer.getLayer().getThickness();

            for (int m = 0; m < 3; m++){
                for (int n = 0; n < 3; n++){
                    temp = Qglo[m][n] * t;
                    A[m][n] += temp;
                    B[m][n] += temp * zm[i];
                    D[m][n] += temp * (t*t/12.0 + zm[i]*zm[i]);
                }
            }
        }

        double Amax = 0.0, Bmax = 0.0;
        for (int m = 0; m < 3; m++){
            for (int n = 0; n <= m; n++){
                //if (!(B[m][n] < eps && B[m][n] > -eps)) isSym = false;
                if (Math.abs(A[m][n]) > Amax) {
                    Amax = Math.abs(A[m][n]);
                }
                if (Math.abs(B[m][n]) > Bmax) {
                    Bmax = Math.abs(B[m][n]);
                }
            }
        }
        isSym = false;
        if (Bmax < EPS*Amax) {
            isSym = true;
        }

        for(int ii = 0; ii < 3; ii++){
            for(int jj = 0; jj <= ii; jj++){
                ABD[ii][jj] = A[ii][jj];
            }
        }

        for(int ii = 0; ii < 3; ii++){
            System.arraycopy(B[ii], 0, ABD[ii+3], 0, 3);
        }

        for(int ii = 0; ii < 3; ii++){
            for(int jj = 0; jj <= ii; jj++){
                ABD[ii+3][jj+3] = D[ii][jj];
            }
        }
        
        for(int ii = 0; ii < 6; ii++){
            for(int jj = ii+1; jj < 6; jj++){
                ABD[ii][jj] = ABD[jj][ii];
            }
        }

        ABDInv = MatrixTools.getInverse(ABD);
    }

    private void calcDwithZeroD12D16() {
        DwithZeroD12D16 = new double[3][3];
        for (int ii = 0; ii < 3; ii++){
            System.arraycopy(D[ii], 0, DwithZeroD12D16[ii], 0, 3);
        }

        // Nullsetzen der D16 und D26-Terme
        DwithZeroD12D16[0][2] = 0.0;
        DwithZeroD12D16[1][2] = 0.0;
        DwithZeroD12D16[2][0] = 0.0;
        DwithZeroD12D16[2][1] = 0.0;
    }

    private void calcDtilde() {
        // Berechnen von D tilde
        double [][] Ainv    = MatrixTools.getInverse(A);

        double [][] helpMat1    = MatrixTools.MatMult(B, Ainv);
        double [][] helpMat2    = MatrixTools.MatMult(helpMat1, B);

        for (int ii = 0; ii < Dtilde.length; ii++) {
            for (int jj = 0; jj < Dtilde[0].length; jj++) {
                Dtilde[ii][jj] = D[ii][jj] - helpMat2[ii][jj];
            }
        }
    }
      
    private void calculateNonDimensionalParameters() {
        //Seydel's orthotropy parameter
        this.beta_D = (D[0][1] + 2. * D[2][2])/Math.sqrt(D[0][0] * D[1][1]);
        // Transverse contraction parameter
        this.nu_D = D[0][1]/Math.sqrt(D[0][0] * D[1][1]);
        //Anisotropy parameters
        this.gamma_D = D[0][2]/Math.pow((Math.pow(D[0][0], 3.) * D[1][1]), 0.25);
        this.delta_D = D[1][2]/Math.pow((D[0][0] * Math.pow(D[1][1], 3.)), 0.25);
    }
    
    private CLT_Layer[] initCLTLayers(){
        layers = new CLT_Layer[laminat.getNumberofLayers()];
        
        ArrayList<Layer> orig_layers = laminat.getAllLayers();
        
        CLT_Layer tempLayer;
        int ind = 0;
        for (Layer l : orig_layers) {
            if ((tempLayer = l.getLookup().lookup(CLT_Layer.class)) == null){
                layers[ind++] = new CLT_Layer(l);
            }else{
                layers[ind++] = tempLayer;
            }
        }

        return layers;
    }
    
    public double[][] getABDMatrix(){
        return ABD;
    }
    
    /**
     * Flag, ob der Lagenaufbau symmetrisch ist. Dies wird anhand der Einträge der
     * B-Matrix geprüft. Wenn alle Komponenten der B-Matrix kleiner 
     * &epsilon; = 0.00000001, sind liefert diese Methode <CODE>true</CODE>.<br>
     * Es wird nicht am Lagenaufbau direkt geprüft, da der Lagenaufbau nicht zwangweise
     * symmetrisch sein muss, um ein symmetrisches Verhalten zu haben. Wenn z.B.
     * eine Lagen mit Dicke 1mm und zwei mit Dicke 0,5mm enthalten sind, entspricht
     * das Verhalten einem symmetrischen Laminat ohne das der Lagenaufbau symmetrisch ist.
     * @return <CODE>true</CODE>, wenn symmetrisch; sonst <CODE>false</CODE> 
     */
    public boolean isSymmetric(){return isSym;}
    
    /**
     * Liefert die A-Matrix des Laminates.
     * @return A-Matrix des Laminates. (3x3)
     */
    public double[][] getAMatrix(){return A;}
    
    /**
     * Liefert die B-Matrix des Laminates.
     * @return B-Matrix des Laminates. (3x3)
     */
    public double[][] getBMatrix(){return B;}
    
    /**
     * Liefert die D-Matrix des Laminates.
     * @return D-Matrix des Laminates. (3x3)
     */
    public double[][] getDMatrix(){return D;}
    
    /**
     * Liefert die D-Matrix des Laminates mit Nulleinträgen für D12 und D16.
     * @return D-Matrix des Laminates mit Nulleinträgen für D12 und D16. (3x3)
     */
    public double[][] getDMatrixWithZeroD12D16(){return DwithZeroD12D16;}
    
    /**
     * Liefert die D-Tilde-Matrix des Laminates.
     * @return D-Tilde-Matrix des Laminates. (3x3)
     */
    public double[][] getDtildeMatrix(){return Dtilde;}

    /**
     * Liefert die inverse ABD-Matrix zurück.
     * @return inverse ABD-Matrix des Laminates. (6x6)
     */
    public double[][] getInvABDMatrix(){return ABDInv;}

    /**
     * Liefert die inverse A-Matrix des Laminates.
     * @return inverse A-Matrix des Laminates. (3x3)
     */
    public double[][] getaMatrix(){

        double[][] amat = new double[3][3];

        for(int ii = 0; ii < 3; ii++){
            System.arraycopy(ABDInv[ii], 0, amat[ii], 0, 3);
        }

        return amat;

    }

    /**
     * Liefert die inverse B-Matrix des Laminates.
     * Für die inverse ABD-Matrix gilt zu beachten:
     * <table>
     *  <tr>
     *   <td>a</td>
     *   <td>b</td>
     *  </tr>
     *  <tr>
     *   <td>b<sup>T</sup></td>
     *   <td>d</td>
     *  </tr>
     * </table>
     * @return inverse B-Matrix des Laminates. (3x3)
     */
    public double[][] getbMatrix(){

        /*
         * Das ist die Nachgiebigkeitsmatrix auf der oberen Dreiecksmatrix
         * Die Nachgiebigkeitsmatrix auf der unteren Dreiecksmatrix ist die
         * transponierte dieser Matrix
         */

        double[][] bmat = new double[3][3];

        for(int ii = 0; ii < 3; ii++){
            for(int jj = 0; jj < 3; jj++){
                bmat[ii][jj] = ABDInv[ii][jj+3];
            }
        }

        return bmat;

    }

    /**
     * Liefert die inverse D-Matrix des Laminates.
     * @return inverse D-Matrix des Laminates. (3x3)
     */
    public double[][] getdMatrix(){

        double[][] dmat = new double[3][3];

        for(int ii = 0; ii < 3; ii++){
            for(int jj = 0; jj < 3; jj++){
                dmat[ii][jj] = ABDInv[ii+3][jj+3];
            }
        }

        return dmat;
    }

    /** Liefert Seydels Orthotropieparameter der D-Matrix des Laminats.
     * @return Seydels Orthotropieparameter der D-Matrix des Laminats
     */
    public double getBetaD() {
        return this.beta_D;
    }

    /**
     * Liefert den transversalen Kontraktionsparameter der D-Matrix des Laminats.
     * @return Transversaler Kontraktionsparameter der D-Matrix des Laminats
     */
    public double getNuD() {
        return this.nu_D;
    }

    /**
     * Liefert den Anisotropieparameter gamma_D der D-Matrix des Laminats.
     * @return Anisotropieparameter gamma_D der D-Matrix des Laminats
     */
    public double getGammaD() {
        return this.gamma_D;
    }

    /**
     * Liefert den Anisotropieparameter delta_D der D-Matrix des Laminats.
     * @return Anisotropieparameter delta_D der D-Matrix des Laminats
     */
    public double getDeltaD() {
        return this.delta_D;
    }
    
    /**
     * Liefert die Ingenieurskonstante E<sub>x</sub> für eine Zug-/Druckbelastung. 
     * Die x-Richtung entspricht
     * der 0°-Richtung im globalen Koordinatensystem. Dieser Wert berechnet sich
     * aus 1/( |A<sup>-1</sup>|<sub>11</sub>*t<sub>ges</sub> ). Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return E<sub>x</sub>
     */
    public double getExSimple(){
        return 1.0 / (ABDInv[0][0] * tges);
    }
    
    /**
     * Liefert die Ingenieurskonstante E<sub>y</sub> für eine Zug-/Druckbelastung. Die y-Richtung entspricht
     * der 90°-Richtung im globalen Koordinatensystem. Dieser Wert berechnet sich
     * aus 1/( |A<sup>-1</sup>|<sub>22</sub>*t<sub>ges</sub> ). Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return E<sub>y</sub>
     */
    public double getEySimple(){
        return 1.0 / (ABDInv[1][1] * tges);
    }
    
    /**
     * Liefert die Ingenieurskonstante &nu;<sub>xy</sub>.  Dieser Wert berechnet sich
     * aus -|A<sup>-1</sup>|<sub>12</sub> / |A<sup>-1</sup>|<sub>11</sub>. Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return &nu;<sub>xy</sub>
     */
    public double getNuxySimple(){
        return -ABDInv[0][1]/ABDInv[0][0];
    }
    
    /**
     * Liefert die Ingenieurskonstante &nu;<sub>yx</sub>.  Dieser Wert berechnet sich
     * aus -|A<sup>-1</sup>|<sub>12</sub> / |A<sup>-1</sup>|<sub>22</sub>. Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return &nu;<sub>yx</sub>
     */
    public double getNuyxSimple(){
        return -ABDInv[0][1]/ABDInv[1][1];
    }
    
    /**
     * Liefert die Ingenieurskonstante G (Schubmodul) für eine Zug-/Druckbelastung. Dieser Wert berechnet sich
     * aus 1/( |A<sup>-1</sup>|<sub>66</sub>*t<sub>ges</sub> ). Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return E<sub>y</sub>
     */
    public double getGSimple(){
        return 1.0 / (ABDInv[2][2] * tges);
    }

    /**
     * Liefert die Ingenieurskonstante E<sub>x</sub> für eine Zug-/Druckbelastung.
     * Die x-Richtung entspricht
     * der 0°-Richtung im globalen Koordinatensystem. Dieser Wert berechnet sich
     * aus 1/( |A<sup>-1</sup>|<sub>11</sub>*t<sub>ges</sub> ). Somit ist dieser
     * Wert mit Querkontraktionsbehinderung.
     * @return E<sub>x</sub>
     */
    public double getExFixed(){
        return A[0][0] / tges;
    }

    /**
     * Liefert die Ingenieurskonstante E<sub>y</sub> für eine Zug-/Druckbelastung. Die y-Richtung entspricht
     * der 90°-Richtung im globalen Koordinatensystem. Dieser Wert berechnet sich
     * aus 1/( |A<sup>-1</sup>|<sub>22</sub>*t<sub>ges</sub> ). Somit ist dieser
     * Wert mit Querkontraktionsbehinderung.
     * @return E<sub>y</sub>
     */
    public double getEyFixed(){
        return A[1][1] / tges;
    }

    /**
     * Liefert die Ingenieurskonstante &nu;<sub>xy</sub>.  Dieser Wert berechnet sich
     * aus -|A<sup>-1</sup>|<sub>12</sub> / |A<sup>-1</sup>|<sub>11</sub>. Somit ist dieser
     * Wert mit Querkontraktionsbehinderung.
     * @return &nu;<sub>xy</sub>
     */
    public double getNuxyFixed(){
        return -A[0][0]/A[0][1];
    }

    /**
     * Liefert die Ingenieurskonstante &nu;<sub>yx</sub>.  Dieser Wert berechnet sich
     * aus -|A<sup>-1</sup>|<sub>12</sub> / |A<sup>-1</sup>|<sub>22</sub>. Somit ist dieser
     * Wert mit Querkontraktionsbehinderung.
     * @return &nu;<sub>yx</sub>
     */
    public double getNuyxFixed(){
        return -A[0][0]/A[1][1];
    }

    /**
     * Liefert die Ingenieurskonstante G (Schubmodul) für eine Zug-/Druckbelastung. Dieser Wert berechnet sich
     * aus 1/( |A<sup>-1</sup>|<sub>66</sub>*t<sub>ges</sub> ). Somit ist dieser
     * Wert mit Querkontraktionsbehinderung.
     * @return E<sub>y</sub>
     */
    public double getGFixed(){
        return A[2][2] / tges;
    }

    /**
     * Liefert die Ingenieurskonstante E<sub>x</sub> für eine Biegebelastung.
     * Die x-Richtung entspricht
     * der 0°-Richtung im globalen Koordinatensystem. Dieser Wert berechnet sich
     * aus 12* (D<sup>-1</sup>)<sub>11</sub>/t<sub>ges</sub><sup>3</sup>.
     * @return E<sub>x</sub>
     */
    public double getExBendSimple(){
        return 12.0 / ABDInv[3][3] / tges / tges / tges;  // ohne Querkontraktionsbehinderung
    }

    /**
     * Liefert die Ingenieurskonstante E<sub>y</sub> für eine Biegebelastung.
     * Die y-Richtung entspricht
     * der 90°-Richtung im globalen Koordinatensystem. Dieser Wert berechnet sich
     * aus 12* (D<sup>-1</sup>)<sub>22</sub>/t<sub>ges</sub><sup>3</sup>.
     * @return E<sub>y</sub>
     */
    public double getEyBendSimple(){
        return 12.0 / ABDInv[4][4] / tges / tges / tges;  // ohne Querkontraktionsbehinderung
    }

    /**
     * Liefert die Ingenieurskonstante G (Schubmodul) für eine Biegebelastung.
     * Dieser Wert berechnet sich
     * aus 12* (D<sup>-1</sup>)<sub>66</sub>/t<sub>ges</sub><sup>3</sup>.
     * @return G
     */
    public double getGBendSimple(){
        return 12.0 / ABDInv[5][5] / tges / tges / tges;  // ohne Querkontraktionsbehinderung
    }

    /**
     * Liefert die Ingenieurskonstante &nu;<sub>xy</sub>.  Dieser Wert berechnet sich
     * aus -|A<sup>-1</sup>|<sub>12</sub> / |A<sup>-1</sup>|<sub>11</sub>. Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return &nu;<sub>xy</sub>
     */
    public double getNuxyBendSimple(){
        return -ABDInv[3][4]/ABDInv[3][3];
    }

    /**
     * Liefert die Ingenieurskonstante &nu;<sub>yx</sub>.  Dieser Wert berechnet sich
     * aus -|A<sup>-1</sup>|<sub>12</sub> / |A<sup>-1</sup>|<sub>22</sub>. Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return &nu;<sub>yx</sub>
     */
    public double getNuyxBendSimple(){
        return -ABDInv[3][4]/ABDInv[4][4];
    }
    
    /**
     * Liefert die Ingenieurskonstante E<sub>x</sub> für eine Biegebelastung. 
     * Die x-Richtung entspricht
     * der 0°-Richtung im globalen Koordinatensystem. Dieser Wert berechnet sich
     * aus 12* D<sub>11</sub>/t<sub>ges</sub><sup>3</sup>.
     * @return E<sub>x</sub>
     */
    public double getExBendFixed(){
        return 12.0 * D[0][0] / tges / tges / tges;   // mit Querkontraktionsbehinderung
    }
    
    /**
     * Liefert die Ingenieurskonstante E<sub>y</sub> für eine Biegebelastung. 
     * Die y-Richtung entspricht
     * der 90°-Richtung im globalen Koordinatensystem. Dieser Wert berechnet sich
     * aus 12* D<sub>22</sub>/t<sub>ges</sub><sup>3</sup>.
     * @return E<sub>y</sub>
     */
    public double getEyBendFixed(){
        return 12.0 * D[1][1] / tges / tges / tges;   // mit Querkontraktionsbehinderung
    }
    
    /**
     * Liefert die Ingenieurskonstante G (Schubmodul) für eine Biegebelastung. 
     * Dieser Wert berechnet sich
     * aus 12* D<sub>66</sub>/t<sub>ges</sub><sup>3</sup>.
     * @return G
     */
    public double getGBendFixed(){
        return 12.0 * D[2][2] / tges / tges / tges;   // mit Querkontraktionsbehinderung
    }

    /**
     * Liefert die Ingenieurskonstante &nu;<sub>xy</sub>.  Dieser Wert berechnet sich
     * aus -|A<sup>-1</sup>|<sub>12</sub> / |A<sup>-1</sup>|<sub>11</sub>. Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return &nu;<sub>xy</sub>
     */
    public double getNuxyBendFixed(){
        return -D[0][1]/D[0][0];
    }

    /**
     * Liefert die Ingenieurskonstante &nu;<sub>yx</sub>.  Dieser Wert berechnet sich
     * aus -|A<sup>-1</sup>|<sub>12</sub> / |A<sup>-1</sup>|<sub>22</sub>. Somit ist dieser
     * Wert ohne Querkontraktionsbehinderung.
     * @return &nu;<sub>yx</sub>
     */
    public double getNuyxBendFixed(){
        return -D[0][1]/D[1][1];
    }
    
    /**
     * Liefert die Gesamtdicke t<sub>ges</sub>.
     * @return Gesamtdicke t<sub>ges</sub>
     */
    public double getTges(){
        return tges;
    }
    
    /**
     * Liefert die globalen Wärmeausdehnungskoeffizienten des Laminates zurück.
     * Der Rückgabevektor enthält drei Elemente &alpha;<sub>x</sub>, 
     * &alpha;<sub>y</sub> und &alpha;<sub>xy</sub>.
     * @return Vektor mit den Laminatwärmeausdehnungskoeffizienten
     */
    public double[] getAlphaGlobal(){
        Loads l = new Loads();
        l.setDeltaT(1.0);
        double[] thermForce = CLT_Calculator.getHygroThermalForces(this, l);
        double[] alpha_T = new double[3];
        for(int ii = 0; ii < 3; ii++){
            alpha_T[ii] = 0.0;
            for (int jj = 0; jj < 6; jj++){
                alpha_T[ii] += ABDInv[ii][jj]*thermForce[jj];
            }
        }
        return alpha_T;
    }
    
    /**
     * Liefert die globalen Quelldehnungskoeffizienten des Laminates zurück.
     * Der Rückgabevektor enthält drei Elemente &beta;<sub>x</sub>, 
     * &beta;<sub>y</sub> und &beta;<sub>xy</sub>.
     * @return Vektor mit den Laminatquelldehnungskoeffizienten
     */
    public double[] getBetaGlobal(){
        Loads l = new Loads();
        l.setDeltaH(1.0);
        double[] hygralForce = CLT_Calculator.getHygroThermalForces(this, l);
        double[] beta = new double[3];
        for(int ii = 0; ii < 3; ii++){
            beta[ii] = 0.0;
            for (int jj = 0; jj < 6; jj++){
                beta[ii] += ABDInv[ii][jj]*hygralForce[jj];
            }
        }
        return beta;
    }
    
    /**
     * Gibt die "normalized off-axis flexural moduli" D zurück.
     * nach Tsai SW, Hahn HT. Introduction to composite materials, 
     * Lancaster: Technomic Publishing, 1980; Formel (5.49)
     * @return normalized off-axis flexural moduli
     */
    public double[][] getNormalizedOffAxisFlexuralModuli(){
        
        double[][] Dnorm = new double[3][3];
        
        //(B.1)
        int n = layers.length;
        int nt = n*n*n;
        int halb = (int)Math.floor(n/2) + n%2;
        double ht, htmo;
        
        for (int i = 0; i < 3; i++){
            for (int j = i; j < 3; j++){
                for (int t = 0; t < halb; t++){
                    ht = halb-t;
                    htmo = ht-1.0;
                    Dnorm[i][j] += layers[t].getQMatGlobal()[i][j]*(ht*ht*ht-htmo*htmo*htmo);
                }
                Dnorm[i][j] = 8.0*Dnorm[i][j]/nt;
            }
        }
        
        Dnorm[1][0] = Dnorm[0][1];
        Dnorm[2][0] = Dnorm[0][2];
        Dnorm[2][1] = Dnorm[1][2];
        
        return Dnorm;
    }
    
    public MassMoments getMassMoments(){
        
        if (!isSym){
            return null;
        }
        
        double t, zm;
        double temp;
        double I0 = 0.0;
        double I1 = 0.0;
        double I2 = 0.0;
        CLT_Layer layer;
        
        for (CLT_Layer layer1 : layers) {
            layer = layer1;
            t  = layer.getLayer().getThickness();
            zm = layer.getZm();
            temp = layer.getLayer().getMaterial().getRho()*t;
            // Funktioniert das Ganz nur für Laminate, bei denen ober und unterhalb der Symmetrieebene dieselben Dichten und Dicken der Einzelschichten aufweisen?
            // ist bei Wahl Symmetrie in eLamX ja gegeben
            // die "Mass Moment of inertias" sollten also für symmetrische Laminate anwendbar sein, auch wenn die Schichten nicht dieselbe Dichte und Dicke haben
            I0 += temp;
            I1 += temp * zm;
            I2 += temp * (t*t/12.0 + zm*zm);
        }
        return new MassMoments(I0, I1, I2);
    }
    
    public class MassMoments{
        
        private final double I0;
        private final double I1;
        private final double I2;

        public MassMoments(double I0, double I1, double I2) {
            this.I0 = I0;
            this.I1 = I1;
            this.I2 = I2;
        }

        public double getI0() {
            return I0;
        }

        public double getI1() {
            return I1;
        }

        public double getI2() {
            return I2;
        }
        
    }
    
    public double getAreaWeight(){
        return laminat.getAreaWeight();
    }
}
