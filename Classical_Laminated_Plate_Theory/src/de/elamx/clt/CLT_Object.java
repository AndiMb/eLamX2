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
package de.elamx.clt;

import de.elamx.laminate.DependingObject;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class CLT_Object implements DependingObject{
    
    private EventListenerList listenerList_ = new EventListenerList();
    
    public void addCLTRefreshListener(CLTRefreshListener listener){
        listenerList_.add(CLTRefreshListener.class, listener);
    }
    
    public void removeCLTRefreshListener(CLTRefreshListener listener){
        listenerList_.remove(CLTRefreshListener.class, listener);
    }
    
    protected void fireRefreshed(){
        Object[] listeners = listenerList_.getListenerList();
        
        for (int i = listeners.length-2; i>=0; i-=2){
            if (listeners[i]==CLTRefreshListener.class){
                ((CLTRefreshListener)listeners[i+1]).refreshed();
            }
        }
    }
    
    /**
     *
     */
    public abstract void refresh();
    
    @Override
    public void update(){
        refresh();
        fireRefreshed();
    }
}
