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
package de.elamx.materialdb;

import de.elamx.laminate.Material;
import de.elamx.laminate.eLamXLookup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

@ActionID(category = "Materials",
        id = "de.elamx.materialdb.ImportAction")
@ActionRegistration(iconBase = "de/elamx/materialdb/configure.png",
        displayName = "#CTL_ImportAction")
@ActionReferences({
    @ActionReference(path = "Menu/Materials", position = 1100, separatorBefore = 1050)
    ,
    @ActionReference(path = "eLamXActions/Materials", position = 1100, separatorBefore = 1050)
})
public final class ImportAction implements ActionListener {

    private MaterialImportPanel panel;

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean useFile = NbPreferences.forModule(MaterialsDBOptionsPanel.class).getBoolean("MaterialsDBOptions.useFile", false);
        String path = NbPreferences.forModule(ImportAction.class).get("MaterialsDBOptions.path", "");

        panel = new MaterialImportPanel();
        if (useFile) {
            panel.setMaterials(MaterialDataBase.getMaterialsFromFile(new File(path)));
        } else {
            panel.setMaterials(MaterialDataBase.getMaterials());
        }
        String importText = NbBundle.getMessage(ImportAction.class, "ImportAction.Dialog.Import");
        String cancelText = NbBundle.getMessage(ImportAction.class, "ImportAction.Dialog.Cancel");
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(ImportAction.class, "ImportAction.Title"),
                true,
                new Object[]{importText, cancelText},
                importText,
                DialogDescriptor.DEFAULT_ALIGN,
                null,
                null);
        if (DialogDisplayer.getDefault().notify(dd) == importText) {
            for (Material m : panel.getSelectedMaterials()) {
                eLamXLookup.getDefault().add(m);
            }
        }
    }
}
