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

import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Material;
import java.io.File;
import java.util.Date;

/**
 *
 * @author Florian Dexl
 */
public interface HDF5OutputWriterService {
    
    public void writeHeader(IHDF5Writer hdf5writer, File inputFile, String inputFileMD5, Date date);
    
    public void writeLaminateInformation(IHDF5Writer hdf5writer, Laminat laminate);
    
    public void writeMaterialInformation(IHDF5Writer hdf5writer, Material material);
}
