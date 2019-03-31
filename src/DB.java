import java.sql.*;
import java.util.ArrayList;

public class DB {

    public Connection c;
    public DB(){

        String user = "username here";
        String pass = "password here";
        try{
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/rock7-2",user,pass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertTeam(Long serial, Long marker, String name){
        //System.out.println("***********************************************************************************************************************");
        String sql = "INSERT INTO `teams` (`serial`, `marker`, `name`) VALUES ("+serial+", "+marker+", \""+name+"\");";
        //System.out.println(sql);
        try {
            Statement stmt = c.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPosition(String name, Boolean alert, Long altitude, String type, Double dtfKm, Long pos_id, Long gpsAt, Double sogKnots, Long battery, Long cog, Double dtfNm, Long txAt, Double longitude, Double latitude, Long gpsAtMillis, Double sogKmph) {
        //System.out.println(serial);
        String sql = "INSERT INTO `positions` (`team_name`, `alert`, `altitude`, `type`, `dtfKm`, `pos_id`, `gpsAt`, `sogKnots`, `battery`, `cog`, `dtfNm`, `txAt`, `longitude`, `latitude`, `gpsAtMillis`, `sogKmph`) VALUES (\""+name+"\", "+alert+", "+altitude+", \""+type+"\", "+dtfKm+", "+pos_id+", "+gpsAt+", "+sogKnots+", "+battery+", "+cog+", "+dtfNm+", "+txAt+", "+longitude+", "+latitude+", "+gpsAtMillis+", "+sogKmph+");";
        //System.out.println(sql);
        try {
            Statement stmt = c.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Could also use prepared statements and bind values to it to make it more efficient
    }

    public ResultSet getBoats(){
        String sql = "SELECT name FROM teams;";
        ResultSet rs = null;
        try {
            Statement stmt = c.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet getPositions(String name) {
        String sql = "SELECT gpsAtMillis, longitude, latitude, altitude FROM positions WHERE team_name = \""+name+"\";";
        ResultSet rs = null;
        try {
            Statement stmt = c.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet getNearBy(Long gpsAtMillis, Double max_long, Double min_long, Double max_lat, Double min_lat) {
        String sql = "SELECT team_name, longitude, latitude, altitude FROM positions WHERE gpsAtMillis ="+gpsAtMillis+" AND longitude > "+min_long+" AND longitude < "+max_long+" AND latitude > "+min_lat+" AND latitude < "+max_lat+";";
        ResultSet rs = null;
        try {
            Statement stmt = c.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
}
