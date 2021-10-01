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
package de.elamx.laminate.addFailureCriteria;

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
 * Diese Klasse ist die Implementierung des Sun-Kriteriums.
 *
 * @author Iwan Kappes
 */
public class Sun extends Criterion {

    public static Sun getDefault(FileObject obj) {
        Sun sn = new Sun(obj);

        return sn;
    }

    public Sun(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer layer, StressStrainState sss) {

        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();

        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }

        double RF_F = 0;                 // reserve factor 1st failure criteria
        double RF_M = 0;                 // reserve factor 2nd failure criteria
        double R_pz = material.getRParTen();
        double R_pd = material.getRParCom();
        double R_sz = material.getRNorTen();
        double R_sd = material.getRNorCom();
        double R_sp = material.getRShear();
        
        // Wenn die Schicht eingebettet ist, sind die Schubfestigkeit und die Zugfestigkeit quer zur Faserrichtung um den Faktor 1.5 erhöht.
        if (layer != null && layer.isEmbedded()) {
            R_sp = 1.5  * R_sp;
            R_sz = 1.5 * R_sz;
        }

        // Fibre Failure Test
        if (stresses[0] > 0.0) {

            RF_F = R_pz / stresses[0];

            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FibreFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        } else {

            RF_F = Math.sqrt(R_pd / stresses[0] * R_pd / stresses[0]);

            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FibreFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }

        // Matrix Failure Test
        if (stresses[1] > 0.0) {

            RF_M = Math.sqrt(1 / ((stresses[1] / R_sz) * (stresses[1] / R_sz) + (stresses[2] / R_sp) * (stresses[2] / R_sp)));

            if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M);
                rf.setFailureName("MatrixFailureTension");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        } else {
            RF_M = Math.sqrt(1 / ((stresses[1] / R_sd) * (stresses[1] / R_sd) + (stresses[2] / R_sp) * (stresses[2] / R_sp)));

            if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M);
                rf.setFailureName("MatrixFailureCompression");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        }

        rf.setFailureName(NbBundle.getMessage(Sun.class, "Sun." + rf.getFailureName()));

        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {

        /* Allgemeine Bemerkungen:
         * Es wird von einer Kugel mit dem Radius eins ausgegangen.
         * Der Radius entspricht dem Spannungsvektor zur Berechnung
         * des Versagenskörpers. Daraus wird der entsprechende Reserve-
         * faktor bestimmt, der mit der Berechnungsspannung die Ober-
         * fläche des Versagenskörpers bildet. 
         * Die Kugelkoordinaten sind wie folgt definiert:
         * X = cos(theta)   ;   Y = sin(theta)*sin(phi)   ;   Z = sin(theta)*cos(phi)
         */
        int Points_x = (int) (quality * 20);                             // Abtastpunkte entlang der X-Achse
        int Points_phi = (int) (quality * 14);                           // Abtastpunkte in Phi Richtung (vielfaches von zwei)
        int X_NORMALE;
        int a;
        int b;
        int c;
        int d;

        double R_pz = material.getRParTen();
        double R_pd = material.getRParCom();
        double R_sz = material.getRNorTen();
        double R_sd = material.getRNorCom();
        double R_sp = material.getRShear();
        double X_GRENZE;

        double DELTA_X = (R_pz + R_pd) / (Points_x - 1);         // Abtastschrittweite entlang der X-Achse
        double DELTA_PHI = Math.PI / (Points_phi-1);             // Abtastschrittweite in Phi

        double[] yCoord1 = new double[Points_phi];
        double[] zCoord1 = new double[Points_phi];

        double[] yNorCoord1 = new double[Points_phi];
        double[] zNorCoord1 = new double[Points_phi];

        // BEGIN DER BESTIMMUNG DES REFERENZQUERSCHNITTES
        // Berechnung der Halbellipse im positiven Y Bereich
        for (int iii = 0; iii < Points_phi; iii++) {
            yCoord1[iii] = R_sz * Math.cos(-Math.PI / 2 + iii * DELTA_PHI);
            zCoord1[iii] = R_sp * Math.sin(-Math.PI / 2 + iii * DELTA_PHI);

            yNorCoord1[iii] = 2 * R_sz * Math.cos(-Math.PI / 2 + iii * DELTA_PHI) / R_sz / R_sz;
            zNorCoord1[iii] = 2 * R_sp * Math.sin(-Math.PI / 2 + iii * DELTA_PHI) / R_sp / R_sp;
        }

        double[] yCoord2 = new double[Points_phi];
        double[] zCoord2 = new double[Points_phi];

        double[] yNorCoord2 = new double[Points_phi];
        double[] zNorCoord2 = new double[Points_phi];

        // Berechnung der Halbellipse im negativen Y Bereich
        for (int iii = 0; iii < Points_phi; iii++) {
            yCoord2[iii] = R_sd * Math.cos(Math.PI / 2 + iii * DELTA_PHI);
            zCoord2[iii] = R_sp * Math.sin(Math.PI / 2 + iii * DELTA_PHI);

            yNorCoord2[iii] = 2 * R_sd * Math.cos(Math.PI / 2 + iii * DELTA_PHI) / R_sd / R_sd;
            zNorCoord2[iii] = 2 * R_sp * Math.sin(Math.PI / 2 + iii * DELTA_PHI) / R_sp / R_sp;
        }

        // Number Points Referenzquerschnitt
        int numPoints = (2*(Points_phi-1)*(Points_x-1) + (Points_phi-1) * 4)*4;

        // ENDE DER BESTIMMUNG DES REFERENZQUERSCHNITTES
        Vector3[] vertices = new Vector3[numPoints];
        Vector3[] normals  = new Vector3[numPoints];
        Vector3 normal;
        
        int index = 0;

        // BEGIN STIRNFLÄCHENDARSTELLUNG
        for (int iii = 0; iii < 2; iii++) {

            if (iii == 0) {
                X_GRENZE = R_pz;
                X_NORMALE = 1;
                a = 0;
                b = 1;
                c = 2;
                d = 3;
            } else {
                X_GRENZE = -R_pd;
                X_NORMALE = -1;
                a = 3;
                b = 2;
                c = 1;
                d = 0;
            }

            // Berechnung der Halbellipse im positiven Y Bereich
            for (int jjj = 0; jjj < zCoord1.length-1; jjj++) {

                // 1. Punkt des Quads
                vertices[index+a] = new Vector3((float) (X_GRENZE), (float) (0), (float) (0));
                normal = new Vector3((float) (X_NORMALE), 0f, 0f);
                normal.normalizeLocal();
                normals[index+a] = normal;

                // 2. Punkt des Quads
                vertices[index+b] = new Vector3((float) (X_GRENZE), (float) (yCoord1[jjj]), (float) (zCoord1[jjj]));
                normal = new Vector3((float) (X_NORMALE), 0f, 0f);
                normal.normalizeLocal();
                normals[index+b] = normal;

                // 3. Punkt des Quads
                vertices[index+c] = new Vector3((float) (X_GRENZE), (float) (yCoord1[jjj + 1]), (float) (zCoord1[jjj + 1]));
                normal = new Vector3((float) (X_NORMALE), 0f, 0f);
                normal.normalizeLocal();
                normals[index+c] = normal;

                // 4. Punkt des Quads
                vertices[index+d] = new Vector3((float) (X_GRENZE), (float) (0), (float) (0));
                normal = new Vector3((float) (X_NORMALE), 0f, 0f);
                normal.normalizeLocal();
                normals[index+d] = normal;
                index+=4;
            }

            // Berechnung der Halbellipse im negativen Y Bereich
            for (int jjj = 0; jjj < zCoord2.length-1; jjj++) {

                // 1. Punkt des Quads
                vertices[index+a] = new Vector3((float) (X_GRENZE), (float) (0), (float) (0));
                normal = new Vector3((float) (X_NORMALE), 0f, 0f);
                normal.normalizeLocal();
                normals[index+a] = normal;

                // 2. Punkt des Quads
                vertices[index+b] = new Vector3((float) (X_GRENZE), (float) (yCoord2[jjj]), (float) (zCoord2[jjj]));
                normal = new Vector3((float) (X_NORMALE), 0f, 0f);
                normal.normalizeLocal();
                normals[index+b] = normal;

                // 3. Punkt des Quads
                vertices[index+c] = new Vector3((float) (X_GRENZE), (float) (yCoord2[jjj + 1]), (float) (zCoord2[jjj + 1]));
                normal = new Vector3((float) (X_NORMALE), 0f, 0f);
                normal.normalizeLocal();
                normals[index+c] = normal;

                // 4. Punkt des Quads
                vertices[index+d] = new Vector3((float) (X_GRENZE), (float) (0), (float) (0));
                normal = new Vector3((float) (X_NORMALE), 0f, 0f);
                normal.normalizeLocal();
                normals[index+d] = normal;
                index+=4;
            }
        }
        // ENDE STIRNFLÄCHENDARSTELLUNG

        // BEGIN MANTELDARSTELLUNG
        // Berechnung der Halbellipse im positiven Y Bereich
        for (int iii = 0; iii < (Points_x - 1); iii++) {
            for (int jjj = 0; jjj < (zCoord1.length-1); jjj++) {
                // 4. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + iii * DELTA_X), (float) (yCoord1[jjj + 1]), (float) (zCoord1[jjj + 1]));
                normal = new Vector3(0f, (float) (yNorCoord1[jjj + 1]), (float) (zNorCoord1[jjj + 1]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;

                // 3. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (iii + 1) * DELTA_X), (float) (yCoord1[jjj + 1]), (float) (zCoord1[jjj + 1]));
                normal = new Vector3(0f, (float) (yNorCoord1[jjj + 1]), (float) (zNorCoord1[jjj + 1]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;
                
                // 2. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (iii + 1) * DELTA_X), (float) (yCoord1[jjj]), (float) (zCoord1[jjj]));
                normal = new Vector3(0f, (float) (yNorCoord1[jjj]), (float) (zNorCoord1[jjj]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;
                
                // 1. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + iii * DELTA_X), (float) (yCoord1[jjj]), (float) (zCoord1[jjj]));
                normal = new Vector3(0f, (float) (yNorCoord1[jjj]), (float) (zNorCoord1[jjj]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;
            }
        }
        // ENDE MANTELDARSTELLUNG

        // BEGIN MANTELDARSTELLUNG
        // Berechnung der Halbellipse im negativen Y Bereich
        for (int iii = 0; iii < (Points_x - 1); iii++) {
            for (int jjj = 0; jjj < (zCoord1.length-1); jjj++) {

                // 4. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + iii * DELTA_X), (float) (yCoord2[jjj + 1]), (float) (zCoord2[jjj + 1]));
                normal = new Vector3(0f, (float) (yNorCoord2[jjj + 1]), (float) (zNorCoord2[jjj + 1]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;

                // 3. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (iii + 1) * DELTA_X), (float) (yCoord2[jjj + 1]), (float) (zCoord2[jjj + 1]));
                normal = new Vector3(0f, (float) (yNorCoord2[jjj + 1]), (float) (zNorCoord2[jjj + 1]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;

                // 2. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (iii + 1) * DELTA_X), (float) (yCoord2[jjj]), (float) (zCoord2[jjj]));
                normal = new Vector3(0f, (float) (yNorCoord2[jjj]), (float) (zNorCoord2[jjj]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;

                // 1. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + iii * DELTA_X), (float) (yCoord2[jjj]), (float) (zCoord2[jjj]));
                normal = new Vector3(0f, (float) (yNorCoord2[jjj]), (float) (zNorCoord2[jjj]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;
            }
        }
        // ENDE MANTELDARSTELLUNG
     
        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setNormalBuffer(BufferUtils.createFloatBuffer(normals));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();

        return mesh;
    }

}
