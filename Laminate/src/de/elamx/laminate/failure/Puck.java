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
import java.util.HashMap;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Diese Klasse ist die Implementierung des Puck-Kriteriums.
 *
 * @author Iwan Kappes
 */
public class Puck extends Criterion {

    public static final String PSPD = Puck.class.getName() + ".pspd";
    public static final String PSPZ = Puck.class.getName() + ".pspz";
    public static final String A0 = Puck.class.getName() + ".a0";
    public static final String LAMBDA_MIN = Puck.class.getName() + ".lambda_min";
    public static final Map<String, String> failureNameMap = new HashMap<>();

    public static Puck getDefault(FileObject obj) {
        Puck pu = new Puck(obj);

        return pu;
    }

    public Puck(FileObject obj) {
        super(obj);
        
        failureNameMap.put("MatrixFailureModusA", NbBundle.getMessage(Puck.class, "Puck." + "MatrixFailureModusA"));
        failureNameMap.put("MatrixFailureModusB", NbBundle.getMessage(Puck.class, "Puck." + "MatrixFailureModusB"));
        failureNameMap.put("MatrixFailureModusC", NbBundle.getMessage(Puck.class, "Puck." + "MatrixFailureModusC"));
        failureNameMap.put("FiberFailureTension", NbBundle.getMessage(Puck.class, "Puck." + "FiberFailureTension"));
        failureNameMap.put("FiberFailureCompression", NbBundle.getMessage(Puck.class, "Puck." + "FiberFailureCompression"));
        
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer layer, StressStrainState sss) {

        double dTemp;
        String message = "Error in Puck-Criteria! Check if the value is zero: ";

        double Pspd = material.getAdditionalValue(PSPD);
        double Pspz = material.getAdditionalValue(PSPZ);
        double a0 = material.getAdditionalValue(A0);
        double lambdamin = material.getAdditionalValue(LAMBDA_MIN);

        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();

        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }

        double Afb;

        if (stresses[0] >= 0.0) {
            Afb = stresses[0] / material.getRParTen();
        } else {
            Afb = -stresses[0] / material.getRParCom();
        }

        double Rass = 0.5 * material.getRShear() / Pspd;

        dTemp = 1.0 + 2.0 * Pspd * material.getRNorCom() / material.getRShear();
        if (dTemp < 0.0) {
            throw new ArithmeticException("illegal double value: " + dTemp);
        }
        Rass = Rass * (Math.sqrt(dTemp) - 1.0);

        double Pssd = Pspd * Rass / material.getRShear();

        dTemp = 1.0 + 2.0 * Pssd;
        if (dTemp < 0.0) {
            throw new ArithmeticException("illegal double value: " + dTemp);
        }
        double tauxyc = material.getRShear() * Math.sqrt(dTemp);

        double Azfb;

        if (stresses[1] == 0.0 && stresses[2] == 0.0) {
            Azfb = 0.0;
        } else {
            if (stresses[1] > 0.0) {
                // Modus A
                double a = (1.0 - Pspz * material.getRNorTen() / material.getRShear()) / material.getRNorTen();
                a *= a;
                double b = 1.0 / material.getRShear() / material.getRShear();
                double c = Pspz / material.getRShear();

                dTemp = a * stresses[1] * stresses[1] + b * stresses[2] * stresses[2];
                if (dTemp < 0.0) {
                    throw new ArithmeticException(message + dTemp);
                }
                Azfb = Math.sqrt(dTemp) + c * stresses[1];
                rf.setMinimalReserveFactor(1.0 / Azfb);
                rf.setFailureName(failureNameMap.get("MatrixFailureModusA"));

            } else if (0 <= Math.abs(stresses[2] / stresses[1])
                    && Math.abs(stresses[1] / stresses[2]) <= Rass / Math.abs(tauxyc)) {
                //Modus B
                double a = Pspd / material.getRShear();
                a *= a;
                double b = 1.0 / material.getRShear() / material.getRShear();
                double c = Pspd / material.getRShear();

                dTemp = a * stresses[1] * stresses[1] + b * stresses[2] * stresses[2];
                if (dTemp < 0.0) {
                    throw new ArithmeticException(message + dTemp);
                }
                Azfb = Math.sqrt(dTemp) + c * stresses[1];

                rf.setMinimalReserveFactor(1.0 / Azfb);
                rf.setFailureName(failureNameMap.get("MatrixFailureModusB"));
            } else {
                //Modus C
                double a = 1 / material.getRNorCom() / material.getRNorCom();
                double b = 0.5 / ((1 + Pssd) * material.getRShear());
                b *= b;
                double c = -material.getRNorCom();

                Azfb = (a * stresses[1] * stresses[1] + b * stresses[2] * stresses[2]) * c / stresses[1];

                rf.setMinimalReserveFactor(1.0 / Azfb);
                rf.setFailureName(failureNameMap.get("MatrixFailureModusC"));
            }
        }

        double rfTempMin = Afb > Azfb ? 1 / Afb : 1 / Azfb;

        if (Azfb != 0 && (rfTempMin * stresses[0] > a0 * material.getRParTen() || rfTempMin * stresses[0] < -a0 * material.getRParCom())) {
            // Abschwächung

            dTemp = 1 - lambdamin * lambdamin;
            if (dTemp < 0.0) {
                throw new ArithmeticException(message + dTemp);
            }
            double a = (1 - a0) / (Math.sqrt(dTemp));

            double delta = Azfb / Afb;

            dTemp = 1.0 + delta * delta * (a * a - a0 * a0);
            if (dTemp < 0.0) {
                throw new ArithmeticException(message + dTemp);
            }
            double lambda = (a0 + a * Math.sqrt(dTemp)) / (1.0 + a * a * delta * delta) * delta;

            Azfb = Azfb / lambda;
            rf.setMinimalReserveFactor(1.0 / Azfb);
        }

        rf.setFailureType(ReserveFactor.MATRIX_FAILURE);

        if (Afb > Azfb) {
            rf.setMinimalReserveFactor(1.0 / Afb);
            if (stresses[0] >= 0.0) {
                rf.setFailureName(failureNameMap.get("FiberFailureTension"));
            } else {
                rf.setFailureName(failureNameMap.get("FiberFailureCompression"));
            }
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }

        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {

        double Pspd = material.getAdditionalValue(PSPD);
        double Pspz = material.getAdditionalValue(PSPZ);
        double a0 = material.getAdditionalValue(A0);
        double lambdamin = material.getAdditionalValue(LAMBDA_MIN);
        if (a0 > 1.0) {
            a0 = 1.0;
        }
        if (a0 < 0.0) {
            a0 = 0.0;
        }

        if (a0 == 1.0) {
            lambdamin = 1.0;
        }
        if (lambdamin > 1.0) {
            lambdamin = 1.0;
        }
        if (lambdamin < 0.0) {
            lambdamin = 0.0;
        }

        double Rass = 0.5 * material.getRShear() / Pspd;
        Rass = Rass * (Math.sqrt(1.0 + 2.0 * Pspd * material.getRNorCom() / material.getRShear()) - 1.0);

        double Pssd = Pspd * Rass / material.getRShear();

        int numP_Face = (int) (quality * 5);    // Elementanzahl für die Strinflächen in radialer Richtung (Faserbruch)
        int numP_Fib = (int) (quality * 50);    // Elementanzahl in Faserlastrichtung
        int numP_ModA = (int) (quality * 25);    // Elementanzahl für Modus A in Umfangsrichtung
        int numP_ModB = (int) (quality * 5);    // Elementanzahl für Modus B in Umfangsrichtung
        int numP_ModC = (int) (quality * 25);    // Elementanzahl für Modus C in Umfangsrichtung

        int numP_All = numP_ModA + 2 * numP_ModB + numP_ModC; // Gesamtelementanzahl für Modus A bis C in Umfangsrichtung

        int numPointsBody = numP_All * numP_Fib * 4;
        int numPointsFace = numP_All * numP_Face * 4;
        int totalNumPoints = numPointsBody + 2 * numPointsFace;

        /* Zunächst werden die Koordinaten in der (sigma_par) sigma_perp tau Ebene erzeugt,
         * da diese für alle sigma_par gleich sind. Im Bereich der Abschwächung
         * werden beide Koordinaten dann nur mit dem Abschwächungsfaktor
         * skaliert. Dies spart Rechenzeit.
         */
        double[] yCoord = new double[numP_All + 1];
        double[] zCoord = new double[numP_All + 1];

        double[] yNorCoord = new double[numP_All + 1];
        double[] zNorCoord = new double[numP_All + 1];

        double y, z, ynor, znor;

        /*
         * Modus A
         */
        /* Bestimmen der Hilfsgrößen, die wiefolgt definiert sind:
         * sqrt( a * (simga_y)² + b * (tau_xy)²) + c * simga_y = 1
         */
        double deltaPhi = Math.PI / numP_ModA;
        double a = (1.0 - Pspz * material.getRNorTen() / material.getRShear()) / material.getRNorTen();
        a *= a;
        double b = 1.0 / material.getRShear() / material.getRShear();
        double c = Pspz / material.getRShear();

        for (int jj = 0; jj < numP_ModA; jj++) {
            y = material.getRNorTen() * Math.sin(deltaPhi * jj);
            // Der Betrag an dieser stelle ist notwendig, da durch Rundungsfehler knapp negative Werte berechnet werden können.
            z = -Math.sqrt(Math.abs((1.0 - c * y) * (1.0 - c * y) - a * y * y) / b);
            if (deltaPhi * jj > Math.PI / 2.0) {
                z *= -1.0;
            }
            yCoord[jj] = y;
            zCoord[jj] = z;

            ynor = a * y / Math.sqrt(Math.abs(a * y * y + b * z * z)) + c;
            znor = b * z / Math.sqrt(Math.abs(a * y * y + b * z * z));
            yNorCoord[jj] = ynor;
            zNorCoord[jj] = znor;
        }

        /*
         * Modus B
         */
        /* Bestimmen der Hilfsgrößen, die wiefolgt definiert sind:
         * sqrt( a * (simga_y)² + b * (tau_xy)²) + c * simga_y = 1
         */
        double deltaY = Rass / numP_ModB;
        a = Pspd / material.getRShear();
        a *= a;
        b = 1.0 / material.getRShear() / material.getRShear();
        c = Pspd / material.getRShear();

        for (int jj = 0; jj < numP_ModB; jj++) {
            // Modus B für positives tau_xy
            y = -deltaY * jj;
            z = Math.sqrt(((1.0 - c * y) * (1.0 - c * y) - a * y * y) / b);
            yCoord[numP_ModA + jj] = y;
            zCoord[numP_ModA + jj] = z;

            ynor = a * y / Math.sqrt(a * y * y + b * z * z) + c;
            znor = b * z / Math.sqrt(a * y * y + b * z * z);
            yNorCoord[numP_ModA + jj] = ynor;
            zNorCoord[numP_ModA + jj] = znor;

            // Modus B für negatives tau_xy
            y = -Rass + deltaY * jj;
            z = -Math.sqrt(((1.0 - c * y) * (1.0 - c * y) - a * y * y) / b);
            yCoord[numP_ModA + numP_ModB + numP_ModC + jj] = y;
            zCoord[numP_ModA + numP_ModB + numP_ModC + jj] = z;

            ynor = a * y / Math.sqrt(a * y * y + b * z * z) + c;
            znor = b * z / Math.sqrt(a * y * y + b * z * z);
            yNorCoord[numP_ModA + numP_ModB + numP_ModC + jj] = ynor;
            zNorCoord[numP_ModA + numP_ModB + numP_ModC + jj] = znor;
        }

        /*
         * Modus C
         */
        /* Bestimmen der Hilfsgrößen, die wiefolgt definiert sind:
         * ( a * (simga_y)² + b * (tau_xy)²) = c * simga_y
         */
        deltaPhi = Math.PI / numP_ModC;
        a = 1 / material.getRNorCom() / material.getRNorCom();
        b = 0.5 / ((1 + Pssd) * material.getRShear());
        b *= b;
        c = -1.0 / material.getRNorCom();

        for (int jj = 0; jj < numP_ModC; jj++) {
            // Linienstartpunkt
            y = -(material.getRNorCom() - Rass) * Math.sin(deltaPhi * jj) - Rass;
            z = Math.sqrt((c * y - a * y * y) / b);
            if (deltaPhi * jj > Math.PI / 2.0) {
                z *= -1.0;
            }
            yCoord[numP_ModA + numP_ModB + jj] = y;
            zCoord[numP_ModA + numP_ModB + jj] = z;

            ynor = 2.0 * y / material.getRNorCom() / material.getRNorCom() + 1 / material.getRNorCom();
            znor = z / (2.0 * (1.0 + Pssd) * (1.0 + Pssd) * material.getRShear() * material.getRShear());
            yNorCoord[numP_ModA + numP_ModB + jj] = ynor;
            zNorCoord[numP_ModA + numP_ModB + jj] = znor;
        }

        // Setzen des Abschlusspunktes. Dieser muss gleich dem Startpunkt sein.
        yCoord[numP_All] = yCoord[0];
        zCoord[numP_All] = zCoord[0];

        yNorCoord[numP_All] = yNorCoord[0];
        zNorCoord[numP_All] = zNorCoord[0];

        /*
         * Aufbau des Körpers
         */
        double delta_x = (material.getRParTen() + material.getRParCom()) / numP_Fib;

        // Berechnen weiterer Hilfsgrößen für Puck
        a = (1 - a0) / (Math.sqrt(1 - lambdamin * lambdamin));
        double lambda;

        // Erzeugen des Versagenskörpers
        //QuadArray quads = new QuadArray(totalNumPoints, QuadArray.COORDINATES | QuadArray.NORMALS);
        //Vector3 normal;
        
        Vector3[] vertices = new Vector3[totalNumPoints];
        Vector3[] normals  = new Vector3[totalNumPoints];
        Vector3 normal;
        
        
        double xnor;
        for (int ii = 0; ii < numP_Fib; ii++) {

            double x = ii * delta_x - material.getRParCom();
            lambda = 1.0;
            xnor = 0.0;
            if (x < -a0 * material.getRParCom()) {
                // Berechnen des Abschwächungsfaktors auf Faserdruck
                lambda = Math.sqrt(1.0 - (-x / material.getRParCom() - a0) * (-x / material.getRParCom() - a0) / a / a);
                xnor = (2.0 * x / material.getRParCom() / material.getRParCom() + 2.0 * a0 / material.getRParCom()) / a / a;
            } else if (x > a0 * material.getRParTen()) {
                // Berechnen des Abschwächungsfaktors auf Faserzug
                lambda = Math.sqrt(1.0 - (x / material.getRParTen() - a0) * (x / material.getRParTen() - a0) / a / a);
                xnor = (2.0 * x / material.getRParTen() / material.getRParTen() - 2.0 * a0 / material.getRParTen()) / a / a;
            }
            for (int jj = 0; jj < numP_All; jj++) {
                vertices[(jj + ii * numP_All) * 4] = new Vector3((float) x, (float) (lambda * yCoord[jj]), (float) (lambda * zCoord[jj]));
                vertices[(jj + ii * numP_All) * 4 + 1] = new Vector3((float) x, (float) (lambda * yCoord[jj + 1]), (float) (lambda * zCoord[jj + 1]));
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj]), (float) (lambda * zNorCoord[jj]));
                normal.normalizeLocal();
                normals[(jj + ii * numP_All) * 4] = normal;
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj + 1]), (float) (lambda * zNorCoord[jj + 1]));
                normal.normalizeLocal();
                normals[(jj + ii * numP_All) * 4 + 1] = normal;
            }

            x = (ii + 1) * delta_x - material.getRParCom();
            lambda = 1.0;
            if (x < -a0 * material.getRParCom()) {
                // Berechnen des Abschwächungsfaktors auf Faserdruck
                lambda = Math.sqrt(1.0 - (-x / material.getRParCom() - a0) * (-x / material.getRParCom() - a0) / a / a);
                xnor = (2.0 * x / material.getRParCom() / material.getRParCom() + 2.0 * a0 / material.getRParCom()) / a / a;
            } else if (x > a0 * material.getRParTen()) {
                // Berechnen des Abschwächungsfaktors auf Faserzug
                lambda = Math.sqrt(1.0 - (x / material.getRParTen() - a0) * (x / material.getRParTen() - a0) / a / a);
                xnor = (2.0 * x / material.getRParTen() / material.getRParTen() - 2.0 * a0 / material.getRParTen()) / a / a;
            }
            for (int jj = 0; jj < numP_All; jj++) {
                vertices[(jj + ii * numP_All) * 4 + 2] = new Vector3((float) x, (float) (lambda * yCoord[jj + 1]), (float) (lambda * zCoord[jj + 1]));
                vertices[(jj + ii * numP_All) * 4 + 3] = new Vector3((float) x, (float) (lambda * yCoord[jj]), (float) (lambda * zCoord[jj]));
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj + 1]), (float) (lambda * zNorCoord[jj + 1]));
                normal.normalizeLocal();
                normals[(jj + ii * numP_All) * 4 + 2] = normal;
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj]), (float) (lambda * zNorCoord[jj]));
                normal.normalizeLocal();
                normals[(jj + ii * numP_All) * 4 + 3] = normal;
            }
        }

        // Faserbruch auf Zug
        int sInd = numPointsBody;
        double delta = lambdamin / numP_Face;
        normal = new Vector3(1.0f, 0.0f, 0.0f);
        for (int ii = 0; ii < numP_Face; ii++) {
            for (int jj = 0; jj < numP_All; jj++) {
                vertices[sInd + (jj + ii * numP_All) * 4 + 3] = new Vector3((float) material.getRParTen(), (float) (delta * ii * yCoord[jj]), (float) (delta * ii * zCoord[jj]));
                vertices[sInd + (jj + ii * numP_All) * 4 + 2] = new Vector3((float) material.getRParTen(), (float) (delta * ii * yCoord[jj + 1]), (float) (delta * ii * zCoord[jj + 1]));
                vertices[sInd + (jj + ii * numP_All) * 4 + 1] = new Vector3((float) material.getRParTen(), (float) (delta * (ii + 1) * yCoord[jj + 1]), (float) (delta * (ii + 1) * zCoord[jj + 1]));
                vertices[sInd + (jj + ii * numP_All) * 4] = new Vector3((float) material.getRParTen(), (float) (delta * (ii + 1) * yCoord[jj]), (float) (delta * (ii + 1) * zCoord[jj]));
                normals[sInd + (jj + ii * numP_All) * 4 + 3] = normal;
                normals[sInd + (jj + ii * numP_All) * 4 + 2] = normal;
                normals[sInd + (jj + ii * numP_All) * 4 + 1] = normal;
                normals[sInd + (jj + ii * numP_All) * 4] = normal;
            }
        }

        // Faserbruch auf Druck
        sInd += numPointsFace;
        normal = new Vector3(-1.0f, 0.0f, 0.0f);
        for (int ii = 0; ii < numP_Face; ii++) {
            for (int jj = 0; jj < numP_All; jj++) {
                vertices[sInd + (jj + ii * numP_All) * 4] = new Vector3((float) -material.getRParCom(), (float) (delta * ii * yCoord[jj]), (float) (delta * ii * zCoord[jj]));
                vertices[sInd + (jj + ii * numP_All) * 4 + 1] = new Vector3((float) -material.getRParCom(), (float) (delta * ii * yCoord[jj + 1]), (float) (delta * ii * zCoord[jj + 1]));
                vertices[sInd + (jj + ii * numP_All) * 4 + 2] = new Vector3((float) -material.getRParCom(), (float) (delta * (ii + 1) * yCoord[jj + 1]), (float) (delta * (ii + 1) * zCoord[jj + 1]));
                vertices[sInd + (jj + ii * numP_All) * 4 + 3] = new Vector3((float) -material.getRParCom(), (float) (delta * (ii + 1) * yCoord[jj]), (float) (delta * (ii + 1) * zCoord[jj]));
                normals[sInd + (jj + ii * numP_All) * 4 + 3] = normal;
                normals[sInd + (jj + ii * numP_All) * 4 + 2] = normal;
                normals[sInd + (jj + ii * numP_All) * 4 + 1] = normal;
                normals[sInd + (jj + ii * numP_All) * 4] = normal;
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
