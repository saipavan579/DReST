package com.Daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnMgr {
    static Connection conn;
    static String url;
    
    public static Connection getConnection() throws SQLException{
        try{
            String url = "jdbc:mysql://localhost:3306/DReST?zeroDateTimeBehavior=convertToNull";
            Class.forName("com.mysql.jdbc.Driver");
            try{
                conn = DriverManager.getConnection( url, "root", "sairam");
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        catch(ClassNotFoundException e){
        	e.printStackTrace();
        }
        return(conn);
    }
}

