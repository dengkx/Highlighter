package com.util;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

import java.sql.Types;

public class FlinkJDBCExample {


    public static void main(String[] args) throws Exception {

        final TypeInformation<?>[] fieldTypes =
                new TypeInformation<?>[]{BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO};

        final RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes);

        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();

        String selectQuery = "select * from user";
        String driverName = "com.mysql.jdbc.Driver";
        String sourceDB = "oee";
        String sinkDB = "oee";
        String dbURL = "jdbc:mysql://114.116.81.193:3306/";
        String dbPassword = "passw0rd";
        String dbUser = "root";

        JDBCInputFormat.JDBCInputFormatBuilder inputBuilder =
                JDBCInputFormat.buildJDBCInputFormat()
                        .setDrivername(driverName)
                        .setDBUrl(dbURL + sourceDB)
                        .setQuery(selectQuery)
                        .setRowTypeInfo(rowTypeInfo)
                        .setUsername(dbUser)
                        .setPassword(dbPassword);
        DataSet<Row> source = environment.createInput(inputBuilder.finish());
        DataSet<Row> transformedSet = source.filter(row -> row.getField(1).toString().length() > 5);
        String insertQuery = "INSERT INTO mydata values (?, ?,?)";
        JDBCOutputFormat.JDBCOutputFormatBuilder outputBuilder =
                JDBCOutputFormat.buildJDBCOutputFormat()
                        .setDrivername(driverName)
                        .setDBUrl(dbURL + sinkDB)
                        .setQuery(insertQuery)
                        .setSqlTypes(new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR})
                        .setUsername(dbUser)
                        .setPassword(dbPassword);

        transformedSet.output(outputBuilder.finish());

        environment.execute();

    }
}
