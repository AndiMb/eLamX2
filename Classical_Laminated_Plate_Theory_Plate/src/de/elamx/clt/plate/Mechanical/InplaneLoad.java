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

import de.elamx.clt.plate.Boundary.Boundary;

/**
 * In this class the object load is created which contains the geometric
 * stiffness matrix of the plate
 *
 * @author Martin Rädel
 * @author Andreas Hauffe
 */
public class InplaneLoad {

    private double nx_  = 0.0; // Kraftfluss [N/mm] in x-Richtung
    private double ny_  = 0.0; // Kraftfluss [N/mm] in y-Richtung
    private double nxy_ = 0.0; // Schubfluss [N/mm]
    
    private double mx_  = 0.0;
    private double my_  = 0.0;
    private double mxy_ = 0.0;

    private int k; // 1. Index der Steifigkeitsmatrix
    private int l; // 2. Index der Steifigkeitsmatrix
    
    /**
     * load plate contains the 3 in-plane loads - 2 of axial compression and 1 of shear load.
     * Die Lasten müssen für Druck negativ sein und für Zug positiv.
     *
     * @param nx Kraftfluss [N/mm] in x-Richtung
     * @param ny Kraftfluss [N/mm] in y-Richtung
     * @param nxy Schubfluss [N/mm]
     */
    public InplaneLoad(double nx, double ny, double nxy){
        this(nx,ny,nxy,0.0,0.0,0.0);
    }
    
    public InplaneLoad(double nx, double ny, double nxy, double mx, double my, double mxy){
        nx_  = nx;
        ny_  = ny;
        nxy_ = nxy;
        mx_  = mx;
        my_  = my;
        mxy_ = mxy;
    }
    
    public void setNxx(double val){nx_  = val;}
    public void setNyy(double val){ny_  = val;}
    public void setNxy(double val){nxy_ = val;}
    public void setMxx(double val){mx_  = val;}
    public void setMyy(double val){my_  = val;}
    public void setMxy(double val){mxy_ = val;}
    
    public double getNxx(){return nx_;}
    public double getNyy(){return ny_;}
    public double getNxy(){return nxy_;}
    public double getMxx(){return mx_;}
    public double getMyy(){return my_;}
    public double getMxy(){return mxy_;}
        
    public double[] getForces(){return new double[]{nx_,ny_,nxy_};}
    public double[] getMoments(){return new double[]{mx_,my_,mxy_};}
    public double[] getLoads(){return new double[]{nx_,ny_,nxy_,mx_,my_,mxy_};}

    /**
     * the method load.add fills the geometric stiffness matrix with the values based on the ritz-approach
     * @param Kgmat geometrische Steifigkeitsmatrix (m*n x m*n)
     * @param m Anzahl der Terme für den Ritz-Ansatz in x-Richtung
     * @param n Anzahl der Terme für den Ritz-Ansatz in y-Richtung
     * @param bx Randbedingungsobjekt in x-Richtung
     * @param by Randbedingungsobjekt in y-Richtung
     */
    public void add(double[][] Kgmat, int m, int n, Boundary bx, Boundary by){
        k = -1;
        for (int pp = 0; pp < m; pp++){
            for (int qq = 0; qq < n; qq++){
                k++;
                l=-1;
                for (int ii = 0; ii < m; ii++){
                    for (int jj = 0; jj < n; jj++){
                        l++;
                        Kgmat[k][l] = nx_  * ( bx.IdXdX(ii,pp) * by.IXX(jj,qq) )
                                    + nxy_ * ( bx.IXdX(ii,pp)  * by.IXdX(qq,jj) + bx.IXdX(pp,ii) * by.IXdX(jj,qq) )
                                    + ny_  * ( bx.IXX(ii,pp)   * by.IdXdX(jj,qq) );
                    }
                }
            }
        }
    }
}
