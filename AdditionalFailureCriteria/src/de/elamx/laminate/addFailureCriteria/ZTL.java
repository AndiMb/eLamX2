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
 * Diese Klasse ist die Implementierung des ZTL-Kriteriums.
 *
 * @author Andreas Hauffe
 */
public class ZTL extends Criterion {

    public static final String F12_STAR = ZTL.class.getName() + ".f12star";

    public static ZTL getDefault(FileObject obj) {
        ZTL ztl = new ZTL(obj);

        return ztl;
    }

    public ZTL(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double f12star = material.getAdditionalValue(F12_STAR);
        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();

        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }

        double F1 = 1 / material.getRParTen() - 1 / material.getRParCom();
        double F2 = 1 / material.getRNorTen() - 1 / material.getRNorCom();
        double F11 = 1 / (material.getRParTen() * material.getRParCom());
        double F22 = 1 / (material.getRNorTen() * material.getRNorCom());
        double F12 = f12star * Math.sqrt(F11 * F22);
        double F66 = 1 / (material.getRShear() * material.getRShear());

        double Q = F11 * stresses[0] * stresses[0];
        Q += 2.0 * F12 * stresses[0] * stresses[1];
        Q += F22 * stresses[1] * stresses[1];
        Q += F66 * stresses[2] * stresses[2];

        double L = F1 * stresses[0] + F2 * stresses[1];

        double resFac = (Math.sqrt(L * L + 4.0 * Q) - L) / (2.0 * Q);

        rf.setMinimalReserveFactor(resFac);
        rf.setFailureName("MatrixFailure");
        rf.setFailureType(ReserveFactor.MATRIX_FAILURE);

        double value;

        if (stresses[0] >= 0.0) {
            value = material.getRParTen() / stresses[0];
            if (value < rf.getMinimalReserveFactor()) {
                rf.setMinimalReserveFactor(value);
                rf.setFailureName("FiberFailureTension");
                rf.setFailureType(ReserveFactor.FIBER_FAILURE);
            }
        } else {
            value = -material.getRParCom() / stresses[0];
            if (value < rf.getMinimalReserveFactor()) {
                rf.setMinimalReserveFactor(value);
                rf.setFailureName("FiberFailureCompression");
                rf.setFailureType(ReserveFactor.FIBER_FAILURE);
            }
        }

        rf.setFailureName(NbBundle.getMessage(ZTL.class, "ZTL." + rf.getFailureName()));

        return rf;
    }

    @Override
    public Mesh getAsMesh(Material material, double quality) {

        double f12star = material.getAdditionalValue(F12_STAR);

        /* Allgemeine Bemerkunge:
         * Der abgeschnitte Ellipsiod wird immer "ringweise" aufgebaut. Zu jedem
         * x-Wert (welcher vorgegeben wird) wird die dazugehörige Schittellipse 
         * berechnet. Dadurch erhält man auf dem schräg liegenden Versagenskörper 
         * ein "gerades" Netz. Die Enden werden auch gerade abgeschnitten. Würde
         * man wie im Tsai/Wu-Kriterium arbeiten, würde man an den Enden dreieckige
         * Elemente erhalten.
         */
        // Berechnen der Festigkeitsparameter des Tsai-Wu-Kriteriums aus den Festigkeiten
        double F1 = 1 / material.getRParTen() - 1 / material.getRParCom();
        double F2 = 1 / material.getRNorTen() - 1 / material.getRNorCom();
        double F11 = 1 / (material.getRParTen() * material.getRParCom());
        double F22 = 1 / (material.getRNorTen() * material.getRNorCom());
        double F12 = f12star * Math.sqrt(F11 * F22);
        double F66 = 1 / (material.getRShear() * material.getRShear());

        int numPoints_phi = (int) (quality * 25);                        // Anzahl der Punkte in phi-Richtung (Umfangsrichtung der Ellipsen)
        int numPoints_x = 2 * numPoints_phi;           // Anzahl der Punkte in x-Richtung
        int numPoints_Limits = (int) (quality * 5);                        // Anzahl der Punkte an den Stirnseiten (Faserbruchkriterium)

        double delta_phi = 2 * Math.PI / numPoints_phi;      // Winkelabstaende
        double delta_x = (material.getRParTen() + material.getRParCom()) / numPoints_x;   // Schrittweite in X-Richtung

        int numPointsBody = (numPoints_phi + 1) * (numPoints_x + 1) * 4;       // Anzahl der Punkte für die Vernetzung
        int numPointsFace = (numPoints_phi + 1) * (numPoints_Limits + 1) * 4;  // Anzahl der Punkte für die Vernetzung
        int totalNumPoints = numPointsBody + 2 * numPointsFace;
        
        Vector3[] vertices = new Vector3[totalNumPoints];
        Vector3[] normals  = new Vector3[totalNumPoints];

        /* Aufbauen des Netzen und Setzen der Flächennormalen an jedem Punkt.
         * Die Normalen sind für die Beleuchtung wichtig. Vorder- und Rückseite
         * der Elemente wird aber über die Reihenfolge der gegebenen Punkte definiert.
         * 
         * Anmerkung Hauffe: Der Aufbau ist nicht mehr der Originale und schwerer zu verstehen.
         * Der Quellcode wurde so geändert, dass Berechnungen die innerhalb einer
         * Schleife konstant sind, außerhalb dieser Schleife berechnet wird. 
         * Der Quellcode wird dadurch unverständlicher aber bedeutend schneller.
         * Zur Beruhigung, das ganze geht noch Kryptischer, da im bisherigen Aufbau,
         * trotzdem fast jeder Punkt zweimal berechnet wird. 
         */
        int ii, jj;
        double schnitt_a = F22;
        double schnitt_b = F66;
        double x, y, z, xnor, ynor, znor;
        double schnitt_d, schnitt_m, schnitt_s1, schnitt_halb_a, schnitt_halb_b;
        Vector3 normal;

        for (ii = 0; ii < numPoints_x; ii++) {
            x = delta_x * ii - material.getRParCom();
            schnitt_d = 0.5 * (F2 + 2 * F12 * x);
            schnitt_m = F11 * x * x + F1 * x - 1;
            schnitt_s1 = -schnitt_d / schnitt_a;
            schnitt_halb_a = Math.sqrt((schnitt_d * schnitt_d - schnitt_a * schnitt_m) / (schnitt_a * schnitt_a));
            schnitt_halb_b = Math.sqrt((schnitt_d * schnitt_d - schnitt_a * schnitt_m) / (schnitt_a * schnitt_b));

            for (jj = 0; jj < numPoints_phi; jj++) {

                // Modell von "-x" nach "+x" aufbauen
                // einige Werte berechnen die innerhalb eines Schnittes konstant sind
                y = schnitt_halb_a * Math.cos(delta_phi * jj) + schnitt_s1;
                z = schnitt_halb_b * Math.sin(delta_phi * jj);
                vertices[(ii + jj * numPoints_x) * 4] = new Vector3((float) x, (float) y, (float) z);

                xnor = 2 * F11 * x + 2 * F12 * y + F1;
                ynor = 2 * F22 * y + 2 * F12 * x + F2;
                znor = 2 * F66 * z;
                normal = new Vector3((float) xnor, (float) ynor, (float) znor);
                normal.normalizeLocal();
                normals[(ii + jj * numPoints_x) * 4] = normal;

                y = schnitt_halb_a * Math.cos(delta_phi * (jj + 1)) + schnitt_s1;
                z = schnitt_halb_b * Math.sin(delta_phi * (jj + 1));
                vertices[(ii + jj * numPoints_x) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                xnor = 2 * F11 * x + 2 * F12 * y + F1;
                ynor = 2 * F22 * y + 2 * F12 * x + F2;
                znor = 2 * F66 * z;
                normal = new Vector3((float) xnor, (float) ynor, (float) znor);
                normal.normalizeLocal();
                normals[(ii + jj * numPoints_x) * 4 + 1] = normal;
            }

            x = delta_x * (ii + 1) - material.getRParCom();
            schnitt_d = 0.5 * (F2 + 2 * F12 * x);
            schnitt_m = F11 * x * x + F1 * x - 1;
            schnitt_s1 = -schnitt_d / schnitt_a;
            schnitt_halb_a = Math.sqrt((schnitt_d * schnitt_d - schnitt_a * schnitt_m) / (schnitt_a * schnitt_a));
            schnitt_halb_b = Math.sqrt((schnitt_d * schnitt_d - schnitt_a * schnitt_m) / (schnitt_a * schnitt_b));

            for (jj = 0; jj < numPoints_phi; jj++) {
                y = schnitt_halb_a * Math.cos(delta_phi * (jj + 1)) + schnitt_s1;
                z = schnitt_halb_b * Math.sin(delta_phi * (jj + 1));
                vertices[(ii + jj * numPoints_x) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                xnor = 2 * F11 * x + 2 * F12 * y + F1;
                ynor = 2 * F22 * y + 2 * F12 * x + F2;
                znor = 2 * F66 * z;
                normal = new Vector3((float) xnor, (float) ynor, (float) znor);
                normal.normalizeLocal();
                normals[(ii + jj * numPoints_x) * 4 + 2] = normal;

                y = schnitt_halb_a * Math.cos(delta_phi * (jj)) + schnitt_s1;
                z = schnitt_halb_b * Math.sin(delta_phi * (jj));
                vertices[(ii + jj * numPoints_x) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                xnor = 2 * F11 * x + 2 * F12 * y + F1;
                ynor = 2 * F22 * y + 2 * F12 * x + F2;
                znor = 2 * F66 * z;
                normal = new Vector3((float) xnor, (float) ynor, (float) znor);
                normal.normalizeLocal();
                normals[(ii + jj * numPoints_x) * 4 + 3] = normal;
            }
        }

        // Faserzugversagen
        int sInd = numPointsBody;
        double RParTen_Ellipse_y_offset = -(F2 + 2.0 * F12 * material.getRParTen()) / (2.0 * F22);
        double RParTen_Ellipse_wurzel = Math.sqrt((1 / (4.0) * (F2 + 2 * F12 * material.getRParTen()) * (F2 + 2 * F12 * material.getRParTen()) - F22 * (F11 * material.getRParTen() * material.getRParTen() + F1 * material.getRParTen() - 1)) / (F22 * F22));

        Vector3 normale = new Vector3(1.0f, 0.0f, 0.0f);
        x = material.getRParTen();
        for (jj = 0; jj < numPoints_phi; jj++) {
            for (ii = 0; ii < numPoints_Limits; ii++) {

                y = (ii) / numPoints_Limits * (RParTen_Ellipse_wurzel * Math.cos(delta_phi * jj)) + RParTen_Ellipse_y_offset;
                z = (ii) / numPoints_Limits * RParTen_Ellipse_wurzel * Math.sqrt(F22 / F66) * Math.sin(delta_phi * jj);

                vertices[sInd + (ii + jj * numPoints_Limits) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_Limits) * 4] = normale;

                y = (ii + 1) / (float) (numPoints_Limits) * (RParTen_Ellipse_wurzel * Math.cos(delta_phi * jj)) + RParTen_Ellipse_y_offset;
                z = (ii + 1) / (float) (numPoints_Limits) * RParTen_Ellipse_wurzel * Math.sqrt(F22 / F66) * Math.sin(delta_phi * jj);
                vertices[sInd + (ii + jj * numPoints_Limits) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_Limits) * 4 + 1] = normale;

                y = (ii + 1) / (float) (numPoints_Limits) * (RParTen_Ellipse_wurzel * Math.cos(delta_phi * (jj + 1))) + RParTen_Ellipse_y_offset;
                z = (ii + 1) / (float) (numPoints_Limits) * RParTen_Ellipse_wurzel * Math.sqrt(F22 / F66) * Math.sin(delta_phi * (jj + 1));
                vertices[sInd + (ii + jj * numPoints_Limits) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_Limits) * 4 + 2] = normale;

                y = (ii) / (float) (numPoints_Limits) * (RParTen_Ellipse_wurzel * Math.cos(delta_phi * (jj + 1))) + RParTen_Ellipse_y_offset;
                z = (ii) / (float) (numPoints_Limits) * RParTen_Ellipse_wurzel * Math.sqrt(F22 / F66) * Math.sin(delta_phi * (jj + 1));
                vertices[sInd + (ii + jj * numPoints_Limits) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_Limits) * 4 + 3] = normale;
            }
        }

        // Faserdruckversagen
        sInd += numPointsFace;
        double RParCom_Ellipse_y_offset = -(F2 - 2.0 * F12 * material.getRParCom()) / (2.0 * F22);
        double RParCom_Ellipse_wurzel = Math.sqrt((1 / (4.0) * (F2 - 2 * F12 * material.getRParCom()) * (F2 - 2 * F12 * material.getRParCom()) - F22 * (F11 * material.getRParCom() * material.getRParCom() - F1 * material.getRParCom() - 1)) / (F22 * F22));
        normale = new Vector3(-1.0f, 0.0f, 0.0f);
        x = -material.getRParCom();

        for (jj = 0; jj < numPoints_phi; jj++) {
            for (ii = 0; ii < numPoints_Limits; ii++) {

                y = (ii) / (float) (numPoints_Limits) * (RParCom_Ellipse_wurzel * Math.cos(delta_phi * jj)) + RParCom_Ellipse_y_offset;
                z = (ii) / (float) (numPoints_Limits) * RParCom_Ellipse_wurzel * Math.sqrt(F22 / F66) * Math.sin(delta_phi * jj);
                vertices[sInd + (ii + jj * numPoints_Limits) * 4] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_Limits) * 4] = normale;

                y = (ii) / (float) (numPoints_Limits) * (RParCom_Ellipse_wurzel * Math.cos(delta_phi * (jj + 1))) + RParCom_Ellipse_y_offset;
                z = (ii) / (float) (numPoints_Limits) * RParCom_Ellipse_wurzel * Math.sqrt(F22 / F66) * Math.sin(delta_phi * (jj + 1));
                vertices[sInd + (ii + jj * numPoints_Limits) * 4 + 1] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_Limits) * 4 + 1] = normale;

                y = (ii + 1) / (float) (numPoints_Limits) * (RParCom_Ellipse_wurzel * Math.cos(delta_phi * (jj + 1))) + RParCom_Ellipse_y_offset;
                z = (ii + 1) / (float) (numPoints_Limits) * RParCom_Ellipse_wurzel * Math.sqrt(F22 / F66) * Math.sin(delta_phi * (jj + 1));
                vertices[sInd + (ii + jj * numPoints_Limits) * 4 + 2] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_Limits) * 4 + 2] = normale;

                y = (ii + 1) / (float) (numPoints_Limits) * (RParCom_Ellipse_wurzel * Math.cos(delta_phi * jj)) + RParCom_Ellipse_y_offset;
                z = (ii + 1) / (float) (numPoints_Limits) * RParCom_Ellipse_wurzel * Math.sqrt(F22 / F66) * Math.sin(delta_phi * jj);
                vertices[sInd + (ii + jj * numPoints_Limits) * 4 + 3] = new Vector3((float) x, (float) y, (float) z);
                normals[sInd + (ii + jj * numPoints_Limits) * 4 + 3] = normale;
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
