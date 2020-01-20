package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import uk.ac.leeds.ccg.math.Math_Double;

public class Geotools_LegendLayer extends DirectLayer {

    private final Geotools_StyleParameters styleParameters;

    private final List<Geotools_LegendItem> legendItems;
    private final MapContent mc;
    ReferencedEnvelope bounds;

    private final int originalImageWidth;
    private final int originalImageHeight;
    private final boolean addLegendToTheSide;
    private final String legendTitle;

    private AffineTransform at;
    private FontRenderContext frc;
    //private final FontMetrics mapTitleFontMetrics;

    private final Font legendTitleFont;
    //private final FontMetrics legendTitleFontMetrics;

    private final Font legendItemFont;
    private int legendItemFontHeight;
    //private final FontMetrics legendItemFontMetrics;

    private final Color backgroundColor = Color.WHITE;

    private static final int marginWidth = 2;  // horizontal margin between edge and contents
    private static final int marginHeight = 2; // vertical margin between edge and contents
    private static final int spaceWidth = 3;   // horizontal space between legend components (e.g. label and icon)
    private static final int spaceHeight = 4;  // vertical space between lines of text

    private static final int legendInsetWidth = 2;   // x coord val of left edge of bounds rectangle
    private static final int legendInsetHeight = 2;  // y coord val of bottom edge of bounds rectangle

    private int newImageWidth;
    private int newImageHeight;

    public int legendWidth;
    public int legendHeight;

    private int legendTitleWidth;
    private int legendTitleHeight;
    private int legendItemLabelMaxWidth;
    private int legendItemLabelMaxHeight;
    private int legendIconWidth;

    private int legendUpperLeftX;
    private int legendUpperLeftY;

    /**
     * @param styleParameters styleParameters
     * @param title Legend title.
     * @param li Legend items.
     * @param mc MapContext
     * @param w Image width.
     * @param h Image height.
     * @param addLegendToTheSide If true then legend added to the side.
     */
    public Geotools_LegendLayer(Geotools_StyleParameters styleParameters,
            String title, List<Geotools_LegendItem> li, MapContent mc, int w,
            int h, boolean addLegendToTheSide) {
        this.styleParameters = styleParameters;
        this.legendTitleFont = new Font("Ariel", Font.BOLD, 12);
        this.legendItemFont = new Font("Ariel", Font.PLAIN, 10);
        this.legendTitle = title;
        this.legendItems = li;
        this.mc = mc;
        this.originalImageWidth = w;
        this.originalImageHeight = h;
        this.addLegendToTheSide = addLegendToTheSide;
        init();
    }

    public final void init() {
        this.frc = new FontRenderContext(at,
                RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
                RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
        buildLegendParams();
        initBounds();
    }

    private void buildLegendParams() {
        int[] legendTitleMaxWidthAndHeight;
        legendTitleMaxWidthAndHeight = getLegendTitleMaxWidthAndHeight();
        legendTitleWidth = legendTitleMaxWidthAndHeight[0];
        legendTitleHeight = legendTitleMaxWidthAndHeight[1];

        int[] legendItemLabelMaxWidthAndHeight;
        legendItemLabelMaxWidthAndHeight = getLegendItemLabelMaxWidthAndHeight();
        legendItemLabelMaxWidth = legendItemLabelMaxWidthAndHeight[0];
        legendItemLabelMaxHeight = legendItemLabelMaxWidthAndHeight[1];
        legendIconWidth = legendItemLabelMaxHeight;

        legendWidth = Math.max(
                legendTitleWidth + (2 * marginWidth),
                legendIconWidth + spaceWidth + legendItemLabelMaxWidth + (2 * marginWidth));

        legendHeight
                = marginHeight
                + legendTitleHeight + spaceHeight
                + (spaceHeight + legendItemLabelMaxHeight) * legendItems.size()
                + marginHeight;

        if (addLegendToTheSide) {
            this.legendUpperLeftX = legendInsetWidth; //imageWidth;
            this.newImageWidth = originalImageWidth + legendWidth + legendInsetWidth;
        } else {
            this.legendUpperLeftX = legendInsetWidth;
        }

        //this.legendUpperLeftY = newImageHeight - legendHeight - legendInsetHeight;
    }

   /**
     * @param r2 The rectangle.
     * @return The maximum height.
     */
     public static int getMaxHeight(Rectangle2D r2) {
        return Math_Double.roundUpToNearestInt(r2.getHeight());
    }

    /**
     * @param r2 The rectangle.
     * @return The maximum width.
     */
    public static int getMaxWidth(Rectangle2D r2) {
        return Math_Double.roundUpToNearestInt(r2.getWidth());
    }

    /**
     * For some reason, this seems to by returning incorrect width and possibly
     * height!
     *
     * @return
     */
    public int[] getLegendTitleMaxWidthAndHeight() {
        int[] r = new int[2];
        int width = 0;
        int height = 0;
        //FontMetrics.stringWidth(String str)
        Rectangle2D b;
        b = legendTitleFont.getStringBounds(legendTitle, frc);
        TextLayout tl = new TextLayout(legendTitle, legendTitleFont, frc);
        b = tl.getBounds();
//        LineMetrics lm = legendTitleFont.getLineMetrics(title, frc);
//        lm.getHeight();
        // width
        width = getMaxWidth(b);
        width += 30; // This is a terrible hack and should be removed. The hack is there because the width is not what is wanted probably as a result of the frc AffineTransform.
        // height
        height = getMaxHeight(b);
        height += 5; // This is a terrible hack and should be removed. The hack is there because the height is not what is wanted probably as a result of the frc AffineTransform.
        r[0] = width;
        r[1] = height;
        return r;
    }

    /**
     *
     * @return
     */
    public int[] getLegendItemLabelMaxWidthAndHeight() {
        int[] r = new int[2];
        int maxWidth = 0;
        int maxHeight = 0;
//        for (int i = 0; i < legendItems.size(); i++) {
//            Geotools_LegendItem li = legendItems.get(i);
        for (Geotools_LegendItem li : legendItems) {
            Rectangle2D b;
            b = legendItemFont.getStringBounds(li.getLabel(), frc);
            // width
            int w;
            w = getMaxWidth(b);
            maxWidth = Math.max(maxWidth, w);
            // height
            int h;
            h = getMaxHeight(b);
            maxHeight = Math.max(maxHeight, h);
        }
        r[0] = maxWidth;
        r[1] = maxHeight;
        return r;
    }

    @Override
    public void draw(Graphics2D graphics, MapContent mapContent,
            MapViewport viewport) {
        try {
//			//Wait for the viewport transform to finish loading
//			while(viewport.getScreenToWorld() == null)
//			{
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//				}
//			}
//		 	
//			//Create the segments of the scale bar
//			Rectangle scrRect = viewport.getScreenArea();	

            at = graphics.getTransform();
            init();

            newImageHeight = originalImageHeight; // This is true until title is added
            this.legendUpperLeftY = legendInsetHeight;
//            this.legendUpperLeftY = newImageHeight - legendHeight - legendInsetHeight;
//            this.legendUpperLeftY -= 10; // Terrible hack

            int x;
            int y;
            // constructor args: x/y of upper left corner
//            // Add a white background and draw a box
//            graphics.setColor(backgroundColor);
//            graphics.fill(bounds);
//            graphics.setFont(titleFont);
//            graphics.setColor(Color.BLACK);
//            graphics.draw(bounds);
            // Draw Legend title and set font for drawing items
            graphics.setFont(legendTitleFont);
            graphics.setColor(Color.BLACK);

            int startLegendX;
            startLegendX = legendUpperLeftX + marginWidth;
            int startLegendTitleY;
            startLegendTitleY = legendUpperLeftY + marginHeight + legendTitleHeight;
            graphics.drawString(legendTitle, startLegendX, startLegendTitleY);
            graphics.setFont(legendItemFont);
            for (int i = 0; i < legendItems.size(); i++) {
                graphics.setColor(Color.BLACK);
                String label = legendItems.get(i).getLabel();
                Rectangle regionIcon;
                regionIcon = new Rectangle(
                        //insetWidth + legendWidth - marginWidth - legendIconWidth + upperLeftX, // For end placement
                        startLegendX, // For start placement
                        startLegendTitleY + spaceHeight + (i * (spaceHeight + legendItemLabelMaxHeight)),
                        legendIconWidth,
                        legendItemLabelMaxHeight);
                graphics.drawString(
                        label,
                        //insetWidth + marginWidth + upperLeftX, // For start placement
                        startLegendX + legendIconWidth + spaceWidth, // For end placement
                        startLegendTitleY + spaceHeight + (i * (spaceHeight + legendItemLabelMaxHeight)) + legendItemLabelMaxHeight);
                if (legendItems.get(i).getColor() != null) {
                    Color c = legendItems.get(i).getColor();
                    graphics.setColor(c);
                    graphics.fill(regionIcon);
                    if (styleParameters.isDrawBoundaries()) {
                        c = Color.BLACK;
                        //DW_Style.getDefaultStroke(Color.BLACK);
//                    } else {
//                        Stroke stroke = DW_Style.styleBuilder.createStroke(
//                                DW_Style.styleBuilder.literalExpression(c),
//                                DW_Style.styleBuilder.literalExpression(0),
//                                DW_Style.styleBuilder.literalExpression(0));
                    }
                    graphics.setColor(c);
                    graphics.draw(regionIcon);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void initBounds() {
        bounds = mc.getMaxBounds();
        double minX;
        double minY;
        double maxX;
        double maxY;
        minX = bounds.getMinX();
        minY = bounds.getMinY();
        maxX = bounds.getMaxX();
        maxY = bounds.getMaxY();
//        System.out.println("minX " + minX);
//        System.out.println("minY " + minY);
//        System.out.println("maxX " + maxX);
//        System.out.println("maxY " + maxY);
        double width = bounds.getWidth();
        //System.out.println("height " + height + ", width " + width);
        double imageWidth_double = originalImageWidth;
        double minXnew = minX - (width / imageWidth_double) * legendWidth;
        //double height = bounds.getHeight();
        //double imageHeight_double = imageHeight;
        //double minYnew = minY - ( height / imageHeight_double ) * legendHeight;
        //bounds.expandToInclude(minXnew, minYnew);
        bounds.expandToInclude(minXnew, minY);
//        minX = bounds.getMinX();
//        minY = bounds.getMinY();
//        maxX = bounds.getMaxX();
//        maxY = bounds.getMaxY();
//        System.out.println("minX " + minX);
//        System.out.println("minY " + minY);
//        System.out.println("maxX " + maxX);
//        System.out.println("maxY " + maxY);
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return bounds;
    }

}
