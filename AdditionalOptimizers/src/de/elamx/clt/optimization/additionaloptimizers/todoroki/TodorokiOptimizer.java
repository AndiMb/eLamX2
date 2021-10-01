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
package de.elamx.clt.optimization.additionaloptimizers.todoroki;

import de.elamx.clt.optimization.OptimizationInput;
import de.elamx.clt.optimization.Optimizer;
import de.elamx.clt.optimization.additionaloptimizers.DummyBundle;
import de.elamx.clt.optimization.sda.SequentialDecisionApproach;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.addFailureCriteria.MaxStress;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 * Diese Implementierung basiert auf "Akira Todoroki: Object-Oriented Approach 
 * to Optimize Composite Laminated Plate Stiffness with Discrete Ply Angles" 
 * (https://doi.org/10.1177%2F002199839603000904)
 *  
 * @author Andreas Hauffe
 */
@ServiceProvider(service = Optimizer.class)
public class TodorokiOptimizer extends Optimizer{

    public TodorokiOptimizer() {
        this(null);
    }

    public TodorokiOptimizer(OptimizationInput input) {
        super(NbBundle.getMessage(DummyBundle.class, "TodorokiOptimizer.name"), input);
    }

    @Override
    public Laminat internalOptimize() {
        double[] angles = input.getAngles();
        ArrayList<MinimalReserveFactorCalculator> calculators = input.getCalculators();

        Layer baseLayer = new Layer("", NbBundle.getMessage(DummyBundle.class, "Optimized_Layer") + " " + atomicLayerCounter.incrementAndGet(), input.getMaterial(), 0.0, input.getThickness(), input.getCriterion());
        
        Layer superLayer = getSuperlayer();
        
        Laminat laminat = new Laminat(UUID.randomUUID().toString(), NbBundle.getMessage(DummyBundle.class, "Optimized_Laminate") + " " + atomicLaminateCounter.incrementAndGet(), false);

        boolean isSymmetricLaminateNeeded = false;

        for (MinimalReserveFactorCalculator calcs : calculators) {
            isSymmetricLaminateNeeded = calcs.isSymmetricLaminateNeeded();
            if (isSymmetricLaminateNeeded) {
                break;
            }
        }

        laminat.setSymmetric(isSymmetricLaminateNeeded || input.isSymmetricLaminat());
        
        // Bestimmung der maximalen Lagenanzahl durch Sequential Decision Approach
        
        SequentialDecisionApproach sda = new SequentialDecisionApproach(input);
        
        Laminat sdaLam = sda.internalOptimize();

        int numberOfCheckedLaminates = sda.getResult().getNumberOfCheckedLaminates();
        int numberOfConstraintEvals = sda.getResult().getNumberOfContraintEvaluations();
        
        result.setBestLaminate(sdaLam.getCopyWithoutListener(false));
        result.setMinReserveFactor(sda.getResult().getMinReserveFactor());
        result.setNumberOfCheckedLaminates(numberOfCheckedLaminates);
        result.setNumberOfContraintEvaluations(numberOfConstraintEvals);
        
        atomicLaminateCounter.decrementAndGet();
        
        //int maxLayerNum = sdaLam.getNumberofLayers();
        //System.out.println("maximale Lagenanzahl : " + maxLayerNum);
        
        // Bestimmung der minimalen Lagenanzahl mittels Superlagen
        
        double minResFac = 0.0;
        while(minResFac < 1.0){
        
            laminat.addLayer(superLayer.getCopyWithoutListeners(superLayer.getAngle()));
            
            minResFac = Double.MAX_VALUE;
            for (MinimalReserveFactorCalculator calcs : calculators) {
                minResFac = Math.min(minResFac, calcs.getMinimalReserveFactor(laminat));
                numberOfConstraintEvals++;
            }            
            
            numberOfCheckedLaminates++;
        
        }
            
        result.setNumberOfCheckedLaminates(numberOfCheckedLaminates);
        result.setNumberOfContraintEvaluations(numberOfConstraintEvals);
        
        //int minLayerNum = laminat.getNumberofLayers();
        //System.out.println("minimale Lagenanzahl : " + minLayerNum);
        
        ArrayList<Individuum> allIndivs = new ArrayList<>();
        
        
        allIndivs.add(new Individuum(laminat.getCopyWithoutListener(false),0));
        ArrayList<Individuum> allIndivsOld;
        
        while(true){
            
            //System.out.println("Anzahl an Individuen vorher: " + allIndivs.size());
            //System.out.println("Lagenanzahl : " + allIndivs.get(0).laminat.getNumberofLayers());
        
            allIndivsOld = allIndivs;
            allIndivs = new ArrayList<>();

            for (Individuum individuum : allIndivsOld) {

                // Ändern der äußersten Superlage
                Individuum[] indivs = createSubLaminates(individuum, baseLayer, angles);

                for (Individuum indiv : indivs) {
                    minResFac = Double.MAX_VALUE;
                    for (MinimalReserveFactorCalculator calcs : calculators) {
                        minResFac = Math.min(minResFac, calcs.getMinimalReserveFactor(indiv.getLaminat()));
                        numberOfConstraintEvals++;
                    }
                    numberOfCheckedLaminates++;
                    /*String angleStr = "[";
                    String separator = "";
                    for (Layer l : indivs[ii].getLaminat().getLayers()){
                    angleStr += separator + (l.getName().equals("Superlayer") ? "SL" : ("" + l.getAngle()));
                    separator = "/";
                    }
                    angleStr += "]_S";
                    System.out.println("minimaler Reserverfaktor für Entwurf : " + minResFac + " - " +  angleStr);*/
                    indiv.setMinReserverFactor(minResFac);
                    if (minResFac >= 1.0) {
                        allIndivs.add(indiv);
                    }
                }
                
                result.setNumberOfCheckedLaminates(numberOfCheckedLaminates);
                result.setNumberOfContraintEvaluations(numberOfConstraintEvals);
            }
            
            /*System.out.println("Anzahl an Individuen nachher: " + allIndivs.size());
            
            if (!allIndivs.isEmpty()) {
                System.out.println("" + allIndivs.get(0).outestSuperLayerPosition + " ==  "  + allIndivs.get(0).laminat.getNumberofLayers());
            }*/
            
            if (!allIndivs.isEmpty() && allIndivs.get(0).outestSuperLayerPosition == (allIndivs.get(0).laminat.isSymmetric() ? allIndivs.get(0).laminat.getNumberofLayers()/2 : allIndivs.get(0).laminat.getNumberofLayers())){
                //System.out.println("Bestes gefunden");
                break;
            }

            if (allIndivs.isEmpty()){
                laminat.addLayer(superLayer.getCopyWithoutListeners(superLayer.getAngle()));
                allIndivs.add(new Individuum(laminat.getCopyWithoutListener(false),0));
                //System.out.println("mehr Lagen (" + laminat.getNumberofLayers() + ")");
            }
        
        }
        
        Individuum bestIndiv = allIndivs.get(0);
        for (Individuum individuum : allIndivs) {
            //System.out.println("min. Reservefaktor" + individuum.minReserverFactor);
            if (bestIndiv.minReserverFactor < individuum.minReserverFactor){
                bestIndiv = individuum;
            }
        }
        
        result.setBestLaminate(bestIndiv.laminat.getCopyWithoutListener(false));
        result.setMinReserveFactor(bestIndiv.minReserverFactor);
        result.setNumberOfCheckedLaminates(numberOfCheckedLaminates);
        result.setNumberOfContraintEvaluations(numberOfConstraintEvals);
        
        return bestIndiv.laminat;
    }
    
    private Individuum[] createSubLaminates(Individuum indiv, Layer baseLayer, double[] angles){
        Individuum[] newIndivs = new Individuum[angles.length];
        for(int ii = 0; ii < angles.length; ii++){
            Laminat lam = indiv.getLaminat().getCopyWithoutListener(false);
            Layer newLayer = baseLayer.getCopyWithoutListeners(baseLayer.getAngle());
            newLayer.setAngle(angles[ii]);
            lam.setLayer(indiv.getOutestSuperLayerPosition(), newLayer);
            newIndivs[ii] = new Individuum(lam,indiv.getOutestSuperLayerPosition()+1);
        }
        return newIndivs;
    }

    @Override
    public Optimizer getInstance(OptimizationInput input) {
        return new TodorokiOptimizer(input);
    }

    @Override
    public boolean onlySymmetricLaminates() {
        return true;
    }
    
    private Layer getSuperlayer(){
        double EPar = input.getMaterial().getEpar();
        double nue  = 0.0;
        double G    = EPar/(2.0*(1.0+nue));
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
        superMaterial.setRShear(2.0*RPar/3.0);
        
        Criterion criterion = null;
        Lookup lkp = Lookups.forPath("elamx/failurecriteria");
            Collection<? extends Criterion> c = lkp.lookupAll(Criterion.class);
            for (Criterion crit : c) {
                if (crit instanceof MaxStress) {
                    criterion = crit;
                    break;
                }
            }
        
        return new Layer("", "Superlayer", superMaterial,  0.0, input.getThickness(), criterion);
    }
    
    private class Individuum{
        
        private final Laminat laminat;
        private double minReserverFactor = 0.0;
        private int outestSuperLayerPosition = 0;

        public Individuum(Laminat laminat, int outestSuperLayerPosition) {
            this.laminat = laminat;
            this.outestSuperLayerPosition = outestSuperLayerPosition;
        }
        /**
         * Get the value of outestSuperLayerPosition
         *
         * @return the value of outestSuperLayerPosition
         */
        public int getOutestSuperLayerPosition() {
            return outestSuperLayerPosition;
        }

        /**
         * Set the value of outestSuperLayerPosition
         *
         * @param outestSuperLayerPosition new value of outestSuperLayerPosition
         */
        public void setOutestSuperLayerPosition(int outestSuperLayerPosition) {
            this.outestSuperLayerPosition = outestSuperLayerPosition;
        }

        /**
         * Get the value of minReserverFactor
         *
         * @return the value of minReserverFactor
         */
        public double getMinReserverFactor() {
            return minReserverFactor;
        }

        /**
         * Set the value of minReserverFactor
         *
         * @param minReserverFactor new value of minReserverFactor
         */
        public void setMinReserverFactor(double minReserverFactor) {
            this.minReserverFactor = minReserverFactor;
        }

        /**
         * Get the value of laminat
         *
         * @return the value of laminat
         */
        public Laminat getLaminat() {
            return laminat;
        }

    }
    
}
