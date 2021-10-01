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
package de.elamx.laminateditor;

import de.elamx.laminate.Laminat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(category = "Laminate",
id = "de.elamx.core.laminateeditor.OpenLaminateEditorAction")
@ActionRegistration(iconBase = "de/elamx/laminateditor/resources/ilr_icon16.png",
displayName = "#CTL_OpenLaminateEditorAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Laminate", position = 100)
})
public final class OpenLaminateEditorAction implements ActionListener {

    private final Laminat context;

    public OpenLaminateEditorAction(Laminat context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (LaminatEditorTopComponent.uniqueLaminates.add(context)) {
            LaminatEditorTopComponent letc = new LaminatEditorTopComponent(context);
            letc.open();
            letc.requestActive();
        } else { //In this case, the TopComponent is already open, but needs to become active:
            for (TopComponent tc : WindowManager.getDefault().findMode("editor").getTopComponents()){
                if (tc instanceof LaminatEditorTopComponent && tc.getLookup().lookup(Laminat.class) == context){
                    tc.requestActive();
                }
            }
        }
    }
}
