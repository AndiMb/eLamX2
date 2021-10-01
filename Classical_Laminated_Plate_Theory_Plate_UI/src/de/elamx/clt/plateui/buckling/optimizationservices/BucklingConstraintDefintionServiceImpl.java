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
package de.elamx.clt.plateui.buckling.optimizationservices;

import de.elamx.clt.plate.MinimalBucklingReserveFactorImpl;
import de.elamx.clt.plateui.buckling.BucklingModuleData;
import de.elamx.laminate.optimization.ConstraintDefinitionService;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = ConstraintDefinitionService.class)
public class BucklingConstraintDefintionServiceImpl extends ConstraintDefinitionService {

    public BucklingConstraintDefintionServiceImpl() {
        this(NbBundle.getMessage(BucklingModuleData.class, "BucklingModule.name"));
    }

    public BucklingConstraintDefintionServiceImpl(String name) {
        super(name);
    }

    @Override
    public Node getNewNode() {
        return new BucklingConstraintDefinitionNode();
    }

    @Override
    public String getMRFCClassName() {
        return MinimalBucklingReserveFactorImpl.class.getName();
    }

    @Override
    public Node getNode(MinimalReserveFactorCalculator calculator) {
        if (calculator instanceof MinimalBucklingReserveFactorImpl) {
            return new BucklingConstraintDefinitionNode((MinimalBucklingReserveFactorImpl) calculator);
        }
        return null;
    }

}
