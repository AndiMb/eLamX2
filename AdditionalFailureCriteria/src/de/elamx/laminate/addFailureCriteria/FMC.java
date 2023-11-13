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
 * Diese Klasse ist die Implementierung des Cuntze-Kriteriums.
 *
 * @author Iwan Kappes
 */
public class FMC extends Criterion {

    public static final String MUESP = FMC.class.getName() + ".muesp";
    public static final String M = FMC.class.getName() + ".m";

    public static FMC getDefault(FileObject obj) {
        FMC fmc = new FMC(obj);

        return fmc;
    }

    public FMC(FileObject obj) {
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

        double mue_sp = material.getAdditionalValue(MUESP);
        double m = material.getAdditionalValue(M);

        double ff;
        double iff13;
        double iff2;

        String Text_f_f;
        String Text_f_m;
        String Text_f_sp;

        if (stresses[0] >= 0.0) {                                                //Faserzugversagen
            ff = Math.pow(stresses[0] / material.getRParTen(), m);
            Text_f_f = "FiberFailureTension";
        } else {                                                                  //Faserdruckversagen
            ff = Math.pow(-stresses[0] / material.getRParCom(), m);
            Text_f_f = "FiberFailureCompression";
        }

        if (stresses[1] >= 0.0) {                                                //Matrixzugversagen
            iff13 = Math.pow(stresses[1] / material.getRNorTen(), m);
            Text_f_m = "MatrixFailureTension";
        } else {                                                                  //Matrixdruckversagen
            iff13 = Math.pow(-stresses[1] / material.getRNorCom(), m);
            Text_f_m = "MatrixFailureCompression";
        }

        //Matrixschubversagen
        /*
        * Aus der Quelle:
        * Efficient 3D and 2D failure conditions for UD laminae and their application within the verification of the laminate design; 
        * R.G. Cuntze; Composites Science and Technology 66 (2006) 1081–1096
        * wird an dieser Stelle Gleichung 10b statt der in der Quelle bevorzugten Gleichung 11a verwendet. Beide Gleichungen sind nur für eines
        * Reservefaktor von 1, also für die eigentliche Versagensbedingung identisch.
        * Bei der Verwendung von Gleichung 11a lässt sich bei einer Multiplikation aller Spannungsanteile mit einem Reservefaktor, dieser
        * nicht mehr ausklammern. Dies führt dazu, dass bei Verwendung von Gleichung 11a es nicht mehr möglich ist, im Falle eines Reservefaktors
        * von 0,1 alle Lasten mit einem Faktor 10 zu versehen und damit einen Reservefaktor von 1 zu erreichen. Das Verhalten des Versagenskriteriums
        * wird durch die Verwendung von Gleichung 11a nicht-linear. Es wird angenommen, dass eLamX²-Nutzende ein solches Verhalten nicht erwarten,
        * was zu größeren Fehlern führen könnte.
        * Zudem wird eine Prüfung eingeführt, dass der Zähler grundsätzlich positiv bleiben muss, um mathematisch unzulässige Operationen während
        * der Potenzbildung mit m zu vermeiden.
        */
        double stresses2 = Math.abs(stresses[2]);
        iff2 = stresses2 != 0 && stresses2 + mue_sp * stresses[1] > 0.0 ? Math.pow((stresses2 + mue_sp * stresses[1]) / material.getRShear(), m) : 0.0;
        Text_f_sp = "MatrixFailureShear";

        if (ff > iff13) {
            if (ff > iff2) {
                rf.setFailureName(Text_f_f);
                rf.setFailureType(ReserveFactor.FIBER_FAILURE);
            } else {
                rf.setFailureName(Text_f_sp);
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        } else {
            if (iff13 > iff2) {
                rf.setFailureName(Text_f_m);
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            } else {
                rf.setFailureName(Text_f_sp);
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        }

        // Gesamtreservefaktor
        double f = Math.pow(ff + iff13 + iff2, (-1.0 / m));

        rf.setMinimalReserveFactor(f);

        rf.setFailureName(NbBundle.getMessage(FMC.class, "FMC." + rf.getFailureName()));

        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {

        double b_sp = material.getAdditionalValue(MUESP);                       // Cuntze Kurvenparameter
        double m = material.getAdditionalValue(M);                              // Kurvenrundungsparameter

        double R_pt = material.getRParTen();
        double R_pc = material.getRParCom();
        double R_st = material.getRNorTen();
        double R_sc = material.getRNorCom();
        double R_sp = material.getRShear();

        int numP_FF = (int) (quality * 25);     // Elementanzahl in Faserlastrichtung (in eine Richtung (positiv/negativ)
        int numP_IFF = (int) (quality * 35);    // Elementanzahl für Modus A in Umfangsrichtung
        int numPointsBody = (2 * numP_FF - 1) * numP_IFF * 4;

        double[] yCoord = new double[numP_IFF];
        double[] zCoord = new double[numP_IFF];

        double[] yNorCoord = new double[numP_IFF];
        double[] zNorCoord = new double[numP_IFF];

        double deltaPhi = 2.0 * Math.PI / (numP_IFF - 1);
        double y, z, ynor, znor, rf;

        double a = 0.0;

        double h = 0.0001;

        for (int jj = 0; jj < numP_IFF; jj++) {
            y = Math.cos(deltaPhi * jj);
            z = Math.sin(deltaPhi * jj);
            rf = Reservefaktor_Berechnung2D(y, z, R_st, R_sc, R_sp, b_sp, m);
            y *= rf;
            z *= rf;
            yCoord[jj] = y;
            zCoord[jj] = z;

            ynor = Reservefaktor_Berechnung2D(y + h, z, R_st, R_sc, R_sp, b_sp, m);
            ynor -= Reservefaktor_Berechnung2D(y - h, z, R_st, R_sc, R_sp, b_sp, m);
            ynor /= -(2 * h);
            znor = Reservefaktor_Berechnung2D(y, z + h, R_st, R_sc, R_sp, b_sp, m);
            znor -= Reservefaktor_Berechnung2D(y, z - h, R_st, R_sc, R_sp, b_sp, m);
            znor /= -(2 * h);
            yNorCoord[jj] = ynor;
            zNorCoord[jj] = znor;
        }

        Vector3[] vertices = new Vector3[numPointsBody];
        Vector3[] normals  = new Vector3[numPointsBody];
        Vector3 normal;
        double lambda = 1.0;
        double x;
        double xnor = 0.0;

        double delta_x = material.getRParCom() / (numP_FF - 1);
        for (int ii = 0; ii < numP_FF - 1; ii++) {

            x = ii * delta_x - material.getRParCom();
            x = -mapping(-x, R_pc, m);
            lambda = Math.pow(1.0 - Math.pow(-x / R_pc, m), 1 / m);
            xnor = m / Math.pow(R_pc, m) * Math.pow(Math.abs(x), m - 1);
            xnor = -xnor;
            for (int jj = 0; jj < numP_IFF - 1; jj++) {
                vertices[(jj + ii * numP_IFF) * 4] = new Vector3((float) x, (float) (lambda * yCoord[jj]), (float) (lambda * zCoord[jj]));
                vertices[(jj + ii * numP_IFF) * 4 + 1] = new Vector3((float) x, (float) (lambda * yCoord[jj + 1]), (float) (lambda * zCoord[jj + 1]));
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj]), (float) (lambda * zNorCoord[jj]));
                normal.normalizeLocal();
                normals[(jj + ii * numP_IFF) * 4] = normal;
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj + 1]), (float) (lambda * zNorCoord[jj + 1]));
                normal.normalizeLocal();
                normals[(jj + ii * numP_IFF) * 4 + 1] = normal;
            }

            x = Math.min((ii + 1) * delta_x - material.getRParCom(), 0.0);
            x = -mapping(-x, R_pc, m);
            lambda = Math.pow(1.0 - Math.pow(-x / R_pc, m), 1 / m);
            xnor = m / Math.pow(R_pc, m) * Math.pow(Math.abs(x), m - 1);
            xnor = -xnor;
            for (int jj = 0; jj < numP_IFF - 1; jj++) {
                vertices[(jj + ii * numP_IFF) * 4 + 2] = new Vector3((float) x, (float) (lambda * yCoord[jj + 1]), (float) (lambda * zCoord[jj + 1]));
                vertices[(jj + ii * numP_IFF) * 4 + 3] = new Vector3((float) x, (float) (lambda * yCoord[jj]), (float) (lambda * zCoord[jj]));
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj + 1]), (float) (lambda * zNorCoord[jj + 1]));
                normal.normalizeLocal();
                normals[(jj + ii * numP_IFF) * 4 + 2] = normal;
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj]), (float) (lambda * zNorCoord[jj]));
                normal.normalizeLocal();
                normals[(jj + ii * numP_IFF) * 4 + 3] = normal;
            }
        }

        delta_x = material.getRParTen() / (numP_FF - 1);
        for (int ii = 0; ii < numP_FF - 1; ii++) {

            x = ii * delta_x;
            x = mapping(x, R_pt, m);
            lambda = Math.pow(1.0 - Math.pow(x / R_pt, m), 1 / m);
            xnor = m / Math.pow(R_pt, m) * Math.pow(Math.abs(x), m - 1);
            for (int jj = 0; jj < numP_IFF - 1; jj++) {
                vertices[(jj + (numP_FF + ii) * numP_IFF) * 4] = new Vector3((float) x, (float) (lambda * yCoord[jj]), (float) (lambda * zCoord[jj]));
                vertices[(jj + (numP_FF + ii) * numP_IFF) * 4 + 1] = new Vector3((float) x, (float) (lambda * yCoord[jj + 1]), (float) (lambda * zCoord[jj + 1]));
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj]), (float) (lambda * zNorCoord[jj]));
                normal.normalizeLocal();
                normals[(jj + (numP_FF + ii) * numP_IFF) * 4] = normal;
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj + 1]), (float) (lambda * zNorCoord[jj + 1]));
                normal.normalizeLocal();
                normals[(jj + (numP_FF + ii) * numP_IFF) * 4 + 1] = normal;
            }

            x = (ii + 1) * delta_x;
            x = mapping(x, R_pt, m);
            lambda = Math.pow(1.0 - Math.pow(x / R_pt, m), 1 / m);
            xnor = m / Math.pow(R_pt, m) * Math.pow(Math.abs(x), m - 1);
            for (int jj = 0; jj < numP_IFF - 1; jj++) {
                vertices[(jj + (numP_FF + ii) * numP_IFF) * 4 + 2] = new Vector3((float) x, (float) (lambda * yCoord[jj + 1]), (float) (lambda * zCoord[jj + 1]));
                vertices[(jj + (numP_FF + ii) * numP_IFF) * 4 + 3] = new Vector3((float) x, (float) (lambda * yCoord[jj]), (float) (lambda * zCoord[jj]));
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj + 1]), (float) (lambda * zNorCoord[jj + 1]));
                normal.normalizeLocal();
                normals[(jj + (numP_FF + ii) * numP_IFF) * 4 + 2] = normal;
                normal = new Vector3((float) xnor, (float) (lambda * yNorCoord[jj]), (float) (lambda * zNorCoord[jj]));
                normal.normalizeLocal();
                normals[(jj + (numP_FF + ii) * numP_IFF) * 4 + 3] = normal;
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

    private double mapping(double x, double xmax, double m) {
        return xmax * Math.pow(1.0 - Math.pow((xmax - x) / xmax, m), 1 / m);
    }

    private double Reservefaktor_Berechnung2D(double Y, double Z, double R_st, double R_sc, double R_sp, double b_sp, double m) {

        double iff1 = Y > 0.0 ? Math.pow(Y / R_st, m) : 0.0;
        double iff3 = Y < 0.0 ? Math.pow(-Y / R_sc, m) : 0.0;
        double zt = Math.abs(Z);
        double iff2 = zt > 0.00001 && zt + b_sp * Y > 0.0 ? Math.pow((zt + b_sp * Y) / R_sp, m) : 0.0;

        // Berechnung des resultierenden Reservefaktors
        return Math.pow(iff1 + iff2 + iff3, (-1.0 / m));
    }
}
