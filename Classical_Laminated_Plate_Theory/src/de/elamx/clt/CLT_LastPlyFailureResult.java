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

/**
 *
 * @author Andreas Hauffe
 */
public class CLT_LastPlyFailureResult {
    
    private final CLT_LayerResult[][] layerResults;
    private final boolean[][] zfw_fail;
    private final boolean[][] fb_fail;
    private final Integer[] layerNumber;
    private final Double[] rf_min;
    private final String[] FailureType;

    public CLT_LastPlyFailureResult(CLT_LayerResult[][] layerResults, boolean[][] zfw_fail, boolean[][] fb_fail, Integer[] layerNumber, Double[] rf_min, String[] FailureType) {
        this.layerResults = layerResults;
        this.zfw_fail = zfw_fail;
        this.fb_fail = fb_fail;
        this.layerNumber = layerNumber;
        this.rf_min = rf_min;
        this.FailureType = FailureType;
    }

    public CLT_LayerResult[][] getLayerResult() {
        return layerResults;
    }

    public boolean[][] getZfw_fail() {
        return zfw_fail;
    }

    public boolean[][] getFb_fail() {
        return fb_fail;
    }

    public Integer[] getLayerNumber() {
        return layerNumber;
    }

    public Double[] getRf_min() {
        return rf_min;
    }

    public String[] getFailureType() {
        return FailureType;
    }
}
