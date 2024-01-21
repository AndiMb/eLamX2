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
import de.elamx.core.outputStreamService;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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

    @Override
    protected void process(Env env, Map<Option, String[]> maps) throws CommandException {
        if (maps.containsKey(inputOption)) {
            String fileName = maps.get(inputOption)[0];
            File inputFile = new File(fileName);
            if (inputFile.exists() && inputFile.isFile()) {
                FileObject fo = FileUtil.toFileObject(inputFile);
                eLamXLookup.getDefault().setModified(false);
                eLamXLookup.getDefault().setFileObject(fo, true);
            }
        }

        PrintStream out = System.out;
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

        for (Laminat lam : eLamXLookup.getDefault().lookupAll(Laminat.class)) {
            for (outputStreamService tos : Lookup.getDefault().lookupAll(outputStreamService.class)) {
                tos.writeToStream(lam, out);
            }
        }

        if (out != System.out) {
            out.close();
        }
        
        eLamXLookup.getDefault().setModified(false);

        LifecycleManager.getDefault().exit();
    }

}
