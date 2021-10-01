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
package de.elamx.clt.plateui.stiffenerui.wizard;

import de.elamx.clt.plateui.stiffenerui.StiffenerDefinitionService;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class StiffenerDefinitionServiceNode extends AbstractNode{
    
    private final StiffenerDefinitionService service;

    public StiffenerDefinitionServiceNode(StiffenerDefinitionService service) {
        super(Children.LEAF);
        this.service = service;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet =  super.createSheet();

        Sheet.Set defaultProp = Sheet.createPropertiesSet();
        defaultProp.setName("DefaultProperties");
        defaultProp.setDisplayName(NbBundle.getMessage(StiffenerDefinitionServiceNode.class, "StiffenerDefinitionServiceNode.GeometricProperties"));

        try {
            for (StiffenerDefinitionService.Property p : service.getPropertyDefinitions()) {
                if (p.getCl() == double.class){
                    PropertySupport.Reflection<Double> nodeProp = new PropertySupport.Reflection<>(service, double.class, p.getName());
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
