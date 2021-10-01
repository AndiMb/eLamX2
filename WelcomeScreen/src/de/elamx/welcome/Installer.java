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
package de.elamx.welcome;

import java.util.Set;
import org.openide.modules.ModuleInstall;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.WindowSystemEvent;
import org.openide.windows.WindowSystemListener;

public class Installer extends ModuleInstall implements Runnable {

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(this);
        WindowManager.getDefault().addWindowSystemListener(new WindowSystemListener() {

            @Override
            public void beforeLoad(WindowSystemEvent event) {
            }

            @Override
            public void afterLoad(WindowSystemEvent event) {
            }

            @Override
            public void beforeSave(WindowSystemEvent event) {
                WindowManager.getDefault().removeWindowSystemListener(this);
                WelcomeComponent topComp = null;
                boolean isEditorShowing = false;
                Set<TopComponent> tcs = TopComponent.getRegistry().getOpened();
                for (Mode mode : WindowManager.getDefault().getModes()) {
                    TopComponent tc = mode.getSelectedTopComponent();
                    if (tc instanceof WelcomeComponent) {
                        topComp = (WelcomeComponent) tc;
                    }
                    if (null != tc && WindowManager.getDefault().isEditorTopComponent(tc)) {
                        isEditorShowing = true;
                    }
                }
                if (WelcomeOptions.getDefault().isShowOnStartup()) {
                    if (!isEditorShowing) {
                        if (topComp == null) {
                            topComp = WelcomeComponent.findComp();
                        }
                        //activate welcome screen at shutdown to avoid editor initialization
                        //before the welcome screen is activated again at startup
                        topComp.open();
                        topComp.requestActive();
                    }
                } else if (topComp != null) {
                    topComp.close();
                }
            }

            @Override
            public void afterSave(WindowSystemEvent event) {
            }
        });
    }

    @Override
    public void run() {
        //FeedbackSurvey.start();
    }

}
