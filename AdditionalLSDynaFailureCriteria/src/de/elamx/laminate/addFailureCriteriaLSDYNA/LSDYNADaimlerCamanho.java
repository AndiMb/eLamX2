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
package de.elamx.laminate.addFailureCriteriaLSDYNA;

/* Die Klasse ist die Implementierung des Kriteriums aus dem Schadensmodell DaimlerCamaho LSDYNA.
 *
 * @author Tim Dorau
 */

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
 * Diese Klasse ist die Implementierung des LSDYNADaimlerCamanho-Kriteriums.
 *
 * @author Tim Dorau
 */
public class LSDYNADaimlerCamanho extends Criterion {
    
    public static final String G1C = LSDYNADaimlerCamanho.class.getName() + ".g1c";
    public static final String G2C = LSDYNADaimlerCamanho.class.getName() + ".g2c";
    
    public static LSDYNADaimlerCamanho getDefault(FileObject obj) {
        LSDYNADaimlerCamanho hf = new LSDYNADaimlerCamanho(obj);

        return hf;
    }

    public LSDYNADaimlerCamanho(FileObject obj) {
        super(obj);
    }
    
    
    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double[] stresses = sss.getStress();
        double[] strains = sss.getStrain();
        ReserveFactor rf = new ReserveFactor();
        
        /** Unterscheidung von insgesamt 4 Fällen: */
        
        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }
        
        /** Berechnung der Spannungen in der Brucheben
         g1c g2c kritische Energiefreisetzungsraten
         alpha0 Winkel der Bruchebene bei reiner Druckbeanspruchung 53° in rad
         */

        double g1c = material.getAdditionalValue(G1C);
        double g2c = material.getAdditionalValue(G2C);
        double RF_F; double RF_M;
        double alpha0 = Math.toRadians(53);
        
        /* Berechnung der Festigkeiten der Bruchebene R_T, R_L */
        double R_L = material.getRShear();
        double R_T = material.getRNorCom() * Math.cos(alpha0) * (Math.sin(alpha0) + Math.cos(alpha0) / Math.tan(2 * alpha0));
        
        /* innere Reibungsparameter */
        double eta_T = -1 / Math.tan(2 * alpha0);
        double eta_L = - (R_L * Math.cos(2 * alpha0)) / (material.getRNorCom() * Math.cos(alpha0) * Math.cos(alpha0));
        
        /*Spannungen im Knickband unter Winkel phi_C (rad): sig1m, sig2m, tau12m */
        double phi_C_zaehler = 1 - Math.sqrt(1 - 4 * (R_L / material.getRParCom() + eta_L) * (R_L / material.getRParCom()));
        double phi_C_nenner = 2 * (R_L / material.getRParCom() + eta_L);
        double phi_C = Math.atan(phi_C_zaehler / phi_C_nenner);
        double sig_2m = Math.sin(phi_C) * Math.sin(phi_C) * stresses[0]  + Math.cos(phi_C) * Math.cos(phi_C) * stresses[1];
        sig_2m -= 2* Math.sin(phi_C) * Math.cos(phi_C) * Math.abs(stresses[2]);
        double tau_12m = (stresses[1] - stresses[0]) * Math.sin(phi_C) * Math.cos(phi_C) + Math.abs(stresses[2]) * (Math.cos(phi_C) * Math.cos(phi_C) - Math.sin(phi_C) * Math.sin(phi_C));
        
        
        if (stresses[0] > 0.0) {
            // Faserzugversagen mit alpha0 = 0°
            RF_F = (material.getRParTen() / Math.abs(material.getEpar()) / strains[0]); 
            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FiberFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        } else {
            // Faserdruckversagen mit alpha0 = 53°
            if (Math.abs(tau_12m) + eta_L * sig_2m > 0) {
                RF_F = R_L / (Math.abs(tau_12m) + eta_L * sig_2m);
            }
            else {
                RF_F = 9999;
            }
            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FiberFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }
        
        if (stresses[1] >= 0.0) {
            // Matrixzugbruch mit alpha0 = 0°
            double g = g1c / g2c;
            double Q_1 = g * (stresses[1] / material.getRNorTen()) * (stresses[1] / material.getRNorTen()) + (stresses[2] / R_L) * (stresses[2] / R_L);
            double L_1 = (1 - g) * stresses[1] / material.getRNorTen();
            RF_M = (Math.sqrt(L_1 * L_1 + 4.0 * Q_1) - L_1) / (2.0 * Q_1);
            if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M );
                rf.setFailureName("MatrixFailureTension");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        } else {
            // Matrixdruckbruch Fall 1: alpha = 0°
            if (Math.abs(stresses[2]) + eta_L * stresses[1] > 0) {
                RF_M = R_L / (Math.abs(stresses[2]) + eta_L * stresses[1]);
            }
            else {
                RF_M = 9999;
            }
            // Matrixdruckbruch Fall 2 alpha = 53°:
            double theta = Math.atan(- Math.abs(stresses[2]) / (stresses[1] * Math.sin(alpha0)));
            double tau_Teff = - stresses[1] * Math.cos(alpha0) * (Math.sin(alpha0) - eta_T * Math.cos(alpha0) * Math.cos(theta));
            if (tau_Teff <= 0) {
                tau_Teff = 0;
            }
            double tau_Leff = Math.cos(alpha0) * (Math.abs(stresses[2] + eta_L * stresses[1] * Math.cos(alpha0) * Math.sin(theta)));
            if (tau_Leff <= 0) {
                tau_Leff = 0;
            }
            double Q_2 = tau_Teff / R_T * tau_Teff / R_T + tau_Leff / R_L *  tau_Leff / R_L;
            double RF_M2 = 1 / Math.sqrt(Q_2);
            if (RF_M2 <= RF_M) {
                RF_M = RF_M2;
            }
            if (RF_M  < RF_F) {
            rf.setMinimalReserveFactor(RF_M);
            rf.setFailureName("MatrixFailureCompression");
            rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        }
        
        rf.setFailureName(NbBundle.getMessage(LSDYNADaimlerCamanho.class, "LSDYNADaimlerCamanho." + rf.getFailureName()));
        return rf;
    }

    /* Die Stirnflächen fehlen noch, bis jetzt nur gerade Fläche maximales Spannungskriterium */
    @Override
    public Mesh getAsMesh(Material material, double quality) {
        
        // zusätzliche Materialparameter (kritsiche Energiefreisetzungsraten)
        double g1c = material.getAdditionalValue(G1C);
        double g2c = material.getAdditionalValue(G2C);
        double alpha0 = Math.toRadians(53);
        double g = g1c / g2c;
        
        // Berechnung der Festigkeiten der Bruchebene R_T, R_L 
        double R_L = material.getRShear();
        double R_T = material.getRNorCom() * Math.cos(alpha0) * (Math.sin(alpha0) + Math.cos(alpha0) / Math.tan(2 * alpha0));
        
        /* innere Reibungsparameter */
        double eta_T = -1 / Math.tan(2 * alpha0);
        double eta_L = - (R_L * Math.cos(2 * alpha0)) / (material.getRNorCom() * Math.cos(alpha0) * Math.cos(alpha0));
        
        // Anzahl der Punkte in x- bzw. y-Richtung (entspricht sig_p und sig_s-Richtung) bzw. theta als Umlaufwinkel, y1 sig-2 Druckseite
        int Points_x = (int) (quality * 20);                             
        int Points_y1 = (int) (quality * 15);
        int Points_theta = (int) (quality * 14);
        
        // Materialparameter
        double R_pz = material.getRParTen();
        double R_pd = material.getRParCom();
        double R_sz = material.getRNorTen();
        double R_sd = material.getRNorCom();
        double R_sp = material.getRShear();

        /* Der Versagenskörper besitzt eine elliptische Fläche in der tau-sig_2-Ebene in positiver sigma_2 Richtung 
           mit einer Verschiebung des Ellipsenmittelpunktes. Für negatives sigma_2 setzt sich der Körper aus einer
           Ebene und bei hohen Druck quer zur Faserrichtung aus einem ellipsenförmingen Teil zusammen.
           Für positive sigma_1 ist der Körper durhc eine senkrechte Ebene begrenzt (maximals Dehnungskriterium).
           Für negative sigma_1 entspricht das Faserdruckversagen einer schräg im Raum verlaufenden Ebene.
           Die Ellipse besitzt keine Abhängigkeit von der x-Koordinate und ist somit über die gesamte Länge konstant*/
        
        //Ellipsengeometriedefiniton für negative sigma_2:
        // Quadrate der Halbachsen aus der Transformation des Matrixkriteriums auf die Ellipsennormalform durch quadratische Ergänzung
        double a2 = R_sz * R_sz / g * (1+ (1 - g) * (1 - g) /4 /g);
        double b2 = R_L * R_L * (1 + (1 - g) *(1 - g)/4 / g);
        double a = Math.sqrt(a2);
        double b = Math.sqrt(b2);
        // Koordinaten des Ellipsenmittelpunkts
        double z_MP = 0;
        double y_MP = -(1 - g) / 2 / g * R_sz;
        
        // Schrittweiten in x-und y Richtung
        double DELTA_X = (R_pz + R_pd) / (Points_x - 1);         
        double DELTA_Y1 = (R_sd) / (Points_y1 - 1);
        double DELTA_THETA_1 = Math.PI /2 / (Points_theta-1);
       // z-Coord1 für die obere Hälfte der Ellipse, z-Coord2 für die untere Hälfte
        double[] yCoord1 = new double[Points_theta];
        double[] zCoord1 = new double[Points_theta];
        double[] zCoord2 = new double[Points_theta];

        // Berechnung der Ellipsen der beiden Ellipsenhälften auf positiver sigma_2 Seite
        for (int j = 0; j < Points_theta; j++) {      
            yCoord1[j] = a * b/(Math.sqrt(b2 + a2 * Math.tan(j * DELTA_THETA_1) * Math.tan(j * DELTA_THETA_1))) + y_MP;
            zCoord1[j] = Math.sqrt((1 - (yCoord1[j] - y_MP) * (yCoord1[j] - y_MP) / a2)* b2);
            zCoord2[j] = -Math.sqrt((1 - (yCoord1[j] - y_MP) * (yCoord1[j] - y_MP) / a2)* b2);
            // Die Ellipse darf nicht in die negative sigma_2 Richtung, hinzufügen von Dummypunkten
            if (yCoord1[j] < 0) {
                yCoord1[j] = 0;
                zCoord1[j] = Math.sqrt((1 - (yCoord1[j] - y_MP) * (yCoord1[j] - y_MP) / a2)* b2);
                zCoord2[j] = -Math.sqrt((1 - (yCoord1[j] - y_MP) * (yCoord1[j] - y_MP) / a2)* b2);
            }
        }
        
        // Berechnung der Ebene bei geringem bis modertem Druck quer zur Faserrichtung
        double[] yCoord2 = new double[Points_y1];
        double[] zCoord3 = new double[Points_y1];
        double[] zCoord4 = new double[Points_y1];
        
        for (int j = 0; j < Points_y1; j++) {      
            yCoord2[j] = -j * DELTA_Y1;
            zCoord3[j] = R_L - eta_L * yCoord2[j];
        }
        
        
        for (int j = 0; j < Points_y1; j++) {
            double fAkt = 99;
            double tauAkt = 9999;
            for (double tau_12 = 0; tau_12 <= (1.5 * R_sp); tau_12 += 0.1) {
                double alpRad = Math.toRadians(53);
                double tau_Teff  = -yCoord2[j] * Math.cos(alpRad) * (Math.sin(alpRad) - eta_T * Math.cos(alpRad));
                if (tau_Teff <= 0) {
                    tau_Teff = 0;
                }
                double tau_Leff = Math.cos(alpRad) * (Math.abs(tau_12) + eta_L * yCoord2[j] * Math.cos(alpRad));
                if (tau_Leff <= 0) {
                    tau_Leff = 0;
                }
                double f_1 = (tau_Teff / R_T) * (tau_Teff / R_T) + (tau_Leff / R_L) * (tau_Leff / R_L);
                // Suche des tau_12 was jeweils am nächsten am Versagensindex 1 liegt
                if (Math.abs(1 - f_1) <= Math.abs(1 - fAkt)) {
                    fAkt = f_1;
                    tauAkt = tau_12;
                }
            }
            if (tauAkt <= zCoord3[j]){
                zCoord3[j] = tauAkt;
            }
        }
        zCoord3[Points_y1 - 1] = 0;
        
        // sig-2 Druckbereich im negativen
        for (int i=0; i<zCoord3.length; i++) {
            zCoord4[i] = zCoord3[i] * -1;
        }
   
        
        // Punkteanzahl des Körpers = Ellipse Zug + Stirnfläche Zug Ellipse + 
        int numPoints = 4 * Points_theta * 2 * Points_x + Points_theta * 4 * 4 + Points_y1 * 4 * Points_x * 2+ 4 * Points_theta * 2;
        numPoints += 4 * Points_y1 * 2 * 2;

        // Aufbau der Mantelfläche
        Vector3[] vertices = new Vector3[numPoints];
        Vector3[] normals  = new Vector3[numPoints];
        Vector3 normal;
        
        int index = 0;
        // obere Ellipse
        // Berechnung der Halbellipse im positiven sigma_2 Bereich  
        // Reihenfolge der Punkte beachten!
        for (int i = 0; i < (Points_x - 1); i++) {
            for (int j = 0; j < (zCoord1.length-1); j++) {
                // 4. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord1[j + 1]), (float) (zCoord1[j + 1]));
                index++;
                // 3. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord1[j + 1]), (float) (zCoord1[j + 1]));
                index++;
                // 2. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord1[j]), (float) (zCoord1[j]));
                index++;
                // 1. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord1[j]), (float) (zCoord1[j]));
                index++;
            }
        }
        // untere Ellipse
        for (int i = 0; i < (Points_x - 1); i++) {
            for (int j = 0; j < (zCoord2.length-1); j++) {
                // 4. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord1[j + 1]), (float) (zCoord2[j + 1]));
                index++;
                // 3. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord1[j + 1]), (float) (zCoord2[j + 1]));
                index++;
                // 2. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord1[j]), (float) (zCoord2[j]));
                index++;
                // 1. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord1[j]), (float) (zCoord2[j]));
                index++;
            }
        }
        // Ebene im negativen sigma_2-Bereich bei positivem tau_12
        for (int i = 0; i < (Points_x - 1); i++) {
            for (int j = 0; j < (zCoord3.length-1); j++) {
                // 4. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord2[j + 1]), (float) (zCoord3[j + 1]));
                index++;
                // 3. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord2[j + 1]), (float) (zCoord3[j + 1]));
                index++;
                // 2. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord2[j]), (float) (zCoord3[j]));
                index++;
                // 1. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord2[j]), (float) (zCoord3[j]));
                index++;
            }
        }
        // Ebene im negativen sigma_2-Bereich bei negativen tau_12
        for (int i = 0; i < (Points_x - 1); i++) {
            for (int j = 0; j < (zCoord4.length-1); j++) {
                // 4. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord2[j + 1]), (float) (zCoord4[j + 1]));
                index++;
                // 3. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord2[j + 1]), (float) (zCoord4[j + 1]));
                index++;
                // 2. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + (i + 1) * DELTA_X), (float) (yCoord2[j]), (float) (zCoord4[j]));
                index++;
                // 1. Punkt des Quads
                vertices[index] = new Vector3((float) (-R_pd + i * DELTA_X), (float) (yCoord2[j]), (float) (zCoord4[j]));
                index++;
            }
        }

        // Stirnfläche auf der positiven sigma_2 Seite
        // Zugseite obere Hälfte
        for (int i = 0; i < zCoord1.length-1; i++) {
            // 1. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (0), (float) (0));
            index++;
            // 2. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord1[i]), (float) (zCoord1[i]));
            index++;
             // 3. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord1[i + 1]), (float) (zCoord1[i + 1]));
            index++;
            // 4. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (0), (float) (0));
            index++;
        }
        // Zugseite untere Hälfte
        for (int i = 0; i < zCoord1.length-1; i++) {
            // 1. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (0), (float) (0));
            index++;
            // 2. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord1[i]), (float) (zCoord2[i]));
            index++;
             // 3. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord1[i + 1]), (float) (zCoord2[i + 1]));
            index++;
            // 4. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (0), (float) (0));
            index++;
        }
      
        // Stirnfläche auf der negative sigma_2 Seite mit den Koordinaten der Bruchebenensuche
        // Zugseite untere Hälfte #2
        for (int i = 0; i < zCoord4.length-1; i++) {
            // 1. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (0), (float) (0));
            index++;
            // 2. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord2[i]), (float) (zCoord4[i]));
            index++;
             // 3. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord2[i + 1]), (float) (zCoord4[i + 1]));
            index++;
            // 4. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (0), (float) (0));
            index++;
        }
        
        // Zugseite obere Hälfte #2
        for (int i = 0; i < zCoord3.length-1; i++) {
            // 1. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (0), (float) (0));
            index++;
            // 2. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord2[i]), (float) (zCoord3[i]));
            index++;
             // 3. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (yCoord2[i + 1]), (float) (zCoord3[i + 1]));
            index++;
            // 4. Punkt des Quads
            vertices[index] = new Vector3((float) (R_pz), (float) (0), (float) (0));
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

