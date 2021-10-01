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

import java.beans.PropertyEditorSupport;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 *
 * @author raedel
 */
public class IntegerPropertyEditorSupport extends PropertyEditorSupport {
    
    private final static NumberFormat dFormat = new DecimalFormat("0");
    
    static{
        dFormat.setGroupingUsed(false);
    }
    
    public IntegerPropertyEditorSupport(){}
    
    public IntegerPropertyEditorSupport(Object source){
        super(source);
    }

    @Override
    public String getAsText() {
        Integer i = (Integer) getValue();
        if (i == null || i == 0) {
            return dFormat.format(0);
        }
        return dFormat.format(i);
    }

    @Override
    public void setAsText(String s) {
        try {
            setValue(dFormat.parse(s).intValue());
        } catch (ParseException ex) {
            setValue(0);
        }
    }
    
}
