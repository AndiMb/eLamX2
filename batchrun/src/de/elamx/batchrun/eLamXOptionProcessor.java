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
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.elamx.core.BatchRunService;
import de.elamx.core.GeneralOutputWriterService;

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
     *
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
            try {
                out = new PrintStream(outputFile);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(eLamXOptionProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        GeneralOutputWriterService writerService = Lookup.getDefault().lookup(GeneralOutputWriterService.class);

        // Schreiben des Headers
        writerService.writeHeader(out);

        // Schleife über alle in der eLamX-Datei enthaltenen Laminate
        for (Laminat lam : eLamXLookup.getDefault().lookupAll(Laminat.class)) {
            // Schreiben der Laminatinformationen
            writerService.writeLaminateInformation(out, lam);
            /*
            Schleife über alle Module-Ausgabe-Service-Klasse, um die jeweiligen
            Berechnungen und Ausgaben anzustoßen, z.B. das Berechnungsmodul.
             */
            for (BatchRunService tos : Lookup.getDefault().lookupAll(BatchRunService.class)) {
                tos.performBatchTasksAndOutput(lam, out);
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
}
