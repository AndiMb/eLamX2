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
package de.elamx.clt.calculation.services;

import de.elamx.clt.CLT_Input;
import de.elamx.clt.calculation.CalculationModuleData;
import de.elamx.clt.calculation.calc.CalculationPanel;
import de.elamx.clt.optimization.MinimalReserveFactorImplementation;
import de.elamx.core.propertyeditor.ForcePropertyEditorSupport;
import de.elamx.core.propertyeditor.MoisturePropertyEditorSupport;
import de.elamx.core.propertyeditor.StrainPropertyEditorSupport;
import de.elamx.core.propertyeditor.TemperaturPropertyEditorSupport;
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
        return ImageUtilities.loadImage ("de/elamx/clt/calculation/resources/kcalc.png");
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(CalculationModuleData.class, "CalculationModule.name");
    }
    
    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        
        try {
            
            
            /*
             Nx/Ex
            */
            SigEpsProperty sigEps0 = new SigEpsProperty(data.getInput(), 0);
            generalProp.put(sigEps0);
            
            PropertySupport.Reflection<Double> nxProp = new PropertySupport.Reflection<Double>(data.getInput().getLoad(), double.class, CLT_Input.PROP_N_X){
                @Override
                public boolean canWrite() {
                    return !data.getInput().isUseStrains(0);
                }
                
            };
            nxProp.setDisplayName(NbBundle.getMessage(CalculationPanel.class, "CLT_CalculationPanel.ForceRB0.text"));
            nxProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            generalProp.put(nxProp);
            
            PropertySupport.Reflection<Double> exProp = new PropertySupport.Reflection<Double>(data.getInput().getStrains(), double.class, CLT_Input.PROP_EPSILON_X){
                @Override
                public boolean canWrite() {
                    return data.getInput().isUseStrains(0);
                }
                
            };
            exProp.setDisplayName(NbBundle.getMessage(ConstraintDefinitionNode.class, "DisplRB0.text"));
            exProp.setPropertyEditorClass(StrainPropertyEditorSupport.class);
            generalProp.put(exProp);
            
            
            
            
            /*
             Ny/Ey
            */
            SigEpsProperty sigEps1 = new SigEpsProperty(data.getInput(), 1);
            generalProp.put(sigEps1);
            
            PropertySupport.Reflection<Double> nyProp = new PropertySupport.Reflection<Double>(data.getInput().getLoad(), double.class, CLT_Input.PROP_N_Y){
                @Override
                public boolean canWrite() {
                    return !data.getInput().isUseStrains(1);
                }
                
            };
            nyProp.setDisplayName(NbBundle.getMessage(CalculationPanel.class, "CLT_CalculationPanel.ForceRB1.text"));
            nyProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            generalProp.put(nyProp);
            
            PropertySupport.Reflection<Double> eyProp = new PropertySupport.Reflection<Double>(data.getInput().getStrains(), double.class, CLT_Input.PROP_EPSILON_Y){
                @Override
                public boolean canWrite() {
                    return data.getInput().isUseStrains(1);
                }
                
            };
            eyProp.setDisplayName(NbBundle.getMessage(ConstraintDefinitionNode.class, "DisplRB1.text"));
            eyProp.setPropertyEditorClass(StrainPropertyEditorSupport.class);
            generalProp.put(eyProp);
            
            
            
            
            /*
             Nxy/Exy
            */            
            SigEpsProperty sigEps2 = new SigEpsProperty(data.getInput(), 2);
            generalProp.put(sigEps2);
            
            PropertySupport.Reflection<Double> nxyProp = new PropertySupport.Reflection<Double>(data.getInput().getLoad(), double.class, CLT_Input.PROP_N_XY){
                @Override
                public boolean canWrite() {
                    return !data.getInput().isUseStrains(2);
                }
                
            };
            nxyProp.setDisplayName(NbBundle.getMessage(CalculationPanel.class, "CLT_CalculationPanel.ForceRB2.text"));
            nxyProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            generalProp.put(nxyProp);
            
            PropertySupport.Reflection<Double> exyProp = new PropertySupport.Reflection<Double>(data.getInput().getStrains(), double.class, CLT_Input.PROP_GAMMA_XY){
                @Override
                public boolean canWrite() {
                    return data.getInput().isUseStrains(2);
                }
                
            };
            exyProp.setDisplayName(NbBundle.getMessage(ConstraintDefinitionNode.class, "DisplRB2.text"));
            exyProp.setPropertyEditorClass(StrainPropertyEditorSupport.class);
            generalProp.put(exyProp);
            
            
            
            
            /*
             Mx/Kx
            */            
            SigEpsProperty sigEps3 = new SigEpsProperty(data.getInput(), 3);
            generalProp.put(sigEps3);
            
            PropertySupport.Reflection<Double> mxProp = new PropertySupport.Reflection<Double>(data.getInput().getLoad(), double.class, CLT_Input.PROP_M_X){
                @Override
                public boolean canWrite() {
                    return !data.getInput().isUseStrains(3);
                }
                
            };
            mxProp.setDisplayName(NbBundle.getMessage(CalculationPanel.class, "CLT_CalculationPanel.ForceRB3.text"));
            mxProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            generalProp.put(mxProp);
            
            PropertySupport.Reflection<Double> kxProp = new PropertySupport.Reflection<Double>(data.getInput().getStrains(), double.class, CLT_Input.PROP_KAPPA_X){
                @Override
                public boolean canWrite() {
                    return data.getInput().isUseStrains(3);
                }
                
            };
            kxProp.setDisplayName(NbBundle.getMessage(ConstraintDefinitionNode.class, "DisplRB3.text"));
            kxProp.setPropertyEditorClass(StrainPropertyEditorSupport.class);
            generalProp.put(kxProp);
            
            
            
            
            /*
             My/Ky
            */    
            SigEpsProperty sigEps4 = new SigEpsProperty(data.getInput(), 4);
            generalProp.put(sigEps4);
            
            PropertySupport.Reflection<Double> myProp = new PropertySupport.Reflection<Double>(data.getInput().getLoad(), double.class, CLT_Input.PROP_M_Y){
                @Override
                public boolean canWrite() {
                    return !data.getInput().isUseStrains(4);
                }
                
            };
            myProp.setDisplayName(NbBundle.getMessage(CalculationPanel.class, "CLT_CalculationPanel.ForceRB4.text"));
            myProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            generalProp.put(myProp);
            
            PropertySupport.Reflection<Double> kyProp = new PropertySupport.Reflection<Double>(data.getInput().getStrains(), double.class, CLT_Input.PROP_KAPPA_Y){
                @Override
                public boolean canWrite() {
                    return data.getInput().isUseStrains(4);
                }
                
            };
            kyProp.setDisplayName(NbBundle.getMessage(ConstraintDefinitionNode.class, "DisplRB4.text"));
            kyProp.setPropertyEditorClass(StrainPropertyEditorSupport.class);
            generalProp.put(kyProp);
            
            
            
            
            /*
             Mxy/Kxy
            */                
            SigEpsProperty sigEps5 = new SigEpsProperty(data.getInput(), 5);
            generalProp.put(sigEps5);
            
            PropertySupport.Reflection<Double> mxyProp = new PropertySupport.Reflection<Double>(data.getInput().getLoad(), double.class, CLT_Input.PROP_M_XY){
                @Override
                public boolean canWrite() {
                    return !data.getInput().isUseStrains(5);
                }
                
            };
            mxyProp.setDisplayName(NbBundle.getMessage(CalculationPanel.class, "CLT_CalculationPanel.ForceRB5.text"));
            mxyProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            generalProp.put(mxyProp);
            
            PropertySupport.Reflection<Double> kxyProp = new PropertySupport.Reflection<Double>(data.getInput().getStrains(), double.class, CLT_Input.PROP_KAPPA_XY){
                @Override
                public boolean canWrite() {
                    return data.getInput().isUseStrains(5);
                }
                
            };
            kxyProp.setDisplayName(NbBundle.getMessage(ConstraintDefinitionNode.class, "DisplRB5.text"));
            kxyProp.setPropertyEditorClass(StrainPropertyEditorSupport.class);
            generalProp.put(kxyProp);
            
            PropertySupport.Reflection<Double> deltaTProp = new PropertySupport.Reflection<>(data.getInput().getLoad(), double.class, CLT_Input.PROP_DELTAT);
            deltaTProp.setDisplayName(NbBundle.getMessage(CalculationPanel.class, "CLT_CalculationPanel.deltaTemplabel.text"));
            deltaTProp.setPropertyEditorClass(TemperaturPropertyEditorSupport.class);
            generalProp.put(deltaTProp);
            
            PropertySupport.Reflection<Double> deltaHProp = new PropertySupport.Reflection<>(data.getInput().getLoad(), double.class, CLT_Input.PROP_DELTAH);
            deltaHProp.setDisplayName(NbBundle.getMessage(CalculationPanel.class, "CLT_CalculationPanel.deltaHyglabel.text"));
            deltaHProp.setPropertyEditorClass(MoisturePropertyEditorSupport.class);
            generalProp.put(deltaHProp);
            
            
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        sheet.put(generalProp);

        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(CLT_Input.PROP_N_X) ||
            evt.getPropertyName().equals(CLT_Input.PROP_N_Y) ||
            evt.getPropertyName().equals(CLT_Input.PROP_N_XY) ||
            evt.getPropertyName().equals(CLT_Input.PROP_N_X) ||
            evt.getPropertyName().equals(CLT_Input.PROP_N_Y) ||
            evt.getPropertyName().equals(CLT_Input.PROP_N_XY) ||
            evt.getPropertyName().equals(CLT_Input.PROP_DELTAT) ||
            evt.getPropertyName().equals(CLT_Input.PROP_DELTAH)){
            this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> myActions = Utilities.actionsForPath("eLamXActions/OptimizationConstraints");
        return myActions.toArray(new Action[myActions.size()]);
    }
}