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
package de.elamx.clt.springinui;

import de.elamx.clt.springin.SpringInInput;
import de.elamx.clt.springin.SpringInResult;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.modules.eLamXModuleData;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 *
 * @author Andreas Hauffe
 */
public class SpringInModuleData extends eLamXModuleData implements PropertyChangeListener {

    public static final String PROP_RESULT = "PROP_RESULT";

    private final SpringInInput input;
    private SpringInResult result = null;

    public SpringInModuleData(Laminat laminat) {
        this(laminat, new SpringInInput());
    }

    public SpringInModuleData(Laminat laminat, SpringInInput input) {
        super(laminat, NbBundle.getMessage(SpringInModuleData.class, "SpringInModule.name"));
        this.input = input;
        this.input.addPropertyChangeListener(WeakListeners.propertyChange(this, this.input));
    }

    public SpringInInput getSpringInInput() {
        return input;
    }

    /**
     * @return the result
     */
    public SpringInResult getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(SpringInResult result) {
        SpringInResult oldResult = this.result;
        this.result = result;
        firePropertyChange(PROP_RESULT, oldResult, result);
    }

    @Override
    public eLamXModuleData copy(Laminat laminate) {
        return new SpringInModuleData(laminate, input.copy());
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        eLamXLookup.getDefault().setModified(true);
    }

}
