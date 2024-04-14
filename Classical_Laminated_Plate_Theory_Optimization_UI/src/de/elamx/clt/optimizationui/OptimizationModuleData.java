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
package de.elamx.clt.optimizationui;

import de.elamx.clt.optimization.OptimizationInput;
import de.elamx.clt.optimization.OptimizationResult;
import de.elamx.clt.optimization.Optimizer;
import de.elamx.laminate.ELamXObject;
import de.elamx.laminate.eLamXLookup;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.UUID;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class OptimizationModuleData extends ELamXObject implements PropertyChangeListener {

    private final OptimizationInput input;

    private OptimizationResult result = null;
    private Optimizer optimizer;
    private int angleType = 0;
    public static final String PROP_RESULT = "PROP_RESULT";
    public static final String PROP_OPTIMIZER = "OPTIMIZER";
    public static final String PROP_ANGLETYPE = "ANGLETYPE";

    @SuppressWarnings("this-escape")
    public OptimizationModuleData(OptimizationInput input, boolean addToLookup) {
        super(UUID.randomUUID().toString(), NbBundle.getMessage(OptimizationModuleData.class, "OptimizationModule.name"), addToLookup);
        this.input = input;
        this.input.addPropertyChangeListener(this);
    }

    public OptimizationInput getOptimizationInput() {
        return input;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        eLamXLookup.getDefault().setModified(true);
    }

    /**
     * @return the result
     */
    public OptimizationResult getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(OptimizationResult result) {
        OptimizationResult oldResult = this.result;
        this.result = result;
        firePropertyChange(PROP_RESULT, oldResult, result);
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(Optimizer optimizer) {
        Optimizer oldOptimizer = this.optimizer;
        this.optimizer = optimizer;
        firePropertyChange(PROP_OPTIMIZER, oldOptimizer, this.optimizer);
    }

    public int getAngleType() {
        return angleType;
    }

    public void setAngleType(int angleType) {
        int oldAngleType = this.angleType;
        this.angleType = angleType;
        firePropertyChange(PROP_ANGLETYPE, oldAngleType, this.angleType);
    }

    public OptimizationModuleData getCopy() {
        return getCopy(true);
    }

    public OptimizationModuleData getCopy(boolean addToLookup) {
        OptimizationModuleData data = new OptimizationModuleData(input.copy(), addToLookup);
        data.optimizer = this.optimizer;
        return data;
    }

    @Override
    public int getUpdatePriority() {
        return 0;
    }
}
