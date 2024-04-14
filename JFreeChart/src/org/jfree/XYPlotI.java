
package org.jfree;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.XYDataset;


public class XYPlotI extends XYPlot {

    public static final int DOMAIN_AXIS = 1;
    public static final int RANGE_AXIS = 2;

    private boolean quadraticDataArea = true;
    private boolean equalAxes = false;
    private boolean equalTicks = false;

    private int dominantAxis = DOMAIN_AXIS;

    /**
     * Creates a new {@code XYPlot} instance with no dataset, no axes and
     * no renderer.  You should specify these items before using the plot.
     */
    public XYPlotI() {
        this(null, null, null, null);
    }

    /**
     * Creates a new plot with the specified dataset, axes and renderer.  Any
     * of the arguments can be {@code null}, but in that case you should
     * take care to specify the value before using the plot (otherwise a
     * {@code NullPointerException} may be thrown).
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param domainAxis  the domain axis ({@code null} permitted).
     * @param rangeAxis  the range axis ({@code null} permitted).
     * @param renderer  the renderer ({@code null} permitted).
     */
    public XYPlotI(XYDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis,
            XYItemRenderer renderer) {
        super(dataset, domainAxis, rangeAxis, renderer);
    }

    /**
     * Draws the plot within the specified area on a graphics device.
     *
     * @param g2  the graphics device.
     * @param area  the plot area (in Java2D space).
     * @param anchor  an anchor point in Java2D space ({@code null}
     *                permitted).
     * @param parentState  the state from the parent plot, if there is one
     *                     ({@code null} permitted).
     * @param info  collects chart drawing information ({@code null}
     *              permitted).
     */
    @Override
    @SuppressWarnings("unchecked")
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
            PlotState parentState, PlotRenderingInfo info) {

        // if the plot area is too small, just return...
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }

        // record the plot area...
        if (info != null) {
            info.setPlotArea(area);
        }

        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);

        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.getAxisOffset().trim(dataArea);
        
        /**
         * SUGGESTION START
         */
        if (equalAxes) {

            if (quadraticDataArea) {
                // Square the screen area to be displayed
                double dCenterX = dataArea.getCenterX();
                double dCenterY = dataArea.getCenterY();

                double dWidth = dataArea.getWidth();
                double dHeight = dataArea.getHeight();

                double dMinBreadth = Math.min(dWidth, dHeight);

                dataArea.setRect(dCenterX - dMinBreadth / 2d, dCenterY - dMinBreadth / 2d, dMinBreadth, dMinBreadth);

                // Determine data range
                ValueAxis horizontalAxis = getDomainAxis();
                ValueAxis verticalAxis = getRangeAxis();
                Range horizontalRange = horizontalAxis.getRange();
                Range verticalRange = verticalAxis.getRange();
                double dHorizontalDataRange = horizontalRange.getUpperBound() - horizontalRange.getLowerBound();
                double dVerticalDataRange = verticalRange.getUpperBound() - verticalRange.getLowerBound();
                double dLargerDataRange = Math.max(dHorizontalDataRange, dVerticalDataRange);

                // Center and scale the axes equally
                double dDataCenter, dMin, dMax;
                dDataCenter = verticalRange.getCentralValue();
                dMin = dDataCenter - (dLargerDataRange / 2);
                dMax = dDataCenter + (dLargerDataRange / 2);
                verticalAxis.setRange(new Range(dMin, dMax), false, false);

                dDataCenter = horizontalRange.getCentralValue();
                dMin = dDataCenter - (dLargerDataRange / 2);
                dMax = dDataCenter + (dLargerDataRange / 2);
                horizontalAxis.setRange(new Range(dMin, dMax), false, false);
            } else {

                double dWidth = dataArea.getWidth();
                double dHeight = dataArea.getHeight();
                double displayRatio = dWidth / dHeight;

                // Determine data range
                ValueAxis horizontalAxis = getDomainAxis();
                //horizontalAxis
                ValueAxis verticalAxis = getRangeAxis();

                autoAdjustRange((NumberAxis) horizontalAxis);
                autoAdjustRange((NumberAxis) verticalAxis);

                Range horizontalRange = horizontalAxis.getRange();
                Range verticalRange = verticalAxis.getRange();
                double dHorizontalDataRange = horizontalRange.getUpperBound() - horizontalRange.getLowerBound();
                double dVerticalDataRange = verticalRange.getUpperBound() - verticalRange.getLowerBound();
                double dataRatio = dHorizontalDataRange / dVerticalDataRange;

                if (dataRatio > displayRatio) {
                    dVerticalDataRange = dHorizontalDataRange / displayRatio;

                    double dDataCenter, dMin, dMax;
                    dDataCenter = verticalRange.getCentralValue();
                    dMin = dDataCenter - (dVerticalDataRange / 2);
                    dMax = dDataCenter + (dVerticalDataRange / 2);
                    verticalAxis.setRange(new Range(dMin, dMax), false, false);
                } else {
                    dHorizontalDataRange = dVerticalDataRange * displayRatio;

                    double dDataCenter, dMin, dMax;
                    dDataCenter = horizontalRange.getCentralValue();
                    dMin = dDataCenter - (dHorizontalDataRange / 2);
                    dMax = dDataCenter + (dHorizontalDataRange / 2);
                    horizontalAxis.setRange(new Range(dMin, dMax), false, false);
                }
            }
        }
        /**
         * SUGGESTION END
         */

        dataArea = integerise(dataArea);
        if (dataArea.isEmpty()) {
            return;
        }
        createAndAddEntity((Rectangle2D) dataArea.clone(), info, null, null);
        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        Map<?,?> axisStateMap = drawAxes(g2, area, dataArea, info);

        PlotOrientation orient = getOrientation();

        // the anchor point is typically the point where the mouse last
        // clicked - the crosshairs will be driven off this point...
        if (anchor != null && !dataArea.contains(anchor)) {
            anchor = null;
        }
        CrosshairState crosshairState = new CrosshairState();
        crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairState.setAnchor(anchor);

        crosshairState.setAnchorX(Double.NaN);
        crosshairState.setAnchorY(Double.NaN);
        if (anchor != null) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                double x;
                if (orient == PlotOrientation.VERTICAL) {
                    x = domainAxis.java2DToValue(anchor.getX(), dataArea,
                            getDomainAxisEdge());
                }
                else {
                    x = domainAxis.java2DToValue(anchor.getY(), dataArea,
                            getDomainAxisEdge());
                }
                crosshairState.setAnchorX(x);
            }
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                double y;
                if (orient == PlotOrientation.VERTICAL) {
                    y = rangeAxis.java2DToValue(anchor.getY(), dataArea,
                            getRangeAxisEdge());
                }
                else {
                    y = rangeAxis.java2DToValue(anchor.getX(), dataArea,
                            getRangeAxisEdge());
                }
                crosshairState.setAnchorY(y);
            }
        }
        crosshairState.setCrosshairX(getDomainCrosshairValue());
        crosshairState.setCrosshairY(getRangeCrosshairValue());
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getForegroundAlpha()));

        /**
         * SUGGESTION START
         */
        AxisState domainAxisState;
        AxisState rangeAxisState;
        if (equalTicks && quadraticDataArea) {
            switch (dominantAxis) {
                case DOMAIN_AXIS:
                    domainAxisState = (AxisState) axisStateMap.get(getDomainAxis());
                    rangeAxisState = (AxisState) axisStateMap.get(getDomainAxis());
                    break;
                case RANGE_AXIS:
                    domainAxisState = (AxisState) axisStateMap.get(getRangeAxis());
                    rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());
                    break;
                default:
                    domainAxisState = (AxisState) axisStateMap.get(getDomainAxis());
                    rangeAxisState = (AxisState) axisStateMap.get(getDomainAxis());
                    break;
            }
        } else {
            domainAxisState = (AxisState) axisStateMap.get(getDomainAxis());
            rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());
        }
        
        /**
         * SUGGESTION END
         */

        /*AxisState domainAxisState = (AxisState) axisStateMap.get(
                getDomainAxis());*/
        if (domainAxisState == null) {
            if (parentState != null) {
                domainAxisState = (AxisState) parentState.getSharedAxisStates()
                        .get(getDomainAxis());
            }
        }

        /*AxisState rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());*/
        if (rangeAxisState == null) {
            if (parentState != null) {
                rangeAxisState = (AxisState) parentState.getSharedAxisStates()
                        .get(getRangeAxis());
            }
        }
        if (domainAxisState != null) {
            drawDomainTickBands(g2, dataArea, domainAxisState.getTicks());
        }
        if (rangeAxisState != null) {
            drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks());
        }
        if (domainAxisState != null) {
            drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
            drawZeroDomainBaseline(g2, dataArea);
        }
        if (rangeAxisState != null) {
            drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
            drawZeroRangeBaseline(g2, dataArea);
        }

        Graphics2D savedG2 = g2;
        BufferedImage dataImage = null;
        boolean suppressShadow = Boolean.TRUE.equals(g2.getRenderingHint(
                JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION));
        if (this.getShadowGenerator() != null && !suppressShadow) {
            dataImage = new BufferedImage((int) dataArea.getWidth(),
                    (int)dataArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
            g2 = dataImage.createGraphics();
            g2.translate(-dataArea.getX(), -dataArea.getY());
            g2.setRenderingHints(savedG2.getRenderingHints());
        }

        // draw the markers that are associated with a specific dataset...
        //for (XYDataset dataset: this.datasets.values()) {
        for (int ii = 0; ii < this.getDatasetCount(); ii++){
            XYDataset dataset = this.getDataset(ii);
            int datasetIndex = indexOf(dataset);
            drawDomainMarkers(g2, dataArea, datasetIndex, Layer.BACKGROUND);
        }
        //for (XYDataset dataset: this.datasets.values()) {
        for (int ii = 0; ii < this.getDatasetCount(); ii++){
            XYDataset dataset = this.getDataset(ii);
            int datasetIndex = indexOf(dataset);
            drawRangeMarkers(g2, dataArea, datasetIndex, Layer.BACKGROUND);
        }

        // now draw annotations and render data items...
        boolean foundData = false;
        DatasetRenderingOrder order = getDatasetRenderingOrder();
        List<Integer> rendererIndices = getRendererIndices(order);
        List<Integer> datasetIndices = getDatasetIndices(order);

        // draw background annotations
        for (int i : rendererIndices) {
            XYItemRenderer renderer = getRenderer(i);
            if (renderer != null) {
                ValueAxis domainAxis = getDomainAxisForDataset(i);
                ValueAxis rangeAxis = getRangeAxisForDataset(i);
                renderer.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, 
                        Layer.BACKGROUND, info);
            }
        }

        // render data items...
        for (int datasetIndex : datasetIndices) {
            XYDataset dataset = this.getDataset(datasetIndex);
            foundData = render(g2, dataArea, datasetIndex, info, 
                    crosshairState) || foundData;
        }

        // draw foreground annotations
        for (int i : rendererIndices) {
            XYItemRenderer renderer = getRenderer(i);
            if (renderer != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                renderer.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, 
                            Layer.FOREGROUND, info);
            }
        }

        // draw domain crosshair if required...
        int datasetIndex = crosshairState.getDatasetIndex();
        ValueAxis xAxis = getDomainAxisForDataset(datasetIndex);
        RectangleEdge xAxisEdge = getDomainAxisEdge(getDomainAxisIndex(xAxis));
        if (!this.isDomainCrosshairLockedOnData() && anchor != null) {
            double xx;
            if (orient == PlotOrientation.VERTICAL) {
                xx = xAxis.java2DToValue(anchor.getX(), dataArea, xAxisEdge);
            }
            else {
                xx = xAxis.java2DToValue(anchor.getY(), dataArea, xAxisEdge);
            }
            crosshairState.setCrosshairX(xx);
        }
        setDomainCrosshairValue(crosshairState.getCrosshairX(), false);
        if (isDomainCrosshairVisible()) {
            double x = getDomainCrosshairValue();
            Paint paint = getDomainCrosshairPaint();
            Stroke stroke = getDomainCrosshairStroke();
            drawDomainCrosshair(g2, dataArea, orient, x, xAxis, stroke, paint);
        }

        // draw range crosshair if required...
        ValueAxis yAxis = getRangeAxisForDataset(datasetIndex);
        RectangleEdge yAxisEdge = getRangeAxisEdge(getRangeAxisIndex(yAxis));
        if (!this.isRangeCrosshairLockedOnData() && anchor != null) {
            double yy;
            if (orient == PlotOrientation.VERTICAL) {
                yy = yAxis.java2DToValue(anchor.getY(), dataArea, yAxisEdge);
            } else {
                yy = yAxis.java2DToValue(anchor.getX(), dataArea, yAxisEdge);
            }
            crosshairState.setCrosshairY(yy);
        }
        setRangeCrosshairValue(crosshairState.getCrosshairY(), false);
        if (isRangeCrosshairVisible()) {
            double y = getRangeCrosshairValue();
            Paint paint = getRangeCrosshairPaint();
            Stroke stroke = getRangeCrosshairStroke();
            drawRangeCrosshair(g2, dataArea, orient, y, yAxis, stroke, paint);
        }

        if (!foundData) {
            drawNoDataMessage(g2, dataArea);
        }

        for (int i : rendererIndices) { 
            drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (int i : rendererIndices) {
            drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }

        drawAnnotations(g2, dataArea, info);
        if (this.getShadowGenerator() != null && !suppressShadow) {
            BufferedImage shadowImage
                    = this.getShadowGenerator().createDropShadow(dataImage);
            g2 = savedG2;
            g2.drawImage(shadowImage, (int) dataArea.getX()
                    + this.getShadowGenerator().calculateOffsetX(),
                    (int) dataArea.getY()
                    + this.getShadowGenerator().calculateOffsetY(), null);
            g2.drawImage(dataImage, (int) dataArea.getX(),
                    (int) dataArea.getY(), null);
        }
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);

        drawOutline(g2, dataArea);
    }

    public boolean useQuadraticDataArea(){
        return quadraticDataArea;
    }
    
    public void useQuadraticDataArea(boolean quadraticDataArea){
        this.quadraticDataArea = quadraticDataArea;
    }

    /**
     * Determine whether equal axes are being used for the display
     */
    public boolean useEqualAxes() {
        return equalAxes;
    }

    /**
     * Specify whether or not to use equal axes when plotting
     */
    public void useEqualAxes(boolean equalAxes) {
        this.equalAxes = equalAxes;

        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, null);
        datasetChanged(event);
    }

    public boolean useEqualTicks() {
        return equalTicks;
    }

    public void useEqualTicks(boolean equalTicks) {
        this.equalTicks = equalTicks;

        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, null);
        datasetChanged(event);
    }

    public void setDominantAxis(int axis) {
        dominantAxis = axis;
    }

    public int getDominantAxis() {
        return dominantAxis;
    }

    private void autoAdjustRange(NumberAxis axis) {

        ValueAxisPlot vap = (ValueAxisPlot) this;

        Range r = vap.getDataRange(axis);
        if (r == null) {
            r = axis.getDefaultAutoRange();
        }

        double upper = r.getUpperBound();
        double lower = r.getLowerBound();
        if (axis.getRangeType() == RangeType.POSITIVE) {
            lower = Math.max(0.0, lower);
            upper = Math.max(0.0, upper);
        } else if (axis.getRangeType() == RangeType.NEGATIVE) {
            lower = Math.min(0.0, lower);
            upper = Math.min(0.0, upper);
        }

        if (axis.getAutoRangeIncludesZero()) {
            lower = Math.min(lower, 0.0);
            upper = Math.max(upper, 0.0);
        }
        double range = upper - lower;

        // if fixed auto range, then derive lower bound...
        double fixedAutoRange = axis.getFixedAutoRange();
        if (fixedAutoRange > 0.0) {
            lower = upper - fixedAutoRange;
        } else {
            // ensure the autorange is at least <minRange> in size...
            double minRange = axis.getAutoRangeMinimumSize();
            if (range < minRange) {
                double expand = (minRange - range) / 2;
                upper = upper + expand;
                lower = lower - expand;
                if (lower == upper) { // see bug report 1549218
                    double adjust = Math.abs(lower) / 10.0;
                    lower = lower - adjust;
                    upper = upper + adjust;
                }
                if (axis.getRangeType() == RangeType.POSITIVE) {
                    if (lower < 0.0) {
                        upper = upper - lower;
                        lower = 0.0;
                    }
                } else if (axis.getRangeType() == RangeType.NEGATIVE) {
                    if (upper > 0.0) {
                        lower = lower - upper;
                        upper = 0.0;
                    }
                }
            }

            if (axis.getAutoRangeStickyZero()) {
                if (upper <= 0.0) {
                    upper = Math.min(0.0, upper + axis.getUpperMargin() * range);
                } else {
                    upper = upper + axis.getUpperMargin() * range;
                }
                if (lower >= 0.0) {
                    lower = Math.max(0.0, lower - axis.getLowerMargin() * range);
                } else {
                    lower = lower - axis.getLowerMargin() * range;
                }
            } else {
                upper = upper + axis.getUpperMargin() * range;
                lower = lower - axis.getLowerMargin() * range;
            }
        }

        axis.setRange(new Range(lower, upper), false, false);
    }

    /**
     * Trims a rectangle to integer coordinates.
     *
     * @param rect  the incoming rectangle.
     *
     * @return A rectangle with integer coordinates.
     */
    private Rectangle integerise(Rectangle2D rect) {
        int x0 = (int) Math.ceil(rect.getMinX());
        int y0 = (int) Math.ceil(rect.getMinY());
        int x1 = (int) Math.floor(rect.getMaxX());
        int y1 = (int) Math.floor(rect.getMaxY());
        return new Rectangle(x0, y0, (x1 - x0), (y1 - y0));
    }

    /**
     * Returns the indices of the non-null datasets in the specified order.
     * 
     * @param order  the order ({@code null} not permitted).
     * 
     * @return The list of indices. 
     */
    private List<Integer> getDatasetIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList<Integer>();
        int index = 0;
        while (result.size() < this.getDatasetCount()){
            if (this.getDataset(index) != null){
                result.add(index++);
            }
        }
        Collections.sort(result);
        if (order == DatasetRenderingOrder.REVERSE) {
            Collections.reverse(result);
        }
        return result;
    }
    
    private List<Integer> getRendererIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList<Integer>();
        int index = 0;
        while (result.size() < this.getRendererCount()){
            if (this.getRenderer(index) != null){
                result.add(index++);
            }
        }
        Collections.sort(result);
        if (order == DatasetRenderingOrder.REVERSE) {
            Collections.reverse(result);
        }
        return result;        
    }
}
