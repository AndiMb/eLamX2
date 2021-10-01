/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview;

import de.elamx.laminate.Material;
import org.openide.nodes.Node;

/**
 *
 * @author Andreas Hauffe
 */
public interface MaterialNodeProvider {
    public Material[] getMaterials(); 
    
    public Node getNodes(Material mat);
}
