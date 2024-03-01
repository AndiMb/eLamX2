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
package de.elamx.clt.plate.Mechanical;

import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_Laminate.MassMoments;
import de.elamx.clt.plate.Boundary.Boundary;
import de.elamx.mathtools.MatrixTools;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Diese Klasse stellt eine Platte für das Beulen einer Rechteckplatte dar. Hier
 * kann die globale Steifigkeitsmatrix gefüllt werden.
 *
 * @author Martin Rädel
 * @author Oliver Hennig
 * @author Andreas Hauffe
 */
public class Plate {

    private double width_ = 0.0; // Breite der Platte (Ausdehnung in x-Richtung)
    private double length_ = 0.0; // Länge der Platte (Ausdehnung in y-Richtung)

    /**
     * Erzeugt ein neues Plattenobjekt mit Länge und Breite.
     *
     * @param length Länge der Platte (Ausdehnung in x-Richtung)
     * @param width Breite der Platte (Ausdehnung in y-Richtung)
     */
    public Plate(double length, double width) {
        length_ = length;
        width_ = width;
    }

    /**
     * Setzen der Länge der Platte (Ausdehnung in x-Richtung).
     *
     * @param length Länge der Platte (Ausdehnung in x-Richtung)
     */
    public void setLength(double length) {
        length_ = length;
    }

    /**
     * Gibt die Länge der Platte (Ausdehnung in x-Richtung) zurück
     *
     * @return Länge der Platte (Ausdehnung in x-Richtung)
     */
    public double getLength() {
        return length_;
    }

    /**
     * Setzen der Breite der Platte (Ausdehnung in x-Richtung).
     *
     * @param width Breite der Platte (Ausdehnung in x-Richtung)
     */
    public void setWidth(double width) {
        width_ = width;
    }

    /**
     * Gibt die Breite der Platte (Ausdehnung in x-Richtung) zurück.
     *
     * @return Breite der Platte (Ausdehnung in x-Richtung)
     */
    public double getWidth() {
        return width_;
    }
    
    /**
     * Fügt die Eigenschaften der Platte der Steifigkeitsmatix basierend auf den
     * Randbedingungen hinzu. Dabei werden die Werte auf die entsprechenden Terme
     * addiert. Deshalb muss die Steifigkeitsmatrix am Anfang null gesetzt werden.
     * @param laminat Laminat der Platte
     * @param kmat Steifigkeitsmatrix (m*n x m*n)
     * @param m Anzahl der Terme für den Ritz-Ansatz in x-Richtung
     * @param n Anzahl der Terme für den Ritz-Ansatz in y-Richtung
     * @param wholeD Flag, ob die D16 und D26 Terme der D-Matrix des Laminates null gesetzt werden sollen
     * @param bx Randbedingungsobjekt in x-Richtung
     * @param by Randbedingungsobjekt in y-Richtung
     */
    public void addStiffness(CLT_Laminate laminat, double[][] kmat, int m, int n, boolean wholeD, Boundary bx, Boundary by){
        this.addStiffness(laminat, kmat, m, n, wholeD, false, bx, by);
    }
    
    /**
     * Fügt die Eigenschaften der Platte der Steifigkeitsmatix basierend auf den
     * Randbedingungen hinzu. Dabei werden die Werte auf die entsprechenden Terme
     * addiert. Deshalb muss die Steifigkeitsmatrix am Anfang null gesetzt werden.
     * @param laminat Laminat der Platte
     * @param kmat Steifigkeitsmatrix (m*n x m*n)
     * @param m Anzahl der Terme für den Ritz-Ansatz in x-Richtung
     * @param n Anzahl der Terme für den Ritz-Ansatz in y-Richtung
     * @param wholeD Flag, ob die D16 und D26 Terme der D-Matrix des Laminates null gesetzt werden sollen
     * @param Dtilde Flag, die D-Matrix durch D-Tilde-Matrix ersetzt werden soll
     * @param bx Randbedingungsobjekt in x-Richtung
     * @param by Randbedingungsobjekt in y-Richtung
     */
    public void addStiffness(CLT_Laminate laminat, double[][] kmat, int m, int n, boolean wholeD, boolean Dtilde, Boundary bx, Boundary by){

        // Hier wird die D-Matrix des Laminates gespeichert.
        double [][] dmattemp = laminat.getDMatrix();

        // Die D-Matrix des Laminates muss umgespeichert werden, um eine Kopie zu erstellen.
        // Tut man das nicht, werden beim Nullsetzen der D16 und D26 Terme, die
        // Original-D-Matrix verändert und andere Module bekommen eine andere
        // D-Matrix.
        double [][] dmat = new double[3][3];
        for (int ii = 0; ii < 3; ii++){
            System.arraycopy(dmattemp[ii], 0, dmat[ii], 0, 3);
        }

        // Nullsetzen der D16 und D26-Terme
        if (!wholeD){
            dmat[0][2] = 0.0;
            dmat[1][2] = 0.0;
            dmat[2][0] = 0.0;
            dmat[2][1] = 0.0;
        }

        // Berechnen von D tilde
        if (Dtilde) {
            double [][] Bmat       = laminat.getBMatrix();
            double [][] BmatTransp = MatrixTools.MatTransp(Bmat);
            double [][] AmatInv    = laminat.getaMatrix();

            double [][] helpMat1    = MatrixTools.MatMult(BmatTransp, AmatInv);
            double [][] helpMat2    = MatrixTools.MatMult(helpMat1, Bmat);
           
            for (int ii = 0; ii < dmat.length; ii++) {
                for (int jj = 0; jj < dmat[0].length; jj++) {
                    dmat[ii][jj] -= helpMat2[ii][jj];
                }
            }
        }

        int k = -1; // Laufvariable (1. Index) für die Steifigkeitsmatrix
        int l = -1; // Laufvariable (2. Index) für die Steifigkeitsmatrix
        for (int pp = 0; pp < m; pp++){
            for (int qq = 0; qq < n; qq++){
                k++;
                l=-1;
                for (int ii = 0; ii < m; ii++){
                    for (int jj = 0; jj < n; jj++){                        
                        l++;
                        kmat[k][l] += ( dmat[0][0]*(bx.IdX2dX2(ii,pp) * by.IXX(jj,qq))
                                    +   dmat[0][1]*(bx.IXdX2(ii,pp)   * by.IXdX2(qq,jj)   + bx.IXdX2(pp,ii)  * by.IXdX2(jj,qq))
                                    + 2*dmat[0][2]*(bx.IdXdX2(ii,pp)  * by.IXdX(qq,jj)    + bx.IdXdX2(pp,ii) * by.IXdX(jj,qq))
                                    +   dmat[1][1]*(bx.IXX(ii,pp)     * by.IdX2dX2(jj,qq))
                                    + 2*dmat[1][2]*(bx.IXdX(pp,ii)    * by.IdXdX2(jj,qq)  + bx.IXdX(ii,pp)   * by.IdXdX2(qq,jj))
                                    + 4*dmat[2][2]*(bx.IdXdX(ii,pp)   * by.IdXdX(jj,qq)));
                    }
                }
            }
        }
    }
    
    /**
     * Fügt die Eigenschaften der Platte der Steifigkeitsmatix basierend auf den
     * Randbedingungen hinzu. Dabei werden die Werte auf die entsprechenden Terme
     * addiert. Deshalb muss die Steifigkeitsmatrix am Anfang null gesetzt werden.
     * @param laminat Laminat der Platte
     * @param mmat Massenmatrix (m*n x m*n)
     * @param m Anzahl der Terme für den Ritz-Ansatz in x-Richtung
     * @param n Anzahl der Terme für den Ritz-Ansatz in y-Richtung
     * @param bx Randbedingungsobjekt in x-Richtung
     * @param by Randbedingungsobjekt in y-Richtung
     */
    public void addMass(CLT_Laminate laminat, double[][] mmat, int m, int n, Boundary bx, Boundary by){
        
        MassMoments mm = laminat.getMassMoments();
        
        if (mm == null){
            return;
        }
        
        double I0 = mm.getI0();
        double I2 = mm.getI2();
        
        int k = -1; // Laufvariable (1. Index) für die Steifigkeitsmatrix
        int l = -1; // Laufvariable (2. Index) für die Steifigkeitsmatrix
        for (int pp = 0; pp < m; pp++){
            for (int qq = 0; qq < n; qq++){
                k++;
                l=-1;
                for (int ii = 0; ii < m; ii++){
                    for (int jj = 0; jj < n; jj++){                        
                        l++;
                        mmat[k][l] -= (I0*bx.IXX(ii,pp)*by.IXX(jj,qq)
                                    +  I2*(bx.IdXdX(ii,pp)*by.IXX(jj,qq) + bx.IXX(ii,pp)*by.IdXdX(jj,qq)));
                    }
                }
            }
        }
    }
    
    public void addStiffnessAndMass(CLT_Laminate laminat, double[][] kmat, double[][] mmat, int m, int n, boolean wholeD, Boundary bx, Boundary by){
        this.addStiffnessAndMass(laminat, kmat, mmat, m, n, wholeD, false, bx, by);
    }
    
    public void addStiffnessAndMass(CLT_Laminate laminat, double[][] kmat, double[][] mmat, int m, int n, boolean wholeD, boolean Dtilde, Boundary bx, Boundary by){
        
        addMass(laminat, mmat, m, n, bx, by);
        addStiffness(laminat, kmat, m, n, wholeD, Dtilde, bx, by);
        
    }
}
