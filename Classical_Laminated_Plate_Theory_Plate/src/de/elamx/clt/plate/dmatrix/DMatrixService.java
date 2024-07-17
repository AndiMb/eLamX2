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
package de.elamx.clt.plate.dmatrix;

import de.elamx.clt.CLT_Laminate;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class DMatrixService {
    
    public static final String PROP_NAME = "name";
    public static final String PROP_SHORTNAME = "shortName";
    
    private String shortName;
    private String name;

    public DMatrixService(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        String oldShortName = this.shortName;
        this.shortName = shortName;
        propertyChangeSupport.firePropertyChange(PROP_SHORTNAME, oldShortName, shortName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    @Override
    public String toString() {
        return getName();
    }
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public abstract double[][] getDMatrix(CLT_Laminate laminate);
    
    public abstract boolean needsSymmetricLaminate();
    
    public abstract String getBatchRunOutput();
}
