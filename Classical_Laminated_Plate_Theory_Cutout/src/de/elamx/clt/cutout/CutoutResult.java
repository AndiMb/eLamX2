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

/**
 *
 * @author raedel
 */
public class CutoutResult{
    
    private final CLT_Laminate laminate;
    private final CutoutInput input;
    
    private final double[]   alpha, nalpha, malpha;
    private final double[]   nx, ny, nxy, mx, my, mxy;
    
    public CutoutResult(CLT_Laminate laminate, CutoutInput input, double[][] results){
        this.laminate = laminate;
        this.input = input.copy();
        this.input.getCutoutGeometry().calcConstants(); // notwendig, da diese in der Kopie sonst nicht initialisiert sind.
        
        alpha  = results[0];
        nx     = results[1];
        ny     = results[2];
        nxy    = results[3];
        mx     = results[4];
        my     = results[5];
        mxy    = results[6];
        nalpha = results[7];
        malpha = results[8];
    }

    public CLT_Laminate getLaminate() {
        return laminate;
    }

    public CutoutInput getInput() {
        return input;
    }
    
    public double[] getAlpha(){return alpha;}
    public double[] getNxx(){return nx;}
    public double[] getNyy(){return ny;}
    public double[] getNxy(){return nxy;}
    public double[] getMxx(){return mx;}
    public double[] getMyy(){return my;}
    public double[] getMxy(){return mxy;}
    public double[] getNAlpha(){return nalpha;}
    public double[] getMAlpha(){return malpha;}
    
}
