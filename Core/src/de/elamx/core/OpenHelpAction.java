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
package de.elamx.core;

import de.elamx.utilities.BrowserLauncher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;

@ActionID(
        category = "Help",
        id = "de.elamx.core.OpenHelpAction"
)
@ActionRegistration(
        displayName = "#CTL_OpenHelpAction"
)
@ActionReference(path = "Menu/Help", position = 100, separatorAfter = 150)
public final class OpenHelpAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            BrowserLauncher.browse("https://github.com/AndiMb/eLamX2/wiki");
        } catch (IOException | URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
