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
package de.elamx.clt.calculation.lastplyfailureui.batchrun;

import ch.systemsx.cisd.hdf5.CompoundElement;
import ch.systemsx.cisd.hdf5.HDF5CompoundType;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import de.elamx.clt.CLT_LastPlyFailureResult;
import de.elamx.clt.calculation.lastplyfailureui.LastPlyFailureModuleData;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Florian Dexl
 */
@ServiceProvider(service = HDF5LastPlyFailureOutputWriterService.class, position = 1)
public class HDF5LastPlyFailureOutputWriterServiceImpl implements HDF5LastPlyFailureOutputWriterService {

    @Override
    public void writeResults(IHDF5Writer hdf5writer, LastPlyFailureModuleData data, CLT_LastPlyFailureResult results) {
        String lastPlyFailureGroup = "laminates/".concat(data.getLaminat().getName().concat("/last ply failure/"));
        if (!hdf5writer.object().exists(lastPlyFailureGroup)) {
            hdf5writer.object().createGroup(lastPlyFailureGroup);
        }
        String groupName = lastPlyFailureGroup.concat(data.getName());
        hdf5writer.object().createGroup(groupName);

        double[] forces = data.getLastPlyFailureInput().getLoad().getForceMomentAsVector();

        ArrayList<Double> loadsValuesArrayList = new ArrayList<>();
        ArrayList<String> loadsNamesArrayList = new ArrayList<>();

        loadsValuesArrayList.add(forces[0]);
        loadsNamesArrayList.add("nxx");

        loadsValuesArrayList.add(forces[1]);
        loadsNamesArrayList.add("nyy");

        loadsValuesArrayList.add(forces[2]);
        loadsNamesArrayList.add("nxy");

        loadsValuesArrayList.add(forces[3]);
        loadsNamesArrayList.add("mxx");

        loadsValuesArrayList.add(forces[4]);
        loadsNamesArrayList.add("myy");

        loadsValuesArrayList.add(forces[5]);
        loadsNamesArrayList.add("mxy");

        loadsValuesArrayList.add(data.getLastPlyFailureInput().getJ_a());
        loadsNamesArrayList.add("jA");

        HDF5CompoundType<List<?>> loadsType
                = hdf5writer.compound().getInferredType("Loads", loadsNamesArrayList, loadsValuesArrayList);

        hdf5writer.compound().write(groupName.concat("/loads"), loadsType, loadsValuesArrayList);

        hdf5writer.float64().write(groupName.concat("/degradationFactor"), data.getLastPlyFailureInput().getDegradationFactor());
        hdf5writer.float64().write(groupName.concat("/epsilonAllow"), data.getLastPlyFailureInput().getEpsilon_crit());

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
