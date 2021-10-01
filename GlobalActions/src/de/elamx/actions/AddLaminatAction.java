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

import de.elamx.core.actionprovider.LaminateEditorActionProvider;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(category = "Laminates",
id = "de.elamx.core.AddLaminatAction")
@ActionRegistration(iconBase = "de/elamx/actions/resources/addlaminate.png",
displayName = "#CTL_AddLaminatAction")
@ActionReferences({
    @ActionReference(path = "Menu/Laminates", position = 0),
    @ActionReference(path = "Toolbars/Laminates", position = 0),
    @ActionReference(path = "eLamXActions/Laminates", position = 0)
})
public final class AddLaminatAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Laminat lam = new Laminat(UUID.randomUUID().toString(), NbBundle.getMessage(AddLaminatAction.class, "AddLaminatAction.NewLaminate"));
        eLamXLookup.getDefault().add(lam);
        
        LaminateEditorActionProvider al = Lookup.getDefault().lookup(LaminateEditorActionProvider.class);
        if (al != null){
            al.getOpenLaminateEditorAction(lam).actionPerformed(e);
        }
    }
}
