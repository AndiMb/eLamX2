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

/**
 *
 * @author raedel
 */
public class Cutout {
    
    private static int I_WERTE = 721;                                           // Anzahl Werte über Lochwinkel
    
    public static int getNumWerte(){return I_WERTE;}
    
    public static CutoutResult calc(CLT_Laminate laminat, CutoutInput input) {
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Anzahl Werte über 360°-Winkel
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        I_WERTE = input.getValues();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Konstanten
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        input.getCutoutGeometry().calcConstants();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Berechnung
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        if (laminat.isSymmetric()){return calcSymmetric  (laminat,input);}
        else                      {return calcUnsymmetric(laminat,input);}
    }
        
    private static CutoutResult calcSymmetric(CLT_Laminate laminat, CutoutInput input){
        
        double[] loads = input.getLoads();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte a,b,p,q,s des Lochs fuer Kraft und Momente
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        SymHoleQuantitiesForce  hqf = new SymHoleQuantitiesForce( laminat, loads);
        SymHoleQuantitiesMoment hqm = new SymHoleQuantitiesMoment(laminat, loads);
        
        hqf.calc();
        hqm.calc();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Stress Functions
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //Stressfunctions Phi, Psi
        SymStressFunction funcN = new SymStressFunction(hqf, input.getCutoutGeometry());
        SymStressFunction funcM = new SymStressFunction(hqm, input.getCutoutGeometry());
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // N's und M's berechnen
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        Complex[] s = hqf.gets();
        Complex[] p = hqm.getp();
        Complex[] q = hqm.getq();
        
        double angle, alpha, sar, car;
        double h      = laminat.getTges();
        double vald   = h*h*h/6.0;
        double deltaW = 360.0/(I_WERTE-1);
        double[][] NM = new double[9][I_WERTE];
        Complex[] phipsiN, phipsiM;
        
        for (int ii = 0; ii <= I_WERTE-1; ii++){
            
            angle   = ii*deltaW;
            phipsiN = funcN.calc(angle);
            phipsiM = funcM.calc(angle);
            
            alpha = input.getCutoutGeometry().getAlpha(angle);
            sar   = Math.sin(Math.toRadians(alpha));
            car   = Math.cos(Math.toRadians(alpha));
            
            NM[0][ii] = alpha;                                                                                                                              //Alpha
            NM[1][ii] = loads[0] + (2.0*Complex.addmultiple(Complex.pow(s[0],2).multiply(phipsiN[0]), Complex.pow(s[1],2).multiply(phipsiN[1])).getRe())*h; //Nx
            NM[2][ii] = loads[1] + (2.0*Complex.addmultiple(phipsiN[0], phipsiN[1]).getRe())*h;                                                             //Ny
            NM[3][ii] = loads[2] - (2.0*Complex.addmultiple(s[0].multiply(phipsiN[0]), s[1].multiply(phipsiN[1])).getRe())*h;                               //Nxy
            NM[4][ii] = loads[3] - (p[0].multiply(phipsiM[0]).add(q[0].multiply(phipsiM[1]))).getRe()*vald;                                                 //Mx
            NM[5][ii] = loads[4] - (p[1].multiply(phipsiM[0]).add(q[1].multiply(phipsiM[1]))).getRe()*vald;                                                 //My
            NM[6][ii] = loads[5] - (p[2].multiply(phipsiM[0]).add(q[2].multiply(phipsiM[1]))).getRe()*vald;                                                 //Mxy
            NM[7][ii] = NM[1][ii]*sar*sar+NM[2][ii]*car*car-2.0*NM[3][ii]*sar*car;                                                                          //Ntheta (N in Richtung Alpha zum Vergleich)
            NM[8][ii] = NM[4][ii]*sar*sar+NM[5][ii]*car*car-2.0*NM[6][ii]*sar*car;                                                                          //Mtheta (M in Richtung Alpha zum Vergleich)
        }
        
        return new CutoutResult(laminat, input, NM);
    }
    
    private static CutoutResult calcUnsymmetric(CLT_Laminate laminat, CutoutInput input){
        
        double[] loads = input.getLoads();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Werte a,b,p,q,s des Lochs fuer Kraft und Momente
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        UnsymHoleQuantities  hq = new UnsymHoleQuantities(laminat);
        hq.calc();
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Potentiale
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //Stressfunctions Phi, Psi
        UnsymPotentials pots = new UnsymPotentials(hq, input.getCutoutGeometry(), loads);

        //9. N, M berechnen
        double deltaW = 360.0/(I_WERTE-1);
        double[][] NM = new double[9][I_WERTE];
        double angle, alpha, sar, car;
        
        //(16)
        for (int ii = 0; ii <= I_WERTE -1; ii++){
            angle = ii*deltaW;
            
            alpha = input.getCutoutGeometry().getAlpha(angle);
            sar   = Math.sin(Math.toRadians(alpha));
            car   = Math.cos(Math.toRadians(alpha));
            
            NM[0][ii] = alpha;
            for (int jj = 0; jj < 4; jj++){
                NM[1][ii] += hq.getc()[jj].multiply(pots.getPotential()[jj][ii]).getRe()*2.0;  //Nx
                NM[2][ii] += hq.getd()[jj].multiply(pots.getPotential()[jj][ii]).getRe()*2.0;  //Ny
                NM[3][ii] += hq.gete()[jj].multiply(pots.getPotential()[jj][ii]).getRe()*2.0;  //Nxy
                NM[4][ii] += hq.getf()[jj].multiply(pots.getPotential()[jj][ii]).getRe()*2.0;  //Mx
                NM[5][ii] += hq.getg()[jj].multiply(pots.getPotential()[jj][ii]).getRe()*2.0;  //My
                NM[6][ii] += hq.geth()[jj].multiply(pots.getPotential()[jj][ii]).getRe()*2.0;  //Mxy
            }

            NM[7][ii] = NM[1][ii]*sar*sar + NM[2][ii]*car*car - 2*NM[3][ii]*sar*car;  //Ntheta (N in Richtung Alpha zum Vergleich)
            NM[8][ii] = NM[4][ii]*sar*sar + NM[5][ii]*car*car - 2*NM[6][ii]*sar*car;  //Mtheta (M in Richtung Alpha zum Vergleich)
        }
        
        return new CutoutResult(laminat, input, NM);
    }
    
    private void resultantsToString(double[][] nm){
        for (int ii = 0; ii < nm[0].length; ii++){
            for (int jj = 0; jj < 8; jj++){
                System.out.print(String.format("%23.16e", nm[jj][ii]));
            }
            System.out.print("\n");
        }
    }
    
}
