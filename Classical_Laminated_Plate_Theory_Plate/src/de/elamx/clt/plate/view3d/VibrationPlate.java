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
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.geom.BufferUtils;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.clt.plate.VibrationInput;
import de.elamx.clt.plate.VibrationResult;
import static de.elamx.clt.plate.view3d.Plate.setPlateAppearance;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author raedel
 */
public class VibrationPlate extends Plate<VibrationInput> {

    private final VibrationResult result;
    private int eigenvectorNumber = 0;

    public VibrationPlate(VibrationInput input, VibrationResult result) {
        super(input);
        this.result = result;
    }

    public int getEigenvectorNumber() {
        return eigenvectorNumber;
    }

    public void setEigenvectorNumber(int eigenvectorNumber) {
        this.eigenvectorNumber = eigenvectorNumber;
    }

    @Override
    public List<Mesh> getShapes(boolean reinit) {
        if (reinit) {
            init();
        }

        double maxz = 0.0;
        double scale = 0.1;
        //double zScale = 0.0;

        int numPoints = enum_x * enum_y * 4;

        Vector3[] vertices = new Vector3[numPoints];
        ColorRGBA[] colors = new ColorRGBA[numPoints];

        double[][] dZ = new double[enum_x + 1][enum_y + 1];
        double yTemp, xTemp, zTemp;

        double[] eigen;

        // calculation of plate deflection for all grid positions
        double[] bx_wx = new double[m];
        double[] by_wx = new double[n];

        double[][] eigenvector = result.getEigenvectors_()[eigenvectorNumber];

        for (int jj = 0; jj <= enum_y; jj++) {
            yTemp = jj * elemsize_y;
            for (int nn = 0; nn < n; nn++) {
                by_wx[nn] = by.wx(nn, yTemp);
            }

            for (int ii = 0; ii <= enum_x; ii++) {

                xTemp = ii * elemsize_x;
                for (int mm = 0; mm < n; mm++) {
                    bx_wx[mm] = bx.wx(mm, xTemp);
                }
                zTemp = 0.0;

                for (int mm = 0; mm < m; mm++) {
                    eigen = eigenvector[mm];
                    for (int nn = 0; nn < n; nn++) {
                        //zTemp += eigen[nn]*bx.wx(mm,xTemp)*by.wx(nn,yTemp);     // Ritz-approach w=sum(c*X(x)*Y(y))
                        zTemp += eigen[nn] * bx_wx[mm] * by_wx[nn];
                    }
                }

                if (Math.abs(zTemp) > maxz) {
                    maxz = Math.abs(zTemp);
                }
                dZ[ii][jj] = zTemp;
            }
        }
        zScale = scale / maxz;

        // creation of Quad-Elements and adding of coordinates in kartesian plate coordinate system
        ColorRGBA farbe;
        for (int jj = 0; jj < enum_y; jj++) {
            for (int ii = 0; ii < enum_x; ii++) {
                xTemp = elemsize_x * ii;
                yTemp = elemsize_y * jj;
                xTemp = (xTemp - deltax) / maxsize;
                yTemp = (yTemp - deltay) / maxsize;
                zTemp = zScale * dZ[ii][jj];
                vertices[(ii + jj * enum_x) * 4] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                farbe = getRainbowColor(1.0 - Math.abs(zTemp / scale));
                colors[(ii + jj * enum_x) * 4] = farbe;

                xTemp = elemsize_x * (ii + 1);
                xTemp = (xTemp - deltax) / maxsize;
                zTemp = zScale * dZ[ii + 1][jj];
                vertices[(ii + jj * enum_x) * 4 + 1] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                farbe = getRainbowColor(1.0 - Math.abs(zTemp / scale));
                colors[(ii + jj * enum_x) * 4 + 1] = farbe;

                yTemp = elemsize_y * (jj + 1);
                yTemp = (yTemp - deltay) / maxsize;
                zTemp = zScale * dZ[ii + 1][jj + 1];
                vertices[(ii + jj * enum_x) * 4 + 2] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                farbe = getRainbowColor(1.0 - Math.abs(zTemp / scale));
                colors[(ii + jj * enum_x) * 4 + 2] = farbe;

                xTemp = elemsize_x * ii;
                xTemp = (xTemp - deltax) / maxsize;
                zTemp = zScale * dZ[ii][jj + 1];
                vertices[(ii + jj * enum_x) * 4 + 3] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                farbe = getRainbowColor(1.0 - Math.abs(zTemp / scale));
                colors[(ii + jj * enum_x) * 4 + 3] = farbe;
            }
        }

        ArrayList<Mesh> shapes = new ArrayList<>();

        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setColorBuffer(BufferUtils.createFloatBuffer(colors));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();
        setPlateAppearance(mesh);

        shapes.add(mesh);

        for (StiffenerProperties s : input.getStiffenerProperties()) {
            if (s.getDirection() == StiffenerProperties.X_DIRECTION) {
                Mesh shape = Stiffenerx.getShape(this, s, eigenvector);
                setStiffenerAppearance(shape);
                shapes.add(shape);
            } else if (s.getDirection() == StiffenerProperties.Y_DIRECTION) {
                Mesh shape = Stiffenery.getShape(this, s, eigenvector);
                setStiffenerAppearance(shape);
                shapes.add(shape);
            }
        }

        return shapes;
    }

    @Override
    public List<Node> getUndeformedWithBC() {
        ArrayList<Node> group = new ArrayList<>();

        return group;
    }

}
