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
package de.elamx.clt.calculation.services;

import de.elamx.clt.CLT_Input;
import de.elamx.clt.calculation.CalculationModuleData;
import de.elamx.clt.calculation.LoadSaveLaminateHookImpl;
import de.elamx.clt.optimization.MinimalReserveFactorImplementation;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.optimization.MRFC_ModuleDataGenerator;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = MRFC_ModuleDataGenerator.class)
public class MRFC_ModuleDataGeneratorImpl implements MRFC_ModuleDataGenerator {

    @Override
    public boolean generateELamXModuleData(Laminat laminat, MinimalReserveFactorCalculator calculator) {
        if (calculator instanceof MinimalReserveFactorImplementation) {
            MinimalReserveFactorImplementation calc = (MinimalReserveFactorImplementation) calculator;

            laminat.getLookup().add(new CalculationModuleData(laminat, calc.getInput().copy()));

            return true;
        }
        return false;
    }

    @Override
    public String getCalculatorClassName() {
        return MinimalReserveFactorImplementation.class.getName();
    }

    @Override
    public MinimalReserveFactorCalculator load(Element loadFromElement) {

        CLT_Input input = LoadSaveLaminateHookImpl.loadInput(loadFromElement);

        return new MinimalReserveFactorImplementation(input);
    }

    @Override
    public void store(Document doc, Element storeToElement, MinimalReserveFactorCalculator calculator) {
        if (calculator instanceof MinimalReserveFactorImplementation) {
            MinimalReserveFactorImplementation calc = (MinimalReserveFactorImplementation) calculator;
            LoadSaveLaminateHookImpl.storeInput(doc, storeToElement, calc.getInput());
        }
    }

}
