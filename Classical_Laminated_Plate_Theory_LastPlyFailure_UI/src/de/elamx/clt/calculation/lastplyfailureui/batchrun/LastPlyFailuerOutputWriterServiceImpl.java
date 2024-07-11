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
package de.elamx.clt.calculation.lastplyfailureui.batchrun;

import de.elamx.clt.CLT_LastPlyFailureResult;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.calculation.lastplyfailureui.LastPlyFailureModuleData;
import de.elamx.utilities.Utilities;
import java.io.PrintStream;
import java.util.Locale;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = LastPlyFailureOutputWriterService.class, position = 1)
public class LastPlyFailuerOutputWriterServiceImpl implements LastPlyFailureOutputWriterService {

    @Override
    public void writeResults(PrintStream out, LastPlyFailureModuleData data, CLT_LastPlyFailureResult result) {
        
        out.println("********************************************************************************");
        out.println(Utilities.centeredText("LAST PLY FAILURE", 80));
        out.println(Utilities.centeredText(data.getName(), 80));
        out.println("********************************************************************************");

        Locale lo = Locale.ENGLISH;

        int maxIterationNumber = result.getLayerResult().length - 1;

        out.println();
        out.println("Mechanical loads :");
        out.println();
        double[] forces = data.getLastPlyFailureInput().getLoad().getForceMomentAsVector();
        out.printf(lo, "  nxx  = %-17.10E%n", forces[0]);
        out.printf(lo, "  nyy  = %-17.10E%n", forces[1]);
        out.printf(lo, "  nxy  = %-17.10E%n", forces[2]);
        out.printf(lo, "  mxx  = %-17.10E%n", forces[3]);
        out.printf(lo, "  myy  = %-17.10E%n", forces[4]);
        out.printf(lo, "  mxy  = %-17.10E%n", forces[5]);
        out.println();
        out.println("Additional Input :");
        out.println();
        out.printf(lo, "  jA       = %-17.10E%n", data.getLastPlyFailureInput().getJ_a());
        out.printf(lo, "  degFac   = %-17.10E%n", data.getLastPlyFailureInput().getDegradationFactor());
        out.printf(lo, "  epsAllow = %-17.10E%n", data.getLastPlyFailureInput().getEpsilon_crit());
        
        for (int iter = 0; iter < maxIterationNumber; iter++) {

            CLT_LayerResult[] results = result.getLayerResult()[iter];

            out.println();
            out.println(Utilities.centeredText("Iteration " + iter, 80));
            out.println();
            out.println("Local layer results :");
            out.println();
            
            out.println("Layer of Failure: " + Integer.toString(result.getLayerNumber()[iter]));
            out.printf(lo, "RF Iteration:      %12.5E%n", result.getRf_min()[iter]);
            out.println("Failure Type:     " + result.getFailureType()[iter]);

            out.println("  No.      zmi                 s11          s22          s12          e11          e22          e12          RF       FF    IFF");
            double[] str, eps;
            for (int ii = 0; ii < results.length; ii++) {
                str = results[ii].getSss_upper().getStress();
                eps = results[ii].getSss_upper().getStrain();
                out.printf(lo, "  %3d  %12.5E upper %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E %5s %5s%n", (ii + 1), results[ii].getClt_layer().getZm(), str[0], str[1], str[2], eps[0], eps[1], eps[2], results[ii].getRr_upper().getMinimalReserveFactor(), result.getFb_fail()[iter][ii], result.getZfw_fail()[iter][ii]);
                str = results[ii].getSss_lower().getStress();
                eps = results[ii].getSss_lower().getStrain();
                out.printf(lo, "                    lower %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E %5s %5s%n", str[0], str[1], str[2], eps[0], eps[1], eps[2], results[ii].getRr_lower().getMinimalReserveFactor(), result.getFb_fail()[iter][ii], result.getZfw_fail()[iter][ii]);
            }
        }

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
    }

}
