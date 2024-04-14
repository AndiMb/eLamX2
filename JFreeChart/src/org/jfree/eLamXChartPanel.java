/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfree;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author Andreas
 */
public class eLamXChartPanel extends ChartPanel{
    
    private static final int DEFAULT_MINIMUM_DRAW_WIDTH  = 100;
    private static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 100;
    
    private static final int DEFAULT_MAXIMUM_DRAW_WIDTH  = 3840;
    private static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 2160;

    @SuppressWarnings("this-escape")
    public eLamXChartPanel(JFreeChart chart) {
        super(chart);

        setMinimumDrawWidth(DEFAULT_MINIMUM_DRAW_WIDTH);
        setMinimumDrawHeight(DEFAULT_MINIMUM_DRAW_HEIGHT);
        setMaximumDrawWidth(DEFAULT_MAXIMUM_DRAW_WIDTH);
        setMaximumDrawHeight(DEFAULT_MAXIMUM_DRAW_HEIGHT);
        setPopupMenu(null);

        setDomainZoomable(false);
        setRangeZoomable(false);
    }
    
}
