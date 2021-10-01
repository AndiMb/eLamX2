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
import de.elamx.clt.plateui.buckling.CLT_BucklingTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(category = "Buckling",
id = "de.elamx.clt.buckling.OpenBucklingAction")
@ActionRegistration(iconBase = "de/elamx/clt/plateui/resources/buckling.png",
displayName = "#CTL_OpenCLT_BucklingAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Buckling", position = 0)
})
public final class OpenBucklingAction implements ActionListener {

    private final BucklingModuleData context;

    public OpenBucklingAction(BucklingModuleData context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (CLT_BucklingTopComponent.uniqueBucklingData.add(context)) {
            CLT_BucklingTopComponent letc = new CLT_BucklingTopComponent(context);
            letc.open();
            letc.requestActive();
        } else { //In this case, the TopComponent is already open, but needs to become active:
            for (TopComponent tc : WindowManager.getDefault().findMode("editor").getTopComponents()){
                if (tc instanceof CLT_BucklingTopComponent && tc.getLookup().lookup(BucklingModuleData.class) == context){
                    tc.requestActive();
                }
            }
        }
    }
}
