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
package de.elamx.core;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

/**
 *
 * @author Andreas Hauffe
 */
public class ELamXDecimalFormat extends DecimalFormat{
    
    public ELamXDecimalFormat(String pattern, DecimalFormatSymbols symbols) {
        super(pattern, symbols);
    }

    @Override
    public void applyPattern(String pattern){
        super.applyPattern(pattern);
        firePatternChange();
    }

    @Override
    public void setRoundingMode(RoundingMode rounding) {
        super.setRoundingMode(rounding);
        firePatternChange();
    }

    private void firePatternChange(){
        for (ELamXDecimalFormatListener listener : listenerList) {
            listener.patternChanged();
        }
    }
    
    private final ArrayList<ELamXDecimalFormatListener> listenerList = new ArrayList<>();
    
    public void addELamXDecimalFormatListener(ELamXDecimalFormatListener listener){
        listenerList.add(listener);
    }
    
    public void removeELamXDecimalFormatListener(ELamXDecimalFormatListener listener){
        listenerList.remove(listener);
    }
}
