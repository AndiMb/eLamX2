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
 * @author Andreas Hauffe
 */
public class CLT_CalculatorTest {

    public CLT_CalculatorTest() {
    }

    @Test
    public void HSB37103_01() {

        DefaultMaterial mat = new DefaultMaterial(UUID.randomUUID().toString(), "Mat1", 132700.0, 9300.0, 0.28, 4600.0, 0.0, false);
        mat.putAdditionalValue(Puck.PSPD, 0.3);
        mat.putAdditionalValue(Puck.PSPZ, 0.35);
        mat.putAdditionalValue(Puck.A0, 0.5);
        mat.putAdditionalValue(Puck.LAMBDA_MIN, 0.5);

        List<Layer> layers = new ArrayList<>();

        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 70.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 90.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 10.0, 0.125));
        layers.add(new Layer(UUID.randomUUID().toString(), "Layer1", mat, 0.0, 0.125));

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
        assertEquals(12.64, strains.getEpsilon_x()*1000.0,0.005);
        assertEquals(-0.43, strains.getEpsilon_y()*1000.0,0.005);
        assertEquals(-7.42, strains.getGamma_xy()*1000.0,0.005);
        assertEquals(83.88, strains.getKappa_x()*1000.0,0.005);
        assertEquals(-6.58, strains.getKappa_y()*1000.0,0.005);
        assertEquals( 5.99, strains.getKappa_xy()*1000.0,0.005);
        
        CLT_LayerResult[] layerResults = CLT_Calculator.getLayerResults(clt_lam, loads, strains);
        
        /*
        Layer 4 lower
        */
        StressStrainState sss = layerResults[3].getSss_lower();
        double[] strain = sss.getStrain();
        assertEquals(-8.33, strain[0]*1000.0,0.005);
        assertEquals( 1.22, strain[1]*1000.0,0.005);
        assertEquals(-8.92, strain[2]*1000.0,0.005);
        double[] stress = sss.getStress();
        assertEquals(-1108.8, stress[0],0.05);
        assertEquals(  -10.4, stress[1],0.05);
        assertEquals(  -41.0, stress[2],0.05);
        
        /*
        Layer 4 upper
        */
        sss = layerResults[3].getSss_upper();
        strain = sss.getStrain();
        assertEquals( 2.15, strain[0]*1000.0,0.005);
        assertEquals( 0.40, strain[1]*1000.0,0.005);
        assertEquals(-8.17, strain[2]*1000.0,0.005);
        stress = sss.getStress();
        assertEquals(  288.0, stress[0],0.05);
        assertEquals(    9.3, stress[1],0.05);
        assertEquals(  -37.6, stress[2],0.05);
        
        /*
        Layer 3 lower
        */
        sss = layerResults[2].getSss_lower();
        strain = sss.getStrain();
        assertEquals( 0.70, strain[0]*1000.0,0.005);
        assertEquals( 1.85, strain[1]*1000.0,0.005);
        assertEquals(-8.28, strain[2]*1000.0,0.005);
        stress = sss.getStress();
        assertEquals(   98.3, stress[0],0.05);
        assertEquals(   19.1, stress[1],0.05);
        assertEquals(  -38.1, stress[2],0.05);
        
        /*
        Layer 3 upper
        */
        sss = layerResults[2].getSss_upper();
        strain = sss.getStrain();
        assertEquals(10.97, strain[0]*1000.0,0.005);
        assertEquals( 1.24, strain[1]*1000.0,0.005);
        assertEquals(-11.44, strain[2]*1000.0,0.005);
        stress = sss.getStress();
        assertEquals( 1467.2, stress[0],0.05);
        assertEquals(   40.3, stress[1],0.05);
        assertEquals(  -52.6, stress[2],0.05);
        
        /*
        Layer 2 lower
        */
        sss = layerResults[1].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.43, strain[0]*1000.0,0.005);
        assertEquals(12.64, strain[1]*1000.0,0.005);
        assertEquals( 7.42, strain[2]*1000.0,0.005);
        stress = sss.getStress();
        assertEquals(  -23.6, stress[0],0.05);
        assertEquals(  117.0, stress[1],0.05);
        assertEquals(   34.2, stress[2],0.05);
        
        /*
        Layer 2 upper
        */
        sss = layerResults[1].getSss_upper();
        strain = sss.getStrain();
        assertEquals(-1.25, strain[0]*1000.0,0.005);
        assertEquals(23.12, strain[1]*1000.0,0.005);
        assertEquals( 6.68, strain[2]*1000.0,0.005);
        stress = sss.getStress();
        assertEquals( -105.9, stress[0],0.05);
        assertEquals(  212.9, stress[1],0.05);
        assertEquals(   30.7, stress[2],0.05);
        
        /*
        Layer 1 lower
        */
        sss = layerResults[0].getSss_lower();
        strain = sss.getStrain();
        assertEquals(-0.54, strain[0]*1000.0,0.005);
        assertEquals(22.41, strain[1]*1000.0,0.005);
        assertEquals(-10.55, strain[2]*1000.0,0.005);
        stress = sss.getStress();
        assertEquals(  -13.7, stress[0],0.05);
        assertEquals(  208.2, stress[1],0.05);
        assertEquals(  -48.5, stress[2],0.05);
        
        /*
        Layer 1 upper
        */
        sss = layerResults[0].getSss_upper();
        strain = sss.getStrain();
        assertEquals( 0.20, strain[0]*1000.0,0.005);
        assertEquals(31.34, strain[1]*1000.0,0.005);
        assertEquals(-18.39, strain[2]*1000.0,0.005);
        stress = sss.getStress();
        assertEquals(  108.5, stress[0],0.05);
        assertEquals(  293.6, stress[1],0.05);
        assertEquals(  -84.6, stress[2],0.05);
    }
}
