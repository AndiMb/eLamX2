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
package de.elamx.clt.plate.view3d;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import de.elamx.clt.plate.Boundary.Boundary;
import de.elamx.clt.plate.Boundary.Boundary_CC_200;
import de.elamx.clt.plate.Boundary.Boundary_CF_200;
import de.elamx.clt.plate.Boundary.Boundary_FF_200;
import de.elamx.clt.plate.Boundary.Boundary_SC_200;
import de.elamx.clt.plate.Boundary.Boundary_SF_200;
import de.elamx.clt.plate.Boundary.Boundary_SS_200;
import de.elamx.clt.plate.Input;
import de.view3d.View3DProperties;
import java.util.List;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class Plate <T extends Input>{

    protected double zScale = 1.0;
    protected double elemsize_x = 1.0;
    protected double elemsize_y = 1.0;
    protected double maxsize = 1.0;
    protected double deltax = 1.0;
    protected double deltay = 1.0;
    protected int enum_x = 1;
    protected int enum_y = 1;
    protected int n;
    protected int m;
    protected Boundary bx, by;
    protected double width;
    protected double length;
    protected final T input;

    @SuppressWarnings("this-escape")
    public Plate(T input) {
        this.input = input;
        init();
    }

    public T getInput() {
        return input;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public Boundary getBx() {
        return bx;
    }

    public Boundary getBy() {
        return by;
    }

    protected void init() {
        width = input.getWidth();
        length = input.getLength();

        m = input.getM();
        n = input.getN();

        // create new object boundary x-direction with integrals needed for
        // calculation based on boundary condition and geometry
        switch (input.getBcx()) {
            case 0:
                bx = new Boundary_SS_200(length, m);
                break;
            case 1:
                bx = new Boundary_CC_200(length, m);
                break;
            case 2:
                bx = new Boundary_CF_200(length, m);
                break;
            case 3:
                bx = new Boundary_FF_200(length, m);
                break;
            case 4:
                bx = new Boundary_SC_200(length, m);
                break;
            case 5:
                bx = new Boundary_SF_200(length, m);
                break;
            default:
                bx = new Boundary_SS_200(length, m);
        }
        // create new object boundary y-direction with integrals needed for
        // calculation based on boundary condition and geometry
        switch (input.getBcy()) {
            case 0:
                by = new Boundary_SS_200(width, n);
                break;
            case 1:
                by = new Boundary_CC_200(width, n);
                break;
            case 2:
                by = new Boundary_CF_200(width, n);
                break;
            case 3:
                by = new Boundary_FF_200(width, n);
                break;
            case 4:
                by = new Boundary_SC_200(width, n);
                break;
            case 5:
                by = new Boundary_SF_200(width, n);
                break;
            default:
                by = new Boundary_SS_200(width, n);
        }

        // Netzerzeugung

        int numOfElements = (int)(View3DProperties.getDefault().getNetQuality()*100);
        int minNumOfElements = 10;
        if (length >= width) {
            maxsize = length;
            enum_x = numOfElements;
            elemsize_x = length / enum_x;

            enum_y = (int) (width / elemsize_x);
            if (enum_y < minNumOfElements) {
                enum_y = minNumOfElements;
            }
            elemsize_y = width / enum_y;
        } else {
            maxsize = width;
            enum_y = numOfElements;
            elemsize_y = width / enum_y;

            enum_x = (int) (length / elemsize_y);
            if (enum_x < minNumOfElements) {
                enum_x = minNumOfElements;
            }
            elemsize_x = length / enum_x;
        }

        deltax = length / 2.0;
        deltay = width / 2.0;
    }

    public double getZScale() {
        return zScale;
    }

    public double getElementSizeX() {
        return elemsize_x;
    }

    public double getElementSizeY() {
        return elemsize_y;
    }

    public double getDeltaX() {
        return deltax;
    }

    public double getDeltaY() {
        return deltay;
    }

    public int getElementNumberX() {
        return enum_x;
    }

    public int getElementNumberY() {
        return enum_y;
    }

    public double getMaximumSize() {
        return maxsize;
    }

    public abstract List<Mesh> getShapes(boolean reinit);

    public ColorRGBA getRainbowColor(double value) {
        int r = 0;
        int g = 255;
        int b = 0;

        if (value < 0.25) {
            r = 255;
        } else if (value <= 0.5 && value >= 0.25) {
            r = (int) (255.0 - 255.0 * (value - 0.25) / 0.25);
        }

        if (value <= 0.25) {
            g = (int) (255.0 * value / 0.25);
        } else if (value >= 0.75) {
            g = (int) (255.0 - 255.0 * (value - 0.75) / 0.25);
        }

        if (value > 0.75) {
            b = 255;
        } else if (value >= 0.5 && value <= 0.75) {
            b = (int) (255.0 * (value - 0.5) / 0.25);
        }

        return new ColorRGBA(r/255.0f, g/255.0f, b/255.0f, 1.0f);
    }
    
    public abstract List<Node> getUndeformedWithBC();
    
    public static void setStiffenerAppearance(Mesh mesh){
        
        mesh.setSolidColor(new ColorRGBA(0.5f,0.5f,0.5f,1.0f));
        
        // Add a material state
        final MaterialState ms = new MaterialState();
        // Pull diffuse color for front from mesh color
        ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
        ms.setColorMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        mesh.setRenderState(ms);
    }

    public static void setPlateAppearance(Mesh mesh){
        
        // Add a material state
        final MaterialState ms = new MaterialState();
        // Pull diffuse color for front from mesh color
        ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
        ms.setColorMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        mesh.setRenderState(ms);
    }
}
