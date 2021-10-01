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
package de.elamx.filesupport;

import de.elamx.laminate.eLamXLookup;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.netbeans.api.actions.Savable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

@ActionID(
        category = "File",
        id = "de.elamx.filesupport.SaveAction"
)
@ActionRegistration(
        iconBase = "de/elamx/filesupport/resources/save.png",
        displayName = "#CTL_SaveAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1450),
    @ActionReference(path = "Toolbars/File", position = 200)
})
public final class SaveAction extends AbstractAction implements LookupListener {

    private final Lookup.Result<Savable> result;

    public SaveAction() {
        super();
        result = Savable.REGISTRY.lookupResult(Savable.class);
        result.addLookupListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (eLamXLookup.getDefault().getFileObject().toURI().toString().startsWith("memory://")) {
            (new SaveAsAction()).actionPerformed(e);
        } else {
            if (result.allInstances().isEmpty()) {
                refreshEnabled(false);
            } else {
                for (Savable s : result.allInstances()) {
                    try {
                        s.save();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    @Override
    public void setEnabled(boolean newValue) {
        super.setEnabled(newValue); //To change body of generated methods, choose Tools | Templates.
    }

    private void refreshEnabled(final boolean isEnabled) {
        if (EventQueue.isDispatchThread()) {
            setEnabled(isEnabled);
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setEnabled(isEnabled);
                }
            });
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        refreshEnabled(!result.allItems().isEmpty());
    }
}
