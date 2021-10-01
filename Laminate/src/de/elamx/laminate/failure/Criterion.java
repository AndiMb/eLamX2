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
package de.elamx.laminate.failure;

import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import de.elamx.laminate.StressStrainState;
import de.elamx.mathtools.MatrixTools;
import java.awt.Color;
import org.openide.filesystems.FileObject;

/**
 * Diese abstrakte Klasse stellt das Standardobjekt für ein Kriterium dar.
 *
 * @author Andreas Hauffe - TU-Dresden - 10.11.2008
 */
public abstract class Criterion {

    private String displayName;
    private String description;
    private Color color;

    public Criterion(FileObject obj) {
        setDisplayName((String) obj.getAttribute("displayName"));
        setDescription((String) obj.getAttribute("description"));

        float r = Float.parseFloat((String) obj.getAttribute("color.r"));
        float g = Float.parseFloat((String) obj.getAttribute("color.g"));
        float b = Float.parseFloat((String) obj.getAttribute("color.b"));

        setColor(new Color(r, g, b));
    }

    public abstract ReserveFactor getReserveFactor(Material material, Layer layer, StressStrainState stressStateState);

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    private void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Color getColor() {
        return color;
    }

    private void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public Mesh getAsMesh(Material material, double quality) {

        int numPoints_phi = (int) (quality * 30);         // Anzahl der Punkte in phi-Richtung
        int numPoints_theta = 2 * numPoints_phi;           // Anzahl der Punkte in theta-Richtung

        double detlaTheta = Math.PI / (numPoints_theta - 1);
        double deltaPhi = 2.0 * Math.PI / (numPoints_phi - 1);
        
        double scaleX = (material.getRParTen() + material.getRParCom()) / 2.0;
        double scaleYZ = (material.getRNorTen() + material.getRNorCom() + material.getRShear()) / 3.0;
        double transX = (material.getRParTen() - material.getRParCom()) / 2.0;
        
        double nu21 = material.getNue21();

        double temp = 1.0 / (1.0 - material.getNue12() * nu21);
        
        double[][] Qlok = new double[3][3];

        Qlok[0][0] = temp * material.getEpar();
        Qlok[0][1] = temp * material.getEpar() * nu21;
        Qlok[0][2] = 0.0;
        Qlok[1][0] = Qlok[0][1];
        Qlok[1][1] = temp * material.getEnor();
        Qlok[1][2] = 0.0;
        Qlok[2][0] = 0.0;
        Qlok[2][1] = 0.0;
        Qlok[2][2] = material.getG();
        
        double[][] Slok = MatrixTools.getInverse(Qlok);

        double x, y, z;
        
        double[][][] punkte = new double[numPoints_theta][numPoints_phi][3];

        for (int iTheta = 0; iTheta < numPoints_theta; iTheta++) {
            
            x = Math.cos(iTheta * detlaTheta);
            temp = Math.sin(iTheta * detlaTheta);
            
            x *= scaleX; x += transX;
            
            for (int iPhi = 0; iPhi < numPoints_phi; iPhi++) {
                y = temp * Math.cos(iPhi * deltaPhi);
                z = temp * Math.sin(iPhi * deltaPhi);
                
                y *= scaleYZ;
                z *= scaleYZ;
                
                double[] stress = new double[]{x,y,z};
                double[] strain = new double[]{0.0,0.0,0.0};
                
                for (int ii = 0; ii < 3; ii++){
                    for (int jj = 0; jj < 3; jj++){
                        strain[ii] += Slok[ii][jj] * stress[jj];
                    }
                }
                
                ReserveFactor rf = getReserveFactor(material, null, new StressStrainState(stress, strain));
                double dRF = rf.getMinimalReserveFactor();
                
                punkte[iTheta][iPhi][0] = x*dRF;
                punkte[iTheta][iPhi][1] = y*dRF;
                punkte[iTheta][iPhi][2] = z*dRF;
            }
        }

        Vector3[] vertices = new Vector3[numPoints_phi * numPoints_theta * 4];
        Vector3[] normals = new Vector3[numPoints_phi * numPoints_theta * 4];
        
        double[] p1,p2,p3,p4;
        Vector3 normal;

        for (int i = 0 ; i < numPoints_theta-1; i++){
            for (int j = 0 ; j < numPoints_phi-1; j++){
                // 1. Punkt des Quads
                p1 = punkte[i][j];
                p2 = punkte[i][j+1];
                p3 = punkte[i+1][j+1];
                p4 = punkte[i+1][j];

                vertices[4*(j+i*numPoints_phi)+3] = new Vector3((float)(p1[0]),(float)(p1[1]),(float)(p1[2]));
                normal = getNormal(p1,p2,p4);
                normal.normalizeLocal();
                normals[4*(j+i*numPoints_phi)+3] = normal;
                // 2. Punkt des Quads
                vertices[4*(j+i*numPoints_phi)+2] = new Vector3((float)(p2[0]),(float)(p2[1]),(float)(p2[2]));
                normal = getNormal(p2,p3,p1);
                normal.normalizeLocal();
                normals[4*(j+i*numPoints_phi)+2] = normal;
                // 3. Punkt des Quads
                vertices[4*(j+i*numPoints_phi)+1] = new Vector3((float)(p3[0]),(float)(p3[1]),(float)(p3[2]));
                normal = getNormal(p3,p4,p2);
                normal.normalizeLocal();
                normals[4*(j+i*numPoints_phi)+1] = normal;
                // 4. Punkt des Quads
                vertices[4*(j+i*numPoints_phi)+0] = new Vector3((float)(p4[0]),(float)(p4[1]),(float)(p4[2]));
                normal = getNormal(p4,p1,p3);
                normal.normalizeLocal();
                normals[4*(j+i*numPoints_phi)+0] = normal;
            }
        }

        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setNormalBuffer(BufferUtils.createFloatBuffer(normals));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();

        return mesh;
    }

    private Vector3 getNormal(double[] p1, double[] p2, double[] p3){
        double[] xvec = new double[3];
        double[] yvec = new double[3];
        double[] zvec = new double[3];

        for (int i = 0; i < xvec.length; i++) xvec[i] = p2[i]-p1[i];
        for (int i = 0; i < yvec.length; i++) yvec[i] = p3[i]-p1[i];

        zvec[0] = xvec[1]*yvec[2] - xvec[2]*yvec[1];
        zvec[1] = xvec[2]*yvec[0] - xvec[0]*yvec[2];
        zvec[2] = xvec[0]*yvec[1] - xvec[1]*yvec[0];

        return new Vector3((float)(-zvec[0]),(float)(-zvec[1]),(float)(-zvec[2]));
    }
}
