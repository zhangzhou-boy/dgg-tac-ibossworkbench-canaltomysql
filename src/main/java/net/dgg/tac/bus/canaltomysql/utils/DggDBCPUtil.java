package net.dgg.tac.bus.canaltomysql.utils;

import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @Classname DggDBCPUtil
 * @Description dbcp数据库连接池，使用druid连接池会报错，暂时没找到原因
 * @Date 2019/4/22 13:28
 * @Created by dgg-yanshun
 */
public class DggDBCPUtil {
    private static Properties properties = new Properties();
    private static DataSource dataSource;
    static{
        try {
            properties.load(DggDBCPUtil.class.getClassLoader().getResourceAsStream("dbcp.properties"));
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs){
        //关闭连接和释放资源
        try {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setParams(PreparedStatement pstm, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof Integer) {
                Integer data = (Integer) param;
                pstm.setInt(i + 1, data);
            } else if (param instanceof Long) {
                Long data = (Long) param;
                pstm.setLong(i + 1, data);
            } else if (param instanceof Float) {
                Float data = (Float) param;
                pstm.setFloat(i + 1, data);
            } else if (param instanceof Double) {
                Double data = (Double) param;
                pstm.setDouble(i + 1, data);
            } else if (param instanceof String) {
                String data = (String) param;
                pstm.setString(i + 1, data);
            } else if (param instanceof Date) {
                Date data = (Date) param;
                pstm.setTimestamp(i + 1, new Timestamp(data.getTime()));
            } else if (param instanceof Timestamp) {
                Timestamp data = (Timestamp) param;
                pstm.setTimestamp(i + 1, data);
            } else if (param instanceof java.sql.Date) {
                java.sql.Date data = (java.sql.Date) param;
                pstm.setDate(i + 1, data);
            } else {
                pstm.setObject(i + 1, param);
            }
        }
    }
}
