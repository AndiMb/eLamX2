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
package de.elamx.clt.optimization.sda;

import de.elamx.clt.optimization.OptimizationInput;
import de.elamx.clt.optimization.Optimizer;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.util.ArrayList;
import java.util.UUID;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Diese Implementierung basiert auf den Angaben in Abschnitt 2.2.1 in
 * "Akira Todoroki: Object-Oriented Approach to Optimize Composite Laminated Plate Stiffness 
 * with Discrete Ply Angles" (https://doi.org/10.1177%2F002199839603000904)
 *  
 * @author Andreas Hauffe
 */
@ServiceProvider(service = Optimizer.class)
public class SequentialDecisionApproach extends Optimizer{

    public SequentialDecisionApproach() {
        this(null);
    }

    public SequentialDecisionApproach(OptimizationInput input) {
        super(NbBundle.getMessage(Optimizer.class, "SequentialDecisionApproach.name"), input);
    }

    @Override
    public Laminat internalOptimize() {
        double[] angles = input.getAngles();
        ArrayList<MinimalReserveFactorCalculator> calculators = input.getCalculators();

        Layer baseLayer = new Layer("", NbBundle.getMessage(Optimizer.class, "Optimized_Layer") + " " + atomicLayerCounter.incrementAndGet(), input.getMaterial(), 0.0, input.getThickness(), input.getCriterion());

        Laminat laminat = new Laminat(UUID.randomUUID().toString(), NbBundle.getMessage(Optimizer.class, "Optimized_Laminate") + " " + atomicLaminateCounter.incrementAndGet(), false);

        boolean isSymmetricLaminateNeeded = false;

        for (MinimalReserveFactorCalculator calcs : calculators) {
            isSymmetricLaminateNeeded = calcs.isSymmetricLaminateNeeded();
            if (isSymmetricLaminateNeeded) {
                break;
            }
        }

        laminat.setSymmetric(isSymmetricLaminateNeeded || input.isSymmetricLaminat());

        double minReserveFactor = -Double.MAX_VALUE;

        result.setBestLaminate(laminat.getCopyWithoutListener(false));
        result.setMinReserveFactor(minReserveFactor);
        result.setNumberOfCheckedLaminates(0);
        result.setNumberOfContraintEvaluations(0);

        int numberOfCheckedLaminates = 0;
        int numberOfConstraintEvals = 0;

        while (minReserveFactor < 1.0) {
            Layer actLayer = baseLayer.getCopyWithoutListeners(baseLayer.getAngle());

            laminat.addLayer(laminat.getNumberofLayers()/2,actLayer);

            double actResFac = -Double.MAX_VALUE;
            double bestAngle = 0.0;
            for (int ii = 0; ii < angles.length; ii++) {

                actLayer.setAngle(angles[ii]);

                double minResFac = Double.MAX_VALUE;
                for (MinimalReserveFactorCalculator calcs : calculators) {
                    minResFac = Math.min(minResFac, calcs.getMinimalReserveFactor(laminat));
                }

                if (minResFac > actResFac) {
                    actResFac = minResFac;
                    bestAngle = angles[ii];
                }
            }

            actLayer.setAngle(bestAngle);

            double minResFac = Double.MAX_VALUE;
            for (MinimalReserveFactorCalculator calcs : calculators) {
                minResFac = Math.min(minResFac, calcs.getMinimalReserveFactor(laminat));
                numberOfConstraintEvals++;
            }

            minReserveFactor = minResFac;
            numberOfCheckedLaminates++;

            result.setBestLaminate(laminat.getCopyWithoutListener(false));
            result.setMinReserveFactor(minReserveFactor);
            result.setNumberOfCheckedLaminates(numberOfCheckedLaminates);
            result.setNumberOfContraintEvaluations(numberOfConstraintEvals);
        }

        result.setFinished(true);
        
        return laminat;
    }

    @Override
    public Optimizer getInstance(OptimizationInput input) {
        return new SequentialDecisionApproach(input);
    }

    @Override
    public boolean onlySymmetricLaminates() {
        return false;
    }

}
