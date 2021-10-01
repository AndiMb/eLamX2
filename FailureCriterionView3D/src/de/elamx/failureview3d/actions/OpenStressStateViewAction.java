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
package de.elamx.failureview3d.actions;

import de.elamx.clt.calculation.LayerResultContainer;
import de.elamx.failureview3d.stressstateview.StressStateViewTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(category = "LayerResultContainer",
id = "de.elamx.failureview3d.actions.OpenStressStateViewAction")
@ActionRegistration(displayName = "#CTL_OpenStressStateViewAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/LayerResultContainer", position = 300)
})
public final class OpenStressStateViewAction implements ActionListener {

    private final LayerResultContainer context;

    public OpenStressStateViewAction(LayerResultContainer context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (StressStateViewTopComponent.uniqueLayerResultContainer.add(context)) {
            StressStateViewTopComponent letc = new StressStateViewTopComponent(context);
            letc.open();
            letc.requestActive();
        } else { //In this case, the TopComponent is already open, but needs to become active:
            for (TopComponent tc : WindowManager.getDefault().findMode("editor").getTopComponents()){
                if (tc instanceof StressStateViewTopComponent && tc.getLookup().lookup(LayerResultContainer.class) == context){
                    tc.requestActive();
                }
            }
        }
    }
}
