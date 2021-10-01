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
package de.elamx.clt.plate.Stiffener;

import de.elamx.clt.plate.Boundary.Boundary;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class Stiffener {

    public static final int X_DIRECTION = 1;
    public static final int Y_DIRECTION = 2;
    public static final String X_DIRECTION_STRING = "x";
    public static final String Y_DIRECTION_STRING = "y";
    protected StiffenerProperties props_ = null;
    protected double position_ = 0.0;

    public Stiffener(StiffenerProperties properties, double position) {
        props_ = properties;
        position_ = position;
    }

    public double getPosition() {
        return position_;
    }

    public void setPosition(double position) {
        position_ = position;
    }

    public StiffenerProperties getProperties() {
        return props_;
    }

    public abstract void addStiffness(double[][] kmat, int m, int n, Boundary bx, Boundary by);
    
    public abstract void addMass(double[][] mmat, int m, int n, Boundary bx, Boundary by);
    
    public void addStiffnessAndMass(double[][] kmat, double[][] mmat, int m, int n, Boundary bx, Boundary by) {
        addStiffness(kmat, m, n, bx, by);
        addMass(mmat, m, n, bx, by);
    }

    public abstract int getDirection();

    public abstract String getDirectionAsString();

    public abstract Stiffener getCopy();
}
