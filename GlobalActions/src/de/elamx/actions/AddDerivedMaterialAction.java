/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.actions;

import de.elamx.laminate.DerivedMaterial;
import de.elamx.laminate.Material;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

@ActionID(
        category = "Materials",
        id = "de.elamx.core.AddDerivedMaterialAction"
)
@ActionRegistration(
        iconBase = "de/elamx/actions/resources/addmaterial.png",
        displayName = "#CTL_AddDerivedMaterialAction"
)
@ActionReferences({
    @ActionReference(path = "eLamXActions/Material", position = 50)
})
public final class AddDerivedMaterialAction implements ActionListener {

    private final Material context;

    public AddDerivedMaterialAction(Material context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.addDerivedMaterial(new DerivedMaterial(UUID.randomUUID().toString(), "New Derived Material", context, true));
    }
}
