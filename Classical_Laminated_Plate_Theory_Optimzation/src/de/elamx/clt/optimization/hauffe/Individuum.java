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
public class Individuum {
    
    private int numLayers;
    private double[] angles = new double[0];
    private double objective = Double.MAX_VALUE;
    private double minReserveFactor = 0.0;

    public Individuum(int numLayers, double[] angles) {
        this.numLayers = numLayers > 0 ? numLayers : 1;
        this.angles = angles;
    }

    /**
     * Get the value of numLayers
     *
     * @return the value of numLayers
     */
    public int getNumLayers() {
        return numLayers;
    }

    /**
     * Set the value of numLayers
     *
     * @param numLayers new value of numLayers
     */
    public void setNumLayers(int numLayers) {
        this.numLayers = numLayers > 0 ? numLayers : 1;
    }

    public void setAngles(double[] angles){
        this.angles = angles;
    }
    
    public double[] getAngles(){
        return angles;
    }
    
    public int getMaxLayerNum(){
        return angles.length;
    }
    
    public void setMaxLayerNum(int maxLayerNum){
        double[] newAngles = new double[maxLayerNum];
        
        System.arraycopy(angles, 0, newAngles, 0, maxLayerNum);
        
        angles = newAngles;
        
        if (numLayers > angles.length){
            numLayers = angles.length;
        }
    }

    public double getObjective() {
        return objective;
    }

    public void setObjective(double objective) {
        this.objective = objective;
    }

    public double getMinReserveFactor() {
        return minReserveFactor;
    }

    public void setMinReserveFactor(double minReserveFactor) {
        this.minReserveFactor = minReserveFactor;
    }
    
    public Individuum copy(){
        
        double[] anglesCopy = new double[angles.length];
        System.arraycopy(angles, 0, anglesCopy, 0, angles.length);
        
        return new Individuum(numLayers, anglesCopy);
        
    }
    
}
