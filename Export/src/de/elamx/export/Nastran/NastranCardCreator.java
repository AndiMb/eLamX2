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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 *
 * @author Andreas Hauffe
 */
public class NastranCardCreator {
    
    public static final int SMALL_FORMAT = 0;
    public static final int LARGE_FORMAT = 1;
    public static final int FREE_FORMAT  = 2;
    
    private static int defaultFormat = LARGE_FORMAT;
    
    private String cardName;
    private int format;
    private ArrayList<Entry> entries = new ArrayList<>();

    public static void setDefaultFormat(int defaultFormat) {
        NastranCardCreator.defaultFormat = defaultFormat;
    }

    public NastranCardCreator(String cardName) {
        this(defaultFormat, cardName);
    }

    public NastranCardCreator(int format, String cardName) {
        this.format = format;
        this.cardName = cardName;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }
    
    /**
     * Hier wird ein Integerwert für eine Nastrankarte übergeben. Dabei wird als 
     * Basis das small Format verwendet. Die Liniennummer sollte immer größer
     * Null sein und die Spaltennummer einen Wert von 2 und 9 haben.
     * @param line
     * @param column
     * @param value 
     */
    public void addInt(int line, int column, int value){
        assert (column < 10 && column > 1) : "Column in NastranCardCreator is not between 1 and 10";
        entries.add(new Entry(line, column, value));
    }
    
    /**
     * Hier wird ein Stringwert für eine Nastrankarte übergeben. Dabei wird als 
     * Basis das small Format verwendet. Die Liniennummer sollte immer größer
     * Null sein und die Spaltennummer einen Wert von 2 und 9 haben.
     * @param line
     * @param column
     * @param value 
     */
    public void addString(int line, int column, String value){
        assert (column < 10 && column > 1) : "Column in NastranCardCreator is not between 1 and 10";
        assert value.length() < 9 : "String length for a String value in NastranCardCreator is too long.";
        if (value.length() > 8){
            value = value.substring(0, 8);
        }
        entries.add(new Entry(line, column, value));
    }
    
    /**
     * Hier wird ein Realwert für eine Nastrankarte übergeben. Dabei wird als 
     * Basis das small Format verwendet. Die Liniennummer sollte immer größer
     * Null sein und die Spaltennummer einen Wert von 2 und 9 haben.
     * @param line
     * @param column
     * @param value 
     */
    public void addReal(int line, int column, double value){
        assert (column < 10 && column > 1) : "Column in NastranCardCreator is not between 1 and 10";
        entries.add(new Entry(line, column, value));
    }
    
    public String getCard(){
        Collections.sort(entries);
        switch(format){
            case SMALL_FORMAT:
                return getSmallFormatCard();
            case LARGE_FORMAT:
                return getLargeFormatCard();
            default:
                return getSmallFormatCard();
        }
    }
    
    private String getSmallFormatCard(){
        String card = String.format(Locale.ENGLISH, "%-8s", cardName);
        
        int actColumn = 2;
        int actLine   = 1;
        
        for (Entry e : entries){
            if (actColumn == 10){
                card += getLineBreak(false);
                actColumn = 2;
                actLine++;
            }
            assert (actLine <= e.line || actColumn <= e.column) : "Mist, beim Rausschreiben einer " + cardName + "-Karte funktioniert was nicht! (" + actLine + ", " +  e.line + ", " + actColumn + ", " +  e.column + ")";
            if (actColumn != e.column || actLine != e.line){
                while (actColumn != e.column || actLine != e.line){
                    card += "        ";
                    actColumn++;
                    if (actColumn == 10){
                        card += getLineBreak(false);
                        actColumn = 2;
                        actLine++;
                    }
                }
            }
            if (e.getVal() instanceof String){
                card += String.format(Locale.ENGLISH, "%-8s", (String)e.getVal());
            }else if (e.getVal() instanceof Integer){
                card += String.format(Locale.ENGLISH, "%8d", (Integer)e.getVal());
            }else if (e.getVal() instanceof Double){
                card += FixedFloat.getString(((Double)e.val), 8);
            }
            actColumn++;
        }
        
        return card;
    }
    
    private String getLargeFormatCard(){
        String card = String.format(Locale.ENGLISH, "%-8s", cardName.length() < 8 ? cardName + "*" : cardName.substring(0, 7) + "*");
        
        int actColumn = 2;
        int actLine   = 1;
        boolean inLine = true;
        
        for (Entry e : entries){
            if (actColumn == 6){
                card += getLongFormatLineBreak(inLine);
                actColumn = 2;
                actLine++;
                inLine = !inLine;
            }
            int eColumn = e.column < 6 ? e.column : e.column-4;
            int eLine = e.column < 6 ? e.line*2-1 : e.line*2;
            assert (actLine <= eLine || actColumn <= eColumn) : "Mist, beim Rausschreiben einer " + cardName + "-Karte funktioniert was nicht!";
            if (actColumn != eColumn || actLine != eLine){
                while (actColumn != eColumn || actLine != eLine){
                    card += "                ";
                    actColumn++;
                    if (actColumn == 6){
                        card += getLongFormatLineBreak(inLine);
                        actColumn = 2;
                        actLine++;
                        inLine = !inLine;
                    }
                }
            }
            if (e.getVal() instanceof String){
                card += String.format(Locale.ENGLISH, "%-16s", (String)e.getVal());
            }else if (e.getVal() instanceof Integer){
                card += String.format(Locale.ENGLISH, "%16d", (Integer)e.getVal());
            }else if (e.getVal() instanceof Double){
                card += FixedFloat.getString(((Double)e.val), 16);
            }
            actColumn++;
        }
        if (actLine%2 > 0){
            while (actColumn != 6) {
                card += "                ";
                actColumn++;
            }
            card += getLongFormatLineBreak(true);
        }
        
        return card;
    }
    
    private String getLongFormatLineBreak(boolean inLine){
        return "*\n*       ";
    }
    
    private String getLineBreak(boolean inLine){
        if (inLine) return "*\n*       ";
        return "+\n+       ";
    }
    
    private class Entry implements Comparable<Entry>{
        
        private final Object val;
        private final int line;
        private final int column;

        public Entry(int line, int column, Object val) {
            this.val = val;
            this.line = line;
            this.column = column;
        }

        public int getColumn() {
            return column;
        }

        public int getLine() {
            return line;
        }

        public Object getVal() {
            return val;
        }

        @Override
        public int compareTo(Entry o) {
            int returnVal = Integer.compare(line, o.line);
            if (returnVal == 0){
                returnVal = Integer.compare(column, o.column);
            }
            return returnVal;
        }
    } 
    
}
