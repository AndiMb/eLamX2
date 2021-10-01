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

import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 *
 * @author Andreas Hauffe
 */
public class OptimizationInput {
    
    public static final String PROP_ANGLES = "ANGLES";
    public static final String PROP_THICKNESS = "THICKNESS";
    public static final String PROP_MATERIAL = "MATERIAL";
    public static final String PROP_CRITERION = "CRITERION";
    public static final String PROP_SYMMETRY = "SYMMETRY";
    public static final String PROP_OPTIMIZER = "OPTIMIZER";
    public static final String PROP_CALCULATORS = "CALCULATORS";
    
    private double[] angles;
    private double   thickness;
    private LayerMaterial material;
    private Criterion criterion;
    private ArrayList<MinimalReserveFactorCalculator> calculators;
    private boolean symmetricLaminat;

    private final boolean notify = true;
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    
    public OptimizationInput(){
        this(null, 0.0, null, null, null, false);
    }

    public OptimizationInput(double[] angles, double thickness, LayerMaterial material, Criterion criterion, ArrayList<MinimalReserveFactorCalculator> calculators, boolean symmetricLaminat) {
        this.angles = angles;
        this.thickness = thickness;
        this.material = material;
        this.criterion = criterion;
        this.calculators = calculators;
        this.symmetricLaminat = symmetricLaminat;
    }
    
    public double[] getAngles() {
        return angles;
    }

    public void setAngles(double[] angles) {
        double[] oldAngles = this.angles;
        this.angles = angles;
        firePropertyChange(PROP_ANGLES, oldAngles, this.angles);
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        double oldThickness = this.thickness;
        this.thickness = thickness;
        firePropertyChange(PROP_ANGLES, oldThickness, this.thickness);
    }

    public LayerMaterial getMaterial() {
        return material;
    }

    public void setMaterial(LayerMaterial material) {
        LayerMaterial oldMaterial = this.material;
        this.material = material;
        firePropertyChange(PROP_MATERIAL, oldMaterial, this.material);
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        Criterion oldCriterion = criterion;
        this.criterion = criterion;
        firePropertyChange(PROP_CRITERION, oldCriterion, this.criterion);
        
    }

    public ArrayList<MinimalReserveFactorCalculator> getCalculators() {
        return calculators;
    }
    
    public void setCalculators(ArrayList<MinimalReserveFactorCalculator> newCalcs){
        calculators.clear();
        calculators.addAll(newCalcs);
        firePropertyChange(PROP_CALCULATORS, null, calculators);
    }

    public boolean isSymmetricLaminat() {
        return symmetricLaminat;
    }

    public void setSymmetricLaminat(boolean symmetricLaminat) {
        boolean oldSymmetricLaminat = this.symmetricLaminat;
        this.symmetricLaminat = symmetricLaminat;
        firePropertyChange(PROP_SYMMETRY, oldSymmetricLaminat, this.symmetricLaminat);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param property
     * @param listener
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (notify) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (notify) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        if (notify) {
            propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }
    }

    public OptimizationInput copy() {
        double[] newAngles = new double[angles.length];
        System.arraycopy(angles, 0, newAngles, 0, angles.length);
        
        ArrayList<MinimalReserveFactorCalculator> newCalculators = new ArrayList<>();
        for (MinimalReserveFactorCalculator calc : calculators) {
            newCalculators.add(calc.getCopy());
        }
        
        return new OptimizationInput(newAngles, thickness, material, criterion, newCalculators, symmetricLaminat);
    }    
}
