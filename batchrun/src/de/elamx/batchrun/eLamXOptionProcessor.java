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
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import de.elamx.core.outputStreamService;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import de.elamx.utilities.Utilities;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = OptionProcessor.class)
public class eLamXOptionProcessor extends OptionProcessor {

    private final Option inputOption = Option.requiredArgument('i', "input");
    private final Option outputOption = Option.requiredArgument('o', "output");

    @Override
    protected Set<Option> getOptions() {
        Set<Option> set = new HashSet<>();
        set.add(inputOption);
        set.add(outputOption);
        return set;
    }

    /**
     * Verarbeiten der Befehlszeile
     * @param env Umgebung
     * @param maps Optionen
     * @throws CommandException 
     */
    @Override
    protected void process(Env env, Map<Option, String[]> maps) throws CommandException {
        /*
        Öffnen der über die Option "-i" übergebenen *.elamx-Datei und Laden
        in das globale Lookup.
        */
        if (maps.containsKey(inputOption)) {
            String fileName = maps.get(inputOption)[0];
            File inputFile = new File(fileName);
            if (inputFile.exists() && inputFile.isFile()) {
                FileObject fo = FileUtil.toFileObject(inputFile);
                eLamXLookup.getDefault().setFileObject(fo);
            }
        }

        // Setzen des Output-Streams auf STD-Out
        PrintStream out = System.out;
        /*
        Wenn eine Ausgabedatei mit der Option "-o" übergeben wurde, dass 
        Öffnen des OutputStreams.
        */
        if (maps.containsKey(outputOption)) {
            String fileName = maps.get(outputOption)[0];
            File outputFile = new File(fileName);
            if (outputFile.exists() && outputFile.isDirectory()) {
                try {
                    out = new PrintStream(outputFile);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(eLamXOptionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        // Schreiben des Headers
        writeHeader(out);
        
        // Schleife über alle in der eLamX-Datei enthaltenen Laminate
        for (Laminat lam : eLamXLookup.getDefault().lookupAll(Laminat.class)) {
            // Schreiben der Laminatinformationen
            writeLaminateInformation(out, lam);
            /*
            Schleife über alle Module-Ausgabe-Service-Klasse, um die jeweiligen
            Berechnungen und Ausgaben anzustoßen, z.B. das Berechnungsmodul.
            */
            for (outputStreamService tos : Lookup.getDefault().lookupAll(outputStreamService.class)) {
                tos.writeToStream(lam, out);
            }
        }

        // Schließen des OutputStreams
        if (out != System.out) {
            out.close();
        }

        /*
        Falls sich Änderungen hinsichtlich des Inputs und somit in der 
        eLamX-Datei ergeben haben sollten, diese ignorieren und des Änderungs-
        status auf false setzen. Aktuell muss eine Änderung des Inputs ein
        Fehler sein. Da die Datei nicht modifiziert werden sollte.
        */
        eLamXLookup.getDefault().setModified(false);

        // Beenden von eLamX
        LifecycleManager.getDefault().exit();
    }

    /**
     * Schreiben allgemeiner Informationen zum eLamX-Lauf.
     * @param out PrintStream für die Ausgaben
     */
    private void writeHeader(PrintStream out) {
        out.println("eLamX 2 - Module Calculation");
        out.println("");
        out.println("Technische Unitversitaet Dresden");
        out.println("Lehrstuhl fuer Luftfahrzeugtechnik");
        out.println("http://tu-dresden.de/mw/ilr/lft");
        out.println();
        out.println();
    }

    /**
     * Herausschreiben der Laminatinformationen
     * @param out PrintStream für die Ausgaben
     * @param laminate Laminat, dessen Informationen geschrieben werden sollen
     */
    private void writeLaminateInformation(PrintStream out, Laminat laminate) {
        
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
        out.println(laminate.isInvertZ() ? "         ---bottom---" : "         ---top---");
        for (int ii = 0; ii < layerNum; ii++){
            l = layers.get(ii);
            out.printf(lo,"%4d :  %-30s%-20.5f%5.1f%n", (ii+1), l.getName(), l.getThickness(), l.getAngle());
        }
        if (laminate.isSymmetric()) {
            out.println("         ---mid-plane---");
        }else {
            out.println(laminate.isInvertZ() ? "         ---top---" : "         ---bottom---");
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
        out.println();
    }
}
