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
package de.elamx.clt.calculation;

import de.elamx.clt.CLT_Calculator;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.laminate.Laminat;
import java.io.PrintStream;
import java.util.Collection;
import org.openide.util.lookup.ServiceProvider;
import de.elamx.core.BatchRunService;
import org.openide.util.Lookup;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = BatchRunService.class)
public class CalculationBatchRunServiceImpl implements BatchRunService {

    @Override
    public void performBatchTasksAndOutput(Laminat laminate, PrintStream ps) {
        Collection<? extends CalculationModuleData> col = laminate.getLookup().lookupAll(CalculationModuleData.class);
        if (col.isEmpty()){
            return;
        }
        CLT_Laminate clt_lam = laminate.getLookup().lookup(CLT_Laminate.class);
        if (clt_lam == null) {
            clt_lam = new CLT_Laminate(laminate);
        }
        
        CalculationOutputWriterServiceImpl outputWriter = Lookup.getDefault().lookup(CalculationOutputWriterServiceImpl.class);
        
        for (CalculationModuleData data : col) {
            CLT_Calculator.determineValues(clt_lam, data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), data.getDataHolder().isUseStrains());
            CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(data.getLaminat().getLookup().lookup(CLT_Laminate.class), data.getDataHolder().getLoad(), data.getDataHolder().getStrains());
            outputWriter.writeResults(ps, data, data.getDataHolder().getLoad(), data.getDataHolder().getStrains(), layerResults);
        }
    }
}
