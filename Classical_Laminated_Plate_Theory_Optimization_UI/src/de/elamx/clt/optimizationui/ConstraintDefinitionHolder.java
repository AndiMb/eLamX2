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
package de.elamx.clt.optimizationui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author Andreas Hauffe
 */
public class ConstraintDefinitionHolder implements NodeListener {

    private final ArrayList<Node> nodes = new ArrayList<>();

    public static final String PROP_NODES = "Nodes";

    /**
     * Get the value of string
     *
     * @return the value of string
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Set the value of string
     *
     * @param string new value of string
     */
    public void addNode(Node node) {
        nodes.add(node);
        node.addNodeListener(this);
        propertyChangeSupport.firePropertyChange(PROP_NODES, null, node);
    }

    public void removeNode(Node node) {
        node.removeNodeListener(this);
        nodes.remove(node);
        propertyChangeSupport.firePropertyChange(PROP_NODES, node, null);
    }

    public void clear() {
        for (Node node : nodes) {
            node.removeNodeListener(this);
        }
        nodes.clear();
        propertyChangeSupport.firePropertyChange(PROP_NODES, nodes, null);
    }

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public Node[] getNodesArray() {
        return nodes.toArray(new Node[nodes.size()]);
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        removeNode(ev.getNode());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
