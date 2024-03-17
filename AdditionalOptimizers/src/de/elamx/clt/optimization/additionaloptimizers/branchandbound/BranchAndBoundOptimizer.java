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
package de.elamx.clt.optimization.additionaloptimizers.branchandbound;

import de.elamx.clt.optimization.OptimizationInput;
import de.elamx.clt.optimization.Optimizer;
import de.elamx.clt.optimization.additionaloptimizers.DummyBundle;
import de.elamx.laminate.DataLayer;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = Optimizer.class)
public class BranchAndBoundOptimizer extends Optimizer{

    public BranchAndBoundOptimizer() {
        this(null);
    }

    public BranchAndBoundOptimizer(OptimizationInput input) {
        super(NbBundle.getMessage(DummyBundle.class, "BranchAndBoundOptimizer.name"), input);
    }

    @Override
    public Laminat internalOptimize() {
        double[] angles = input.getAngles();
        ArrayList<MinimalReserveFactorCalculator> calculators = input.getCalculators();

        DataLayer baseLayer = new DataLayer("", NbBundle.getMessage(DummyBundle.class, "Optimized_Layer") + " " + atomicLayerCounter.incrementAndGet(), input.getMaterial(), 0.0, input.getThickness(), input.getCriterion());

        Laminat laminat = new Laminat(UUID.randomUUID().toString(), NbBundle.getMessage(DummyBundle.class, "Optimized_Laminate") + " " + atomicLaminateCounter.incrementAndGet(), false);

        boolean isSymmetricLaminateNeeded = false;

        for (MinimalReserveFactorCalculator calcs : calculators) {
            isSymmetricLaminateNeeded = calcs.isSymmetricLaminateNeeded();
            if (isSymmetricLaminateNeeded) {
                break;
            }
        }

        laminat.setSymmetric(isSymmetricLaminateNeeded || input.isSymmetricLaminat());

        double maxReserveFactor = -Double.MAX_VALUE;

        result.setBestLaminate(laminat.getCopyWithoutListener(false));
        result.setMinReserveFactor(maxReserveFactor);
        result.setNumberOfCheckedLaminates(0);
        result.setNumberOfContraintEvaluations(0);

        int numberOfCheckedLaminates = 0;
        int numberOfConstraintEvals = 0;
        
        ArrayList<Laminat> oldLaminates = new ArrayList<>();
        oldLaminates.add(laminat);
        ArrayList<Laminat> newLaminates;
        Laminat bestLam = laminat;
        
        while(maxReserveFactor < 1.0){
            
            newLaminates = new ArrayList<>(oldLaminates.size());
            System.gc();
            
            for (Laminat lam : oldLaminates) {
                newLaminates.addAll(Arrays.asList(createSubLaminates(lam, baseLayer, angles)));
            }
            
            for (Laminat lam : newLaminates) {

                double minResFac = Double.MAX_VALUE;
                for (MinimalReserveFactorCalculator calcs : calculators) {
                    minResFac = Math.min(minResFac, calcs.getMinimalReserveFactor(lam));
                    numberOfConstraintEvals++;
                }

                if (minResFac > maxReserveFactor) {
                    maxReserveFactor = minResFac;
                    bestLam          = lam;
                }
                numberOfCheckedLaminates++;
                
                if (numberOfCheckedLaminates % 100 == 0){
                    result.setBestLaminate(bestLam.getCopyWithoutListener(false));
                    result.setMinReserveFactor(maxReserveFactor);
                    result.setNumberOfCheckedLaminates(numberOfCheckedLaminates);
                    result.setNumberOfContraintEvaluations(numberOfConstraintEvals);
                }
                
            }
            
            result.setBestLaminate(bestLam.getCopyWithoutListener(false));
            result.setMinReserveFactor(maxReserveFactor);
            result.setNumberOfCheckedLaminates(numberOfCheckedLaminates);
            result.setNumberOfContraintEvaluations(numberOfConstraintEvals);
            
            oldLaminates = newLaminates;
        }

        result.setFinished(true);
        
        return bestLam;
    }
    
    private Laminat[] createSubLaminates(Laminat laminate, DataLayer baseLayer, double[] angles){
        Laminat[] newLaminates = new Laminat[angles.length];
        for(int ii = 0; ii < angles.length; ii++){
            newLaminates[ii] = laminate.getCopyWithoutListener(false);
            DataLayer newLayer = baseLayer.getCopyWithoutListeners(angles[ii]);
            newLaminates[ii].addLayer(newLayer);
        }
        return newLaminates;
    }

    @Override
    public Optimizer getInstance(OptimizationInput input) {
        return new BranchAndBoundOptimizer(input);
    }

    @Override
    public boolean onlySymmetricLaminates() {
        return false;
    }
    
}
