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

import de.elamx.utilities.BrowserLauncher;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JLabel;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;

/**
 *
 * @author Andreas Hauffe
 */
public class Logo extends JLabel implements MouseListener{
    
    private final String url;
    
    public Logo(){
        this("no url");
    }
    
    @SuppressWarnings("this-escape")
    public Logo(String url){
        this.url = url;
        addMouseListener( this );
        setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            BrowserLauncher.browse(url);
        } catch (IOException | URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        StatusDisplayer.getDefault().setStatusText( url );
    }

    @Override
    public void mouseExited(MouseEvent e) {
        StatusDisplayer.getDefault().setStatusText( null );
    }
}
