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
 * @author maik
 */
public class HalpinTsai extends MicroMechModel{

    public static HalpinTsai getDefault(FileObject obj) {
        return new HalpinTsai(obj);
    }

    // Modellspezifische Groessen
    double etaE22 = 2.0;
    double etaG = 1.0;

    // Anlegen eines Konstruktors
    public HalpinTsai(FileObject obj) {
        super(obj);
    }

    @Override
    public double getE11(Fiber fiber, Matrix matrix, double phi) {
        return fiber.getEpar() * phi + matrix.getEpar() * (1 - phi);
    }

    @Override
    public double getE22(Fiber fiber, Matrix matrix, double phi) {
        double mue = ((fiber.getEnor()/matrix.getEpar())-1.0)/((fiber.getEnor()/matrix.getEpar())+etaE22);
        return ((1+etaE22*mue*phi)/(1-mue*phi))*matrix.getEpar();
    }

    @Override
    public double getNue12(Fiber fiber, Matrix matrix, double phi) {
        return (fiber.getNue12() * phi) + (matrix.getNue12() * (1-phi));
    }

    @Override
    public double getG12(Fiber fiber, Matrix matrix, double phi) {
        double mue = ((fiber.getG()/matrix.getG())-1)/((fiber.getG()/matrix.getG())+etaG);
        return ((1+etaG*mue*phi)/(1-mue*phi))*matrix.getG();
    }

}
