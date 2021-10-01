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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andreas Hauffe
 */
public class GEP {
    
    private static Individuum getRandomIndividuum(Individuum[] indivs){
        return indivs[(int)(indivs.length*Math.random())];
    }
    
    public static int getRandomInteger(int upperBound){
        return (int)(upperBound*Math.random());
    }
    
    public static int getRandomInteger(int lowerBound, int upperBound){
        return lowerBound + (int)((upperBound-lowerBound)*Math.random());
    }
    
    public static void onePointCrossover(Individuum[] eltern, Individuum[] kinder, int start, int ende){
        
        if ((ende-start+1)%2 != 0){
            System.err.println("Ungerade Anzahl von Kindern für onePointCrossover");
            return;
        }
        
        for (int ii = start; ii <= ende; ii+=2){
            kinder[ii]   = getRandomIndividuum(eltern).copy();
            kinder[ii+1] = getRandomIndividuum(eltern).copy();
            
            int crosspoint = getRandomInteger(kinder[ii].getMaxLayerNum());
            
            double[] anglesK1 = kinder[ii].getAngles();
            double[] anglesK2 = kinder[ii+1].getAngles();
            double temp;
            
            for (int jj = crosspoint; jj < anglesK1.length; jj++){
                temp = anglesK1[jj];
                anglesK1[jj] = anglesK2[jj];
                anglesK2[jj] = temp;
            }
        }
    }
    
    public static void twoPointCrossover(Individuum[] eltern, Individuum[] kinder, int start, int ende){
        
        if ((ende-start+1)%2 != 0){
            System.err.println("Ungerade Anzahl von Kindern für onePointCrossover");
            return;
        }
        
        for (int ii = start; ii <= ende; ii+=2){
            kinder[ii]   = getRandomIndividuum(eltern).copy();
            kinder[ii+1] = getRandomIndividuum(eltern).copy();
            
            int crosspoint1 = getRandomInteger(kinder[ii].getMaxLayerNum());
            int crosspoint2 = crosspoint1+getRandomInteger(kinder[ii].getMaxLayerNum()-crosspoint1);
            
            double[] anglesK1 = kinder[ii].getAngles();
            double[] anglesK2 = kinder[ii+1].getAngles();
            double temp;
            
            for (int jj = crosspoint1; jj < crosspoint2; jj++){
                temp = anglesK1[jj];
                anglesK1[jj] = anglesK2[jj];
                anglesK2[jj] = temp;
            }
        }
    }
    
    public static void mutation(OptimizationParameter params, Individuum[] eltern, Individuum[] kinder, int start, int ende, double[] possibleAngles){
        
        for (int ii = start; ii <= ende; ii++){
            kinder[ii]   = getRandomIndividuum(eltern).copy();
            
            if (Math.random() <= params.getMutationswahrscheinlichkeit()){
                kinder[ii].setNumLayers(getRandomInteger(params.getMinLayerNum(), params.getMaxLayerNum()));
            }
            
            double[] angles = kinder[ii].getAngles();
            
            for (int jj = 0; jj < angles.length; jj++){
                if (Math.random() <= params.getMutationswahrscheinlichkeit()){
                    angles[jj] = possibleAngles[getRandomInteger(possibleAngles.length)];
                }
            }
        }
    }
    
    public static void permutation(Individuum[] eltern, Individuum[] kinder, int start, int ende){
        for (int ii = start; ii <= ende; ii++){
            kinder[ii] = getRandomIndividuum(eltern).copy();
            
            ArrayList<Double> anglesVec = new ArrayList<>();
            
            for (double d : kinder[ii].getAngles()){
                anglesVec.add(d);
            }
            
            double[] angles = kinder[ii].getAngles();
            
            for (int jj = 0; jj < angles.length; jj++){
                angles[jj] = anglesVec.remove(getRandomInteger(anglesVec.size()));
            }
        }
    }
    
    public static void angleShifts(OptimizationParameter params, Individuum[] eltern, Individuum[] kinder, int start, int ende, double[] possibleAngles){
        double[] sortedAngle = new double[possibleAngles.length];
        System.arraycopy(possibleAngles, 0, sortedAngle, 0, possibleAngles.length);
        Map<Double, Integer> angleIndexMap = new HashMap<>();
        
        for(int ii = 0; ii < sortedAngle.length; ii++){
            angleIndexMap.put(sortedAngle[ii], ii);
        }
        
        for (int ii = start; ii <= ende; ii++){
            kinder[ii]   = getRandomIndividuum(eltern).copy();
            
            if (Math.random() <= params.getMutationswahrscheinlichkeit()){
                kinder[ii].setNumLayers(getRandomInteger(params.getMinLayerNum(), params.getMaxLayerNum()));
            }
            
            double[] angles = kinder[ii].getAngles();
            
            for (int jj = 0; jj < angles.length; jj++){
                if (Math.random() <= params.getShiftwahrscheinlichkeit()){
                    int newIndex = angleIndexMap.get(angles[jj]) + (int)Math.signum(Math.random()-0.5);
                    if (newIndex == sortedAngle.length){
                        newIndex = 0;
                    }
                    if (newIndex == -1){
                        newIndex = sortedAngle.length-1;
                    }
                    angles[jj] = sortedAngle[newIndex];
                }
            }
        }
    }
    
    public static Individuum selection(Individuum[] eltern, Individuum[] kinder){
        return strGASelection(eltern, kinder);
    }
    
    private static Individuum strGASelection(Individuum[] eltern, Individuum[] kinder){
        Individuum[] eltern_save = new Individuum[eltern.length];
        System.arraycopy(eltern, 0, eltern_save, 0, eltern.length);
        for (Individuum kinder1 : kinder) {
            int index = -1;
            int minDist = Integer.MAX_VALUE;
            for (int jj = 0; jj < eltern.length; jj++) {
                int dist = getDist(kinder1, eltern[jj]);
                if (dist < minDist){
                    minDist = dist;
                    index = jj;
                }
            }
            if (eltern[index].getMinReserveFactor() < 1.0 && kinder1.getMinReserveFactor() > eltern[index].getMinReserveFactor() || kinder1.getNumLayers() < eltern[index].getNumLayers() && (kinder1.getMinReserveFactor() > 1.0 || kinder1.getMinReserveFactor() > eltern[index].getMinReserveFactor()) || kinder1.getNumLayers() == eltern[index].getNumLayers() && kinder1.getMinReserveFactor() > eltern[index].getMinReserveFactor()) {
                eltern[index] = kinder1;
            }
        }
        Individuum bestIndiv = eltern[0];
        if (bestIndiv.getMinReserveFactor() < 1.0){
            for (Individuum individuum : eltern){
                if (individuum.getMinReserveFactor() >= 1.0){
                    bestIndiv = individuum;
                    break;
                }
            }
        }
        for (Individuum individuum : eltern){
            if (individuum.getNumLayers() < bestIndiv.getNumLayers() && individuum.getMinReserveFactor() >= 1.0){
                bestIndiv = individuum;
            }else if (individuum.getNumLayers() == bestIndiv.getNumLayers() && individuum.getMinReserveFactor() > bestIndiv.getMinReserveFactor()){
                bestIndiv = individuum;
            }
        }
        /*if (bestIndiv.getMinReserveFactor() < 1.0){
            for (int ii = 0; ii < eltern_save.length; ii++){
                System.out.println("eltern_save: " + ii + " " + eltern_save[ii].getNumLayers() + " " + eltern_save[ii].getMinReserveFactor());
            }
            for (int ii = 0; ii < eltern.length; ii++){
                System.out.println("eltern: " + ii + " " + eltern[ii].getNumLayers() + " " + eltern[ii].getMinReserveFactor());
            }
        }*/
        return bestIndiv;
    }
    
    private static int getDist(Individuum indiv1, Individuum indiv2){
        
        int minNumLayer = Math.min(indiv1.getNumLayers(), indiv2.getNumLayers());
        int maxNumLayer = Math.max(indiv1.getNumLayers(), indiv2.getNumLayers());
        
        double[] angles1 = indiv1.getAngles();
        double[] angles2 = indiv2.getAngles();
        
        int dist = 0;
        for (int ii = 0; ii < minNumLayer; ii++){
            if (angles1[ii] != angles2[ii]){
                dist++;
            }
        }
        dist += (maxNumLayer - minNumLayer);
        
        return dist;
    }
    
    /*private static Individuum rouletteRadSelection(Individuum[] eltern, Individuum[] kinder){
        Individuum[] all = new Individuum[eltern.length+kinder.length];
        System.arraycopy(eltern, 0, all, 0, eltern.length);
        System.arraycopy(kinder, 0, all, eltern.length, kinder.length);
        
        double temp = 0.0;
        double minObjective = Double.MAX_VALUE;
        Individuum bestIndiv = null;
        for (Individuum individuum : all) {
            temp += individuum.getObjective();
            if (individuum.getObjective() < minObjective){
                minObjective = individuum.getObjective();
                bestIndiv = individuum;
            }
        }
        
        double[] auswahlWahrsch = new double[all.length];
        auswahlWahrsch[0] = all[0].getObjective()/temp;
        for (int ii = 1; ii < all.length; ii++){
            auswahlWahrsch[ii] = all[ii].getObjective()/temp;
        }
        
        eltern[0] = bestIndiv;
        
        int jj = 1;
        while (jj < eltern.length){
            double zz = Math.random();
            for (int ii = 0; ii < auswahlWahrsch.length; ii++){
                if (zz <= auswahlWahrsch[ii]){
                    eltern[jj] = all[ii];
                }
            }
            jj++;
        }
        
        return bestIndiv;
    }*/
}
