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

import de.elamx.utilities.BrowserLauncher;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Map;
import javax.swing.Action;
import javax.swing.UIManager;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.modules.Places;
import org.openide.util.Exceptions;

/**
 *
 * @author S. Aubrecht
 */
public class Utils {

    /** Creates a new instance of Utils */
    private Utils() {
    }

    public static Graphics2D prepareGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Map<?,?> rhints = (Map<?,?>)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints")); //NOI18N
        if( rhints == null && Boolean.getBoolean("swing.aatext") ) { //NOI18N
             g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        } else if( rhints != null ) {
            g2.addRenderingHints( rhints );
        }
        return g2;
    }

    public static void showURL(String href) {
        try {
            BrowserLauncher.browse(href);
        } catch (IOException | URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
        /*try {
            HtmlBrowser.URLDisplayer displayer = HtmlBrowser.URLDisplayer.getDefault();
            if (displayer != null) {
                displayer.showURL(new URL(href));
            }
        } catch (Exception e) {}*/
    }

    static int getDefaultFontSize() {
        Integer customFontSize = (Integer)UIManager.get("customFontSize"); // NOI18N
        if (customFontSize != null) {
            return customFontSize.intValue();
        } else {
            Font systemDefaultFont = UIManager.getFont("TextField.font"); // NOI18N
            return (systemDefaultFont != null)
                ? systemDefaultFont.getSize()
                : 11;
        }
    }

    public static Action findAction( String key ) {
        FileObject fo = FileUtil.getConfigFile(key);
        
        if (fo != null && fo.isValid()) {
            try {
                DataObject dob = DataObject.find(fo);
                InstanceCookie ic = dob.getCookie(InstanceCookie.class);
                
                if (ic != null) {
                    Object instance = ic.instanceCreate();
                    if (instance instanceof Action) {
                        Action a = (Action) instance;
                        return a;
                    }
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
                return null;
            }
        }
        return null;
    }
    
    public static Color getColor( String resId ) {
        if (resId.equals("RssDateTimeColor")){
            Integer rgb = Integer.decode("0xCA6900");
            return new Color(rgb.intValue());
        }else if (resId.equals("RssDetailsColor")){
            Integer rgb = Integer.decode("0x454545");
            return new Color(rgb.intValue());
        }else if (resId.equals("HeaderForegroundColor")){
            Integer rgb = Integer.decode("0x1D2153");
            return new Color(rgb.intValue());
        }else if (resId.equals("RssDetailsColor")){
            Integer rgb = Integer.decode("0x454545");
            return new Color(rgb.intValue());
        }
        return Color.BLACK;
    }

    public static Color getLinkColor() {
        Color res = UIManager.getColor("nb.html.link.foreground"); //NOI18N
        if( null == res )
            res = getColor( Constants.LINK_COLOR );
        return res;
    }

    public static Color getFocusedLinkColor() {
        Color res = UIManager.getColor("nb.html.link.foreground.focus"); //NOI18N
        if( null == res )
            res = getColor( Constants.LINK_IN_FOCUS_COLOR );
        return res;
    }

    public static Color getVisitedLinkColor() {
        Color res = UIManager.getColor("nb.html.link.foreground.visited"); //NOI18N
        if( null == res )
            res = getColor( Constants.VISITED_LINK_COLOR );
        return res;
    }

    public static Color getBottomBarColor() {
        Color res = UIManager.getColor("nb.startpage.bottombar.background"); //NOI18N
        if( null == res )
            res = getColor( Constants.COLOR_BOTTOM_BAR );
        return res;
    }

    public static Color getTopBarColor() {
        Color res = UIManager.getColor("nb.startpage.topbar.background"); //NOI18N
        if( null == res )
            res = getColor( Constants.COLOR_TAB_BACKGROUND );
        return res;
    }

    public static Color getBorderColor() {
        Color res = UIManager.getColor("nb.startpage.border.color"); //NOI18N
        if( null == res )
            res = getColor( Constants.COLOR_BORDER );
        return res;
    }

    public static Color getTabBorder1Color() {
        Color res = UIManager.getColor("nb.startpage.tab.border1.color"); //NOI18N
        if( null == res )
            res = getColor( Constants.COLOR_TAB_BORDER1 );
        return res;
    }

    public static Color getTabBorder2Color() {
        Color res = UIManager.getColor("nb.startpage.tab.border2.color"); //NOI18N
        if( null == res )
            res = getColor( Constants.COLOR_TAB_BORDER2 );
        return res;
    }

    public static Color getRssHeaderColor() {
        Color res = UIManager.getColor("nb.startpage.rss.header.color"); //NOI18N
        if( null == res )
            res = getColor( Constants.COLOR_HEADER );
        return res;
    }

    public static Color getRssDetailsColor() {
        Color res = UIManager.getColor("nb.startpage.rss.details.color"); //NOI18N
        if( null == res )
            res = getColor( Constants.COLOR_RSS_DETAILS );
        return res;
    }

    public static Color getRssDateColor() {
        Color res = UIManager.getColor("nb.startpage.rss.date.color"); //NOI18N
        if( null == res )
            res = getColor( Constants.COLOR_RSS_DATE );
        return res;
    }

    public static boolean isDefaultButtons() {
        return UIManager.getBoolean( "nb.startpage.defaultbuttonborder" ); //NOI18N
    }

    public static boolean isSimpleTabs() {
        return UIManager.getBoolean( "nb.startpage.simpletabs" ); //NOI18N
    }

    public static File getCacheStore() throws IOException {
        return Places.getCacheSubdirectory("welcome"); // NOI18N
    }

    /**
     * Try to extract the URL from the given DataObject using reflection.
     * (The DataObject should be URLDataObject in most cases)
     */
    public static String getUrlString(DataObject dob) {
        try {
            Method m = dob.getClass().getDeclaredMethod( "getURLString", new Class<?>[] {} ); //NOI18N
            m.setAccessible( true );
            Object res = m.invoke( dob );
            if( null != res ) {
                return res.toString();
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
    }
}