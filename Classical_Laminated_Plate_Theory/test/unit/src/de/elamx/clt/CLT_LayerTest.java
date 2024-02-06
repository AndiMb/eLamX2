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

import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.Puck;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Florian Dexl
 */
public class CLT_LayerTest {

    public CLT_LayerTest() {
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der Steifigkeitsmatrix der Lage im lokalen Lagenkoordinatensystem
     */
    @Test
    public void HSB37103_01_E_layerStiffnessLocal() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        double[][] QMatLoc;

        for (CLT_Layer clt_lay: clt_lam.getCLTLayers()) {
            QMatLoc = clt_lay.getQMatLocal();
        
            assertEquals(133433.0, QMatLoc[0][0], 0.5);
            assertEquals(  2618.0, QMatLoc[0][1], 0.5);
            assertEquals(     0.0, QMatLoc[0][2], 0.5);
            assertEquals(  2618.0, QMatLoc[1][0], 0.5);
            assertEquals(  9351.0, QMatLoc[1][1], 0.5);
            assertEquals(     0.0, QMatLoc[1][2], 0.5);
            assertEquals(     0.0, QMatLoc[2][0], 0.5);
            assertEquals(     0.0, QMatLoc[2][1], 0.5);
            assertEquals(  4600.0, QMatLoc[2][2], 0.5);
        }
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der Steifigkeitsmatrix der Lage im globalen Koordinatensystem
     */
    @Test
    public void HSB37103_01_E_layerStiffnessGlobal() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);

        CLT_Layer[] clt_lay = clt_lam.getCLTLayers();
        
        double[][] QMatLoc;

        QMatLoc = clt_lay[3].getQMatGlobal();
        
        assertEquals(133433.0, QMatLoc[0][0], 0.5);
        assertEquals(  2618.0, QMatLoc[0][1], 0.5);
        assertEquals(     0.0, QMatLoc[0][2], 0.5);
        assertEquals(  2618.0, QMatLoc[1][0], 0.5);
        assertEquals(  9351.0, QMatLoc[1][1], 0.5);
        assertEquals(     0.0, QMatLoc[1][2], 0.5);
        assertEquals(     0.0, QMatLoc[2][0], 0.5);
        assertEquals(     0.0, QMatLoc[2][1], 0.5);
        assertEquals(  4600.0, QMatLoc[2][2], 0.5);

        QMatLoc = clt_lay[2].getQMatGlobal();
        
        assertEquals(126207.0, QMatLoc[0][0], 0.5);
        assertEquals(  6103.0, QMatLoc[0][1], 0.5);
        assertEquals( 20183.0, QMatLoc[0][2], 0.5);
        assertEquals(  6103.0, QMatLoc[1][0], 0.5);
        assertEquals(  9608.0, QMatLoc[1][1], 0.5);
        assertEquals(  1036.0, QMatLoc[1][2], 0.5);
        assertEquals( 20183.0, QMatLoc[2][0], 0.5);
        assertEquals(  1036.0, QMatLoc[2][1], 0.5);
        assertEquals(  8084.0, QMatLoc[2][2], 0.5);

        QMatLoc = clt_lay[1].getQMatGlobal();
        
        assertEquals(  9351.0, QMatLoc[0][0], 0.5);
        assertEquals(  2618.0, QMatLoc[0][1], 0.5);
        assertEquals(     0.0, QMatLoc[0][2], 0.5);
        assertEquals(  2618.0, QMatLoc[1][0], 0.5);
        assertEquals(133433.0, QMatLoc[1][1], 0.5);
        assertEquals(     0.0, QMatLoc[1][2], 0.5);
        assertEquals(     0.0, QMatLoc[2][0], 0.5);
        assertEquals(     0.0, QMatLoc[2][1], 0.5);
        assertEquals(  4600.0, QMatLoc[2][2], 0.5);

        QMatLoc = clt_lay[0].getQMatGlobal();
        
        assertEquals( 11559.0, QMatLoc[0][0], 0.5);
        assertEquals( 14926.0, QMatLoc[0][1], 0.5);
        assertEquals(  5272.0, QMatLoc[0][2], 0.5);
        assertEquals( 14926.0, QMatLoc[1][0], 0.5);
        assertEquals(106611.0, QMatLoc[1][1], 0.5);
        assertEquals( 34607.0, QMatLoc[1][2], 0.5);
        assertEquals(  5272.0, QMatLoc[2][0], 0.5);
        assertEquals( 34607.0, QMatLoc[2][1], 0.5);
        assertEquals( 16907.0, QMatLoc[2][2], 0.5);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der A-Matrix des Laminats
     */
    @Test
    public void HSB37103_01_E_AMatrix() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);
        
        double[][] AMat = clt_lam.getAMatrix();
        
        assertEquals(35069.0, AMat[0][0], 0.5);
        assertEquals( 3283.0, AMat[0][1], 0.5);
        assertEquals( 3182.0, AMat[0][2], 0.5);
        assertEquals( 3283.0, AMat[1][0], 0.5);
        assertEquals(32376.0, AMat[1][1], 0.5);
        assertEquals( 4455.0, AMat[1][2], 0.5);
        assertEquals( 3182.0, AMat[2][0], 0.5);
        assertEquals( 4455.0, AMat[2][1], 0.5);
        assertEquals( 4274.0, AMat[2][2], 0.5);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der B-Matrix des Laminats
     */
    @Test
    public void HSB37103_01_E_BMatrix() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);
        
        double[][] BMat = clt_lam.getBMatrix();
        
        assertEquals(-3769.0, BMat[0][0], 0.5);
        assertEquals(  261.0, BMat[0][1], 0.5);
        assertEquals(  -34.0, BMat[0][2], 0.5);
        assertEquals(  261.0, BMat[1][0], 0.5);
        assertEquals( 3247.0, BMat[1][1], 0.5);
        assertEquals(  803.0, BMat[1][2], 0.5);
        assertEquals(  -34.0, BMat[2][0], 0.5);
        assertEquals(  803.0, BMat[2][1], 0.5);
        assertEquals(  261.0, BMat[2][2], 0.5);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der D-Matrix des Laminats
     */
    @Test
    public void HSB37103_01_E_DMatrix() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);
        
        double[][] DMat = clt_lam.getDMatrix();
        
        assertEquals(749.0, DMat[0][0], 0.5);
        assertEquals( 86.0, DMat[0][1], 0.5);
        assertEquals( 37.0, DMat[0][2], 0.5);
        assertEquals( 86.0, DMat[1][0], 0.5);
        assertEquals(622.0, DMat[1][1], 0.5);
        assertEquals(158.0, DMat[1][2], 0.5);
        assertEquals( 37.0, DMat[2][0], 0.5);
        assertEquals(158.0, DMat[2][1], 0.5);
        assertEquals(106.0, DMat[2][2], 0.5);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der ABD-Matrix des Laminats
     */
    @Test
    public void HSB37103_01_E_ABDMatrix() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);
        
        double[][] ABDMat = clt_lam.getABDMatrix();
        
        assertEquals(35069.0, ABDMat[0][0], 0.5);
        assertEquals( 3283.0, ABDMat[0][1], 0.5);
        assertEquals( 3182.0, ABDMat[0][2], 0.5);
        assertEquals( 3283.0, ABDMat[1][0], 0.5);
        assertEquals(32376.0, ABDMat[1][1], 0.5);
        assertEquals( 4455.0, ABDMat[1][2], 0.5);
        assertEquals( 3182.0, ABDMat[2][0], 0.5);
        assertEquals( 4455.0, ABDMat[2][1], 0.5);
        assertEquals( 4274.0, ABDMat[2][2], 0.5);
        
        assertEquals(-3769.0, ABDMat[0][3], 0.5);
        assertEquals(  261.0, ABDMat[0][4], 0.5);
        assertEquals(  -34.0, ABDMat[0][5], 0.5);
        assertEquals(  261.0, ABDMat[1][3], 0.5);
        assertEquals( 3247.0, ABDMat[1][4], 0.5);
        assertEquals(  803.0, ABDMat[1][5], 0.5);
        assertEquals(  -34.0, ABDMat[2][3], 0.5);
        assertEquals(  803.0, ABDMat[2][4], 0.5);
        assertEquals(  261.0, ABDMat[2][5], 0.5);
        
        assertEquals(-3769.0, ABDMat[3][0], 0.5);
        assertEquals(  261.0, ABDMat[3][1], 0.5);
        assertEquals(  -34.0, ABDMat[3][2], 0.5);
        assertEquals(  261.0, ABDMat[4][0], 0.5);
        assertEquals( 3247.0, ABDMat[4][1], 0.5);
        assertEquals(  803.0, ABDMat[4][2], 0.5);
        assertEquals(  -34.0, ABDMat[5][0], 0.5);
        assertEquals(  803.0, ABDMat[5][1], 0.5);
        assertEquals(  261.0, ABDMat[5][2], 0.5);
        
        assertEquals(749.0, ABDMat[3][3], 0.5);
        assertEquals( 86.0, ABDMat[3][4], 0.5);
        assertEquals( 37.0, ABDMat[3][5], 0.5);
        assertEquals( 86.0, ABDMat[4][3], 0.5);
        assertEquals(622.0, ABDMat[4][4], 0.5);
        assertEquals(158.0, ABDMat[4][5], 0.5);
        assertEquals( 37.0, ABDMat[5][3], 0.5);
        assertEquals(158.0, ABDMat[5][4], 0.5);
        assertEquals(106.0, ABDMat[5][5], 0.5);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der inversen A-Matrix des Laminats
     */
    @Test
    public void HSB37103_01_E_aMatrix() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);
        
        double[][] aMat = clt_lam.getaMatrix();
        
        assertEquals( 7.22, aMat[0][0]*100000.0, 0.005);
        assertEquals(-0.37, aMat[0][1]*100000.0, 0.005);
        assertEquals(-4.82, aMat[0][2]*100000.0, 0.005);
        assertEquals(-0.37, aMat[1][0]*100000.0, 0.005);
        assertEquals( 6.54, aMat[1][1]*100000.0, 0.005);
        assertEquals(-0.25, aMat[1][2]*100000.0, 0.005);
        assertEquals(-4.82, aMat[2][0]*100000.0, 0.005);
        assertEquals(-0.25, aMat[2][1]*100000.0, 0.005);
        assertEquals(34.96, aMat[2][2]*100000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der inversen B-Matrix des Laminats
     */
    @Test
    public void HSB37103_01_E_bMatrix() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);
        
        double[][] bMat = clt_lam.getbMatrix();
        
        assertEquals( 36.09, bMat[0][0]*100000.0, 0.005);
        assertEquals( -1.53, bMat[0][1]*100000.0, 0.005);
        assertEquals(  6.63, bMat[0][2]*100000.0, 0.005);
        assertEquals( -0.36, bMat[1][0]*100000.0, 0.005);
        assertEquals(-34.16, bMat[1][1]*100000.0, 0.005);
        assertEquals(  2.15, bMat[1][2]*100000.0, 0.005);
        assertEquals(-17.37, bMat[2][0]*100000.0, 0.005);
        assertEquals(-30.89, bMat[2][1]*100000.0, 0.005);
        assertEquals(-33.44, bMat[2][2]*100000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der inversen D-Matrix des Laminats
     */
    @Test
    public void HSB37103_01_E_dMatrix() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);
        
        double[][] dMat = clt_lam.getdMatrix();
        
        assertEquals( 318.54, dMat[0][0]*100000.0, 0.005);
        assertEquals( -33.64, dMat[0][1]*100000.0, 0.005);
        assertEquals(  -4.25, dMat[0][2]*100000.0, 0.005);
        assertEquals( -33.64, dMat[1][0]*100000.0, 0.005);
        assertEquals( 478.03, dMat[1][1]*100000.0, 0.005);
        assertEquals(-367.15, dMat[1][2]*100000.0, 0.005);
        assertEquals(  -4.25, dMat[2][0]*100000.0, 0.005);
        assertEquals(-367.15, dMat[2][1]*100000.0, 0.005);
        assertEquals(1557.74, dMat[2][2]*100000.0, 0.005);
    }

    /**
     * KLT-Beispiel aus HSB 37103-01, Entwurf Issue E
     * Prüfung der inversen ABD-Matrix des Laminats
     */
    @Test
    public void HSB37103_01_E_InvABDMatrix() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer2", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer3", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer4", mat, 0.0, 0.125));

        Laminat lam = new Laminat(UUID.randomUUID().toString(), "Laminat1", false);

        lam.addLayers(layers);

        CLT_Laminate clt_lam = new CLT_Laminate(lam);
        
        double[][] InvABDMat = clt_lam.getInvABDMatrix();
        
        assertEquals( 7.22, InvABDMat[0][0]*100000.0, 0.005);
        assertEquals(-0.37, InvABDMat[0][1]*100000.0, 0.005);
        assertEquals(-4.82, InvABDMat[0][2]*100000.0, 0.005);
        assertEquals(-0.37, InvABDMat[1][0]*100000.0, 0.005);
        assertEquals( 6.54, InvABDMat[1][1]*100000.0, 0.005);
        assertEquals(-0.25, InvABDMat[1][2]*100000.0, 0.005);
        assertEquals(-4.82, InvABDMat[2][0]*100000.0, 0.005);
        assertEquals(-0.25, InvABDMat[2][1]*100000.0, 0.005);
        assertEquals(34.96, InvABDMat[2][2]*100000.0, 0.005);
        
        assertEquals( 36.09, InvABDMat[0][3]*100000.0, 0.005);
        assertEquals( -1.53, InvABDMat[0][4]*100000.0, 0.005);
        assertEquals(  6.63, InvABDMat[0][5]*100000.0, 0.005);
        assertEquals( -0.36, InvABDMat[1][3]*100000.0, 0.005);
        assertEquals(-34.16, InvABDMat[1][4]*100000.0, 0.005);
        assertEquals(  2.15, InvABDMat[1][5]*100000.0, 0.005);
        assertEquals(-17.37, InvABDMat[2][3]*100000.0, 0.005);
        assertEquals(-30.89, InvABDMat[2][4]*100000.0, 0.005);
        assertEquals(-33.44, InvABDMat[2][5]*100000.0, 0.005);
        
        assertEquals( 36.09, InvABDMat[3][0]*100000.0, 0.005);
        assertEquals( -0.36, InvABDMat[3][1]*100000.0, 0.005);
        assertEquals(-17.37, InvABDMat[3][2]*100000.0, 0.005);
        assertEquals( -1.53, InvABDMat[4][0]*100000.0, 0.005);
        assertEquals(-34.16, InvABDMat[4][1]*100000.0, 0.005);
        assertEquals(-30.89, InvABDMat[4][2]*100000.0, 0.005);
        assertEquals(  6.63, InvABDMat[5][0]*100000.0, 0.005);
        assertEquals(  2.15, InvABDMat[5][1]*100000.0, 0.005);
        assertEquals(-33.44, InvABDMat[5][2]*100000.0, 0.005);
        
        assertEquals( 318.54, InvABDMat[3][3]*100000.0, 0.005);
        assertEquals( -33.64, InvABDMat[3][4]*100000.0, 0.005);
        assertEquals(  -4.25, InvABDMat[3][5]*100000.0, 0.005);
        assertEquals( -33.64, InvABDMat[4][3]*100000.0, 0.005);
        assertEquals( 478.03, InvABDMat[4][4]*100000.0, 0.005);
        assertEquals(-367.15, InvABDMat[4][5]*100000.0, 0.005);
        assertEquals(  -4.25, InvABDMat[5][3]*100000.0, 0.005);
        assertEquals(-367.15, InvABDMat[5][4]*100000.0, 0.005);
        assertEquals(1557.74, InvABDMat[5][5]*100000.0, 0.005);
    }
}