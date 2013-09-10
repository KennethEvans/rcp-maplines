package net.kenevans.maplines.lines;

import java.util.ArrayList;
import java.util.List;

import net.kenevans.core.utils.SWTUtils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/*
 * Created on Aug 17, 2013
 * By Kenneth Evans, Jr.
 */

public class Line
{
    private Color color = new Color(Display.getCurrent(), 255, 0, 0);
    private List<Point> points = new ArrayList<Point>();
    private String desc = "";

    public Line(int[][] data) {
        if(data == null || data.length == 0) {
            return;
        }
        if(data[0].length != 2) {
            SWTUtils.errMsgAsync(null,
                "Data array must be an array of sets of two integers");
            return;
        }
        for(int[] point : data) {
            points.add(new Point(point[0], point[1]));
        }
    }

    public Line() {
        // Do nothing
    }

    public Line(Color color) {
        this.color = color;
    }

    public void addPoint(Point point) {
        if(points != null) {
            points.add(point);
        }
    }

    public void removePoint(Point point) {
        if(points != null) {
            points.remove(point);
        }
    }

    public void removePoint(int index) {
        if(points != null) {
            points.remove(index);
        }
    }

    public int getNPoints() {
        return points.size();
    }

    /**
     * @return The value of color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color The new value for color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return The value of points.
     */
    public List<Point> getPoints() {
        return points;
    }

    /**
     * @param points The new value for points.
     */
    public void setPoints(List<Point> points) {
        this.points = points;
    }

    /**
     * @return The value of desc.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc The new value for desc.
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

}
