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
package de.elamx.clt.cutout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author raedel
 */
public class CutoutInput implements PropertyChangeListener {

    public static final String PROP_GEOMETRY = "GEO";
    public static final String PROP_NXX = "NXX";
    public static final String PROP_NYY = "NYY";
    public static final String PROP_NXY = "NXY";
    public static final String PROP_MXX = "MXX";
    public static final String PROP_MYY = "MYY";
    public static final String PROP_MXY = "MXY";
    public static final String PROP_VALUES = "Values";

    private int values;
    private CutoutGeometry cg;
    private Loads loads;

    private final boolean notify = true;
    private final transient PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

    public CutoutInput() {
        this(new CircularCutoutGeometry(NbBundle.getMessage(CircularCutoutGeometry.class, "CircularCutoutGeometry.description"), 1.0), 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Cutout.getNumWerte());
    }

    public CutoutInput(CutoutGeometry cg, double nxx, double nyy, double nxy,
            double mxx, double myy, double mxy,
            int values) {
        this(cg, new Loads(nxx, nyy, nxy, mxx, myy, mxy), values);
    }

    public CutoutInput(CutoutGeometry cg, Loads loads, int values) {
        setCutoutGeometry(cg);
        this.loads = loads;
        this.values = values;
    }

    /**
     * @param val plate force resultant in x-direction
     */
    public final void setCutoutGeometry(CutoutGeometry val) {
        CutoutGeometry oldval = cg;
        cg = val;
        cg.addPropertyChangeListener(this);
        firePropertyChange(PROP_GEOMETRY, oldval, cg);
        if (oldval != null) {
            oldval.removePropertyChangeListener(this);
        }
    }

    public CutoutGeometry getCutoutGeometry() {
        return cg;
    }

    /**
     * @param val plate force resultant in x-direction
     */
    public void setNXX(double val) {
        double oldval = loads.getNxx();
        loads.setNxx(val);
        firePropertyChange(PROP_NXX, oldval, loads.getNxx());
    }

    /**
     * @return plate force resultant in x-direction
     */
    public double getNXX() {
        return loads.getNxx();
    }

    /**
     * @param val plate force resultant in y-direction
     */
    public void setNYY(double val) {
        double oldval = loads.getNyy();
        loads.setNyy(val);
        firePropertyChange(PROP_NYY, oldval, loads.getNyy());
    }

    /**
     * @return plate force resultant in y-direction
     */
    public double getNYY() {
        return loads.getNyy();
    }

    /**
     * @param val plate shear force resultant
     */
    public void setNXY(double val) {
        double oldval = loads.getNxy();
        loads.setNxy(val);
        firePropertyChange(PROP_NXY, oldval, loads.getNxy());
    }

    /**
     * @return plate shear force resultant
     */
    public double getNXY() {
        return loads.getNxy();
    }

    /**
     * @param val plate moment resultant in x-direction
     */
    public void setMXX(double val) {
        double oldval = loads.getMxx();
        loads.setMxx(val);
        firePropertyChange(PROP_MXX, oldval, loads.getMxx());
    }

    /**
     * @return plate force resultant in x-direction
     */
    public double getMXX() {
        return loads.getMxx();
    }

    /**
     * @param val plate force resultant in y-direction
     */
    public void setMYY(double val) {
        double oldval = loads.getMyy();
        loads.setMyy(val);
        firePropertyChange(PROP_MYY, oldval, loads.getMyy());
    }

    /**
     * @return plate moment resultant in y-direction
     */
    public double getMYY() {
        return loads.getMyy();
    }

    /**
     * @param val plate shear force resultant
     */
    public void setMXY(double val) {
        double oldval = loads.getMxy();
        loads.setMxy(val);
        firePropertyChange(PROP_MXY, oldval, loads.getMxy());
    }

    /**
     * @return plate twist moment resultant
     */
    public double getMXY() {
        return loads.getMxy();
    }

    /**
     * @param val number of values
     */
    public void setValues(int val) {
        int oldval = values;
        values = val;
        firePropertyChange(PROP_MXY, oldval, values);
    }

    /**
     * @return Number of values over hole angle 360°
     */
    public int getValues() {
        return values;
    }

    public double[] getForces() {
        return loads.getForces();
    }

    public double[] getMoments() {
        return loads.getMoments();
    }

    public double[] getLoads() {
        return loads.getLoads();
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

    public CutoutInput copy() {
        return new CutoutInput(cg.getCopy(), loads.getNxx(), loads.getNyy(), loads.getNxy(),
                loads.getMxx(), loads.getMyy(), loads.getMxy(),
                values);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange("test", 0, 1);
    }

}
