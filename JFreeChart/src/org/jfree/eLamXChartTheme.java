/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfree;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.openide.util.NbPreferences;

/**
 *
 * @author Andreas
 */
public class eLamXChartTheme extends StandardChartTheme{
    
    private static eLamXChartTheme instance;
    
    public static final String PROP_PLOT_BACKGROUND_PAINT = "PlotBackgroundPaint";
    public static final String PROP_RANGE_GRID_PAINT = "RangeGridlinePaint";
    public static final String PROP_DOMAIN_GRID_PAINT = "DomainGridlinePaint";

    private eLamXChartTheme() {
        this(false);
    }

    private eLamXChartTheme( boolean shadow) {
        super("eLamX-Theme", shadow);
        
        Font font = (new JLabel()).getFont();
        
        this.setSmallFont(new Font(font.getFontName(), font.getStyle(), font.getSize()-2));
        this.setRegularFont(font);
        this.setLargeFont(new Font(font.getFontName(), Font.BOLD, font.getSize()+2));
        this.setExtraLargeFont(new Font(font.getFontName(), Font.BOLD, font.getSize()+6));
        
        this.setPlotBackgroundPaint(new Color(NbPreferences.forModule(eLamXChartTheme.class).getInt(PROP_PLOT_BACKGROUND_PAINT, (Color.white).getRGB())));
        this.setRangeGridlinePaint(new Color(NbPreferences.forModule(eLamXChartTheme.class).getInt(PROP_RANGE_GRID_PAINT, (Color.black).getRGB())));
        this.setDomainGridlinePaint(new Color(NbPreferences.forModule(eLamXChartTheme.class).getInt(PROP_DOMAIN_GRID_PAINT, (Color.black).getRGB())));
    }

    public void apply(JFreeChart chart) {
        super.apply(chart);
        
        chart.getPlot().setDrawingSupplier(new eLamXDrawingSupplier());
        
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);
        chart.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
    }
    
    public static eLamXChartTheme getInstance(){
        if (instance == null){
            instance = new eLamXChartTheme();
        }
        return instance;
    }

    @Override
    public void setPlotBackgroundPaint(Paint paint) {
        NbPreferences.forModule(eLamXChartTheme.class).putInt(PROP_PLOT_BACKGROUND_PAINT, ((Color)paint).getRGB());
        super.setPlotBackgroundPaint(paint); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRangeGridlinePaint(Paint paint) {
        NbPreferences.forModule(eLamXChartTheme.class).putInt(PROP_RANGE_GRID_PAINT, ((Color)paint).getRGB());
        super.setRangeGridlinePaint(paint); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDomainGridlinePaint(Paint paint) {
        NbPreferences.forModule(eLamXChartTheme.class).putInt(PROP_DOMAIN_GRID_PAINT, ((Color)paint).getRGB());
        super.setDomainGridlinePaint(paint); //To change body of generated methods, choose Tools | Templates.
    }
}
