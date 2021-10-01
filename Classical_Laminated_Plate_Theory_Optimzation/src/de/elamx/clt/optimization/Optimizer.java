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
package de.elamx.clt.optimization;

import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.optimization.MRFC_ModuleDataGenerator;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import org.openide.util.Lookup;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class Optimizer{
    
    protected final OptimizationInput input;
    protected final OptimizationResult result;
    protected static final AtomicInteger atomicLaminateCounter = new AtomicInteger(0);
    protected final AtomicInteger atomicLayerCounter = new AtomicInteger(0);
    
    private final String name;

    public Optimizer(String name, OptimizationInput input) {
        this.input  = input;
        this.result = new OptimizationResult();
        this.name   = name;
    }
    
    public abstract Laminat internalOptimize();
    
    public final void optimize(boolean addToLookup){
        Laminat laminat = internalOptimize();
        
        if (laminat == null){
            return;
        }
        
        if (addToLookup){

            Collection<? extends MRFC_ModuleDataGenerator> dataGens = Lookup.getDefault().lookupAll(MRFC_ModuleDataGenerator.class);

            laminat = laminat.getCopy(false);

            boolean test;
            for (MinimalReserveFactorCalculator calcs : input.getCalculators()) {
                for (MRFC_ModuleDataGenerator gen : dataGens) {
                    test = gen.generateELamXModuleData(laminat, calcs);
                    if (test) {
                        break;
                    }
                }
            }

            eLamXLookup.getDefault().add(laminat);
        }
    }
    
    public abstract Optimizer getInstance(OptimizationInput input);
    
    public abstract boolean onlySymmetricLaminates();

    public final OptimizationResult getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
