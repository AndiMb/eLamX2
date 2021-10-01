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
import de.elamx.mathtools.QuadraticComplexMatrix;
import de.elamx.mathtools.QuadraticMatrix;

/**
 *
 * @author Sommerfeld, Franziska
 * @author raedel
 */
public class UnsymPotentials {
    
    private final Complex[] aunsym = new Complex[4];
    private final Complex[] bunsym = new Complex[4];
    
    private final HoleQuantities q;
    private final CutoutGeometry h;
    private final double[]       l;
    
    private Complex[][] pot;
    
    /**
     * Konstruktor des First Stage Potential aus folgenderder Theorie:
     * [1] Ukadgaonker; Rao: A general Solution for stress resultants and moments around holes in unsymmetric laminates
     * 
     * @param q
     * @param hole
     * @param load
     */
    public UnsymPotentials(HoleQuantities q, CutoutGeometry hole, double[] load){
        this.q = q;
        this.h = hole;
        this.l = load;
        calc();
    }
    
    public Complex[][] getPotential(){
        return pot;
    }
    
    private void calc(){
        
        int vali;
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // First Stage Potential
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        Complex[] fsp = new Complex[4];                                         // First Stage Potential
        
        //(20, 21, 22)
        //-->lineares Gleichungssystem (7 Unbekannte (Re[Aj], Im[Aj]), Im[A4]=0)
        //Matrix (entspricht Koeffizienten des rechten Teils der Gleichungen)
        QuadraticMatrix k = new QuadraticMatrix(7);
        for (int ii = 0; ii < k.getRows(); ii++){ 
            if (ii%2 == 0){
                //Koeffizienten vor den Realteilen
                vali = ii/2;
                //(20) 
                k.setValueAt(q.getc()[vali].getRe(), 0, ii);                   //1.Zeile
                k.setValueAt(q.getd()[vali].getRe(), 1, ii);                   //2.Zeile
                k.setValueAt(q.gete()[vali].getRe(), 2, ii);                   //3.Zeile
                k.setValueAt(q.getf()[vali].getRe(), 3, ii);                   //4.Zeile
                k.setValueAt(q.getg()[vali].getRe(), 4, ii);                   //5.Zeile
                k.setValueAt(q.geth()[vali].getRe(), 5, ii);                   //6.Zeile
                //(21)
                k.setValueAt(q.getp()[vali].multiply(q.gets()[vali]).subtract(q.getq()[vali]).getRe(), 6, ii); //7.Zeile
            }else{
                //Koeffizienten vor den Imaginärteilen
                vali = (ii-1)/2;
                //(20)
                k.setValueAt(-q.getc()[vali].getIm(), 0, ii);                  //1.Zeile
                k.setValueAt(-q.getd()[vali].getIm(), 1, ii);                  //2.Zeile
                k.setValueAt(-q.gete()[vali].getIm(), 2, ii);                  //3.Zeile
                k.setValueAt(-q.getf()[vali].getIm(), 3, ii);                  //4.Zeile
                k.setValueAt(-q.getg()[vali].getIm(), 4, ii);                  //5.Zeile
                k.setValueAt(-q.geth()[vali].getIm(), 5, ii);                  //6.Zeile
                //(21)
                k.setValueAt(-q.getp()[vali].multiply(q.gets()[vali]).subtract(q.getq()[vali]).getIm(), 6, ii);    //7.Zeile   
            }
        }
        
        //(20, 21)
        //Vektor (entspricht linken Teil der Gleichungen)
        double[] v = new double[7];
        for (int ii = 0; ii < 6; ii++){v[ii] = l[ii]/2;}
        v[6] = 0;
        
        //Gleichung lösen
        double[] tmp = k.getInverse().multiplyVektor(v);
        for (int ii = 0; ii < v.length; ii++){
            if (ii%2 == 0){
                fsp[(ii)/2] = new Complex();
                fsp[(ii)/2].setRe(tmp[ii]);
            }else {
                fsp[(ii-1)/2].setIm(tmp[ii]);
            }  
        }
        
        //(22)
        fsp[3].setIm(0);
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Vorbereitung Second Stage Potential
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        //(58)
        Complex[] K = new Complex[8];
        for (int ii = 0; ii < K.length; ii++){K[ii] = new Complex();}
        for (int ii = 0; ii < 4; ii++){
            K[0] = K[0].add(Complex.multiplymultiple(q.gete()[ii], fsp[ii], q.geta()[ii]));
            K[1] = K[1].add(Complex.multiplymultiple(q.gete()[ii], fsp[ii], q.getb()[ii]));
            K[2] = K[2].add(Complex.multiplymultiple(q.getd()[ii], fsp[ii], q.geta()[ii]));
            K[3] = K[3].add(Complex.multiplymultiple(q.getd()[ii], fsp[ii], q.getb()[ii]));
            K[4] = K[4].add(Complex.multiplymultiple(q.getg()[ii], fsp[ii], q.geta()[ii]));
            K[5] = K[5].add(Complex.multiplymultiple(q.getg()[ii], fsp[ii], q.getb()[ii]));
            K[6] = K[6].add(Complex.multiplymultiple(Complex.addmultiple(q.geth()[ii].multiply(2.0), q.gets()[ii].multiply(q.getg()[ii])), fsp[ii], q.geta()[ii]));
            K[7] = K[7].add(Complex.multiplymultiple(Complex.addmultiple(q.geth()[ii].multiply(2.0), q.gets()[ii].multiply(q.getg()[ii])), fsp[ii], q.getb()[ii]));
        }
        
        //(57)
        //ohne R, da es sich später kürzt (59)
        for (int i = 0; i < 4; i++){
            aunsym[i] = Complex.addmultiple(K[i*2  ], K[i*2+1].getConjugate()).multiply(0.5);
            bunsym[i] = Complex.addmultiple(K[i*2+1], K[i*2  ].getConjugate()).multiply(0.5);
        }
        
        //8. Potential
        double deltaW = 360.0/(double)Cutout.getNumWerte();
        pot = new Complex[4][Cutout.getNumWerte()];
        Complex[] ssptmp;
        
        //(60)
        for (int j = 0; j <= Cutout.getNumWerte() - 1; j++){
            ssptmp = get2ndStagePotential(Math.toRadians(j*deltaW));
            for (int i = 0; i < 4; i++){
                pot[i][j] = fsp[i].subtract(ssptmp[i]);
            }
        }
    }
    
    /**
     * Gibt das Second Stage Potential für den angegebenen Winkel zurück.
     * @param theta Winkel im Gradmaß
     * @return Second Stage Potential
     */
    private Complex[] get2ndStagePotential(double theta){
        
        Complex[] ssp = new Complex[4];
        
        //(2)
        Complex zeta = new Complex(Math.cos(theta), Math.sin(theta));
        
        //(59)
        Complex[] phij2 = getPhij2(zeta);
        Complex[] wj    = getWj(zeta);
        
        for (int i = 0; i < 4; i++){ssp[i] = phij2[i].divide(wj[i]);}
        
        return ssp;
    }
    
    private Complex[] getPhij2(Complex zeta){
        Complex[] phij2;

        //(56)
        //Koeffizienten der linken Seite der Gleichungen als Matrix
        QuadraticComplexMatrix c = new QuadraticComplexMatrix(4);
        for (int ii = 0; ii < c.getRows(); ii++){
            c.setValueAt(q.gete()[ii], 0, ii);                                 //1.Zeile
            c.setValueAt(q.getd()[ii], 1, ii);                                 //2.Zeile
            c.setValueAt(q.getg()[ii], 2, ii);                                 //3.Zeile
            c.setValueAt(Complex.addmultiple(q.geth()[ii].multiply(2.0), q.gets()[ii].multiply(q.getg()[ii])), 3, ii);    //4.Zeile
        }
        
        //rechte Seite der Gleichung als Vektor
        Complex[] v = new Complex[4];
        //Summenteil
        Complex summe = new Complex();
        double[] m = h.getConstants();
        for (int i = 1; i < m.length; i++){
            if (m[i] != 0){
                summe = summe.add((new Complex(i*m[i])).divide(Complex.pow(zeta, i+1)));
            }
        }
        //komplette rechte Seite der Gleichung
        for (int ii = 0; ii < v.length; ii++){
            v[ii] = Complex.addmultiple(aunsym[ii].divide(Complex.pow(zeta,2)), bunsym[ii].multiply(summe)).getNegative();
        }
        
        //Gleichung lösen
        phij2 = c.getInverse().multiplyComplexVektor(v);
        
        return phij2;
    }
    
    private Complex[] getWj(Complex zeta){
        Complex[] wj = new Complex[4];
        double[] m = h.getConstants();
        
        //(6) abgeleitet nach zeta
        //Summenteile
        Complex summe1 = new Complex();
        Complex summe2 = new Complex();
        for (int i = 1; i < m.length; i++){
            if (m[i] != 0){
                summe1 = summe1.add(Complex.pow(zeta, i-1).multiply(i*m[i]));
                summe2 = summe2.add((new Complex(-i*m[i])).divide(Complex.pow(zeta, i+1)));
            }
        }
        //gesamte Gleichung
        for (int i = 0; i < 4; i++){
            wj[i] = Complex.addmultiple(q.geta()[i].multiply(Complex.pow(zeta, -2).getNegative().add(summe1)), q.getb()[i].multiply(summe2.add(1.0))).multiply(0.5);
        }
        
        return wj;
    }
    
}
