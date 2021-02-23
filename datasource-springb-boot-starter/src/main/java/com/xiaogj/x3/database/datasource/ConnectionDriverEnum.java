package com.xiaogj.x3.database.datasource;

/**
 * @ClassName: ConnectionDriverEnum
 * @Description: 连接驱动枚举
 * @author: xiaolinlin
 * @date: 2020/9/9 10:37
 **/
public enum ConnectionDriverEnum {

    /** SQL server */
    SQL_SERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    /** msyql server 8 */
    MYSQL_SERVER("com.mysql.cj.jdbc.Driver"),
    /** oracle server */
    ORACLE_SERVER("oracle.jdbc.driver.OracleDriver");

    private String driver;

    ConnectionDriverEnum(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }
}
