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
package de.elamx.clt.springinui;

import de.elamx.clt.springinui.geometrycalculators.GeometryCalculator;
import de.elamx.clt.springinui.geometrycalculators.SimpleGeometryCalculator;

/**
 *
 * @author Andreas Hauffe
 */
public class GlobalSpringInProperties {
    
    private static GlobalSpringInProperties instance;
    
    private GeometryCalculator geoCalc = new SimpleGeometryCalculator();
    
    public static GlobalSpringInProperties getInstance(){
        if (instance == null){
            instance = new GlobalSpringInProperties();
        }
        return instance;
    }
    
    private GlobalSpringInProperties(){
        
    }

    /**
     * Get the value of geoCalc1
     *
     * @return the value of geoCalc1
     */
    public GeometryCalculator getGeoCalc() {
        return geoCalc;
    }

    /**
     * Set the value of geoCalc1
     *
     * @param geoCalc1 new value of geoCalc1
     */
    public void setGeoCalc(GeometryCalculator geoCalc) {
        this.geoCalc = geoCalc;
    }

    
}
