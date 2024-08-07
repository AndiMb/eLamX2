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
package de.elamx.clt.plateui.stiffenerui;

import de.elamx.clt.plate.Input;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author Andreas Hauffe
 */
public class StiffenerNodeFactory extends ChildFactory<StiffenerProperties> implements PropertyChangeListener{
    
    private final Input input;

    @SuppressWarnings("this-escape")
    public StiffenerNodeFactory(Input input) {
        this.input = input;
        input.addPropertyChangeListener(Input.PROP_STIFF_PROP, WeakListeners.propertyChange(this, input));
    }

    @Override
    protected boolean createKeys(List<StiffenerProperties> toPopulate) {
        for (StiffenerProperties prop : input.getStiffenerProperties()) {
            toPopulate.add(prop);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(StiffenerProperties key) {
        if (key instanceof StiffenerDefinitionService){
            return new StiffenerDefinitionNode((StiffenerDefinitionService)key);
        }
        return null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.refresh(true);
    }
    
}
