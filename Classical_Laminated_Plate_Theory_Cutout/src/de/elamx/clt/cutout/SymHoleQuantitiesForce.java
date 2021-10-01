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

import de.elamx.clt.CLT_Laminate;
import de.elamx.mathtools.Complex;
import de.elamx.mathtools.Polynom;

/**
 *
 * @author Sommerfeld, Franziska
 * @author raedel
 */
public class SymHoleQuantitiesForce extends HoleQuantities {
    
    public SymHoleQuantitiesForce(CLT_Laminate lam, double[] load){
        super(lam,load);
        
        s = new Complex[2];
    }
    
    /**
     * Methode berechnet Werte zur Berechnung der Schnittkräfte an einer Lochkontur
     * in einem symmetrischen Laminat nach folgender Theorie:
     * [1] Ukadgaonker; Rao: A general Solution for stresses around holes in symmetric laminates under inplane loading
     * 
     */
    @Override
    protected void calc(){
        
        double B1, B2, C1;
        
        double[]   G = new double[3];
        double[][] ad = new double[3][3];
        double[][] bd = new double[3][3];
        
        Complex[] K = new Complex[4];
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte a
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //(A.1)
        for (int ii = 0; ii < 3; ii++) {
            for (int jj = 0; jj < 3; jj++) {
                bd[ii][jj] = lam.getAMatrix()[ii][jj]/lam.getTges();
            }
        }
        
        //(A.2)
        double B =   bd[0][0]*bd[1][1]*bd[2][2] - bd[0][0]*bd[1][2]*bd[1][2] + 
                   2*bd[0][1]*bd[1][2]*bd[0][2] - bd[2][2]*bd[0][1]*bd[0][1] -
                     bd[1][1]*bd[0][2]*bd[0][2];

        ad[0][0] = (bd[1][1]*bd[2][2] - bd[1][2]*bd[1][2])/B;
        ad[0][1] = (bd[0][2]*bd[1][2] - bd[0][1]*bd[2][2])/B;
        ad[0][2] = (bd[0][1]*bd[1][2] - bd[0][2]*bd[1][1])/B;
        ad[1][1] = (bd[0][0]*bd[2][2] - bd[0][2]*bd[0][2])/B;
        ad[1][2] = (bd[0][1]*bd[0][2] - bd[0][0]*bd[1][2])/B;
        ad[2][2] = (bd[0][0]*bd[1][1] - bd[0][1]*bd[0][1])/B;
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte s
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //(12) zum finden des größten Q
        double[] arr5 = new double[5];
        arr5[4] =    ad[0][0];
        arr5[3] = -2*ad[0][2];
        arr5[2] =  2*ad[0][1] + ad[2][2];
        arr5[1] = -2*ad[1][2];
        arr5[0] =    ad[1][1];
        
        Polynom poly = new Polynom(arr5);
        poly.calcRoots();
        Complex[] roots = poly.getRoots();
        
        //ohne konjugiert Komplexe
        for (int i = 0; i < 2; i++){s[i] = roots[i*2];}
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Konstanten zur Berechnung der Schnittkräfte um eine Lochkontur im Symmetrischen Laminat.
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        for (int i = 0; i < 3; i++){G[i] = load[i]/lam.getTges();}
        
        //Die Konstanten werden berechnet nach [1]: [1](7) bzw. [2](26)
        calcab(2);
        
        //[1](25)
        double r1 = s[0].getRe();
        double r2 = s[1].getRe();
        double i1 = s[0].getIm();
        double i2 = s[1].getIm();
        
        double vald1 = 2*((r2-r1)*(r2-r1) + i2*i2 - i1*i1);
        double vald2 =    r1*r1 -  i1*i1;
        double vald3 =    r2*r2 -  i2*i2;
        
        //B* = B1; B'* = B2; C'* = C1
        B1 = (G[0] + (r2*r2 + i2*i2)*G[1] + 2*r2*G[2])/vald1;
        B2 = ( - G[0] + (vald2 - 2*r1*r2)*G[1] - 2*r2*G[2])/vald1;
        C1 = ((r1 -r2)*G[0] + (r2*vald2 - r1*vald3)*G[1] + (vald2 - vald3)*G[2])/(i2*vald1);
        
        //[1](27)
        Complex valc = new Complex(0,1).multiply(C1).add(B2);
        K[0] = Complex.addmultiple(a[0].multiply(B1), a[1].multiply(valc)).multiply(0.5);
        K[1] = Complex.addmultiple(b[0].multiply(B1), b[1].multiply(valc)).multiply(0.5);
        K[2] = Complex.addmultiple(a[0].multiply(s[0]).multiply(B1), a[1].multiply(s[1]).multiply(valc)).multiply(0.5);
        K[3] = Complex.addmultiple(b[0].multiply(s[0]).multiply(B1), b[1].multiply(s[1]).multiply(valc)).multiply(0.5);
        
        Complex[] Kkonj = new Complex[K.length];
        for (int ii = 0; ii < Kkonj.length; ii++){Kkonj[ii] = K[ii].getConjugate();}
        
        //[1](31)
        Complex tmp = s[0].subtract(s[1]);
        
        a[2] = (s[1].multiply(K[0].add(Kkonj[1])).subtract(K[2].add(Kkonj[3]))).divide(tmp);
        b[2] = (s[1].multiply(K[1].add(Kkonj[0])).subtract(K[3].add(Kkonj[2]))).divide(tmp);
        a[3] = (s[0].multiply(K[0].add(Kkonj[1])).subtract(K[2].add(Kkonj[3]))).divide(tmp).getNegative();
        b[3] = (s[0].multiply(K[1].add(Kkonj[0])).subtract(K[3].add(Kkonj[2]))).divide(tmp).getNegative();
    }
    
}
