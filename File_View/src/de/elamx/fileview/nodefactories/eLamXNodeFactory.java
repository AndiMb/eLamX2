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
package de.elamx.fileview.nodefactories;

import de.elamx.fileview.ELamXNodeProvider;
import de.elamx.laminate.eLamXLookup;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Andreas Hauffe
 */
public class eLamXNodeFactory extends ChildFactory<ELamXNodeProvider> implements PropertyChangeListener{

    @SuppressWarnings("this-escape")
    public eLamXNodeFactory() {
        eLamXLookup.getDefault().addPropertyChangeListener(this);
    }

    @Override
    protected boolean createKeys(List<ELamXNodeProvider> list) {
        if (eLamXLookup.getDefault().getFileObject() == null) return true;
        for (ELamXNodeProvider eLamXNodeProvider : Lookup.getDefault().lookupAll(ELamXNodeProvider.class)){
            list.add(eLamXNodeProvider);
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(list, new Comparator<ELamXNodeProvider>(){
            @Override
            public int compare(ELamXNodeProvider o1, ELamXNodeProvider o2) {
                return Integer.compare(o1.getPosition(), o2.getPosition());
            }
        });
        return true;
    }   
    
    @Override
    protected Node createNodeForKey(ELamXNodeProvider key) {
        if (key == null){
            return null;
        }
        return key.getNode();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(eLamXLookup.PROP_FILEOBJECT)){
            this.refresh(true);
        }
    }
}
