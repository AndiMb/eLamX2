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
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.openide.util.NbPreferences;

/**
 *
 * @author Andreas Hauffe
 */
public class GlobalProperties{
    
    public static final String FORMAT_DOUBLE        = "Format.Double";
    public static final String FORMAT_SMALL_DOUBLE  = "Format.SmallDouble";
    public static final String FORMAT_STIFFNESS     = "Format.Stiffness";
    public static final String FORMAT_INV_STIFFNESS = "Format.InverseStiffness";
    public static final String FORMAT_POISSONRATIO  = "Format.PoissonRatio";
    public static final String FORMAT_THICKNESS     = "Format.Thickness";
    public static final String FORMAT_YIELDSTRESS   = "Format.YieldStress";
    public static final String FORMAT_ANGLE         = "Format.Angle";
    public static final String FORMAT_DENSITY       = "Format.Density";
    public static final String FORMAT_HYGROTHERMCOEFF = "Format.HygroThermCoefficient";
    public static final String FORMAT_NONDIMDMATPARAM = "Format.NondimDmatParam";
    public static final String FORMAT_FORCE           = "Format.Force";
    public static final String FORMAT_STRAIN          = "Format.Strain";
    public static final String FORMAT_STRESS          = "Format.Stress";
    public static final String FORMAT_DISPLACEMENT    = "Format.Displacement";
    public static final String FORMAT_FREQUENCY       = "Format.Frequency";
    public static final String FORMAT_RESERVE_FACTOR  = "Format.ReserveFactor";
    public static final String FORMAT_PERCENT         = "Format.Percent";
    public static final String FORMAT_EIGENVALUE      = "Format.Eigenvalue";
    public static final String FORMAT_TEMPERATURE     = "Format.Temperature";
    public static final String USE_enUS_LOCALE        = "use_en-US_locale";
    public static final String SHOW_TRANSVERSAL_SHEAR = "show_transversal_shear";
    public static final String INVERT_Z_DEFAULT       = "invert_z_default";
    public static final String RESERVE_FACTOR_ROUND_DOWN = "reserve_factor_round_down";
    
    private final HashMap<String, ELamXDecimalFormat> formats = new HashMap<>();
    
    private static final GlobalProperties instance = new GlobalProperties();
    
    private boolean useENUS;
    private boolean showTransShear;

    private boolean invertZDefault;
    private boolean reserveFactorRoundDown;
    
    private boolean headless = false;
    
    private GlobalProperties(){
        init();
    }
    
    public static GlobalProperties getDefault(){
        return instance;
    }

    public boolean isUseENUS() {
        return useENUS;
    }
    
    private void init(){
        useENUS = Boolean.parseBoolean(NbPreferences.forModule(GlobalProperties.class).get(USE_enUS_LOCALE, Boolean.toString(false)));
        
        Locale locale = Locale.getDefault();
        if (useENUS){
            locale = Locale.forLanguageTag("en-US");
        }
        
        showTransShear = Boolean.parseBoolean(NbPreferences.forModule(GlobalProperties.class).get(SHOW_TRANSVERSAL_SHEAR, Boolean.toString(false)));
        invertZDefault = Boolean.parseBoolean(NbPreferences.forModule(GlobalProperties.class).get(INVERT_Z_DEFAULT, Boolean.toString(false)));
        
        formats.put(FORMAT_DOUBLE, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_DOUBLE, "0.0#####"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_SMALL_DOUBLE, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_SMALL_DOUBLE, "0.0###E0"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_STIFFNESS, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_STIFFNESS, "0.0"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_INV_STIFFNESS, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_INV_STIFFNESS, "0.0###E0"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_POISSONRATIO, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_POISSONRATIO, "0.000"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_THICKNESS, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_THICKNESS, "0.0##"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_YIELDSTRESS, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_YIELDSTRESS, "0.0"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_ANGLE, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_ANGLE, "0.0"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_DENSITY, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_DENSITY, "0.0###E0"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_HYGROTHERMCOEFF, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_HYGROTHERMCOEFF, "0.0###E0"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_NONDIMDMATPARAM, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_NONDIMDMATPARAM, "0.0#####"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_FORCE, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_FORCE, "0.0##"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_STRAIN, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_STRAIN, "0.0#####"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_STRESS, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_STRESS, "0.0##"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_DISPLACEMENT, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_DISPLACEMENT, "0.0##"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_FREQUENCY, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_FREQUENCY, "0.0###E0"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_RESERVE_FACTOR, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_RESERVE_FACTOR, "0.0##"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_PERCENT, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_PERCENT, "0.00##"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_EIGENVALUE, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_EIGENVALUE, "0.00##"), new DecimalFormatSymbols(locale)));
        formats.put(FORMAT_TEMPERATURE, new ELamXDecimalFormat(NbPreferences.forModule(GlobalProperties.class).get(FORMAT_TEMPERATURE, "0.0"), new DecimalFormatSymbols(locale)));
        
        reserveFactorRoundDown = Boolean.parseBoolean(NbPreferences.forModule(GlobalProperties.class).get(RESERVE_FACTOR_ROUND_DOWN, Boolean.toString(false)));
        if (reserveFactorRoundDown) formats.get(FORMAT_RESERVE_FACTOR).setRoundingMode(RoundingMode.DOWN);
    }
    
    public ELamXDecimalFormat getFormat(String format){
        return formats.get(format);
    }
    
    public void useUSLocale(boolean use){
        this.useENUS = use;
        Locale locale = getActualLocale();
        for(Map.Entry<String, ELamXDecimalFormat> entry : formats.entrySet()){
            entry.getValue().setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
        }
    }
    
    public Locale getActualLocale(){
        Locale locale = Locale.getDefault();
        if (useENUS){
            locale = Locale.forLanguageTag("en-US");
        }
        return locale;
    }

    public boolean isShowTransShear() {
        return showTransShear;
    }

    public void setShowTransShear(boolean showTransShear) {
        this.showTransShear = showTransShear;
    }

    public boolean isInvertZDefault() {
        return invertZDefault;
    }

    public void setInvertZDefault(boolean invertZDefault) {
        this.invertZDefault = invertZDefault;
    }

    public boolean isReserveFactorRoundDown() {
        return reserveFactorRoundDown;
    }

    public void setReserveFactorRoundDown(boolean reserveFactorRoundDown) {
        this.reserveFactorRoundDown = reserveFactorRoundDown;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }
}
