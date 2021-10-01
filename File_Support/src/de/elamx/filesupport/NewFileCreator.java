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
package de.elamx.filesupport;

import de.elamx.laminate.eLamXLookup;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class NewFileCreator{

    private static AtomicInteger _integer = new AtomicInteger(0);
    
    public static void create(){
        try {
            URL ulr = NewFileCreator.class.getResource("/de/elamx/filesupport/emptyeLamXFile.elamx");
            FileObject fo = URLMapper.findFileObject(ulr);
            DataObject template = DataObject.find(fo);
            FileSystem memFS = FileUtil.createMemoryFileSystem();
            FileObject root = memFS.getRoot();
            DataFolder dataFolder = DataFolder.findFolder(root);
            DataObject gdo = template.createFromTemplate(
                    dataFolder,
                    NbBundle.getMessage(NewFileCreator.class, "NewFile.name") + _integer.incrementAndGet());
            eLamXLookup.getDefault().setFileObject(gdo.getPrimaryFile());
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
