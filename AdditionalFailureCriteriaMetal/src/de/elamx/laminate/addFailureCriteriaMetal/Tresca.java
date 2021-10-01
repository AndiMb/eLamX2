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
package de.elamx.laminate.addFailureCriteriaMetal;

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.ReserveFactor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class Tresca extends IsotropCriterion {

    public static Tresca getDefault(FileObject obj) {
        Tresca vM = new Tresca(obj);
        return vM;
    }

    public Tresca(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        checkMaterial(material);
        double[] stresses = sss.getStress();

        ReserveFactor rf = new ReserveFactor();

        double s1 = (stresses[0] + stresses[1]) / 2.0 + Math.sqrt(((stresses[0] - stresses[1]) / 2.0) * ((stresses[0] - stresses[1]) / 2.0) + stresses[2] * stresses[2]);
        double s2 = (stresses[0] + stresses[1]) / 2.0 - Math.sqrt(((stresses[0] - stresses[1]) / 2.0) * ((stresses[0] - stresses[1]) / 2.0) + stresses[2] * stresses[2]);

        double R_F = material.getRParTen() / Math.max(Math.abs(s1), Math.max(Math.abs(s2), Math.abs(s1 - s2)));

        rf.setMinimalReserveFactor(R_F);
        rf.setFailureName("Failure");
        rf.setFailureType(ReserveFactor.GENERAL_MATERIAL_FAILURE);

        rf.setFailureName(NbBundle.getMessage(Tresca.class, "Tresca." + rf.getFailureName()));

        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {
        double cone_length = Math.sqrt(2.0) * material.getRParTen() / 2.0;
        double cyl_length  = Math.sqrt(2.0) * material.getRParTen();
        double radius      = Math.sqrt(2.0) * material.getRParTen() / 2.0;
        
        
        int numSegments_x = (int) (quality * 5);                                // Anzahl der Punkte in phi-Richtung
        int numSegments_teta = 4 * (int) (quality * 7);                         // Anzahl der Punkte in theta-Richtung

        double delta_x_cone = cone_length / numSegments_x;                      // Winkelabstaende
        double delta_x_cyl = cyl_length / numSegments_x;                        // Winkelabstaende
        double delta_teta = 2.0 * Math.PI / numSegments_teta;                   // Berechnen des Mittelpunktes im globalen Koordinatensystem
        
        Vector3[][] points = new Vector3[numSegments_teta + 1][3 * (numSegments_x + 1)];
        Vector3[][] vectors = new Vector3[numSegments_teta + 1][3 * (numSegments_x + 1)];
        double x, y, z, yT, zT, rT;
        double xnor, ynor, znor;

        
        for (int ii = 0; ii <= numSegments_teta; ii++) {

            yT = Math.cos(delta_teta * ii);
            zT = Math.sin(delta_teta * ii);
            rT = Math.sqrt(1.0 / (yT * yT + zT * zT * 2.0));
            yT *= rT;
            zT *= rT;

            // erster Kegel
            for (int jj = 0; jj < numSegments_x + 1; jj++) {
                x = delta_x_cone * jj - cone_length - cyl_length / 2.0;
                y = yT * (delta_x_cone * jj * radius / cone_length);
                z = zT * (delta_x_cone * jj * radius / cone_length);
                points[ii][jj] = new Vector3((float) x, (float) y, (float) z);
                xnor = - cone_length / radius * Math.sqrt(1.0 / (yT * yT  + zT * zT * 2.0));
                ynor = 2 * yT;
                znor = 4 * zT;
                vectors[ii][jj] = new Vector3((float) xnor, (float) ynor, (float) znor);
                vectors[ii][jj].normalizeLocal();
            }
            
            y = yT * radius;
            z = zT * radius;
            
            // Zylinder
            for (int jj = 0; jj < numSegments_x + 1; jj++) {
                x = delta_x_cyl * jj - cyl_length / 2.0;
                points[ii][jj+numSegments_x + 1] = new Vector3((float) x, (float) y, (float) z);
                xnor = 0.0;
                ynor = 2 * y / radius / radius;
                znor = 4 * z / radius / radius;
                vectors[ii][jj+numSegments_x + 1] = new Vector3((float) xnor, (float) ynor, (float) znor);
                vectors[ii][jj+numSegments_x + 1].normalizeLocal();
            }

            // erster Kegel
            for (int jj = 0; jj < numSegments_x + 1; jj++) {
                x = delta_x_cone * jj + cyl_length / 2.0;
                y = yT * (radius - delta_x_cone * jj * radius / cone_length);
                z = zT * (radius - delta_x_cone * jj * radius / cone_length);
                points[ii][jj+2*numSegments_x + 2] = new Vector3((float) x, (float) y, (float) z);
                xnor = cone_length / radius * Math.sqrt(1.0 / (yT * yT  + zT * zT * 2.0));
                ynor = 2 * yT;
                znor = 4 * zT;
                vectors[ii][jj+2*numSegments_x + 2] = new Vector3((float) xnor, (float) ynor, (float) znor);
                vectors[ii][jj+2*numSegments_x + 2].normalizeLocal();
            }
        }

        int numPoints = (numSegments_teta + 1) * (3 * (numSegments_x + 1)) * 4;  // Anzahl der Punkte für die Vernetzung
        Vector3[] vertices = new Vector3[numPoints];
        Vector3[] normals = new Vector3[numPoints];

        /* Aufbauen des Netzen und Setzen der Flächennormalen an jedem Punkt.
         * Die Normalen sind für die Beleuchtung wichtig. Vorder- und Rückseite
         * der Elemente wird aber über die Reihenfolge der gegebenen Punkte definiert.
         */
        for (int jj = 0; jj < 3*numSegments_x+2; jj++) {
            for (int ii = 0; ii < numSegments_teta; ii++) {

                vertices[(ii + jj * numSegments_teta) * 4] = points[ii][jj];
                normals[(ii + jj * numSegments_teta) * 4] = vectors[ii][jj];

                vertices[(ii + jj * numSegments_teta) * 4 + 1] = points[ii + 1][jj];
                normals[(ii + jj * numSegments_teta) * 4 + 1] = vectors[ii + 1][jj];

                vertices[(ii + jj * numSegments_teta) * 4 + 2] = points[ii + 1][jj + 1];
                normals[(ii + jj * numSegments_teta) * 4 + 2] = vectors[ii + 1][jj + 1];

                vertices[(ii + jj * numSegments_teta) * 4 + 3] = points[ii][jj + 1];
                normals[(ii + jj * numSegments_teta) * 4 + 3] = vectors[ii][jj + 1];
            }
        }

        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setNormalBuffer(BufferUtils.createFloatBuffer(normals));
        meshData.setIndexMode(IndexMode.Quads);
        
        Quaternion rotator = new Quaternion().applyRotationZ(MathUtils.QUARTER_PI);
        meshData.rotatePoints(rotator);
        meshData.rotateNormals(rotator);

        mesh.updateModelBound();

        return mesh;
    }

}
