import java.util.ArrayList;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GPXparser {

    // defaut constuctor
    GPXparser() {
    }

    // function patseGpx which analyzes the gpx file
    public ArrayList<Waypoint> parseGpx(File file) {

        ArrayList<Waypoint> waypoints = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList wptList = doc.getElementsByTagName("wpt");
            for (int i = 0; i < wptList.getLength(); i++) {
                Element wptElement = (Element) wptList.item(i);
                double lat = Double.parseDouble(wptElement.getAttribute("lat"));
                double lon = Double.parseDouble(wptElement.getAttribute("lon"));
                String eleString = wptElement.getElementsByTagName("ele").item(0).getTextContent();
                double ele = Double.parseDouble(eleString);
                // System.out.println("lattitude is: "+ lat);
                // System.out.println("longitude is: "+ lon);
                // System.out.println("ELE is: "+ele);
                String timeString = wptElement.getElementsByTagName("time").item(0).getTextContent();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"); // mia diorthosi
                                                                                                       // edw
                LocalDateTime dateTime = LocalDateTime.parse(timeString, formatter); // dateTime, Time?
                // System.out.println("time is: "+ dateTime);
                waypoints.add(new Waypoint(lat, lon, null, ele));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return waypoints;
    }

    // ---------------------TESTING-----------------//
    public static void main(String[] args) {
        GPXparser g1 = new GPXparser();
        File file1 = new File("route1.gpx");
        g1.parseGpx(file1);
    }
}
