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
package de.elamx.filesupport;

import de.elamx.laminate.eLamXLookup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

@ActionID(category = "File",
id = "de.elamx.filesupport.OpenFileAction")
@ActionRegistration(iconBase = "de/elamx/filesupport/resources/fileopen.png",
displayName = "#CTL_OpenFileAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 0),
    @ActionReference(path = "Toolbars/File", position = 0)
})
public final class OpenFileAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        File basePath = new File(System.getProperty("user.home"));
        File file = new FileChooserBuilder("database-dir").setTitle(NbBundle.getMessage(OpenFileAction.class, "OpenFileAction.Title")).setDefaultWorkingDirectory(basePath).setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".elamx") || name.endsWith(".ELAMX") || (f.isDirectory() && !f.isHidden());
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(OpenFileAction.class, "OpenFileAction.Description");
            }
        }).setSelectionApprover(new FileChooserBuilder.SelectionApprover() {

            @Override
            public boolean approve(File[] selection) {
                if (selection.length > 1) return false;
                return selection[0].getName().endsWith(".elamx") | selection[0].getName().endsWith(".ELAMX");
            }
        }).setApproveText(NbBundle.getMessage(OpenFileAction.class, "OpenFileAction.ApproveText")).setFileHiding(true).showOpenDialog();

        if (file != null && file.exists()) {
            FileObject fo = FileUtil.toFileObject(file);
            eLamXLookup.getDefault().setFileObject(fo);
        }
    }
}
