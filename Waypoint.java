import java.io.Serializable;
import java.sql.Time;

public class Waypoint implements Serializable{
    double lattitude;
    double longitude;
    Time time;
    double ele;

    public Waypoint(double lattitude, double longitude, Time time, double ele ){
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.time = time;
        this.ele = ele;
    }

    //getters
    public double getLattitude() {
        return lattitude;
    } 
    public Time getTime() {
        return time;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getEle() {
        return ele;
    }
     
    //setters
    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setELE(double ele) {
        this.ele = ele;
    }
    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String r = "Waypoint: lattitude: "+getLattitude()+" , longitude: "+getLongitude()+" , elevation: "+getEle()+" , Time: "+getTime();
        return r;
    }
}