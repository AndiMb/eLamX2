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
public class SymHoleQuantitiesMoment extends HoleQuantities {
    
    public SymHoleQuantitiesMoment(CLT_Laminate lam, double[] load){
        super(lam,load);
        
        s = new Complex[2];
        p = new Complex[3];
        q = new Complex[3];
    }
    
    /**
     * Methode berechnet Werte zur Berechnung der Schnittmomente 
     * an einer Lochkontur in einem Symmetrischen Laminat nach folgender Theorie:
     * [2] Ukadgaonker; Rao: A general Solution for moments around holes in symmetric laminates
     */
    @Override
    protected void calc(){
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Gibt die "normalized off-axis flexural moduli" D zurück.
        // nach Tsai SW, Hahn HT. Introduction to composite materials, 
        // Lancaster: Technomic Publishing, 1980; Formel (5.49)
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        double[][] Dnorm = lam.getNormalizedOffAxisFlexuralModuli();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte s,p,q
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //(14) zum finden des größten Q
        double[] arr5 = new double[5];
        arr5[4] =    Dnorm[1][1];
        arr5[3] = 4* Dnorm[0][2];
        arr5[2] = 2*(Dnorm[0][1] + 2*Dnorm[2][2]);
        arr5[1] = 4* Dnorm[0][2];
        arr5[0] =    Dnorm[0][0];

        Polynom poly = new Polynom(arr5);
        poly.calcRoots();
        Complex[] roots = poly.getRoots();
        
        //ohne konjugiert Komplexe
        for (int i = 0; i < 2; i++){s[i] = roots[i*2];}
        
        //(19)
        for (int i = 0; i < 3; i++){
            p[i] = Complex.addmultiple(new Complex(Dnorm[i][0]), Complex.pow(s[0], 2).multiply(Dnorm[i][1]), s[0].multiply(2*Dnorm[i][2]));
            q[i] = Complex.addmultiple(new Complex(Dnorm[i][0]), Complex.pow(s[1], 2).multiply(Dnorm[i][1]), s[1].multiply(2*Dnorm[i][2]));
        }
        
        calcab(2);
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // 
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //[2](A.2)
        double[] ad = new double[3];
        double[] bd = new double[3];
        double[] cd = new double[3];
        
        double tmp1 = Math.pow(s[0].getRe(),2) - Math.pow(s[0].getIm(),2);
        double tmp2 = Math.pow(s[1].getRe(),2) - Math.pow(s[1].getIm(),2);
        
        for (int i = 0; i < 3; i++){
            ad[i] = Dnorm[0][i] + Dnorm[1][i]*tmp1 + 2*Dnorm[2][i]*s[0].getRe();
            bd[i] = Dnorm[0][i] + Dnorm[1][i]*tmp2 + 2*Dnorm[2][i]*s[1].getRe();
            cd[i] = 2*Dnorm[1][i]*s[1].getRe()*s[1].getIm() + 2*Dnorm[2][i]*s[1].getIm() ;
        }
        
        double vald = -6.0/Math.pow(lam.getTges(),3);
        double X = vald*load[3];
        double Y = vald*load[4];
        double Z = vald*load[5];
        
        double[] T = new double[10];
        T[0] =     X*cd[1] -     Y*cd[0];
        T[1] = bd[0]*cd[2] - cd[0]*bd[2];
        T[2] =     X*cd[2] -     Z*cd[0];
        T[3] = bd[0]*cd[1] - cd[0]*bd[1];
        T[4] = ad[0]*cd[1] - cd[0]*ad[1];
        T[5] = ad[0]*cd[2] - cd[0]*ad[2];
        T[6] =     X/cd[0];
        T[7] =   T[0]*T[1] -  T[2]*T[3];
        T[8] =   T[2]*T[4] -  T[0]*T[5];
        T[9] =   T[1]*T[4] -  T[3]*T[5];
        
        //[2](A.1) B* = B1; B'* = B2; C'* = C1
        double B1, B2, C1;
        B1 = T[7]/T[9];
        B2 = T[8]/T[9];
        C1 = (ad[0]*T[7] + bd[0]*T[8])/(cd[0]*T[9]) - T[6];
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // 
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        Complex valc1, valc2, valc3;
        
        //[2](57)
        valc1 = new Complex(0,1).multiply(C1).add(B2);
        Complex[] K = new Complex[4];
        K[0] = Complex.addmultiple(a[0].multiply(   B1).multiply(p[0]).divide(s[0]), a[1].multiply(valc1).multiply(q[0]).divide(s[1])).multiply(0.5);
        K[1] = Complex.addmultiple(b[0].multiply(   B1).multiply(p[0]).divide(s[0]), b[1].multiply(valc1).multiply(q[0]).divide(s[1])).multiply(0.5);
        K[2] = Complex.addmultiple(a[0].multiply(p[1]).multiply(B1), a[1].multiply(q[1]).multiply(valc1)).multiply(0.5);
        K[3] = Complex.addmultiple(b[0].multiply(p[1]).multiply(B1), b[1].multiply(q[1]).multiply(valc1)).multiply(0.5);
        
        Complex[] Kkonj = new Complex[K.length];
        for (int ii = 0; ii < Kkonj.length; ii++){Kkonj[ii] = K[ii].getConjugate();}
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // 
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //[2](61)
        valc1 = p[0].multiply(q[1]).multiply(s[1]).subtract(p[1].multiply(q[0]).multiply(s[0]));
        valc2 = s[0].divide(valc1);
        valc3 = s[1].divide(valc1).getNegative();
        
        a[2] = (q[0].multiply(K[2].add(Kkonj[3])).subtract(q[1].multiply(s[1]).multiply(K[0].add(Kkonj[1])))).multiply(valc2);
        b[2] = (q[0].multiply(K[3].add(Kkonj[2])).subtract(q[1].multiply(s[1]).multiply(K[1].add(Kkonj[0])))).multiply(valc2);
        a[3] = (p[0].multiply(K[2].add(Kkonj[3])).subtract(p[1].multiply(s[0]).multiply(K[0].add(Kkonj[1])))).multiply(valc3);
        b[3] = (p[0].multiply(K[3].add(Kkonj[2])).subtract(p[1].multiply(s[0]).multiply(K[1].add(Kkonj[0])))).multiply(valc3);
    }
    
}
