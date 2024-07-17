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
package de.elamx.clt.plate;

import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.clt.plate.dmatrix.DMatrixService;
import de.elamx.clt.plate.dmatrix.StandardDMatrixServiceImpl;
import org.openide.util.Lookup;


/**
 *
 * @author Andreas Hauffe
 */
public class BucklingInput extends Input{

    public static final String PROP_DTILDE = "Dtilde";

    public static final String PROP_NX = "nx";
    public static final String PROP_NY = "ny";
    public static final String PROP_NXY = "nxy";
    private double nx;
    private double ny;
    private double nxy;

    public BucklingInput() {
        this(500.0, 500.0, 1.0, 0.0, 0.0, Lookup.getDefault().lookup(StandardDMatrixServiceImpl.class), 0, 0, 10, 10);
    }

    public BucklingInput(double length, double width, double nx, double ny, double nxy, DMatrixService dMatService,
            int bcx, int bcy, int m, int n) {
        super(length, width, dMatService, bcx, bcy, m, n);
        this.nx = nx;
        this.ny = ny;
        this.nxy = nxy;
    }

    /**
     * @return the nx
     */
    public double getNx() {
        return nx;
    }

    /**
     * @param nx the nx to set
     */
    public void setNx(double nx) {
        double oldNx = this.nx;
        this.nx = nx;
        firePropertyChange(PROP_NX, oldNx, nx);
    }

    /**
     * @return the ny
     */
    public double getNy() {
        return ny;
    }

    /**
     * @param ny the ny to set
     */
    public void setNy(double ny) {
        double oldNy = this.ny;
        this.ny = ny;
        firePropertyChange(PROP_NY, oldNy, ny);
    }

    /**
     * @return the nxy
     */
    public double getNxy() {
        return nxy;
    }

    /**
     * @param nxy the nxy to set
     */
    public void setNxy(double nxy) {
        double oldNxy = this.nxy;
        this.nxy = nxy;
        firePropertyChange(PROP_NXY, oldNxy, nxy);
    }

    @Override
    public Input copy() {
        Input in = new BucklingInput(getLength(), getWidth(), nx, ny, nxy, getDMatrixService(), getBcx(), getBcy(), getM(), getN());
        for (StiffenerProperties ss : getStiffenerProperties()){
            in.addStiffenerProperty(ss.getCopy());
        }
        return in;
    }
}
