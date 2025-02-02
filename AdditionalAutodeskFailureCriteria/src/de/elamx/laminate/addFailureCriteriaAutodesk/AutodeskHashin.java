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
package de.elamx.laminate.addFailureCriteriaAutodesk;

/*
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

public class AutodeskHashin extends Criterion {
    
    public static final String ALPHA = AutodeskHashin.class.getName() + ".alp";
    public static final String R23 = AutodeskHashin.class.getName() + ".R23";

    public static AutodeskHashin getDefault(FileObject obj) {
        AutodeskHashin AuHa = new AutodeskHashin(obj);

        return AuHa;
    }

    public AutodeskHashin(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double R_23 = material.getAdditionalValue(R23);
        double alp = material.getAdditionalValue(ALPHA);
        
        double dTemp;

        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();

        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }
        double resFac_Fiber = 0;
        double resFac_Matrix;

        //Faserzugversagen
        if (stresses[0] >= 0.0) {


            double F_S = stresses[0] * stresses[0] / material.getRParTen() / material.getRParTen();
            F_S += alp * (stresses[2] * stresses[2] / material.getRShear() / material.getRShear());
            resFac_Fiber = Math.sqrt(1 / (F_S));

            rf.setMinimalReserveFactor(resFac_Fiber);
            rf.setFailureName("FiberFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }

        //Faserdruckversagen
        if (stresses[0] < 0.0) {

            double F_S = Math.sqrt(stresses[0] * stresses[0]) / material.getRParCom();
            resFac_Fiber = 1 / (F_S);

            rf.setMinimalReserveFactor(resFac_Fiber);
            rf.setFailureName("FiberFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }
        //Matrixzugversagen
        if (stresses[1] >= 0.0) {

            double M_S = stresses[1] * stresses[1] / material.getRNorTen() / material.getRNorTen();
            M_S += stresses[2] * stresses[2] / material.getRShear() / material.getRShear();
            resFac_Matrix = Math.sqrt(1 / (M_S));

            if (resFac_Matrix < resFac_Fiber) {
                rf.setMinimalReserveFactor(resFac_Matrix);
                rf.setFailureName("MatrixFailureTension");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }

        }

        //Matrixdruckversagen
        if (stresses[1] < 0.0) {

            double Q = 0.25 * stresses[1] * stresses[1] / R_23 / R_23;
            Q += stresses[2] * stresses[2] / material.getRShear() / material.getRShear();

            double L = 0.25 * material.getRNorCom() / R_23 / R_23;
            L -= 1.0 / material.getRNorCom();
            L *= stresses[1];
            
            dTemp = L * L + 4.0 * Q;
            if (dTemp < 0.0) {
                throw new ArithmeticException("illegal double value: " + dTemp);
            }

            resFac_Matrix = (Math.sqrt(dTemp) - L) / (2.0 * Q);

            if (resFac_Matrix < resFac_Fiber) {
                rf.setMinimalReserveFactor(resFac_Matrix);
                rf.setFailureName("MatrixFailureCompression");
                rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
            }

        }

        rf.setFailureName(NbBundle.getMessage(AutodeskHashin.class, "AutodeskHashin." + rf.getFailureName()));

        return rf;
    }
}


