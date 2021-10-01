/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview;

import de.elamx.laminate.Laminat;
import de.elamx.laminate.modules.eLamXModuleData;
import org.openide.nodes.Node;

/**
 *
 * @author Andreas Hauffe
 */
public interface eLamXModuleDataNodeProvider {
    
    public eLamXModuleData[] geteLamXModuleData(Laminat laminat); 
    
    public Node getNodes(eLamXModuleData moduleData);
    
}
