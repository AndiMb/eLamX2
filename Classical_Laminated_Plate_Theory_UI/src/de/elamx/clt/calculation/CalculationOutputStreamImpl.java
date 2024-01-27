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

import de.elamx.clt.CLT_Calculator;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.Loads;
import de.elamx.clt.Strains;
import de.elamx.core.outputStreamService;
import de.elamx.laminate.Laminat;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Locale;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = outputStreamService.class)
public class CalculationOutputStreamImpl implements outputStreamService {

    @Override
    public void writeToStream(Laminat laminate, PrintStream ps) {
        Collection<? extends CalculationModuleData> col = laminate.getLookup().lookupAll(CalculationModuleData.class);
        CLT_Laminate clt_lam = laminate.getLookup().lookup(CLT_Laminate.class);
        if (clt_lam == null) {
            clt_lam = new CLT_Laminate(laminate);
        }
        for (CalculationModuleData data : col) {
            CLT_Calculator.determineValues(clt_lam, data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), data.getDataHolder().isUseStrains());
            CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(data.getLaminat().getLookup().lookup(CLT_Laminate.class), data.getDataHolder().getLoad(), data.getDataHolder().getStrains());
            writeResults(System.out, data, data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), layerResults);
        }
    }

    private void writeResults(PrintStream out, CalculationModuleData data, Loads loads, Strains strain, CLT_LayerResult[] results) {

        out.println("********************************************************************************");
        out.println(centeredText("CLASSICAL LAMINATED PLATE THEORY", 80));
        out.println(centeredText(data.getName(), 80));
        out.println("********************************************************************************");

        Locale lo = Locale.ENGLISH;

        out.println();
        out.println("Mechanical loads :");
        out.println();
        double[] forces = loads.getForceMomentAsVector();
        out.printf(lo, "  nxx  = %-17.10E%n", forces[0]);
        out.printf(lo, "  nyy  = %-17.10E%n", forces[1]);
        out.printf(lo, "  nxy  = %-17.10E%n", forces[2]);
        out.printf(lo, "  mxx  = %-17.10E%n", forces[3]);
        out.printf(lo, "  myy  = %-17.10E%n", forces[4]);
        out.printf(lo, "  mxy  = %-17.10E%n", forces[5]);

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Hygrothermal loads :");
        out.println();
        out.printf(lo, "nxx,th = %-17.10E%n", loads.getnT_x());
        out.printf(lo, "nyy,th = %-17.10E%n", loads.getnT_y());
        out.printf(lo, "nxy,th = %-17.10E%n", loads.getnT_xy());
        out.printf(lo, "mxx,th = %-17.10E%n", loads.getmT_x());
        out.printf(lo, "myy,th = %-17.10E%n", loads.getmT_y());
        out.printf(lo, "mxy,th = %-17.10E%n", loads.getmT_xy());
        out.println();
        out.printf(lo, "deltaT = %-17.10E%n", loads.getDeltaT());
        out.printf(lo, "deltac = %-17.10E %s%n", loads.getDeltaH(), "%");

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Global strains :");
        out.println();
        double[] strains = strain.getEpsilonKappaAsVector();
        out.printf(lo, "  exx  = %-17.10E%n", strains[0]);
        out.printf(lo, "  eyy  = %-17.10E%n", strains[1]);
        out.printf(lo, "  gxy  = %-17.10E%n", strains[2]);
        out.printf(lo, "  kxx  = %-17.10E%n", strains[3]);
        out.printf(lo, "  kyy  = %-17.10E%n", strains[4]);
        out.printf(lo, "  kxy  = %-17.10E%n", strains[5]);

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Local layer results :");
        out.println();

        out.println("  No.      zmi                 s11          s22          s12          e11          e22          e12          RF");
        double[] str, eps;
        for (int ii = 0; ii < results.length; ii++) {
            str = results[ii].getSss_upper().getStress();
            eps = results[ii].getSss_upper().getStrain();
            out.printf(lo, "  %3d  %12.5E upper %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E%n", (ii + 1), results[ii].getClt_layer().getZm(), str[0], str[1], str[2], eps[0], eps[1], eps[2], results[ii].getRr_upper().getMinimalReserveFactor());
            str = results[ii].getSss_lower().getStress();
            eps = results[ii].getSss_lower().getStrain();
            out.printf(lo, "                    lower %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E%n", str[0], str[1], str[2], eps[0], eps[1], eps[2], results[ii].getRr_lower().getMinimalReserveFactor());
        }
        out.println();
        out.println();
    }

    public String centeredText(String text, int totalWidth) {
        int padding = (totalWidth - text.length()) / 2;
        return String.format("%" + padding + "s%s%" + padding + "s", "*".repeat(padding), text, "*".repeat(padding));
    }
}
