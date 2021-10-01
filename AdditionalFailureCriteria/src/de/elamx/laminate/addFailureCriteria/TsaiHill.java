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
 *
 * @author Tim Dorau
 */
public class TsaiHill extends Criterion {

    public static TsaiHill getDefault(FileObject obj) {
        TsaiHill tw = new TsaiHill(obj);

        return tw;
    }

    public TsaiHill(FileObject obj) {
        super(obj);
    }

    @Override
    public ReserveFactor getReserveFactor(Material material, Layer l, StressStrainState sss) {
        double[] stresses = sss.getStress();
        ReserveFactor rf = new ReserveFactor();
        
        /** Berechnung der Koeffizienten der quadratischen Gleichung */
        double F1 = 0;
        double F2 = 0;
        double F11;
        double F22;
        double F12;
        double F66 = 1 / (material.getRShear() * material.getRShear());
        
        if (stresses[0] > 0) {
            F11 = 1 / (material.getRParTen() * material.getRParTen());
            F12 = -1 / (2 * material.getRParTen() * material.getRParTen()); 
        } else {
            F11  = 1 / (material.getRParCom() * material.getRParCom());
            F12 = -1 / (2 * material.getRParCom() * material.getRParCom()); 
        }
        
        if (stresses[1] > 0) {            
            F22 = 1 / (material.getRNorTen() * material.getRNorTen());
        } else {
            F22 = 1 / (material.getRNorCom() * material.getRNorCom()); 
        }       
        
        // Berechnung Reservefakto aus Failure Index über quadratische Gleichung
        double Q = F11 * stresses[0] * stresses[0];
        Q += 2.0 * F12 * stresses[0] * stresses[1];
        Q += F22 * stresses[1] * stresses[1];
        Q += F66 * stresses[2] * stresses[2];

        double resFac = 1 / Math.sqrt(Q);

        rf.setMinimalReserveFactor(resFac);
        rf.setFailureName("Failure");
        rf.setFailureType(ReserveFactor.FIBER_FAILURE);

        if (Q == 0.0) {
            rf.setFailureName("");
            rf.setMinimalReserveFactor(Double.POSITIVE_INFINITY);
        } else {
            rf.setFailureName(NbBundle.getMessage(TsaiHill.class, "TsaiHill." + rf.getFailureName()));
        }

        return rf;
    }
}
