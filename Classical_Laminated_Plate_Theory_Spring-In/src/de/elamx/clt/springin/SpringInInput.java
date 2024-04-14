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
package de.elamx.clt.springin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas Hauffe
 */
public class SpringInInput {
    
    public static final String PROP_MODEL = "model";
    public static final String PROP_ANGLE = "angle";
    public static final String PROP_RADIUS = "radius";
    public static final String PROP_ALPHAT_THICK = "alphat_thick";
    public static final String PROP_BASETEMP = "baseTemp";
    public static final String PROP_HARDENINGTEMP = "hardeningTemp";
    public static final String PROP_USEAUTOCALCALPHAT_THICK = "useAutoCalcAlphat_thick";
    public static final String PROP_ZERODEGASCIRCUMDIR = "zeroDegAsCircumDir";
    
    private SpringInModel model;
    private double angle;
    private double radius;
    private double alphat_thick;
    private double baseTemp;
    private double hardeningTemp;
    private boolean useAutoCalcAlphat_thick;
    private boolean zeroDegAsCircumDir;

    public SpringInInput(){
        this(new SimpleRadfordSpringInModel(), 90.0, 10.0, 3.0E-5, 25.0, 180.0, false, true);
    }

    public SpringInInput(SpringInModel model, double angle, double radius, double alphat_thick, 
            double baseTemp, double hardeningTemp, boolean useAutoCalcAlphat_thick, boolean zeroDegAsCircumDir) {
        this.model = model;
        this.radius = radius;
        this.alphat_thick = alphat_thick;
        this.angle = angle;
        this.baseTemp = baseTemp;
        this.hardeningTemp = hardeningTemp;
        this.useAutoCalcAlphat_thick = useAutoCalcAlphat_thick;
        this.zeroDegAsCircumDir = zeroDegAsCircumDir;
    }

    public SpringInModel getModel() {
        return model;
    }

    public void setModel(SpringInModel model) {
        SpringInModel oldModel = this.model;
        this.model = model;
        propertyChangeSupport.firePropertyChange(PROP_MODEL, oldModel, model);
    }

    /**
     * Liefert den Wärmeausdehnungskoeffizienten in Dickenrichtung des
     * Laminats.
     *
     * @return Wärmeausdehnungskoeffizient in Dickenrichtung
     */
    public double getAlphat_thick() {
        return alphat_thick;
    }

    /**
     * Setzen des Wärmeausdehnungskoeffizienten in Dickenrichtung des
     * Laminats.
     * 
     * @param alphat_thick Wärmeausdehnungskoeffizient in Dickenrichtung
     */
    public void setAlphat_thick(double alphat_thick) {
        double oldAlphat_thick = this.alphat_thick;
        this.alphat_thick = alphat_thick;
        propertyChangeSupport.firePropertyChange(PROP_ALPHAT_THICK, oldAlphat_thick, alphat_thick);
    }

    /**
     * Liefert den Radius der Winkelprobe zurück.
     *
     * @return Radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Setzen des Radius der Winkelprobe
     *
     * @param radius Radius
     */
    public void setRadius(double radius) {
        double oldRadius = this.radius;
        this.radius = radius;
        propertyChangeSupport.firePropertyChange(PROP_RADIUS, oldRadius, radius);
    }

    /**
     * Liefert den Winkel im Gradmaß zurück.
     * @return Winkel im Gradmaß
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Setzen des Winkels im Gradmaß.
     * @param angle Winkel im Gradmaß
     */
    public void setAngle(double angle) {
        double oldAngle = this.angle;
        this.angle = angle;
        propertyChangeSupport.firePropertyChange(PROP_ANGLE, oldAngle, angle);
    }

    /**
     * Liefert die Basistemperatur des Prozesses. Im Normalfall sollte es die
     * Raumtemperatur sein.
     * @return Basistemperatur
     */
    public double getBaseTemp() {
        return baseTemp;
    }

    /**
     * Setzen der Basistemperatur des Prozesses. Im Normalfall sollte es die
     * Raumtemperatur sein.
     * @param baseTemp 
     */
    public void setBaseTemp(double baseTemp) {
        double oldBaseTemp = baseTemp;
        this.baseTemp = baseTemp;
        propertyChangeSupport.firePropertyChange(PROP_BASETEMP, oldBaseTemp, baseTemp);
    }

    /**
     * Liefert die Härte-/Maximaltemperatur für den Prozess zurück.
     * @return Härte-/Maximaltemperatur
     */
    public double getHardeningTemp() {
        return hardeningTemp;
    }

    /**
     * Setzen der Härte-/Maximaltemperatur für den Prozess zurück.
     * @param hardeningTemp Härte-/Maximaltemperatur
     */
    public void setHardeningTemp(double hardeningTemp) {
        double oldHardeningTemp = this.hardeningTemp;
        this.hardeningTemp = hardeningTemp;
        propertyChangeSupport.firePropertyChange(PROP_HARDENINGTEMP, oldHardeningTemp, hardeningTemp);
    }

    public boolean isUseAutoCalcAlphat_thick() {
        return useAutoCalcAlphat_thick;
    }

    public void setUseAutoCalcAlphat_thick(boolean useAutoCalcAlphat_thick) {
        boolean oldUseAutoCalcAlphat_thick = this.useAutoCalcAlphat_thick;
        this.useAutoCalcAlphat_thick = useAutoCalcAlphat_thick;
        propertyChangeSupport.firePropertyChange(PROP_USEAUTOCALCALPHAT_THICK, oldUseAutoCalcAlphat_thick, useAutoCalcAlphat_thick);
    }

    public boolean isZeroDegAsCircumDir() {
        return zeroDegAsCircumDir;
    }

    public void setZeroDegAsCircumDir(boolean zeroDegAsCircumDir) {
        boolean oldZeroDegAsCircumDir = this.zeroDegAsCircumDir;
        this.zeroDegAsCircumDir = zeroDegAsCircumDir;
        propertyChangeSupport.firePropertyChange(PROP_ZERODEGASCIRCUMDIR, oldZeroDegAsCircumDir, zeroDegAsCircumDir);
    }
    
    public double getFlangeLength(){
        return radius*2.0;
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
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public SpringInInput copy(){
        return new SpringInInput(model, angle, radius, alphat_thick, baseTemp, hardeningTemp, useAutoCalcAlphat_thick, zeroDegAsCircumDir);
    }
}
