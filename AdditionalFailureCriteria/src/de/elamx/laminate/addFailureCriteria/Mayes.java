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
 * Diese Klasse ist die Implementierung des Mayes-Kriteriums.
 *
 * @author Iwan Kappes
 */
public class Mayes extends Criterion {

    public static Mayes getDefault(FileObject obj) {
        Mayes ms = new Mayes(obj);

        return ms;
    }

    public Mayes(FileObject obj) {
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
        double RF_F;
        double RF_M;

        //Faserversagen
        if (stresses[0] > 0.0) {

            RF_F = Math.sqrt(1 / (stresses[0] * stresses[0] / material.getRParTen() / material.getRParTen() + stresses[2] * stresses[2] / material.getRShear() / material.getRShear()));

            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FibreFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);

        } else {

            RF_F = Math.sqrt(1 / (stresses[0] * stresses[0] / material.getRParCom() / material.getRParCom() + stresses[2] * stresses[2] / material.getRShear() / material.getRShear()));

            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FibreFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);

        }

        //Matrixversagen
        if (stresses[1] >= 0.0) {

            RF_M = Math.sqrt(1 / (stresses[1] * stresses[1] / material.getRNorTen() / material.getRNorTen() + stresses[2] * stresses[2] / material.getRShear() / material.getRShear()));

            if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M);
                rf.setFailureName("MatrixFailureTension");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }

        } else {

            RF_M = Math.sqrt(1 / (stresses[1] * stresses[1] / material.getRNorCom() / material.getRNorCom() + stresses[2] * stresses[2] / material.getRShear() / material.getRShear()));

            if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M);
                rf.setFailureName("MatrixFailureCompression");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }

        }

        rf.setFailureName(NbBundle.getMessage(Mayes.class, "Mayes." + rf.getFailureName()));

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
        int numPoints_phi = (int) (quality * 10);
        int numPoints_teta = 2 * numPoints_phi;

        double delta_phi = Math.PI / numPoints_phi;
        double delta_teta = 2 * Math.PI / numPoints_teta;

        int numPoints = (numPoints_phi + 1) * (numPoints_teta + 1) * 4;
        int totalNumPoints = 8 * numPoints;// + (numPoints_teta+1)*4;

        /* Parameter für das FASER-ZUG-Versagen bestimmen
         *   Der Versagenskörper der beschrieben werden muss, ist ein Zylinder 
         *   entlang der sigma_y-Achse mit einer elliptischen Grundfläche in der
         *   sigma_x-tau_xy-Ebene. Prinzipiell sind die Halbachsen immer gleich.
         *   Eine Verschiebung des Mittelpunktes existiert nicht. Die Werte sind
         *   konstant. Also kann man die hier einmal bestimmen und dann fertsch.
         *   Von der Ellipse wird nur der Teil benötigt, der im Faserzugbereich,
         *   also sigma_x > 0  liegt. Es muss also der Gültigkeitsbereich des Winkels
         *   eingeschränkt werden.
         */
        // Faserzug
        double fibreTen_a = 1.0 / (material.getRParTen() * material.getRParTen());
        double fibreTen_b = 1.0 / (material.getRShear() * material.getRShear());
        // große und kleine Halbachse der Ellipse
        double fibreTen_half_a = material.getRParTen();
        double fibreTen_half_b = material.getRShear();
        // Gültigkeitsbereich für den Winkel festlegen. (Ist immer -90° bis + 90° bzw. -PI/2 bis + PI/2)
        double fibreTen_phi = Math.PI / 2.0;
        double fibreTen_delta_phi = 2.0 * fibreTen_phi / (numPoints_phi);

        // Faserdruck
        double fibreCom_a = 1.0 / (material.getRParCom() * material.getRParCom());
        double fibreCom_b = 1.0 / (material.getRShear() * material.getRShear());
        // große und kleine Halbachse der Ellipse
        double fibreCom_half_a = material.getRParCom();
        double fibreCom_half_b = material.getRShear();
        // Gültigkeitsbereich für den Winkel festlegen. (Ist immer -90° bis + 90° bzw. -PI/2 bis + PI/2)
        double fibreCom_phi = Math.PI / 2.0;
        double fibreCom_delta_phi = 2.0 * fibreCom_phi / (numPoints_phi);

        /* Parameter für das Matrix-ZUG-Versagen bestimmen
         *   Der Versagenskörper der beschrieben werden muss, ist ein Zylinder 
         *   entlang der sigma_x-Achse mit einer elliptischen Grundfläche in der
         *   sigma_y-tau_xy-Ebene. Prinzipiell sind die Halbachsen immer gleich.
         *   Eine Verschiebung des Mittelpunktes existiert nicht. Die Werte sind
         *   konstant. Also kann man die hier einmal bestimmen und dann fertsch.
         *   Von der Ellipse wird nur der Teil benötigt, der im Matrixzugbereich,
         *   also sigma_y > 0  liegt. Es muss also der Gültigkeitsbereich des Winkels
         *   eingeschränkt werden.
         */
        // Matrixzug
        double matrixTen_a = 1.0 / (material.getRNorTen() * material.getRNorTen());
        double matrixTen_b = 1.0 / (material.getRShear() * material.getRShear());
        // große und kleine Halbachse der Ellipse
        double matrixTen_half_a = material.getRNorTen();
        double matrixTen_half_b = material.getRShear();
        // Gültigkeitsbereich für den Winkel festlegen. (Ist immer -90° bis + 90° bzw. -PI/2 bis + PI/2)
        double matrixTen_phi = Math.PI / 2.0;
        double matrixTen_delta_phi = 2.0 * matrixTen_phi / (numPoints_phi);

        /* Parameter für das Matrix-ZUG-Versagen bestimmen
         *   Der Versagenskörper der beschrieben werden muss, ist ein Zylinder 
         *   entlang der sigma_x-Achse mit einer elliptischen Grundfläche in der
         *   sigma_y-tau_xy-Ebene. Prinzipiell sind die Halbachsen immer gleich.
         *   Eine Verschiebung des Mittelpunktes existiert nicht. Die Werte sind
         *   konstant. Also kann man die hier einmal bestimmen und dann fertsch.
         *   Von der Ellipse wird nur der Teil benötigt, der im Matrixzugbereich,
         *   also sigma_y < 0  liegt. Es muss also der Gültigkeitsbereich des Winkels
         *   eingeschränkt werden.
         */
        // Matrixdruck
        double matrixCom_a = 1.0 / (material.getRNorCom() * material.getRNorCom());
        double matrixCom_b = 1.0 / (material.getRShear() * material.getRShear());
        // große und kleine Halbachse der Ellipse
        double matrixCom_half_a = material.getRNorCom();
        double matrixCom_half_b = material.getRShear();
        // Gültigkeitsbereich für den Winkel festlegen.
        double matrixCom_phi = Math.PI / 2.0;
        double matrixCom_delta_phi = 2.0 * matrixCom_phi / (numPoints_phi);

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 1  -- Faserzug- und Matrixzugversagen
        //
        //  -- Faserzugversagen
        Vector3[] vertices = new Vector3[totalNumPoints];
        Vector3[] normals  = new Vector3[totalNumPoints];

        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {
                double x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                double y = (jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixTen_a) * x;
                double z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 2 * x * fibreTen_a;
                double ny = 0;
                double nz = 2 * z * fibreTen_b;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_teta) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_teta) * 4] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                y = (jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixTen_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreTen_a;
                ny = 0;
                nz = 2 * z * fibreTen_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                y = (jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixTen_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreTen_a;
                ny = 0;
                nz = 2 * z * fibreTen_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                y = (jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixTen_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreTen_a;
                ny = 0;
                nz = 2 * z * fibreTen_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_teta) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_teta) * 4 + 3] = normale;
            }
        }

        // Fall 1  -- Faserzug- und Matrixzugversagen
        //
        //   --   Matrixzugversagen
        int sInd = numPoints;

        for (int jj = 0; jj < numPoints_teta; jj++) {
            for (int ii = 0; ii < numPoints_phi; ii++) {

                double x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * ii / 2);
                double y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (jj) / 2.0 - matrixTen_phi);
                double neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                double z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * ii / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixTen_a) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixTen_half_b * Math.sin(matrixTen_delta_phi * (jj) / 2.0 - matrixTen_phi);
                } else {
                    y = Math.sqrt(fibreTen_a / matrixTen_a * x * x);
                }
                double nx = 0;
                double ny = 2 * matrixTen_a * y;
                double nz = 2 * matrixTen_b * z;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2);
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (jj) / 2.0 - matrixTen_phi);
                neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixTen_a) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixTen_half_b * Math.sin(matrixTen_delta_phi * (jj) / 2.0 - matrixTen_phi);
                } else {
                    y = Math.sqrt(fibreTen_a / matrixTen_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixTen_a * y;
                nz = 2 * matrixTen_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2);
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (jj + 1) / 2.0 - matrixTen_phi);
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixTen_a) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixTen_half_b * Math.sin(matrixTen_delta_phi * (jj + 1) / 2.0 - matrixTen_phi);
                } else {
                    y = Math.sqrt(fibreTen_a / matrixTen_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixTen_a * y;
                nz = 2 * matrixTen_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii) / 2);
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (jj + 1) / 2.0 - matrixTen_phi);
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixTen_a) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixTen_half_b * Math.sin(matrixTen_delta_phi * (jj + 1) / 2.0 - matrixTen_phi);
                } else {
                    y = Math.sqrt(fibreTen_a / matrixTen_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixTen_a * y;
                nz = 2 * matrixTen_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 3] = normale;

            }
        }

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 2  -- Faserzug- und Matrixdruckversagen
        //
        //  -- Faserzugversagen
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {

                double x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                double y = -(jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixCom_a) * x;
                double z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 2 * x * fibreTen_a;
                double ny = 0;
                double nz = 2 * z * fibreTen_b;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                y = -(jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixCom_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreTen_a;
                ny = 0;
                nz = 2 * z * fibreTen_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                y = -(jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixCom_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreTen_a;
                ny = 0;
                nz = 2 * z * fibreTen_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                y = -(jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixCom_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                // normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreTen_a;
                ny = 0;
                nz = 2 * z * fibreTen_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 3] = normale;
            }
        }

        //  -- Matrixdruckversagen  
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_teta; jj++) {
            for (int ii = 0; ii < numPoints_phi; ii++) {

                double x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * ii / 2);
                double y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                double neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                double z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * ii / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                } else {
                    y = -Math.sqrt(fibreTen_a / matrixCom_a * x * x);
                }
                double nx = 0;
                double ny = 2 * matrixCom_a * y;
                double nz = 2 * matrixCom_b * z;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 3] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                } else {
                    y = -Math.sqrt(fibreTen_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixCom_a * y;
                nz = 2 * matrixCom_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                } else {
                    y = -Math.sqrt(fibreTen_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixCom_a * y;
                nz = 2 * matrixCom_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                } else {
                    y = -Math.sqrt(fibreTen_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixCom_a * y;
                nz = 2 * matrixCom_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 0] = normale;
            }
        }

        sInd += numPoints;

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 3  -- Faserzug- und Matrixzugversagen
        //
        //  -- Faserdruckversagen
        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {
                double x = -fibreCom_half_a * Math.cos(fibreCom_delta_phi * (ii) / 2.0 - fibreCom_phi);
                double y = -(jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreCom_a / matrixTen_a) * x;
                double z = fibreCom_half_b * Math.sin(fibreCom_delta_phi * (ii) / 2.0 - fibreCom_phi);
                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 2 * x * fibreCom_a;
                double ny = 0;
                double nz = 2 * z * fibreCom_b;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 3] = normale;

                x = -fibreCom_half_a * Math.cos(fibreCom_delta_phi * (ii) / 2.0 - fibreCom_phi);
                y = -(jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreCom_a / matrixTen_a) * x;
                z = fibreCom_half_b * Math.sin(fibreCom_delta_phi * (ii) / 2.0 - fibreCom_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreCom_a;
                ny = 0;
                nz = 2 * z * fibreCom_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = -fibreCom_half_a * Math.cos(fibreCom_delta_phi * (ii + 1) / 2.0 - fibreCom_phi);
                y = -(jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreCom_a / matrixTen_a) * x;
                z = fibreCom_half_b * Math.sin(fibreCom_delta_phi * (ii + 1) / 2.0 - fibreCom_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreCom_a;
                ny = 0;
                nz = 2 * z * fibreCom_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = -fibreCom_half_a * Math.cos(fibreCom_delta_phi * (ii + 1) / 2.0 - fibreCom_phi);
                y = -(jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreCom_a / matrixTen_a) * x;
                z = fibreCom_half_b * Math.sin(fibreCom_delta_phi * (ii + 1) / 2.0 - fibreCom_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreCom_a;
                ny = 0;
                nz = 2 * z * fibreCom_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 0] = normale;
            }
        }

        // Fall 3  -- Faserdruck- und Matrixzugversagen
        //
        //   --   Matrixzugversagen
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_teta; jj++) {
            for (int ii = 0; ii < numPoints_phi; ii++) {

                double x = -fibreCom_half_a * Math.sin(fibreCom_delta_phi * ii / 2);
                double y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (jj) / 2.0 - matrixTen_phi);
                double neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                double z = neg * fibreCom_half_b * Math.cos(fibreCom_delta_phi * ii / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixTen_a) < (-1 * x * x * fibreCom_a)) {
                    z = -matrixTen_half_b * Math.sin(matrixTen_delta_phi * (jj) / 2.0 - matrixTen_phi);
                } else {
                    y = Math.sqrt(fibreCom_a / matrixTen_a * x * x);
                }
                double nx = 0;
                double ny = 2 * matrixTen_a * y;
                double nz = 2 * matrixTen_b * z;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 3] = normale;

                x = -fibreCom_half_a * Math.sin(fibreCom_delta_phi * (ii + 1) / 2);
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (jj) / 2.0 - matrixTen_phi);
                neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                z = neg * fibreCom_half_b * Math.cos(fibreCom_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixTen_a) < (-1 * x * x * fibreCom_a)) {
                    z = -matrixTen_half_b * Math.sin(matrixTen_delta_phi * (jj) / 2.0 - matrixTen_phi);
                } else {
                    y = Math.sqrt(fibreCom_a / matrixTen_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixTen_a * y;
                nz = 2 * matrixTen_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = -fibreCom_half_a * Math.sin(fibreCom_delta_phi * (ii + 1) / 2);
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (jj + 1) / 2.0 - matrixTen_phi);
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreCom_half_b * Math.cos(fibreCom_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixTen_a) < (-1 * x * x * fibreCom_a)) {
                    z = -matrixTen_half_b * Math.sin(matrixTen_delta_phi * (jj + 1) / 2.0 - matrixTen_phi);
                } else {
                    y = Math.sqrt(fibreCom_a / matrixTen_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixTen_a * y;
                nz = 2 * matrixTen_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = -fibreCom_half_a * Math.sin(fibreCom_delta_phi * (ii) / 2);
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (jj + 1) / 2.0 - matrixTen_phi);
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreCom_half_b * Math.cos(fibreCom_delta_phi * (ii) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixTen_a) < (-1 * x * x * fibreCom_a)) {
                    z = -matrixTen_half_b * Math.sin(matrixTen_delta_phi * (jj + 1) / 2.0 - matrixTen_phi);
                } else {
                    y = Math.sqrt(fibreCom_a / matrixTen_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixTen_a * y;
                nz = 2 * matrixTen_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 0] = normale;

            }
        }

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 4  -- Faserdruck- und Matrixdruckversagen
        //
        //  -- Faserdruckversagen
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {

                double x = -fibreCom_half_a * Math.cos(fibreCom_delta_phi * (ii) / 2.0 - fibreCom_phi);
                double y = (jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreCom_a / matrixCom_a) * x;
                double z = fibreCom_half_b * Math.sin(fibreCom_delta_phi * (ii) / 2.0 - fibreCom_phi);
                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 2 * x * fibreCom_a;
                double ny = 0; // (jj) / (float)numPoints_teta * 2;
                double nz = 2 * z * fibreCom_b;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 3] = normale;

                x = -fibreCom_half_a * Math.cos(fibreCom_delta_phi * (ii + 1) / 2.0 - fibreCom_phi);
                y = (jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreCom_a / matrixCom_a) * x;
                z = fibreCom_half_b * Math.sin(fibreCom_delta_phi * (ii + 1) / 2.0 - fibreCom_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreCom_a;
                ny = 0;
                nz = 2 * z * fibreCom_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = -fibreCom_half_a * Math.cos(fibreCom_delta_phi * (ii + 1) / 2.0 - fibreCom_phi);
                y = (jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreCom_a / matrixCom_a) * x;
                z = fibreCom_half_b * Math.sin(fibreCom_delta_phi * (ii + 1) / 2.0 - fibreCom_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreCom_a;
                ny = 0;
                nz = 2 * z * fibreCom_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = -fibreCom_half_a * Math.cos(fibreCom_delta_phi * (ii) / 2.0 - fibreCom_phi);
                y = (jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreCom_a / matrixCom_a) * x;
                z = fibreCom_half_b * Math.sin(fibreCom_delta_phi * (ii) / 2.0 - fibreCom_phi);
                // normale = new Vector3((float)x, (float)y, (float)z);
                nx = 2 * x * fibreCom_a;
                ny = 0;
                nz = 2 * z * fibreCom_b;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 0] = normale;
            }
        }

        //  -- Matrixdruckversagen  
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_teta; jj++) {
            for (int ii = 0; ii < numPoints_phi; ii++) {

                double x = -fibreCom_half_a * Math.sin(fibreCom_delta_phi * ii / 2);
                double y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                double neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                double z = neg * fibreCom_half_b * Math.cos(fibreCom_delta_phi * ii / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a) < (-1 * x * x * fibreCom_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                } else {
                    y = -Math.sqrt(fibreCom_a / matrixCom_a * x * x);
                }
                double nx = 0;
                double ny = 2 * matrixCom_a * y;
                double nz = 2 * matrixCom_b * z;
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4] = normale;

                x = -fibreCom_half_a * Math.sin(fibreCom_delta_phi * (ii + 1) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                z = neg * fibreCom_half_b * Math.cos(fibreCom_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a) < (-1 * x * x * fibreCom_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                } else {
                    y = -Math.sqrt(fibreCom_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixCom_a * y;
                nz = 2 * matrixCom_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = -fibreCom_half_a * Math.sin(fibreCom_delta_phi * (ii + 1) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreCom_half_b * Math.cos(fibreCom_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a) < (-1 * x * x * fibreCom_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                } else {
                    y = -Math.sqrt(fibreCom_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixCom_a * y;
                nz = 2 * matrixCom_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = -fibreCom_half_a * Math.sin(fibreCom_delta_phi * (ii) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreCom_half_b * Math.cos(fibreCom_delta_phi * (ii) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a) < (-1 * x * x * fibreCom_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                } else {
                    y = -Math.sqrt(fibreCom_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = 2 * matrixCom_a * y;
                nz = 2 * matrixCom_b * z;
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 3] = normale;
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
