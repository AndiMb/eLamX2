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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas
 */
public class StiffenersNode extends AbstractNode implements PropertyChangeListener {

    private final Input input;
    private boolean wasEmpty = false;

    public StiffenersNode(Input input) {
        super(input.getStiffenerProperties().isEmpty() ? Children.LEAF : Children.create(new StiffenerNodeFactory(input), false), Lookups.fixed(input));
        this.input = input;
        wasEmpty = input.getStiffenerProperties().isEmpty();
        input.addPropertyChangeListener(Input.PROP_STIFF_PROP, WeakListeners.propertyChange(this, input));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (input.getStiffenerProperties().isEmpty() != wasEmpty) {
            this.setChildren(input.getStiffenerProperties().isEmpty() ? Children.LEAF : Children.create(new StiffenerNodeFactory(input), false));
            wasEmpty = input.getStiffenerProperties().isEmpty();
        }
    }
}
