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

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.reducedinput.ReducedInputHandler;
import de.elamx.core.BatchRunService;
import de.elamx.core.GeneralOutputWriterService;
import de.elamx.core.HDF5OutputWriterService;
import de.elamx.filesupport.NewFileCreator;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Material;
import de.elamx.laminate.eLamXLookup;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.xml.sax.SAXException;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = OptionProcessor.class)
public class eLamXOptionProcessor extends OptionProcessor {

    private final Option inputOption = Option.requiredArgument('i', "input");
    private final Option outputOption = Option.optionalArgument('o', "output");
    private final Option outputTypeOption = Option.optionalArgument('t', "outputtype");
    private final Option reducedInputOption = Option.optionalArgument('b', "reducedinput");
    private final Option hdf5OutputOption = Option.optionalArgument('h', "hdf5output");

    @Override
    protected Set<Option> getOptions() {
        Set<Option> set = new HashSet<>();
        set.add(inputOption);
        set.add(outputOption);
        set.add(outputTypeOption);
        set.add(reducedInputOption);
        set.add(hdf5OutputOption);
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
        File inputFile = null;
        if (maps.containsKey(inputOption)) {
            String fileName = maps.get(inputOption)[0];
            inputFile = new File(fileName);
            if (maps.containsKey(reducedInputOption)) {
                // Neue eLamX-Datei anlegen
                NewFileCreator.create();

                // Einlesen der reduzierten Eingabedatei
                SAXParserFactory factory = SAXParserFactory.newInstance();
                try {
                    SAXParser saxParser = factory.newSAXParser();
                    ReducedInputHandler handler = new ReducedInputHandler();
                    saxParser.parse(inputFile, handler);
                } catch (ParserConfigurationException | SAXException | IOException ex) {
                    Logger.getLogger(eLamXOptionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (inputFile.exists() && inputFile.isFile()) {
                    FileObject fo = FileUtil.toFileObject(inputFile);
                    eLamXLookup.getDefault().setFileObject(fo);
                }
            }
        }

        // Setzen des Output-Streams auf STD-Out
        PrintStream out = System.out;
        /*
        Wenn eine Ausgabedatei mit der Option "-o" übergeben wurde, dann
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

        int outputType = 0;
        if (maps.containsKey(outputTypeOption)) {
            outputType = Integer.parseInt(maps.get(outputTypeOption)[0]);
        }

        List<GeneralOutputWriterService> writerServices = new ArrayList<>(Lookup.getDefault().lookupAll(GeneralOutputWriterService.class));
        GeneralOutputWriterService writerService = writerServices.get(Math.min(Math.max(outputType, 0), writerServices.size() - 1));

        /*
        Wenn eine hdf5-Ausgabedatei mit der Option "-h" definiert wurde, dann
        Öffnen des hdf5-Writers
         */
        IHDF5Writer hdf5out = null;
        if (maps.containsKey(hdf5OutputOption)) {
            String hdf5FileName = maps.get(hdf5OutputOption)[0];
            hdf5out = HDF5Factory.configure(hdf5FileName).overwrite().writer();
        }

        int hdf5OutputType = 0;
        List<HDF5OutputWriterService> hdf5WriterServices = new ArrayList<>(Lookup.getDefault().lookupAll(HDF5OutputWriterService.class));
        HDF5OutputWriterService hdf5WriterService = hdf5WriterServices.get(Math.min(Math.max(hdf5OutputType, 0), writerServices.size() - 1));

        // Schreiben des Headers
        Date date = new Date();
        writerService.writeHeader(out, date);

        // Schreiben des Headers der hdf5-Datei
        if (hdf5out != null) {
            hdf5WriterService.writeHeader(hdf5out, inputFile, date);
        }

        // Schleife über alle in der eLamX-Datei enthaltenen Materialien
        if (hdf5out != null) {
            for (Material mat : eLamXLookup.getDefault().lookupAll(Material.class)) {
                // Schreiben der Materialinformationen in hdf5-Datei
                hdf5WriterService.writeMaterialInformation(hdf5out, mat);
            }
        }

        // Schleife über alle in der eLamX-Datei enthaltenen Laminate
        for (Laminat lam : eLamXLookup.getDefault().lookupAll(Laminat.class)) {
            // Schreiben der Laminatinformationen
            writerService.writeLaminateInformation(out, lam);
            if (hdf5out != null) {
                hdf5WriterService.writeLaminateInformation(hdf5out, lam);
            }
            /*
            Schleife über alle Module-Ausgabe-Service-Klasse, um die jeweiligen
            Berechnungen und Ausgaben anzustoßen, z.B. das Berechnungsmodul.
             */
            for (BatchRunService tos : Lookup.getDefault().lookupAll(BatchRunService.class)) {
                tos.performBatchTasksAndOutput(lam, out, hdf5out, outputType);
            }
        }

        // Schließen des OutputStreams
        if (out != System.out) {
            out.close();
        }

        // Schließen der hdf5-Datei
        if (hdf5out != null) {
            hdf5out.close();
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
