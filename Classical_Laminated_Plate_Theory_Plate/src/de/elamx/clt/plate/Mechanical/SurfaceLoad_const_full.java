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
 *
 * @author Andreas Hauffe
 */
public class SurfaceLoad_const_full extends TransverseLoad {

    public static final String PROP_FORCE = "force";
    
    private double force = 0.0;

    public SurfaceLoad_const_full(String name, double force) {
        super(name);
        this.force = force;
    }
    
    public void setForce(double force) {
        double oldForce = this.force;
        this.force = force;
        propertyChangeSupport.firePropertyChange(PROP_FORCE, oldForce, force);
    }
    public double getForce(){return force;}

    @Override
    public void add(double[] fvec, int m, int n, Boundary bx, Boundary by) {
        int k = -1; // Laufvariable (1. Index) für die Steifigkeitsmatrix
        for (int pp = 0; pp < m; pp++) {
            for (int qq = 0; qq < n; qq++) {
                k++;
                fvec[k] += force * bx.IX(pp) * by.IX(qq);
            }
        }
    }

    @Override
    public TransverseLoad getCopy() {
        return new SurfaceLoad_const_full(getName(), force);
    }

}
