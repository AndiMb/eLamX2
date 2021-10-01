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

import de.elamx.core.actionprovider.MaterialEditorActionProvider;
import de.elamx.laminate.DefaultMaterial;
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

@ActionID(category = "Materials",
id = "de.elamx.core.AddMaterialAction")
@ActionRegistration(iconBase = "de/elamx/actions/resources/addmaterial.png",
displayName = "#CTL_AddMaterialAction")
@ActionReferences({
    @ActionReference(path = "Menu/Materials", position = 100),
    @ActionReference(path = "Toolbars/Materials", position = 100),
    @ActionReference(path = "eLamXActions/Materials", position = 100)
})
public final class AddMaterialAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), NbBundle.getMessage(AddMaterialAction.class, "AddMaterialAction.NewMaterial"), 141000.0, 9340.0, 0.35, 4500.0, 1.70E-9, true);
        mat.setAlphaTPar(1.0E-6);
        mat.setAlphaTNor(3.5E-5);
        mat.setBetaPar(1.0E-2);
        mat.setBetaNor(3.8E-1);
        mat.setRParTen(1500.0);
        mat.setRParCom(1000.0);
        mat.setRNorTen(180.0);
        mat.setRNorCom(240.0);
        mat.setRShear(150.0);
        eLamXLookup.getDefault().add(mat);
        
        MaterialEditorActionProvider ep = Lookup.getDefault().lookup(MaterialEditorActionProvider.class);
        if (ep != null){
            ActionListener al = ep.getOpenEditorAction(mat);
            if (al != null){
                al.actionPerformed(e);
            }
        }
    }
}
