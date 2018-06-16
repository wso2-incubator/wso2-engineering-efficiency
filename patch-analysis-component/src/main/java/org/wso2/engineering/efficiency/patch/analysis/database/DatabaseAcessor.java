package org.wso2.engineering.efficiency.patch.analysis.database;

public class DatabaseAcessor {
    private static DatabaseAcessor databaseAcessor = new DatabaseAcessor();

    public static DatabaseAcessor getInstance(){
        return databaseAcessor;
    }

}
