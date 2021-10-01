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

import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.ReserveFactor;
import de.elamx.mathtools.MatrixTools;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class MaxStrain extends Criterion {

    public static final String EPS_X = MaxStrain.class.getName() + ".eps_x";
    public static final String EPS_Y = MaxStrain.class.getName() + ".eps_y";
    public static final String GAMMA_XY = MaxStrain.class.getName() + ".gamma_xy";
    public static final String GLOBAL_LOCAL = MaxStrain.class.getName() + ".global_lokal";

    public static MaxStrain getDefault(FileObject obj) {
        MaxStrain ms = new MaxStrain(obj);

        return ms;
    }

    public MaxStrain(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer layer, StressStrainState stressStateState) {
        double epsX = material.getAdditionalValue(EPS_X);
        double epsY = material.getAdditionalValue(EPS_Y);
        double gammaXY = material.getAdditionalValue(GAMMA_XY);
        double globalLokal = material.getAdditionalValue(GLOBAL_LOCAL);

        boolean global = globalLokal > 0.5;

        double[] strains = stressStateState.getStrain();

        ReserveFactor rf = new ReserveFactor();
        double value;

        if (strains[0] == 0.0 && strains[1] == 0.0 && strains[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }

        if (global) {
            double[][] transMat = MatrixTools.getInverse(getTransMat_eps_glo_to_loc(layer));
            double[] globStrains = new double[3];

            for (int ii = 0; ii < 3; ii++) {
                globStrains[ii] = 0;
                for (int jj = 0; jj < 3; jj++) {
                    globStrains[ii] += transMat[ii][jj] * strains[jj];
                }
            }

            strains = globStrains;
        }

        rf.setMinimalReserveFactor(epsX / Math.abs(strains[0]));
        rf.setFailureName("FiberFailure");
        rf.setFailureType(ReserveFactor.FIBER_FAILURE);

        value = epsY / Math.abs(strains[1]);
        if (value < rf.getMinimalReserveFactor()) {
            rf.setMinimalReserveFactor(value);
            rf.setFailureName("MatrixFailure");
            rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
        }

        value = gammaXY / Math.abs(strains[2]);
        if (value < rf.getMinimalReserveFactor()) {
            rf.setMinimalReserveFactor(value);
            rf.setFailureName("MatrixFailureShear");
            rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
        }

        if (Double.POSITIVE_INFINITY == rf.getMinimalReserveFactor()) {
            rf.setFailureName("");
        } else {
            rf.setFailureName(NbBundle.getMessage(MaxStress.class, "MaxStrain." + rf.getFailureName()));
        }

        return rf;
    }

    private double[][] getTransMat_eps_glo_to_loc(Layer layer) {
        double[][] transMat_eps_glo_to_loc_ = new double[3][3];

        if (layer != null) {

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
        } else {
            transMat_eps_glo_to_loc_[0][0] = 1.0;
            transMat_eps_glo_to_loc_[1][1] = 1.0;
            transMat_eps_glo_to_loc_[2][2] = 1.0;
        }

        return transMat_eps_glo_to_loc_;
    }

}
