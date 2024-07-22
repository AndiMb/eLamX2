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
    private final String[] failureName;
    private final Integer[] failureType;
    private final Double rf_first_ff;
    private final Double rf_first_iff;
    private final Double rf_first_epsilon;
    private final Double exceedance_factor;
    private final Integer iter_first_ff;
    private final Integer iter_first_iff;
    private final Integer iter_first_epsilon;
    private final Integer iter_exceedance_factor;
    private final boolean ff_before_iff;

    public CLT_LastPlyFailureResult(CLT_LayerResult[][] layerResults, boolean[][] zfw_fail, boolean[][] fb_fail, Integer[] layerNumber, Double[] rf_min, String[] failureName, Integer[] failureType, Double rf_first_ff, Double rf_first_iff, Double rf_first_epsilon, Double exceedance_factor, Integer iter_first_ff, Integer iter_first_iff, Integer iter_first_epsilon, Integer iter_exceedance_factor, boolean ff_before_iff) {
        this.layerResults = layerResults;
        this.zfw_fail = zfw_fail;
        this.fb_fail = fb_fail;
        this.layerNumber = layerNumber;
        this.rf_min = rf_min;
        this.failureName = failureName;
        this.failureType = failureType;
        this.rf_first_ff = rf_first_ff;
        this.rf_first_iff = rf_first_iff;
        this.rf_first_epsilon = rf_first_epsilon;
        this.exceedance_factor = exceedance_factor;
        this.iter_first_ff = iter_first_ff;
        this.iter_first_iff = iter_first_iff;
        this.iter_first_epsilon = iter_first_epsilon;
        this.iter_exceedance_factor = iter_exceedance_factor;
        this.ff_before_iff = ff_before_iff;
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

    public String[] getFailureName() {
        return failureName;
    }

    public Integer[] getFailureType() {
        return failureType;
    }

    public Double getRf_first_ff() {
        return rf_first_ff;
    }

    public Double getRf_first_iff() {
        return rf_first_iff;
    }

    public Double getRf_first_epsilon() {
        return rf_first_epsilon;
    }

    public Double getExceedance_factor() {
        return exceedance_factor;
    }

    public Integer getIter_first_ff() {
        return iter_first_ff;
    }

    public Integer getIter_first_iff() {
        return iter_first_iff;
    }

    public Integer getIter_first_epsilon() {
        return iter_first_epsilon;
    }

    public Integer getIter_exceedance_factor() {
        return iter_exceedance_factor;
    }

    public boolean isFf_before_iff() {
        return ff_before_iff;
    }
}
