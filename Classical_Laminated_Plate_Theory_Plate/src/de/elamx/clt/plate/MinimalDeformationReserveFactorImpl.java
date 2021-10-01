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
import de.elamx.clt.plate.Mechanical.SurfaceLoad_const_full;
import de.elamx.clt.plate.Mechanical.TransverseLoad;
import de.elamx.clt.plate.view3d.DeformationPlate;
import de.elamx.core.ELamXDecimalFormat;
import de.elamx.core.GlobalProperties;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class MinimalDeformationReserveFactorImpl implements MinimalReserveFactorCalculator {

    private static final String[] BOUNDARY_COND = new String[]{"SS", "CC", "CF", "FF", "SC", "SF"};

    private final DeformationInput input;

    public MinimalDeformationReserveFactorImpl() {
        input = new DeformationInput();
        input.addLoad(new SurfaceLoad_const_full("Opt_Load", 0.0));
    }

    public MinimalDeformationReserveFactorImpl(DeformationInput input) {
        this.input = input;
    }

    @Override
    public double getMinimalReserveFactor(Laminat laminat) {
        CLT_Laminate clt_lam = laminat.getLookup().lookup(CLT_Laminate.class);

        if (clt_lam == null) {
            clt_lam = new CLT_Laminate(laminat);
        }

        DeformationResult result = Deformation.calc(clt_lam, input);
        DeformationPlate plate = new DeformationPlate(input, result);

        double[][] values = plate.getValues(DeformationPlate.DISPLACEMENT_Z);

        double maxVal = 0.0;

        for (double[] value : values) {
            for (int jj = 0; jj < value.length; jj++) {
                if (maxVal < Math.abs(value[jj])) {
                    maxVal = Math.abs(value[jj]);
                }
            }
        }

        return input.getMaxDisplacementInZ() / maxVal;
    }

    public DeformationInput getInput() {
        return input;
    }

    @Override
    public boolean isSymmetricLaminateNeeded() {
        return true;
    }

    @Override
    public MinimalReserveFactorCalculator getCopy() {
        return new MinimalDeformationReserveFactorImpl((DeformationInput) input.copy());
    }

    @Override
    public String getHtmlString() {
        ELamXDecimalFormat dff = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);
        ELamXDecimalFormat dft = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);
        StringBuilder stringBuild = new StringBuilder();

        stringBuild.append(NbBundle.getMessage(MinimalDeformationReserveFactorImpl.class, "MinimalDeformationReserveFactorImpl.name"));
        stringBuild.append("; ");

        stringBuild.append(NbBundle.getMessage(MinimalDeformationReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.length"));
        stringBuild.append(" = ");
        stringBuild.append(dft.format(input.getLength()));
        stringBuild.append("; ");

        stringBuild.append(NbBundle.getMessage(MinimalDeformationReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.width"));
        stringBuild.append(" = ");
        stringBuild.append(dft.format(input.getWidth()));
        stringBuild.append("; ");

        stringBuild.append(NbBundle.getMessage(MinimalDeformationReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.bcx"));
        stringBuild.append(" = ");
        stringBuild.append(BOUNDARY_COND[input.getBcx()]);
        stringBuild.append("; ");

        stringBuild.append(NbBundle.getMessage(MinimalDeformationReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.bcy"));
        stringBuild.append(" = ");
        stringBuild.append(BOUNDARY_COND[input.getBcy()]);
        stringBuild.append("; ");

        for (TransverseLoad l : input.getLoads()) {
            if (l instanceof SurfaceLoad_const_full) {
                stringBuild.append(NbBundle.getMessage(MinimalDeformationReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.SurfaceLoad_const_full"));
                stringBuild.append(" = ");
                stringBuild.append(dff.format(((SurfaceLoad_const_full) l).getForce()));
                stringBuild.append("; ");
            }
        }

        stringBuild.append(NbBundle.getMessage(MinimalDeformationReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.maxDisplacementInZ"));
        stringBuild.append(" = ");
        stringBuild.append(dft.format(input.getMaxDisplacementInZ()));
        stringBuild.append("; ");

        stringBuild.append(NbBundle.getMessage(MinimalDeformationReserveFactorImpl.class, "MinimalBucklingReserveFactorImpl.m"));
        stringBuild.append(" = ");
        stringBuild.append(Integer.toString(input.getM()));
        stringBuild.append("; ");

        return stringBuild.toString();
    }
}
