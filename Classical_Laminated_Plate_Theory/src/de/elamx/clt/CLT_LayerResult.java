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

import de.elamx.laminate.Layer;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.ReserveFactor;

/**
 *
 * @author Andreas Hauffe
 */
public class CLT_LayerResult {
    
    private final Layer layer;
    private final CLT_Layer clt_layer;
    private final StressStrainState sss_lower;
    private final StressStrainState sss_upper;
    private final StressStrainState sss_lower_glo;
    private final StressStrainState sss_upper_glo;
    private final ReserveFactor rr_lower;
    private final ReserveFactor rr_upper;
    private final boolean failed;

    public CLT_LayerResult(Layer layer, CLT_Layer clt_layer, 
            StressStrainState[] sss_lower, StressStrainState[] sss_upper, 
            ReserveFactor rr_lower, ReserveFactor rr_upper, 
            boolean failed) {
        this.layer = layer;
        this.clt_layer = clt_layer;
        this.sss_lower = sss_lower[0];
        this.sss_upper = sss_upper[0];
        this.sss_lower_glo = sss_lower[1];
        this.sss_upper_glo = sss_upper[1];
        this.rr_lower = rr_lower;
        this.rr_upper = rr_upper;
        this.failed = failed;
    }

    public Layer getLayer() {
        return layer;
    }

    public CLT_Layer getClt_layer() {
        return clt_layer;
    }

    public StressStrainState getSss_lower() {
        return sss_lower;
    }

    public StressStrainState getSss_upper() {
        return sss_upper;
    }

    public StressStrainState getSss_lower_glo() {
        return sss_lower_glo;
    }

    public StressStrainState getSss_upper_glo() {
        return sss_upper_glo;
    }

    public ReserveFactor getRr_lower() {
        return rr_lower;
    }

    public ReserveFactor getRr_upper() {
        return rr_upper;
    }

    public boolean isFailed() {
        return failed;
    }
}
