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
import de.elamx.mathtools.MatrixTools;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service=DMatrixService.class, position=200)
public class DtildeDMatrixServiceImpl extends DMatrixService{
    
    public DtildeDMatrixServiceImpl() {
        super(NbBundle.getMessage(DtildeDMatrixServiceImpl.class, "DtildeDMatrixServiceImpl.name"), "D̃");
    }

    @Override
    public double[][] getDMatrix(CLT_Laminate laminate) {
        // Berechnen von D tilde
        double [][] Ainv    = MatrixTools.getInverse(laminate.getAMatrix());

        double [][] helpMat1    = MatrixTools.MatMult(laminate.getBMatrix(), Ainv);
        double [][] helpMat2    = MatrixTools.MatMult(helpMat1, laminate.getBMatrix());

        double[][] dmat = laminate.getDMatrix();
        double[][] Dtilde = new double[3][3];
        for (int ii = 0; ii < Dtilde.length; ii++) {
            for (int jj = 0; jj < Dtilde[0].length; jj++) {
                Dtilde[ii][jj] = dmat[ii][jj] - helpMat2[ii][jj];
            }
        }
        return Dtilde;
    }

    @Override
    public boolean needsSymmetricLaminate() {
        return false;
    }

    @Override
    public String getBatchRunOutput() {
        return "D-tilde matrix";
    }
}
