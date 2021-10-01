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
import de.elamx.micromechanics.MicroMechanicMaterial;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Matrix",
        id = "de.elamx.core.DeleteMatrixAction")
@ActionRegistration(iconBase = "de/elamx/micromechanicsui/resources/deletematrix.png",
        displayName = "#CTL_DeleteMatrixAction")
@ActionReferences({
    @ActionReference(path = "Menu/Materials", position = 950),
    @ActionReference(path = "eLamXActions/Matrix", position = 900)
})
public final class DeleteMatrixAction implements ActionListener {

    private final List<Matrix> matrices;

    public DeleteMatrixAction(List<Matrix> matrices) {
        this.matrices = matrices;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Matrix matrix : matrices) {
            boolean inUse = false;
            for (MicroMechanicMaterial material : eLamXLookup.getDefault().lookupAll(MicroMechanicMaterial.class)) {
                if (material.getMatrix() == matrix) {
                    inUse = true;
                    break;
                }
            }
            if (inUse) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        NbBundle.getMessage(DeleteMatrixAction.class, "MSG_MatrixInUse", matrix.getName()),
                        NotifyDescriptor.ERROR_MESSAGE));
            } else {
                eLamXLookup.getDefault().remove(matrix);
            }
        }
    }
}
