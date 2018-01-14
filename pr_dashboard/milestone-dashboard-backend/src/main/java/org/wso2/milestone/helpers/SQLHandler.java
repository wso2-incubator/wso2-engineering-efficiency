package org.wso2.milestone.helpers;

import java.sql.*;
import org.apache.log4j.Logger;

public class SQLHandler {
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;
    private static Connection con ;
    final static Logger logger = Logger.getLogger(SQLHandler.class);


    public SQLHandler(String databaseUrl,String databaseUser,String databasePassword) {
        this.databaseUrl = databaseUrl;
        this.databasePassword = databasePassword;
        this.databaseUser = databaseUser;
        try {
            if(con==null) {
                con = DriverManager.getConnection(this.databaseUrl, this.databaseUser, this.databasePassword);
                logger.info("Connected to the MySQL database");
            }
        } catch (SQLException e) {
            logger.error("SQL Exception while connecting to the MySQL database");
        }

    }

    /**
     * Execute sql query and get result set
     * @param query - sql query
     * @return
     */
    public ResultSet executeQuery(String query ){
        ResultSet resultSet = null;
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(query);
        }
        catch (SQLException e){
            logger.error("SQL Exception while executing the query");
        }
        return resultSet;
    }

}
