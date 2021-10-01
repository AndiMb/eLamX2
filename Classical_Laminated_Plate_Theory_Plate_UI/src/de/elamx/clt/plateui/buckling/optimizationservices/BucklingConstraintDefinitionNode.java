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
package de.elamx.clt.plateui.buckling.optimizationservices;

import de.elamx.clt.plate.BucklingInput;
import de.elamx.clt.plate.MinimalBucklingReserveFactorImpl;
import de.elamx.clt.plateui.buckling.BucklingModuleData;
import de.elamx.clt.plateui.buckling.InputPanel;
import de.elamx.core.propertyeditor.ForcePropertyEditorSupport;
import de.elamx.core.propertyeditor.ThicknessPropertyEditorSupport;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ResourceBundle;
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
public class BucklingConstraintDefinitionNode extends AbstractNode implements PropertyChangeListener {
    
    public static final String PROP_SET_LOAD     = "LoadProperties";
    public static final String PROP_SET_GEOMETRY = "GeometryProperties";
    private static final ResourceBundle BUNDLE = NbBundle.getBundle(BucklingConstraintDefinitionNode.class);

    private final MinimalBucklingReserveFactorImpl data;

    public BucklingConstraintDefinitionNode() {
        this(new MinimalBucklingReserveFactorImpl());
    }
    
    public BucklingConstraintDefinitionNode(MinimalBucklingReserveFactorImpl impl){
        super(Children.LEAF, Lookups.singleton(impl));
        data = this.getLookup().lookup(MinimalBucklingReserveFactorImpl.class);
        data.getInput().addPropertyChangeListener(WeakListeners.propertyChange(this, data.getInput()));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/elamx/clt/plateui/resources/buckling.png");
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(BucklingModuleData.class, "BucklingModule.name");
    }
    
    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set loadProp = Sheet.createPropertiesSet();
        loadProp.setName(PROP_SET_LOAD);
        loadProp.setDisplayName(BUNDLE.getString("ConstraintDefinitionNode.LOADPROPERTIES"));
        
        Sheet.Set geometryProp = Sheet.createPropertiesSet();
        geometryProp.setName(PROP_SET_GEOMETRY);
        geometryProp.setDisplayName(BUNDLE.getString("ConstraintDefinitionNode.GEOMETRYPROPERTIES"));
        
        try {
            PropertySupport.Reflection<Double> nxProp = new PropertySupport.Reflection<>(data.getInput(), double.class, BucklingInput.PROP_NX);
            nxProp.setDisplayName(NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel5.text"));
            nxProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            loadProp.put(nxProp);
            
            PropertySupport.Reflection<Double> nyProp = new PropertySupport.Reflection<>(data.getInput(), double.class, BucklingInput.PROP_NY);
            nyProp.setDisplayName(NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel6.text"));
            nyProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            loadProp.put(nyProp);
            
            PropertySupport.Reflection<Double> nxyProp = new PropertySupport.Reflection<>(data.getInput(), double.class, BucklingInput.PROP_NXY);
            nxyProp.setDisplayName(NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel7.text"));
            nxyProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            loadProp.put(nxyProp);
            
            PropertySupport.Reflection<Double> lengthProp = new PropertySupport.Reflection<>(data.getInput(), double.class, BucklingInput.PROP_LENGTH);
            lengthProp.setDisplayName(NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel1.text"));
            lengthProp.setPropertyEditorClass(ThicknessPropertyEditorSupport.class);
            geometryProp.put(lengthProp);
            
            PropertySupport.Reflection<Double> widthProp = new PropertySupport.Reflection<>(data.getInput(), double.class, BucklingInput.PROP_WIDTH);
            widthProp.setDisplayName(NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel2.text"));
            widthProp.setPropertyEditorClass(ThicknessPropertyEditorSupport.class);
            geometryProp.put(widthProp);
            
            PropertySupport.ReadWrite<Integer> bxProp = new BoundaryConditionPropertyEditorSupport(data.getInput(), BoundaryConditionPropertyEditorSupport.BOUND_BX);
            geometryProp.put(bxProp);
            
            PropertySupport.ReadWrite<Integer> byProp = new BoundaryConditionPropertyEditorSupport(data.getInput(), BoundaryConditionPropertyEditorSupport.BOUND_BY);
            geometryProp.put(byProp);
            
            PropertySupport.Reflection<Integer> mProp = new PropertySupport.Reflection<Integer>(data.getInput(), int.class, BucklingInput.PROP_M){
                @Override
                public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    data.getInput().setM(val);
                    data.getInput().setN(val);
                }
            };
            mProp.setDisplayName(NbBundle.getMessage(InputPanel.class, "InputPanel.jLabel8.text"));
            mProp.setPropertyEditorClass(TermPropertyEditorSupport.class);
            geometryProp.put(mProp);
            
            
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        sheet.put(loadProp);
        sheet.put(geometryProp);

        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BucklingInput.PROP_NX) ||
            evt.getPropertyName().equals(BucklingInput.PROP_NY) ||
            evt.getPropertyName().equals(BucklingInput.PROP_NXY)){
            this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> myActions = Utilities.actionsForPath("eLamXActions/OptimizationConstraints");
        return myActions.toArray(new Action[myActions.size()]);
    }
}