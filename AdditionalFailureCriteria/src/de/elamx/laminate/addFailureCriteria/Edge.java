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
 * Diese Klasse ist die Implementierung des Edge-Kriteriums.
 *
 * @author Iwan Kappes
 */
public class Edge extends Criterion {

    public static Edge getDefault(FileObject obj) {
        Edge ed = new Edge(obj);

        return ed;
    }

    public Edge(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {

        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();

        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }

        double RF1;                 // reserve factor 1st failure criteria
        double RF2;                 // reserve factor 2nd failure criteria
        double RF3;                 // reserve factor 3dt failure criteria
        double RF4;                 // reserve factor 4th failure criteria
        double RF5;                 // reserve factor 5th failure criteria
        double RF6;                 // reserve factor 6th failure criteria
        double RF7;                 // reserve factor 7th failure criteria
        double R_pz = material.getRParTen();
        double R_pd = material.getRParCom();
        double R_sz = material.getRNorTen();
        double R_sd = material.getRNorCom();
        double R_sp = material.getRShear();

        // 1. Failure Criteria -> RF1 * sigma2 = R_sz
        if (stresses[1] > 0.0) {

            RF1 = R_sz / stresses[1];

            rf.setMinimalReserveFactor(RF1);
            rf.setFailureName("MatrixFailureTension");
            rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
        }

        // 2. Failure Criteria -> RF2^2*(sigma2/R_sz)^2 + RF2^2*(tau12/R_sp)^2 = 1
        if (stresses[1] > 0.0) {

            RF2 = Math.sqrt(1 / ((stresses[1] / R_sz) * (stresses[1] / R_sz) + (stresses[2] / R_sp) * (stresses[2] / R_sp)));

            if ((RF2 < rf.getMinimalReserveFactor()) | (rf.getMinimalReserveFactor() == 0.0)) {
                rf.setMinimalReserveFactor(RF2);
                rf.setFailureName("MatrixShearFailure");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        }

        // 3. Failure Criteria -> RF3 * sigma2 = R_sd
        if (stresses[1] < 0.0) {

            RF3 = (-1 * R_sd) / stresses[1];

            if ((RF3 < rf.getMinimalReserveFactor()) | (rf.getMinimalReserveFactor() == 0.0)) {
                rf.setMinimalReserveFactor(RF3);
                rf.setFailureName("MatrixFailureCompression");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        }

        // 4. Failure Criteria -> RF4 * sigma1 = R_pz
        if (stresses[0] > 0.0) {

            RF4 = R_pz / stresses[0];

            if ((RF4 < rf.getMinimalReserveFactor()) | (rf.getMinimalReserveFactor() == 0.0)) {
                rf.setMinimalReserveFactor(RF4);
                rf.setFailureName("FibreFailureTension");
                rf.setFailureType(ReserveFactor.FIBER_FAILURE);
            }
        }

        // 5. Failure Criteria -> RF5 * sigma1 = R_pd
        if (stresses[0] < 0.0) {

            RF5 = (-1 * R_pd) / stresses[0];

            if ((RF5 < rf.getMinimalReserveFactor()) | (rf.getMinimalReserveFactor() == 0.0)) {
                rf.setMinimalReserveFactor(RF5);
                rf.setFailureName("FibreFailureCompression");
                rf.setFailureType(ReserveFactor.FIBER_FAILURE);
            }
        }

        // 6. Failure Criteria -> RF6*|tau12| = R_sp
        RF6 = R_sp / Math.abs(stresses[2]);

        if ((RF6 < rf.getMinimalReserveFactor()) | (rf.getMinimalReserveFactor() == 0.0)) {
            rf.setMinimalReserveFactor(RF6);
            rf.setFailureName("ShearFailure");
            rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
        }

        // 7. Failure Criteria -> RF2*(sigma1/R_pd) + RF2*(tau12/R_sp) = 1
        if ((stresses[0] < 0.0) & (stresses[2] != 0.0)) {

            RF7 = 1.0 / ((-1.0 * stresses[0] / R_pd) + (Math.abs(stresses[2]) / R_sp));

            if ((RF7 < rf.getMinimalReserveFactor()) | (rf.getMinimalReserveFactor() == 0.0)) {
                rf.setMinimalReserveFactor(RF7);
                rf.setFailureName("FibreShearFailure");
                rf.setFailureType(ReserveFactor.FIBER_FAILURE);
            }
        }

        rf.setFailureName(NbBundle.getMessage(Edge.class, "Edge." + rf.getFailureName()));

        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {

        int Points_x = (int) (quality * 20);                                       // Abtastpunkte im pos. und neg. X Bereich
        int Points_y = (int) (quality * 10);                                       // Abtastpunkte im neg. Y Bereich 
        int Points_z = (int) (quality * 11);                                       // Abtastpunkte im gesamten Z Bereich (ungerade bitte)
        int Points_phi = (int) (quality * 20);                                     // Abtastpunkte in Phi Richtung
        int obergrenze = (int) Math.round(Points_z / 2.0);

        double R_pz = material.getRParTen();
        double R_pd = material.getRParCom();
        double R_sz = material.getRNorTen();
        double R_sd = material.getRNorCom();
        double R_sp = material.getRShear();

        double DELTA_X_POS = R_pz / (Points_x - 1);           // Abtastschrittweite im pos. X Bereich
        double DELTA_X_NEG = -R_pd / (Points_x - 1);          // Abtastschrittweite im neg. X Bereich
        double DELTA_Y = -R_sd / (Points_y - 1);          // Abtastschrittweite im neg. Y Bereich
        double DELTA_Z = 2 * R_sp / (Points_z - 1);         // Abtastschrittweite im Z Bereich
        double DELTA_PHI = Math.PI / (Points_phi - 1);          // Abtastschrittweite in Phi

        // Number Points Referenzquerschnitt
        int NumRefQ = Points_phi + 2 * Points_y + Points_z - 3;

        double[] yCoord = new double[NumRefQ];
        double[] zCoord = new double[NumRefQ];

        double[] yNorCoord = new double[NumRefQ];
        double[] zNorCoord = new double[NumRefQ];

        // Abschlussfläche an X Pos Grenze
        int numPoints = ((Points_y - 1) * (Points_z - 1) + (Points_phi - 1) * (obergrenze - 1)) * 4;
        // Mantelfläche
        numPoints += (2 * (Points_x - 1) * (Points_phi - 1 + 2 * (Points_y - 1) + (Points_z - 1))) * 4;

        // BEGIN der Bestimmung des Referenzquerschnittes
        // Berechnung der Halbellipse im positiven Y Bereich
        for (int iii = 0; iii < (Points_phi); iii++) {
            yCoord[iii] = R_sz * Math.cos(-Math.PI / 2 + iii * DELTA_PHI);
            zCoord[iii] = R_sp * Math.sin(-Math.PI / 2 + iii * DELTA_PHI);

            yNorCoord[iii] = 2 * R_sz * Math.cos(-Math.PI / 2 + iii * DELTA_PHI) / R_sz / R_sz;
            zNorCoord[iii] = 2 * R_sp * Math.sin(-Math.PI / 2 + iii * DELTA_PHI) / R_sp / R_sp;
        }

        // Berechnung des negativen Y Bereiches für positives konstantes Z
        for (int iii = 1; iii < (Points_y); iii++) {
            yCoord[Points_phi - 1 + iii] = iii * DELTA_Y;
            zCoord[Points_phi - 1 + iii] = R_sp;

            yNorCoord[Points_phi - 1 + iii] = 0;
            zNorCoord[Points_phi - 1 + iii] = 1 / R_sp;
        }

        // Berechnung des Z Bereiches für kostantes negatives Y
        for (int iii = 1; iii < (Points_z); iii++) {
            yCoord[Points_phi + Points_y - 2 + iii] = -R_sd;
            zCoord[Points_phi + Points_y - 2 + iii] = R_sp - iii * DELTA_Z;

            yNorCoord[Points_phi + Points_y - 2 + iii] = -1 / R_sd;
            zNorCoord[Points_phi + Points_y - 2 + iii] = 0;
        }

        // Berechnung des negativen Y Bereiches für negatives konstantes Z
        for (int iii = 1; iii < (Points_y); iii++) {
            yCoord[Points_phi + Points_y + Points_z - 3 + iii] = -R_sd - iii * DELTA_Y;
            zCoord[Points_phi + Points_y + Points_z - 3 + iii] = -R_sp;

            yNorCoord[Points_phi + Points_y + Points_z - 3 + iii] = 0;
            zNorCoord[Points_phi + Points_y + Points_z - 3 + iii] = -1 / R_sp;
        }

        // ENDE der Bestimmung des Referenzquerschnittes    
        
        
        Vector3[] vertices = new Vector3[numPoints];
        Vector3[] normals  = new Vector3[numPoints];
        Vector3 normal;

        //BEGIN Bestimmung der Quads der Abschlussfläche an X Pos Grenze
        for (int iii = 0; iii < (Points_z - 1); iii++) {
            for (int jjj = 0; jjj < (Points_y - 1); jjj++) {
                // 1. Punkt des Quads
                vertices[jjj * 4 + iii * (Points_y - 1) * 4] = new Vector3((float) R_pz, (float) (DELTA_Y * jjj), (float) (-R_sp + DELTA_Z * iii));
                normal = new Vector3(1f, 0f, 0f);
                normal.normalizeLocal();
                normals[jjj * 4 + iii * (Points_y - 1) * 4] = normal;

                // 2. Punkt des Quads
                vertices[jjj * 4 + 1 + iii * (Points_y - 1) * 4] = new Vector3((float) R_pz, (float) (DELTA_Y * (jjj)), (float) (-R_sp + DELTA_Z * (iii + 1)));
                normal = new Vector3(1f, 0f, 0f);
                normal.normalizeLocal();
                normals[jjj * 4 + 1 + iii * (Points_y - 1) * 4] = normal;

                // 3. Punkt des Quads
                vertices[jjj * 4 + 2 + iii * (Points_y - 1) * 4] = new Vector3((float) R_pz, (float) (DELTA_Y * (jjj + 1)), (float) (-R_sp + DELTA_Z * (iii + 1)));
                normal = new Vector3(1f, 0f, 0f);
                normal.normalizeLocal();
                normals[jjj * 4 + 2 + iii * (Points_y - 1) * 4] = normal;

                // 4. Punkt des Quads
                vertices[jjj * 4 + 3 + iii * (Points_y - 1) * 4] = new Vector3((float) R_pz, (float) (DELTA_Y * (jjj + 1)), (float) (-R_sp + DELTA_Z * (iii)));
                normal = new Vector3(1f, 0f, 0f);
                normal.normalizeLocal();
                normals[jjj * 4 + 3 + iii * (Points_y - 1) * 4] = normal;
            }
        }
        // ENDE Bestimmung der Quads der Abschlussfläche an X Pos Grenze

        //BEGIN Bestimmung der Halbeelipse der Abschlussfläche an X Pos Grenze
        for (int iii = 0; iii < (obergrenze - 1); iii++) {
            for (int jjj = 0; jjj < (Points_phi - 1); jjj++) {
                // 1. Punkt des Quads
                vertices[(Points_y - 1) * (Points_z - 1) * 4 + jjj * 4 + iii * (Points_phi - 1) * 4] = new Vector3((float) R_pz, (float) (R_sz * (((double) iii) / (obergrenze - 1)) * Math.cos(-Math.PI / 2 + DELTA_PHI * jjj)), (float) (R_sp * (((double) iii) / (obergrenze - 1)) * Math.sin(-Math.PI / 2 + DELTA_PHI * jjj)));
                normal = new Vector3(1f, 0f, 0f);
                normal.normalizeLocal();
                normals[(Points_y - 1) * (Points_z - 1) * 4 + jjj * 4 + iii * (Points_phi - 1) * 4] = normal;

                // 2. Punkt des Quads
                vertices[(Points_y - 1) * (Points_z - 1) * 4 + jjj * 4 + 1 + iii * (Points_phi - 1) * 4] = new Vector3((float) R_pz, (float) (R_sz * (((double) (iii + 1)) / (obergrenze - 1)) * Math.cos(-Math.PI / 2 + DELTA_PHI * jjj)), (float) (R_sp * (((double) (iii + 1)) / (obergrenze - 1)) * Math.sin(-Math.PI / 2 + DELTA_PHI * jjj)));
                normal = new Vector3(1f, 0f, 0f);
                normal.normalizeLocal();
                normals[(Points_y - 1) * (Points_z - 1) * 4 + jjj * 4 + 1 + iii * (Points_phi - 1) * 4] = normal;

                // 3. Punkt des Quads
                vertices[(Points_y - 1) * (Points_z - 1) * 4 + jjj * 4 + 2 + iii * (Points_phi - 1) * 4] = new Vector3((float) R_pz, (float) (R_sz * (((double) (iii + 1)) / (obergrenze - 1)) * Math.cos(-Math.PI / 2 + DELTA_PHI * (jjj + 1))), (float) (R_sp * (((double) (iii + 1)) / (obergrenze - 1)) * Math.sin(-Math.PI / 2 + DELTA_PHI * (jjj + 1))));
                normal = new Vector3(1f, 0f, 0f);
                normal.normalizeLocal();
                normals[(Points_y - 1) * (Points_z - 1) * 4 + jjj * 4 + 2 + iii * (Points_phi - 1) * 4] = normal;

                // 4. Punkt des Quads
                vertices[(Points_y - 1) * (Points_z - 1) * 4 + jjj * 4 + 3 + iii * (Points_phi - 1) * 4] = new Vector3((float) R_pz, (float) (R_sz * (((double) iii) / (obergrenze - 1)) * Math.cos(-Math.PI / 2 + DELTA_PHI * (jjj + 1))), (float) (R_sp * (((double) iii) / (obergrenze - 1)) * Math.sin(-Math.PI / 2 + DELTA_PHI * (jjj + 1))));
                normal = new Vector3(1f, 0f, 0f);
                normal.normalizeLocal();
                normals[(Points_y - 1) * (Points_z - 1) * 4 + jjj * 4 + 3 + iii * (Points_phi - 1) * 4] = normal;
            }
        }
        // ENDE Bestimmung der Halbeelipse der Abschlussfläche an X Pos Grenze

        int NumStirnFl = ((Points_y - 1) * (Points_z - 1) + (obergrenze - 1) * (Points_phi - 1)) * 4;

        // BEGIN Manteldarstellung im pos. X Bereich
        for (int iii = 0; iii < (Points_x - 1); iii++) {
            for (int jjj = 0; jjj < (NumRefQ - 1); jjj++) {

                // 1. Punkt des Quads
                vertices[NumStirnFl + jjj * 4 + 3 + iii * (NumRefQ - 1) * 4] = new Vector3((float) (iii * DELTA_X_POS), (float) (yCoord[jjj]), (float) (zCoord[jjj]));
                normal = new Vector3(0f, (float) (yNorCoord[jjj]), (float) (zNorCoord[jjj]));
                normal.normalizeLocal();
                normals[NumStirnFl + jjj * 4 + 3 + iii * (NumRefQ - 1) * 4] = normal;

                // 2. Punkt des Quads
                vertices[NumStirnFl + jjj * 4 + 2 + iii * (NumRefQ - 1) * 4] = new Vector3((float) ((iii + 1) * DELTA_X_POS), (float) (yCoord[jjj]), (float) (zCoord[jjj]));
                normal = new Vector3(0f, (float) (yNorCoord[jjj]), (float) (zNorCoord[jjj]));
                normal.normalizeLocal();
                normals[NumStirnFl + jjj * 4 + 2 + iii * (NumRefQ - 1) * 4] = normal;

                // 3. Punkt des Quads
                vertices[NumStirnFl + jjj * 4 + 1 + iii * (NumRefQ - 1) * 4] = new Vector3((float) ((iii + 1) * DELTA_X_POS), (float) (yCoord[jjj + 1]), (float) (zCoord[jjj + 1]));
                normal = new Vector3(0f, (float) (yNorCoord[jjj + 1]), (float) (zNorCoord[jjj + 1]));
                normal.normalizeLocal();
                normals[NumStirnFl + jjj * 4 + 1 + iii * (NumRefQ - 1) * 4] = normal;

                // 4. Punkt des Quads
                vertices[NumStirnFl + jjj * 4 + 0 + iii * (NumRefQ - 1) * 4] = new Vector3((float) (iii * DELTA_X_POS), (float) (yCoord[jjj + 1]), (float) (zCoord[jjj + 1]));
                normal = new Vector3(0f, (float) (yNorCoord[jjj + 1]), (float) (zNorCoord[jjj + 1]));
                normal.normalizeLocal();
                normals[NumStirnFl + jjj * 4 + 0 + iii * (NumRefQ - 1) * 4] = normal;

            }
        }
        // ENDE Manteldarstellung im pos. X Bereich

        // Punktmenge des Mantels des pos. X Bereiches
        int NumPosMantel = (NumRefQ - 1) * (Points_x - 1) * 4;
        double Z_gr_1;
        double Z_gr_2;

        // BEGIN Manteldarstellung im neg. X Bereich
        for (int iii = 0; iii < (Points_x - 1); iii++) {

            double[] zTempCoord_1 = new double[NumRefQ];
            double[] zTempCoord_2 = new double[NumRefQ];
            double[] xNorCoord_1 = new double[NumRefQ];
            double[] xNorCoord_2 = new double[NumRefQ];

            Z_gr_1 = (1 + iii * DELTA_X_NEG / R_pd) * R_sp;
            Z_gr_2 = (1 + (iii + 1) * DELTA_X_NEG / R_pd) * R_sp;

            for (int a = 0; a < NumRefQ; a++) {

                if (Z_gr_1 > Math.abs(zCoord[a])) {
                    zTempCoord_1[a] = zCoord[a];
                    xNorCoord_1[a] = 0.0;
                } else {
                    if (zCoord[a] / Math.abs(zCoord[a]) > 0) {
                        zTempCoord_1[a] = Z_gr_1;
                    } else {
                        zTempCoord_1[a] = -Z_gr_1;
                    }
                    xNorCoord_1[a] = -1 / R_pd;
                }
            }

            for (int a = 0; a < NumRefQ; a++) {

                if (Z_gr_2 > Math.abs(zCoord[a])) {
                    zTempCoord_2[a] = zCoord[a];
                    xNorCoord_2[a] = 0.0;
                } else {
                    if (zCoord[a] / Math.abs(zCoord[a]) > 0) {
                        zTempCoord_2[a] = Z_gr_2;
                    } else {
                        zTempCoord_2[a] = -Z_gr_2;
                    }
                    xNorCoord_2[a] = -1 / R_pd;
                }
            }

            for (int jjj = 0; jjj < (NumRefQ - 1); jjj++) {

                // 1. Punkt des Quads
                vertices[NumStirnFl + NumPosMantel + jjj * 4 + 0 + iii * (NumRefQ - 1) * 4] = new Vector3((float) (iii * DELTA_X_NEG), (float) (yCoord[jjj]), (float) (zTempCoord_1[jjj]));
                normal = new Vector3((float) (xNorCoord_1[jjj]), (float) (1 * yNorCoord[jjj]), (float) (1 * zNorCoord[jjj]));
                normal.normalizeLocal();
                normals[NumStirnFl + NumPosMantel + jjj * 4 + 0 + iii * (NumRefQ - 1) * 4] = normal;

                // 2. Punkt des Quads
                vertices[NumStirnFl + NumPosMantel + jjj * 4 + 1 + iii * (NumRefQ - 1) * 4] = new Vector3((float) ((iii + 1) * DELTA_X_NEG), (float) (yCoord[jjj]), (float) (zTempCoord_2[jjj]));
                normal = new Vector3((float) (xNorCoord_2[jjj]), (float) (1 * yNorCoord[jjj]), (float) (1 * zNorCoord[jjj]));
                normal.normalizeLocal();
                normals[NumStirnFl + NumPosMantel + jjj * 4 + 1 + iii * (NumRefQ - 1) * 4] = normal;

                // 3. Punkt des Quads
                vertices[NumStirnFl + NumPosMantel + jjj * 4 + 2 + iii * (NumRefQ - 1) * 4] = new Vector3((float) ((iii + 1) * DELTA_X_NEG), (float) (yCoord[jjj + 1]), (float) (zTempCoord_2[jjj + 1]));
                normal = new Vector3((float) (xNorCoord_2[jjj + 1]), (float) (1 * yNorCoord[jjj + 1]), (float) (1 * zNorCoord[jjj + 1]));
                normal.normalizeLocal();
                normals[NumStirnFl + NumPosMantel + jjj * 4 + 2 + iii * (NumRefQ - 1) * 4] = normal;

                // 4. Punkt des Quads
                vertices[NumStirnFl + NumPosMantel + jjj * 4 + 3 + iii * (NumRefQ - 1) * 4] = new Vector3((float) (iii * DELTA_X_NEG), (float) (yCoord[jjj + 1]), (float) (zTempCoord_1[jjj + 1]));
                normal = new Vector3((float) (xNorCoord_1[jjj + 1]), (float) (1 * yNorCoord[jjj + 1]), (float) (1 * zNorCoord[jjj + 1]));
                normal.normalizeLocal();
                normals[NumStirnFl + NumPosMantel + jjj * 4 + 3 + iii * (NumRefQ - 1) * 4] = normal;

            }
        }
        // ENDE Manteldarstellung im neg. X Bereich
        
        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setNormalBuffer(BufferUtils.createFloatBuffer(normals));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();

        return mesh;
    }
}
