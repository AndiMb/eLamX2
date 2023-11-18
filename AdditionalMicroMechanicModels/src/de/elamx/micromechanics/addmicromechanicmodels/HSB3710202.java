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
import de.elamx.micromechanics.models.MicroMechModel;
import org.openide.filesystems.FileObject;


/**
 * 
 * Diese micromechanische Modell stammt aus dem Handbuch für Strukturberechnung
 * (HSB) Arbeitsplatz 37102-2, wobei darin auf die Quelle 
 * R. M. Jones: Mechanics of Composite Materials, Scripta Book Company, 
 * Washington, D.C. 1975 verwiesen wird.
 * 
 * @author Andreas Hauffe
 */
public class HSB3710202 extends MicroMechModel{

    public static HSB3710202 getDefault(FileObject obj) {
        return new HSB3710202(obj);
    }

    public HSB3710202(FileObject obj) {
        super(obj);
    }

    @Override
    public double getE11(Fiber fiber, Matrix matrix, double phi) {
        return fiber.getEpar() * phi + matrix.getEpar() * (1.0-phi);
    }

    @Override
    public double getE22(Fiber fiber, Matrix matrix, double phi) {
        double val1 = (1.0 - 2.0 * Math.sqrt(phi/Math.PI));
        double val2 = Math.PI / (2.0 * (1.0 - matrix.getEnor()/fiber.getEnor()));
        double val3 = 2.0 / ((1.0 - matrix.getEnor()/fiber.getEnor()) * Math.sqrt(1.0 - 4.0 * phi / Math.PI * (1.0 - matrix.getEnor()/fiber.getEnor()) * (1.0 - matrix.getEnor()/fiber.getEnor())));
        double val4 = Math.atan(Math.sqrt(
                (1.0 + 2.0 * Math.sqrt(phi / Math.PI) * (1.0 - matrix.getEnor()/fiber.getEnor())) /
                (1.0 - 2.0 * Math.sqrt(phi / Math.PI) * (1.0 - matrix.getEnor()/fiber.getEnor()))));
    
        return matrix.getEnor() * (val1 - val2 + val3 * val4);
    }

    @Override
    public double getNue12(Fiber fiber, Matrix matrix, double phi) {
        return fiber.getNue12()*phi + matrix.getNue12()*(1-phi);
    }

    @Override
    public double getG12(Fiber fiber, Matrix matrix, double phi) {
        double val1 = (1.0 - 2.0 * Math.sqrt(phi/Math.PI));
        double val2 = Math.PI / (2.0 * (1.0 - matrix.getG()/fiber.getG()));
        double val3 = 2.0 / ((1.0 - matrix.getG()/fiber.getG()) * Math.sqrt(1.0 - 4.0 * phi / Math.PI * (1.0 - matrix.getG()/fiber.getG()) * (1.0 - matrix.getG()/fiber.getG())));
        double val4 = Math.atan(Math.sqrt(
                (1.0 + 2.0 * Math.sqrt(phi / Math.PI) * (1.0 - matrix.getG()/fiber.getG())) /
                (1.0 - 2.0 * Math.sqrt(phi / Math.PI) * (1.0 - matrix.getG()/fiber.getG()))));
    
        return matrix.getG() * (val1 - val2 + val3 * val4);
    }    
}