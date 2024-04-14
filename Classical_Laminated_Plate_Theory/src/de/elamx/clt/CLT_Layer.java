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

import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import de.elamx.laminate.StressStrainState;
import de.elamx.mathtools.MatrixTools;

/**
 *
 * @author Andreas Hauffe
 */
public class CLT_Layer extends CLT_Object {

    public static final int POSITION_UPPER = 0;
    public static final int POSITION_LOWER = 1;
    public static final int POSITION_MIDDLE = 2;

    private final Layer layer;

    private double[][] Qlok_ = null;                // Lokale reduzierte Steifigkeitsmatrix

    private double zm = 0.0;

    @SuppressWarnings("this-escape")
    public CLT_Layer(Layer layer) {
        this.layer = layer;
        refresh();
        this.layer.getLookup().add(this);
    }

    /**
     * Alle notwendigen Daten der Schicht werden neu berechnet. Bisher wird nur
     * die lokale Q-Matrix neu bestimmt.
     */
    @Override
    public void refresh() {
        calcQlocal();
    }

    public double getZm() {
        return zm;
    }

    public void setZm(double zm) {
        this.zm = zm;
    }

    public Layer getLayer() {
        return layer;
    }

    private void calcQlocal() {
        Material material = layer.getMaterial();
        double nu21 = material.getNue21();

        double temp = 1.0 / (1.0 - material.getNue12() * nu21);

        Qlok_ = new double[3][3];

        Qlok_[0][0] = temp * material.getEpar();
        Qlok_[0][1] = temp * material.getEpar() * nu21;
        Qlok_[0][2] = 0.0;
        Qlok_[1][0] = Qlok_[0][1];
        Qlok_[1][1] = temp * material.getEnor();
        Qlok_[1][2] = 0.0;
        Qlok_[2][0] = 0.0;
        Qlok_[2][1] = 0.0;
        Qlok_[2][2] = material.getG();
    }

    /**
     * Gibt die Q-Matrix im lokalen bzw. Schichtkoordinatensystem zurück.
     *
     * @return Q-Matrix im lokalen Koordinatensystem.
     */
    public double[][] getQMatLocal() {
        return Qlok_;
    }

    /**
     * Gibt die Q-Matrix im globalen Koordinatensystem, also gedreht um den
     * Schichtwinkel, zurück.
     *
     * @return Q-Matrix im globalen System
     */
    public double[][] getQMatGlobal() {
        return getQMatGlobal(layer.getAngle());
    }

    /**
     * Gibt die Q-Matrix im globalen Koordinatensystem, also gedreht um den
     * übergegeben Winkel, zurück.
     *
     * @param angle Winkel, um den das globale Koordinatensystem gegenüber dem
     * Lokalen gedreht ist.
     * @return Q-Matrix im globalen System
     */
    private double[][] getQMatGlobal(double angle) {
        double c = Math.cos(angle * Math.PI / 180.0);
        double c2 = c * c;
        double c3 = c2 * c;
        double c4 = c3 * c;

        double s = Math.sin(angle * Math.PI / 180.0);
        double s2 = s * s;
        double s3 = s2 * s;
        double s4 = s3 * s;

        double[][] Qglo = new double[3][3];

        Qglo[0][0] = c4 * Qlok_[0][0] + 2 * c2 * s2 * Qlok_[0][1] + s4 * Qlok_[1][1] + 4 * c2 * s2 * Qlok_[2][2];
        Qglo[0][1] = c2 * s2 * Qlok_[0][0] + (c4 + s4) * Qlok_[0][1] + c2 * s2 * Qlok_[1][1] - 4 * c2 * s2 * Qlok_[2][2];
        Qglo[0][2] = s * c3 * Qlok_[0][0] - c * s * (c2 - s2) * Qlok_[0][1] - c * s3 * Qlok_[1][1] - 2 * c * s * (c2 - s2) * Qlok_[2][2];
        Qglo[1][0] = Qglo[0][1];
        Qglo[1][1] = s4 * Qlok_[0][0] + 2 * c2 * s2 * Qlok_[0][1] + c4 * Qlok_[1][1] + 4 * c2 * s2 * Qlok_[2][2];
        Qglo[1][2] = c * s3 * Qlok_[0][0] + c * s * (c2 - s2) * Qlok_[0][1] - s * c3 * Qlok_[1][1] + 2 * c * s * (c2 - s2) * Qlok_[2][2];
        Qglo[2][0] = Qglo[0][2];
        Qglo[2][1] = Qglo[1][2];
        Qglo[2][2] = c2 * s2 * Qlok_[0][0] - 2 * c2 * s2 * Qlok_[0][1] + c2 * s2 * Qlok_[1][1] + (c2 - s2) * (c2 - s2) * Qlok_[2][2];

        return Qglo;
    }

    /**
     * Gibt die Q-Matrix im globalen Koordinatensystem, also gedreht um den
     * Schichtwinkel, zurück.
     *
     * @return Q-Matrix im globalen System
     */
    public double[][] getQMatGlobal_deltaAngle(double deltaAngle) {
        return getQMatGlobal(layer.getAngle() + deltaAngle);
    }

    /**
     * Gibt die S-Matrix (Nachgiebigkeitsmatrix) im lokalen bzw.
     * Schichtkoordinatensystem zurück.
     *
     * @return S-Matrix im lokalen Koordinatensystem.
     */
    public double[][] getSMatLokal() {
        return MatrixTools.getInverse(getQMatLocal());
    }

    /**
     * Gibt die S-Matrix im globalen Koordinatensystem, also gedreht um den
     * übergegeben Winkel, zurück.
     *
     * @return S-Matrix im globalen System
     */
    public double[][] getSMatGlobal() {
        return MatrixTools.getInverse(getQMatGlobal());
    }

    /**
     * Setzen der Verformungsgrößen im globalen Koordinatensystem. Der Vektor
     * ist wiefolgt aufgebaut:<br />
     * (&epsilon;<sub>x</sub>, &epsilon;<sub>y</sub>, &gamma;<sub>xy</sub>,
     * &kappa;<sub>x</sub>, &kappa;<sub>y</sub>,
     * &kappa;<sub>xy</sub>)<sup>T</sup>. Es werden also die Dehnungen und die
     * Krümmungen übergeben. Dies bewirkt, dass die Spannungsn in der Schicht
     * und die Reservefaktoren berechnet werden.
     *
     * @param epskappa globale Verformungsgrößen (Dehnungen, Krümmungen)
     * @param deltaTemp Temperaturänderung
     * @param deltaHygro Änderung der relativen Feuchte
     */
    public StressStrainState[] getStressState(double[] epskappa, double deltaTemp, double deltaHygro, int position, boolean calcGlobal) {
        double pos = 0.0;
        switch (position) {
            case POSITION_UPPER:
                pos = zm + layer.getThickness() / 2.0;
                break;
            case POSITION_LOWER:
                pos = zm - layer.getThickness() / 2.0;
                break;
            case POSITION_MIDDLE:
                pos = zm;
                break;
            default:
                pos = 0.0;
                break;
        }
        double[] strain_glo = new double[3];
        strain_glo[0] = epskappa[0] + pos * epskappa[3];
        strain_glo[1] = epskappa[1] + pos * epskappa[4];
        strain_glo[2] = epskappa[2] + pos * epskappa[5];
        return getStressStrainState(strain_glo, deltaTemp, deltaHygro, calcGlobal);
    }
    
    public StressStrainState[] getStressState_radial(double[] epskappa, double deltaTemp, double deltaHygro, int position, double meanRadius, boolean calcGlobal) {
        
        double w = epskappa[1] * meanRadius;
        
        double pos = 0.0;
        switch (position) {
            case POSITION_UPPER:
                pos = zm + layer.getThickness() / 2.0;
                break;
            case POSITION_LOWER:
                pos = zm - layer.getThickness() / 2.0;
                break;
            case POSITION_MIDDLE:
                pos = zm;
                break;
            default:
                pos = 0.0;
                break;
        }
        double[] strain_glo = new double[3];
        strain_glo[0] = epskappa[0];
        strain_glo[1] = w / (meanRadius + pos);
        strain_glo[2] = epskappa[2];
        return getStressStrainState(strain_glo, deltaTemp, deltaHygro, calcGlobal);
    }

    private StressStrainState[] getStressStrainState(double[] strain_glo, double deltaTemp, double deltaHygro, boolean calcGlobal) {

        Material material = layer.getMaterial();

        double[][] transeps = getTransMat_eps_glo_to_loc();

        double[] alpha = new double[]{material.getAlphaTPar(), material.getAlphaTNor(), 0.0};
        double[] beta = new double[]{material.getBetaPar(), material.getBetaNor(), 0.0};

        double[] strain_loc = new double[3];

        // Transformation der globalen Dehnungen in die lokalen Dehnungen
        for (int ii = 0; ii < 3; ii++) {
            strain_loc[ii] = 0.0;
            for (int jj = 0; jj < 3; jj++) {
                strain_loc[ii] += transeps[ii][jj] * strain_glo[jj];
            }
        }

        double[] stress_loc = new double[3];

        // Berechnen der lokalen Spannungen
        for (int ii = 0; ii < 3; ii++) {
            stress_loc[ii] = 0.0;
            for (int jj = 0; jj < 3; jj++) {
                stress_loc[ii] += Qlok_[ii][jj] * (strain_loc[jj] - alpha[jj] * deltaTemp - beta[jj] * deltaHygro);
            }
        }

        if (calcGlobal) {
            double[] stress_glo = new double[3];

            // Transformation der lokalen Spanngungen in die globalen Spannungen
            for (int ii = 0; ii < 3; ii++) {
                stress_glo[ii] = 0.0;
                for (int jj = 0; jj < 3; jj++) {
                    stress_glo[ii] += transeps[jj][ii] * stress_loc[jj];
                }
            }

            return new StressStrainState[]{new StressStrainState(stress_loc, strain_loc), new StressStrainState(stress_glo, strain_glo)};
        }
        return new StressStrainState[]{new StressStrainState(stress_loc, strain_loc)};
    }

    private double[][] getTransMat_eps_glo_to_loc() {
        double[][] transMat_eps_glo_to_loc_ = new double[3][3];

        double c = Math.cos(layer.getRadAngle());
        double c2 = c * c;

        double s = Math.sin(layer.getRadAngle());
        double s2 = s * s;

        transMat_eps_glo_to_loc_[0][0] = c2;
        transMat_eps_glo_to_loc_[0][1] = s2;
        transMat_eps_glo_to_loc_[0][2] = s * c;
        transMat_eps_glo_to_loc_[1][0] = s2;
        transMat_eps_glo_to_loc_[1][1] = c2;
        transMat_eps_glo_to_loc_[1][2] = -s * c;
        transMat_eps_glo_to_loc_[2][0] = -2.0 * c * s;
        transMat_eps_glo_to_loc_[2][1] = 2.0 * c * s;
        transMat_eps_glo_to_loc_[2][2] = c2 - s2;

        return transMat_eps_glo_to_loc_;
    }
}
