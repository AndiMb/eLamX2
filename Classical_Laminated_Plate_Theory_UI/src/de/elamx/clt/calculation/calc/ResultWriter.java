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
package de.elamx.clt.calculation.calc;

import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.Loads;
import de.elamx.clt.Strains;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author Andreas Hauffe
 */
public class ResultWriter {

    public static void writeResults(FileWriter file, Laminat laminate, Loads loads, Strains strain, CLT_LayerResult[] results){
        if (file == null) {
            return;
        }
        PrintWriter out = new PrintWriter(new BufferedWriter(file));

        Locale lo = Locale.ENGLISH;

        CLT_Laminate clt_laminate = laminate.getLookup().lookup(CLT_Laminate.class);
        
        ArrayList<Layer> layers = laminate.getLayers();
        Layer l;
        int layerNum = layers.size();

        out.println("eLamX 2 - Module Calculation");
        out.println("");
        out.println("Technische Unitversitaet Dresden");
        out.println("Lehrstuhl fuer Luftfahrzeugtechnik");
        out.println("http://tu-dresden.de/mw/ilr/lft");
        out.println();
        out.println("_______________________________________________________________________________");
        out.println();

        // Lagenaufbau
        out.println("Selected Lay-up :");
        out.println("Lay-up is" + (laminate.isSymmetric() ? " " : " not ") + "symmetric");
        out.println("Total number of layers = " + laminate.getNumberofLayers());
        out.println("Total thickness        = " + clt_laminate.getTges());

        out.println("Layer   name                          thickness           angle");
        out.println("                                      (nominal)           (deg)");
        out.println("         ---top---");
        for (int ii = 0; ii < layerNum; ii++){
            l = layers.get(ii);
            out.printf(lo,"%4d :  %-30s%-20.5f%5.1f%n", (ii+1), l.getName(), l.getThickness(), l.getAngle());
        }
        if (laminate.isSymmetric()) {
            out.println("         ---mid-plane---");
        }else {
            out.println("         ---bottom---");
        }

        // Materialdaten der Einzelschicht
        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Material data :");
        for (int ii = 0; ii < layerNum; ii++){
            l = layers.get(ii);
            out.println("Layer " + (ii+1) + " : ");
            Material m = l.getMaterial();
            out.printf(lo,"  E11      = %-10.1f    ", m.getEpar());
            out.printf(lo,"  E22      = %-10.1f%n"  , m.getEnor());
            out.printf(lo,"  v12      = %-10.5f    ", m.getNue12());
            out.printf(lo,"  v21      = %-10.5f%n"  , m.getNue21());
            out.printf(lo,"  G12      = %-10.1f    ", m.getG());
            out.printf(lo,"  rho      = %-10.5f%n"  , m.getRho());
            out.printf(lo,"  a11      = %-10.4E    ", m.getAlphaTPar());
            out.printf(lo,"  a22      = %-10.4E%n"  , m.getAlphaTNor());
            out.printf(lo,"  b11      = %-10.4E    ", m.getBetaPar());
            out.printf(lo,"  b22      = %-10.4E%n"  , m.getBetaNor());
            out.printf(lo,"  S11T     = %-10.1f    ", m.getRParTen());
            out.printf(lo,"  S22T     = %-10.1f%n"  , m.getRNorTen());
            out.printf(lo,"  S11C     = %-10.1f    ", m.getRParCom());
            out.printf(lo,"  S22C     = %-10.1f%n"  , m.getRNorCom());
            out.printf(lo,"  S12      = %-10.1f    ", m.getRShear());
            out.printf(lo,"  Crit.    = %-30s%n"    , l.getCriterion().getDisplayName());
            for (String key : m.getAdditionalValueKeySet()){
                out.printf(lo,"  %-26s = %-10.5f%n", m.getAdditionalValueDisplayName(key), m.getAdditionalValue(key));
            }
        }

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("ABD-Matrix (Stiffness matrix):");
        double[][] amat = clt_laminate.getAMatrix();
        double[][] bmat = clt_laminate.getBMatrix();
        double[][] dmat = clt_laminate.getDMatrix();
        out.printf(lo,"  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", amat[0][0], amat[0][1], amat[0][2], bmat[0][0], bmat[0][1], bmat[0][2]);
        out.printf(lo,"  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", amat[1][0], amat[1][1], amat[1][2], bmat[1][0], bmat[1][1], bmat[1][2]);
        out.printf(lo,"  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", amat[2][0], amat[2][1], amat[2][2], bmat[2][0], bmat[2][1], bmat[2][2]);
        out.printf(lo,"  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", bmat[0][0], bmat[0][1], bmat[0][2], dmat[0][0], dmat[0][1], dmat[0][2]);
        out.printf(lo,"  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", bmat[1][0], bmat[1][1], bmat[1][2], dmat[1][0], dmat[1][1], dmat[1][2]);
        out.printf(lo,"  %10.1f    %10.1f    %10.1f    %10.1f    %10.1f    %10.1f%n", bmat[2][0], bmat[2][1], bmat[2][2], dmat[2][0], dmat[2][1], dmat[2][2]);

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("abd-Matrix (Flexiblity matrix):");
        amat = clt_laminate.getaMatrix();
        bmat = clt_laminate.getbMatrix();
        dmat = clt_laminate.getdMatrix();
        out.printf(lo,"  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", amat[0][0], amat[0][1], amat[0][2], bmat[0][0], bmat[0][1], bmat[0][2]);
        out.printf(lo,"  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", amat[1][0], amat[1][1], amat[1][2], bmat[1][0], bmat[1][1], bmat[1][2]);
        out.printf(lo,"  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", amat[2][0], amat[2][1], amat[2][2], bmat[2][0], bmat[2][1], bmat[2][2]);
        out.printf(lo,"  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", bmat[0][0], bmat[1][0], bmat[2][0], dmat[0][0], dmat[0][1], dmat[0][2]);
        out.printf(lo,"  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", bmat[0][1], bmat[1][1], bmat[2][1], dmat[1][0], dmat[1][1], dmat[1][2]);
        out.printf(lo,"  %17.10E    %17.10E    %17.10E    %17.10E    %17.10E    %17.10E%n", bmat[0][2], bmat[1][2], bmat[2][2], dmat[2][0], dmat[2][1], dmat[2][2]);

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Effective Stiffness :");
        out.println();

        out.println(  "          with Poisson effect        without Poisson effect");
        out.println(  "         Membrane      Flexural      Membrane      Flexural");
        out.printf(lo,"  Exx  = %-10.1f    %-10.1f    %-10.1f    %-10.1f%n", clt_laminate.getExSimple(), clt_laminate.getExBendSimple(), clt_laminate.getExFixed(), clt_laminate.getExBendFixed());
        out.printf(lo,"  Eyy  = %-10.1f    %-10.1f    %-10.1f    %-10.1f%n", clt_laminate.getEySimple(), clt_laminate.getEyBendSimple(), clt_laminate.getEyFixed(), clt_laminate.getEyBendFixed());
        out.printf(lo,"  Gxy  = %-10.1f    %-10.1f    %-10.1f    %-10.1f%n",  clt_laminate.getGSimple(),  clt_laminate.getGBendSimple(),  clt_laminate.getGFixed(),  clt_laminate.getGBendFixed());
        out.printf(lo,"  vxy  = %-10.5f    %-10.5f    %-10s    %-10s%n",  clt_laminate.getNuxySimple(),  clt_laminate.getNuxyBendSimple(), "-", "-");
        out.printf(lo,"  vyx  = %-10.5f    %-10.5f    %-10s    %-10s%n",  clt_laminate.getNuyxSimple(),  clt_laminate.getNuyxBendSimple(), "-", "-");

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Mechanical loads :");
        out.println();
        double[] forces = loads.getForceMomentAsVector();
        out.printf(lo,"  nxx  = %-17.10E%n", forces[0]);
        out.printf(lo,"  nyy  = %-17.10E%n", forces[1]);
        out.printf(lo,"  nxy  = %-17.10E%n", forces[2]);
        out.printf(lo,"  mxx  = %-17.10E%n", forces[3]);
        out.printf(lo,"  myy  = %-17.10E%n", forces[4]);
        out.printf(lo,"  mxy  = %-17.10E%n", forces[5]);

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Hygrothermal loads :");
        out.println();
        out.printf(lo,"nxx,th = %-17.10E%n", loads.getnT_x());
        out.printf(lo,"nyy,th = %-17.10E%n", loads.getnT_y());
        out.printf(lo,"nxy,th = %-17.10E%n", loads.getnT_xy());
        out.printf(lo,"mxx,th = %-17.10E%n", loads.getmT_x());
        out.printf(lo,"myy,th = %-17.10E%n", loads.getmT_y());
        out.printf(lo,"mxy,th = %-17.10E%n", loads.getmT_xy());
        out.println();
        out.printf(lo,"deltaT = %-17.10E%n", loads.getDeltaT());
        out.printf(lo,"deltac = %-17.10E %s%n", loads.getDeltaH(), "%");


        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Global strains :");
        out.println();
        double[] strains = strain.getEpsilonKappaAsVector();
        out.printf(lo,"  exx  = %-17.10E%n", strains[0]);
        out.printf(lo,"  eyy  = %-17.10E%n", strains[1]);
        out.printf(lo,"  gxy  = %-17.10E%n", strains[2]);
        out.printf(lo,"  kxx  = %-17.10E%n", strains[3]);
        out.printf(lo,"  kyy  = %-17.10E%n", strains[4]);
        out.printf(lo,"  kxy  = %-17.10E%n", strains[5]);

        out.println();
        out.println("_______________________________________________________________________________");
        out.println();
        out.println("Local layer results :");
        out.println();

        out.println(  "  No.      zmi                 s11          s22          s12          e11          e22          e12          RF");
        double[] str, eps;
        for (int ii = 0; ii < results.length; ii++){
            str = results[ii].getSss_upper().getStress();
            eps = results[ii].getSss_upper().getStrain();
            out.printf(lo,"  %3d  %12.5E upper %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E%n", (ii+1), results[ii].getClt_layer().getZm(), str[0], str[1], str[2], eps[0], eps[1], eps[2], results[ii].getRr_upper().getMinimalReserveFactor());
            str = results[ii].getSss_lower().getStress();
            eps = results[ii].getSss_lower().getStrain();
            out.printf(lo,"                    lower %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E %12.5E%n", str[0], str[1], str[2], eps[0], eps[1], eps[2], results[ii].getRr_lower().getMinimalReserveFactor());
        }

        out.flush();
    }

}
