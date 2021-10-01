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
package de.elamx.micromechanicsui.actions;

import de.elamx.laminate.Material;
import de.elamx.laminate.eLamXLookup;
import de.elamx.micromechanics.Matrix;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Matrix",
id = "de.elamx.micromechanicsui.actions.CopyMatrixAction")
@ActionRegistration(displayName = "#CTL_CopyMatrixAction")
@ActionReferences({
    @ActionReference(path = "Menu/Materials", position = 150),
    @ActionReference(path = "eLamXActions/Matrix", position = 50)
})
public final class CopyMatrixAction implements ActionListener {
    
    private final List<Matrix> context;
    
    public CopyMatrixAction(List<Matrix> materials){
        this.context = materials;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Matrix material : context) {
            Material copy = material.getCopy();
            copy.setName(copy.getName() + "-" + NbBundle.getMessage(CopyMatrixAction.class, "CopyOf_Suffix"));
            eLamXLookup.getDefault().add(copy);
        }
    }
}
