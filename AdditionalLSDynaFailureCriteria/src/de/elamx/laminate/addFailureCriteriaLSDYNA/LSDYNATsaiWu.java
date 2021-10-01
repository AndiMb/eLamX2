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
 * Diese Klasse ist die Implementierung von LS-DYNA MAT055.
 * @author Tim Dorau
 */


public class LSDYNATsaiWu extends Criterion {

    public static final String BETA = LSDYNATsaiWu.class.getName() + ".beta";

    public static LSDYNATsaiWu getDefault(FileObject obj) {
        LSDYNATsaiWu LSDtw = new LSDYNATsaiWu(obj);
        return LSDtw;
    }

    public LSDYNATsaiWu(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double beta = material.getAdditionalValue(BETA);
        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();
        double R_F = 0; double R_M;
        
        if (stresses[0] == 0.0 && stresses[1] == 0.0 && stresses[2] == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
            return rf;
        }
        
        //Faserzugversagen
        if (stresses[0] >= 0.0) {
            double Q_1= stresses[0] * stresses[0] / material.getRParTen() / material.getRParTen();
            double L_1 = beta * stresses[2] / material.getRShear();
            R_F = (Math.sqrt(L_1 * L_1 + 4.0 * Q_1) - L_1) / (2.0 * Q_1);
            rf.setMinimalReserveFactor(R_F);
            rf.setFailureName("FiberFailureTension");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }

        //Faserdruckversagen
        if (stresses[0] < 0.0) {
            double F_S = Math.sqrt(stresses[0] * stresses[0]) / material.getRParCom();
            R_F = 1 / (F_S);
            rf.setMinimalReserveFactor(R_F);
            rf.setFailureName("FiberFailureCompression");
            rf.setFailureType(ReserveFactor.FIBER_FAILURE);
        }
        
        if (Double.isNaN(R_F)) {
            R_F = 99999999;
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
        }       

        double Q_2= stresses[1] * stresses[1] / material.getRNorTen() / material.getRNorCom() + stresses[2] * stresses[2] / material.getRShear() / material.getRShear();
        double L_2 = (material.getRNorCom() - material.getRNorTen()) * stresses[1] / (material.getRNorCom() * material.getRNorTen());
        R_M = (Math.sqrt(L_2 * L_2 + 4.0 * Q_2) - L_2) / (2.0 * Q_2);
        if (R_M < R_F) {
            rf.setMinimalReserveFactor(R_M);
            rf.setFailureName("MatrixFailure");
            rf.setFailureType(ReserveFactor.MATRIX_FAILURE);
        }

        rf.setFailureName(NbBundle.getMessage(LSDYNATsaiWu.class, "LSDYNATsaiWu." + rf.getFailureName()));
        return rf;
    }
}

