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
package de.elamx.clt.calculation.batchrun;

import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.Loads;
import de.elamx.clt.Strains;
import de.elamx.clt.calculation.CalculationModuleData;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Florian Dexl
 */
@ServiceProvider(service = HDF5CalculationOutputWriterService.class, position = 1)
public class HDF5CalculationOutputWriterServiceImpl implements HDF5CalculationOutputWriterService {

    @Override
    public void writeResults(IHDF5Writer hdf5writer, CalculationModuleData data, Loads loads, Strains strain, CLT_LayerResult[] results) {
        String calculationGroup = "laminates/".concat(data.getLaminat().getName().concat("/calculation/"));
        if (!hdf5writer.object().exists(calculationGroup)) {
            hdf5writer.object().createGroup(calculationGroup);
        }
        String groupName = calculationGroup.concat(data.getName());
        hdf5writer.object().createGroup(groupName);

        double[] forces = loads.getForceMomentAsVector();

        hdf5writer.object().createGroup(groupName.concat("/mechanical loads"));
        hdf5writer.float64().write(groupName.concat("/mechanical loads/nxx"), forces[0]);
        hdf5writer.float64().write(groupName.concat("/mechanical loads/nyy"), forces[1]);
        hdf5writer.float64().write(groupName.concat("/mechanical loads/nxy"), forces[2]);
        hdf5writer.float64().write(groupName.concat("/mechanical loads/mxx"), forces[3]);
        hdf5writer.float64().write(groupName.concat("/mechanical loads/myy"), forces[4]);
        hdf5writer.float64().write(groupName.concat("/mechanical loads/mxy"), forces[5]);

        hdf5writer.object().createGroup(groupName.concat("/hygrothermal loads"));
        hdf5writer.float64().write(groupName.concat("/hygrothermal loads/nxx"), loads.getnT_x());
        hdf5writer.float64().write(groupName.concat("/hygrothermal loads/nyy"), loads.getnT_y());
        hdf5writer.float64().write(groupName.concat("/hygrothermal loads/nxy"), loads.getnT_xy());
        hdf5writer.float64().write(groupName.concat("/hygrothermal loads/mxx"), loads.getmT_x());
        hdf5writer.float64().write(groupName.concat("/hygrothermal loads/myy"), loads.getmT_y());
        hdf5writer.float64().write(groupName.concat("/hygrothermal loads/mxy"), loads.getmT_xy());
        hdf5writer.float64().write(groupName.concat("/hygrothermal loads/deltaT"), loads.getDeltaT());
        hdf5writer.float64().write(groupName.concat("/hygrothermal loads/deltac"), loads.getDeltaH());

        double[] strains = strain.getEpsilonKappaAsVector();
        hdf5writer.object().createGroup(groupName.concat("/global strains"));
        hdf5writer.float64().write(groupName.concat("/global strains/exx"), strains[0]);
        hdf5writer.float64().write(groupName.concat("/global strains/eyy"), strains[1]);
        hdf5writer.float64().write(groupName.concat("/global strains/gxy"), strains[2]);
        hdf5writer.float64().write(groupName.concat("/global strains/kxx"), strains[3]);
        hdf5writer.float64().write(groupName.concat("/global strains/kyy"), strains[4]);
        hdf5writer.float64().write(groupName.concat("/global strains/kxy"), strains[5]);

        int layerResultsNum = results.length;

        hdf5writer.object().createGroup(groupName.concat("/local layer results"));
        hdf5writer.object().createGroup(groupName.concat("/local layer results/upper"));
        hdf5writer.object().createGroup(groupName.concat("/local layer results/lower"));
        hdf5writer.int32().setAttr(groupName.concat("/local layer results/upper"), "number of layers", layerResultsNum);
        hdf5writer.int32().setAttr(groupName.concat("/local layer results/lower"), "number of layers", layerResultsNum);

        double[] s11 = new double[layerResultsNum];
        double[] s22 = new double[layerResultsNum];
        double[] s12 = new double[layerResultsNum];
        double[] e11 = new double[layerResultsNum];
        double[] e12 = new double[layerResultsNum];
        double[] e22 = new double[layerResultsNum];
        double[] RF = new double[layerResultsNum];
        for (int ii = 0; ii < layerResultsNum; ii++) {
            s11[ii] = results[ii].getSss_upper().getStress()[0];
            s22[ii] = results[ii].getSss_upper().getStress()[1];
            s12[ii] = results[ii].getSss_upper().getStress()[2];
            e11[ii] = results[ii].getSss_upper().getStrain()[0];
            e22[ii] = results[ii].getSss_upper().getStrain()[1];
            e12[ii] = results[ii].getSss_upper().getStrain()[2];
            RF[ii] = results[ii].getRr_upper().getMinimalReserveFactor();
        }
        hdf5writer.float64().createArray(groupName.concat("/local layer results/upper/s11"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/upper/s11"), s11);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/upper/s12"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/upper/s12"), s12);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/upper/s22"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/upper/s22"), s22);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/upper/e11"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/upper/e11"), e11);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/upper/e22"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/upper/e22"), e22);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/upper/e12"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/upper/e12"), e12);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/upper/RF"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/upper/RF"), RF);

        s11 = new double[layerResultsNum];
        s22 = new double[layerResultsNum];
        s12 = new double[layerResultsNum];
        e11 = new double[layerResultsNum];
        e12 = new double[layerResultsNum];
        e22 = new double[layerResultsNum];
        for (int ii = 0; ii < layerResultsNum; ii++) {
            s11[ii] = results[ii].getSss_lower().getStress()[0];
            s22[ii] = results[ii].getSss_lower().getStress()[1];
            s12[ii] = results[ii].getSss_lower().getStress()[2];
            e11[ii] = results[ii].getSss_lower().getStrain()[0];
            e22[ii] = results[ii].getSss_lower().getStrain()[1];
            e12[ii] = results[ii].getSss_lower().getStrain()[2];
            RF[ii] = results[ii].getRr_lower().getMinimalReserveFactor();
        }
        hdf5writer.float64().createArray(groupName.concat("/local layer results/lower/s11"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/lower/s11"), s11);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/lower/s12"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/lower/s12"), s12);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/lower/s22"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/lower/s22"), s22);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/lower/e11"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/lower/e11"), e11);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/lower/e22"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/lower/e22"), e22);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/lower/e12"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/lower/e12"), e12);
        hdf5writer.float64().createArray(groupName.concat("/local layer results/lower/RF"), layerResultsNum);
        hdf5writer.float64().writeArray(groupName.concat("/local layer results/lower/RF"), RF);

    }

}
