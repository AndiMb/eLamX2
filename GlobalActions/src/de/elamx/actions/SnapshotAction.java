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
package de.elamx.actions;

import de.elamx.core.SnapshotService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;

@ActionID(
        category = "eLamX_Modules",
        id = "de.elamx.core.actions.SnapshotAction")
@ActionRegistration(
        iconBase = "de/elamx/actions/resources/snapshot.png",
        displayName = "#CTL_SnapshotAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 200),
    @ActionReference(path = "Toolbars/eLamX_Tools", position = 200)
})
public final class SnapshotAction implements ActionListener {

    private final SnapshotService context;

    public SnapshotAction(SnapshotService context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        File basePath = new File(System.getProperty("user.home"));
        File file = new FileChooserBuilder("snapshot-dir").setTitle(NbBundle.getMessage(SnapshotAction.class, "SnapshotAction.Title"))
                .setDefaultWorkingDirectory(basePath)
                .setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        String name = f.getName().toLowerCase();
                        return name.endsWith(".png") || name.endsWith(".PNG") || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return NbBundle.getMessage(SnapshotAction.class, "SnapshotAction.Description");
                    }
                })
                .setFilesOnly(true).showSaveDialog();

        if (file != null) {
            String filePath = file.getPath();
            if (!filePath.toLowerCase().endsWith(".png")) {
                file = new File(filePath + ".png");
            }
            context.saveSnapshot(file);
        }
    }
}
