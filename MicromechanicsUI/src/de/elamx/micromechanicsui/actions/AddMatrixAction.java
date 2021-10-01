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

import de.elamx.laminate.eLamXLookup;
import de.elamx.micromechanics.Matrix;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Matrices",
        id = "de.elamx.micromechanicsui.actions.AddMatrixAction")
@ActionRegistration(iconBase = "de/elamx/micromechanicsui/resources/addmatrix.png",
        displayName = "#CTL_AddMatrixAction")
@ActionReferences({
    @ActionReference(path = "Menu/Materials", position = 50),
    @ActionReference(path = "Toolbars/Materials", position = 50),
    @ActionReference(path = "eLamXActions/Matrices", position = 20)
})
public final class AddMatrixAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        /*Matrix[] matrices = new Matrix[5];

        matrices[0] = new Matrix(UUID.randomUUID().toString(), "New Material", 141000.0, 0.35, 1.7, false);
        matrices[0].setE(3390.0);
        matrices[0].setNue(0.41);
        //matrices[0].setG(1210.0);
        matrices[0].setName("EP-'913'");
        matrices[0].setAlpha(0.0);
        matrices[0].setBeta(0.0);
        matrices[0].setRho(1.23E-9);
        matrices[1] = new Matrix(UUID.randomUUID().toString(), "New Material", 141000.0, 0.35, 1.7, false);
        matrices[1].setE(3900.0);
        matrices[1].setNue(0.41);
        //matrices[1].setG(1400.0);
        matrices[1].setName("EP-'914'");
        matrices[1].setAlpha(0.0);
        matrices[1].setBeta(0.0);
        matrices[1].setRho(1.29E-9);
        matrices[2] = new Matrix(UUID.randomUUID().toString(), "New Material", 141000.0, 0.35, 1.7, false);
        matrices[2].setE(3500.0);
        matrices[2].setNue(0.35);
        //matrices[2].setG(1300.0);
        matrices[2].setName("EP-'LY556/Hardener HY 951'");
        matrices[2].setAlpha(63.0);
        matrices[2].setBeta(0.0);
        matrices[2].setRho(1.17E-9);
        matrices[3] = new Matrix(UUID.randomUUID().toString(), "New Material", 141000.0, 0.35, 1.7, false);
        matrices[3].setE(3500.0);
        matrices[3].setNue(0.35);
        //matrices[3].setG(1300.0);
        matrices[3].setName("EP-'LY556/Hardener HY 951'");
        matrices[3].setAlpha(63.0);
        matrices[3].setBeta(0.33);
        matrices[3].setRho(1.17E-9);
        matrices[4] = new Matrix(UUID.randomUUID().toString(), "New Material", 141000.0, 0.35, 1.7, false);
        matrices[4].setE(2890.0);
        matrices[4].setNue(0.35);
        //matrices[4].setG(1070.0);
        matrices[4].setName("EP-'RTM6'");
        matrices[4].setAlpha(65.0);
        matrices[4].setBeta(0.0);
        matrices[4].setRho(1.141E-9);
        
        for (Matrix matrix : matrices) {
            eLamXLookup.getDefault().add(matrix);
        }*/

        Matrix mat = new Matrix(UUID.randomUUID().toString(), NbBundle.getMessage(AddMatrixAction.class, "AddMatrixAction.NewMatrix"), 3500.0, 0.35, 1.17E-9, true);
        mat.setAlpha(60.0E-6);
        mat.setBeta(30.0E-2);
        eLamXLookup.getDefault().add(mat);

        (new PropertiesAction(mat)).actionPerformed(e);
    }
}
