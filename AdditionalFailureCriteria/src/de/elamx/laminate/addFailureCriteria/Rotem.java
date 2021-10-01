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
 * Diese Klasse ist die Implementierung des Rotem-Kriteriums.
 *
 * @author Iwan Kappes
 */
public class Rotem extends Criterion {

    public static final String EM = Rotem.class.getName() + ".em";
    public static final String RMC = Rotem.class.getName() + ".rmc";
    public static final String RMT = Rotem.class.getName() + ".rmt";

    public static Rotem getDefault(FileObject obj) {
        Rotem rm = new Rotem(obj);

        return rm;
    }

    public Rotem(FileObject obj) {
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

        double RF_F = 0;
        double RF_M = 0;

        //Faserversagen
        if (stresses[0] > 0.0) {

            RF_F = material.getRParTen() / stresses[0];

            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FibreFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);

        } else {

            RF_F = -material.getRParTen() / stresses[0];

            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FibreFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);

        }

        //Matrixversagen
        if (stresses[1] >= 0.0) {

            RF_M = material.getEnor() * material.getEnor() * stresses[0] / material.getEpar() * stresses[0] / material.getEpar() * stresses[0] / material.getEpar() * stresses[0] / material.getEpar() / material.getRNorTen() / material.getRNorTen();
            RF_M = 1 - RF_M;
            RF_M = RF_M / (stresses[1] * stresses[1] / material.getRNorTen() / material.getRNorTen() + stresses[2] * stresses[2] / material.getRShear() / material.getRShear());
            RF_M = Math.sqrt(RF_M);

            if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M);
                rf.setFailureName("MatrixFailureTension");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }

        } else {

            RF_M = material.getEnor() * material.getEnor() * stresses[0] / material.getEpar() * stresses[0] / material.getEpar() * stresses[0] / material.getEpar() * stresses[0] / material.getEpar() / material.getRNorCom() / material.getRNorCom();
            RF_M = 1 - RF_M;
            RF_M = RF_M / (stresses[1] * stresses[1] / material.getRNorCom() / material.getRNorCom() + stresses[2] * stresses[2] / material.getRShear() / material.getRShear());
            RF_M = Math.sqrt(RF_M);

            if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M);
                rf.setFailureName("MatrixFailureCompression");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }

        }

        rf.setFailureName(NbBundle.getMessage(Rotem.class, "Rotem." + rf.getFailureName()));

        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {

        /* Es müssen 4 Fälle unterschieden werden
         * 
         *                              Faserversagen
         *                             Zug      Druck
         *  Matrixversagen     Zug    1. Fall   3. Fall
         *                     Druck  2. Fall   4. Fall
         * 
         * 
         * 
         */
        int numPoints_X = (int) (quality * 10);                               // Längsschritte
        int numPoints_phi = (int) (quality * 10);                            // Umfangsschritte

        double delta_phi = Math.PI / numPoints_phi;
        double delta_X_pos = material.getRParTen() / numPoints_X;
        double delta_X_neg = material.getRParCom() / numPoints_X;

        int numPoints = (2 * numPoints_phi) * numPoints_X * 2 * 4 + (2 * numPoints_phi) * 4 * 2;

        /* Parameter für das Matrix-ZUG-Versagen bestimmen
         *   Der Versagenskörper der beschrieben werden muss, ist Zylinderähnlich 
         *   entlang der sigma_x-Achse mit einer elliptischen Grundfläche in der
         *   sigma_y-tau_xy-Ebene. Die Halbachsen des Grundquerschnittes bleiben immer gleich.
         *   Eine Verschiebung des Mittelpunktes existiert nicht.
         */
        // Matrixzug
        double matrixTen_a = 1.0 / (material.getRNorTen() * material.getRNorTen());
        double matrixTen_b = 1.0 / (material.getRShear() * material.getRShear());
        // große und kleine Halbachse der Ellipse
        double matrixTen_half_a = material.getRNorTen();
        double matrixTen_half_b = material.getRShear();


        /* Parameter für das Matrix-DRUCK-Versagen bestimmen
         *   Der Versagenskörper der beschrieben werden muss, ist Zylinderähnlich 
         *   entlang der sigma_x-Achse mit einer elliptischen Grundfläche in der
         *   sigma_y-tau_xy-Ebene. Die Halbachsen des Grundquerschnittes bleiben immer gleich.
         *   Eine Verschiebung des Mittelpunktes existiert nicht.
         */
        // Matrixdruck
        double matrixCom_a = 1.0 / (material.getRNorCom() * material.getRNorCom());
        double matrixCom_b = 1.0 / (material.getRShear() * material.getRShear());
        // große und kleine Halbachse der Ellipse
        double matrixCom_half_a = material.getRNorCom();
        double matrixCom_half_b = material.getRShear();

        /* Der Grundquerschnitt wird mittels eines Abminderungsfaktors (AF)
         * an den tatsächlichen Wert angepasst. Hierzu sind die Faktoren EPar,
         * Em und Rm notwendig.
         */
        double Em = material.getAdditionalValue(EM);
        double EPar = material.getEpar();
        double RmTen = material.getAdditionalValue(RMT);
        double RmCom = material.getAdditionalValue(RMC);

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 1  -- Faserzug- und Matrixzugversagen
        //
        Vector3[] vertices = new Vector3[numPoints];
        Vector3[] normals  = new Vector3[numPoints];

        for (int jj = 0; jj < numPoints_X; jj++) {
            for (int ii = 0; ii < numPoints_phi; ii++) {
                double x = delta_X_pos * (jj);
                double e = x / EPar;
                double AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
                double y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
                double z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 0;
                double ny = 2 * y * matrixTen_a / AF / AF;
                double nz = 2 * z * matrixTen_b / AF / AF;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_phi) * 4 + 3] = normale;

                x = delta_X_pos * (jj + 1);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
                y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
                z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixTen_a / AF / AF;
                nz = 2 * z * matrixTen_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = delta_X_pos * (jj + 1);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
                y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
                z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixTen_a / AF / AF;
                nz = 2 * z * matrixTen_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = delta_X_pos * (jj);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
                y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
                z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixTen_a / AF / AF;
                nz = 2 * z * matrixTen_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_phi) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_phi) * 4 + 0] = normale;
            }
        }
        int Points = numPoints_phi * numPoints_X * 4;

        // Deckel für Fall 1 +++++++++++++++++++++++
        for (int ii = 0; ii < numPoints_phi; ii++) {
            double x = delta_X_pos * (numPoints_X);
            double e = x / EPar;
            double AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
            double y = 0;
            double z = 0;

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            double nx = 1;
            double ny = 0;
            double nz = 0;
            Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 0] = normale;

            x = delta_X_pos * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
            y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
            z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 1] = normale;

            x = delta_X_pos * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
            y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
            z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 2] = normale;

            x = delta_X_pos * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
            y = 0;
            z = 0;

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 3] = normale;
        }

        Points += numPoints_phi * 4;

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 2  -- Faserzug- und Matrixdruckversagen
        //
        for (int jj = 0; jj < numPoints_X; jj++) {
            for (int ii = 0; ii < numPoints_phi; ii++) {
                double x = delta_X_pos * (jj);
                double e = x / EPar;
                double AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
                double y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
                double z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 0;
                double ny = 2 * y * matrixCom_a / AF / AF;
                double nz = 2 * z * matrixCom_b / AF / AF;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 0] = normale;

                x = delta_X_pos * (jj + 1);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
                y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
                z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixCom_a / AF / AF;
                nz = 2 * z * matrixCom_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = delta_X_pos * (jj + 1);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
                y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
                z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixCom_a / AF / AF;
                nz = 2 * z * matrixCom_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = delta_X_pos * (jj);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
                y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
                z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixCom_a / AF / AF;
                nz = 2 * z * matrixCom_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 3] = normale;
            }
        }
        Points += numPoints_phi * numPoints_X * 4;

        // Deckel für Fall 2 +++++++++++++++++++++++
        for (int ii = 0; ii < numPoints_phi; ii++) {
            double x = delta_X_pos * (numPoints_X);
            double e = x / EPar;
            double AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
            double y = 0;
            double z = 0;

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            double nx = 1;
            double ny = 0;
            double nz = 0;
            Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 3] = normale;

            x = delta_X_pos * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
            y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
            z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 2] = normale;

            x = delta_X_pos * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
            y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
            z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 1] = normale;

            x = delta_X_pos * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
            y = 0;
            z = 0;

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 0] = normale;
        }

        Points += numPoints_phi * 4;

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 3  -- Faserdruck- und Matrixzugversagen
        //
        for (int jj = 0; jj < numPoints_X; jj++) {
            for (int ii = 0; ii < numPoints_phi; ii++) {
                double x = -delta_X_neg * (jj);
                double e = x / EPar;
                double AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
                double y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
                double z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 0;
                double ny = 2 * y * matrixTen_a / AF / AF;
                double nz = 2 * z * matrixTen_b / AF / AF;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 0] = normale;

                x = -delta_X_neg * (jj + 1);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
                y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
                z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixTen_a / AF / AF;
                nz = 2 * z * matrixTen_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = -delta_X_neg * (jj + 1);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
                y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
                z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixTen_a / AF / AF;
                nz = 2 * z * matrixTen_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = -delta_X_neg * (jj);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
                y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
                z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixTen_a / AF / AF;
                nz = 2 * z * matrixTen_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 3] = normale;
            }
        }
        Points += numPoints_phi * numPoints_X * 4;

        // Deckel für Fall 3 +++++++++++++++++++++++
        for (int ii = 0; ii < numPoints_phi; ii++) {
            double x = -delta_X_neg * (numPoints_X);
            double e = x / EPar;
            double AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
            double y = 0;
            double z = 0;

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            double nx = 1;
            double ny = 0;
            double nz = 0;
            Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 3] = normale;

            x = -delta_X_neg * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
            y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
            z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 2] = normale;

            x = -delta_X_neg * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
            y = AF * matrixTen_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
            z = AF * matrixTen_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 1] = normale;

            x = -delta_X_neg * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmTen / RmTen);
            y = 0;
            z = 0;

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 0] = normale;
        }

        Points += numPoints_phi * 4;

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 4  -- Faserzug- und Matrixdruckversagen
        //
        for (int jj = 0; jj < numPoints_X; jj++) {
            for (int ii = 0; ii < numPoints_phi; ii++) {
                double x = -delta_X_neg * (jj);
                double e = x / EPar;
                double AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
                double y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
                double z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 0;
                double ny = 2 * y * matrixCom_a / AF / AF;
                double nz = 2 * z * matrixCom_b / AF / AF;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 3] = normale;

                x = -delta_X_neg * (jj + 1);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
                y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
                z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixCom_a / AF / AF;
                nz = 2 * z * matrixCom_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = -delta_X_neg * (jj + 1);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
                y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
                z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixCom_a / AF / AF;
                nz = 2 * z * matrixCom_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = -delta_X_neg * (jj);
                e = x / EPar;
                AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
                y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
                z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = 2 * y * matrixCom_a / AF / AF;
                nz = 2 * z * matrixCom_b / AF / AF;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[Points + (ii + jj * numPoints_phi) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[Points + (ii + jj * numPoints_phi) * 4 + 0] = normale;
            }
        }
        Points += numPoints_phi * numPoints_X * 4;

        // Deckel für Fall 4 +++++++++++++++++++++++
        for (int ii = 0; ii < numPoints_phi; ii++) {
            double x = -delta_X_neg * (numPoints_X);
            double e = x / EPar;
            double AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
            double y = 0;
            double z = 0;

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            double nx = 1;
            double ny = 0;
            double nz = 0;
            Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 0] = normale;

            x = -delta_X_neg * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
            y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii) - Math.PI / 2);
            z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii) - Math.PI / 2);

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 1] = normale;

            x = -delta_X_neg * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
            y = -AF * matrixCom_half_a * Math.cos(delta_phi * (ii + 1) - Math.PI / 2);
            z = AF * matrixCom_half_b * Math.sin(delta_phi * (ii + 1) - Math.PI / 2);

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 2] = normale;

            x = -delta_X_neg * (numPoints_X);
            e = x / EPar;
            AF = Math.sqrt(1 - Em * Em * e * e * e * e / RmCom / RmCom);
            y = 0;
            z = 0;

            //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
            nx = 1;
            ny = 0;
            nz = 0;
            normale = new Vector3((float) nx, (float) ny, (float) nz);
            normale.normalizeLocal();
            vertices[Points + ii * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
            normals[Points + ii * 4 + 3] = normale;
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
