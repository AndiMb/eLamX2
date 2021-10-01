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
package de.elamx.clt.cutout;

import de.elamx.core.propertyeditor.ThicknessPropertyEditorSupport;
import java.awt.Image;
import java.beans.PropertyEditorSupport;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author raedel
 */
@ServiceProvider(service=CutoutGeometry.class)
public class CircularCutoutGeometry extends CutoutGeometry{
    
    private static final double MAX_ASPECT_RATIO = Double.POSITIVE_INFINITY;
    
    private static Property[] props;
    
    public CircularCutoutGeometry(){
        this(NbBundle.getMessage(CircularCutoutGeometry.class, "CircularCutoutGeometry.description"), 1.0);
    }
    
    public CircularCutoutGeometry(String name, double a) {
        super(name, a);
        if (props == null) initProperties();
    }
    
    @Override
    public void calcConstants() {
        constants = new double[]{0.0,0.0};
    }

    @Override
    public double[][] getSimplifiedGeometry(double[] angles) {
        
        double[][] xy = new double[2][angles.length];
        
        for (int ii = 0; ii < angles.length; ii++){
            xy[0][ii] = a*Math.cos(Math.toRadians(angles[ii]));
            xy[1][ii] = a*Math.sin(Math.toRadians(angles[ii]));
        }
        
        return xy;
    }
    
    private void initProperties(){
        props = new Property[2];
        
        props[0] = new Property(PROP_NAME, String.class, 
                NbBundle.getMessage(CircularCutoutGeometry.class, "CircularCutoutGeometry.name"), 
                NbBundle.getMessage(CircularCutoutGeometry.class, "CircularCutoutGeometry.name.shortDescription"), 
                PropertyEditorSupport.class);
        props[1] = new Property(PROP_A, double.class, 
                NbBundle.getMessage(CircularCutoutGeometry.class, "CircularCutoutGeometry.a"), 
                NbBundle.getMessage(CircularCutoutGeometry.class, "CircularCutoutGeometry.a.shortDescription"), 
                ThicknessPropertyEditorSupport.class);
    }

    @Override
    public Property[] getPropertyDefinitions() {return props;}
    
    @Override
    public CircularCutoutGeometry getCopy() {
        return new CircularCutoutGeometry(getName(), a);
    }
    
    @Override
    public String getDisplayName(){
        return NbBundle.getMessage(CircularCutoutGeometry.class, "CircularCutoutGeometry.displayname");
    };
    
    @Override
    public double getMaximumAspectRatio() {return MAX_ASPECT_RATIO;}

    @Override
    public ImageIcon getImage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Image getNodeIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
