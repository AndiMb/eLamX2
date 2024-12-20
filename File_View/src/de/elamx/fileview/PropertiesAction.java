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
package de.elamx.fileview;

import de.elamx.laminate.Material;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(
        category = "Material",
        id = "de.elamx.fileview.PropertiesAction"
        )
@ActionRegistration(
        iconBase = "de/elamx/fileview/resources/edit.png",
        displayName = "#CTL_PropertiesAction"
        )
@ActionReferences({
    @ActionReference(path = "eLamXActions/Material", position = 0)
})
public final class PropertiesAction implements ActionListener {

    private final Material context;

    public PropertiesAction(Material context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Node n = null;
        for (MaterialNodeProvider matProv : Lookup.getDefault().lookupAll(MaterialNodeProvider.class)) {
            n = matProv.getNodes(context);
            if (n != null){
                break;
            }
        }
        
        if (n == null){
            return;
        }
        
        PropertiesPanel panel = new PropertiesPanel(n);
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(PropertiesAction.class, "PropertiesAction.Title", context.getName()),
                true,
                new Object[] { NotifyDescriptor.OK_OPTION },
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN, 
                null,
                null);
        DialogDisplayer.getDefault().notify(dd);
    }
}
