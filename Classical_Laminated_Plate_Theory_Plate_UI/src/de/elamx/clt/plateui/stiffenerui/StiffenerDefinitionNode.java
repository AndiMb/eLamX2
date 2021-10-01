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
package de.elamx.clt.plateui.stiffenerui;

import de.elamx.clt.plate.Stiffener.Properties.DefaultStiffenerProperties;
import de.elamx.core.ELamXDecimalFormat;
import de.elamx.core.GlobalProperties;
import de.elamx.core.propertyeditor.PositionPropertyEditorSupport;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas
 */
public class StiffenerDefinitionNode extends AbstractNode implements PropertyChangeListener {

    private final StiffenerDefinitionService props;

    public StiffenerDefinitionNode(StiffenerDefinitionService props) {
        super(Children.LEAF, Lookups.singleton(props));
        this.props = props;
        props.addPropertyChangeListener(this);
    }

    @Override
    public void setName(String name) {
        props.setName(name);
    }

    @Override
    public String getName() {
        return props.getName();
    }

    @Override
    public boolean canRename() {
        return true;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        props.detach();
        props.removePropertyChangeListener(this);
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> myActions = Utilities.actionsForPath("eLamXActions/StiffenerDefinitions");
        return myActions.toArray(new Action[myActions.size()]);
    }
    
    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName("GeneralProperties");
        generalProp.setDisplayName(NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.GeneralProperties"));

        try {
            PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<>(props, String.class, DefaultStiffenerProperties.PROP_NAME);
            PropertySupport.Reflection<Double> positionProp = new PropertySupport.Reflection<>(props, double.class, DefaultStiffenerProperties.PROP_POSITION);

            nameProp.setDisplayName(NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.Name"));
            positionProp.setDisplayName(NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.Position"));
            positionProp.setPropertyEditorClass(PositionPropertyEditorSupport.class);
            
            generalProp.put(nameProp);
            generalProp.put(new DirectionProperty(props));
            generalProp.put(positionProp);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        sheet.put(generalProp);
        
        Sheet.Set defaultProp = Sheet.createPropertiesSet();
        defaultProp.setName("GeometryProperties");
        defaultProp.setDisplayName(NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.GeometryProperties"));

        try {
            for (StiffenerDefinitionService.Property p : props.getPropertyDefinitions()) {
                if (p.getCl() == double.class){
                    PropertySupport.Reflection<Double> nodeProp = new PropertySupport.Reflection<>(props, double.class, p.getName());
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
        
        if (props.getClass().getName().indexOf("DefaultStiffenerProperties") == -1){
            final ELamXDecimalFormat df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);
            
            Sheet.Set calcedProp = Sheet.createPropertiesSet();
            calcedProp.setName("AdditionalProperties");
            calcedProp.setDisplayName(NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.AdditionalProperties"));
            
            PropertySupport.ReadOnly<String>   AProp = new PropertySupport.ReadOnly<String>("A", String.class, NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.A"), NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.A.short")) {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return df.format(props.getA());
                }
            };
            AProp.setValue("suppressCustomEditor", Boolean.TRUE);
            calcedProp.put(AProp);
            
            PropertySupport.ReadOnly<String>   IProp = new PropertySupport.ReadOnly<String>("I", String.class, NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.I"), NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.I.short")) {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return df.format(props.getI());
                }
            };
            IProp.setValue("suppressCustomEditor", Boolean.TRUE);
            calcedProp.put(IProp);
            
            PropertySupport.ReadOnly<String>   JProp = new PropertySupport.ReadOnly<String>("J", String.class, NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.J"), NbBundle.getMessage(StiffenerDefinitionNode.class, "StiffenerDefinitionNode.J.short")) {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return df.format(props.getJ());
                }
            };
            JProp.setValue("suppressCustomEditor", Boolean.TRUE);
            calcedProp.put(JProp);
            
            sheet.put(calcedProp);
        }

        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    @Override
    public Image getIcon(int type) {
        return props.getNodeIcon();
    }
}
