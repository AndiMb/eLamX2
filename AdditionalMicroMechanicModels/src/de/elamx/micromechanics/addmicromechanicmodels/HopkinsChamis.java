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
package de.elamx.micromechanics.addmicromechanicmodels;

import de.elamx.micromechanics.Fiber;
import de.elamx.micromechanics.Matrix;
import de.elamx.micromechanics.models.Mischungsregel_m;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Richard
 */
public class HopkinsChamis extends Mischungsregel_m{

    public static HopkinsChamis getDefault(FileObject obj) {
        return new HopkinsChamis(obj);
    }

    public HopkinsChamis(FileObject obj) {
        super(obj);
    }

    @Override
    public double getE22(Fiber fiber, Matrix matrix, double phi){
        double x = Math.sqrt(phi);
        return matrix.getEpar()*((1.0 - x) + x/(1.0-x*(1-matrix.getEpar()/fiber.getEnor())));
    }

    @Override
    public double getG12(Fiber fiber, Matrix matrix, double phi){
        double x = Math.sqrt(phi);
        return matrix.getG()*((1.0 - x) + x/(1.0-x*(1-matrix.getG()/fiber.getG())));
    }
}
