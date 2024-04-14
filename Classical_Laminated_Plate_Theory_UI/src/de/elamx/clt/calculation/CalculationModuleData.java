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

import de.elamx.clt.CLT_Input;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.modules.eLamXModuleData;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class CalculationModuleData extends eLamXModuleData implements PropertyChangeListener{
    
    private final CLT_Input dataHolder;
    
    public CalculationModuleData(Laminat laminat){
        this(laminat, new CLT_Input());
    }

    @SuppressWarnings("this-escape")
    public CalculationModuleData(Laminat laminat, CLT_Input dataHolder) {
        super(laminat, NbBundle.getMessage(CalculationModuleData.class, "CalculationModule.name"));
        this.dataHolder = dataHolder;
        this.dataHolder.addPropertyChangeListener(this);
    }

    public CLT_Input getDataHolder() {
        return dataHolder;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        eLamXLookup.getDefault().setModified(true);
    }

    @Override
    public eLamXModuleData copy(Laminat laminate) {
        return new CalculationModuleData(laminate, dataHolder.copy());
    }
}
