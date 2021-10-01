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

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
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
public class vonMises extends IsotropCriterion {

    public static vonMises getDefault(FileObject obj) {
        vonMises vM = new vonMises(obj);
        return vM;
    }

    public vonMises(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        checkMaterial(material);
        double[] stresses = sss.getStress();
        
        ReserveFactor rf = new ReserveFactor();
        
        double R_F = Math.sqrt(material.getRParTen()*material.getRParTen()/(stresses[0]*stresses[0] + stresses[1]*stresses[1] - stresses[0]*stresses[1] + 3.0*stresses[2]*stresses[2]));
        
        rf.setMinimalReserveFactor(R_F);
        rf.setFailureName("Failure");
        rf.setFailureType(ReserveFactor.GENERAL_MATERIAL_FAILURE);
        
        rf.setFailureName(NbBundle.getMessage(vonMises.class, "vonMises." + rf.getFailureName()));
        
        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {
        /* Allgemeine Bemerkungen:
         * Der Ellipsiod wird zunächst als Kugel aufgebaut, da sich für diese
         * einfacher ein strukturiertes Netz erzeugen läßt. Dann wird eine 
         * Transformation der Kugel zum Ellipsoiden mit den entsprechenden 
         * Halbachsen vorgenommen. Zum Schluss wird der Körper um die z-Achse
         * gedreht.
         */

        int numPoints_phi = 4 * (int) (quality * 7);         // Anzahl der Punkte in phi-Richtung
        int numPoints_teta = 2 * numPoints_phi;           // Anzahl der Punkte in theta-Richtung

        double delta_phi = 2 * Math.PI / numPoints_phi;    // Winkelabstaende
        double delta_teta = Math.PI / numPoints_teta;        // Berechnen des Mittelpunktes im globalen Koordinatensystem
        
        Vector3 s = new Vector3(0.0, 0.0, 0.0);

        // Berechnen der Halbachsen des Ellipsoiden
        double halfA = Math.sqrt(2.0) * material.getRParTen();
        double halfB = Math.sqrt(1.0/3.0) * halfA;
        double halfC = material.getRParTen()/Math.sqrt(3.0);

        // Berechen des Drehwinkels um die z-Achse
        double alpha = -Math.PI / 4.0;
        //double alpha = 0.0;

        Transform trans3D = new Transform();

        /* Drehen der Kugel um die x-Achse
         * Dies ist notwendig, um die Pole der Kugel x-Richtung des Systems 
         * zu bekommen, da der Ellipsoid in diese Richtung bei Faserverbunden 
         * am längsten wird.
         */
        Matrix3 rot2Mat = new Matrix3();
        rot2Mat.applyRotationY(Math.PI / 2.0);
        Transform rot2 = new Transform();
        rot2.setRotation(rot2Mat);

        // Erzeugen der Tranformationsmatrix zum Skalieren der Kugel zum Ellipsoiden
        
        Transform scale = new Transform();
        scale.setScale(new Vector3(halfA, halfB, halfC));

        // Erzeugen der Tranformationsmatrix zum Rotieren des Ellipsoiden um die z-Achse
        Matrix3 rotMat = new Matrix3();
        rotMat.applyRotationZ(-alpha);
        Transform rot = new Transform();
        rot.setRotation(rotMat);

        // Erzeugen der Tranformationsmatrix zum Verschieben der gedrehten Ellispoiden
        Transform trans = new Transform();
        trans.setTranslation(s);

        /* Aufbauen der Gesamtrotationsmatrix. Die Reihenfolge der Ausführung 
         * ist an dieser Stelle wichtig.
         */
        //trans3D = trans3D.multiply(trans, null);
        trans3D = trans3D.multiply(rot, null);
        trans3D = trans3D.multiply(scale, null);
        trans3D = trans3D.multiply(rot2, null);

        Vector3[][] points = new Vector3[numPoints_teta + 1][numPoints_phi + 1];
        Vector3[][] vectors = new Vector3[numPoints_teta + 1][numPoints_phi + 1];
        double x, y, z, temp;
        double xnor, ynor, znor;
        double t = 1.0 / Math.sqrt(2.0);
        for (int ii = 0; ii <= numPoints_teta; ii++) {

            z = Math.cos(delta_teta * ii);
            temp = Math.sin(delta_teta * ii);

            for (int jj = 0; jj <= numPoints_phi; jj++) {
                x = temp * Math.cos(delta_phi * jj);
                y = temp * Math.sin(delta_phi * jj);
                points[ii][jj] = new Vector3((float) x, (float) y, (float) z);
                points[ii][jj] = trans3D.applyForward(points[ii][jj]);
                xnor = (1.0 / halfA / halfA + 1.0 / halfB / halfB ) * points[ii][jj].getX() + ( -1.0 / halfB / halfB + 1.0 / halfA / halfA ) * points[ii][jj].getY();
                ynor = (1.0 / halfA / halfA + 1.0 / halfB / halfB ) * points[ii][jj].getY() + ( -1.0 / halfB / halfB + 1.0 / halfA / halfA ) * points[ii][jj].getX();
                znor = 2.0 / halfC / halfC * points[ii][jj].getZ();
                vectors[ii][jj] = new Vector3((float) xnor, (float) ynor, (float) znor);
                vectors[ii][jj].normalizeLocal();
            }
        }

        int numPoints = (numPoints_phi + 1) * (numPoints_teta + 1) * 4;  // Anzahl der Punkte für die Vernetzung
        Vector3[] vertices = new Vector3[numPoints];
        Vector3[] normals  = new Vector3[numPoints];
        
        

        /* Aufbauen des Netzen und Setzen der Flächennormalen an jedem Punkt.
         * Die Normalen sind für die Beleuchtung wichtig. Vorder- und Rückseite
         * der Elemente wird aber über die Reihenfolge der gegebenen Punkte definiert.
         */
        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {

                vertices[(ii + jj * numPoints_teta) * 4] = points[ii][jj];
                normals[(ii + jj * numPoints_teta) * 4] = vectors[ii][jj];

                vertices[(ii + jj * numPoints_teta) * 4 + 1] = points[ii + 1][jj];
                normals[(ii + jj * numPoints_teta) * 4 + 1] = vectors[ii + 1][jj];

                vertices[(ii + jj * numPoints_teta) * 4 + 2] = points[ii + 1][jj + 1];
                normals[(ii + jj * numPoints_teta) * 4 + 2] = vectors[ii + 1][jj + 1];

                vertices[(ii + jj * numPoints_teta) * 4 + 3] = points[ii][jj + 1];
                normals[(ii + jj * numPoints_teta) * 4 + 3] = vectors[ii][jj + 1];
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
    
}
