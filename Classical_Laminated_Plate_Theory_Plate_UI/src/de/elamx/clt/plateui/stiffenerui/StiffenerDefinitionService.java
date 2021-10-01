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

import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import java.awt.Image;
import java.beans.PropertyEditor;
import javax.swing.ImageIcon;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class StiffenerDefinitionService extends StiffenerProperties {

    public StiffenerDefinitionService(String name, int direction, double position) {
        super(name, direction, position);
    }
    
    public abstract Property[] getPropertyDefinitions();
    
    public abstract ImageIcon getGeometryParameterImage();
    
    public abstract ImageIcon getImage();
    
    public abstract Image getNodeIcon();
    
    public abstract String getDisplayName();
    
    public class Property{
        
        private final String name;
        private final Class<? extends Object> cl;
        private final String displayName;
        private final Class<? extends PropertyEditor> editorClass;
        private final String shortDescription;

        public Property(String name, Class<? extends Object> cl, String displayName, String shortDescription, Class<? extends PropertyEditor> editorClass) {
            this.name = name;
            this.cl = cl;
            this.displayName = displayName;
            this.editorClass = editorClass;
            this.shortDescription = shortDescription;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Object> getCl() {
            return cl;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Class<? extends PropertyEditor> getEditorClass() {
            return editorClass;
        }

        public String getShortDescription() {
            return shortDescription;
        }
    }
}
