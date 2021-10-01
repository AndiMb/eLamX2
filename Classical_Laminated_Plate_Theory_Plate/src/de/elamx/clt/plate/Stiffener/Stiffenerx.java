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
 * Stiffenersx.class creates the Object Stiifenerx with its material and
 * geometric properties for a longitudinal reinforcement in y-direction method
 * add computes the stiffness matrix of the reinforcement
 *
 * @author Martin Rädel
 */
public class Stiffenerx extends Stiffener {

    public Stiffenerx(StiffenerProperties properties, double position) {
        super(properties, position);
    }

    @Override
    public void addStiffness(double[][] kmat, int m, int n, Boundary bx, Boundary by) {
        int k, l;
        
        double tPos = position_ + by.getA()/2.0;

        double E = props_.getE();
        double I = props_.getI();
        double G = props_.getG();
        double J = props_.getJ();

        k = -1;
        for (int pp = 0; pp < m; pp++) {
            for (int qq = 0; qq < n; qq++) {
                k++;
                l = -1;
                for (int ii = 0; ii < m; ii++) {
                    for (int jj = 0; jj < n; jj++) {
                        l++;
                        kmat[k][l] += E * I * bx.IdX2dX2(ii, pp) * by.wx(jj, tPos) * by.wx(qq, tPos)
                                + G * J * bx.IdXdX(ii, pp) * by.wdx(jj, tPos) * by.wdx(qq, tPos);
                    }
                }
            }
        }
    }
    
    @Override
    public void addMass(double[][] mmat, int m, int n, Boundary bx, Boundary by){
        int k,l;
        
        double tPos = position_ + bx.getA()/2.0;
        
        double rho = props_.getRho();
        double A = props_.getA();
        
        k=-1;
        for (int pp = 0; pp < m; pp++){
            for (int qq = 0; qq < n; qq++){
                k++;
                l=-1;
                for (int ii = 0; ii < m; ii++){
                    for (int jj = 0; jj < n; jj++){
                        l++;
                        mmat[k][l] -= rho*A*bx.IXX(ii,pp)    *by.wx(jj,tPos) *by.wx(qq,tPos);
                    }
                }
            }
        }
    }

    @Override
    public int getDirection() {
        return Stiffener.X_DIRECTION;
    }

    @Override
    public String getDirectionAsString() {
        return Stiffener.X_DIRECTION_STRING;
    }

    @Override
    public Stiffener getCopy() {
        return new Stiffenerx(this.getProperties().getCopy(), this.getPosition());
    }
}