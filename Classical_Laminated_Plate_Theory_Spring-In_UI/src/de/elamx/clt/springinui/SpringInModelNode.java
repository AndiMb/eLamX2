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
package de.elamx.clt.springinui;

import de.elamx.clt.springin.SpringInModel;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author raedel
 */
public class SpringInModelNode extends AbstractNode{
    
    private final SpringInModel sim;

    public SpringInModelNode(SpringInModel sim) {
        super(Children.LEAF);
        this.sim = sim;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet =  super.createSheet();

        Sheet.Set defaultProp = Sheet.createPropertiesSet();
        defaultProp.setName("DefaultProperties");
        defaultProp.setDisplayName(NbBundle.getMessage(SpringInModelNode.class, "SpringInModelNode.InputProperties"));

        try {
            for (SpringInModel.Property p : sim.getPropertyDefinitions()) {
                if (p.getCl() == double.class){
                    PropertySupport.Reflection<Double> nodeProp = new PropertySupport.Reflection<>(sim, double.class, p.getName());
                    nodeProp.setDisplayName(p.getDisplayName());
                    nodeProp.setPropertyEditorClass(p.getEditorClass());
                    nodeProp.setShortDescription(p.getShortDescription());
                    defaultProp.put(nodeProp);
                }else if (p.getCl() == int.class){
                    PropertySupport.Reflection<Integer> nodeProp = new PropertySupport.Reflection<>(sim, int.class, p.getName());
                    nodeProp.setDisplayName(p.getDisplayName());
                    nodeProp.setPropertyEditorClass(p.getEditorClass());
                    nodeProp.setShortDescription(p.getShortDescription());
                    defaultProp.put(nodeProp);
                }else if (p.getCl() == String.class){
                    PropertySupport.Reflection<String> nodeProp = new PropertySupport.Reflection<>(sim, String.class, p.getName());
                    nodeProp.setDisplayName(p.getDisplayName());
                    nodeProp.setPropertyEditorClass(p.getEditorClass());
                    nodeProp.setShortDescription(p.getShortDescription());
                    defaultProp.put(nodeProp);
                }
            }
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        
        sheet.put(defaultProp);
        
        return sheet;
    }
    
}
