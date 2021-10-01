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
package de.elamx.clt.calculation;

import de.elamx.clt.CLT_LayerResult;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas Hauffe
 */
public class LayerResultContainer {
    
    private CLT_LayerResult layerResult;

    public static final String PROP_LAYERRESULT = "layerResult";
    
    public LayerResultContainer(CLT_LayerResult layerResult) {
        this.layerResult = layerResult;
    }

    /**
     * Get the value of layerResult
     *
     * @return the value of layerResult
     */
    public CLT_LayerResult getLayerResult() {
        return layerResult;
    }

    /**
     * Set the value of layerResult
     *
     * @param layerResult new value of layerResult
     */
    public void setLayerResult(CLT_LayerResult layerResult) {
        CLT_LayerResult oldLayerResult = this.layerResult;
        this.layerResult = layerResult;
        propertyChangeSupport.firePropertyChange(PROP_LAYERRESULT, oldLayerResult, layerResult);
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
