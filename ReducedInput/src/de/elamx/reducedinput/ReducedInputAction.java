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
package de.elamx.reducedinput;

import de.elamx.laminate.failure.Criterion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

@ActionID(
        category = "File",
        id = "de.elamx.reducedinput.ReducedInputAction"
)
@ActionRegistration(
        displayName = "#CTL_ReducedInputAction"
)
@ActionReference(path = "Menu/File", position = 110)
public final class ReducedInputAction implements ActionListener {
    
    HashMap<String, Criterion> criterionMap = new HashMap<>();

    @Override
    public void actionPerformed(ActionEvent e) {
        File basePath = new File(System.getProperty("user.home"));
        File file = new FileChooserBuilder("import-dir").setTitle(NbBundle.getMessage(ReducedInputAction.class, "ReducedImportAction.Title")).setDefaultWorkingDirectory(basePath).setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".elamxb") || name.endsWith(".ELAMXB") || (f.isDirectory() && !f.isHidden());
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(ReducedInputAction.class, "ReducedImportAction.Description");
            }
        }).setSelectionApprover(new FileChooserBuilder.SelectionApprover() {

            @Override
            public boolean approve(File[] selection) {
                if (selection.length > 1) {
                    return false;
                }
                return selection[0].getName().endsWith(".elamxb") | selection[0].getName().endsWith(".ELAMXB");
            }
        }).setApproveText(NbBundle.getMessage(ReducedInputAction.class, "ReducedImportAction.ApproveText")).setFileHiding(true).showOpenDialog();

        if (file != null && file.exists()) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                    SAXParser saxParser = factory.newSAXParser();
                    ReducedInputHandler handler = new ReducedInputHandler();
                    saxParser.parse(file, handler);
                } catch (ParserConfigurationException | SAXException | IOException ex) {
                    Logger.getLogger(ReducedInputAction.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
}
