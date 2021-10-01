/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elamx.fileview;

import org.openide.nodes.Node;

/**
 *
 * @author Andreas Hauffe
 */
public interface ELamXNodeProvider {
    
    public Node getNode();
    
    public int getPosition();
}
