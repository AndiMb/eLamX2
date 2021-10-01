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
package de.elamx.clt.pressurevessel.optimization;

import de.elamx.clt.CLT_Calculator;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.Loads;
import de.elamx.clt.pressurevessel.PressureVesselInput;
import de.elamx.core.ELamXDecimalFormat;
import de.elamx.core.GlobalProperties;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class MinimalReserveFactorImplementation implements MinimalReserveFactorCalculator {

    private final PressureVesselInput input;

    public MinimalReserveFactorImplementation() {
        input = new PressureVesselInput();
    }

    public MinimalReserveFactorImplementation(PressureVesselInput input) {
        this.input = input;
    }

    @Override
    public double getMinimalReserveFactor(Laminat laminat) {
        boolean oldNotify = input.isNotify();
        input.setNotify(false);
        CLT_Laminate clt_lam = laminat.getLookup().lookup(CLT_Laminate.class);
        if (clt_lam == null) {
            clt_lam = new CLT_Laminate(laminat);
        }
        
        Loads loads = input.getLoad(clt_lam.getTges());

        CLT_Calculator.determineValues(clt_lam, loads, input.getStrains(), input.isUseStrains());

        CLT_LayerResult[] results = CLT_Calculator.getLayerResults_radial(clt_lam, loads, input.getStrains(), input.getMeanRadius(clt_lam.getTges()));

        double minimalReserveFactor = Double.MAX_VALUE;

        for (CLT_LayerResult r : results) {
            minimalReserveFactor = Math.min(minimalReserveFactor, r.getRr_lower().getMinimalReserveFactor());
            minimalReserveFactor = Math.min(minimalReserveFactor, r.getRr_upper().getMinimalReserveFactor());
        }

        input.setNotify(oldNotify);

        return minimalReserveFactor;
    }

    public PressureVesselInput getInput() {
        return input;
    }

    @Override
    public boolean isSymmetricLaminateNeeded() {
        return false;
    }

    @Override
    public MinimalReserveFactorCalculator getCopy() {
        return new MinimalReserveFactorImplementation(input.copy());
    }

    @Override
    public String getHtmlString() {
        ELamXDecimalFormat df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);

        StringBuilder string = new StringBuilder();

        string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.name"));
        string.append("; ");

        string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.radius"));
        string.append(" = ");
        string.append(df.format(input.getRadius()));
        string.append("; ");

        string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.radiustype"));
        string.append(" = ");
        switch(input.getRadiusType()){
            case PressureVesselInput.RADIUSTYPE_INNER:
                string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.radiustype.inner"));
                break;
            case PressureVesselInput.RADIUSTYPE_MEAN:
                string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.radiustype.mean"));
                break;
            case PressureVesselInput.RADIUSTYPE_OUTER:
                string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.radiustype.outer"));
                break;
            default:
                string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.radiustype.mean"));
        }
        string.append("; ");

        string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.pressure"));
        string.append(" = ");
        string.append(df.format(input.getPressure()));

        return string.toString();
    }

}
