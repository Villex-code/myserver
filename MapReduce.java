import java.util.*;

public class MapReduce {

    public static List<List<Waypoint>> Mapping(String pathname, List<Waypoint> waypoints) {

        final int Workers_size = 3;

        int partitionSize = (int) Math.ceil((double) waypoints.size() / Workers_size);

        if (partitionSize == 1) {
            System.out.println("Too many workers for too little waypoints ");
            partitionSize += 1;
        }

        System.out.println("Partition size : " + partitionSize + " with a total points of " + waypoints.size());

        // Create a list to hold the partitions
        List<List<Waypoint>> partitions = new ArrayList<>();

        // Divide the waypoints list into partitions of size partitionSize
        for (int i = 0; i < waypoints.size(); i += partitionSize) {

            int endIndex = Math.min(i + partitionSize, waypoints.size());

            if (i + partitionSize < waypoints.size() - 1) {
                endIndex += 1;
            }

            System.out.println(endIndex + " : " + waypoints.size());
            List<Waypoint> partition = waypoints.subList(i, endIndex);
            System.out.println(i + " " + endIndex);
            partitions.add(partition);
        }

        return partitions;
    }

}
