/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elamx.fileview.nodeprovider;

import de.elamx.fileview.ELamXNodeProvider;
import de.elamx.fileview.nodes.MaterialsNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = ELamXNodeProvider.class)
public class MaterialsNodeProvider implements ELamXNodeProvider{
    
    @Override
    public Node getNode() {
        return new MaterialsNode();
    }

    @Override
    public int getPosition() {
        return 100;
    }
}
