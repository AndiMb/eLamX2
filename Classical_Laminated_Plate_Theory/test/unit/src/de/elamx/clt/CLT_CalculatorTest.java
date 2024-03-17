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

import de.elamx.laminate.DataLayer;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.Puck;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Andreas Hauffe
 */
public class CLT_CalculatorTest {

    public CLT_CalculatorTest() {
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Issue D Prüfung der globalen Dehnungen
     */
    @Test
    public void HSB37103_01_D_globalStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(100.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(15.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);
        assertEquals(12.64, strains.getEpsilon_x() * 1000.0, 0.005);
        assertEquals(-0.43, strains.getEpsilon_y() * 1000.0, 0.005);
        assertEquals(-7.42, strains.getGamma_xy() * 1000.0, 0.005);
        assertEquals(83.88, strains.getKappa_x() * 1000.0, 0.005);
        assertEquals(-6.58, strains.getKappa_y() * 1000.0, 0.005);
        assertEquals(5.99, strains.getKappa_xy() * 1000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Issue D Prüfung der Lagendehnungen
     */
    @Test
    public void HSB37103_01_D_layerStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(100.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(15.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);

        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);

        /*
        Layer 4 lower
         */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(-8.33, strain[0] * 1000.0, 0.005);
        assertEquals(1.22, strain[1] * 1000.0, 0.005);
        assertEquals(-8.92, strain[2] * 1000.0, 0.005);
        double[] stress = sss.getStress();
        assertEquals(-1108.8, stress[0], 0.05);
        assertEquals(-10.4, stress[1], 0.05);
        assertEquals(-41.0, stress[2], 0.05);

        /*
        Layer 4 upper
         */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals(2.15, strain[0] * 1000.0, 0.005);
        assertEquals(0.40, strain[1] * 1000.0, 0.005);
        assertEquals(-8.17, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(288.0, stress[0], 0.05);
        assertEquals(9.3, stress[1], 0.05);
        assertEquals(-37.6, stress[2], 0.05);

        /*
        Layer 3 lower
         */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals(0.70, strain[0] * 1000.0, 0.005);
        assertEquals(1.85, strain[1] * 1000.0, 0.005);
        assertEquals(-8.28, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(98.3, stress[0], 0.05);
        assertEquals(19.1, stress[1], 0.05);
        assertEquals(-38.1, stress[2], 0.05);

        /*
        Layer 3 upper
         */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(10.97, strain[0] * 1000.0, 0.005);
        assertEquals(1.24, strain[1] * 1000.0, 0.005);
        assertEquals(-11.44, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(1467.2, stress[0], 0.05);
        assertEquals(40.3, stress[1], 0.05);
        assertEquals(-52.6, stress[2], 0.05);

        /*
        Layer 2 lower
         */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.43, strain[0] * 1000.0, 0.005);
        assertEquals(12.64, strain[1] * 1000.0, 0.005);
        assertEquals(7.42, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-23.6, stress[0], 0.05);
        assertEquals(117.0, stress[1], 0.05);
        assertEquals(34.2, stress[2], 0.05);

        /*
        Layer 2 upper
         */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-1.25, strain[0] * 1000.0, 0.005);
        assertEquals(23.12, strain[1] * 1000.0, 0.005);
        assertEquals(6.68, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-105.9, stress[0], 0.05);
        assertEquals(212.9, stress[1], 0.05);
        assertEquals(30.7, stress[2], 0.05);

        /*
        Layer 1 lower
         */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.54, strain[0] * 1000.0, 0.005);
        assertEquals(22.41, strain[1] * 1000.0, 0.005);
        assertEquals(-10.55, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-13.7, stress[0], 0.05);
        assertEquals(208.2, stress[1], 0.05);
        assertEquals(-48.5, stress[2], 0.05);

        /*
        Layer 1 upper
         */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals(0.20, strain[0] * 1000.0, 0.005);
        assertEquals(31.34, strain[1] * 1000.0, 0.005);
        assertEquals(-18.39, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(108.5, stress[0], 0.05);
        assertEquals(293.6, stress[1], 0.05);
        assertEquals(-84.6, stress[2], 0.05);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "All section forces and
     * moments" Prüfung der globalen Dehnungen
     */
    @Test
    public void HSB37103_01_E_allSectionForcesAndMoments_globalStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(100.0);
        loads.setN_y(125.0);
        loads.setN_xy(80.0);
        loads.setM_x(15.0);
        loads.setM_y(20.0);
        loads.setM_xy(12.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);
        assertEquals(8.81, strains.getEpsilon_x() * 1000.0, 0.005);
        assertEquals(0.97, strains.getEpsilon_y() * 1000.0, 0.005);
        assertEquals(10.03, strains.getGamma_xy() * 1000.0, 0.005);
        assertEquals(62.29, strains.getKappa_x() * 1000.0, 0.005);
        assertEquals(-22.44, strains.getKappa_y() * 1000.0, 0.005);
        assertEquals(95.42, strains.getKappa_xy() * 1000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "All section forces and
     * moments" Prüfung der Lagendehnungen
     */
    @Test
    public void HSB37103_01_E_allSectionForcesAndMoments_layerStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(100.0);
        loads.setN_y(125.0);
        loads.setN_xy(80.0);
        loads.setM_x(15.0);
        loads.setM_y(20.0);
        loads.setM_xy(12.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);

        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);

        /*
        Layer 4 lower
         */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(-6.77, strain[0] * 1000.0, 0.005);
        assertEquals(6.58, strain[1] * 1000.0, 0.005);
        assertEquals(-13.82, strain[2] * 1000.0, 0.005);
        double[] stress = sss.getStress();
        assertEquals(-885.7, stress[0], 0.05);
        assertEquals(43.8, stress[1], 0.05);
        assertEquals(-63.6, stress[2], 0.05);

        /*
        Layer 4 upper
         */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals(1.02, strain[0] * 1000.0, 0.005);
        assertEquals(3.77, strain[1] * 1000.0, 0.005);
        assertEquals(-1.89, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(145.9, stress[0], 0.05);
        assertEquals(38.0, stress[1], 0.05);
        assertEquals(-8.7, stress[2], 0.05);

        /*
        Layer 3 lower
         */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals(0.78, strain[0] * 1000.0, 0.005);
        assertEquals(4.01, strain[1] * 1000.0, 0.005);
        assertEquals(-0.84, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(114.4, stress[0], 0.05);
        assertEquals(39.6, stress[1], 0.05);
        assertEquals(-3.9, stress[2], 0.05);

        /*
        Layer 3 upper
         */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(10.28, strain[0] * 1000.0, 0.005);
        assertEquals(-0.51, strain[1] * 1000.0, 0.005);
        assertEquals(6.75, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(1371.0, stress[0], 0.05);
        assertEquals(22.2, stress[1], 0.05);
        assertEquals(31.0, stress[2], 0.05);

        /*
        Layer 2 lower
         */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(0.97, strain[0] * 1000.0, 0.005);
        assertEquals(8.81, strain[1] * 1000.0, 0.005);
        assertEquals(-10.03, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(152.3, stress[0], 0.05);
        assertEquals(84.9, stress[1], 0.05);
        assertEquals(-46.2, stress[2], 0.05);

        /*
        Layer 2 upper
         */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-1.84, strain[0] * 1000.0, 0.005);
        assertEquals(16.59, strain[1] * 1000.0, 0.005);
        assertEquals(-21.96, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-201.6, stress[0], 0.05);
        assertEquals(150.3, stress[1], 0.05);
        assertEquals(-101.0, stress[2], 0.05);

        /*
        Layer 1 lower
         */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(7.38, strain[0] * 1000.0, 0.005);
        assertEquals(7.38, strain[1] * 1000.0, 0.005);
        assertEquals(-28.67, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(1003.7, stress[0], 0.05);
        assertEquals(88.3, stress[1], 0.05);
        assertEquals(-131.9, stress[2], 0.05);

        /*
        Layer 1 upper
         */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals(9.64, strain[0] * 1000.0, 0.005);
        assertEquals(10.09, strain[1] * 1000.0, 0.005);
        assertEquals(-44.61, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(1313.3, stress[0], 0.05);
        assertEquals(119.6, stress[1], 0.05);
        assertEquals(-205.2, stress[2], 0.05);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section normal force
     * n_x" Prüfung der globalen Dehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionNormalForce_nX_globalStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(100.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(0.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);
        assertEquals(7.22, strains.getEpsilon_x() * 1000.0, 0.005);
        assertEquals(-0.37, strains.getEpsilon_y() * 1000.0, 0.005);
        assertEquals(-4.82, strains.getGamma_xy() * 1000.0, 0.005);
        assertEquals(36.09, strains.getKappa_x() * 1000.0, 0.005);
        assertEquals(-1.53, strains.getKappa_y() * 1000.0, 0.005);
        assertEquals(6.63, strains.getKappa_xy() * 1000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section normal force
     * n_x" Prüfung der Lagendehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionNormalForce_nX_layerStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(100.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(0.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);

        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);

        /*
        Layer 4 lower
         */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(-1.80, strain[0] * 1000.0, 0.005);
        assertEquals(0.01, strain[1] * 1000.0, 0.005);
        assertEquals(-6.47, strain[2] * 1000.0, 0.005);
        double[] stress = sss.getStress();
        assertEquals(-240.5, stress[0], 0.05);
        assertEquals(-4.6, stress[1], 0.05);
        assertEquals(-29.8, stress[2], 0.05);

        /*
        Layer 4 upper
         */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals(2.71, strain[0] * 1000.0, 0.005);
        assertEquals(-0.18, strain[1] * 1000.0, 0.005);
        assertEquals(-5.65, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(361.0, stress[0], 0.05);
        assertEquals(5.4, stress[1], 0.05);
        assertEquals(-26.0, stress[2], 0.05);

        /*
        Layer 3 lower
         */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals(1.66, strain[0] * 1000.0, 0.005);
        assertEquals(0.87, strain[1] * 1000.0, 0.005);
        assertEquals(-6.29, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(223.3, stress[0], 0.05);
        assertEquals(12.5, stress[1], 0.05);
        assertEquals(-29.0, stress[2], 0.05);

        /*
        Layer 3 upper
         */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(6.17, strain[0] * 1000.0, 0.005);
        assertEquals(0.68, strain[1] * 1000.0, 0.005);
        assertEquals(-7.12, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(824.8, stress[0], 0.05);
        assertEquals(22.5, stress[1], 0.05);
        assertEquals(-32.8, stress[2], 0.05);

        /*
        Layer 2 lower
         */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.37, strain[0] * 1000.0, 0.005);
        assertEquals(7.22, strain[1] * 1000.0, 0.005);
        assertEquals(4.82, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-30.6, stress[0], 0.05);
        assertEquals(66.6, stress[1], 0.05);
        assertEquals(22.2, stress[2], 0.05);

        /*
        Layer 2 upper
         */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-0.56, strain[0] * 1000.0, 0.005);
        assertEquals(11.73, strain[1] * 1000.0, 0.005);
        assertEquals(3.99, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-44.4, stress[0], 0.05);
        assertEquals(108.2, stress[1], 0.05);
        assertEquals(18.4, stress[2], 0.05);

        /*
        Layer 1 lower
         */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.41, strain[0] * 1000.0, 0.005);
        assertEquals(11.58, strain[1] * 1000.0, 0.005);
        assertEquals(-4.85, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-24.0, stress[0], 0.05);
        assertEquals(107.2, stress[1], 0.05);
        assertEquals(-22.3, stress[2], 0.05);

        /*
        Layer 1 upper
         */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals(0.22, strain[0] * 1000.0, 0.005);
        assertEquals(15.27, strain[1] * 1000.0, 0.005);
        assertEquals(-8.50, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(69.1, stress[0], 0.05);
        assertEquals(143.4, stress[1], 0.05);
        assertEquals(-39.1, stress[2], 0.05);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section normal force
     * n_y" Prüfung der globalen Dehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionNormalForce_nY_globalStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(125.0);
        loads.setN_xy(0.0);
        loads.setM_x(0.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);
        assertEquals(-0.46, strains.getEpsilon_x() * 1000.0, 0.005);
        assertEquals(8.17, strains.getEpsilon_y() * 1000.0, 0.005);
        assertEquals(-0.32, strains.getGamma_xy() * 1000.0, 0.005);
        assertEquals(-0.45, strains.getKappa_x() * 1000.0, 0.005);
        assertEquals(-42.70, strains.getKappa_y() * 1000.0, 0.005);
        assertEquals(2.69, strains.getKappa_xy() * 1000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section normal force
     * n_y" Prüfung der Lagendehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionNormalForce_nY_layerStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(125.0);
        loads.setN_xy(0.0);
        loads.setM_x(0.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);

        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);

        /*
        Layer 4 lower
         */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(-0.35, strain[0] * 1000.0, 0.005);
        assertEquals(18.84, strain[1] * 1000.0, 0.005);
        assertEquals(-0.99, strain[2] * 1000.0, 0.005);
        double[] stress = sss.getStress();
        assertEquals(2.5, stress[0], 0.05);
        assertEquals(175.3, stress[1], 0.05);
        assertEquals(-4.6, stress[2], 0.05);

        /*
        Layer 4 upper
         */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-0.41, strain[0] * 1000.0, 0.005);
        assertEquals(13.51, strain[1] * 1000.0, 0.005);
        assertEquals(-0.65, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-19.0, stress[0], 0.05);
        assertEquals(125.2, stress[1], 0.05);
        assertEquals(-3.0, stress[2], 0.05);

        /*
        Layer 3 lower
         */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.10, strain[0] * 1000.0, 0.005);
        assertEquals(13.20, strain[1] * 1000.0, 0.005);
        assertEquals(4.14, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(21.2, stress[0], 0.05);
        assertEquals(123.2, stress[1], 0.05);
        assertEquals(19.1, stress[2], 0.05);

        /*
        Layer 3 upper
         */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-0.26, strain[0] * 1000.0, 0.005);
        assertEquals(7.96, strain[1] * 1000.0, 0.005);
        assertEquals(2.65, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-13.6, stress[0], 0.05);
        assertEquals(73.8, stress[1], 0.05);
        assertEquals(12.2, stress[2], 0.05);

        /*
        Layer 2 lower
         */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(8.17, strain[0] * 1000.0, 0.005);
        assertEquals(-0.46, strain[1] * 1000.0, 0.005);
        assertEquals(0.32, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(1089.0, stress[0], 0.05);
        assertEquals(17.1, stress[1], 0.05);
        assertEquals(1.5, stress[2], 0.05);

        /*
        Layer 2 upper
         */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(2.83, strain[0] * 1000.0, 0.005);
        assertEquals(-0.52, strain[1] * 1000.0, 0.005);
        assertEquals(-0.02, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(376.8, stress[0], 0.05);
        assertEquals(2.6, stress[1], 0.05);
        assertEquals(-0.1, stress[2], 0.05);

        /*
        Layer 1 lower
         */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(2.45, strain[0] * 1000.0, 0.005);
        assertEquals(-0.13, strain[1] * 1000.0, 0.005);
        assertEquals(2.14, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(326.2, stress[0], 0.05);
        assertEquals(5.2, stress[1], 0.05);
        assertEquals(9.9, stress[2], 0.05);

        /*
        Layer 1 upper
         */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-2.16, strain[0] * 1000.0, 0.005);
        assertEquals(-0.92, strain[1] * 1000.0, 0.005);
        assertEquals(-1.51, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-291.2, stress[0], 0.05);
        assertEquals(-14.2, stress[1], 0.05);
        assertEquals(-6.9, stress[2], 0.05);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section normal force
     * n_xy" Prüfung der globalen Dehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionNormalForce_nXY_globalStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(0.0);
        loads.setN_xy(80.0);
        loads.setM_x(0.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);
        assertEquals(-3.85, strains.getEpsilon_x() * 1000.0, 0.005);
        assertEquals(-0.20, strains.getEpsilon_y() * 1000.0, 0.005);
        assertEquals(27.97, strains.getGamma_xy() * 1000.0, 0.005);
        assertEquals(-13.90, strains.getKappa_x() * 1000.0, 0.005);
        assertEquals(-24.71, strains.getKappa_y() * 1000.0, 0.005);
        assertEquals(-26.75, strains.getKappa_xy() * 1000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section normal force
     * n_xy" Prüfung der Lagendehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionNormalForce_nXY_layerStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(0.0);
        loads.setN_xy(80.0);
        loads.setM_x(0.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);

        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);

        /*
        Layer 4 lower
         */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(-0.38, strain[0] * 1000.0, 0.005);
        assertEquals(5.97, strain[1] * 1000.0, 0.005);
        assertEquals(34.65, strain[2] * 1000.0, 0.005);
        double[] stress = sss.getStress();
        assertEquals(-35.0, stress[0], 0.05);
        assertEquals(54.9, stress[1], 0.05);
        assertEquals(159.4, stress[2], 0.05);

        /*
        Layer 4 upper
         */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-2.12, strain[0] * 1000.0, 0.005);
        assertEquals(2.89, strain[1] * 1000.0, 0.005);
        assertEquals(31.31, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-274.9, stress[0], 0.05);
        assertEquals(21.4, stress[1], 0.05);
        assertEquals(144.0, stress[2], 0.05);

        /*
        Layer 3 lower
         */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals(3.39, strain[0] * 1000.0, 0.005);
        assertEquals(-2.62, strain[1] * 1000.0, 0.005);
        assertEquals(31.13, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(445.3, stress[0], 0.05);
        assertEquals(-15.6, stress[1], 0.05);
        assertEquals(143.2, stress[2], 0.05);

        /*
        Layer 3 upper
         */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(1.04, strain[0] * 1000.0, 0.005);
        assertEquals(-5.10, strain[1] * 1000.0, 0.005);
        assertEquals(27.53, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(125.2, stress[0], 0.05);
        assertEquals(-44.9, stress[1], 0.05);
        assertEquals(126.6, stress[2], 0.05);

        /*
        Layer 2 lower
         */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.20, strain[0] * 1000.0, 0.005);
        assertEquals(-3.85, strain[1] * 1000.0, 0.005);
        assertEquals(-27.97, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-37.3, stress[0], 0.05);
        assertEquals(-36.6, stress[1], 0.05);
        assertEquals(-128.6, stress[2], 0.05);

        /*
        Layer 2 upper
         */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-3.29, strain[0] * 1000.0, 0.005);
        assertEquals(-5.59, strain[1] * 1000.0, 0.005);
        assertEquals(-24.62, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-454.0, stress[0], 0.05);
        assertEquals(-60.9, stress[1], 0.05);
        assertEquals(-113.3, stress[2], 0.05);

        /*
        Layer 1 lower
         */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(4.35, strain[0] * 1000.0, 0.005);
        assertEquals(-13.24, strain[1] * 1000.0, 0.005);
        assertEquals(-17.39, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(546.0, stress[0], 0.05);
        assertEquals(-112.4, stress[1], 0.05);
        assertEquals(-80.0, stress[2], 0.05);

        /*
        Layer 1 upper
         */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals(0.35, strain[0] * 1000.0, 0.005);
        assertEquals(-14.06, strain[1] * 1000.0, 0.005);
        assertEquals(-15.69, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(9.4, stress[0], 0.05);
        assertEquals(-130.6, stress[1], 0.05);
        assertEquals(-72.2, stress[2], 0.05);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section bending
     * moment m_x" Prüfung der globalen Dehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionBendingMoment_mX_globalStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(15.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);
        assertEquals(5.41, strains.getEpsilon_x() * 1000.0, 0.005);
        assertEquals(-0.05, strains.getEpsilon_y() * 1000.0, 0.005);
        assertEquals(-2.61, strains.getGamma_xy() * 1000.0, 0.005);
        assertEquals(47.78, strains.getKappa_x() * 1000.0, 0.005);
        assertEquals(-5.05, strains.getKappa_y() * 1000.0, 0.005);
        assertEquals(-0.64, strains.getKappa_xy() * 1000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section bending
     * moment m_x" Prüfung der Lagendehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionBendingMoment_mX_layerStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(15.0);
        loads.setM_y(0.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);

        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);

        /*
        Layer 4 lower
         */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(-6.53, strain[0] * 1000.0, 0.005);
        assertEquals(1.21, strain[1] * 1000.0, 0.005);
        assertEquals(-2.45, strain[2] * 1000.0, 0.005);
        double[] stress = sss.getStress();
        assertEquals(-868.3, stress[0], 0.05);
        assertEquals(-5.8, stress[1], 0.05);
        assertEquals(-11.3, stress[2], 0.05);

        /*
        Layer 4 upper
         */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-0.56, strain[0] * 1000.0, 0.005);
        assertEquals(0.58, strain[1] * 1000.0, 0.005);
        assertEquals(-2.53, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-73.0, stress[0], 0.05);
        assertEquals(3.9, stress[1], 0.05);
        assertEquals(-11.6, stress[2], 0.05);

        /*
        Layer 3 lower
         */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.96, strain[0] * 1000.0, 0.005);
        assertEquals(0.97, strain[1] * 1000.0, 0.005);
        assertEquals(-1.99, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-125.0, stress[0], 0.05);
        assertEquals(6.6, stress[1], 0.05);
        assertEquals(-9.1, stress[2], 0.05);

        /*
        Layer 3 upper
         */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(4.80, strain[0] * 1000.0, 0.005);
        assertEquals(0.56, strain[1] * 1000.0, 0.005);
        assertEquals(-4.32, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(642.4, stress[0], 0.05);
        assertEquals(17.8, stress[1], 0.05);
        assertEquals(-19.9, stress[2], 0.05);

        /*
        Layer 2 lower
         */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.05, strain[0] * 1000.0, 0.005);
        assertEquals(5.41, strain[1] * 1000.0, 0.005);
        assertEquals(2.61, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(7.0, stress[0], 0.05);
        assertEquals(50.5, stress[1], 0.05);
        assertEquals(12.0, stress[2], 0.05);

        /*
        Layer 2 upper
         */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-0.68, strain[0] * 1000.0, 0.005);
        assertEquals(11.39, strain[1] * 1000.0, 0.005);
        assertEquals(2.69, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-61.6, stress[0], 0.05);
        assertEquals(104.7, stress[1], 0.05);
        assertEquals(12.4, stress[2], 0.05);

        /*
        Layer 1 lower
         */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.14, strain[0] * 1000.0, 0.005);
        assertEquals(10.84, strain[1] * 1000.0, 0.005);
        assertEquals(-5.70, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(10.2, stress[0], 0.05);
        assertEquals(101.0, stress[1], 0.05);
        assertEquals(-26.2, stress[2], 0.05);

        /*
        Layer 1 upper
         */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-0.02, strain[0] * 1000.0, 0.005);
        assertEquals(16.06, strain[1] * 1000.0, 0.005);
        assertEquals(-9.89, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(39.4, stress[0], 0.05);
        assertEquals(150.2, stress[1], 0.05);
        assertEquals(-45.5, stress[2], 0.05);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section bending
     * moment m_y" Prüfung der globalen Dehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionBendingMoment_mY_globalStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(0.0);
        loads.setM_y(20.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);
        assertEquals(-0.31, strains.getEpsilon_x() * 1000.0, 0.005);
        assertEquals(-6.83, strains.getEpsilon_y() * 1000.0, 0.005);
        assertEquals(-6.18, strains.getGamma_xy() * 1000.0, 0.005);
        assertEquals(-6.73, strains.getKappa_x() * 1000.0, 0.005);
        assertEquals(95.61, strains.getKappa_y() * 1000.0, 0.005);
        assertEquals(-73.43, strains.getKappa_xy() * 1000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section bending
     * moment m_y" Prüfung der Lagendehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionBendingMoment_mY_layerStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(0.0);
        loads.setM_y(20.0);
        loads.setM_xy(0.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);

        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);

        /*
        Layer 4 lower
         */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(1.38, strain[0] * 1000.0, 0.005);
        assertEquals(-30.73, strain[1] * 1000.0, 0.005);
        assertEquals(12.18, strain[2] * 1000.0, 0.005);
        double[] stress = sss.getStress();
        assertEquals(103.1, stress[0], 0.05);
        assertEquals(-283.8, stress[1], 0.05);
        assertEquals(56.0, stress[2], 0.05);

        /*
        Layer 4 upper
         */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals(0.53, strain[0] * 1000.0, 0.005);
        assertEquals(-18.78, strain[1] * 1000.0, 0.005);
        assertEquals(3.00, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(22.1, stress[0], 0.05);
        assertEquals(-174.2, stress[1], 0.05);
        assertEquals(13.8, stress[2], 0.05);

        /*
        Layer 3 lower
         */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals(0.46, strain[0] * 1000.0, 0.005);
        assertEquals(-18.71, strain[1] * 1000.0, 0.005);
        assertEquals(-3.79, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(13.0, stress[0], 0.05);
        assertEquals(-173.8, stress[1], 0.05);
        assertEquals(-17.4, stress[2], 0.05);

        /*
        Layer 3 upper
         */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-1.56, strain[0] * 1000.0, 0.005);
        assertEquals(-5.58, strain[1] * 1000.0, 0.005);
        assertEquals(-8.04, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-222.8, stress[0], 0.05);
        assertEquals(-56.2, stress[1], 0.05);
        assertEquals(-37.0, stress[2], 0.05);

        /*
        Layer 2 lower
         */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-6.83, strain[0] * 1000.0, 0.005);
        assertEquals(-0.31, strain[1] * 1000.0, 0.005);
        assertEquals(6.18, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-912.3, stress[0], 0.05);
        assertEquals(-20.8, stress[1], 0.05);
        assertEquals(28.4, stress[2], 0.05);

        /*
        Layer 2 upper
         */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(5.12, strain[0] * 1000.0, 0.005);
        assertEquals(-1.15, strain[1] * 1000.0, 0.005);
        assertEquals(15.36, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(680.1, stress[0], 0.05);
        assertEquals(2.7, stress[1], 0.05);
        assertEquals(70.6, stress[2], 0.05);

        /*
        Layer 1 lower
         */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.55, strain[0] * 1000.0, 0.005);
        assertEquals(4.52, strain[1] * 1000.0, 0.005);
        assertEquals(15.79, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-61.5, stress[0], 0.05);
        assertEquals(40.8, stress[1], 0.05);
        assertEquals(72.6, stress[2], 0.05);

        /*
        Layer 1 upper
         */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals(6.95, strain[0] * 1000.0, 0.005);
        assertEquals(8.13, strain[1] * 1000.0, 0.005);
        assertEquals(31.05, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(949.3, stress[0], 0.05);
        assertEquals(94.2, stress[1], 0.05);
        assertEquals(142.8, stress[2], 0.05);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section bending
     * moment m_xy" Prüfung der globalen Dehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionBendingMoment_mXY_globalStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(0.0);
        loads.setM_y(0.0);
        loads.setM_xy(12.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);
        assertEquals(0.80, strains.getEpsilon_x() * 1000.0, 0.005);
        assertEquals(0.26, strains.getEpsilon_y() * 1000.0, 0.005);
        assertEquals(-4.01, strains.getGamma_xy() * 1000.0, 0.005);
        assertEquals(-0.51, strains.getKappa_x() * 1000.0, 0.005);
        assertEquals(-44.06, strains.getKappa_y() * 1000.0, 0.005);
        assertEquals(186.93, strains.getKappa_xy() * 1000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E "Only section bending
     * moment m_xy" Prüfung der Lagendehnungen
     */
    @Test
    public void HSB37103_01_E_onlySectionBendingMoment_mXY_layerStrains() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<DataLayer> layers = new ArrayList<>();

        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new DataLayer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        Loads loads = new Loads();
        loads.setN_x(0.0);
        loads.setN_y(0.0);
        loads.setN_xy(0.0);
        loads.setM_x(0.0);
        loads.setM_y(0.0);
        loads.setM_xy(12.0);
        loads.setDeltaH(0.0);
        loads.setDeltaT(0.0);

        Strains strains = new Strains();
        strains.setEpsilon_x(0.0);
        strains.setEpsilon_y(0.0);
        strains.setGamma_xy(0.0);
        strains.setKappa_x(0.0);
        strains.setKappa_y(0.0);
        strains.setKappa_xy(0.0);

        boolean[] useStrains = new boolean[6];
        useStrains[0] = false;
        useStrains[1] = false;
        useStrains[2] = false;
        useStrains[3] = false;
        useStrains[4] = false;
        useStrains[5] = false;

        CLT_Calculator.determineValues(clt_lam, loads, strains, useStrains);

        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);

        /*
        Layer 4 lower
         */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(0.92, strain[0] * 1000.0, 0.005);
        assertEquals(11.27, strain[1] * 1000.0, 0.005);
        assertEquals(-50.74, strain[2] * 1000.0, 0.005);
        double[] stress = sss.getStress();
        assertEquals(152.6, stress[0], 0.05);
        assertEquals(107.8, stress[1], 0.05);
        assertEquals(-233.4, stress[2], 0.05);

        /*
        Layer 4 upper
         */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals(0.86, strain[0] * 1000.0, 0.005);
        assertEquals(5.77, strain[1] * 1000.0, 0.005);
        assertEquals(-27.38, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(129.7, stress[0], 0.05);
        assertEquals(56.2, stress[1], 0.05);
        assertEquals(-125.9, stress[2], 0.05);

        /*
        Layer 3 lower
         */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-3.68, strain[0] * 1000.0, 0.005);
        assertEquals(10.30, strain[1] * 1000.0, 0.005);
        assertEquals(-24.05, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-463.4, stress[0], 0.05);
        assertEquals(86.7, stress[1], 0.05);
        assertEquals(-110.6, stress[2], 0.05);

        /*
        Layer 3 upper
         */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(0.09, strain[0] * 1000.0, 0.005);
        assertEquals(0.96, strain[1] * 1000.0, 0.005);
        assertEquals(-3.95, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(14.9, stress[0], 0.05);
        assertEquals(9.2, stress[1], 0.05);
        assertEquals(-18.2, stress[2], 0.05);

        /*
        Layer 2 lower
         */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(0.26, strain[0] * 1000.0, 0.005);
        assertEquals(0.80, strain[1] * 1000.0, 0.005);
        assertEquals(4.01, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(36.5, stress[0], 0.05);
        assertEquals(8.1, stress[1], 0.05);
        assertEquals(18.5, stress[2], 0.05);

        /*
        Layer 2 upper
         */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-5.25, strain[0] * 1000.0, 0.005);
        assertEquals(0.73, strain[1] * 1000.0, 0.005);
        assertEquals(-19.35, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(-698.5, stress[0], 0.05);
        assertEquals(-6.9, stress[1], 0.05);
        assertEquals(-89.0, stress[2], 0.05);

        /*
        Layer 1 lower
         */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(1.67, strain[0] * 1000.0, 0.005);
        assertEquals(-6.19, strain[1] * 1000.0, 0.005);
        assertEquals(-18.67, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(206.7, stress[0], 0.05);
        assertEquals(-53.5, stress[1], 0.05);
        assertEquals(-85.9, stress[2], 0.05);

        /*
        Layer 1 upper
         */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals(4.31, strain[0] * 1000.0, 0.005);
        assertEquals(-14.40, strain[1] * 1000.0, 0.005);
        assertEquals(-40.07, strain[2] * 1000.0, 0.005);
        stress = sss.getStress();
        assertEquals(537.3, stress[0], 0.05);
        assertEquals(-123.4, stress[1], 0.05);
        assertEquals(-184.3, stress[2], 0.05);
    }
}