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

import de.elamx.clt.plateui.buckling.BucklingModuleData;
import de.elamx.laminate.Laminat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

@ActionID(category = "Laminate",
id = "de.elamx.clt.buckling.AddBucklingAction")
@ActionRegistration(iconBase = "de/elamx/clt/plateui/resources/buckling.png",
displayName = "#CTL_AddCLT_BucklingAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Laminate", position = 600),
    @ActionReference(path = "Toolbars/eLamX_Modules_Laminate", position = 200)
})
public final class AddBucklingAction implements ActionListener {

    private final Laminat context;

    public AddBucklingAction(Laminat context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        BucklingModuleData data = new BucklingModuleData(context);
        context.getLookup().add(data);
        (new OpenBucklingAction(data)).actionPerformed(ev);
    }
}
