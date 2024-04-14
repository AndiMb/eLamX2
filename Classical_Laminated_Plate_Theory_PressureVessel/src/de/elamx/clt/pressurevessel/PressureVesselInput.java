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
package de.elamx.clt.pressurevessel;

import de.elamx.clt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas Hauffe
 */
public class PressureVesselInput {
    
    public static final String PROP_PRESSURE = "Pressure";
    public static final String PROP_RADIUS = "Radius";
    public static final String PROP_RADIUSTYPE = "RadiusType";
    
    public static final int RADIUSTYPE_INNER = 1;
    public static final int RADIUSTYPE_MEAN  = 2;
    public static final int RADIUSTYPE_OUTER = 4;
    
    private final Loads load = new Loads();
    private final Strains strains = new Strains();
    private boolean[] useStrains = new boolean[]{false, false, false, true, true, true};
    
    private double pressure = 0.0;
    private double radius = 1.0;
    private int radiusType = RADIUSTYPE_MEAN;
    
    private boolean notify = true;

    public PressureVesselInput() {
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        double oldval = this.pressure;
        this.pressure = pressure;
        firePropertyChange(PROP_PRESSURE, oldval, this.pressure);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        double oldval = this.radius;
        this.radius = radius;
        firePropertyChange(PROP_RADIUS, oldval, this.radius);
    }
    
    public void setRadiusType(int type){
        int oldval = this.radiusType;
        this.radiusType = type;
        firePropertyChange(PROP_RADIUSTYPE, oldval, this.radiusType);
    }
    
    public int getRadiusType(){
        return radiusType;
    }
    
    public double getMeanRadius(double thickness){
        double meanRadius = radius;
        if (radiusType == RADIUSTYPE_INNER){
            meanRadius += thickness/2.0;
        }else if (radiusType == RADIUSTYPE_OUTER){
            meanRadius -= thickness/2.0;
        }
        return meanRadius;
    }
    
    private void recalcLoads(double thickness){
        double meanRadius = getMeanRadius(thickness);        
        load.setN_x((pressure * meanRadius)/2.0);
        load.setN_y(pressure * meanRadius);
        load.setN_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
    
    @SuppressWarnings("this-escape")
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
        if (notify) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }
    
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        if (notify) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
    
    public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }
    
    public PressureVesselInput copy(){
        PressureVesselInput input = new PressureVesselInput();
        
        input.setPressure(pressure);
        input.setRadius(radius);
        input.setRadiusType(radiusType);
        
        return input;
    }
    
    public Loads getLoad(double thickness){
        recalcLoads(thickness);
        return load;
    }
    
    public Strains getStrains(){
        return strains;
    }

    /**
     * Get the value of useStrains
     *
     * @return the value of useStrains
     */
    public boolean[] isUseStrains() {
        return useStrains;
    }
}
