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
package de.elamx.clt.calculation.lastplyfailureui.batchrun;

import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.clt.CLT_Calculator;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LastPlyFailureResult;
import de.elamx.clt.calculation.lastplyfailureui.LastPlyFailureModuleData;
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
public class LastPlyFailureBatchRunServiceImpl implements BatchRunService {

    @Override
    public void performBatchTasksAndOutput(Laminat laminate, PrintStream ps, IHDF5Writer hdf5writer, int outputType) {
        Collection<? extends LastPlyFailureModuleData> col = laminate.getLookup().lookupAll(LastPlyFailureModuleData.class);
        if (col.isEmpty()) {
            return;
        }
        CLT_Laminate clt_lam = laminate.getLookup().lookup(CLT_Laminate.class);
        if (clt_lam == null) {
            clt_lam = new CLT_Laminate(laminate);
        }

        List<LastPlyFailureOutputWriterService> writerServices = new ArrayList<>(Lookup.getDefault().lookupAll(LastPlyFailureOutputWriterService.class));
        LastPlyFailureOutputWriterService outputWriter = writerServices.get(Math.min(Math.max(outputType, 0),writerServices.size()-1));

        /*HDF5LastPlyFailureOutputWriterService hdf5OutputWriter = null;
        if (hdf5writer != null) {
            List<HDF5LastPlyFailureOutputWriterService> hdf5WriterServices = new ArrayList<>(Lookup.getDefault().lookupAll(HDF5LastPlyFailureOutputWriterService.class));
            hdf5OutputWriter = hdf5WriterServices.get(Math.min(Math.max(outputType, 0), hdf5WriterServices.size() - 1));
        }*/
        for (LastPlyFailureModuleData data : col) {

            CLT_LastPlyFailureResult lpfResult = CLT_Calculator.determineValuesLastPlyFailure(
                    clt_lam,
                    data.getLastPlyFailureInput().getLoad(),
                    data.getLastPlyFailureInput().getStrain(),
                    data.getLastPlyFailureInput().isUseStrains(),
                    data.getLastPlyFailureInput().getDegradationFactor(),
                    data.getLastPlyFailureInput().getEpsilon_crit(),
                    data.getLastPlyFailureInput().getJ_a(),
                    data.getLastPlyFailureInput().isDegradeAllOnFibreFailure()
            );

            outputWriter.writeResults(ps, data, lpfResult);
            /*if (hdf5OutputWriter != null) {
                hdf5OutputWriter.writeResults(hdf5writer, data, lpfResult);
            }*/
        }
    }
}
