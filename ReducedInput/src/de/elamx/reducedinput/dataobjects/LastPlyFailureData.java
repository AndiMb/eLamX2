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
package de.elamx.reducedinput.dataobjects;

/**
 *
 * @author Florian Dexl
 */
public class LastPlyFailureData extends ReducedInputDataObject {

    private String loadcase = null;

    private Double degradationFactor;
    private Double epsilonCrit;
    private Boolean degradeAllOnFibreFailure;

    public LastPlyFailureData(String name) {
        super(name);
    }

    public String getLoadcase() {
        return loadcase;
    }

    public void setLoadcase(String loadcase) {
        this.loadcase = loadcase;
    }

    public Double getDegradationFactor() {
        return degradationFactor;
    }

    public void setDegradationFactor(Double degradationFactor) {
        this.degradationFactor = degradationFactor;
    }

    public Double getEpsilonCrit() {
        return epsilonCrit;
    }

    public void setEpsilonCrit(Double epsilonCrit) {
        this.epsilonCrit = epsilonCrit;
    }

    public Boolean getDegradeAllOnFibreFailure() {
        return degradeAllOnFibreFailure;
    }

    public void setDegradeAllOnFibreFailure(Boolean degradeAllOnFibreFailure) {
        this.degradeAllOnFibreFailure = degradeAllOnFibreFailure;
    }


}