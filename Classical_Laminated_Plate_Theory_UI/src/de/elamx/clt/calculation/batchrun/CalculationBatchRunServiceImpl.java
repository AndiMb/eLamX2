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

import ch.systemsx.cisd.hdf5.HDF5CompoundType;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.clt.CLT_Calculator;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.calculation.CalculationModuleData;
import de.elamx.core.BatchRunService;
import de.elamx.laminate.Laminat;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = BatchRunService.class)
public class CalculationBatchRunServiceImpl implements BatchRunService {

    private static HDF5CompoundType<List<?>> HDF5minRFType = null;

    @Override
    public void performBatchTasksAndOutput(Laminat laminate, PrintStream ps, IHDF5Writer hdf5writer, int outputType) {
        Collection<? extends CalculationModuleData> col = laminate.getLookup().lookupAll(CalculationModuleData.class);
        if (col.isEmpty()) {
            return;
        }
        CLT_Laminate clt_lam = laminate.getLookup().lookup(CLT_Laminate.class);
        if (clt_lam == null) {
            clt_lam = new CLT_Laminate(laminate);
        }

        List<CalculationOutputWriterService> writerServices = new ArrayList<>(Lookup.getDefault().lookupAll(CalculationOutputWriterService.class));
        CalculationOutputWriterService outputWriter = writerServices.get(Math.min(Math.max(outputType, 0), writerServices.size() - 1));

        HDF5CalculationOutputWriterService hdf5OutputWriter = null;
        if (hdf5writer != null) {
            List<HDF5CalculationOutputWriterService> hdf5WriterServices = new ArrayList<>(Lookup.getDefault().lookupAll(HDF5CalculationOutputWriterService.class));
            hdf5OutputWriter = hdf5WriterServices.get(Math.min(Math.max(outputType, 0), hdf5WriterServices.size() - 1));

        }

        double minRF = Double.MAX_VALUE;
        String minRF_calculation = "";
        double minRF_temp;
        int minRF_layer = -1;
        String minRF_position = "";
        for (CalculationModuleData data : col) {
            CLT_Calculator.determineValues(clt_lam, data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), data.getDataHolder().isUseStrains());
            CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(data.getLaminat().getLookup().lookup(CLT_Laminate.class), data.getDataHolder().getLoad(), data.getDataHolder().getStrains());
            outputWriter.writeResults(ps, data, data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), layerResults);

            if (hdf5OutputWriter != null) {
                hdf5OutputWriter.writeResults(hdf5writer, data, data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), layerResults);

                minRF_temp = Double.MAX_VALUE;
                for (int lay = 0; lay < layerResults.length; lay++) {
                    if (layerResults[lay].getRr_lower().getMinimalReserveFactor() < minRF_temp) {
                        minRF_temp = layerResults[lay].getRr_lower().getMinimalReserveFactor();
                        minRF_layer = lay + 1;
                        minRF_position = "lower";
                    }

                    if (layerResults[lay].getRr_upper().getMinimalReserveFactor() < minRF_temp) {
                        minRF_temp = layerResults[lay].getRr_upper().getMinimalReserveFactor();
                        minRF_layer = lay + 1;
                        minRF_position = "upper";
                    }
                }
                if (minRF_temp < minRF) {
                    minRF = minRF_temp;
                    minRF_calculation = data.getName();
                }
            }
        }

        if ((hdf5writer != null) && (minRF != Double.MAX_VALUE)) {
            ArrayList<Object> minRFValuesArrayList = new ArrayList<>();
            ArrayList<String> minRFNamesArrayList = new ArrayList<>();

            minRFValuesArrayList.add(minRF);
            minRFNamesArrayList.add("RF");

            minRFValuesArrayList.add(minRF_calculation);
            minRFNamesArrayList.add("calculation");

            minRFValuesArrayList.add(minRF_layer);
            minRFNamesArrayList.add("layer");

            minRFValuesArrayList.add(minRF_position);
            minRFNamesArrayList.add("position");

            if (HDF5minRFType == null) {
                HDF5minRFType = hdf5writer.compound().getInferredType("Minimum RF", minRFNamesArrayList, minRFValuesArrayList);
            }

            String calculationGroup = "laminates/".concat(laminate.getName().concat("/calculation"));
            hdf5writer.compound().write(calculationGroup.concat("/min RF"), HDF5minRFType, minRFValuesArrayList);
        }
    }
}
