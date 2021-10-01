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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = LifecycleManager.class, position = 1)
public class eLamXLifeCycleManager extends LifecycleManager {

    @Override
    public void saveAll() {
    }

    @Override
    public void exit() {
        boolean close = true;
        if (eLamXLookup.getDefault().getDataObject().isModified()) {
            NotifyDescriptor.Confirmation message = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Installer.class, "SaveFileMessage", eLamXLookup.getDefault().getFileObject().getName()),
                    NotifyDescriptor.YES_NO_CANCEL_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE);

            Object result = DialogDisplayer.getDefault().notify(message);
            //When user clicks "Yes", indicating they really want to save,
            //we need to disable the Save action,
            //so that it will only be usable when the next change is made
            //to the JTextField:
            if (NotifyDescriptor.YES_OPTION.equals(result)) {
                Action a = FileUtil.getConfigObject("Actions/File/de-elamx-filesupport-SaveAction.instance", Action.class);
                if (a != null) {
                    if (EventQueue.isDispatchThread()) {
                        a.actionPerformed(null);
                    } else {
                        try {
                            EventQueue.invokeAndWait(() -> {
                                a.actionPerformed(null);
                            });
                        } catch (InterruptedException | InvocationTargetException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                if (eLamXLookup.getDefault().getDataObject().isModified()) {
                    close = false;
                }
            } else if (NotifyDescriptor.NO_OPTION.equals(result)) {
                eLamXLookup.getDefault().setModified(false);
            } else {
                close = false;
            }
        }
        if (close) {
            Collection<? extends LifecycleManager> c = Lookup.getDefault().lookupAll(LifecycleManager.class);
            for (LifecycleManager lm : c) {
                if (lm != this) {
                    lm.exit();
                }
            }
        }
    }

    @Override
    public void markForRestart() throws UnsupportedOperationException {
        /*String classLoaderName = TopSecurityManager.class.getClassLoader().getClass().getName();
        if (!classLoaderName.endsWith(".Launcher$AppClassLoader") && !classLoaderName.endsWith(".ClassLoaders$AppClassLoader")) {   // NOI18N
            throw new UnsupportedOperationException("not running in regular module system, cannot restart"); // NOI18N
        }*/
        File userdir = Places.getUserDirectory();
        if (userdir == null) {
            throw new UnsupportedOperationException("no userdir"); // NOI18N
        }
        File restartFile = new File(userdir, "var/restart"); // NOI18N
        if (!restartFile.exists()) {
            try {
                restartFile.createNewFile();
            } catch (IOException x) {
                throw new UnsupportedOperationException(x);
            }
        }
    }

}
