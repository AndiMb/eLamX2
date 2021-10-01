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
import de.elamx.core.propertyeditor.ForcePropertyEditorSupport;
import de.elamx.core.propertyeditor.PositionPropertyEditorSupport;
import java.awt.Image;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas
 */
public class PointLoadNode extends TransverseLoadNode {
    
    private PointLoad pLoad;

    public PointLoadNode(PointLoad pLoad) {
        super(pLoad);
        this.pLoad = pLoad;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet =  super.createSheet();

        Sheet.Set defaultProp = Sheet.createPropertiesSet();
        defaultProp.setName("PointLoadProperties");
        defaultProp.setDisplayName(NbBundle.getMessage(PointLoadNode.class, "PointLoadNode.PointLoadProperties"));

        try {
            PropertySupport.Reflection<Double> xProp = new PropertySupport.Reflection<>(pLoad, double.class, PointLoad.PROP_X);
            PropertySupport.Reflection<Double> yProp = new PropertySupport.Reflection<>(pLoad, double.class, PointLoad.PROP_Y);
            PropertySupport.Reflection<Double> fProp = new PropertySupport.Reflection<>(pLoad, double.class, PointLoad.PROP_FORCE);

            xProp.setDisplayName(NbBundle.getMessage(PointLoadNode.class, "PointLoadNode.X"));
            xProp.setPropertyEditorClass(PositionPropertyEditorSupport.class);
            yProp.setDisplayName(NbBundle.getMessage(PointLoadNode.class, "PointLoadNode.Y"));
            yProp.setPropertyEditorClass(PositionPropertyEditorSupport.class);
            fProp.setDisplayName(NbBundle.getMessage(PointLoadNode.class, "PointLoadNode.FORCE"));
            fProp.setPropertyEditorClass(ForcePropertyEditorSupport.class);
            
            defaultProp.put(xProp);
            defaultProp.put(yProp);
            defaultProp.put(fProp);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        
        sheet.put(defaultProp);
        
        return sheet;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage ("de/elamx/clt/plateui/resources/PointLoad16.png");
    }
}
