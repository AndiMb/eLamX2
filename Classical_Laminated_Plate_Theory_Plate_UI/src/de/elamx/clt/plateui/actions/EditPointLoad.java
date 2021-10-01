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

import de.elamx.clt.plate.Mechanical.PointLoad;
import de.elamx.clt.plate.Mechanical.SurfaceLoad_const_full;
import de.elamx.clt.plate.Mechanical.TransverseLoad;
import de.elamx.clt.plateui.deformation.load.PointLoadVisualPanel;
import de.elamx.clt.plateui.deformation.load.SurfaceLoad_const_fullVisualPanel;
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
        category = "TransverseLoad",
        id = "de.elamx.clt.bucklingui.actions.EditStiffenerDefinition")
@ActionRegistration(
        iconBase = "de/elamx/clt/plateui/resources/buckling.png",
        displayName = "#CTL_EditTransverseLoad")
@ActionReferences({
    @ActionReference(path = "eLamXActions/TransverseLoad", position = 0)
})
public final class EditPointLoad implements ActionListener {

    private final TransverseLoad context;

    public EditPointLoad(TransverseLoad context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (context instanceof PointLoad){
            showPointLoadEdit((PointLoad)context);
        }else if (context instanceof SurfaceLoad_const_full){
            showSurfaceLoad_Const_FullEdit((SurfaceLoad_const_full)context);
        }
    }
    
    private void showPointLoadEdit(PointLoad load){
        PointLoadVisualPanel panel = new PointLoadVisualPanel();
        panel.setForce(load.getForce());
        panel.setXPosition(load.getX());
        panel.setYPosition(load.getY());
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(EditPointLoad.class, "EditPointLoad.Title", load.getName()),
                true,
                new Object[] { NotifyDescriptor.OK_OPTION },
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN, 
                null,
                null);

        // let's display the dialog now...
        if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
            load.setForce(panel.getForce().doubleValue());
            load.setX(panel.getXPosition().doubleValue());
            load.setY(panel.getYPosition().doubleValue());
        }
    }
    
    private void showSurfaceLoad_Const_FullEdit(SurfaceLoad_const_full load){
        SurfaceLoad_const_fullVisualPanel panel = new SurfaceLoad_const_fullVisualPanel();
        panel.setForce(load.getForce());
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(EditPointLoad.class, "EditSurfaceLoad_const_full.Title", load.getName()),
                true,
                new Object[] { NotifyDescriptor.OK_OPTION },
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN, 
                null,
                null);

        // let's display the dialog now...
        if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
            load.setForce((panel.getForce()).doubleValue());
        }
    }
}
