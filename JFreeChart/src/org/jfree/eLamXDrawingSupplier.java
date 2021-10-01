/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfree;

import java.awt.BasicStroke;
import java.awt.Stroke;
import org.jfree.chart.plot.DefaultDrawingSupplier;

/**
 *
 * @author Andreas
 */
public class eLamXDrawingSupplier extends DefaultDrawingSupplier {

    public eLamXDrawingSupplier() {
        super(DEFAULT_PAINT_SEQUENCE, DEFAULT_FILL_PAINT_SEQUENCE,
             DEFAULT_OUTLINE_PAINT_SEQUENCE,
             new Stroke[] {new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)},
             DEFAULT_OUTLINE_STROKE_SEQUENCE,
             DEFAULT_SHAPE_SEQUENCE);
    }
    
    
}
