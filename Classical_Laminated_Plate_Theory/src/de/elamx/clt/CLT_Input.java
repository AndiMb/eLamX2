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
package de.elamx.clt;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas Hauffe
 */
public class CLT_Input{

    public static final String PROP_DELTAT = "deltaT";
    public static final String PROP_DELTAH = "deltaH";
    public static final String PROP_M_XY = "m_xy";
    public static final String PROP_M_Y = "m_y";
    public static final String PROP_M_X = "m_x";
    public static final String PROP_N_XY = "n_xy";
    public static final String PROP_N_Y = "n_y";
    public static final String PROP_N_X = "n_x";
    public static final String PROP_HYGTERFORCES = "hygtherforces";
    public static final String PROP_KAPPA_XY = "kappa_xy";
    public static final String PROP_KAPPA_Y = "kappa_y";
    public static final String PROP_KAPPA_X = "kappa_x";
    public static final String PROP_GAMMA_XY = "gamma_xy";
    public static final String PROP_EPSILON_Y = "epsilon_y";
    public static final String PROP_EPSILON_X = "epsilon_x";
    
    private final Loads load = new Loads(){

        @Override
        public void setDeltaH(double deltaH) {
            double oldDeltaHyg = getDeltaH();
            super.setDeltaH(deltaH);
            firePropertyChange(PROP_DELTAH, oldDeltaHyg, deltaH);
        }

        @Override
        public void setDeltaT(double deltaT) {
            double oldDeltaT = getDeltaT();
            super.setDeltaT(deltaT);
            firePropertyChange(PROP_DELTAT, oldDeltaT, deltaT);
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
            firePropertyChange(PROP_HYGTERFORCES, null, forces);
        }
        
    };
    private final Strains strains = new Strains(){

        @Override
        public void setKappa_xy(double kappa_xy) {
            double oldKappa_xy = getKappa_xy();
            super.setKappa_xy(kappa_xy);
            firePropertyChange(PROP_KAPPA_XY, oldKappa_xy, kappa_xy);
        }

        @Override
        public void setKappa_y(double kappa_y) {
            double oldKappa_y = getKappa_y();
            super.setKappa_y(kappa_y);
            firePropertyChange(PROP_KAPPA_Y, oldKappa_y, kappa_y);
        }

        @Override
        public void setKappa_x(double kappa_x) {
            double oldKappa_x = getKappa_x();
            super.setKappa_x(kappa_x);
            firePropertyChange(PROP_KAPPA_X, oldKappa_x, kappa_x);
        }

        @Override
        public void setGamma_xy(double gamma_xy) {
            double oldGamma_xy = getGamma_xy();
            super.setGamma_xy(gamma_xy);
            firePropertyChange(PROP_GAMMA_XY, oldGamma_xy, gamma_xy);
        }

        @Override
        public void setEpsilon_y(double epsilon_y) {
            double oldEpsilon_y = getEpsilon_y();
            super.setEpsilon_y(epsilon_y);
            firePropertyChange(PROP_EPSILON_Y, oldEpsilon_y, epsilon_y);
        }

        @Override
        public void setEpsilon_x(double epsilon_x) {
            double oldEpsilon_x = getEpsilon_x();
            super.setEpsilon_x(epsilon_x);
            firePropertyChange(PROP_EPSILON_X, oldEpsilon_x, epsilon_x);
        }
        
    };
    
    private boolean notify = true;
    private boolean[] useStrains = new boolean[6];
    public static final String PROP_USESTRAINS = "useStrains";

    public Loads getLoad() {
        return load;
    }

    public Strains getStrains() {
        return strains;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    /**
     * Get the value of useStrains
     *
     * @return the value of useStrains
     */
    public boolean[] isUseStrains() {
        return useStrains;
    }

    /**
     * Set the value of useStrains
     *
     * @param useStrains new value of useStrains
     */
    public void setUseStrains(boolean[] useStrains) {
        if (useStrains.length != 6) return;
        boolean[] oldUseStrains = this.useStrains;
        this.useStrains = useStrains;
        firePropertyChange(PROP_USESTRAINS, oldUseStrains, useStrains);
    }

    /**
     * Get the value of useStrains at specified index
     *
     * @param index
     * @return the value of useStrains at specified index
     */
    public boolean isUseStrains(int index) {
        return this.useStrains[index];
    }

    /**
     * Set the value of useStrains at specified index.
     *
     * @param index
     * @param newUseStrains new value of useStrains at specified index
     */
    public void setUseStrains(int index, boolean newUseStrains) {
        boolean oldUseStrains = this.useStrains[index];
        this.useStrains[index] = newUseStrains;
        fireIndexedPropertyChange(PROP_USESTRAINS, index, oldUseStrains, newUseStrains);
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
    
    public CLT_Input copy(){
        CLT_Input dataHolder = new CLT_Input();
        
        Strains stn = dataHolder.getStrains();
        stn.setEpsilon_x(strains.getEpsilon_x());
        stn.setEpsilon_y(strains.getEpsilon_y());
        stn.setGamma_xy(strains.getGamma_xy());
        stn.setKappa_x(strains.getKappa_x());
        stn.setKappa_xy(strains.getKappa_xy());
        stn.setKappa_y(strains.getKappa_y());
        
        Loads ld = dataHolder.getLoad();
        ld.setDeltaH(load.getDeltaH());
        ld.setDeltaT(load.getDeltaT());
        ld.setM_x(load.getM_x());
        ld.setM_xy(load.getM_xy());
        ld.setM_y(load.getM_y());
        ld.setN_x(load.getN_x());
        ld.setN_xy(load.getN_xy());
        ld.setN_y(load.getN_y());
        
        boolean[] copyUseStrains = new boolean[useStrains.length];
        
        System.arraycopy(useStrains, 0, copyUseStrains, 0, useStrains.length);
        
        dataHolder.setUseStrains(copyUseStrains);
        
        return dataHolder;
    }
}
