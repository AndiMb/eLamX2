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
package de.elamx.clt.cutoutui.actions;

import de.elamx.clt.cutoutui.CutoutModuleData;
import de.elamx.laminate.Laminat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

/**
 *
 * @author raedel
 */
@ActionID(category = "Laminate",
id = "de.elamx.clt.cutoutui.actions.AddCutoutAction")
@ActionRegistration(iconBase = "de/elamx/clt/cutoutui/resources/cutout.png",
displayName = "#CTL_AddCLT_CutoutAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Laminate", position = 900),
    @ActionReference(path = "Toolbars/eLamX_Modules_Laminate", position = 500)
})
public class AddCutoutAction implements ActionListener {

    private final Laminat context;

    public AddCutoutAction(Laminat context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        CutoutModuleData data = new CutoutModuleData(context);
        context.getLookup().add(data);
        (new OpenCutoutAction(data)).actionPerformed(ev);
    }
    
}
