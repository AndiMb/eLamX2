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
 *
 * @author Tim Dorau
 */
public class Christensen extends Criterion {

    public static Christensen getDefault(FileObject obj) {
        Christensen chr = new Christensen(obj);

        return chr;
    }

    public Christensen(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();

        // Das Christensen-Kriterium wird durch 2 Gleichtung, jeweils 1x Faserbuch und 1x Zwischenfaserbruch vollständig beschrieben
        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }

        double F1 = 1 / material.getRParTen() - 1 / material.getRParCom();
        double F2 = 1 / material.getRNorTen() - 1 / material.getRNorCom();
        double F11 = 1 / (material.getRParTen() * material.getRParCom());
        double F22 = 1 / (material.getRNorTen() * material.getRNorCom());
        double F66 = 1 / (material.getRShear() * material.getRShear());

        /**
         * Berechnung Faserbruch
         */
        double Q_F = F11 * stresses[0] * stresses[0];
        double L_F = F1 * stresses[0];
        double resFac_F = Double.MAX_VALUE;

        if (stresses[0] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
        } else {
            resFac_F = (Math.sqrt(L_F * L_F + 4.0 * Q_F) - L_F) / (2.0 * Q_F);
            rf.setMinimalReserveFactor(resFac_F);
            rf.setFailureName("FiberFailure");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }

        /**
         * Berechnung Zwischenfaserbruch
         */
        double Q_M = F22 * stresses[1] * stresses[1];
        Q_M += F66 * stresses[2] * stresses[2];
        double L_M = F2 * stresses[1];
        double resFac_M = (Math.sqrt(L_M * L_M + 4.0 * Q_M) - L_M) / (2.0 * Q_M);

        if (resFac_M < resFac_F) {
            rf.setMinimalReserveFactor(resFac_M);
            rf.setFailureName("MatrixFailure");
            rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
        }

        rf.setFailureName(NbBundle.getMessage(Christensen.class, "Christensen." + rf.getFailureName()));

        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {

        // Anzahl der Punkte in x- bzw. y-Richtung (entspricht sig_p und sig_s-Richtung)
        int Points_x = (int) (quality * 20);

        // Materialparameter
        double R_pz = material.getRParTen();
        double R_pd = material.getRParCom();
        double R_sz = material.getRNorTen();
        double R_sd = material.getRNorCom();
        double R_sp = material.getRShear();

        /* Der Versagenskörper besitzt eine elliptische Fläche in der tau-sig_2-Ebene mit einer Verschiebung des Ellipsenmittelpunktes 
           Die Ellipse besitzt keine Abhängigkeit von der x-Koordinate und ist somit über die gesamte Länge konstant*/
        //Ellipsengeometriedefiniton:
        // Quadrate der Halbachsen aus der Transformation des Matrixkriteriums auf die Ellipsennormalform durch quadratische Ergänzung
        double a = 0.5 * (R_sz + R_sd);
        double b2 = R_sp * R_sp * (1 + ((R_sd - R_sz) * (R_sd - R_sz)) / (4 * R_sz * R_sd));
        double b = Math.sqrt(b2);
        // Koordinaten des Ellipsenmittelpunkts
        double y_MP = -(R_sd - R_sz) / 2;

        // Schrittweiten in x-und y Richtung
        double DELTA_X = (R_pz + R_pd) / (Points_x - 1);
        // DARSTELLUNG MIT EINEM UMLAUFWINKEL THETA
        int Points_theta = (int) (quality * 28);

        // Schrittweite in rad
        double dTheta = 2.0 * Math.PI / (Points_theta - 1);

        // z-Coord1,3 für die obere Hälfte der Ellipse, z-Coord2,4 für die untere Hälfte; 3 und 4 für negative sigma_2
        double[] yCoord = new double[Points_theta];
        double[] zCoord = new double[Points_theta];
        double[] yNorCoord = new double[Points_theta];
        double[] zNorCoord = new double[Points_theta];

        for (int j = 0; j < Points_theta; j++) {
            yCoord[j] = a * Math.cos(j * dTheta) + y_MP;
            zCoord[j] = b * Math.sin(j * dTheta);
            yNorCoord[j] = Math.cos(j * dTheta) / a;
            zNorCoord[j] = Math.sin(j * dTheta) / b;
        }

        // Punktanzahl = Mantelfläche + 2 * Stirnfläche
        int numPoints = 4 * Points_theta * Points_x + Points_theta * 4 * 2;

        // Aufbau der Mantelfläche
        Vector3[] vertices = new Vector3[numPoints];
        Vector3[] normals = new Vector3[numPoints];
        Vector3 normal;

        int index = 0;

        // Mantelfläche
        for (int i = 0; i < (Points_x - 1); i++) {
            for (int j = 0; j < (Points_theta - 1); j++) {
                // 4. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord[j + 1]), (float) (zCoord[j + 1]));
                normal = new Vector3(0f, (float) (yNorCoord[j + 1]), (float) (zNorCoord[j + 1]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;
                // 3. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord[j + 1]), (float) (zCoord[j + 1]));
                normal = new Vector3(0f, (float) (yNorCoord[j + 1]), (float) (zNorCoord[j + 1]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;
                // 2. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord[j]), (float) (zCoord[j]));
                normal = new Vector3(0f, (float) (yNorCoord[j]), (float) (zNorCoord[j]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;
                // 1. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord[j]), (float) (zCoord[j]));
                normal = new Vector3(0f, (float) (yNorCoord[j]), (float) (zNorCoord[j]));
                normal.normalizeLocal();
                normals[index] = normal;
                index++;
            }
        }
        
        // Stirnfläche
        for (int j = 0; j < (Points_theta - 1); j++) {
            // 4. Punkt des Quads
            vertices[index] = new Vector3((float) (-R_pd), 0.f, 0.f);
            normal = new Vector3(-1.f, 0.f, 0.f);
            normal.normalizeLocal();
            normals[index] = normal;
            index++;
            // 3. Punkt des Quads
            vertices[index] = new Vector3((float) (-R_pd), (float) (yCoord[j + 1]), (float) (zCoord[j + 1]));
            normal = new Vector3(-1.f, 0.f, 0.f);
            normal.normalizeLocal();
            normals[index] = normal;
            index++;
            // 2. Punkt des Quads
            vertices[index] = new Vector3((float) (-R_pd), (float) (yCoord[j]), (float) (zCoord[j]));
            normal = new Vector3(-1.f, 0.f, 0.f);
            normal.normalizeLocal();
            normals[index] = normal;
            index++;
            // 1. Punkt des Quads
            vertices[index] = new Vector3((float) (-R_pd), 0.f, 0.f);
            normal = new Vector3(-1.f, 0.f, 0.f);
            normal.normalizeLocal();
            normals[index] = normal;
            index++;
        }
        
        // Stirnfläche
        for (int j = 0; j < (Points_theta - 1); j++) {
            // 4. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), 0.f, 0.f);
            normal = new Vector3(1.f, 0.f, 0.f);
            normal.normalizeLocal();
            normals[index] = normal;
            index++;
            // 3. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord[j]), (float) (zCoord[j]));
            normal = new Vector3(1.f, 0.f, 0.f);
            normal.normalizeLocal();
            normals[index] = normal;
            index++;
            // 2. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord[j + 1]), (float) (zCoord[j + 1]));
            normal = new Vector3(1.f, 0.f, 0.f);
            normal.normalizeLocal();
            normals[index] = normal;
            index++;
            // 1. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), 0.f, 0.f);
            normal = new Vector3(1.f, 0.f, 0.f);
            normal.normalizeLocal();
            normals[index] = normal;
            index++;
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
