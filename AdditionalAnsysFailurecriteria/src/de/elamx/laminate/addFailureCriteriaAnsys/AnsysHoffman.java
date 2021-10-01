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
package de.elamx.laminate.addFailureCriteriaAnsys;

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
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.ReserveFactor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;


/**
 * Diese Klasse ist die Implementierung des Hoffman-Kriteriums.
 * Die Klasse ist aufgrund der ANSYS Vorzeichenregelung wahrscheinlich überflüssig.
 * 
 * @author Tim Dorau
 */
public class AnsysHoffman extends Criterion {

    public static AnsysHoffman getDefault(FileObject obj) {
        AnsysHoffman hf = new AnsysHoffman(obj);

        return hf;
    }

    public AnsysHoffman(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();
        
        /** Berechnung der Koeffizienten der quadratischen Gleichung */
        double F1 = 1 / (material.getRParTen() * material.getRParCom());
        double F2 = 1 / (material.getRNorTen() * material.getRNorCom());
        double F11 = 1 / (material.getRParTen() * material.getRParCom());
        double F22 = 1 / (material.getRNorTen() * material.getRNorCom());
        double F12 = -1/(2* material.getRParTen()*material.getRParCom());
        double F66 = 1 / (material.getRShear() * material.getRShear());
        
        // Berechnung Reservefakto aus Failure Index über quadratische Gleichung
        double Q = F11 * stresses[0] * stresses[0];
        Q += 2.0 * F12 * stresses[0] * stresses[1];
        Q += F22 * stresses[1] * stresses[1];
        Q += F66 * stresses[2] * stresses[2];

        double L = F1 * stresses[0] + F2 * stresses[1];

        double resFac = (Math.sqrt(L * L + 4.0 * Q) - L) / (2.0 * Q);

        rf.setMinimalReserveFactor(resFac);
        rf.setFailureName("Failure");
        rf.setFailureType(ReserveFactor.FIBER_FAILURE);

        if (Q == 0.0 && L == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
        } else {
            rf.setFailureName(NbBundle.getMessage(AnsysHoffman.class, "AnsysHoffman." + rf.getFailureName()));
        }

        return rf;
    }

    /* Unterschied wahrscheinlich nur auf Vorzeichendefinition der Festigkeiten in ANSYS begründet. */
    @Override
    public Mesh getAsMesh(Material material, double quality) {

        /* Allgemeine Bemerkungen:
         * Der Ellipsiod wird zunächst als Kugel aufgebaut, da sich für diese
         * einfacher ein strukturiertes Netz erzeugen läßt. Dann wird eine 
         * Transformation der Kugel zum Ellipsoiden mit den entsprechenden 
         * Halbachsen vorgenommen. Zum Schluss wird der Körper um die z-Achse
         * gedreht.
         */
        
        // Berechnen der Festigkeitsparameter des Tsai-Wu-Kriteriums aus den Festigkeiten
        double F1 = 1 / (material.getRParTen() * material.getRParCom());
        double F2 = 1 / (material.getRNorTen() * material.getRNorCom());
        double F11 = 1 / (material.getRParTen() * material.getRParCom());
        double F22 = 1 / (material.getRNorTen() * material.getRNorCom());
        double F12 = -1/(2* material.getRParTen()*material.getRParCom());
        double F66 = 1 / (material.getRShear() * material.getRShear());

        int numPoints_phi = (int) (quality * 25);                        // Anzahl der Punkte in phi-Richtung
        int numPoints_teta = 2 * numPoints_phi;           // Anzahl der Punkte in theta-Richtung

        double delta_phi = 2 * Math.PI / numPoints_phi;    // Winkelabstaende
        double delta_teta = Math.PI / numPoints_teta;

        /* Ab hier erfolgt die Transformation.
         * Diese wurde von Markus Herrich (Numerische Mathematik) im Rahmen 
         * eines Industriepraktikums im August 2008 am Lehrstuhl für 
         * Luftfahrzeugtechnik erstellt. Eine Dokumentation ist am 
         * Lehrstuhl erhältlich.
         */
        // Berechnen eines Hilfsvektors
        Vector3 m = new Vector3(F1 / 2.0, F2 / 2.0, 0);

        // Berechnen des Mittelpunktes im globalen Koordinatensystem
        double s2 = (F12 * F1 - F11 * F2) / (2.0 * F11 * F22 - 2.0 * F12 * F12);
        Vector3 s = new Vector3(-(F1 + 2.0 * F12 * s2) / (2.0 * F11), s2, 0);

        double mts = m.dot(s);

        double temp1 = (F11 + F22) / 2.0;
        double temp2 = Math.sqrt(F12 * F12 + (F11 - F22) * (F11 - F22) / 4.0);

        // Berechnen der Eigenwerte
        double lambda1 = temp1 + temp2;
        double lambda2 = temp1 - temp2;
        double lambda3 = F66;

        // Berechnen der Halbachsen des Ellipsoiden
        double halfA = Math.sqrt((1 - mts) / lambda1);
        double halfB = Math.sqrt((1 - mts) / lambda2);
        double halfC = Math.sqrt((1 - mts) / lambda3);

        // Berechen des Drehwinkels um die z-Achse
        double alpha = Math.PI / 2.0;
        if (F12 != 0.0) {
            alpha = -Math.signum(F12) * Math.asin(1.0 / Math.sqrt(1.0 + (F11 - lambda2) * (F11 - lambda2) / F12 / F12));
        }

        Transform trans3D = new Transform();

        /* Drehen der Kugel um die x-Achse
         * Dies ist notwendig, um die Pole der Kugel x-Richtung des Systems 
         * zu bekommen, da der Ellipsoid in diese Richtung bei Faserverbunden 
         * am längsten wird.
         */
        Matrix3 rot2Mat = new Matrix3();
        rot2Mat.applyRotationX(Math.PI / 2.0);
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
        trans3D = trans3D.multiply(trans, null);
        trans3D = trans3D.multiply(rot, null);
        trans3D = trans3D.multiply(scale, null);
        trans3D = trans3D.multiply(rot2, null);

        Vector3[][] points = new Vector3[numPoints_teta + 1][numPoints_phi + 1];
        Vector3[][] vectors = new Vector3[numPoints_teta + 1][numPoints_phi + 1];
        double x, y, z, temp;
        double xnor, ynor, znor;
        for (int ii = 0; ii <= numPoints_teta; ii++) {

            z = Math.cos(delta_teta * ii);
            temp = Math.sin(delta_teta * ii);

            for (int jj = 0; jj <= numPoints_phi; jj++) {
                x = temp * Math.cos(delta_phi * jj);
                y = temp * Math.sin(delta_phi * jj);
                points[ii][jj] = new Vector3((float) x, (float) y, (float) z);
                points[ii][jj] = trans3D.applyForward(points[ii][jj]);
                xnor = 2 * F11 * points[ii][jj].getX() + 2 * F12 * points[ii][jj].getY() + F1;
                ynor = 2 * F22 * points[ii][jj].getY() + 2 * F12 * points[ii][jj].getX() + F2;
                znor = 2 * F66 * points[ii][jj].getZ();
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
