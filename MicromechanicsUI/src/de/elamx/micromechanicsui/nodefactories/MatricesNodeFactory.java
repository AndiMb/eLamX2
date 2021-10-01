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
package de.elamx.micromechanicsui.nodefactories;

import de.elamx.laminate.eLamXLookup;
import de.elamx.micromechanics.Matrix;
import de.elamx.micromechanicsui.nodes.MatrixNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Andreas Hauffe
 */
public class MatricesNodeFactory extends ChildFactory<Matrix> implements LookupListener{

    private Lookup.Result<Matrix> result = null;
    
    public MatricesNodeFactory() {
        result = eLamXLookup.getDefault().lookupResult(Matrix.class);
        result.addLookupListener(this);
    }
    
    @Override
    protected boolean createKeys(List<Matrix> list) {
        list.addAll(result.allInstances());
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(list, new Comparator<Matrix>(){
            @Override
            public int compare(Matrix o1, Matrix o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return true;
    }

    @Override
    protected Node createNodeForKey(Matrix key) {
        return new MatrixNode(key);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        this.refresh(true);
    }
    
}
