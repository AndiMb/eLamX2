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
package de.elamx.hdf5.output;

import ch.systemsx.cisd.hdf5.HDF5CompoundType;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.clt.CLT_Laminate;
import de.elamx.core.HDF5OutputWriterService;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Florian Dexl
 */
@ServiceProvider(service = HDF5OutputWriterService.class, position = 1000)
public class HDF5OutputWriterServiceImpl implements HDF5OutputWriterService {

    @Override
    public void writeHeader(IHDF5Writer hdf5writer, File inputFile) {
        hdf5writer.object().createGroup("materials");
        hdf5writer.object().createGroup("laminates");

        if (inputFile != null) {
            byte[] data = null;
            try {
                hdf5writer.string().write("input file", FileUtils.readFileToString(inputFile));
                data = FileUtils.readFileToByteArray(inputFile);
            } catch (IOException ex) {
                Logger.getLogger(HDF5OutputWriterServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            byte[] hash = null;
            try {
                hash = MessageDigest.getInstance("MD5").digest(data);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(HDF5OutputWriterServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            String checksum = new BigInteger(1, hash).toString(16);
            hdf5writer.string().setAttr("input file", "md5 checksum", checksum);
            hdf5writer.string().setAttr("input file", "filename", inputFile.getName());
            hdf5writer.string().setAttr("input file", "absolute path", inputFile.getAbsolutePath());
        }
    }

    @Override
    public void writeLaminateInformation(IHDF5Writer hdf5writer, Laminat laminate) {

        CLT_Laminate clt_laminate = laminate.getLookup().lookup(CLT_Laminate.class);
        if (clt_laminate == null) {
            clt_laminate = new CLT_Laminate(laminate);
        }

        String groupName = "laminates/".concat(laminate.getName());
        hdf5writer.object().createGroup(groupName);

        hdf5writer.bool().setAttr("/".concat(groupName), "symmetric", laminate.isSymmetric());
        hdf5writer.int32().setAttr("/".concat(groupName), "total number of layers", laminate.getNumberofLayers());
        hdf5writer.float64().setAttr("/".concat(groupName), "total thickness", clt_laminate.getTges());

        hdf5writer.float64().createMatrix("/".concat(groupName).concat("/ABD-Matrix"), 6, 6);
        hdf5writer.float64().writeMatrix("/".concat(groupName).concat("/ABD-Matrix"), clt_laminate.getABDMatrix());

        hdf5writer.object().createGroup("/".concat(groupName).concat("/effective stiffness"));
        hdf5writer.object().createGroup("/".concat(groupName).concat("/effective stiffness/with poisson effect"));

        ArrayList<Double> effectiveStiffnessValuesArrayList = new ArrayList<>();
        ArrayList<String> effectiveStiffnessNamesArrayList = new ArrayList<>();

        effectiveStiffnessValuesArrayList.add(clt_laminate.getExSimple());
        effectiveStiffnessNamesArrayList.add("Exx");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getEySimple());
        effectiveStiffnessNamesArrayList.add("Eyy");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getGSimple());
        effectiveStiffnessNamesArrayList.add("Gxy");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getNuxySimple());
        effectiveStiffnessNamesArrayList.add("vxy");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getNuyxSimple());
        effectiveStiffnessNamesArrayList.add("vyx");

        HDF5CompoundType<List<?>> effectiveStiffnessType
                = hdf5writer.compound().getInferredType("Effective stiffness with poisson effect", effectiveStiffnessNamesArrayList, effectiveStiffnessValuesArrayList);

        hdf5writer.compound().write("/".concat(groupName).concat("/effective stiffness/with poisson effect/membrane"), effectiveStiffnessType, effectiveStiffnessValuesArrayList);
        
        effectiveStiffnessValuesArrayList = new ArrayList<>();
        effectiveStiffnessNamesArrayList = new ArrayList<>();

        effectiveStiffnessValuesArrayList.add(clt_laminate.getExBendSimple());
        effectiveStiffnessNamesArrayList.add("Exx");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getEyBendSimple());
        effectiveStiffnessNamesArrayList.add("Eyy");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getGBendSimple());
        effectiveStiffnessNamesArrayList.add("Gxy");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getNuxyBendSimple());
        effectiveStiffnessNamesArrayList.add("vxy");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getNuyxBendSimple());
        effectiveStiffnessNamesArrayList.add("vyx");

        effectiveStiffnessType
                = hdf5writer.compound().getInferredType("Effective stiffness with poisson effect", effectiveStiffnessNamesArrayList, effectiveStiffnessValuesArrayList);

        hdf5writer.compound().write("/".concat(groupName).concat("/effective stiffness/with poisson effect/flexural"), effectiveStiffnessType, effectiveStiffnessValuesArrayList);

        hdf5writer.object().createGroup("/".concat(groupName).concat("/effective stiffness/without poisson effect"));
        
        effectiveStiffnessValuesArrayList = new ArrayList<>();
        effectiveStiffnessNamesArrayList = new ArrayList<>();

        effectiveStiffnessValuesArrayList.add(clt_laminate.getExFixed());
        effectiveStiffnessNamesArrayList.add("Exx");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getEyFixed());
        effectiveStiffnessNamesArrayList.add("Eyy");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getGFixed());
        effectiveStiffnessNamesArrayList.add("Gxy");

        effectiveStiffnessType
                = hdf5writer.compound().getInferredType("Effective stiffness without poisson effect", effectiveStiffnessNamesArrayList, effectiveStiffnessValuesArrayList);

        hdf5writer.compound().write("/".concat(groupName).concat("/effective stiffness/without poisson effect/membrane"), effectiveStiffnessType, effectiveStiffnessValuesArrayList);
        
        effectiveStiffnessValuesArrayList = new ArrayList<>();
        effectiveStiffnessNamesArrayList = new ArrayList<>();

        effectiveStiffnessValuesArrayList.add(clt_laminate.getExBendFixed());
        effectiveStiffnessNamesArrayList.add("Exx");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getEyBendFixed());
        effectiveStiffnessNamesArrayList.add("Eyy");

        effectiveStiffnessValuesArrayList.add(clt_laminate.getGBendFixed());
        effectiveStiffnessNamesArrayList.add("Gxy");

        effectiveStiffnessType
                = hdf5writer.compound().getInferredType("Effective stiffness without poisson effect", effectiveStiffnessNamesArrayList, effectiveStiffnessValuesArrayList);

        hdf5writer.compound().write("/".concat(groupName).concat("/effective stiffness/without poisson effect/flexural"), effectiveStiffnessType, effectiveStiffnessValuesArrayList);

        String layupGroupName = "/".concat(groupName).concat("/layup");
        hdf5writer.object().createGroup(layupGroupName);
        ArrayList<Layer> layers = laminate.getLayers();
        int layerNum = layers.size();
        Layer l;
        String[] names = new String[layerNum];
        double[] thicknesses = new double[layerNum];
        double[] angles = new double[layerNum];
        String[] materials = new String[layerNum];
        String[] criteria = new String[layerNum];
        for (int ii = 0; ii < layerNum; ii++) {
            l = layers.get(ii);
            names[ii] = l.getName();
            thicknesses[ii] = l.getThickness();
            angles[ii] = l.getAngle();
            materials[ii] = l.getMaterial().getName();
            criteria[ii] = l.getCriterion().getDisplayName();
        }
        hdf5writer.writeStringArray("/".concat(layupGroupName).concat("/names"), names);
        hdf5writer.float64().writeArray("/".concat(layupGroupName).concat("/thicknesses"), thicknesses);
        hdf5writer.float64().writeArray("/".concat(layupGroupName).concat("/angles"), angles);
        hdf5writer.writeStringArray("/".concat(layupGroupName).concat("/materials"), materials);
        hdf5writer.writeStringArray("/".concat(layupGroupName).concat("/criteria"), criteria);
    }

    @Override
    public void writeMaterialInformation(IHDF5Writer hdf5writer, Material material) {
        String groupName = "materials/".concat(material.getName());
        hdf5writer.object().createGroup(groupName);

        ArrayList<Double> materialPropertyValuesArrayList = new ArrayList<>();
        ArrayList<String> materialPropertyNamesArrayList = new ArrayList<>();

        materialPropertyValuesArrayList.add(material.getEpar());
        materialPropertyNamesArrayList.add("E11");

        materialPropertyValuesArrayList.add(material.getEnor());
        materialPropertyNamesArrayList.add("E22");

        materialPropertyValuesArrayList.add(material.getNue12());
        materialPropertyNamesArrayList.add("v12");

        materialPropertyValuesArrayList.add(material.getNue21());
        materialPropertyNamesArrayList.add("v21");

        materialPropertyValuesArrayList.add(material.getG());
        materialPropertyNamesArrayList.add("G12");

        materialPropertyValuesArrayList.add(material.getRho());
        materialPropertyNamesArrayList.add("rho");

        materialPropertyValuesArrayList.add(material.getAlphaTPar());
        materialPropertyNamesArrayList.add("a11");

        materialPropertyValuesArrayList.add(material.getAlphaTNor());
        materialPropertyNamesArrayList.add("a22");

        materialPropertyValuesArrayList.add(material.getBetaPar());
        materialPropertyNamesArrayList.add("b11");

        materialPropertyValuesArrayList.add(material.getBetaNor());
        materialPropertyNamesArrayList.add("b22");

        materialPropertyValuesArrayList.add(material.getRParTen());
        materialPropertyNamesArrayList.add("S11T");

        materialPropertyValuesArrayList.add(material.getRNorTen());
        materialPropertyNamesArrayList.add("S22T");

        materialPropertyValuesArrayList.add(material.getRParCom());
        materialPropertyNamesArrayList.add("S11C");

        materialPropertyValuesArrayList.add(material.getRNorCom());
        materialPropertyNamesArrayList.add("S22C");

        materialPropertyValuesArrayList.add(material.getRShear());
        materialPropertyNamesArrayList.add("S12");

        for (String key : material.getAdditionalValueKeySet()) {
            materialPropertyValuesArrayList.add(material.getAdditionalValue(key));
            materialPropertyNamesArrayList.add(material.getAdditionalValueDisplayName(key));
        }

        HDF5CompoundType<List<?>> materialPropertyType
                = hdf5writer.compound().getInferredType("Material properties", materialPropertyNamesArrayList, materialPropertyValuesArrayList);

        hdf5writer.compound().write("/".concat(groupName).concat("/properties"), materialPropertyType, materialPropertyValuesArrayList);
    }

}
