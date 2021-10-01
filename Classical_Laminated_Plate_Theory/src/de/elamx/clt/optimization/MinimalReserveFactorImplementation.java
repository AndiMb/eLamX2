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
package de.elamx.clt.optimization;

import de.elamx.clt.CLT_Calculator;
import de.elamx.clt.CLT_Input;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.core.ELamXDecimalFormat;
import de.elamx.core.GlobalProperties;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import org.openide.util.*;

/**
 *
 * @author Andreas Hauffe
 */
public class MinimalReserveFactorImplementation implements MinimalReserveFactorCalculator {

    private final CLT_Input input;

    public MinimalReserveFactorImplementation() {
        input = new CLT_Input();
    }

    public MinimalReserveFactorImplementation(CLT_Input input) {
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

        CLT_Calculator.determineValues(clt_lam, input.getLoad(), input.getStrains(), input.isUseStrains());

        CLT_LayerResult[] results = CLT_Calculator.getLayerResults(clt_lam, input.getLoad(), input.getStrains());

        double minimalReserveFactor = Double.MAX_VALUE;

        for (CLT_LayerResult r : results) {
            minimalReserveFactor = Math.min(minimalReserveFactor, r.getRr_lower().getMinimalReserveFactor());
            minimalReserveFactor = Math.min(minimalReserveFactor, r.getRr_upper().getMinimalReserveFactor());
        }

        input.setNotify(oldNotify);

        return minimalReserveFactor;
    }

    public CLT_Input getInput() {
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
        ELamXDecimalFormat dfstrain = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRAIN);
        ELamXDecimalFormat dfT = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_TEMPERATURE);
        ELamXDecimalFormat dfH = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_PERCENT);

        StringBuilder string = new StringBuilder();

        string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.name"));
        string.append("; ");

        if (!input.isUseStrains(0)) {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.nx"));
            string.append(" = ");
            string.append(df.format(input.getLoad().getN_x()));
            string.append("; ");
        } else {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.ex"));
            string.append(" = ");
            string.append(dfstrain.format(input.getStrains().getEpsilon_x()));
            string.append("; ");
        }

        if (!input.isUseStrains(1)) {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.ny"));
            string.append(" = ");
            string.append(df.format(input.getLoad().getN_y()));
            string.append("; ");
        } else {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.ey"));
            string.append(" = ");
            string.append(dfstrain.format(input.getStrains().getEpsilon_y()));
            string.append("; ");
        }

        if (!input.isUseStrains(2)) {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.nxy"));
            string.append(" = ");
            string.append(df.format(input.getLoad().getN_xy()));
            string.append("; ");
        } else {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.gxy"));
            string.append(" = ");
            string.append(dfstrain.format(input.getStrains().getGamma_xy()));
            string.append("; ");
        }

        if (!input.isUseStrains(3)) {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.mx"));
            string.append(" = ");
            string.append(df.format(input.getLoad().getM_x()));
            string.append("; ");
        } else {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.kx"));
            string.append(" = ");
            string.append(dfstrain.format(input.getStrains().getKappa_x()));
            string.append("; ");
        }

        if (!input.isUseStrains(4)) {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.my"));
            string.append(" = ");
            string.append(df.format(input.getLoad().getM_y()));
            string.append("; ");
        } else {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.ky"));
            string.append(" = ");
            string.append(dfstrain.format(input.getStrains().getKappa_y()));
            string.append("; ");
        }

        if (!input.isUseStrains(5)) {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.mxy"));
            string.append(" = ");
            string.append(df.format(input.getLoad().getM_xy()));
            string.append("; ");
        } else {
            string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.kxy"));
            string.append(" = ");
            string.append(dfstrain.format(input.getStrains().getKappa_xy()));
            string.append("; ");
        }

        string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.deltaT"));
        string.append(" = ");
        string.append(dfT.format(input.getLoad().getDeltaT()));
        string.append("; ");

        string.append(NbBundle.getMessage(MinimalReserveFactorImplementation.class, "MinimalReserveFactorImplementation.deltaH"));
        string.append(" = ");
        string.append(dfH.format(input.getLoad().getDeltaH()));

        return string.toString();
    }

}
