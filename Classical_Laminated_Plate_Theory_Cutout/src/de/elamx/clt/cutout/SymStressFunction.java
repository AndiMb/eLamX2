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
package de.elamx.clt.cutout;

import de.elamx.mathtools.Complex;

/**
 * Diese Klasse repräsentiert die Stress Functions die zur Berechnung der 
 * Schnittkärfte und -momente um eine Lochkontur bei Symmetrischen 
 * Laminat gebraucht werden.
 * 
 * Verwendet wird folgende Theorie:
 * [1] Ukadgaonker; Rao: A general Solution for stresses around holes in symmetric laminates under inplane loading
 * [2] Ukadgaonker; Rao: A general Solution for moments around holes in symmetric laminates
 * 
 * @author Franziska Sommerfeld
 * @author raedel
 */
public class SymStressFunction {
    
    private final HoleQuantities q;
    private final CutoutGeometry h;
    
    /**
     * Konstruktor der Stress Functions. Übergeben werden die Konstanten 
     * entsprechend der Theorie [1] oder [2], der Ausschnitt und der Winkel.
     * @param q Konstanten der Lochgeometrie
     * @param hole Ausschnitt
     */
    public SymStressFunction(HoleQuantities q, CutoutGeometry hole){
        this.q = q;
        this.h = hole;
    }
    
    public Complex[] calc(double theta){
        
        double rad = theta*Math.PI/180.0;
        
        double[]  m = h.getConstants();
        Complex[] a = q.geta();
        Complex[] b = q.getb();
        
        //[1](2) bzw. [2](21)
        Complex zeta = new Complex(Math.cos(rad), Math.sin(rad));
                
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // W
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //[1](6) bzw. [2](25) abgeleitet nach zeta
        //Summenteile
        Complex summe1 = new Complex();
        Complex summe2 = new Complex();
        for (int ii = 1; ii < m.length; ii++){
            if (m[ii] != 0){
                summe1 = summe1.add(Complex.pow(zeta, ii-1).multiply(ii*m[ii]));
                summe2 = summe2.add((new Complex(-ii*m[ii])).divide(Complex.pow(zeta, ii+1)));
            }
        }
        //gesamte Gleichung
        Complex[] w = new Complex[2];
        for (int ii = 0; ii < w.length; ii++){
            w[ii] = Complex.addmultiple(a[ii].multiply(Complex.pow(zeta, -2).getNegative().add(summe1)), b[ii].multiply(summe2.add(1.0))).multiply(0.5);
        }
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Phi, Psi
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //[1](30) (aber psi positiv, da a4 und b4 bereits negiert)  bzw. [2](60) abgeleitet nach zeta
        Complex[] phipsi = new Complex[2];
        //Summenteil
        Complex summe = new Complex();
        for (int ii = 1; ii < m.length; ii++){
            if (m[ii] != 0){
                summe = summe.add(new Complex(ii*m[ii]).divide(Complex.pow(zeta,ii+1)));
            }
        }
        //gesamte Gleichung
        for (int ii = 0; ii < 2; ii++){
            phipsi[ii] = a[ii+2].getNegative().divide(Complex.pow(zeta, 2)).subtract(b[ii+2].multiply(summe));
        }
        
        //[1](33) bzw. [2](63)
        for (int ii = 0; ii < phipsi.length; ii++){
            phipsi[ii] = phipsi[ii].divide(w[ii]);
        }
        
        return phipsi;
    }

}