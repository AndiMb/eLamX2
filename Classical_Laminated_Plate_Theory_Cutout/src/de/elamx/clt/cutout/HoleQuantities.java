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
public abstract class HoleQuantities {
    
    protected final CLT_Laminate lam;
    protected double[] load;
    
    protected Complex[] a = new Complex[4];
    protected Complex[] b = new Complex[4];
    
    protected Complex[] s;
    protected Complex[] p;
    protected Complex[] q;
    
    protected Complex[] c = new Complex[4];
    protected Complex[] d = new Complex[4];
    protected Complex[] e = new Complex[4];
    protected Complex[] f = new Complex[4];
    protected Complex[] g = new Complex[4];
    protected Complex[] h = new Complex[4];
    
    public HoleQuantities(CLT_Laminate lam, double[] load){
        this.lam  = lam;
        this.load = load;
    }
    
    public Complex[] geta(){return a;}
    public Complex[] getb(){return b;}
    
    public Complex[] gets(){return s;}
    public Complex[] getp(){return p;}
    public Complex[] getq(){return q;}
    
    public Complex[] getc(){return c;}
    public Complex[] getd(){return d;}
    public Complex[] gete(){return e;}
    public Complex[] getf(){return f;}
    public Complex[] getg(){return g;}
    public Complex[] geth(){return h;}
    
    protected abstract void calc();
    
    protected void calcab(int maxidx){
        //[1](7) bzw. [2](26)
        for (int i = 0; i < maxidx; i++){      
            a[i] = new Complex(1 - s[i].getIm(),  s[i].getRe());
            b[i] = new Complex(1 + s[i].getIm(), -s[i].getRe());
        }
    }
    
}
