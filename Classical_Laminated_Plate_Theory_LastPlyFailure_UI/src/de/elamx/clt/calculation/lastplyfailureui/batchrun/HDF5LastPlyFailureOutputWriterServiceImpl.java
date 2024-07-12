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
import de.elamx.clt.CLT_LayerResult;
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

        String inputGroupName = groupName.concat("/input");
        hdf5writer.object().createGroup(inputGroupName);

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

        hdf5writer.compound().write(inputGroupName.concat("/loads"), loadsType, loadsValuesArrayList);

        hdf5writer.float64().write(inputGroupName.concat("/degradationFactor"), data.getLastPlyFailureInput().getDegradationFactor());
        hdf5writer.float64().write(inputGroupName.concat("/epsilonAllow"), data.getLastPlyFailureInput().getEpsilon_crit());
        hdf5writer.bool().write(inputGroupName.concat("/degradeAllOnFibreFailure"), data.getLastPlyFailureInput().isDegradeAllOnFibreFailure());

        String RFGroupName = groupName.concat("/RF results");
        if (results.getRf_first_epsilon() != null) {
            hdf5writer.compound().write(RFGroupName.concat("/RF epsilon"), new HDF5LPFRFResult(results.getRf_first_epsilon(), results.getIter_first_epsilon()));
        }

        if (results.getRf_first_ff() != null) {
            hdf5writer.compound().write(RFGroupName.concat("/RF first FF"), new HDF5LPFRFResult(results.getRf_first_ff(), results.getIter_first_ff()));
        }

        if (results.getRf_first_iff() != null) {
            hdf5writer.compound().write(RFGroupName.concat("/RF first IFF"), new HDF5LPFRFResult(results.getRf_first_iff(), results.getIter_first_iff()));
        }

        hdf5writer.bool().write(RFGroupName.concat("/FF before IFF"), results.isFf_before_iff());

        hdf5writer.compound().write(RFGroupName.concat("/exceedance factor"), new HDF5LPFRFResult(results.getExceedance_factor(), results.getIter_exceedance_factor()));

        int maxIterationNumber = results.getLayerResult().length - 1;

        String iterationGroupName = groupName.concat("/iterations");
        hdf5writer.object().createGroup(iterationGroupName);
        hdf5writer.int32().setAttr(iterationGroupName, "max. iteration number", maxIterationNumber);

        String iterationNumGroupName, localLayerResultGroupName;
        for (int iter = 0; iter < maxIterationNumber; iter++) {
            iterationNumGroupName = iterationGroupName.concat("/iteration " + iter);
            hdf5writer.object().createGroup(iterationNumGroupName);

            ArrayList<Object> iterationResultValuesArrayList = new ArrayList<>();
            ArrayList<String> iterationResultNamesArrayList = new ArrayList<>();

            iterationResultValuesArrayList.add(results.getLayerNumber()[iter]);
            iterationResultNamesArrayList.add("layer of failure");

            iterationResultValuesArrayList.add(results.getRf_min()[iter]);
            iterationResultNamesArrayList.add("RF iteration");

            iterationResultValuesArrayList.add(results.getFailureType()[iter]);
            iterationResultNamesArrayList.add("failure type");

            iterationResultValuesArrayList.add(results.getFailureTypeShort()[iter]);
            iterationResultNamesArrayList.add("failure type short");

            HDF5CompoundType<List<?>> iterationResultType
                    = hdf5writer.compound().getInferredType("LPF iteration result", iterationResultNamesArrayList, iterationResultValuesArrayList);

            hdf5writer.compound().write(iterationNumGroupName.concat("/iteration result"), iterationResultType, iterationResultValuesArrayList);

            CLT_LayerResult[] clt_results = results.getLayerResult()[iter];

            int layerNumber = clt_results.length;

            localLayerResultGroupName = iterationNumGroupName.concat("/local layer results");
            hdf5writer.object().createGroup(localLayerResultGroupName);
            hdf5writer.int32().setAttr(localLayerResultGroupName, "layer number", layerNumber);

            HDF5LPFLocalLayerResult[] localLayerResults_up = new HDF5LPFLocalLayerResult[layerNumber];
            HDF5LPFLocalLayerResult[] localLayerResults_lo = new HDF5LPFLocalLayerResult[layerNumber];
            double[] str, eps;
            for (int ii = 0; ii < layerNumber; ii++) {
                str = clt_results[ii].getSss_upper().getStress();
                eps = clt_results[ii].getSss_upper().getStrain();
                localLayerResults_up[ii] = new HDF5LPFLocalLayerResult((ii + 1), clt_results[ii].getClt_layer().getZm(), str[0], str[1], str[2], eps[0], eps[1], eps[2], clt_results[ii].getRr_upper().getMinimalReserveFactor(), results.getFb_fail()[iter][ii], results.getZfw_fail()[iter][ii]);
                str = clt_results[ii].getSss_lower().getStress();
                eps = clt_results[ii].getSss_lower().getStrain();
                localLayerResults_lo[ii] = new HDF5LPFLocalLayerResult((ii + 1), clt_results[ii].getClt_layer().getZm(), str[0], str[1], str[2], eps[0], eps[1], eps[2], clt_results[ii].getRr_lower().getMinimalReserveFactor(), results.getFb_fail()[iter][ii], results.getZfw_fail()[iter][ii]);
            }
            hdf5writer.compound().writeArray(localLayerResultGroupName.concat("/upper"), localLayerResults_up);
            hdf5writer.compound().writeArray(localLayerResultGroupName.concat("/lower"), localLayerResults_lo);
        }
    }

    /**
     * A HDF5 Data Transfer Object for local layer results of last ply failure.
     */
    static class HDF5LPFLocalLayerResult {

        // Include the unit in the member name
        @CompoundElement(memberName = "number")
        private int number;

        // Include the unit in the member name
        @CompoundElement(memberName = "zmi")
        private double zmi;

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

        // Include the unit in the member name
        @CompoundElement(memberName = "FF")
        private boolean FF;

        // Include the unit in the member name
        @CompoundElement(memberName = "IFF")
        private boolean IFF;

        // Important: needs to have a default constructor, otherwise JHDF5 will bail out on reading.
        HDF5LPFLocalLayerResult() {
        }

        HDF5LPFLocalLayerResult(int number, double zmi, double s11, double s22, double s12, double e11, double e22, double e12, double RF, boolean FF, boolean IFF) {
            this.number = number;
            this.zmi = zmi;
            this.s11 = s11;
            this.s22 = s22;
            this.s12 = s12;
            this.e11 = e11;
            this.e22 = e22;
            this.e12 = e12;
            this.RF = RF;
            this.FF = FF;
            this.IFF = IFF;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public double getZmi() {
            return zmi;
        }

        public void setZmi(double zmi) {
            this.zmi = zmi;
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

        public boolean isFF() {
            return FF;
        }

        public void setFF(boolean FF) {
            this.FF = FF;
        }

        public boolean isIFF() {
            return IFF;
        }

        public void setIFF(boolean IFF) {
            this.IFF = IFF;
        }

        @Override
        public String toString() {
            return "LocalLayerResult [number=" + number + ", zmi=" + zmi + ", s11=" + s11 + ", s22=" + s22 + ", s12=" + s12 + "e11" + e11 + ", e22=" + e22 + "e12" + e12 + ", RF" + RF + ", FF=" + FF + ", IFF=" + IFF + "]";
        }

    }

    /**
     * A HDF5 Data Transfer Object for reserve factor results of last ply failure.
     */
    static class HDF5LPFRFResult {

        // Include the unit in the member name
        @CompoundElement(memberName = "RF")
        private double RF;

        // Include the unit in the member name
        @CompoundElement(memberName = "iteration")
        private int iteration;

        // Important: needs to have a default constructor, otherwise JHDF5 will bail out on reading.
        HDF5LPFRFResult() {
        }

        HDF5LPFRFResult(double RF, int iteration) {
            this.RF = RF;
            this.iteration = iteration;
        }

        public double getRF() {
            return RF;
        }

        public void setRF(double RF) {
            this.RF = RF;
        }

        public int getIteration() {
            return iteration;
        }

        public void setIteration(int iteration) {
            this.iteration = iteration;
        }

        @Override
        public String toString() {
            return "LocalLayerResult [RF=" + RF + ", iteration=" + iteration + "]";
        }

    }

}
