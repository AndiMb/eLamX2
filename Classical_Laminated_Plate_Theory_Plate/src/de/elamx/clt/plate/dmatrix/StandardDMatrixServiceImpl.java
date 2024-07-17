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
 * @author Andreas Hauffe
 */
@ServiceProvider(service=DMatrixService.class, position=1)
public class StandardDMatrixServiceImpl extends DMatrixService {
    
    public StandardDMatrixServiceImpl() {
        super(NbBundle.getMessage(StandardDMatrixServiceImpl.class, "StandardDMatrixServiceImpl.name"), "D");
    }

    @Override
    public double[][] getDMatrix(CLT_Laminate laminate) {
        return laminate.getDMatrix();
    }

    @Override
    public boolean needsSymmetricLaminate() {
        return true;
    }

    @Override
    public String getBatchRunOutput() {
        return "Original D matrix";
    }
}
