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
package de.elamx.welcome;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Andreas Hauffe
 */
public class Constants {
    public static final int START_PAGE_MIN_WIDTH = 700;
    public static final int FONT_SIZE = 12;
    public static final String FONT_NAME = "Arial";
    
    public static final Font TAB_FONT = new Font( FONT_NAME, Font.BOLD, FONT_SIZE+1 ); //NOI18N
    public static final Font BUTTON_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE+1 );
    public static final Font RSS_DESCRIPTION_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE-1 );
    
    public static final int TEXT_INSETS_LEFT = 10;
    public static final int TEXT_INSETS_RIGHT = 10;
    
    public static final Color COLOR_HEADER = new Color(Integer.decode("0x1D2153")); //NOI18N
    public static final Color COLOR_RSS_DETAILS = new Color(Integer.decode("0x454545")); //NOI18N
    public static final Color COLOR_RSS_DATE = new Color(Integer.decode("0xCA6900")); //NOI18N
    public static final Color COLOR_LINK = new Color(Integer.decode("0x23569D")); //NOI18N
    
}
