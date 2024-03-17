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
package de.elamx.clt.optimization.hauffe;

import de.elamx.clt.optimization.OptimizationInput;
import de.elamx.clt.optimization.Optimizer;
import de.elamx.clt.optimization.sda.SequentialDecisionApproach;
import de.elamx.laminate.DataLayer;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.addFailureCriteria.MaxStress;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = Optimizer.class)
public class HauffeOptimizer extends Optimizer {

    private static final OptimizationParameter params = new OptimizationParameter();

    public HauffeOptimizer() {
        this(null);
    }

    public HauffeOptimizer(OptimizationInput input) {
        super(NbBundle.getMessage(Optimizer.class, "HauffeOptimizer.name"), input);
    }

    @Override
    public Laminat internalOptimize() {
        double[] angles = input.getAngles();
        ArrayList<MinimalReserveFactorCalculator> calculators = input.getCalculators();

        DataLayer baseLayer = new DataLayer("", "", input.getMaterial(), 0.0, input.getThickness(), input.getCriterion());

        boolean isSymmetricLaminateNeeded = false;

        for (MinimalReserveFactorCalculator calcs : calculators) {
            isSymmetricLaminateNeeded = calcs.isSymmetricLaminateNeeded();
            if (isSymmetricLaminateNeeded) {
                break;
            }
        }

        // Bestimmung der maximalen Lagenanzahl durch Sequential Decision Approach
        SequentialDecisionApproach sda = new SequentialDecisionApproach(input);

        Laminat sdaLam = sda.internalOptimize();

        int numberOfCheckedLaminates = sda.getResult().getNumberOfCheckedLaminates();
        int numberOfConstraintEvals = sda.getResult().getNumberOfContraintEvaluations();

        atomicLaminateCounter.decrementAndGet();

        params.setMaxLayerNum(sdaLam.getLayers().size() + params.getDeltaMaxLayerNum());

        // Bestimmung der minimalen Lagenanzahl mittels Superlagen
        DataLayer superLayer = getSuperlayer();

        Laminat laminat = new Laminat("", "", false);
        laminat.setSymmetric(isSymmetricLaminateNeeded || input.isSymmetricLaminat());

        double minResFac = 0.0;
        while (minResFac < 1.0) {
            laminat.addLayer(superLayer.getCopyWithoutListeners(superLayer.getAngle()));

            minResFac = Double.MAX_VALUE;
            for (MinimalReserveFactorCalculator calcs : calculators) {
                minResFac = Math.min(minResFac, calcs.getMinimalReserveFactor(laminat));
                numberOfConstraintEvals++;
            }

            numberOfCheckedLaminates++;
        }

        params.setMinLayerNum(laminat.getLayers().size() - 1);
        
        result.setNewResults(sdaLam.getCopyWithoutListener(false),
                             numberOfConstraintEvals,
                             numberOfCheckedLaminates,
                             sda.getResult().getMinReserveFactor());

        Individuum[] eltern = new Individuum[params.getAnzEltern()];
        Individuum[] kinder = new Individuum[params.getAnzKinder()];

        double[] tmpAngles = new double[params.getMaxLayerNum()];

        int ind = 0;
        for (Layer l : sdaLam.getLayers()) {
            tmpAngles[ind++] = l.getAngle();
        }

        while (ind < params.getMaxLayerNum()) {
            tmpAngles[ind++] = angles[GEP.getRandomInteger(angles.length)];
        }

        eltern[0] = new Individuum(sdaLam.getLayers().size(), tmpAngles);
        evalObjectiv(input, eltern[0], baseLayer, isSymmetricLaminateNeeded);
        numberOfConstraintEvals += calculators.size();
        numberOfCheckedLaminates++;
        
        eltern[0] = permuteIndividuum(eltern[0], baseLayer, isSymmetricLaminateNeeded);

        for (int ii = 1; ii < eltern.length; ii++) {
            eltern[ii] = getNewIndividuum(angles);
            evalObjectiv(input, eltern[ii], baseLayer, isSymmetricLaminateNeeded);
            numberOfConstraintEvals += calculators.size();
            numberOfCheckedLaminates++;
        }

        Individuum bestIndiv = null;
        int stopGenCounter = 0;
        int generationOfLastChange = 0;
        int generationOhneAenderung = 0;
        Individuum oldBestIndiv = eltern[0];
        for (int ii = 0; ii < params.getMaxGenerations(); ii++) {
            
            if (Thread.interrupted ()) return null;
            
            GEP.mutation(params, eltern, kinder, 0, 19, angles);
            GEP.onePointCrossover(eltern, kinder, 20, 29);
            GEP.twoPointCrossover(eltern, kinder, 30, 39);
            GEP.permutation(eltern, kinder, 40, 49);
            GEP.angleShifts(params, eltern, kinder, 50, 59, angles);

            for (Individuum indiv : kinder) {
                evalObjectiv(input, indiv, baseLayer, isSymmetricLaminateNeeded);
                numberOfConstraintEvals += calculators.size();
                numberOfCheckedLaminates++;
            }

            oldBestIndiv = bestIndiv;
            bestIndiv = GEP.selection(eltern, kinder);
        
            if (oldBestIndiv == bestIndiv) {
                stopGenCounter++;
                generationOhneAenderung++;
            } else {
                generationOfLastChange = ii + 1;
                stopGenCounter = 0;
            }
            
            result.setNewResults(IndividuumToLaminat(bestIndiv, baseLayer, isSymmetricLaminateNeeded),
                             numberOfConstraintEvals,
                             numberOfCheckedLaminates,
                             bestIndiv.getMinReserveFactor(),
                             generationOfLastChange);

            kinder = new Individuum[params.getAnzKinder()];

            if (params.getMaxLayerNum() > bestIndiv.getNumLayers() + params.getDeltaMaxLayerNum()
                    || generationOhneAenderung > params.getMaxGenerationOhneAenderung()) {
                generationOhneAenderung = 0;
                params.setMaxLayerNum(bestIndiv.getNumLayers() + params.getDeltaMaxLayerNum());
                params.setMinLayerNum(bestIndiv.getNumLayers() - 1);
                eltern[0] = bestIndiv;
                eltern[0].setMaxLayerNum(params.getMaxLayerNum());

                for (int jj = 1; jj < eltern.length; jj++) {
                    eltern[jj] = getNewIndividuum(angles);
                    evalObjectiv(input, eltern[jj], baseLayer, isSymmetricLaminateNeeded);
                    numberOfConstraintEvals += calculators.size();
                    numberOfCheckedLaminates++;
                }
            }

            if (stopGenCounter == params.getStopGens()) {
                break;
            }
        }

        laminat = IndividuumToLaminat(bestIndiv, baseLayer, isSymmetricLaminateNeeded).getCopy(false);

        laminat.setName(NbBundle.getMessage(Optimizer.class, "Optimized_Laminate") + " " + atomicLaminateCounter.incrementAndGet());

        for (Layer l : laminat.getLayers()) {
            l.setName(NbBundle.getMessage(Optimizer.class, "Optimized_Layer") + " " + atomicLayerCounter.incrementAndGet());
        }
        
        result.setFinished(true);

        return laminat;
    }

    @Override
    public Optimizer getInstance(OptimizationInput input) {
        return new HauffeOptimizer(input);
    }

    private Individuum getNewIndividuum(double[] possibleAngles) {

        int numLayer = params.getMinLayerNum() + (int) ((params.getMaxLayerNum() - params.getMinLayerNum()) * Math.random());

        double[] angles = new double[params.getMaxLayerNum()];

        for (int ii = 0; ii < angles.length; ii++) {
            angles[ii] = possibleAngles[(int) (possibleAngles.length * Math.random())];
        }

        return new Individuum(numLayer, angles);
    }

    private void evalObjectiv(OptimizationInput input, Individuum indiv, DataLayer baseLayer, boolean isSymmetryNeeded) {

        Laminat laminat = IndividuumToLaminat(indiv, baseLayer, isSymmetryNeeded);

        double minResFac = Double.MAX_VALUE;
        for (MinimalReserveFactorCalculator calcs : input.getCalculators()) {
            minResFac = Math.min(minResFac, calcs.getMinimalReserveFactor(laminat));
        }

        double objective = (double) indiv.getNumLayers() + 100.0 * ((1.0 - Math.min(1.0, minResFac)) > 0.0 ? 1.0 : 0.0);

        indiv.setObjective(objective);
        indiv.setMinReserveFactor(minResFac);
    }

    private Laminat IndividuumToLaminat(Individuum indiv, DataLayer baseLayer, boolean isSymmetryNeeded) {
        Laminat laminat = new Laminat("", "", false);
        laminat.setSymmetric(isSymmetryNeeded || input.isSymmetricLaminat());

        double[] angles = indiv.getAngles();
        DataLayer[] layers = new DataLayer[indiv.getNumLayers()];

        for (int ii = 0; ii < indiv.getNumLayers(); ii++) {
            layers[ii] = baseLayer.getCopyWithoutListeners(angles[ii]);
        }

        laminat.addLayers(Arrays.asList(layers));

        return laminat;
    }

    private Individuum permuteIndividuum(Individuum indiv, DataLayer baseLayer, boolean isSymmetryNeeded) {
        double[] angles = input.getAngles();
        Individuum bestIndiv = indiv;
        for (int layNum = 0; layNum < indiv.getNumLayers(); layNum++) {
            Individuum checkIndiv = bestIndiv.copy();
            for (int angleNum = 0; angleNum < angles.length; angleNum++) {
                checkIndiv.getAngles()[layNum] = angles[angleNum];
                evalObjectiv(input, checkIndiv, baseLayer, isSymmetryNeeded);
                if (checkIndiv.getMinReserveFactor() > bestIndiv.getMinReserveFactor()) {
                    bestIndiv = checkIndiv.copy();
                    bestIndiv.setMinReserveFactor(checkIndiv.getMinReserveFactor());
                    bestIndiv.setObjective(checkIndiv.getObjective());
                }
            }
        }
        return bestIndiv;
    }

    @Override
    public boolean onlySymmetricLaminates() {
        return false;
    }

    private DataLayer getSuperlayer() {
        double EPar = input.getMaterial().getEpar();
        double nue = 0.0;
        double G = EPar / (2.0 * (1.0 + nue));
        DefaultMaterial superMaterial = new DefaultMaterial("", "Supermaterial", EPar, EPar, nue, G, 0.0, false);
        double RPar = Math.max(input.getMaterial().getRParTen(), input.getMaterial().getRParCom());
        superMaterial.setRParTen(RPar);
        superMaterial.setRParCom(RPar);
        superMaterial.setRNorTen(RPar);
        superMaterial.setRNorCom(RPar);
        /*
         *
         *  ACHTUNG: Der Schubwert ist durch reine Versuche bestimmt.
         *
         */
        superMaterial.setRShear(2.0 * RPar / 3.0);

        Criterion criterion = null;
        Lookup lkp = Lookups.forPath("elamx/failurecriteria");
        Collection<? extends Criterion> c = lkp.lookupAll(Criterion.class);
        for (Criterion crit : c) {
            if (crit instanceof MaxStress) {
                criterion = crit;
                break;
            }
        }

        return new DataLayer("", "Superlayer", superMaterial, 0.0, input.getThickness(), criterion);
    }
}
