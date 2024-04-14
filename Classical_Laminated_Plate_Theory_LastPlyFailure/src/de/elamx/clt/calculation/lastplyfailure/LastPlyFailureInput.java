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
package de.elamx.clt.calculation.lastplyfailure;

import de.elamx.clt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas Hauffe
 */
public class LastPlyFailureInput {
    
    public static final String PROP_M_XY = "m_xy";
    public static final String PROP_M_Y = "m_y";
    public static final String PROP_M_X = "m_x";
    public static final String PROP_N_XY = "n_xy";
    public static final String PROP_N_Y = "n_y";
    public static final String PROP_N_X = "n_x";
    
    private final Loads load = new Loads(){

        @Override
        public void setDeltaH(double deltaH) {
            super.setDeltaH(deltaH);
        }

        @Override
        public void setDeltaT(double deltaT) {
            super.setDeltaT(deltaT);
        }

        @Override
        public void setM_xy(double m_xy) {
            double oldM_xy = getM_xy();
            super.setM_xy(m_xy);
            firePropertyChange(PROP_M_XY, oldM_xy, m_xy);
        }

        @Override
        public void setM_y(double m_y) {
            double oldM_y = getM_y();
            super.setM_y(m_y);
            firePropertyChange(PROP_M_Y, oldM_y, m_y);
        }

        @Override
        public void setM_x(double m_x) {
            double oldM_x = getM_x();
            super.setM_x(m_x);
            firePropertyChange(PROP_M_X, oldM_x, m_x);
        }

        @Override
        public void setN_xy(double n_xy) {
            double oldN_xy = getN_xy();
            super.setN_xy(n_xy);
            firePropertyChange(PROP_N_XY, oldN_xy, n_xy);
        }

        @Override
        public void setN_y(double n_y) {
            double oldN_y = getN_y();
            super.setN_y(n_y);
            firePropertyChange(PROP_N_Y, oldN_y, n_y);
        }

        @Override
        public void setN_x(double n_x) {
            double oldN_x = getN_x();
            super.setN_x(n_x);
            firePropertyChange(PROP_N_X, oldN_x, n_x);
        }

        @Override
        public void setHygrothermalForcesAsVector(double[] forces) {
            super.setHygrothermalForcesAsVector(forces);
        }
    };
    
    private boolean notify = true;
    
    private final boolean[] useStrains = new boolean[]{false, false, false, false, false, false};
    
    public LastPlyFailureInput() {
    }

    public Loads getLoad() {
        return load;
    }
    
    public Strains getStrain(){
        return new Strains();
    }
    
    public boolean[] isUseStrains() {
        return useStrains;
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
    
    public LastPlyFailureInput copy(){
        LastPlyFailureInput dataHolder = new LastPlyFailureInput();
        
        Loads ld = dataHolder.getLoad();
        ld.setDeltaH(load.getDeltaH());
        ld.setDeltaT(load.getDeltaT());
        ld.setM_x(load.getM_x());
        ld.setM_xy(load.getM_xy());
        ld.setM_y(load.getM_y());
        ld.setN_x(load.getN_x());
        ld.setN_xy(load.getN_xy());
        ld.setN_y(load.getN_y());
        
        return dataHolder;
    }
}
