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
package de.elamx.clt.plate.AdditionalCutoutGeometries;

import de.elamx.clt.cutout.CutoutGeometry;
import de.elamx.core.propertyeditor.CutoutTermPropertyEditorSupport;
import de.elamx.core.propertyeditor.ThicknessPropertyEditorSupport;
import java.awt.Image;
import java.beans.PropertyEditorSupport;
import java.util.Locale;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author raedel
 */
@ServiceProvider(service=CutoutGeometry.class)
public class RectangularCutoutGeometry extends CutoutGeometry{
    
    private static final double MAX_ASPECT_RATIO = 100.0;
    
    public static final String PROP_TERM     = "Terme";
    
    // Konstaten zeilenweise unter Ausnutzung Symmetrie
    private static final double[][] cs = new double[][] {{0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {5.0000000000000000E-01,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {4.1666666666666660E-02,	-8.3333333333333320E-02,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {1.2500000000000000E-02,	-1.2500000000000000E-02,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {5.5803571428571420E-03,	-4.4642857142857135E-03,	-2.2321428571428568E-03,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {3.0381944444444440E-03,	-2.1701388888888890E-03,	-8.6805555555555550E-04,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {1.8643465909090910E-03,	-1.2428977272727273E-03,	-4.4389204545454550E-04,	-3.5511363636363638E-04,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {1.2394831730769231E-03,	-7.8876201923076925E-04,	-2.6292067307692313E-04,	-1.8780048076923077E-04,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {8.7280273437499980E-04,	-5.3710937500000000E-04,	-1.7089843749999998E-04,	-1.1393229166666667E-04,	-1.0172526041666667E-04,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {6.4176671645220580E-04,	-3.8506002987132350E-04,	-1.1848000919117647E-04,	-7.5396369485294110E-05,	-6.2830307904411760E-05,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {4.8808047645970390E-04,	-2.8710616262335520E-04,	-8.6131848787006580E-05,	-5.3004214638157885E-05,	-4.2162443462171040E-05,	-3.9351613898026314E-05,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {3.8137890043712790E-04,	-2.2079831077938987E-04,	-6.4940679640997010E-05,	-3.8964407784598207E-05,	-2.9972621372767850E-05,	-2.6702880859375000E-05,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {3.0468857806661855E-04,	-1.7410775889521060E-04,	-5.0399614417034650E-05,	-2.9646832010020376E-05,	-2.2235124007515283E-05,	-1.9156414529551629E-05,	-1.8285668414572010E-05,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {2.4796962738037105E-04,	-1.4015674591064453E-04,	-4.0044784545898440E-05,	-2.3183822631835938E-05,	-1.7046928405761717E-05,	-1.4319419860839844E-05,	-1.3217926025390623E-05,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {2.0500134538721153E-04,	-1.1480075341683846E-04,	-3.2443691183019560E-05,	-1.8539252104582606E-05,	-1.3416564023053204E-05,	-1.1048935077808520E-05,	-9.9440415700276680E-06,	-9.6162160237630200E-06,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {1.7177698941066342E-04,	-9.5431660783701910E-05,	-2.6720865019436540E-05,	-1.5103097619681523E-05,	-1.0787926871201086E-05,	-8.7438986219208800E-06,	-7.7152046664007770E-06,	-7.2743358283207336E-06,	 0.0000000000000000E+00,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {1.4562948396609672E-04,	-8.0347301498536120E-05,	-2.2318694860704480E-05,	-1.2498469121994509E-05,	-8.8304401405396000E-06,	-7.0643521124316800E-06,	-6.1348320976380380E-06,	-5.6708531994973460E-06,	-5.5290818695099130E-06,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {1.2473256157880480E-04,	-6.8401727317409060E-05,	-1.8869442018595606E-05,	-1.0483023343664226E-05,	-7.3381163405649590E-06,	-5.8066833651427070E-06,	-4.9771571701223200E-06,	-4.5280903577804565E-06,	-4.3283216655254360E-06,	 0.0000000000000000E+00,	 0.0000000000000000E+00},
                                                          {1.0780457107882413E-04,	-5.8802493315722260E-05,	-1.6123264296246422E-05,	-8.8955940944807850E-06,	-6.1774958989449910E-06,	-4.8431567847728730E-06,	-4.1061546653509140E-06,	-3.6871592913355147E-06,	-3.4688406490853856E-06,	-3.4008241657699860E-06,	 0.0000000000000000E+00},
                                                          {9.3926457732261440E-05,	-5.0988648483227630E-05,	-1.3905995040880264E-05,	-7.6258682482246610E-06,	-5.2592194815342490E-06,	-4.0905040411933060E-06,	-3.4360233946023760E-06,	-3.0518717107337876E-06,	-2.8338808742528030E-06,	-2.7344464576123535E-06,	 0.0000000000000000E+00},
                                                          {8.2426487586837100E-05,	-4.4554858155047095E-05,	-1.2093461499227065E-05,	-6.5964335450329460E-06,	-4.5217488010306480E-06,	-3.4926611428650520E-06,	-2.9105509523875440E-06,	-2.5612848381010385E-06,	-2.3524844436906280E-06,	-2.2404613749434548E-06,	-2.2050856690232950E-06}};


    // Absolute Konstanten fuer gerade Zeilen       i=0: t^-1                  i=1: t^1 -> -                   i=2: t^3                        i=3: t^5 -> -                   i=4: t^7                        i=5: t^9 -> -                   i=6: t^11                       i=7: t^13 -> -                  i=8: t^15                       i=9: t^17 -> -                  i=10: t^19                 i=11: t^21 -> -              i=12: t^23                 i=13: t^25 -> -              i=14: t^27                 i=15: t^29 -> -              i=16: t^31                 i=17: t^33 -> -              i=18: t^35                 i=19: t^37 -> -              i=20: t^39
    //private final double[] ks_ = new double[]{      1.0000000000000000E+00,	 0.0000000000000000E+00,	-8.3333333333333320E-02,         0.0000000000000000E+00,        -2.2321428571428568E-03,         0.0000000000000000E+00,	-3.5511363636363638E-04,         0.0000000000000000E+00,	-1.0172526041666667E-04,         0.0000000000000000E+00,	-3.9351613898026314E-05,    0.0000000000000000E+00,	-1.8285668414572010E-05,    0.0000000000000000E+00,	-9.6162160237630200E-06,    0.0000000000000000E+00,	-5.5290818695099130E-06,    0.0000000000000000E+00,	-3.4008241657699860E-06,    0.0000000000000000E+00,	-2.2050856690232950E-06};
    private static final double[] ks = new double[]{      0.0000000000000000E+00,	 0.0000000000000000E+00,	-8.3333333333333320E-02,         0.0000000000000000E+00,        -2.2321428571428568E-03,         0.0000000000000000E+00,	-3.5511363636363638E-04,         0.0000000000000000E+00,	-1.0172526041666667E-04,         0.0000000000000000E+00,	-3.9351613898026314E-05,    0.0000000000000000E+00,	-1.8285668414572010E-05,    0.0000000000000000E+00,	-9.6162160237630200E-06,    0.0000000000000000E+00,	-5.5290818695099130E-06,    0.0000000000000000E+00,	-3.4008241657699860E-06,    0.0000000000000000E+00,	-2.2050856690232950E-06};

    protected int terme;
    
    private static Property[] props;
    
    public RectangularCutoutGeometry(){
        this(NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.description"), 10.0, 1.0, 11);
    }
    
    public RectangularCutoutGeometry(String name, double a, double b, int terme) {
        super(name, a, b);
        this.terme = terme;
        if (props == null) initProperties();
    }
    
    /**
     * @param val number of terms to set
     */
    public void setTerme(int val) {
        double oldterme = terme;
        terme = val;
        propertyChangeSupport.firePropertyChange(PROP_TERM, oldterme, val);
    }

    /**
     * @return number of terms
     */
    public int getTerme() {return terme;}

    @Override
    public void calcConstants() {
        
        // Seitenverhaeltnis der Platte
        double sv = a/b;
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Mapping-Exponent k
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        // Funktioneller Zusammenhang zwischen Seitenverhältnis und Mapping-Exponent k
        // aus Formeloptimierung
        
        /*
        double c1 =  0.38411723614861731E+00;
        double c2 = -0.77660240231989019E+00;
        double c3 =  0.24435538973186077E+00;
        double c4 =  0.24994391279313621E+00;
        
        double k = c1*Math.tanh(c2*Math.tanh(c3*Math.log(sv)))+c4;
        */
        
        double c1 =  0.31721563872923131E+00;
        double c2 =  0.21503777636095087E+01;
        double c3 = -0.21564914207141888E+01;
        double c4 =  0.21448661255846596E+00;
        double c5 = -0.23046158430458372E+00;
        double c6 = -0.10000112709518594E+01;
        double c7 = -0.50027197439674209E+01;
        double c8 = -0.25000052791860594E+00;
        
        double k = c1*Math.sin(c2*Math.tanh(c3*Math.tanh(c4*Math.tanh(c5*Math.log(Math.abs(c6*sv)))/Math.tanh(c7))))-c8;
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Konstanten m
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        double[] nzcs = new double[terme];
        
        // Schleife über Konstanten
        for (int ii = 0; ii < terme; ii++){
            nzcs[ii] = 0.0;
            
            // Schleife über Spalten
            for (int jj = 0; jj <= ii; jj++){
                if (2*jj>=ii) {break;}
                nzcs[ii] += cs[ii][jj]*2.0*Math.cos((ii-2*jj)*2.0*k*Math.PI);
            }
            
            // Absolute Terme dazu
            nzcs[ii] += ks[ii];
        }
        
        constants = new double[(terme-1)*2];
        for (int ii = 0 ; ii < nzcs.length-1; ii++){
            constants[2*ii  ] = 0.0;
            constants[2*ii+1] = nzcs[ii+1];
        }
        
    }
    
    /**
     * Methode berechnet fuer Werte k der Mapping-Funktion die entsprechenden
     * Seitenverhaeltnisse von Rechtecken. Diese Werte koennen dann im Rahmen 
     * der Formeloptimierung fuer eine Approximation des Wertes k in Abhaengigkeit
     * des Seitenverhaeltnisses genutzt werden.
     * 
     * Mit min = 3.5E-2 und max = 4.65E-2 sind die Werte so gewaehlt, dass
     * Seitenverhaeltnisse a/b im Bereich von 0.01...100 abgebildet werden koennen.
     * 
     * Die Methode wird in der Berechnung selbst nicht verwendet.
     * Aufruf: ((RectangularCutoutGeometry)input.getCutoutGeometry()).printAspectRatios();
     * 
     */
    public void printAspectRatios(){
        
        int terme = 21, count = 1;
        double a,b, fac, k;
        
        double min = 3.50E-2;
        double max = 4.65E-1;
        double inc = 4.50E-5;
        
        k = min;
        
        System.out.println(String.format("%1$"+ 27 + "s", "k") +
                           //String.format("%1$"+ 27 + "s", "a") +
                           //String.format("%1$"+ 27 + "s", "b") +
                           String.format("%1$"+ 27 + "s", "a/b"));
        
        while(true){
            
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Konstanten m
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

            double[] constants = new double[terme];

            // Schleife über Konstanten
            for (int ii = 0; ii < terme; ii++){
                constants[ii] = 0.0;

                // Schleife über Spalten
                for (int jj = 0; jj <= ii; jj++){
                    if (2*jj>=ii) {break;}
                    constants[ii] += cs[ii][jj]*2.0*Math.cos((ii-2*jj)*2.0*k*Math.PI);
                }

                // Absolute Terme dazu
                constants[ii] += ks[ii];
            }
            
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Seitenverhaeltnis berechnen
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            
            a = 0.0;
            b = 0.0;
            
            for (int ii = 0; ii < constants.length; ii++){
                a += constants[ii];

                if (ii%2 == 0) {fac =  1.0;}
                else           {fac = -1.0;}

                b += constants[ii]*fac;
            }
            
            // Ausgabe
            System.out.println(String.format(Locale.ENGLISH,"%27.16E" ,k) +
                               //String.format(Locale.ENGLISH,"%27.16E" ,a) +
                               //String.format(Locale.ENGLISH,"%27.16E" ,b) +
                               String.format(Locale.ENGLISH,"%27.16E" ,(a/b)));
            
            // hochzaehlen
            k += inc;
            
            // Abbruchkriterium
            if (k > max-inc){break;}
            
            count += 1;
        
        }
        
        System.out.println("Anzahl Werte: " + (count));
        System.out.println("sollte kleiner 10000 sein, damit Formeloptimierung funktioniert");
        
    }

    @Override
    public double[][] getSimplifiedGeometry(double[] angles) {
        
        double[][] xy = new double[2][angles.length];
        
        double thetaecke = Math.toDegrees(Math.atan(b/a));
        
        int anzahlWQ = 0;   // Anzahl Winkel innerhalb eines Quatranten
        for (int i = 0; angles[i] <= 90; i++){
            //Koordinaten für 1.Quatrant
            if (angles[i] <= thetaecke){
                xy[0][i] = a;
                xy[1][i] = a*Math.tan(Math.toRadians(angles[i]));
            }else{
                xy[0][i] = b/Math.tan(Math.toRadians(angles[i]));
                xy[1][i] = b; 
            }
            anzahlWQ = i;
        }
        for (int i = 0; i <= anzahlWQ; i++){
            //x-Koordinaten im 2., 3. und 4. Quatranten
            xy[0][anzahlWQ*2 - i] = -xy[0][i];
            xy[0][anzahlWQ*2 + i] = -xy[0][i];
            xy[0][anzahlWQ*4 - i] =  xy[0][i];
            //y-Koordinaten im 2., 3. und 4. Quatranten
            xy[1][anzahlWQ*2 - i] =  xy[1][i];
            xy[1][anzahlWQ*2 + i] = -xy[1][i];
            xy[1][anzahlWQ*4 - i] = -xy[1][i];
        }
        
        return xy;
    }
    
    //@Override
    private void initProperties(){
        props = new Property[4];
        
        props[0] = new Property(PROP_NAME, String.class, 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.name"), 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.name.shortDescription"), 
                PropertyEditorSupport.class);
        props[1] = new Property(PROP_A, double.class, 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.a"), 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.a.shortDescription"), 
                ThicknessPropertyEditorSupport.class);
        props[2] = new Property(PROP_B, double.class, 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.b"), 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.b.shortDescription"), 
                ThicknessPropertyEditorSupport.class);
        props[3] = new Property(PROP_TERM, int.class, 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.terme"), 
                NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.terme.shortDescription"),
                CutoutTermPropertyEditorSupport.class);
    }

    @Override
    public RectangularCutoutGeometry getCopy() {
        return new RectangularCutoutGeometry(getName(), a, b, terme);
    }
    
    @Override
    public String getDisplayName(){
        return NbBundle.getMessage(RectangularCutoutGeometry.class, "RectangularCutoutGeometry.displayname");
    };
    
    @Override
    public double getMaximumAspectRatio() {return MAX_ASPECT_RATIO;}
    
    @Override
    public Property[] getPropertyDefinitions() {return props;}
    

    @Override
    public ImageIcon getImage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Image getNodeIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
