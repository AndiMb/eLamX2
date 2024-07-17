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
package de.elamx.clt.plate.dmatrix;

import de.elamx.clt.CLT_Laminate;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author ahauffe
 */
@ServiceProvider(service=DMatrixService.class, position=100)
public class SpecialOrthotropicDMatrixServiceImpl extends DMatrixService {

    public SpecialOrthotropicDMatrixServiceImpl() {
        super(NbBundle.getMessage(SpecialOrthotropicDMatrixServiceImpl.class, "SpecialOrthotropicDMatrixServiceImpl.name"), "D");
    }

    @Override
    public double[][] getDMatrix(CLT_Laminate laminate) {
        double[][] dmat = laminate.getDMatrix();
        double[][] DwithZeroD12D16 = new double[3][3];
        for (int ii = 0; ii < 3; ii++){
            System.arraycopy(dmat[ii], 0, DwithZeroD12D16[ii], 0, 3);
        }

        // Nullsetzen der D16 und D26-Terme
        DwithZeroD12D16[0][2] = 0.0;
        DwithZeroD12D16[1][2] = 0.0;
        DwithZeroD12D16[2][0] = 0.0;
        DwithZeroD12D16[2][1] = 0.0;
        
        return DwithZeroD12D16;
    }

    @Override
    public boolean needsSymmetricLaminate() {
        return true;
    }

    @Override
    public String getBatchRunOutput() {
        return "D matrix with D_{16} = D_{26} = 0";
    }
}
