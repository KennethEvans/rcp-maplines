package net.kenevans.maplines.lines;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.kenevans.core.utils.SWTUtils;

import org.eclipse.swt.graphics.Point;

/*
 * Created on Aug 20, 2013
 * By Kenneth Evans, Jr.
 */

public class GPXUtils
{
    public static void writeGPXFile(String fileName,
        MapCalibration mapCalibration, Lines lines) {
        File file = new File(fileName);
        boolean doIt = true;
        if(file.exists()) {
            Boolean res = SWTUtils.confirmMsg("File exists: " + file.getPath()
                + "\nOK to overwrite?");
            if(!res) {
                doIt = false;
            }
        }
        if(doIt) {
            PrintWriter out = null;
            try {
                Date date = new Date();
                out = new PrintWriter(new FileWriter(file));
                // Write header
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
                out.println("<gpx");
                out.println(" creator=\"MapLines\"");
                // out.println(" version=\"1.1\"");
                out.println(" xmlns=\"http://www.topografix.com/GPX/1/1\"");
                out.println(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
                out.println(" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 ");
                out.println("   http://www.topografix.com/GPX/1/1/gpx.xsd\">");

                // Write metadata
                out.println("  <metadata>");
                out.println(String.format("    <time>%s</time>", timeString(date)));
                out.println("  </metadata>");

                // Write lines
                if(lines != null) {
                    Line line;
                    Point point;
                    out.println("  <trk>");
                    out.println("    <name>Map Lines</name>");
                    out.println("    <extensions>");
                    out.println("      <gpxx:TrackExtension xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\">");
                    out.println("        <gpxx:DisplayColor>Blue</gpxx:DisplayColor>");
                    out.println("      </gpxx:TrackExtension>");
                    out.println("    </extensions>");
                    for(int i = 0; i < lines.getNLines(); i++) {
                        line = lines.getLines().get(i);
                        out.println("    <trkseg>");
                        for(int j = 0; j < line.getNPoints(); j++) {
                            point = line.getPoints().get(j);
                            double[] vals = mapCalibration.transform(point.x,
                                point.y);
                            out.println(String.format(
                                "      <trkpt lat=\"%f\" lon=\"%f\">", vals[1],
                                vals[0]));
                            out.println("        <ele>0.0</ele>");
                            out.println(String.format("        <time>%s</time>",
                                timeString(date)));
                            out.println("      </trkpt>");
                        }
                        out.println("    </trkseg>");
                    }
                    out.println("  </trk>");
                    out.println("</gpx>");
                }
            } catch(Exception ex) {
                SWTUtils.excMsg("Error writing " + fileName, ex);
            } finally {
                if(out != null) out.close();
                out = null;
            }
        }
    }

    public static String timeString(Date date) {
        if(date == null) {
            return "NA";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

}
