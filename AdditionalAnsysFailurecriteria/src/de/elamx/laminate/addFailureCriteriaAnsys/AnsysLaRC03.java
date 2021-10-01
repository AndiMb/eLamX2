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
package de.elamx.laminate.addFailureCriteriaAnsys;

import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.ReserveFactor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Diese Klasse ist die Implementierung des LaRC03-Kriteriums.
 *
 * @author Tim Dorau
 */
public class AnsysLaRC03 extends Criterion {
    
    public static final String G1C = AnsysLaRC03.class.getName() + ".g1c";
    public static final String G2C = AnsysLaRC03.class.getName() + ".g2c";
    public static final String ALP0 = AnsysLaRC03.class.getName() + ".alp0";
    
    public static AnsysLaRC03 getDefault(FileObject obj) {
        AnsysLaRC03 hf = new AnsysLaRC03(obj);

        return hf;
    }

    public AnsysLaRC03(FileObject obj) {
        super(obj);
    }
    
    
    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double[] stresses = sss.getStress();
        double[] strains = sss.getStrain();
        ReserveFactor rf = new ReserveFactor();
         
        /** Unterscheidung von insgesamt 6 Fällen: */
        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }
        
        // Winkel der Bruchebene bei reiner Druchbelastung und Transformation in Radian + Runden auf nächsten ganzzahligen Wert
        double alp0 = material.getAdditionalValue(ALP0);
        double alpha0 = (int) Math.ceil(alp0);
        double alp0Rad = Math.toRadians(alpha0);
        double alpRad; 
        double alpMax = 0;
        
        // kritische Energiefreisetzungsraten für kritischen Rissfortschritt
        double g1c = material.getAdditionalValue(G1C);
        double g2c = material.getAdditionalValue(G2C);
        
        
        // Reservefaktoren für Faser und Matrix
        double RF_F; double RF_M;

        // Festigkeiten der Bruchebene R_L und R_T bzw. entsprechende in-situ Festigkeit R_Lis und R_2is für eingebettet Schichten, g Quotion krit. Energiefreisetzungsraten
        double R_L = material.getRShear();
        double R_T = material.getRNorCom() * Math.cos(alp0Rad) * (Math.sin(alp0Rad) + Math.cos(alp0Rad) / Math.tan(2 * alp0Rad));
        double R_2is; double R_Lis; double g;
        
        // Parameter für die Berechnung der in-situ Festigkeiten
        double lam22 = 2 * ( 1 / material.getEnor() - (material.getNue21() * material.getNue21()) / material.getEpar());
        double lam44 = 1 / material.getG();
        
        // effektiv wirkende Spannungen in der Bruchebene
        double tau_TeffMax = 0; double tau_LeffMax = 0; double tau_TeffMax_m = 0; double tau_LeffMax_m = 0;
        double tau_Teff; double tau_Leff; double tau_Teff_m; double tau_Leff_m;
        double fMax = 0; double f_1; double fMax_m = 0;
        
        // Berechnung der inneren Reibungsparameter
        double eta_T = -1 / Math.tan(2 * alp0Rad);
        double eta_L = - (R_L * Math.cos(2 * alp0Rad)) / (material.getRNorCom() * Math.cos(alp0Rad) * Math.cos(alp0Rad));
        
        /* Unterscheidung der in-situ Festigkeiten in dünne und dicke Schichten 
        Ab hier stehen R_Lis und R_2is stellvertretend für die Festigkeiten der Bruchebene, auch wenn die Schicht nicht eingebettet ist */
        if (l != null && l.isEmbedded()) {
            // Schicht ist eingebettet -> in-situ Festigkeiten benutzen
            if (l.getThickness() >= 0.7) {
                // dicke eingebette Schicht z.B. wenn mehrere Einzelschichten zu einer dickeren Schicht zusammengefasst werden
                R_2is = 1.12 * Math.sqrt(2) * material.getRNorTen();
                R_Lis = Math.sqrt(2) * R_L;
                g = 1.12 * 1.12 * lam22 / lam44 * (material.getRNorTen() / R_L) * (material.getRNorTen() / R_L);
            } else {
                // dünne eingebette Schicht
                R_2is = Math.sqrt((8 * g1c )/ (Math.PI * l.getThickness() *  lam22));
                R_Lis = Math.sqrt((8 * g2c )/ (Math.PI * l.getThickness() *  lam44));
                g = g1c / g2c;
            }
        } else {
            // Schicht ist nicht eingebettet -> R_T und R_L werden weiter verwendet
            R_2is = material.getRNorTen();
            R_Lis = R_L;
            g = 1.12 * 1.12 * lam22 / lam44 * (material.getRNorTen() / R_L) * (material.getRNorTen() / R_L);
        }
        
        /*Spannungen im Knickband unter Winkel phi (rad): sig1m, sig2m, tau12m; phi_C Verdrehwinkel für reine Druckbeanspruchung in rad*/
        //double phi_C = Math.atan((1 - Math.sqrt(1 - 4 * (R_Lis / material.getRParCom() + eta_L) * (R_Lis / material.getRParCom()))) / (2 * (R_Lis / material.getRParCom() + eta_L)));
        double phi_C_zaehler = 1 - Math.sqrt(1 - 4 * (R_Lis / material.getRParCom() + eta_L) * (R_Lis / material.getRParCom()));
        double phi_C_nenner = 2 * (R_Lis / material.getRParCom() + eta_L);
        double phi_C = Math.atan(phi_C_zaehler / phi_C_nenner);
        double phi = (Math.abs(stresses[2]) + (material.getG() - material.getRParCom()) * phi_C) / (material.getG() + stresses[0] - stresses[1]);
        double sig_1m = Math.cos(phi) * Math.cos(phi) * stresses[0]  + Math.sin(phi) * Math.sin(phi) * stresses[1]; 
        sig_1m += 2* Math.sin(phi) * Math.cos(phi) * stresses[2];
        double sig_2m = Math.sin(phi) * Math.sin(phi) * stresses[0]  + Math.cos(phi) * Math.cos(phi) * stresses[1];
        sig_2m -= 2* Math.sin(phi) * Math.cos(phi) * stresses[2];
        double tau_12m = - Math.sin(phi) * Math.cos(phi) * stresses[0] + Math.sin(phi) * Math.cos(phi) * stresses[1];
        tau_12m += (Math.cos(phi) * Math.cos(phi) - Math.sin(phi) * Math.sin(phi)) * stresses[2];
        
        // Faserbruchkriterium
        if (stresses[0] >= 0.0) {
            // Faserzugversagen LarC # 3
            RF_F = material.getRParTen() /  (material.getEpar() * Math.abs(strains[0]));
            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FiberFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        } else {
            // Faserdruckversagen
            if (sig_2m >= 0) {
                // Faserdruckversagen LaRC03 #5
                double Q_5 = g * (sig_2m / R_2is) * (sig_2m / R_2is) + (tau_12m / R_Lis) * (tau_12m / R_Lis);
                double L_5 = (1 - g) * sig_2m / R_2is;
                RF_F = (Math.sqrt(L_5 * L_5 + 4.0 * Q_5) - L_5) / (2.0 * Q_5);
            } else {
                // Faserdruckversagen LaRC03 #4
                double f_4 = (Math.abs(tau_12m) + eta_L * sig_2m) / R_Lis;
                if (f_4 > 0) {
                    RF_F  = 1 / f_4;
                } else {
                    f_4 = 0;
                    RF_F = 9999;
                }
            }
            rf.setMinimalReserveFactor(RF_F);
            rf.setFailureName("FiberFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }
        
        if (stresses[1] >= 0.0) {
            //2. Zwischenfaserbruch durch Zug quer zur Faserrichtung ( sig_2 >=0) LaRC03 #2:
            double Q_2 = g * (stresses[1] / R_2is) * (stresses[1] / R_2is) + (stresses[2] / R_Lis) * (stresses[2] / R_Lis);
            double L_2 = (1 - g) * stresses[1] / R_2is;
            RF_M = (Math.sqrt(L_2 * L_2 + 4.0 * Q_2) - L_2) / (2.0 * Q_2);
            if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M );
                rf.setFailureName("MatrixFailureTension");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        } else {
            // Berechnung Reservefaktor für Zwischenfaserbruch Druck LaRC03 #1
            if (stresses[0] >= -material.getRNorCom()) {
                // Suche nach der bestimmenden Bruchebene im Intervall 0 <= alpha <= alpha0 für LaRC03#1
                for (int alpha = 0; alpha <= alpha0; alpha++) {
                    // Berechnung der Spannungen in der Bruchebene für das aktuelle Alpha
                    alpRad = Math.toRadians(alpha);
                    tau_Teff  = - stresses[1] * Math.cos(alpRad) * (Math.sin(alpRad) - eta_T * Math.cos(alpRad));
                    if (tau_Teff <= 0) {
                        tau_Teff = 0;
                    }
                    tau_Leff = Math.cos(alpRad) * (Math.abs(stresses[2]) + eta_L * stresses[1] * Math.cos(alpRad));
                    if (tau_Leff <= 0) {
                        tau_Leff = 0;
                    }
                    f_1 = (tau_Teff / R_T) * (tau_Teff / R_T) + (tau_Leff / R_Lis) * (tau_Leff / R_Lis);

                    // Bestimmung maximaler Failure Index fMax
                    if (f_1 >= fMax) {
                        fMax = f_1;
                        alpMax = alpha;
                        tau_TeffMax = tau_Teff;
                        tau_LeffMax = tau_Leff;
                    }
                }
                double Q_1 = (tau_TeffMax / R_T) * (tau_TeffMax / R_T) + (tau_LeffMax / R_Lis)  *  (tau_LeffMax / R_Lis);
                RF_M  = 1 / Math.sqrt(Q_1);
                if (RF_M < RF_F) {
                rf.setMinimalReserveFactor(RF_M );
                rf.setFailureName("MatrixFailureCompression");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
                }
            } else {
                /* Berechnung Reservefaktor für Zwischenfaserbruch Druck LaRC03 #6 
                Suche der entscheidenen Bruchebene in Intervall 0<= alpha <= alpha0*/
                fMax_m = 0;
                for (int alpha = 0; alpha <= alpha0; alpha++) {
                    // Berechnung der Spannungen in der Bruchebene für das aktuelle Alpha
                    alpRad = Math.toRadians(alpha);
                    tau_Teff_m  = - sig_2m * Math.cos(alpRad) * (Math.sin(alpRad) - eta_T * Math.cos(alpRad));
                    if (tau_Teff_m <= 0) {
                        tau_Teff_m = 0;
                    }
                    tau_Leff_m = Math.cos(alpRad) * (Math.abs(tau_12m) + eta_L * sig_2m * Math.cos(alpRad));
                    if (tau_Leff_m <= 0) {
                        tau_Leff_m = 0;
                    }
                    f_1 = (tau_Teff_m / R_T) * (tau_Teff_m / R_T) + (tau_Leff_m / R_Lis) * (tau_Leff_m / R_Lis);
                    // Bestimmung maximaler Failure Index fMax
                    if (f_1 >= fMax_m) {
                        fMax_m = f_1;
                        alpMax = alpha;
                        tau_TeffMax_m = tau_Teff_m;
                        tau_LeffMax_m = tau_Leff_m;
                    }
                }
                double Q_6 = (tau_TeffMax_m / R_T) * (tau_TeffMax_m / R_T) + (tau_LeffMax_m / R_Lis)  *  (tau_LeffMax_m / R_Lis);
                RF_M  = 1 / Math.sqrt(Q_6);
            }
            if (RF_M  < RF_F) {
            rf.setMinimalReserveFactor(RF_M);
            rf.setFailureName("MatrixFailureCompression");
            rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }
        }
        
        rf.setFailureName(NbBundle.getMessage(AnsysLaRC03.class, "AnsysLaRC03." + rf.getFailureName()));
        
        System.out.println("RF = " + rf.getMinimalReserveFactor());
        
        return rf;
    }
}
