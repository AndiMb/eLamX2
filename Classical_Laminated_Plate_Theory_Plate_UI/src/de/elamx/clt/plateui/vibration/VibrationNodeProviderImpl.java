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
package de.elamx.clt.plateui.vibration;

import de.elamx.fileview.eLamXModuleDataNodeProvider;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.modules.eLamXModuleData;
import java.util.Collection;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author raedel
 */
@ServiceProvider(service = eLamXModuleDataNodeProvider.class)
public class VibrationNodeProviderImpl implements eLamXModuleDataNodeProvider{

    @Override
    public eLamXModuleData[] geteLamXModuleData(Laminat laminat) {
        Collection<? extends VibrationModuleData> col = laminat.getLookup().lookupAll(VibrationModuleData.class);
        return col.toArray(new VibrationModuleData[col.size()]);
    }

    @Override
    public Node getNodes(eLamXModuleData moduleData) {
        if (moduleData instanceof VibrationModuleData) {
            return new VibrationDataNode((VibrationModuleData)moduleData);
        }
        return null;
    }
    
}
