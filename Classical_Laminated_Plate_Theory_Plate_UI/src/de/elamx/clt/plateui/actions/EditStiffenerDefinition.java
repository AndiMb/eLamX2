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
package de.elamx.clt.plateui.actions;

import de.elamx.clt.plateui.stiffenerui.StiffenerDefinitionService;
import de.elamx.clt.plateui.stiffenerui.wizard.StiffenerDefinitionVisualPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(
        category = "Stiffener",
        id = "de.elamx.clt.bucklingui.actions.EditStiffenerDefinition")
@ActionRegistration(
        iconBase = "de/elamx/clt/plateui/resources/buckling.png",
        displayName = "#CTL_EditStiffenerDefinition")
@ActionReferences({
    @ActionReference(path = "eLamXActions/StiffenerDefinitions", position = 0)
})
public final class EditStiffenerDefinition implements ActionListener {

    private final StiffenerDefinitionService context;

    public EditStiffenerDefinition(StiffenerDefinitionService context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        StiffenerDefinitionVisualPanel panel = new StiffenerDefinitionVisualPanel();
        panel.setStiffenerDefinitionService(context);
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(EditStiffenerDefinition.class, "EditStiffenerDefinition.Title", context.getName()),
                true,
                new Object[] { NotifyDescriptor.OK_OPTION },
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN, 
                null,
                null);

        // let's display the dialog now...
        if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
            panel.flush();
        }
    }
}
