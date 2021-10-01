/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview;

import de.elamx.laminate.Material;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(
        category = "Material",
        id = "de.elamx.fileview.PropertiesAction"
        )
@ActionRegistration(
        iconBase = "de/elamx/fileview/resources/edit.png",
        displayName = "#CTL_PropertiesAction"
        )
@ActionReferences({
    @ActionReference(path = "eLamXActions/Material", position = 0)
})
public final class PropertiesAction implements ActionListener {

    private final Material context;

    public PropertiesAction(Material context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Node n = null;
        for (MaterialNodeProvider matProv : Lookup.getDefault().lookupAll(MaterialNodeProvider.class)) {
            n = matProv.getNodes(context);
            if (n != null){
                break;
            }
        }
        
        if (n == null){
            return;
        }
        
        PropertiesPanel panel = new PropertiesPanel(n);
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(PropertiesAction.class, "PropertiesAction.Title", context.getName()),
                true,
                new Object[] { NotifyDescriptor.OK_OPTION },
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN, 
                null,
                null);
        DialogDisplayer.getDefault().notify(dd);
    }
}
