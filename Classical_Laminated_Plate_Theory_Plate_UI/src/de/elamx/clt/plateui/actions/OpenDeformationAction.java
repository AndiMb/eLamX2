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

import de.elamx.clt.plateui.deformation.CLT_DeformationTopComponent;
import de.elamx.clt.plateui.deformation.DeformationModuleData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(category = "Deformation",
id = "de.elamx.clt.deformation.OpenDeformationAction")
@ActionRegistration(iconBase = "de/elamx/clt/plateui/resources/deformation.png",
displayName = "#CTL_OpenCLT_DeformationAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Deformation", position = 0)
})
public final class OpenDeformationAction implements ActionListener {

    private final DeformationModuleData context;

    public OpenDeformationAction(DeformationModuleData context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (CLT_DeformationTopComponent.uniqueDeformationData.add(context)) {
            CLT_DeformationTopComponent letc = new CLT_DeformationTopComponent(context);
            letc.open();
            letc.requestActive();
        } else { //In this case, the TopComponent is already open, but needs to become active:
            for (TopComponent tc : WindowManager.getDefault().findMode("editor").getTopComponents()){
                if (tc instanceof CLT_DeformationTopComponent && tc.getLookup().lookup(DeformationModuleData.class) == context){
                    tc.requestActive();
                }
            }
        }
    }
}
