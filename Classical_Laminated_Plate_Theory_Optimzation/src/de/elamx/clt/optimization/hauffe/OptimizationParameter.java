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

/**
 *
 * @author Andreas Hauffe
 */
public class OptimizationParameter {
    
    private double mutationswahrscheinlichkeit = 0.3;
    private int    deltaMaxLayerNum            = 0;
    private int    maxLayerNum                 = Integer.MAX_VALUE-deltaMaxLayerNum;
    //private int    maxLayerNum                 = 100;
    private int    minLayerNum                 = 1;
    private int    maxGenerations              = 6000;
    private int    anzEltern                   = 60;
    private int    anzKinder                   = 60;
    private int    stopGens                    = 6000;
    private double shiftwahrscheinlichkeit     = 0.2;
    private double maxGenerationOhneAenderung  = 400;

    public OptimizationParameter() {
    }

    public double getMutationswahrscheinlichkeit() {
        return mutationswahrscheinlichkeit;
    }

    public void setMutationswahrscheinlichkeit(double mutationswahrscheinlichkeit) {
        this.mutationswahrscheinlichkeit = mutationswahrscheinlichkeit;
    }

    public int getDeltaMaxLayerNum() {
        return deltaMaxLayerNum;
    }

    public void setDeltaMaxLayerNum(int deltaMaxLayerNum) {
        this.deltaMaxLayerNum = deltaMaxLayerNum;
    }

    public int getMaxLayerNum() {
        return maxLayerNum;
    }

    public void setMaxLayerNum(int maxLayerNum) {
        this.maxLayerNum = maxLayerNum;
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }

    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    public int getAnzEltern() {
        return anzEltern;
    }

    public void setAnzEltern(int anzEltern) {
        this.anzEltern = anzEltern;
    }

    public int getAnzKinder() {
        return anzKinder;
    }

    public void setAnzKinder(int anzKinder) {
        this.anzKinder = anzKinder;
    }

    public int getStopGens() {
        return stopGens;
    }

    public void setStopGens(int stopGens) {
        this.stopGens = stopGens;
    }

    public int getMinLayerNum() {
        return minLayerNum;
    }

    public void setMinLayerNum(int minLayerNum) {
        if (minLayerNum < 1){
            this.minLayerNum = 1;
        }else{
            this.minLayerNum = minLayerNum;
        }
    }

    public double getShiftwahrscheinlichkeit() {
        return shiftwahrscheinlichkeit;
    }

    public void setShiftwahrscheinlichkeit(double shiftwahrscheinlichkeit) {
        this.shiftwahrscheinlichkeit = shiftwahrscheinlichkeit;
    }

    public double getMaxGenerationOhneAenderung() {
        return maxGenerationOhneAenderung;
    }

    public void setMaxGenerationOhneAenderung(double maxGenerationOhneAenderung) {
        this.maxGenerationOhneAenderung = maxGenerationOhneAenderung;
    }
    
}
