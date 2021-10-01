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

/**
 *
 * @author raedel
 */
public class VibrationInput extends Input{

    public VibrationInput() {
        this(500.0, 500.0, true, 0, 0, 10, 10);
    }

    public VibrationInput(double length, double width, boolean wholeD, int bcx, int bcy, int m, int n) {
        super(length, width, wholeD, bcx, bcy, m, n);
    }

    @Override
    public Input copy() {
        VibrationInput in = new VibrationInput(getLength(), getWidth(), isWholeD(), getBcx(), getBcy(), getM(), getN());
        for (StiffenerProperties ss : getStiffenerProperties()){
            in.addStiffenerProperty(ss.getCopy());
        }
        return in;
    }
    
}
