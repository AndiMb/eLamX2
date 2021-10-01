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
import de.elamx.mathtools.QuadraticComplexMatrix;

/**
 *
 * @author Sommerfeld, Franziska
 * @author raedel
 */
public class UnsymHoleQuantities extends HoleQuantities{
    
    public UnsymHoleQuantities(CLT_Laminate lam){
        super(lam,null);
        
        s = new Complex[4];
        p = new Complex[4];
        q = new Complex[4];
    }
    
    /**
     * Methode berechnet Werte zur Berechnung der Schnittkräfte und -momente an 
     * einer Lochkontur in einem Unsymmetrischen Laminat nach folgender Theorie:
     * in einem symmetrischen Laminat nach folgender Theorie:
    * [1] Ukadgaonker; Rao: A general Solution for stress resultants and moments around holes in unsymmetric laminates
    */
    @Override
    protected void calc(){
        
        double[][] A = lam.getAMatrix();
        double[][] B = lam.getBMatrix();
        double[][] D = lam.getDMatrix();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // RSTQ
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        double[] R = new double[6];
        double[] S = new double[6];
        double[] T = new double[5];
        double[] Q = new double[9];
        
        //(A.3)
        R[0] =    A[2][2]*B[0][0] -   A[0][2]*B[0][2];
        R[1] =  2*A[2][2]*B[0][2] + 2*A[1][2]*B[0][0] -   A[0][2]*B[0][1] 
               -2*A[0][2]*B[2][2] -   A[0][1]*B[0][2];
        R[2] =    A[1][1]*B[0][0] + 5*A[1][2]*B[0][2] - 3*A[0][2]*B[1][2] 
               -  A[0][1]*B[0][1] - 2*A[0][1]*B[2][2];
        R[3] = -2*A[2][2]*B[1][2] +   A[1][2]*B[0][1] + 2*A[1][2]*B[2][2] 
               +3*A[1][1]*B[0][2] -   A[0][2]*B[1][1] - 3*A[0][1]*B[1][2];
        R[4] = -  A[1][2]*B[1][2] +   A[1][1]*B[0][1] + 2*A[1][1]*B[2][2] 
               -  A[0][1]*B[1][1] -   A[2][2]*B[1][1];
        R[5] =    A[1][1]*B[1][2] -   A[1][2]*B[1][1];
        
        S[0] =    A[0][0]*B[0][2] -   A[0][2]*B[0][0];
        S[1] =    A[0][0]*B[0][1] + 2*A[0][0]*B[2][2] -   A[0][2]*B[0][2] 
               -  A[0][1]*B[0][0] -   A[2][2]*B[0][0];
        S[2] =  3*A[0][0]*B[1][2] +   A[0][2]*B[0][1] + 2*A[0][2]*B[2][2] 
               -2*A[2][2]*B[0][2] - 3*A[0][1]*B[0][2] -   A[1][2]*B[0][0];
        S[3] =    A[0][0]*B[1][1] + 5*A[0][2]*B[1][2] -   A[0][1]*B[0][1] 
               -2*A[0][1]*B[2][2] - 3*A[1][2]*B[0][2];
        S[4] =  2*A[0][2]*B[1][1] + 2*A[2][2]*B[1][2] -   A[0][1]*B[1][2] 
               -  A[1][2]*B[0][1] - 2*A[1][2]*B[2][2];
        S[5] =    A[2][2]*B[1][1] -   A[1][2]*B[1][2];
        
        T[0] =    A[0][0]*A[2][2] -   A[0][2]*A[0][2];
        T[1] =  2*A[0][0]*A[1][2] - 2*A[0][1]*A[0][2];
        T[2] =  2*A[0][2]*A[1][2] +   A[0][0]*A[1][1] -   A[0][1]*A[0][1] 
               -2*A[0][1]*A[2][2];
        T[3] =  2*A[0][2]*A[1][1] - 2*A[0][1]*A[1][2];
        T[4] =    A[1][1]*A[2][2] -   A[1][2]*A[1][2];
        
        //(A.2)
        Q[0] =   B[0][0]*R[0]+  B[0][2]*S[0]-D[0][0]*T[0];
        Q[1] =   B[0][0]*R[1]+3*B[0][2]*R[0]+B[0][2]*S[1]+(B[0][1]+2*B[2][2])*S[0]
               - D[0][0]*T[1]-4*D[0][2]*T[0];
        Q[2] =   B[0][0]*R[2]+3*B[0][2]*R[1]+(B[0][1] + 2*B[2][2])*R[0] 
                + B[0][2]*S[2] + (B[0][1] + 2*B[2][2])*S[1] + 3*B[1][2]*S[0] 
                - D[0][0]*T[2] - 4*D[0][2]*T[1] - (2*D[0][1] + 4*D[2][2])*T[0];
        Q[3] = B[0][0]*R[3] + 3*B[0][2]*R[2] + (B[0][1] + 2*B[2][2])*R[1] 
                + B[1][2]*R[0] + B[0][2]*S[3] + (B[0][1] + 2*B[2][2])*S[2] 
                + 3*B[1][2]*S[1] + B[1][1]*S[0] - D[0][0]*T[3] - 4*D[0][2]*T[2] 
                - (2*D[0][1] + 4*D[2][2])*T[1] - 4*D[1][2]*T[0];
        Q[4] = B[0][0]*R[4] + 3*B[0][2]*R[3] + (B[0][1] + 2*B[2][2])*R[2] 
                + B[1][2]*R[1] + B[0][2]*S[4] + (B[0][1] + 2*B[2][2])*S[3] 
                + 3*B[1][2]*S[2] + B[1][1]*S[1] - D[0][0]*T[4] - 4*D[0][2]*T[3] 
                - (2*D[0][1] + 4*D[2][2])*T[2] - 4*D[1][2]*T[1] - D[1][1]*T[0];
        Q[5] = B[0][0]*R[5] + 3*B[0][2]*R[4] + (B[0][1] + 2*B[2][2])*R[3] 
                + B[1][2]*R[2] + B[0][2]*S[5] + (B[0][1] + 2*B[2][2])*S[4] 
                + 3*B[1][2]*S[3] + B[1][1]*S[2] - 4*D[0][2]*T[4] - (2*D[0][1] 
                + 4*D[2][2])*T[3] - 4*D[1][2]*T[2] - D[1][1]*T[1];
        Q[6] = 3*B[0][2]*R[5] + (B[0][1] + 2*B[2][2])*R[4] + B[1][2]*R[3] 
                + (B[0][1] + 2*B[2][2])*S[5] + 3*B[1][2]*S[4] + B[1][1]*S[3] 
                - (2*D[0][1] + 4*D[2][2])*T[4] - 4*D[1][2]*T[3] - D[1][1]*T[2];
        Q[7] = (B[0][1] + 2*B[2][2])*R[5] + B[1][2]*R[4] + 3*B[1][2]*S[5] 
                + B[1][1]*S[4] - 4*D[1][2]*T[4] - D[1][1]*T[3];
        Q[8] = B[1][2]*R[5] + B[1][1]*S[5] - D[1][1]*T[4];
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte s
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //tmp1 zum finden des größten Q
        double[] arr9 = new double[9];
        System.arraycopy(Q, 0, arr9, 0, 9);
        java.util.Arrays.sort(arr9);
        
        //tmp2 zum anpassen von Q, damit Koeffizienten des Polynoms nicht zu groß
        double[] tmp2 = new double[9];
        for (int i = 0; i < 9; i++){tmp2[i]= Q[i]/arr9[0];}
        
        //(A.1)
        Polynom poly = new Polynom(tmp2);
        poly.calcRoots();
        Complex[] roots = poly.getRoots();
        
        //tmp3 ohne konjugiert Komplexe
        for (int i = 0; i < 4; i++){s[i] = roots[i*2];}
        
        //damit letztes s möglichst keinen Realteil besitzt
        java.util.Arrays.sort(s);
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte a,b
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //(7)
        calcab(4);
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte p,q
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //(A.4)
        for(int ii = 0; ii < 4; ii++){
            
            Complex valc1 = new Complex();                                      //oberer Teil der Gleichung für p (Divident)
            Complex valc2 = new Complex();                                      //oberer Teil der Gleichung für q (Divident)
            Complex valc3 = new Complex();                                      //unterer Teil der Gleichung (Divisor)
            
            for (int jj = 0; jj<6; jj++){
                valc1           = valc1.add(Complex.multiplymultiple(new Complex(R[jj]), Complex.pow(s[ii],jj)));
                valc2           = valc2.add(Complex.multiplymultiple(new Complex(S[jj]), Complex.pow(s[ii],jj)));
                if (jj<5){valc3 = valc3.add(Complex.multiplymultiple(new Complex(T[jj]), Complex.pow(s[ii],jj)));}
            }
            
            p[ii] = valc1.divide(valc3);
            q[ii] = valc2.divide(valc3);
        }
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte c,d,e,f,g,h
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //(17)
        QuadraticComplexMatrix m = new QuadraticComplexMatrix(lam.getABDMatrix());   //ABD-Matrix
        Complex[] v = new Complex[6];                                           //rechter Vektor der Gleichung
        Complex[] cde;
        
        for (int ii = 0; ii < 4; ii++){
            v[0] = p[ii];
            v[1] = q[ii].multiply(s[ii]);
            v[2] = p[ii].multiply(s[ii]).add(q[ii]);
            v[3] = new Complex(-1);
            v[4] = Complex.pow(s[ii], 2).getNegative();
            v[5] = s[ii].multiply(-2.0);
            
            cde = m.multiplyComplexVektor(v);
            
            c[ii] = cde[0];
            d[ii] = cde[1];
            e[ii] = cde[2];
            f[ii] = cde[3];
            g[ii] = cde[4];
            h[ii] = cde[5];
        }
        
    }
    
}
