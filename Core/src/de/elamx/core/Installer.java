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

import java.awt.GraphicsEnvironment;
import javax.swing.UIManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isMacOs = osName.startsWith("mac os x");
        boolean headless = GraphicsEnvironment.isHeadless();
        GlobalProperties.getDefault().setHeadless(headless);
        
        System.setProperty("nb.useSwingHtmlRendering", "true");
        System.setProperty("ps.quickSearch.disabled.global", "true");
        
        if (!isMacOs && !headless) {
            UIManager.put("NbMainWindow.showCustomBackground", Boolean.TRUE);
            RootFrame.init();
        }
    }

    @Override
    public void validate() throws IllegalStateException {
        NbPreferences.root().node("laf").put("laf", "com.formdev.flatlaf.FlatLightLaf");
        super.validate(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
}
