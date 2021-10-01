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
package de.elamx.clt.pressurevesselui.services;

import de.elamx.clt.pressurevessel.PressureVesselInput;
import de.elamx.clt.pressurevessel.optimization.MinimalReserveFactorImplementation;
import de.elamx.clt.pressurevesselui.CLT_PressureVesselTopComponent;
import de.elamx.clt.pressurevesselui.PressureVesselModuleData;
import de.elamx.core.propertyeditor.ForcePropertyEditorSupport;
import de.elamx.core.propertyeditor.MoisturePropertyEditorSupport;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class ConstraintDefinitionNode extends AbstractNode implements PropertyChangeListener {

    private final MinimalReserveFactorImplementation data;

    public ConstraintDefinitionNode() {
        this(new MinimalReserveFactorImplementation());
    }

    public ConstraintDefinitionNode(MinimalReserveFactorImplementation impl) {
        super(Children.LEAF, Lookups.singleton(impl));
        data = this.getLookup().lookup(MinimalReserveFactorImplementation.class);
        data.getInput().addPropertyChangeListener(WeakListeners.propertyChange(this, data.getInput()));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage ("de/elamx/clt/pressurevesselui/resources/pressurevessel.png");
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(PressureVesselModuleData.class, "PressureVesselModule.name");
    }
    
    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        
        try {
                        
            PropertySupport.Reflection<Double> radiusProp = new PropertySupport.Reflection<>(data.getInput(), double.class, PressureVesselInput.PROP_RADIUS);
            radiusProp.setDisplayName(NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.radiusLabel.text"));
            radiusProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            generalProp.put(radiusProp);
            
            RadiusTypeProperty radiusTypeProp = new RadiusTypeProperty(data.getInput());
            generalProp.put(radiusTypeProp);
            
            PropertySupport.Reflection<Double> pressureProp = new PropertySupport.Reflection<>(data.getInput(), double.class, PressureVesselInput.PROP_PRESSURE);
            pressureProp.setDisplayName(NbBundle.getMessage(CLT_PressureVesselTopComponent.class, "CLT_PressureVesselTopComponent.pressureLabel.text"));
            pressureProp.setPropertyEditorClass(MoisturePropertyEditorSupport.class);
            generalProp.put(pressureProp);
            
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        sheet.put(generalProp);

        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PressureVesselInput.PROP_PRESSURE) ||
            evt.getPropertyName().equals(PressureVesselInput.PROP_RADIUS)){
            this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> myActions = Utilities.actionsForPath("eLamXActions/OptimizationConstraints");
        return myActions.toArray(new Action[myActions.size()]);
    }
}