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
package de.elamx.export;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class ExportNotifier {

    private final ExportOptionsPanel panel;

    public ExportNotifier(ExportOptionsPanel panel) {
        this.panel = panel;
    }

    public void show() {
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(ExportNotifier.class, "ExportNotifier.Title"),
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN,
                null,
                null);

        // let's display the dialog now...
        if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {

            final String exportText = panel.getExport().export();
            
            ExportPanel ePanel = new ExportPanel(exportText);
            
            JButton copyButton = new JButton(NbBundle.getMessage(ExportNotifier.class, "copyButton.caption"));
            copyButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection ss = new StringSelection(exportText);
                    clipboard.setContents(ss, null);
                }
                
            });

            dd = new DialogDescriptor(
                    ePanel,
                    NbBundle.getMessage(ExportNotifier.class, "ExportNotifier.Title"),
                    true,
                    new Object[]{copyButton, DialogDescriptor.OK_OPTION},
                    DialogDescriptor.OK_OPTION,
                    DialogDescriptor.DEFAULT_ALIGN,
                    null,
                    null);
            dd.setClosingOptions(new Object[]{DialogDescriptor.OK_OPTION});

            DialogDisplayer.getDefault().notify(dd);

        }
    }
}