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

import de.elamx.clt.calculation.qmatrix.QMatrixPanel;
import de.elamx.laminate.Layer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Layer",
id = "de.elamx.clt.calculation.OpenQMatAction")
@ActionRegistration(iconBase = "de/elamx/clt/calculation/resources/ilr_icon16.png",
displayName = "#CTL_OpenCLT_QMatAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Layer", position = 0)
})
public final class OpenQMatAction implements ActionListener {

    private final Layer context;

    public OpenQMatAction(Layer context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        NotifyDescriptor nd = new NotifyDescriptor(
                new QMatrixPanel(context), 
                NbBundle.getBundle(OpenQMatAction.class).getString("OpenQMatAction.Title"), 
                NotifyDescriptor.DEFAULT_OPTION, 
                NotifyDescriptor.PLAIN_MESSAGE, 
                new Object[] { NotifyDescriptor.OK_OPTION }, 
                NotifyDescriptor.OK_OPTION);
        DialogDisplayer.getDefault().notify(nd);
    }
}
