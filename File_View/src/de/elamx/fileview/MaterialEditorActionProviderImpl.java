/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview;

import de.elamx.core.actionprovider.MaterialEditorActionProvider;
import de.elamx.laminate.Material;
import java.awt.event.ActionListener;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service=MaterialEditorActionProvider.class)
public class MaterialEditorActionProviderImpl implements MaterialEditorActionProvider {

    @Override
    public ActionListener getOpenEditorAction(Material material) {
        return new PropertiesAction(material);
    }
    
}
