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
package de.elamx.core.propertyeditor;

import de.elamx.core.ELamXDecimalFormat;
import de.elamx.core.GlobalProperties;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;

/**
 *
 * @author Andreas Hauffe
 */
public class PositionPropertyEditorSupport extends PropertyEditorSupport {
    
    private final static ELamXDecimalFormat df = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);

    @Override
    public String getAsText() {
        Double d = (Double) getValue();
        if (d == null || d == 0.0) {
            return df.format(0.0);
        }
        return df.format(d);
    }

    @Override
    public void setAsText(String s) {
        try {
            setValue(df.parse(s).doubleValue());
        } catch (ParseException ex) {
            setValue(0.0);
        }
    }
}
