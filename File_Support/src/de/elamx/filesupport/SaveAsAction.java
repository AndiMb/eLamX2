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
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.netbeans.api.actions.Savable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "de.elamx.filesupport.SaveAsAction"
)
@ActionRegistration(
        displayName = "#CTL_SaveAsAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1460)
})
public final class SaveAsAction extends AbstractAction {

    public SaveAsAction() {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        saveAs();
    }

    public void saveAs() {
        File newFile = getNewFileName();
        if (null != newFile) {
            //create target folder if necessary    
            FileObject newFolder = null;
            try {
                File targetFolder = newFile.getParentFile();
                if (null == targetFolder) {
                    throw new IOException(newFile.getAbsolutePath());
                }
                newFolder = FileUtil.createFolder(targetFolder);
            } catch (IOException ioE) {
                NotifyDescriptor error = new NotifyDescriptor(
                        NbBundle.getMessage(DataObject.class, "MSG_CannotCreateTargetFolder"), //NOI18N
                        NbBundle.getMessage(DataObject.class, "LBL_SaveAsTitle"), //NOI18N
                        NotifyDescriptor.DEFAULT_OPTION,
                        NotifyDescriptor.ERROR_MESSAGE,
                        new Object[]{NotifyDescriptor.OK_OPTION},
                        NotifyDescriptor.OK_OPTION);
                DialogDisplayer.getDefault().notify(error);
                return;
            }

            try {
                // Bestimmen des Dateinamens ohne den Suffix ".elamx"
                String filename = newFile.getName();
                int index = filename.lastIndexOf(".elamx");
                if (filename.lastIndexOf(".elamx") > 0) {
                    filename = filename.substring(0, index);
                }

                // Altes DataObject auf nicht modifiziert setzen
                DataObject oldDO = eLamXLookup.getDefault().getDataObject();
                oldDO.setModified(false);

                // Falls bereits eine Datei mit dem neuen Namen existiert -> löschen
                FileObject newFO = newFolder.getFileObject(filename, "elamx");
                if (null != newFO) {
                    newFO.delete();
                }

                // alte Datei an die Stelle der neuen Datei kopieren
                newFO = FileUtil.copyFile(oldDO.getPrimaryFile(), newFolder, filename);
                try {
                    oldDO.setValid(false);
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }

                // altes DataObject durch neues im eLamXLookup ersetzen
                eLamXLookup.getDefault().setChangable(false);
                DataObject newDO = DataObject.find(newFO);
                eLamXLookup.getDefault().setDataObject(newDO);
                eLamXLookup.getDefault().setChangable(true);

                // Speichern mit setModified(true) erzwingen
                eLamXLookup.getDefault().setModified(true);
                int count = 0;
                while (Savable.REGISTRY.lookupAll(Savable.class).isEmpty() && count < 10) {
                    Thread.sleep(100);
                    count++;
                }
                if (count == 10) {
                    NotifyDescriptor nd = new NotifyDescriptor(
                            NbBundle.getMessage(SaveAsAction.class, "MSG_SaveAs_SaveAsErrorMessage", newFile.getName()), //NOI18N
                            NbBundle.getMessage(SaveAsAction.class, "MSG_SaveAs_SaveAsErrorTitle"), //NOI18N
                            NotifyDescriptor.OK_CANCEL_OPTION,
                            NotifyDescriptor.ERROR_MESSAGE,
                            new Object[]{NotifyDescriptor.OK_OPTION}, NotifyDescriptor.OK_OPTION);
                }
                for (Savable s : Savable.REGISTRY.lookupAll(Savable.class)) {
                    s.save();
                }
                eLamXLookup.getDefault().setModified(false);
            } catch (IOException ioE) {
                Exceptions.attachLocalizedMessage(ioE,
                        NbBundle.getMessage(DataObject.class, "MSG_SaveAsFailed", // NOI18N
                                newFile.getName(),
                                ioE.getLocalizedMessage()));
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ioE);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Show file 'save as' dialog window to ask user for a new file name.
     *
     * @return File selected by the user or null if no file was selected.
     */
    private File getNewFileName() {
        File newFile = null;
        File currentFile = null;
        FileObject currentFileObject = getCurrentFileObject();
        if (null != currentFileObject) {
            newFile = FileUtil.toFile(currentFileObject);
            currentFile = newFile;
            if (null == newFile) {
                newFile = new File(currentFileObject.getNameExt());
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(NbBundle.getMessage(DataObject.class, "LBL_SaveAsTitle")); //NOI18N
        chooser.setMultiSelectionEnabled(false);
        if (null != newFile) {
            chooser.setSelectedFile(newFile);
            chooser.setCurrentDirectory(newFile.getParentFile());
        }
        File initialFolder = getInitialFolderFrom(newFile);
        if (null != initialFolder) {
            chooser.setCurrentDirectory(initialFolder);
        }
        File origFile = newFile;
        while (true) {
            if (JFileChooser.APPROVE_OPTION != chooser.showSaveDialog(WindowManager.getDefault().getMainWindow())) {
                return null;
            }
            newFile = chooser.getSelectedFile();
            if (null == newFile) {
                break;
            }
            if (newFile.equals(origFile)) {
                NotifyDescriptor nd = new NotifyDescriptor(
                        NbBundle.getMessage(DataObject.class, "MSG_SaveAs_SameFileSelected"), //NOI18N
                        NbBundle.getMessage(DataObject.class, "MSG_SaveAs_SameFileSelected_Title"), //NOI18N
                        NotifyDescriptor.DEFAULT_OPTION,
                        NotifyDescriptor.INFORMATION_MESSAGE,
                        new Object[]{NotifyDescriptor.OK_OPTION}, NotifyDescriptor.OK_OPTION);
                DialogDisplayer.getDefault().notify(nd);
            } else if (newFile.exists()) {
                NotifyDescriptor nd = new NotifyDescriptor(
                        NbBundle.getMessage(DataObject.class, "MSG_SaveAs_OverwriteQuestion", newFile.getName()), //NOI18N
                        NbBundle.getMessage(DataObject.class, "MSG_SaveAs_OverwriteQuestion_Title"), //NOI18N
                        NotifyDescriptor.YES_NO_OPTION,
                        NotifyDescriptor.QUESTION_MESSAGE,
                        new Object[]{NotifyDescriptor.NO_OPTION, NotifyDescriptor.YES_OPTION}, NotifyDescriptor.NO_OPTION);
                if (NotifyDescriptor.YES_OPTION == DialogDisplayer.getDefault().notify(nd)) {
                    break;
                }
            } else {
                break;
            }
        }
        if (isFromUserDir(currentFile)) {
            File lastUsedDir = chooser.getCurrentDirectory();
            NbPreferences.forModule(SaveAction.class).put("lastUsedDir", lastUsedDir.getAbsolutePath()); //NOI18N
        }

        return newFile;
    }

    private FileObject getCurrentFileObject() {
        return eLamXLookup.getDefault().getFileObject();
    }

    /**
     * @param newFile File being 'saved as'
     * @return Initial folder selected in file chooser. If the file is in
     * netbeans user dir then user's os-dependent home dir or last used folder
     * will be used instead of file's parent folder.
     */
    private File getInitialFolderFrom(File newFile) {
        File res = new File(System.getProperty("user.home")); //NOI18N
        if (null != newFile) {
            File parent = newFile.getParentFile();
            if (isFromUserDir(parent)) {
                String strLastUsedDir = NbPreferences.forModule(SaveAction.class).get("lastUsedDir", res.getAbsolutePath()); //NOI18N
                res = new File(strLastUsedDir);
                if (!res.exists() || !res.isDirectory()) {
                    res = new File(System.getProperty("user.home")); //NOI18N
                }
            } else {
                res = parent;
            }
        }
        return res;
    }

    /**
     * @param file
     * @return True if given file is netbeans user dir.
     */
    private boolean isFromUserDir(File file) {
        if (null == file) {
            return false;
        }
        File nbUserDir = new File(System.getProperty("netbeans.user")); //NOI18N
        return file.getAbsolutePath().startsWith(nbUserDir.getAbsolutePath());
    }
}
