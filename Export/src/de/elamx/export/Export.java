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
package de.elamx.export;

import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class Export<T extends ExportOptions> {
    
    private final Laminat laminat;
    private final T options;

    public Export(Laminat laminat, T options) {
        this.laminat = laminat;
        this.options = options;
    }
    
    public T getOptions(){
        return options;
    }
    
    public Laminat getLaminate(){
        return laminat;
    }
    
    public List<Material> getMaterialsList() {
        ArrayList<Material> materials = new ArrayList<>();

        boolean exists;
        for (Layer l : laminat.getLayers()) {
            Material mat = l.getMaterial();
            exists = false;
            for (Material m : materials) {
                if (m.equals(mat)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                materials.add(mat);
            }
        }
        return materials;
    }
    
    public ArrayList<Layer> getAllLayers(){
        ArrayList<Layer> layers = laminat.getLayers();
        ArrayList<Layer> layTemp = new ArrayList<>(layers.size());
        
        layTemp.addAll(layers);
        
        if (laminat.isSymmetric()){
            int start = layers.size()-1;
            if (laminat.isWithMiddleLayer()) {
                start--;
            }
            for (int ii = start; ii >= 0; ii--){
                layTemp.add(layers.get(ii));
            }
        }
        
        return layTemp;
    }

    public String export() {
        String exportString = "";

        exportString += exportMaterials();

        exportString += exportLaminate();

        return exportString;
    }
    
    public abstract String exportMaterials();
    
    public abstract String exportLaminate();
}
