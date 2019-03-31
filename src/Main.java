import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    public static void main(String[] args) {
        //get db connection
        DB db = new DB();
        Helpers hp = new Helpers();

        // Parse JSON and put data into db
        JSONObject jo = null;
        try {

            jo = (JSONObject) new JSONParser().parse(new FileReader("positions.json"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray teams = (JSONArray) jo.get("teams");
        int count = 0;
        for(Object x : teams){
            JSONObject team = (JSONObject) x;
            Long serial = (Long) team.get("serial");
            Long marker = (Long) team.get("marker");
            String name = (String) team.get("name");

            //Enter into db
            db.insertTeam(serial, marker, name);
            //System.out.println("***********"  +serial+ "************");

            JSONArray positionList = (JSONArray) team.get("positions");
            for(Object y : positionList){
                JSONObject position = (JSONObject) y;

                Boolean alert = (Boolean) position.get("alert");
                Long altitude  = (Long) position.get("altitude");
                String type = (String) position.get("type");
                Double dtfKm = (Double) position.get("dtfKm");
                Long pos_id = (Long) position.get("pos_id");
                Long gpsAt = hp.convertToTimestamp((String) position.get("gpsAt"));
                Double sogKnots = (Double) position.get("sogKnots");
                Long battery = (Long) position.get("battery");
                Long cog = (Long) position.get("cog");
                Double dtfNm = (Double) position.get("dtfNm");
                Long txAt = hp.convertToTimestamp((String) position.get("txAt"));
                Double longitude = (Double) position.get("longitude");
                Double latitude = (Double) position.get("latitude");
                Long gpsAtMillis = (Long) position.get("gpsAtMillis");
                Double sogKmph = (Double) position.get("sogKmph");

                //insert into db
                db.insertPosition(name, alert, altitude, type, dtfKm, pos_id, gpsAt,sogKnots, battery, cog, dtfNm, txAt, longitude, latitude, gpsAtMillis, sogKmph);

                if(count % 100 == 0){
                    System.out.println("Inserted item: "+count);
                }
                count++;
            }

            //Iterate through positionList
            //for each iteration, add entry into db

        }

        // Decide on method to determine number of other vessels visible
        // -> boats will be visible if they are less than 10km away (roughly)

        //HashMap<String, Integer> results = new HashMap<>();
        //for each boat
        ResultSet boats = db.getBoats();
        try {
            while(boats.next()){
                String name = boats.getString("name");

                ResultSet positions = db.getPositions(name);
                Integer dayBoatCount = 0;
                Integer count1 = 0;
                while(positions.next()){
                    Long gpsAtMillis = positions.getLong("gpsAtMillis");
                    Double longitude = positions.getDouble("longitude");
                    Double latitude = positions.getDouble("latitude");
                    Double elevation = positions.getDouble("altitude");

                    int r_earth = 6378; //in km

                    Double max_lat  = latitude  + (10 / r_earth) * (180 / Math.PI);
                    Double max_long = longitude + (10 / r_earth) * (180 / Math.PI) / Math.cos(latitude * Math.PI/180);
                    Double min_lat  = latitude  - (10 / r_earth) * (180 / Math.PI);
                    Double min_long = longitude - (10 / r_earth) * (180 / Math.PI) / Math.cos(latitude * Math.PI/180);

                    ResultSet close = db.getNearBy(gpsAtMillis, max_long, min_long, max_lat, min_lat);

                    HashSet<String> nearbyBoats = new HashSet<>();

                    while(close.next()){
                        String team_name = positions.getString("team_name");
                        Double long2 = positions.getDouble("longitude");
                        Double lat2 = positions.getDouble("latitude");
                        Double elevation2 = positions.getDouble("altitude");



                        Double dist = hp.distance(latitude, lat2, longitude, long2, elevation, elevation2);

                        //System.out.println("");
                        //if the distance is less than roughly 6km, which if you are looking at the horizon from 6ft above sea level, is where the horizon appears
                        //if(dist < 10) {
                            nearbyBoats.add(team_name);
                        System.out.println("added a boat");
                        //}
                    }
                    //get final count of number of boats at that position
                    dayBoatCount += nearbyBoats.size();
                    count1 ++;
                }
                //get final count of number of boats in the day
                System.out.println("Boat count for boat: "+ name + " = " + dayBoatCount);
                //results.put(name, dayBoatCount/count1);
                hp.writeToFile(name, dayBoatCount/count1);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //get all positions it's at
            //for each position
                //get any boats that are less than 10km away in any direction
                //get list of boats visible -> set of boats so each is only in the list once


        // Output summary table showing average number of sightings per vessel per day


    }
}
