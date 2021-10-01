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
package de.elamx.clt.calculation.action;

import de.elamx.clt.calculation.polar.CLT_PolarChartTopComponent;
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
id = "de.elamx.clt.calculation.OpenPolarChartAction")
@ActionRegistration(iconBase = "de/elamx/clt/calculation/resources/polarchart.png",
displayName = "#CTL_OpenCLT_PolarChartAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Laminate", position = 400),
    @ActionReference(path = "Toolbars/eLamX_Modules_Laminate", position = 800)
})
public final class OpenPolarChartAction implements ActionListener {

    private final Laminat context;

    public OpenPolarChartAction(Laminat context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (CLT_PolarChartTopComponent.uniqueLaminates.add(context)) {
            CLT_PolarChartTopComponent letc = new CLT_PolarChartTopComponent(context);
            letc.open();
            letc.requestActive();
        } else { //In this case, the TopComponent is already open, but needs to become active:
            for (TopComponent tc : WindowManager.getDefault().findMode("editor").getTopComponents()){
                if (tc instanceof CLT_PolarChartTopComponent && tc.getLookup().lookup(Laminat.class) == context){
                    tc.requestActive();
                }
            }
        }
    }
}
