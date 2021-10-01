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
package de.elamx.export.Nastran;

import java.util.Locale;

/**
 *
 * @author Andreas Hauffe
 */
public class FixedFloat {

    public static String getString(double value, int digits){
        String s = "" + value;

        if (s.length() <= digits){
            if (s.length() < digits){
                String s2 = "";
                for (int ii = 0; ii < digits - s.length(); ii++){
                    s2 += " ";
                }
                s = s2+s;
            }
            return s;
        }

        if (value >= 0.0){
            if (value >= 1.0){
                double maxFloatVal = 0.0;

                double tFac = 1.0;
                for (int ii = 0; ii < digits-1; ii++){
                    maxFloatVal += 9.0*tFac;
                    tFac *= 10.0;
                }
                if (value <= maxFloatVal){
                    s = "" + value;
                    return s.length() > digits ? s.substring(0, digits) : s;
                }else{
                    return String.format(Locale.ENGLISH,"%" + digits + "." + (digits-6) + "E", value);
                }
            }else{
                s = String.format(Locale.ENGLISH,"%" + (digits+1) + "." + (digits-1) + "f", value).substring(1);
                int zeros = 0;
                for (int ii = 2; ii < s.length(); ii++){
                    if (s.charAt(ii) == '0'){
                        zeros++;
                    }else{
                        break;
                    }
                }
                if (digits - zeros - 1 < digits + 1 - 5){
                    //return String.format(Locale.ENGLISH,"%" + (digits+1) + "." + (digits-5) + "E", value).substring(1);
                    return String.format(Locale.ENGLISH,"%" + (digits) + "." + (digits-6) + "E", value);
                }
                return s;
            }
        }else{
            return "-" + getString(-value, digits-1);
        }

        /*s = "";
        for (int ii = 0; ii < digits; ii++){
            s += "*";
        }
        return s;*/
    }
}
