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
package de.elamx.clt.plateui.deformation.load;

import de.elamx.clt.plate.Mechanical.PointLoad;
import de.elamx.clt.plate.Mechanical.SurfaceLoad_const_full;
import de.elamx.clt.plate.Mechanical.TransverseLoad;
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
public class TransverseLoadNode extends AbstractNode implements PropertyChangeListener {

    public static final String LOAD_TYPE = "loadtype";
    
    private final TransverseLoad tLoad;

    public TransverseLoadNode(TransverseLoad tLoad) {
        super(Children.LEAF, Lookups.singleton(tLoad));
        this.tLoad = tLoad;
        tLoad.addPropertyChangeListener(this);
    }

    @Override
    public void setName(String name) {
        tLoad.setName(name);
    }

    @Override
    public String getName() {
        return tLoad.getName();
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
        tLoad.detach();
        tLoad.removePropertyChangeListener(this);
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> myActions = Utilities.actionsForPath("eLamXActions/TransverseLoad");
        return myActions.toArray(new Action[myActions.size()]);
    }
    
    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName("GeneralProperties");
        generalProp.setDisplayName(NbBundle.getMessage(TransverseLoadNode.class, "TransverseLoadNode.GeneralProperties"));

        try {
            PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<>(tLoad, String.class, TransverseLoad.PROP_NAME);
            PropertySupport.ReadOnly<String>   typeProp = new PropertySupport.ReadOnly<String>(LOAD_TYPE, String.class, NbBundle.getMessage(TransverseLoadNode.class, "TransverseLoadNode.loadtype"), NbBundle.getMessage(TransverseLoadNode.class, "TransverseLoadNode.loadtype.short")) {
                
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (tLoad instanceof PointLoad){
                        return NbBundle.getMessage(TransverseLoadNode.class, "TransverseLoadNode.PointLoad.TypeName");
                    }else if (tLoad instanceof SurfaceLoad_const_full){
                        return NbBundle.getMessage(TransverseLoadNode.class, "TransverseLoadNode.SurfaceLoad_const_full.TypeName");
                    }
                    return "";
                }
            };

            nameProp.setDisplayName(NbBundle.getMessage(TransverseLoadNode.class, "TransverseLoadNode.Name"));
            typeProp.setDisplayName(NbBundle.getMessage(TransverseLoadNode.class, "TransverseLoadNode.Type"));
            typeProp.setValue("suppressCustomEditor", Boolean.TRUE);
            
            generalProp.put(nameProp);
            generalProp.put(typeProp);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        sheet.put(generalProp);

        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
}
