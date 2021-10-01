/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.elamx.welcome.feed;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 *
 * @author S. Aubrecht
 */
public interface Constants {

    static final String COLOR_SECTION_HEADER = "SectionHeaderColor"; //NOI18N
    static final String COLOR_BIG_BUTTON = "BigButtonColor"; //NOI18N
    static final String COLOR_BOTTOM_BAR = "BottomBarColor"; //NOI18N
    static final String COLOR_BORDER = "BorderColor"; //NOI18N
    static final String COLOR_TAB_BACKGROUND = "TabBackgroundColor"; //NOI18N
    static final String COLOR_TAB_BORDER1 = "TabBorder1Color"; //NOI18N
    static final String COLOR_TAB_BORDER2 = "TabBorder2Color"; //NOI18N

    static final String COLOR_RSS_DATE = "RssDateTimeColor"; //NOI18N
    static final String COLOR_RSS_DETAILS = "RssDetailsColor"; //NOI18N
    static final String COLOR_HEADER = "HeaderForegroundColor"; //NOI18N
    
    static final int FONT_SIZE = Utils.getDefaultFontSize()+1;
    static final String FONT_NAME = "Arial"; //NOI18N
    static final Font BUTTON_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE+1 );
    static final Font RSS_DESCRIPTION_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE-1 );
    static final Font TAB_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE+1 ); //NOI18N
    static final Font SECTION_HEADER_FONT = new Font( FONT_NAME, Font.BOLD, FONT_SIZE+7 ); //NOI18N
    static final Font GET_STARTED_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE+1 ) ; //NOI18N
    static final Font CONTENT_HEADER_FONT = new Font( FONT_NAME, Font.BOLD, FONT_SIZE+13 ) ; //NOI18N

    static final Stroke LINK_IN_FOCUS_STROKE = new BasicStroke(1, BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_BEVEL, 0, new float[] {0, 2}, 0);
    static final String LINK_IN_FOCUS_COLOR = "LinkInFocusColor"; //NOI18N
    static final String LINK_COLOR = "LinkColor"; //NOI18N
    static final String VISITED_LINK_COLOR = "VisitedLinkColor"; //NOI18N

    static final int RSS_FEED_TIMER_RELOAD_MILLIS = 60*60*1000;

    static final int TEXT_INSETS_LEFT = 10;
    static final int TEXT_INSETS_RIGHT = 10;

    static final Border HEADER_TEXT_BORDER = BorderFactory.createEmptyBorder( 1, TEXT_INSETS_LEFT, 1, TEXT_INSETS_RIGHT );
    
    static final int START_PAGE_MIN_WIDTH = 700;
}