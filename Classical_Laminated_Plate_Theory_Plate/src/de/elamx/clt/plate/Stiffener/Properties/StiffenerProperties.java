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
package de.elamx.clt.plate.Stiffener.Properties;

import de.elamx.clt.plate.Input;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class StiffenerProperties {

    public static final int X_DIRECTION = 1;
    public static final int Y_DIRECTION = 2;
    public static final String X_DIRECTION_STRING = "x";
    public static final String Y_DIRECTION_STRING = "y";
    
    private int direction;
    public static final String PROP_DIRECTION = "direction";
    private String name;
    public static final String PROP_NAME = "name";
    private double position;
    public static final String PROP_POSITION = "position";
    
    private Input parent;

    public StiffenerProperties(String name, int direction, double position) {
        this.name = name;
        this.direction = direction;
        this.position = position;
    }
    
    public void setParent(Input parent){
        this.parent = parent;
    }
    
    public void detach(){
        parent.removeStiffenerProperty(this);
    }

    /**
     * Get the value of position
     *
     * @return the value of position
     */
    public double getPosition() {
        return position;
    }

    /**
     * Set the value of position
     *
     * @param position new value of position
     */
    public void setPosition(double position) {
        double oldPosition = this.position;
        this.position = position;
        propertyChangeSupport.firePropertyChange(PROP_POSITION, oldPosition, position);
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    /**
     * Get the value of direction
     *
     * @return the value of direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Set the value of direction
     *
     * @param direction new value of direction
     */
    public void setDirection(int direction) {
        int oldDirection = this.direction;
        this.direction = direction;
        propertyChangeSupport.firePropertyChange(PROP_DIRECTION, oldDirection, direction);
    }

    public abstract double getE();

    public abstract double getI();

    public abstract double getG();

    public abstract double getJ();

    public abstract double getZ();

    public abstract double getA();
    
    public abstract double getRho();

    public abstract StiffenerProperties getCopy();
    @SuppressWarnings("this-escape")
    protected transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
