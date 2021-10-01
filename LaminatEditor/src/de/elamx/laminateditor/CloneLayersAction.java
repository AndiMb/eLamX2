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

import de.elamx.laminate.Layer;
import de.elamx.laminateditor.LayerNodeFactory.LayerNode;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Layer",
id = "org.lft.elamx.laminateditorui.actions.CloneLayersAction")
@ActionRegistration(displayName = "#CTL_CloneLayersAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Layer", position = 50)
})
public final class CloneLayersAction implements ActionListener {

    private NumberPanel numberPanel = new NumberPanel();
    private final List<LayerNode> context;

    public CloneLayersAction(List<LayerNode> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {

        final Dialog[] dialogs = new Dialog[1];

        DialogDescriptor dd = new DialogDescriptor(
                numberPanel,
                NbBundle.getBundle(CloneLayersAction.class).getString("CloneLayersAction.Title"),
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION,
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ev) {
                        if (ev.getSource() == DialogDescriptor.OK_OPTION) {
                            try {
                                int n = numberPanel.getNumber();
                                ArrayList<Layer> newLayers = new ArrayList<>(context.size() * n);
                                for (int ii = 0; ii < n; ii++) {
                                    for (LayerNode layerNode : context) {
                                        newLayers.add(layerNode.getLookup().lookup(Layer.class).getCopy());
                                    }
                                }
                                context.get(0).getLaminate().addLayers(newLayers);
                            } catch (NumberFormatException ex) {
                                notifyInvalidInput();
                            }

                            dialogs[0].setVisible(false);
                            dialogs[0].dispose();
                        } else {
                            dialogs[0].setVisible(false);
                            dialogs[0].dispose();
                        }
                    }

                    private void notifyInvalidInput() {
                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                                NbBundle.getMessage(CloneLayersAction.class, "MSG_InvalidValues"),
                                NotifyDescriptor.ERROR_MESSAGE));
                    }
                } // End of annonymnous ActionListener.
                );
        dialogs[0] = DialogDisplayer.getDefault().createDialog(dd);
        dialogs[0].setVisible(true);
    }
}
