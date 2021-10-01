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
 * Diese Klasse ist die Implementierung des Hashin-Kriteriums.
 *
 * @author Iwan Kappes
 */
public class Hashin extends Criterion {

    public static Hashin getDefault(FileObject obj) {
        Hashin ha = new Hashin(obj);

        return ha;
    }

    public Hashin(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {

        double dTemp;

        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();

        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }
        double resFac_Fiber = 0;
        double resFac_Matrix;

        //Faserzugversagen
        if (stresses[0] >= 0.0) {

            double F_S = stresses[0] * stresses[0] / material.getRParTen() / material.getRParTen();
            F_S += stresses[2] * stresses[2] / material.getRShear() / material.getRShear();
            resFac_Fiber = Math.sqrt(1 / (F_S));

            rf.setMinimalReserveFactor(resFac_Fiber);
            rf.setFailureName("FiberFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }

        //Faserdruckversagen
        if (stresses[0] < 0.0) {

            double F_S = Math.sqrt(stresses[0] * stresses[0]) / material.getRParCom();
            resFac_Fiber = 1 / (F_S);

            rf.setMinimalReserveFactor(resFac_Fiber);
            rf.setFailureName("FiberFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }

        //Matrixzugversagen
        if (stresses[1] >= 0.0) {

            double M_S = stresses[1] * stresses[1] / material.getRNorTen() / material.getRNorTen();
            M_S += stresses[2] * stresses[2] / material.getRShear() / material.getRShear();
            resFac_Matrix = Math.sqrt(1 / (M_S));

            if (resFac_Matrix < resFac_Fiber) {
                rf.setMinimalReserveFactor(resFac_Matrix);
                rf.setFailureName("MatrixFailureTension");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }

        }

        //Matrixdruckversagen
        if (stresses[1] < 0.0) {

            double Q = 0.25 * stresses[1] * stresses[1] / material.getRShear() / material.getRShear();
            Q += stresses[2] * stresses[2] / material.getRShear() / material.getRShear();

            double L = 0.25 * material.getRNorCom() / material.getRShear() / material.getRShear();
            L -= 1.0 / material.getRNorCom();
            L *= stresses[1];
            
            dTemp = L * L + 4.0 * Q;
            if (dTemp < 0.0) {
                throw new ArithmeticException("illegal double value: " + dTemp);
            }

            resFac_Matrix = (Math.sqrt(dTemp) - L) / (2.0 * Q);

            if (resFac_Matrix < resFac_Fiber) {
                rf.setMinimalReserveFactor(resFac_Matrix);
                rf.setFailureName("MatrixFailureCompression");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }

        }

        rf.setFailureName(NbBundle.getMessage(Hashin.class, "Hashin." + rf.getFailureName()));

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

        /*  Festigkeitskennwerte:
         material.getRParTen()
         material.getRParCom()
         material.getRNorTen()
         material.getRNorCom()
         material.getRShear()
         */
        int numPoints_phi = (int) (quality * 10);
        int numPoints_teta = 2 * numPoints_phi;

        //double delta_phi = Math.PI / numPoints_phi;
        //double delta_teta = 2 * Math.PI / numPoints_teta;

        int numPoints = (numPoints_phi + 1) * (numPoints_teta + 1) * 4;
        int totalNumPoints = 8 * numPoints + (numPoints_teta + 1) * 4;

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
        double fibreTen_a = 1.0 / (material.getRParTen() * material.getRParTen());
        //double fibreTen_b = 1.0 / (material.getRShear() * material.getRShear());
        // große und kleine Halbachse der Ellipse
        double fibreTen_half_a = material.getRParTen();
        double fibreTen_half_b = material.getRShear();
        // Gültigkeitsbereich für den Winkel festlegen. (Ist immer -90° bis + 90° bzw. -PI/2 bis + PI/2)
        double fibreTen_phi = Math.PI / 2.0;
        double fibreTen_delta_phi = 2.0 * fibreTen_phi / (numPoints_phi);

        // Kontrollausgaben
/*        System.out.println("fibreTen_a      = " + fibreTen_a );
         System.out.println("fibreTen_b      = " + fibreTen_b );
         System.out.println("fibreTen_half_a = " + fibreTen_half_a  );
         System.out.println("fibreTen_half_b = " + fibreTen_half_b  );
         System.out.println("fibreTen_phi    = " + fibreTen_phi *180/Math.PI );
         System.out.println("fibreTen_delta_phi  = " + fibreTen_delta_phi *180/Math.PI );
         */
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
        double matrixTen_a = 1.0 / (material.getRNorTen() * material.getRNorTen());
        double matrixTen_b = 1.0 / (material.getRShear() * material.getRShear());
        // große und kleine Halbachse der Ellipse
        double matrixTen_half_a = material.getRNorTen();
        double matrixTen_half_b = material.getRShear();
        // Gültigkeitsbereich für den Winkel festlegen. (Ist immer -90° bis + 90° bzw. -PI/2 bis + PI/2)
        double matrixTen_phi = Math.PI / 2.0;
        double matrixTen_delta_phi = 2.0 * matrixTen_phi / (numPoints_phi);
        // Kontrollausgaben
/*       System.out.println("matrixTen_a      = " + matrixTen_a );
         System.out.println("matrixTen_b      = " + matrixTen_b );
         System.out.println("matrixTen_half_a = " + matrixTen_half_a  );
         System.out.println("matrixTen_half_b = " + matrixTen_half_b  );
         System.out.println("matrixTen_phi    = " + matrixTen_phi *180/Math.PI );
         System.out.println("matrixTen_delta_phi  = " + matrixTen_delta_phi *180/Math.PI );
         */

        /* Parameter für das MATRIX-DRUCK-Versagen bestimmen
         *   Der Versagenskörper der beschrieben werden muss, ist ein Zylinder
         *   entlang der sigma_x-Achse mit einer elliptischen Grundfläche in der
         *   sigma_y-tau_xy-Ebene. Prinzipiell sind die Halbachsen und
         *   die Verschiebung des Ellipsenmittelpunktes immer gleich und nur von den
         *   Materialkennwerten abhängig. Also kann man die hier einmal
         *   bestimmen und dann fertsch.
         *   Von der Ellipse wird nur der Teil benötigt, der im Matrixdruckbereich,
         *   also sigma_y < 0  liegt. Es muss also der Gültigkeitsbereich des Winkels
         *   eingeschränkt werden.
         */
        double matrixCom_a = 0.25 / (material.getRShear() * material.getRShear());
        double matrixCom_b = 4 * matrixCom_a;
        double matrixCom_d = 0.5 * (material.getRNorCom() * material.getRNorCom() * matrixCom_a - 1.0) / material.getRNorCom();
        // große und kleine Halbachse der Ellipse
        double matrixCom_half_a = Math.sqrt((matrixCom_d * matrixCom_d + matrixCom_a) / (matrixCom_a * matrixCom_a));
        double matrixCom_half_b = Math.sqrt((matrixCom_d * matrixCom_d + matrixCom_a) / (matrixCom_a * matrixCom_b));
        // Verschiebung der Ellipse auf der sigma_y-Achse
        double matrixCom_s1 = -matrixCom_d / matrixCom_a;
        // Gültigkeitsbereich für den Winkel festlegen.
        double matrixCom_phi = 0.0;
        double matrixCom_phi_cos = Math.acos(-1 * matrixCom_s1 / matrixCom_half_a);
        //double matrixCom_phi_sin = Math.asin(material.getRShear() / matrixCom_half_b);
        matrixCom_phi = Math.PI - matrixCom_phi_cos;
        double matrixCom_delta_phi = 2.0 * matrixCom_phi / (numPoints_phi);
        // Kontrollausgaben
/*        System.out.println("matrixCom_a      = " + matrixCom_a );
         System.out.println("matrixCom_b      = " + matrixCom_b );
         System.out.println("matrixCom_d      = " + matrixCom_d );
         System.out.println("matrixCom_s1     = " + matrixCom_s1 );
         System.out.println("matrixCom_half_a = " + matrixCom_half_a  );
         System.out.println("matrixCom_half_b = " + matrixCom_half_b  );
         System.out.println("matrixCom_phi    = " + matrixCom_phi *180/Math.PI );
         System.out.println("matrixCom_phi_cos = " + matrixCom_phi_cos *180/Math.PI );
         System.out.println("matrixCom_phi_sin = " + matrixCom_phi_sin *180/Math.PI );
         System.out.println("matrixCom_delta_phi  = " + matrixCom_delta_phi *180/Math.PI );
         */

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
                double nx = fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                double ny = 0; // (jj) / (float)numPoints_teta * 2;
                double nz = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_teta) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_teta) * 4] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                y = (jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixTen_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                ny = 0; // (jj+1) / (float)numPoints_teta * 2* Math.sqrt(fibreTen_a/matrixTen_a ) *x;
                nz = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                y = (jj + 1) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixTen_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                ny = 0; // (jj+1) / (float)numPoints_teta * 2* Math.sqrt(fibreTen_a/matrixTen_a ) *x;
                nz = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[(ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[(ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                y = (jj) / (float) numPoints_teta * 2 * Math.sqrt(fibreTen_a / matrixTen_a) * x;
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                ny = 0; // (jj) / (float)numPoints_teta * 2* Math.sqrt(fibreTen_a/matrixTen_a ) *x;
                nz = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
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
                double y = (jj) / (float) numPoints_teta * 2 * (-matrixCom_d / matrixCom_a - Math.sqrt((matrixCom_d / matrixCom_a) * (matrixCom_d / matrixCom_a) + fibreTen_a / matrixCom_a * x * x));
                double z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                // Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                double ny = 0;
                double nz = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                y = (jj) / (float) numPoints_teta * 2 * (-matrixCom_d / matrixCom_a - Math.sqrt((matrixCom_d / matrixCom_a) * (matrixCom_d / matrixCom_a) + fibreTen_a / matrixCom_a * x * x));
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                ny = 0;
                nz = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                y = (jj + 1) / (float) numPoints_teta * 2 * (-matrixCom_d / matrixCom_a - Math.sqrt((matrixCom_d / matrixCom_a) * (matrixCom_d / matrixCom_a) + fibreTen_a / matrixCom_a * x * x));
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                //normale = new Vector3((float)x, (float)y, (float)z);
                nx = fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                ny = 0;
                nz = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2.0 - fibreTen_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = fibreTen_half_a * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                y = (jj + 1) / (float) numPoints_teta * 2 * (-matrixCom_d / matrixCom_a - Math.sqrt((matrixCom_d / matrixCom_a) * (matrixCom_d / matrixCom_a) + fibreTen_a / matrixCom_a * x * x));
                z = fibreTen_half_b * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                // normale = new Vector3((float)x, (float)y, (float)z);
                nx = fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
                ny = 0;
                nz = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii) / 2.0 - fibreTen_phi);
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
                double y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi) + matrixCom_s1;
                double neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                double z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * ii / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a - 2 * matrixCom_d * y) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                } else {
                    y = -matrixCom_d / matrixCom_a - Math.sqrt((matrixCom_d / matrixCom_a) * (matrixCom_d / matrixCom_a) + fibreTen_a / matrixCom_a * x * x);
                }
                double nx = 0;
                double ny = -matrixCom_half_b * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                double nz = -matrixCom_half_a * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 3] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi) + matrixCom_s1;
                neg = Math.pow(-1, Math.round((jj - 1) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a - 2 * matrixCom_d * y) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                } else {
                    y = -matrixCom_d / matrixCom_a - Math.sqrt((matrixCom_d / matrixCom_a) * (matrixCom_d / matrixCom_a) + fibreTen_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = -matrixCom_half_b * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                nz = -matrixCom_half_a * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 2] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii + 1) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi) + matrixCom_s1;
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii + 1) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a - 2 * matrixCom_d * y) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                } else {
                    y = -matrixCom_d / matrixCom_a - Math.sqrt((matrixCom_d / matrixCom_a) * (matrixCom_d / matrixCom_a) + fibreTen_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = -matrixCom_half_b * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                nz = -matrixCom_half_a * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 1] = normale;

                x = fibreTen_half_a * Math.sin(fibreTen_delta_phi * (ii) / 2);
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi) + matrixCom_s1;
                neg = Math.pow(-1, Math.round((jj) / (double) numPoints_teta));
                z = neg * fibreTen_half_b * Math.cos(fibreTen_delta_phi * (ii) / 2);
                // überprüfen ob z_matrix < z_faser
                if ((-1 * y * y * matrixCom_a - 2 * matrixCom_d * y) < (-1 * x * x * fibreTen_a)) {
                    z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                } else {
                    y = -matrixCom_d / matrixCom_a - Math.sqrt((matrixCom_d / matrixCom_a) * (matrixCom_d / matrixCom_a) + fibreTen_a / matrixCom_a * x * x);
                }
                nx = 0;
                ny = -matrixCom_half_b * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                nz = -matrixCom_half_a * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_phi) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_phi) * 4 + 0] = normale;
            }
        }

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 3  -- Faserversagen
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {
                double x = -material.getRParCom();
                double y = (jj) / (float) (numPoints_teta) * 2.0 * matrixTen_half_a * Math.cos(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                double z = (jj) / (float) (numPoints_teta) * 2.0 * matrixTen_half_b * Math.sin(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                Vector3 normale = new Vector3(-1.0f, 0.0f, 0.0f);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4] = normale;

                x = -material.getRParCom();
                y = (jj) / (float) (numPoints_teta) * 2.0 * matrixTen_half_a * Math.cos(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                z = (jj) / (float) (numPoints_teta) * 2.0 * matrixTen_half_b * Math.sin(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = -material.getRParCom();
                y = (jj + 1) / (float) (numPoints_teta) * 2.0 * matrixTen_half_a * Math.cos(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                z = (jj + 1) / (float) (numPoints_teta) * 2.0 * matrixTen_half_b * Math.sin(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = -material.getRParCom();
                y = (jj + 1) / (float) (numPoints_teta) * 2.0 * matrixTen_half_a * Math.cos(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                z = (jj + 1) / (float) (numPoints_teta) * 2.0 * matrixTen_half_b * Math.sin(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 3] = normale;
            }
        }
        // Fall 3  -- Matrixzugversagen
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {
                double x = -material.getRParCom() * (jj) * 2.0 / (float) numPoints_teta;
                double y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                double z = matrixTen_half_b * Math.sin(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                //Vector3 normale = new Vector3((float)x, (float)y, (float)z);
                double nx = 0;
                double ny = matrixTen_half_b * Math.cos(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                double nz = matrixTen_half_a * Math.sin(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4] = normale;

                x = -material.getRParCom() * (jj + 1) * 2.0 / (float) numPoints_teta;
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                z = matrixTen_half_b * Math.sin(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                // normale = new Vector3((float)x, (float)y, (float)z);
                nx = 0;
                ny = matrixTen_half_b * Math.cos(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                nz = matrixTen_half_a * Math.sin(matrixTen_delta_phi * (ii) / 2.0 - matrixTen_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = -material.getRParCom() * (jj + 1) * 2.0 / (float) numPoints_teta;
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                z = matrixTen_half_b * Math.sin(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                nx = 0;
                ny = matrixTen_half_b * Math.cos(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                nz = matrixTen_half_a * Math.sin(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = -material.getRParCom() * (jj) * 2.0 / (float) numPoints_teta;
                y = matrixTen_half_a * Math.cos(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                z = matrixTen_half_b * Math.sin(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                nx = 0;
                ny = matrixTen_half_b * Math.cos(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                nz = matrixTen_half_a * Math.sin(matrixTen_delta_phi * (ii + 1) / 2.0 - matrixTen_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 3] = normale;
            }
        }

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Fall 4  -- Faserversagen
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {
                double x = -material.getRParCom();
                double y = (jj) / (float) (numPoints_teta) * 2.0 * (-matrixCom_half_a * Math.cos(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi) + matrixCom_s1);
                double z = (jj) / (float) (numPoints_teta) * 2.0 * (-matrixCom_half_b * Math.sin(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi));
                Vector3 normale = new Vector3(-1.0f, 0.0f, 0.0f);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 3] = normale;

                x = -material.getRParCom();
                y = (jj + 1) / (float) (numPoints_teta) * 2.0 * (-matrixCom_half_a * Math.cos(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi) + matrixCom_s1);
                z = (jj + 1) / (float) (numPoints_teta) * 2.0 * (-matrixCom_half_b * Math.sin(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi));
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = -material.getRParCom();
                y = (jj + 1) / (float) (numPoints_teta) * 2.0 * (-matrixCom_half_a * Math.cos(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi) + matrixCom_s1);
                z = (jj + 1) / (float) (numPoints_teta) * 2.0 * (-matrixCom_half_b * Math.sin(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi));
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = -material.getRParCom();
                y = (jj) / (float) (numPoints_teta) * 2.0 * (-matrixCom_half_a * Math.cos(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi) + matrixCom_s1);
                z = (jj) / (float) (numPoints_teta) * 2.0 * (-matrixCom_half_b * Math.sin(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi));
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 0] = normale;
            }
        }
        // Fall 4  -- Matrixversagen
        sInd += numPoints;

        for (int jj = 0; jj < numPoints_phi; jj++) {
            for (int ii = 0; ii < numPoints_teta; ii++) {
                double x = -material.getRParCom() * (jj) * 2.0 / (float) numPoints_teta;
                double y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi) + matrixCom_s1;
                double z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi);
                double nx = 0;
                double ny = -matrixCom_half_b * Math.cos(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi);
                double nz = -matrixCom_half_a * Math.sin(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi);
                Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 3] = normale;

                x = -material.getRParCom() * (jj) * 2.0 / (float) numPoints_teta;
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi) + matrixCom_s1;
                z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi);
                nx = 0;
                ny = -matrixCom_half_b * Math.cos(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi);
                nz = -matrixCom_half_a * Math.sin(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 2] = normale;

                x = -material.getRParCom() * (jj + 1) * 2.0 / (float) numPoints_teta;
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi) + matrixCom_s1;
                z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi);
                nx = 0;
                ny = -matrixCom_half_b * Math.cos(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi);
                nz = -matrixCom_half_a * Math.sin(matrixCom_delta_phi * (ii + 1) / 2.0 - matrixCom_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 1] = normale;

                x = -material.getRParCom() * (jj + 1) * 2.0 / (float) numPoints_teta;
                y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi) + matrixCom_s1;
                z = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi);
                nx = 0;
                ny = -matrixCom_half_b * Math.cos(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi);
                nz = -matrixCom_half_a * Math.sin(matrixCom_delta_phi * (ii) / 2.0 - matrixCom_phi);
                normale = new Vector3((float) nx, (float) ny, (float) nz);
                normale.normalizeLocal();
                vertices[sInd + (ii + jj * numPoints_teta) * 4 + 0] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_teta) * 4 + 0] = normale;
            }
        }

        sInd += numPoints;

        if (2 * material.getRShear() < material.getRNorCom()) {
//            for (int jj = 0; jj < numPoints_teta; jj++){
            for (int jj = 0; jj < numPoints_phi; jj++) {

                double x = 0;
                double y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi) + matrixCom_s1;
                double z_m = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                double z_f = material.getRShear();
                double z = 0;
                if ((jj == 0) | (z_m > z_f)) {
                    double nx = 1;
                    double ny = 0;
                    double nz = 0;
                    Vector3 normale = new Vector3((float) nx, (float) ny, (float) nz);
                    normale.normalizeLocal();
                    vertices[sInd + (jj) * 4 + 0] = new Vector3((float) x, (float) y, (float) z_m);
                    normals[sInd + (jj) * 4 + 0] = normale;
                    vertices[sInd + (jj + numPoints_phi) * 4 + 3] = new Vector3((float) x, (float) y, (float) -z_m);
                    normals[sInd + (jj + numPoints_phi) * 4 + 3] = normale;

                    y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi) + matrixCom_s1;
                    z_m = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi);
                    vertices[sInd + (jj) * 4 + 1] = new Vector3((float) x, (float) y, (float) z_m);
                    normals[sInd + (jj) * 4 + 1] = normale;
                    vertices[sInd + (jj + numPoints_phi) * 4 + 2] = new Vector3((float) x, (float) y, (float) -z_m);
                    normals[sInd + (jj + numPoints_phi) * 4 + 2] = normale;

                    if (z_m < z_f) {
                        z = z_m;
                    } else {
                        z = z_f;
                    }
                    y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj + 1) / 2.0 - matrixCom_phi) + matrixCom_s1;
                    vertices[sInd + (jj) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                    normals[sInd + (jj) * 4 + 2] = normale;
                    vertices[sInd + (jj + numPoints_phi) * 4 + 1] = new Vector3((float) x, (float) y, (float) -z);
                    normals[sInd + (jj + numPoints_phi) * 4 + 1] = normale;

                    z_m = -matrixCom_half_b * Math.sin(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi);
                    if (z_m < z_f) {
                        z = z_m;
                    } else {
                        z = z_f;
                    }
                    y = -matrixCom_half_a * Math.cos(matrixCom_delta_phi * (jj) / 2.0 - matrixCom_phi) + matrixCom_s1;
                    vertices[sInd + (jj) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                    normals[sInd + (jj) * 4 + 3] = normale;
                    vertices[sInd + (jj + numPoints_phi) * 4 + 0] = new Vector3((float) x, (float) y, (float) -z);
                    normals[sInd + (jj + numPoints_phi) * 4 + 0] = normale;
                }
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
