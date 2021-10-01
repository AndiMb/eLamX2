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

/**
 * In this class the object load is created which contains the geometric
 * stiffness matrix of the plate
 *
 * @author Martin Rädel
 * @author Andreas Hauffe
 */
public class Loads {

    private double nx  = 0.0; // Kraftfluss [N/mm] in x-Richtung
    private double ny  = 0.0; // Kraftfluss [N/mm] in y-Richtung
    private double nxy = 0.0; // Schubfluss [N/mm]
    
    private double mx  = 0.0;
    private double my  = 0.0;
    private double mxy = 0.0;
    
    /**
     * load plate contains the 3 in-plane loads - 2 of axial compression and 1 of shear load.
     * Die Lasten müssen für Druck negativ sein und für Zug positiv.
     *
     * @param nx Kraftfluss [N/mm] in x-Richtung
     * @param ny Kraftfluss [N/mm] in y-Richtung
     * @param nxy Schubfluss [N/mm]
     */
    public Loads(double nx, double ny, double nxy){
        this(nx,ny,nxy,0.0,0.0,0.0);
    }
    
    public Loads(double nx, double ny, double nxy, double mx, double my, double mxy){
        this.nx  = nx;
        this.ny  = ny;
        this.nxy = nxy;
        this.mx  = mx;
        this.my  = my;
        this.mxy = mxy;
    }
    
    public void setNxx(double val){nx  = val;}
    public void setNyy(double val){ny  = val;}
    public void setNxy(double val){nxy = val;}
    public void setMxx(double val){mx  = val;}
    public void setMyy(double val){my  = val;}
    public void setMxy(double val){mxy = val;}
    
    public double getNxx(){return nx;}
    public double getNyy(){return ny;}
    public double getNxy(){return nxy;}
    public double getMxx(){return mx;}
    public double getMyy(){return my;}
    public double getMxy(){return mxy;}
        
    public double[] getForces(){return new double[]{nx,ny,nxy};}
    public double[] getMoments(){return new double[]{mx,my,mxy};}
    public double[] getLoads(){return new double[]{nx,ny,nxy,mx,my,mxy};}
}
