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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas Hauffe
 */
public class OptimizationResult {
    
    public static final String PROP_BESTLAMINATE = "bestLaminate";
    public static final String PROP_MINRESERVEFACTOR = "minReserveFactor";
    public static final String PROP_NUMBEROFCHECKEDLAMINATES = "numberOfCheckedLaminates";
    public static final String PROP_NUMBEROFCONTRAINTEVALUATIONS = "numberOfContraintEvaluations";
    public static final String PROP_FINISHED = "finished";
    public static final String PROP_GENERATIONOFLASTCHANGE = "generationOfLastChange";
    public static final String PROP_ALLRESULTS = "allResults";

    private Laminat bestLaminate;
    private double minReserveFactor;
    private int numberOfCheckedLaminates;
    private int numberOfContraintEvaluations;
    private boolean finished;
    private int generationOfLastChange;

    public OptimizationResult() {
        this(false);
    }

    public OptimizationResult(boolean finished) {
        this.finished = finished;
    }
    

    /**
     * Get the value of finished
     *
     * @return the value of finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Set the value of finished
     *
     * @param finished new value of finished
     */
    public void setFinished(boolean finished) {
        boolean oldFinished = this.finished;
        this.finished = finished;
        propertyChangeSupport.firePropertyChange(PROP_FINISHED, oldFinished, finished);
    }

    /**
     * Get the value of numberOfContraintEvaluations
     *
     * @return the value of numberOfContraintEvaluations
     */
    public int getNumberOfContraintEvaluations() {
        return numberOfContraintEvaluations;
    }

    /**
     * Set the value of numberOfContraintEvaluations
     *
     * @param numberOfContraintEvaluations new value of
     * numberOfContraintEvaluations
     */
    public void setNumberOfContraintEvaluations(int numberOfContraintEvaluations) {
        int oldNumberOfContraintEvaluations = this.numberOfContraintEvaluations;
        this.numberOfContraintEvaluations = numberOfContraintEvaluations;
        propertyChangeSupport.firePropertyChange(PROP_NUMBEROFCONTRAINTEVALUATIONS, oldNumberOfContraintEvaluations, numberOfContraintEvaluations);
    }

    /**
     * Get the value of numberOfCheckedLaminates
     *
     * @return the value of numberOfCheckedLaminates
     */
    public int getNumberOfCheckedLaminates() {
        return numberOfCheckedLaminates;
    }

    /**
     * Set the value of numberOfCheckedLaminates
     *
     * @param numberOfCheckedLaminates new value of numberOfCheckedLaminates
     */
    public void setNumberOfCheckedLaminates(int numberOfCheckedLaminates) {
        int oldNumberOfCheckedLaminates = this.numberOfCheckedLaminates;
        this.numberOfCheckedLaminates = numberOfCheckedLaminates;
        propertyChangeSupport.firePropertyChange(PROP_NUMBEROFCHECKEDLAMINATES, oldNumberOfCheckedLaminates, numberOfCheckedLaminates);
    }

    /**
     * Get the value of minReserveFactor
     *
     * @return the value of minReserveFactor
     */
    public double getMinReserveFactor() {
        return minReserveFactor;
    }

    /**
     * Set the value of minReserveFactor
     *
     * @param minReserveFactor new value of minReserveFactor
     */
    public void setMinReserveFactor(double minReserveFactor) {
        double oldMinReserveFactor = this.minReserveFactor;
        this.minReserveFactor = minReserveFactor;
        propertyChangeSupport.firePropertyChange(PROP_MINRESERVEFACTOR, oldMinReserveFactor, minReserveFactor);
    }

    /**
     * Get the value of bestLaminate
     *
     * @return the value of bestLaminate
     */
    public Laminat getBestLaminate() {
        return bestLaminate;
    }

    /**
     * Set the value of bestLaminate
     *
     * @param bestLaminate new value of bestLaminate
     */
    public void setBestLaminate(Laminat bestLaminate) {
        Laminat oldBestLaminate = this.bestLaminate;
        this.bestLaminate = bestLaminate;
        propertyChangeSupport.firePropertyChange(PROP_BESTLAMINATE, oldBestLaminate, bestLaminate);
    }

    /**
     * Get the value of minReserveFactor
     *
     * @return the value of minReserveFactor
     */
    public int getGenerationOfLastChange() {
        return generationOfLastChange;
    }

    /**
     * Set the value of minReserveFactor
     *
     * @param generationOfLastChange new value of minReserveFactor
     */
    public void setGenerationOfLastChange(int generationOfLastChange) {
        double oldGenerationOfLastChange = this.generationOfLastChange;
        this.generationOfLastChange = generationOfLastChange;
        propertyChangeSupport.firePropertyChange(PROP_GENERATIONOFLASTCHANGE, oldGenerationOfLastChange, generationOfLastChange);
    }
    
    public void setNewResults(Laminat bestLaminate, int numberOfContraintEvaluations, int numberOfCheckedLaminates, double minReserveFactor){
        this.bestLaminate = bestLaminate;
        this.numberOfContraintEvaluations = numberOfContraintEvaluations;
        this.numberOfCheckedLaminates = numberOfCheckedLaminates;
        this.minReserveFactor = minReserveFactor;
        propertyChangeSupport.firePropertyChange(PROP_ALLRESULTS, null, bestLaminate);
    }
    
    public void setNewResults(Laminat bestLaminate, int numberOfContraintEvaluations, int numberOfCheckedLaminates, double minReserveFactor, int generationOfLastChange){
        this.bestLaminate = bestLaminate;
        this.numberOfContraintEvaluations = numberOfContraintEvaluations;
        this.numberOfCheckedLaminates = numberOfCheckedLaminates;
        this.minReserveFactor = minReserveFactor;
        this.generationOfLastChange = generationOfLastChange;
        propertyChangeSupport.firePropertyChange(PROP_ALLRESULTS, null, bestLaminate);
    }

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
