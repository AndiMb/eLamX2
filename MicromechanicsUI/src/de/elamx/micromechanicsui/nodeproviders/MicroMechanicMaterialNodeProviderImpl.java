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
package de.elamx.micromechanicsui.nodeproviders;

import de.elamx.fileview.MaterialNodeProvider;
import de.elamx.laminate.Material;
import de.elamx.laminate.eLamXLookup;
import de.elamx.micromechanics.MicroMechanicMaterial;
import de.elamx.micromechanicsui.nodes.MicroMechanicMaterialNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = MaterialNodeProvider.class)
public class MicroMechanicMaterialNodeProviderImpl implements MaterialNodeProvider{

    @Override
    public Material[] getMaterials() {
        return eLamXLookup.getDefault().lookupAll(MicroMechanicMaterial.class).toArray(new MicroMechanicMaterial[0]);
    }

    @Override
    public Node getNodes(Material mat) {
        if (mat instanceof MicroMechanicMaterial) {
            return new MicroMechanicMaterialNode((MicroMechanicMaterial)mat);
        }
        return null;
    }
    
}
