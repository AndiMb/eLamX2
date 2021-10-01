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

import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;
import de.elamx.clt.plate.Boundary.Boundary;
import de.elamx.clt.plate.Input;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;

/**
 *
 * @author Andreas
 */
public class Stiffenerx {
    
    protected static Mesh getShape(Plate<? extends Input> p, StiffenerProperties stiffener, double[][] eigenvector){

        double zScale     = p.getZScale();
        int enum_x        = p.getElementNumberX();
        double elemsize_x = p.getElementSizeX();
        double maxsize    = p.getMaximumSize();

        Boundary bx = p.getBx();
        Boundary by = p.getBy();
        
        int m = p.getM();
        int n = p.getN();

        int numPoints = enum_x*4;
        Vector3[] vertices = new Vector3[numPoints];

        double[] dZ = new double[enum_x+1];
        double yTemp, xTemp, zTemp;

        double[] eigen;

        // calculation of plate deflection for all grid positions

        double[] bx_wx = new double[m];
        double[] by_wx = new double[n];
        
        yTemp = stiffener.getPosition() + by.getA()/2.0;
        for (int nn = 0; nn < n; nn++)
            by_wx[nn] = by.wx(nn,yTemp);

        for (int ii = 0; ii <= enum_x; ii++){

            xTemp = ii*elemsize_x;
            for (int mm = 0; mm < n; mm++)
                bx_wx[mm] = bx.wx(mm,xTemp);
            zTemp = 0.0;
            
            for (int mm = 0; mm < m; mm++){
                eigen = eigenvector[mm];
                for(int nn = 0; nn < n; nn++){
                    zTemp += eigen[nn]*bx_wx[mm]*by_wx[nn];
                }
            }
            dZ[ii] = zTemp;
        }

        double deltax = p.getDeltaX();
        yTemp = (yTemp-p.getDeltaY())/maxsize;
        double height = 0.01;

        // creation of Quad-Elements and adding of coordinates in kartesian plate coordinate system
        for (int ii = 0; ii < enum_x; ii++){
            xTemp = elemsize_x*ii;
            xTemp = (xTemp-deltax)/maxsize;
            zTemp = zScale*dZ[ii]-height;
            vertices[ii*4] = new Vector3((float)xTemp, (float)yTemp, (float)(zTemp));

            zTemp = zScale*dZ[ii]+height;
            vertices[ii*4+1] = new Vector3((float)xTemp, (float)yTemp, (float)(zTemp));

            xTemp = elemsize_x*(ii+1);
            xTemp = (xTemp-deltax)/maxsize;
            zTemp = zScale*dZ[ii+1]+height;
            vertices[ii*4+2] = new Vector3((float)xTemp, (float)yTemp, (float)(zTemp));

            zTemp = zScale*dZ[ii+1]-height;
            vertices[ii*4+3] = new Vector3((float)xTemp, (float)yTemp, (float)(zTemp));
        }
        
        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();

        return mesh;
    }
    
}
