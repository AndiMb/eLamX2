/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview;

import de.elamx.fileview.nodes.DefaultMaterialNode;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Material;
import de.elamx.laminate.eLamXLookup;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = MaterialNodeProvider.class)
public class DefaultMaterialNodeProviderImpl implements MaterialNodeProvider{

    @Override
    public Material[] getMaterials() {
        return eLamXLookup.getDefault().lookupAll(DefaultMaterial.class).toArray(new DefaultMaterial[0]);
    }

    @Override
    public Node getNodes(Material mat) {
        if (mat instanceof DefaultMaterial) {
            return new DefaultMaterialNode((DefaultMaterial)mat);
        }
        return null;
    }
    
}
