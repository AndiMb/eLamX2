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
package de.elamx.clt.optimizationui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

@ActionID(
        category = "OptimizationConstraints",
        id = "de.elamx.clt.optimizationui.actions.DeleteConstraints")
@ActionRegistration(
        iconBase = "de/elamx/clt/optimizationui/resources/optimization.png",
        displayName = "#CTL_DeleteConstraints")
@ActionReferences({
    @ActionReference(path = "eLamXActions/OptimizationConstraints", position = 100)
})
public final class DeleteConstraints implements ActionListener {

    private final List<AbstractNode> context;

    public DeleteConstraints(List<AbstractNode> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (Node ConstraintsNode : context) {
            try {
                ConstraintsNode.destroy();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
