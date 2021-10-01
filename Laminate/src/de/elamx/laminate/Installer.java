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
package de.elamx.laminate;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        FileObject addValuesFolder = FileUtil.getConfigFile("elamx/additionalMaterialValues");
        for (FileObject addValueFO : addValuesFolder.getChildren()){
            double defaultValue = Double.parseDouble((String)addValueFO.getAttribute("defaultValue"));
            String displayName  = (String)addValueFO.getAttribute("displayName");
            String htmlName  = (String)addValueFO.getAttribute("htmlName");
            String description  = (String)addValueFO.getAttribute("description");
            double minValue = Double.parseDouble((String)addValueFO.getAttribute("minValue"));
            double maxValue = Double.parseDouble((String)addValueFO.getAttribute("maxValue"));
            Material.putDefaultAdditionlValue(addValueFO.getName(), defaultValue, displayName, htmlName, description, minValue, maxValue);
        }
    }
}
