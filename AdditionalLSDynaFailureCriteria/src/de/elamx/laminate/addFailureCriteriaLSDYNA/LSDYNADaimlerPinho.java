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

/* Die Klasse ist die Implementierung von LSDYNA Daimler Pinho in 2D
 *
 * @author Tim Dorau
 */

import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.ReserveFactor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public class LSDYNADaimlerPinho extends Criterion {
    
    public static final String ALP0 = LSDYNADaimlerPinho.class.getName() + ".alp0";
    
    public static LSDYNADaimlerPinho getDefault(FileObject obj) {
        LSDYNADaimlerPinho LSDP = new LSDYNADaimlerPinho(obj);

        return LSDP;
    }

    public LSDYNADaimlerPinho(FileObject obj) {
        super(obj);
    }
    
    
    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double[] stresses = sss.getStress();
        double[] strains = sss.getStrain();
        ReserveFactor rf = new ReserveFactor();
        
        /** Unterscheidung von insgesamt 5 Fällen: */
        
        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }
        
        /** Spannungen in der Bruchebene */
        double sig_n; double tau_T; double tau_L;
        // Definition von alpha0 bei reiner Druckbeanspruchung und Umrechnung in Rad
        double alp0 = material.getAdditionalValue(ALP0);
        double RF_F = 9999; double RF_M = 9999;
        double alpha0 = (int) Math.ceil(alp0);
        double alp0Rad = Math.toRadians(alpha0);
        double alpRad; double alpMax;
        // Berechnung der Festigkeiten der Bruchebene
        double R_L = material.getRShear();
        double R_T = material.getRNorCom() * Math.cos(alp0Rad) * (Math.sin(alp0Rad) + Math.cos(alp0Rad) / Math.tan(2 * alp0Rad));
        // Berechnung der inneren Reibungsparameter
        double eta_T = -1 / Math.tan(2 * alp0Rad);
        double eta_L = R_L * eta_T / R_T;

        // Berechnung Verdrehwinkel (Summe von anfänglicher Fehlausrichtung + Rotation aus der Belastung)
        double theta_C_zaehler = 1 - Math.sqrt(1 - 4 * (R_L / material.getRParCom() + eta_L) * (R_L / material.getRParCom()));
        double theta_C_nenner = 2 * (R_L / material.getRParCom() + eta_L);
        double theta_C = Math.atan(theta_C_zaehler / theta_C_nenner);   
        double theta_i = theta_C - theta_C  * material.getRParCom()/ material.getG();
        double gamma_i = (theta_i * material.getG() + Math.abs(stresses[2])) / (material.getG() + stresses[0] - stresses[1]) - theta_i;
        // Das tau/|tau| in der Theorie dient nur dem Vorzeichen, bei tau=0 ist signum +  bzw -0 => daraus folgt ein Ergebnis, das nicht mit LaRC übereinstimm
        // Der Fall tau = 0 muss demzufolge extra behandelt werden 
        //double theta = (theta_i + gamma_i);
        double theta;
        if (stresses[2] != 0) {
            theta = Math.signum(stresses[2]) * (theta_i + gamma_i);
        } else {
            theta = (theta_i + gamma_i);
        }

        // Spannungen im Knickband, um den Winkel theta verdreht        stresses[2] / Math.abs(stresses[2]) *
        double sig_1m = (stresses[0] + stresses[1]) / 2 + (stresses[0] + stresses[1]) / 2  * Math.cos(2 * theta)  + stresses[2] * Math.sin(2 * theta);
        double sig_2m = stresses[0] + stresses[1] - sig_1m;
        double tau_12m = - (stresses[0] - stresses[1]) / 2 * Math.sin(2 * theta) + stresses[2] * Math.cos(2 * theta);
        

        if (stresses[0] >= 0.0) {
            // Faserzugversagen 
            RF_F = material.getRParTen() / stresses[0]; 
            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FiberFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        } else {
            // Faserdruckversagen
            for (int alpha = 0; alpha < 180; alpha++) {
                alpRad = Math.toRadians(alpha);
                sig_n = sig_2m / 2 + sig_2m / 2 * Math.cos(2 * alpRad);
                tau_T = sig_2m / 2  + sig_2m / 2 * Math.sin(2 * alpRad);
                tau_L = tau_12m * Math.cos(alpRad);
                if (sig_n > 0) {
                    double f_1 = (sig_n / material.getRNorTen()) * (sig_n / material.getRNorTen()) + (tau_T / R_T) * (tau_T / R_T) + (tau_L / R_L) * (tau_L / R_L);
                    double R_F1 = Math.sqrt(1 / f_1);
                    if(R_F1 < RF_F) {
                        RF_F = R_F1;
                    }
                } else {
                    double f_2 = (tau_T / (R_T - eta_T * sig_n)) * (tau_T / (R_T - eta_T * sig_n)) + (tau_L / (R_L - eta_L * sig_n)) * (tau_L / (R_L - eta_L * sig_n));
                    double R_F2 = Math.sqrt(1 / f_2);
                    if (R_F2 < RF_F) {
                        RF_F = R_F2;
                    }
                }
            }
            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FiberFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }
       
        /* Zwischenfaserbruchberechnung -> Suche nach der entscheidenen Bruchebene im Intervall [0,PI[; alle ganzzahligen Winkel*/
        double RF_min_f1 = 9999; double RF_min_f2 = 9999;
        for (int alpha = 0; alpha < 180; alpha++) {
            // Berechnung der Spannungen in der Bruchebene für das aktuelle Alpha
            alpRad = Math.toRadians(alpha);
            sig_n = stresses[1] / 2 + stresses[1] / 2 * Math.cos(2 * alpRad);
            tau_T = -stresses[1] / 2 * Math.sin(2 * alpRad);
            tau_L = stresses[2] * Math.cos(alpRad);
            if (sig_n >= 0) {
                // Matrixzugversagen
                double f1 = (sig_n / material.getRNorTen()) * (sig_n / material.getRNorTen()) + (tau_T / R_T) * (tau_T / R_T) + (tau_L / R_L) * (tau_L / R_L);
                RF_min_f1 = 1 / Math.sqrt(f1);
            } else {
                // Matrixdruckversagen
                double f2 = (tau_T / (R_T - eta_T * sig_n)) * (tau_T / (R_T - eta_T * sig_n)) + (tau_L / (R_L - eta_L * sig_n)) * (tau_L / (R_L - eta_L * sig_n));
                RF_min_f2 = 1 / Math.sqrt(f2);
            }
            if (RF_min_f1 < RF_M & RF_min_f1 < RF_F) {
                RF_M = RF_min_f1;
                rf.setMinimalReserveFactor(RF_M );
                rf.setFailureName("MatrixFailureTension");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
            if (RF_min_f2 < RF_M & RF_min_f2 < RF_F) {
                RF_M = RF_min_f2;
                rf.setMinimalReserveFactor(RF_M);
                rf.setFailureName("MatrixFailureCompression");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        } 
        rf.setFailureName(NbBundle.getMessage(LSDYNADaimlerPinho.class, "LSDYNADaimlerPinho." + rf.getFailureName()));
        return rf;
    }
}

