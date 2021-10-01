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
package de.elamx.export.lsdyna;

import de.elamx.export.Export;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author raedel
 */
public abstract class LSDynaExport extends Export<LSDynaExportOptions>{
    
    protected double ms_;
    protected double ts_;
    protected double ls_;
    
    protected String intformat1 = "      ----%10d%10d";
    protected String intformat2 = "%10.7f%10.4e%10d";
    
    protected final HashMap<Material, String> materialNums = new HashMap<>();
    
    public LSDynaExport(Laminat laminate, double ms, double ls, double ts) {
        super(laminate, new LSDynaExportOptions());
        ms_=ms;             //massenmultiplikator
        ls_=ls;             //Längenmultiplikator
        ts_=ts;             //Zeitmultiplikator
    }
    
    @Override
    public String exportLaminate(){
        
        String exportString = "";
        
        exportString += "*INTEGRATION_SHELL" + "\n";
        exportString += "$     irID       nIP      ESOP" + "\n";
        exportString += String.format(Locale.ENGLISH, intformat1, getLaminate().getNumberofLayers(), 0) + "\n";
        exportString += "$        S        wf       pid" + "\n";
        
        double tempt = 0.0;
        double t, location, weight;
        
        ArrayList<Layer> layers = getAllLayers();
        Layer layer;
        
        for (int ii = 0; ii < layers.size(); ii++) {
            layer = layers.get(ii);
            t        = layer.getThickness();  //und die lage und dickengewicht ermittelt
            location = 2*(0.5*t+tempt)/getLaminate().getThickness()-1.0;
            weight   = t/getLaminate().getThickness(); 
            
            exportString += String.format(Locale.ENGLISH, intformat2, location, weight, Integer.parseInt(materialNums.get(layer.getMaterial()))) + "\n";
            tempt += t;
        }
        exportString += "$\n";
        
        return exportString;
    }
}
