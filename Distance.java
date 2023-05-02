import java.util.ArrayList;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Distance {

    Distance() {
    }

    public static void main(String[] args) {

        try {
            // Parse the GPX file
            File inputFile = new File("route3.gpx");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Extract the locations from the GPX file
            NodeList wptList = doc.getElementsByTagName("wpt");
            ArrayList<double[]> locations = new ArrayList<double[]>();
            for (int i = 0; i < wptList.getLength(); i++) {
                Node wptNode = wptList.item(i);
                if (wptNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element wptElement = (Element) wptNode;
                    double lat = Double.parseDouble(wptElement.getAttribute("lat"));
                    double lon = Double.parseDouble(wptElement.getAttribute("lon"));
                    double[] location = { lat, lon };
                    locations.add(location);
                }
            }

            // Calculate the total distance traveled
            double distance = calculateDistance(locations);
            System.out.printf("Total distance traveled: %.2f km", distance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double calculateDistance(ArrayList<double[]> locations) {
        double distance = 0.0;
        double prevLat = 0.0, prevLon = 0.0;
        for (double[] location : locations) {
            double lat = location[0];
            double lon = location[1];
            if (prevLat != 0.0 && prevLon != 0.0) {
                distance += distance(prevLat, prevLon, lat, lon);
            }
            prevLat = lat;
            prevLon = lon;
        }
        return distance;
    }

    public static ArrayList<double[]> parseGpx(File file) {
        ArrayList<double[]> locations = new ArrayList<double[]>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList wptList = doc.getElementsByTagName("wpt");
            for (int i = 0; i < wptList.getLength(); i++) {
                Element wptElement = (Element) wptList.item(i);
                double lat = Double.parseDouble(wptElement.getAttribute("lat"));
                double lon = Double.parseDouble(wptElement.getAttribute("lon"));
                double[] location = { lat, lon };
                locations.add(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        // The Haversine formula calculates the great-circle distance between two points
        // on a sphere given their longitudes and latitudes.
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance;
    }
}
