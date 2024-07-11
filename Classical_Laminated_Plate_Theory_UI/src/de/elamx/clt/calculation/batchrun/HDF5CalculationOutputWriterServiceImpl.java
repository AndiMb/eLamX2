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
package de.elamx.clt.calculation.batchrun;

import ch.systemsx.cisd.hdf5.CompoundElement;
import ch.systemsx.cisd.hdf5.HDF5CompoundType;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.clt.CLT_LayerResult;
import de.elamx.clt.Loads;
import de.elamx.clt.Strains;
import de.elamx.clt.calculation.CalculationModuleData;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Florian Dexl
 */
@ServiceProvider(service = HDF5CalculationOutputWriterService.class, position = 1)
public class HDF5CalculationOutputWriterServiceImpl implements HDF5CalculationOutputWriterService {

    @Override
    public void writeResults(IHDF5Writer hdf5writer, CalculationModuleData data, Loads loads, Strains strain, CLT_LayerResult[] results) {
        String calculationGroup = "laminates/".concat(data.getLaminat().getName().concat("/calculation/"));
        if (!hdf5writer.object().exists(calculationGroup)) {
            hdf5writer.object().createGroup(calculationGroup);
        }
        String groupName = calculationGroup.concat(data.getName());
        hdf5writer.object().createGroup(groupName);

        double[] forces = loads.getForceMomentAsVector();

        ArrayList<Double> mechanicalLoadsValuesArrayList = new ArrayList<>();
        ArrayList<String> mechanicalLoadsNamesArrayList = new ArrayList<>();

        mechanicalLoadsValuesArrayList.add(forces[0]);
        mechanicalLoadsNamesArrayList.add("nxx");

        mechanicalLoadsValuesArrayList.add(forces[1]);
        mechanicalLoadsNamesArrayList.add("nyy");

        mechanicalLoadsValuesArrayList.add(forces[2]);
        mechanicalLoadsNamesArrayList.add("nxy");

        mechanicalLoadsValuesArrayList.add(forces[3]);
        mechanicalLoadsNamesArrayList.add("mxx");

        mechanicalLoadsValuesArrayList.add(forces[4]);
        mechanicalLoadsNamesArrayList.add("myy");

        mechanicalLoadsValuesArrayList.add(forces[5]);
        mechanicalLoadsNamesArrayList.add("mxy");

        HDF5CompoundType<List<?>> mechanicalLoadsType
                = hdf5writer.compound().getInferredType("Mechanical loads", mechanicalLoadsNamesArrayList, mechanicalLoadsValuesArrayList);

        hdf5writer.compound().write(groupName.concat("/mechanical loads"), mechanicalLoadsType, mechanicalLoadsValuesArrayList);

        ArrayList<Double> hygrothermalLoadsValuesArrayList = new ArrayList<>();
        ArrayList<String> hygrothermalLoadsNamesArrayList = new ArrayList<>();

        hygrothermalLoadsValuesArrayList.add(loads.getnT_x());
        hygrothermalLoadsNamesArrayList.add("nxx");

        hygrothermalLoadsValuesArrayList.add(loads.getnT_y());
        hygrothermalLoadsNamesArrayList.add("nyy");

        hygrothermalLoadsValuesArrayList.add(loads.getnT_xy());
        hygrothermalLoadsNamesArrayList.add("nxy");

        hygrothermalLoadsValuesArrayList.add(loads.getmT_x());
        hygrothermalLoadsNamesArrayList.add("mxx");

        hygrothermalLoadsValuesArrayList.add(loads.getmT_y());
        hygrothermalLoadsNamesArrayList.add("myy");

        hygrothermalLoadsValuesArrayList.add(loads.getmT_xy());
        hygrothermalLoadsNamesArrayList.add("mxy");

        hygrothermalLoadsValuesArrayList.add(loads.getDeltaT());
        hygrothermalLoadsNamesArrayList.add("deltaT");

        hygrothermalLoadsValuesArrayList.add(loads.getDeltaH());
        hygrothermalLoadsNamesArrayList.add("deltac");

        HDF5CompoundType<List<?>> hygrothermalLoadsType
                = hdf5writer.compound().getInferredType("Hygrothermal loads", hygrothermalLoadsNamesArrayList, hygrothermalLoadsValuesArrayList);

        hdf5writer.compound().write(groupName.concat("/hygrothermal loads"), hygrothermalLoadsType, hygrothermalLoadsValuesArrayList);

        double[] strains = strain.getEpsilonKappaAsVector();

        ArrayList<Double> globalStrainsValuesArrayList = new ArrayList<>();
        ArrayList<String> globalStrainsNamesArrayList = new ArrayList<>();

        globalStrainsValuesArrayList.add(strains[0]);
        globalStrainsNamesArrayList.add("exx");

        globalStrainsValuesArrayList.add(strains[1]);
        globalStrainsNamesArrayList.add("eyy");

        globalStrainsValuesArrayList.add(strains[2]);
        globalStrainsNamesArrayList.add("gxy");

        globalStrainsValuesArrayList.add(strains[3]);
        globalStrainsNamesArrayList.add("kxx");

        globalStrainsValuesArrayList.add(strains[4]);
        globalStrainsNamesArrayList.add("kyy");

        globalStrainsValuesArrayList.add(strains[5]);
        globalStrainsNamesArrayList.add("kxy");

        HDF5CompoundType<List<?>> globalStrainsType
                = hdf5writer.compound().getInferredType("Global strains", globalStrainsNamesArrayList, globalStrainsValuesArrayList);

        hdf5writer.compound().write(groupName.concat("/global strains"), globalStrainsType, globalStrainsValuesArrayList);

        int layerResultsNum = results.length;
        
        hdf5writer.object().createGroup(groupName.concat("/local layer results"));

        double s11;
        double s22;
        double s12;
        double e11;
        double e12;
        double e22;
        double RF;
        
        HDF5LocalLayerResult[] llres_lo = new HDF5LocalLayerResult[layerResultsNum];
        for (int ii = 0; ii < layerResultsNum; ii++) {
            s11 = results[ii].getSss_lower().getStress()[0];
            s22 = results[ii].getSss_lower().getStress()[1];
            s12 = results[ii].getSss_lower().getStress()[2];
            e11 = results[ii].getSss_lower().getStrain()[0];
            e22 = results[ii].getSss_lower().getStrain()[1];
            e12 = results[ii].getSss_lower().getStrain()[2];
            RF  = results[ii].getRr_upper().getMinimalReserveFactor();
            llres_lo[ii] = new HDF5LocalLayerResult(s11, s22, s12, e11, e22, e12, RF);
        }
        hdf5writer.compound().writeArray(groupName.concat("/local layer results/lower"), llres_lo);
        hdf5writer.int32().setAttr(groupName.concat("/local layer results/lower"), "number of layers", layerResultsNum);
        
        HDF5LocalLayerResult[] llres_up = new HDF5LocalLayerResult[layerResultsNum];
        for (int ii = 0; ii < layerResultsNum; ii++) {
            s11 = results[ii].getSss_upper().getStress()[0];
            s22 = results[ii].getSss_upper().getStress()[1];
            s12 = results[ii].getSss_upper().getStress()[2];
            e11 = results[ii].getSss_upper().getStrain()[0];
            e22 = results[ii].getSss_upper().getStrain()[1];
            e12 = results[ii].getSss_upper().getStrain()[2];
            RF  = results[ii].getRr_upper().getMinimalReserveFactor();
            llres_up[ii] = new HDF5LocalLayerResult(s11, s22, s12, e11, e22, e12, RF);
        }
        hdf5writer.compound().writeArray(groupName.concat("/local layer results/upper"), llres_up);
        hdf5writer.int32().setAttr(groupName.concat("/local layer results/upper"), "number of layers", layerResultsNum);
    }

    /**
     * A HDF5 Data Transfer Object for local layer results.
     */
    static class HDF5LocalLayerResult
    {
        // Include the unit in the member name
        @CompoundElement(memberName = "s11")
        private double s11;

        // Include the unit in the member name
        @CompoundElement(memberName = "s22")
        private double s22;

        // Include the unit in the member name
        @CompoundElement(memberName = "s12")
        private double s12;

        // Include the unit in the member name
        @CompoundElement(memberName = "e11")
        private double e11;

        // Include the unit in the member name
        @CompoundElement(memberName = "e22")
        private double e22;

        // Include the unit in the member name
        @CompoundElement(memberName = "e12")
        private double e12;

        // Include the unit in the member name
        @CompoundElement(memberName = "RF")
        private double RF;

        // Important: needs to have a default constructor, otherwise JHDF5 will bail out on reading.
        HDF5LocalLayerResult()
        {
        }

        HDF5LocalLayerResult(double s11, double s22, double s12, double e11, double e22, double e12, double RF)
        {
            this.s11 = s11;
            this.s22 = s22;
            this.s12 = s12;
            this.e11 = e11;
            this.e22 = e22;
            this.e12 = e12;
            this.RF  = RF;
        }

        public double getS11() {
            return s11;
        }

        public void setS11(double s11) {
            this.s11 = s11;
        }

        public double getS22() {
            return s22;
        }

        public void setS22(double s22) {
            this.s22 = s22;
        }

        public double getS12() {
            return s12;
        }

        public void setS12(double s12) {
            this.s12 = s12;
        }

        public double getE11() {
            return e11;
        }

        public void setE11(double e11) {
            this.e11 = e11;
        }

        public double getE22() {
            return e22;
        }

        public void setE22(double e22) {
            this.e22 = e22;
        }

        public double getE12() {
            return e12;
        }

        public void setE12(double e12) {
            this.e12 = e12;
        }

        public double getRF() {
            return RF;
        }

        public void setRF(double RF) {
            this.RF = RF;
        }

        @Override
        public String toString()
        {
            return "LocalLayerResult [s11=" + s11 + ", s22=" + s22 + ", s12=" + s12 + "e11" + e11 + ", e22=" + e22 + "e12" + e12 + ", RF" + RF + "]";
        }

    }

}
