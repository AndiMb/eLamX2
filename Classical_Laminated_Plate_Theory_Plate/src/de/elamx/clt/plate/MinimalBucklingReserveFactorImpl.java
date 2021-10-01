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
package de.elamx.clt.plate;

import de.elamx.clt.CLT_Laminate;
import de.elamx.core.ELamXDecimalFormat;
import de.elamx.core.GlobalProperties;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class MinimalBucklingReserveFactorImpl implements MinimalReserveFactorCalculator{

    private static final String[] BOUNDARY_COND = new String[]{"SS", "CC", "CF", "FF", "SC", "SF"};
    
    private final BucklingInput input;

    public MinimalBucklingReserveFactorImpl() {
        input = new BucklingInput();
    }
    
    public MinimalBucklingReserveFactorImpl(BucklingInput input) {
        this.input = input;
    }
    
    @Override
    public double getMinimalReserveFactor(Laminat laminat) {
        CLT_Laminate clt_lam = laminat.getLookup().lookup(CLT_Laminate.class);
        
        if (clt_lam == null) {
            clt_lam = new CLT_Laminate(laminat);
        }
        
        BucklingResult result = Buckling.calc(clt_lam, input);
        
        double smallestPositiveEigenvalue = result.getSmallestPositiveEigenValue();
        
        return smallestPositiveEigenvalue < 0.0 ? Double.POSITIVE_INFINITY : smallestPositiveEigenvalue;
    }

    public BucklingInput getInput() {
        return input;
    }

    @Override
    public boolean isSymmetricLaminateNeeded() {
        return true;
    }

    @Override
    public MinimalReserveFactorCalculator getCopy() {
        return new MinimalBucklingReserveFactorImpl((BucklingInput)input.copy());
    }

    @Override
    public String getHtmlString() {
        ELamXDecimalFormat dff = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);
        ELamXDecimalFormat dft = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);
        StringBuilder stringBuild = new StringBuilder();
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.name"));
        stringBuild.append("; ");
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.length"));
        stringBuild.append(" = ");
        stringBuild.append(dft.format(input.getLength()));
        stringBuild.append("; ");
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.width"));
        stringBuild.append(" = ");
        stringBuild.append(dft.format(input.getWidth()));
        stringBuild.append("; ");
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.bcx"));
        stringBuild.append(" = ");
        stringBuild.append(BOUNDARY_COND[input.getBcx()]);
        stringBuild.append("; ");
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.bcy"));
        stringBuild.append(" = ");
        stringBuild.append(BOUNDARY_COND[input.getBcy()]);
        stringBuild.append("; ");
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.nx"));
        stringBuild.append(" = ");
        stringBuild.append(dff.format(input.getNx()));
        stringBuild.append("; ");
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.ny"));
        stringBuild.append(" = ");
        stringBuild.append(dff.format(input.getNy()));
        stringBuild.append("; ");
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.nxy"));
        stringBuild.append(" = ");
        stringBuild.append(dff.format(input.getNxy()));
        stringBuild.append("; ");
        
        stringBuild.append(NbBundle.getMessage(MinimalBucklingReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.m"));
        stringBuild.append(" = ");
        stringBuild.append(Integer.toString(input.getM()));
        stringBuild.append("; ");
        
        return stringBuild.toString();
    }
    
}
