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
package de.elamx.clt.plate;

import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.WeakListeners;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class Input{

    public static final String PROP_LENGTH = "length";
    public static final String PROP_WIDTH = "width";
    public static final String PROP_WHOLED = "wholeD";
    public static final String PROP_BCX = "bcx";
    public static final String PROP_BCY = "bcy";
    public static final String PROP_M = "m";
    public static final String PROP_N = "n";
    public static final String PROP_STIFF_PROP = "PROP_STIFF_PROP";
    private double  length;
    private double  width;
    private boolean wholeD;
    private int     bcx;
    private int     bcy;
    private int     m;
    private int     n;
    private boolean notify = true;
    @SuppressWarnings("this-escape")
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    
    private final ArrayList<StiffenerProperties> stiffProps = new ArrayList<>();

    private final StiffenerChangeListener sCl = new StiffenerChangeListener();

    public Input(double length, double width, boolean wholeD,
                           int bcx, int bcy, int m, int n){
        this.length = length;
        this.width  = width;
        this.wholeD = wholeD;
        this.bcx    = bcx;
        this.bcy    = bcy;
        this.m      = m;
        this.n      = n;
    }

    /**
     * @return the length
     */
    public double getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(double length) {
        double oldLength = this.length;
        this.length = length;
        firePropertyChange(PROP_LENGTH, oldLength, length);
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        double oldWidth = this.width;
        this.width = width;
        firePropertyChange(PROP_WIDTH, oldWidth, width);
    }

    /**
     * @return the wholeD
     */
    public boolean isWholeD() {
        return wholeD;
    }

    /**
     * @param wholeD the wholeD to set
     */
    public void setWholeD(boolean wholeD) {
        boolean oldWholeD = this.wholeD;
        this.wholeD = wholeD;
        firePropertyChange(PROP_WHOLED, oldWholeD, wholeD);
    }

    /**
     * @return the bcx
     */
    public int getBcx() {
        return bcx;
    }

    /**
     * @param bcx the bcx to set
     */
    public void setBcx(int bcx) {
        int oldBcx = this.bcx;
        this.bcx = bcx;
        firePropertyChange(PROP_BCX, oldBcx, bcx);
    }

    /**
     * @return the bcy
     */
    public int getBcy() {
        return bcy;
    }

    /**
     * @param bcy the bcy to set
     */
    public void setBcy(int bcy) {
        int oldBcy = this.bcy;
        this.bcy = bcy;
        firePropertyChange(PROP_BCY, oldBcy, bcy);
    }

    /**
     * @return the m
     */
    public int getM() {
        return m;
    }

    /**
     * @param m the m to set
     */
    public void setM(int m) {
        int oldM = this.m;
        this.m = m;
        firePropertyChange(PROP_M, oldM, m);
    }

    /**
     * @return the n
     */
    public int getN() {
        return n;
    }

    /**
     * @param n the n to set
     */
    public void setN(int n) {
        int oldN = this.n;
        this.n = n;
        firePropertyChange(PROP_N, oldN, n);
    }
    
    public void addStiffenerProperty(StiffenerProperties prop){
        stiffProps.add(prop);
        prop.addPropertyChangeListener(WeakListeners.propertyChange(sCl, prop));
        prop.setParent(this);
        firePropertyChange(PROP_STIFF_PROP, null, prop);
    }
    
    public void removeStiffenerProperty(StiffenerProperties prop){
        stiffProps.remove(prop);
        prop.removePropertyChangeListener(sCl);
        firePropertyChange(PROP_STIFF_PROP, prop, null);
    }
    
    public List<StiffenerProperties> getStiffenerProperties(){
        return stiffProps;
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

    private class StiffenerChangeListener implements PropertyChangeListener{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            firePropertyChange(PROP_STIFF_PROP, null, evt.getSource());
        }
    }
    
    public abstract Input copy();
}
