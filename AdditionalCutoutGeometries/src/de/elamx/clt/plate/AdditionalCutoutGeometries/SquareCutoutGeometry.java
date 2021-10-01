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
package de.elamx.clt.plate.AdditionalCutoutGeometries;

import de.elamx.clt.cutout.CutoutGeometry;
import de.elamx.core.propertyeditor.CutoutTermPropertyEditorSupport;
import de.elamx.core.propertyeditor.ThicknessPropertyEditorSupport;
import java.beans.PropertyEditorSupport;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author raedel
 */
@ServiceProvider(service=CutoutGeometry.class)
public class SquareCutoutGeometry extends RectangularCutoutGeometry{
    
    private static Property[] props;
    
    public SquareCutoutGeometry(){
        this(NbBundle.getMessage(SquareCutoutGeometry.class, "SquareCutoutGeometry.description"), 1.0, 11);
    }
    
    public SquareCutoutGeometry(String name, double a, int terme) {
        super(name, a,a,terme);
        initProperties();
    }
    
    //@Override
    private void initProperties(){
        props = new Property[3];
        
        props[0] = new Property(PROP_NAME, String.class, 
                NbBundle.getMessage(SquareCutoutGeometry.class, "SquareCutoutGeometry.name"), 
                NbBundle.getMessage(SquareCutoutGeometry.class, "SquareCutoutGeometry.name.shortDescription"), 
                PropertyEditorSupport.class);
        props[1] = new Property(PROP_A, double.class, 
                NbBundle.getMessage(SquareCutoutGeometry.class, "SquareCutoutGeometry.a"), 
                NbBundle.getMessage(SquareCutoutGeometry.class, "SquareCutoutGeometry.a.shortDescription"), 
                ThicknessPropertyEditorSupport.class);
        props[2] = new Property(PROP_TERM, int.class, 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "SquareCutoutGeometry.terme"), 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "SquareCutoutGeometry.terme.shortDescription"),
                CutoutTermPropertyEditorSupport.class);
    }
    
    @Override
    public String getDisplayName(){
        return NbBundle.getMessage(SquareCutoutGeometry.class, "SquareCutoutGeometry.displayname");
    };
    
    @Override
    public Property[] getPropertyDefinitions() {return props;}

    @Override
    public SquareCutoutGeometry getCopy() {
        return new SquareCutoutGeometry(getName(), a, terme);
    }
    
}
