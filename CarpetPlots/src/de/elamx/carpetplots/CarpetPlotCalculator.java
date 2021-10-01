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
package de.elamx.carpetplots;

import de.elamx.laminate.LayerMaterial;
import de.elamx.mathtools.MatrixTools;

/**
 *
 * @author Andreas Hauffe
 */
public class CarpetPlotCalculator {
    
    public static final int VAL_EX    = 0;
    public static final int VAL_NUEXY = 1;
    public static final int VAL_GXY   = 2;

    private final LayerMaterial material;

    private double[][] Q_0, Q_45, Q_m45, Q_90;

    public CarpetPlotCalculator(LayerMaterial material) {
        this.material = material;
    }

    public double[][] getChartData(double fraction0, int nP, int type, boolean with90Deg) {
        double[][] data = new double[2][nP];
        double[][] Qtot = new double[3][3];
        ValueCalculator valCal;
        switch(type){
            case VAL_EX:
                valCal = new ExCalculator(); break;
            case VAL_NUEXY:
                valCal = new NuexyCalculator(); break;
            case VAL_GXY:
                valCal = new GxyCalculator(); break;
            default:
                valCal = new ExCalculator(); break;
        }

        calcQs();

        double deltaFraction45 = (1.0 - fraction0) / (nP - 1);

        for (int ii = 0; ii < nP; ii++) {

            double fraction45 = ii * deltaFraction45;
            double fraction90 = 1.0 - fraction45 - fraction0;
            
            if (!with90Deg){
                fraction0  = 1.0 - fraction45;
                fraction90 = 0.0;
            }

            for (int mm = 0; mm < 3; mm++) {
                for (int nn = 0; nn < 3; nn++) {
                    Qtot[mm][nn] = fraction0 * Q_0[mm][nn] + fraction45 * Q_45[mm][nn] / 2.0 + fraction45 * Q_m45[mm][nn] / 2.0 + fraction90 * Q_90[mm][nn];
                }
            }

            double[][] iQtot = MatrixTools.getInverse(Qtot);

            data[0][ii] = fraction45;
            data[1][ii] = valCal.getValue(iQtot);
        }

        return data;
    }

    private void calcQs() {
        double nu21 = material.getNue21();

        double temp = 1.0 / (1.0 - material.getNue12() * nu21);

        Q_0 = new double[3][3];

        Q_0[0][0] = temp * material.getEpar();
        Q_0[0][1] = temp * material.getEpar() * nu21;
        Q_0[0][2] = 0.0;
        Q_0[1][0] = Q_0[0][1];
        Q_0[1][1] = temp * material.getEnor();
        Q_0[1][2] = 0.0;
        Q_0[2][0] = 0.0;
        Q_0[2][1] = 0.0;
        Q_0[2][2] = material.getG();

        Q_90 = new double[3][3];

        for (int ii = 0; ii < Q_0.length; ii++) {
            System.arraycopy(Q_0[ii], 0, Q_90[ii], 0, Q_0[0].length);
        }

        Q_90[0][0] = Q_0[1][1];
        Q_90[1][1] = Q_0[0][0];

        Q_45 = getQAngle(Q_0, 45.0);
        Q_m45 = getQAngle(Q_0, -45.0);
    }

    private double[][] getQAngle(double[][] Q, double angle) {
        double c = Math.cos(angle * Math.PI / 180.0);
        double c2 = c * c;
        double c3 = c2 * c;
        double c4 = c3 * c;

        double s = Math.sin(angle * Math.PI / 180.0);
        double s2 = s * s;
        double s3 = s2 * s;
        double s4 = s3 * s;

        double[][] Qa = new double[3][3];

        Qa[0][0] = c4 * Q[0][0] + 2 * c2 * s2 * Q[0][1] + s4 * Q[1][1] + 4 * c2 * s2 * Q[2][2];
        Qa[0][1] = c2 * s2 * Q[0][0] + (c4 + s4) * Q[0][1] + c2 * s2 * Q[1][1] - 4 * c2 * s2 * Q[2][2];
        Qa[0][2] = s * c3 * Q[0][0] - c * s * (c2 - s2) * Q[0][1] - c * s3 * Q[1][1] - 2 * c * s * (c2 - s2) * Q[2][2];
        Qa[1][0] = Qa[0][1];
        Qa[1][1] = s4 * Q[0][0] + 2 * c2 * s2 * Q[0][1] + c4 * Q[1][1] + 4 * c2 * s2 * Q[2][2];
        Qa[1][2] = c * s3 * Q[0][0] + c * s * (c2 - s2) * Q[0][1] - s * c3 * Q[1][1] + 2 * c * s * (c2 - s2) * Q[2][2];
        Qa[2][0] = Qa[0][2];
        Qa[2][1] = Qa[1][2];
        Qa[2][2] = c2 * s2 * Q[0][0] - 2 * c2 * s2 * Q[0][1] + c2 * s2 * Q[1][1] + (c2 - s2) * (c2 - s2) * Q[2][2];

        return Qa;
    }
    
    private interface ValueCalculator{
        public double getValue(double[][] Qmat);
    }
    
    private class ExCalculator implements ValueCalculator{

        @Override
        public double getValue(double[][] Qmat) {
            return 1.0 / Qmat[0][0];
        }
        
    }
    
    private class NuexyCalculator implements ValueCalculator{

        @Override
        public double getValue(double[][] Qmat) {
            return -Qmat[0][1]/Qmat[0][0];
        }
        
    }
    
    private class GxyCalculator implements ValueCalculator{

        @Override
        public double getValue(double[][] Qmat) {
            return 1.0 / Qmat[2][2];
        }
        
    }
}
