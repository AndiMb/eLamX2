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
package de.elamx.micromechanics.models;

import de.elamx.micromechanics.Fiber;
import de.elamx.micromechanics.Matrix;
import java.awt.Color;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class MicroMechModel {
    
    private String displayName;
    private String description;
    private String e11HTMLDescription;
    private String rhoHTMLDescription;
    private String e22HTMLDescription;
    private String nue12HTMLDescription;
    private String g12HTMLDescription;
    private Color  color;
    
    public MicroMechModel(FileObject obj) {
        setDisplayName((String)obj.getAttribute("displayName"));
        setDescription((String)obj.getAttribute("description"));
        setRhoHTMLDescription((String)obj.getAttribute("rhoHTMLDescription"));
        setE11HTMLDescription((String)obj.getAttribute("e11HTMLDescription"));
        setE22HTMLDescription((String)obj.getAttribute("e22HTMLDescription"));
        setNue12HTMLDescription((String)obj.getAttribute("nue12HTMLDescription"));
        setG12HTMLDescription((String)obj.getAttribute("g12HTMLDescription"));
        
        float r = Float.parseFloat((String)obj.getAttribute("color.r"));
        float g = Float.parseFloat((String)obj.getAttribute("color.g"));
        float b = Float.parseFloat((String)obj.getAttribute("color.b"));
        
        setColor(new Color(r,g,b));
    }

    public final String getDescription() {
        return description;
    }

    private final void setDescription(String description) {
        this.description = description;
    }

    public final String getDisplayName() {
        return displayName;
    }

    private final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public final Color getColor() {
        return color;
    }

    private final void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public final String toString(){return getDisplayName();}

    public abstract double getE11(Fiber fiber, Matrix matrix, double phi);
    
    public final String getE11HTMLDescription(){return e11HTMLDescription;}
    
    public final void setE11HTMLDescription(String description){e11HTMLDescription = description;}

    public abstract double getE22(Fiber fiber, Matrix matrix, double phi);
    
    public final String getE22HTMLDescription(){return e22HTMLDescription;}
    
    public final void setE22HTMLDescription(String description){e22HTMLDescription = description;}

    public abstract double getNue12(Fiber fiber, Matrix matrix, double phi);
    
    public final String getNue12HTMLDescription(){return nue12HTMLDescription;}
    
    public final void setNue12HTMLDescription(String description){nue12HTMLDescription = description;}

    public abstract double getG12(Fiber fiber, Matrix matrix, double phi);
    
    public final String getG12HTMLDescription(){return g12HTMLDescription;}
    
    public final void setG12HTMLDescription(String description){g12HTMLDescription = description;}
    
    public double getRho(Fiber fiber, Matrix matrix, double phi){
        return fiber.getRho()*phi + matrix.getRho() * (1.0 - phi);
    }
    
    public final String getRhoHTMLDescription(){return rhoHTMLDescription;}
    
    public final void setRhoHTMLDescription(String description){rhoHTMLDescription = description;}
}
