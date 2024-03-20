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
package de.elamx.clt;

import de.elamx.laminate.DataLayer;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Layer;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.Material;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.ReserveFactor;
import de.elamx.mathtools.MatrixTools;

/**
 *
 * @author Andreas Hauffe
 */
public class CLT_Calculator {

    /**
     * Berechnet die nicht vorgegebenen Größen (Kräfte oder Verzerrungen) nach
     * der klassischen Laminattheorie. Über den Boolean-Vektor useStrain wird
     * dabei ausgesagt, ob die entsprechende Kraft (false) vorgegeben ist oder
     * die Dehnung (true).
     *
     * @param lam Laminat
     * @param loads Lasten
     * @param strains Verzerrungen
     * @param useStrain Boolean-Vektor der Länge 6
     */
    public static void determineValues(CLT_Laminate lam, Loads loads, Strains strains, boolean[] useStrain) {
        if (lam == null || loads == null || strains == null) {
            return;
        }

        double[] formom = loads.getForceMomentAsVector();
        double[] epskappa = strains.getEpsilonKappaAsVector();

        // Berechnen der rechten-Hand-Seite des Gleichungssystems
        // (je nachdem was gewählt pro Zeile Schnittlasten oder Verzerrungen, gespeichert in "werte")
        // hygrothermale Schnittlasten berechnen
        double[] tForce = getHygroThermalForces(lam, loads);

        /*
         * Aufstellen der rechten Seite des Gleichungssystems. Dabei muss das
         * vertauschen von linker und rechter Seite berücksichtigt werden.
         * Im Falle von Lasten, werden die thermischen Lasten dazuaddiert.
         */
        double[] rhs = new double[6];
        for (int i = 0; i < 6; i++) {
            if (useStrain[i]) {
                rhs[i] = epskappa[i];
            } else {
                rhs[i] = formom[i] + tForce[i];
            }
        }

        /*
         * Lösen des Gleichungssystems inklusive vertauschen der linken und
         * rechten Seite des Gleichungssystems.
         */
        double[] results = MatrixTools.solveAbWithExchange(lam.getABDMatrix(), rhs, useStrain);

        for (int i = 0; i < 6; i++) {
            if (useStrain[i]) {
                results[i] -= tForce[i];
            }
        }

        // Speichern der Ergebnisse
        if (useStrain[0]) {
            loads.setN_x(results[0]);
        } else {
            strains.setEpsilon_x(results[0]);
        }
        if (useStrain[1]) {
            loads.setN_y(results[1]);
        } else {
            strains.setEpsilon_y(results[1]);
        }
        if (useStrain[2]) {
            loads.setN_xy(results[2]);
        } else {
            strains.setGamma_xy(results[2]);
        }
        if (useStrain[3]) {
            loads.setM_x(results[3]);
        } else {
            strains.setKappa_x(results[3]);
        }
        if (useStrain[4]) {
            loads.setM_y(results[4]);
        } else {
            strains.setKappa_y(results[4]);
        }
        if (useStrain[5]) {
            loads.setM_xy(results[5]);
        } else {
            strains.setKappa_xy(results[5]);
        }

        // Setzen der hygrothermalen Kräfte
        loads.setHygrothermalForcesAsVector(tForce);
    }

    public static CLT_LayerResult[] getLayerResults(CLT_Laminate laminat, Loads load, Strains strain) {
        CLT_Layer[] clt_layers = laminat.getCLTLayers();

        CLT_LayerResult[] results = new CLT_LayerResult[clt_layers.length];

        double[] epskappa = strain.getEpsilonKappaAsVector();
        double deltaTemp = load.getDeltaT();
        double deltaHygro = load.getDeltaH();

        int ii = 0;
        for (CLT_Layer cl : clt_layers) {
            Layer l = cl.getLayer();

            StressStrainState[] sss_lower = cl.getStressState(epskappa, deltaTemp, deltaHygro, CLT_Layer.POSITION_LOWER, true);
            StressStrainState[] sss_upper = cl.getStressState(epskappa, deltaTemp, deltaHygro, CLT_Layer.POSITION_UPPER, true);

            ReserveFactor rr_lower = l.getCriterion().getReserveFactor(l.getMaterial(), l, sss_lower[0]);
            ReserveFactor rr_upper = l.getCriterion().getReserveFactor(l.getMaterial(), l, sss_upper[0]);

            boolean failed = rr_lower.getMinimalReserveFactor() < 1.0 || rr_upper.getMinimalReserveFactor() < 1.0;

            results[ii++] = new CLT_LayerResult(l, cl, sss_lower, sss_upper, rr_lower, rr_upper, failed);
        }

        return results;
    }

    public static CLT_LayerResult[] getLayerResults_radial(CLT_Laminate laminat, Loads load, Strains strain, double meanRadius) {
        CLT_Layer[] clt_layers = laminat.getCLTLayers();

        CLT_LayerResult[] results = new CLT_LayerResult[clt_layers.length];

        double[] epskappa = strain.getEpsilonKappaAsVector();
        double deltaTemp = load.getDeltaT();
        double deltaHygro = load.getDeltaH();

        int ii = 0;
        for (CLT_Layer cl : clt_layers) {
            Layer l = cl.getLayer();

            StressStrainState[] sss_lower = cl.getStressState_radial(epskappa, deltaTemp, deltaHygro, CLT_Layer.POSITION_LOWER, meanRadius, true);
            StressStrainState[] sss_upper = cl.getStressState_radial(epskappa, deltaTemp, deltaHygro, CLT_Layer.POSITION_UPPER, meanRadius, true);

            ReserveFactor rr_lower = l.getCriterion().getReserveFactor(l.getMaterial(), l, sss_lower[0]);
            ReserveFactor rr_upper = l.getCriterion().getReserveFactor(l.getMaterial(), l, sss_upper[0]);

            boolean failed = rr_lower.getMinimalReserveFactor() < 1.0 || rr_upper.getMinimalReserveFactor() < 1.0;

            results[ii++] = new CLT_LayerResult(l, cl, sss_lower, sss_upper, rr_lower, rr_upper, failed);
        }

        return results;
    }

    public static double[] getHygroThermalForces(CLT_Laminate lam, Loads loads) {
        double[] forces = new double[6];

        if (loads.getDeltaH() == 0.0 && loads.getDeltaT() == 0.0) {
            return forces;
        }

        double[] nt = new double[]{0.0, 0.0, 0.0};
        double[] mt = new double[]{0.0, 0.0, 0.0};
        for (CLT_Layer layer : lam.getCLTLayers()) {

            // Winkel
            double angle = layer.getLayer().getAngle() * Math.PI / 180.0;
            // globale Q-Matrix
            double[][] qMatrix = layer.getQMatGlobal();
            //
            Material mat = layer.getLayer().getMaterial();

            // calculate alpha_i and beta_i values
            double[] alpha_i = calc_angle_i(mat.getAlphaTPar(), mat.getAlphaTNor(), angle);
            double[] beta_i = calc_angle_i(mat.getBetaPar(), mat.getBetaNor(), angle);

            double[] hygroCoeff = calcHygroCoeff(alpha_i, beta_i, loads.getDeltaT(), loads.getDeltaH());

            double[] qalpha = MatrixTools.MatVecMult(qMatrix, hygroCoeff);

            double[] qalpha_t = MatrixTools.multiply(qalpha, layer.getLayer().getThickness());
            nt = MatrixTools.add(nt, qalpha_t);

            double[] qalpha_tz = MatrixTools.multiply(qalpha_t, layer.getZm());
            mt = MatrixTools.add(mt, qalpha_tz);
        }

        forces[0] = nt[0];
        forces[1] = nt[1];
        forces[2] = nt[2];
        forces[3] = mt[0];
        forces[4] = mt[1];
        forces[5] = mt[2];

        return forces;
    }

    /**
     * interne hilfsmethode zur berechnung von winkel alpha i
     *
     * @param angleTparallel als {@link double} value
     * @param angleTvertical als {@link double} value
     * @param angle als {@link double} value
     * @return double array with alphaX on position 0, alphaY o position 1,
     * alphaXY on position 2
     */
    private static double[] calc_angle_i(double angleTparallel, double angleTvertical, double angle) {

        double[] rs = new double[3];

        double c = Math.cos(angle);
        double c2 = c * c;

        double s = Math.sin(angle);
        double s2 = s * s;

        rs[0] = angleTparallel * c2 + angleTvertical * s2;
        rs[1] = angleTparallel * s2 + angleTvertical * c2;
        rs[2] = 2.0 * (angleTparallel - angleTvertical) * s * c;

        return rs;
    }

    /**
     * interne hilfsmethode zur Berechnung des Hygro Koffizienten
     *
     * @param alpha_i {@link double} array mi Werten von alpha i
     * @param beta_i {@link double} array mi Werten von beta i
     * @param deltaT Wert fuer delta t
     * @param deltaC Wert fuer delta c
     * @return {@link double} array mit Werten fuer hygro koeffizienten
     */
    private static double[] calcHygroCoeff(double[] alpha_i, double[] beta_i, double deltaT, double deltaC) {
        double[] rs = new double[3];

        for (int i = 0; i < 3; i++) {
            rs[i] = alpha_i[i] * deltaT + beta_i[i] * deltaC;
        }

        return rs;
    }

    public static void determineValuesLastPlyFailure(CLT_Laminate lam, Loads loads, Strains strains, boolean[] useStrain) {
         
        CLT_Layer[] origLayers = lam.getCLTLayers();

        CLT_Layer[] layers = new CLT_Layer[origLayers.length];
        
        int numLayers = origLayers.length;   // Anzahl der Lagen
        
        double[] RF_ZFB  = new double[numLayers];
        double[] RF_FB  = new double[numLayers];

        /*
        Copy all Layers to be able to change the Stiffness for final failure
         */
        for (int ii = 0; ii < numLayers; ii++) {
            Layer oldL = origLayers[ii].getLayer();
            Layer newL = new DataLayer("1", "noname", getAsDefaultMaterial(oldL.getMaterial()), oldL.getAngle(), oldL.getThickness(), oldL.getCriterion());
            layers[ii] = new CLT_Layer(newL);
            layers[ii].setZm(origLayers[ii].getZm());
            RF_ZFB[ii] = Double.NaN;
            RF_FB[ii] = Double.NaN;
        }
        
        
    }

    private static DefaultMaterial getAsDefaultMaterial(LayerMaterial material) {
        DefaultMaterial mat = new DefaultMaterial("1", "",
                material.getEpar(),
                material.getEnor(),
                material.getNue12(),
                material.getG(),
                0.0,
                false);

        mat.setRNorCom(material.getRNorCom());
        mat.setRNorTen(material.getRNorTen());
        mat.setRParCom(material.getRParCom());
        mat.setRParTen(material.getRParTen());
        mat.setRShear(material.getRShear());
        for (String key : mat.getAdditionalValueKeySet()) {
            mat.putAdditionalValue(key, mat.getAdditionalValue(key));
        }
        return mat;
    }
}
