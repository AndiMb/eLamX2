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
package de.elamx.batchrun;

import de.elamx.clt.CLT_Laminate;
import de.elamx.core.GeneralOutputWriterService;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import de.elamx.utilities.Utilities;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service=GeneralOutputWriterService.class, position=1)
public class GeneralOutputWriterServiceImpl implements GeneralOutputWriterService{
    
    /**
     * Schreiben allgemeiner Informationen zum eLamX-Lauf.
     *
     * @param out PrintStream für die Ausgaben
     * @param inputFile Eingabedatei
     * @param inputFileMD5 MD5-Checksumme der Eingabedatei
     * @param date Zeitstempel
     */
    @Override
    public void writeHeader(PrintStream out, File inputFile, String inputFileMD5, Date date) {
        out.println("eLamX 2 - Module Calculation");
        out.println("");
        out.println("Version: " + NbBundle.getBundle("org.netbeans.core.startup.Bundle").getString("currentVersion"));
        out.println("");
        out.println("Technische Universitaet Dresden");
        out.println("Professur fuer Luftfahrzeugtechnik");
        out.println("https://tu-dresden.de/mw/ilr/lft");
        out.println("");
        out.println("Timestamp: " + date.toString());
        out.println("");
        out.println("Processed file (MD5): " + inputFile.getAbsolutePath() + " (" + inputFileMD5 + ")");
        out.println();
        out.println();
    }

    /**
     * Herausschreiben der Laminatinformationen
     *
     * @param out PrintStream für die Ausgaben
     * @param laminate Laminat, dessen Informationen geschrieben werden sollen
     */
    @Override
    public void writeLaminateInformation(PrintStream out, Laminat laminate) {

        out.println("********************************************************************************");
        out.println(Utilities.centeredText("LAMINATE INFORMATION", 80));
        out.println(Utilities.centeredText(laminate.getName(), 80));
        out.println("********************************************************************************");

        Locale lo = Locale.ENGLISH;

        CLT_Laminate clt_laminate = laminate.getLookup().lookup(CLT_Laminate.class);
        if (clt_laminate == null) {
            clt_laminate = new CLT_Laminate(laminate);
        }

        ArrayList<Layer> layers = laminate.getLayers();
        Layer l;
        int layerNum = layers.size();

        // Lagenaufbau
        out.println("Selected Lay-up :");
        out.println("Lay-up is" + (laminate.isSymmetric() ? " " : " not ") + "symmetric");
        out.println("Total number of layers = " + laminate.getNumberofLayers());
        out.println("Total thickness        = " + clt_laminate.getTges());

        out.println("Layer   name                          thickness           angle");
        out.println("                                      (nominal)           (deg)");
        out.println("         ---top---");
        for (int ii = 0; ii < layerNum; ii++) {
            l = layers.get(ii);
            out.printf(lo, "%4d :  %-30s%-20.5f%5.1f%n", (ii + 1), l.getName(), l.getThickness(), l.getAngle());
        }
        if (laminate.isSymmetric()) {
            out.println("         ---mid-plane---");
        } else {
            out.println("         ---bottom---");
        }

        // Materialdaten der Einzelschicht
        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Material data :");
        for (int ii = 0; ii < layerNum; ii++) {
            l = layers.get(ii);
            out.println("Layer " + (ii + 1) + " : ");
            Material m = l.getMaterial();
            out.printf(lo, "  E11      = %-10.1f    ", m.getEpar());
            out.printf(lo, "  E22      = %-10.1f%n", m.getEnor());
            out.printf(lo, "  v12      = %-10.5f    ", m.getNue12());
            out.printf(lo, "  v21      = %-10.5f%n", m.getNue21());
            out.printf(lo, "  G12      = %-10.1f    ", m.getG());
            out.printf(lo, "  rho      = %-10.5f%n", m.getRho());
            out.printf(lo, "  a11      = %-10.4E    ", m.getAlphaTPar());
            out.printf(lo, "  a22      = %-10.4E%n", m.getAlphaTNor());
            out.printf(lo, "  b11      = %-10.4E    ", m.getBetaPar());
            out.printf(lo, "  b22      = %-10.4E%n", m.getBetaNor());
            out.printf(lo, "  S11T     = %-10.1f    ", m.getRParTen());
            out.printf(lo, "  S22T     = %-10.1f%n", m.getRNorTen());
            out.printf(lo, "  S11C     = %-10.1f    ", m.getRParCom());
            out.printf(lo, "  S22C     = %-10.1f%n", m.getRNorCom());
            out.printf(lo, "  S12      = %-10.1f    ", m.getRShear());
            out.printf(lo, "  Crit.    = %-30s%n", l.getCriterion().getDisplayName());
            for (String key : m.getAdditionalValueKeySet()) {
                out.printf(lo, "  %-26s = %-10.5f%n", m.getAdditionalValueDisplayName(key), m.getAdditionalValue(key));
            }
        }

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("ABD-Matrix (Stiffness matrix):");
        double[][] amat = clt_laminate.getAMatrix();
        double[][] bmat = clt_laminate.getBMatrix();
        double[][] dmat = clt_laminate.getDMatrix();
        out.printf(lo, "  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", amat[0][0], amat[0][1], amat[0][2], bmat[0][0], bmat[0][1], bmat[0][2]);
        out.printf(lo, "  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", amat[1][0], amat[1][1], amat[1][2], bmat[1][0], bmat[1][1], bmat[1][2]);
        out.printf(lo, "  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", amat[2][0], amat[2][1], amat[2][2], bmat[2][0], bmat[2][1], bmat[2][2]);
        out.printf(lo, "  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", bmat[0][0], bmat[0][1], bmat[0][2], dmat[0][0], dmat[0][1], dmat[0][2]);
        out.printf(lo, "  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", bmat[1][0], bmat[1][1], bmat[1][2], dmat[1][0], dmat[1][1], dmat[1][2]);
        out.printf(lo, "  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", bmat[2][0], bmat[2][1], bmat[2][2], dmat[2][0], dmat[2][1], dmat[2][2]);

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("abd-Matrix (Flexibility matrix):");
        amat = clt_laminate.getaMatrix();
        bmat = clt_laminate.getbMatrix();
        dmat = clt_laminate.getdMatrix();
        out.printf(lo, "  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", amat[0][0], amat[0][1], amat[0][2], bmat[0][0], bmat[0][1], bmat[0][2]);
        out.printf(lo, "  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", amat[1][0], amat[1][1], amat[1][2], bmat[1][0], bmat[1][1], bmat[1][2]);
        out.printf(lo, "  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", amat[2][0], amat[2][1], amat[2][2], bmat[2][0], bmat[2][1], bmat[2][2]);
        out.printf(lo, "  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", bmat[0][0], bmat[1][0], bmat[2][0], dmat[0][0], dmat[0][1], dmat[0][2]);
        out.printf(lo, "  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", bmat[0][1], bmat[1][1], bmat[2][1], dmat[1][0], dmat[1][1], dmat[1][2]);
        out.printf(lo, "  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", bmat[0][2], bmat[1][2], bmat[2][2], dmat[2][0], dmat[2][1], dmat[2][2]);

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Effective Stiffness :");
        out.println();

        out.println("          with Poisson effect        without Poisson effect");
        out.println("         Membrane      Flexural      Membrane      Flexural");
        out.printf(lo, "  Exx  = %-10.1f    %-10.1f    %-10.1f    %-10.1f%n", clt_laminate.getExSimple(), clt_laminate.getExBendSimple(), clt_laminate.getExFixed(), clt_laminate.getExBendFixed());
        out.printf(lo, "  Eyy  = %-10.1f    %-10.1f    %-10.1f    %-10.1f%n", clt_laminate.getEySimple(), clt_laminate.getEyBendSimple(), clt_laminate.getEyFixed(), clt_laminate.getEyBendFixed());
        out.printf(lo, "  Gxy  = %-10.1f    %-10.1f    %-10.1f    %-10.1f%n", clt_laminate.getGSimple(), clt_laminate.getGBendSimple(), clt_laminate.getGFixed(), clt_laminate.getGBendFixed());
        out.printf(lo, "  vxy  = %-10.5f    %-10.5f    %-10s    %-10s%n", clt_laminate.getNuxySimple(), clt_laminate.getNuxyBendSimple(), "-", "-");
        out.printf(lo, "  vyx  = %-10.5f    %-10.5f    %-10s    %-10s%n", clt_laminate.getNuyxSimple(), clt_laminate.getNuyxBendSimple(), "-", "-");
        out.println();

        out.println("Non-dimensional parameters :");
        out.printf(lo, "  beta_D   = %17.10E%n", clt_laminate.getBetaD());
        out.printf(lo, "  nu_D     = %17.10E%n", clt_laminate.getNuD());
        out.printf(lo, "  gamma_D  = %17.10E%n", clt_laminate.getGammaD());
        out.printf(lo, "  delta_D  = %17.10E%n", clt_laminate.getDeltaD());
        out.println();
        out.println();
    }
}
