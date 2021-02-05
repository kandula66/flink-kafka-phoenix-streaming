

package com.cloudera.flink.examples;


import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Properties;
import java.util.UUID;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FlinkKafkaSSL {
    public static void main(String[] args) throws Exception {
        // Read parameters from command line
        final ParameterTool params = ParameterTool.fromArgs(args);

        if(params.getNumberOfParameters() < 4) {
            System.out.println("\nUsage: FlinkKafkaTable --read-topic <topic> --write-topic <topic> --bootstrap.servers <kafka brokers> --group.id <groupid>");
            return;
        }
        String sasl_username = "rkandula";
        String sasl_password = "K@rthi24300k";
       // String jaasTemplate = """org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        	//	String jaasConfig = String.format(jaasTemplate, sasl_username, sasl_password);
            String jaasConfig="com.sun.security.auth.module.Krb5LoginModule required\n" + 
            		"    useKeyTab=true\n" + 
            		"    refreshKrb5Config=true\n" + 
            		"    storeKey=true\n" + 
            		"    serviceName=\"kafka\"\n" + 
            		"    keyTab=\"/home/rkandula/moad-aw-dev0-env-rkandula.keytab\"\n" + 
            		"    principal=\"rkandula@MOAD-AW.YOVJ-8G7D.CLOUDERA.SITE\"; ";
        
            
        
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        
        
        env.enableCheckpointing(5000);
        
        EnvironmentSettings tableSettings = EnvironmentSettings
				.newInstance()
				.useBlinkPlanner()
				.build();
;
		StreamTableEnvironment tableEnvironment = StreamTableEnvironment
				.create(env, tableSettings);

    
		TypeInformation<?>[] fieldTypes = new TypeInformation<?>[] {
		    BasicTypeInfo.STRING_TYPE_INFO,
		    BasicTypeInfo.STRING_TYPE_INFO
		    };
		RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes);
		JDBCInputFormat jdbcInputFormat = JDBCInputFormat.buildJDBCInputFormat()
		        .setDrivername("org.apache.phoenix.queryserver.client.Driver")
		        .setDBUrl("jdbc:phoenix:thin:url=https://cod--or6q9la130qm-gateway0.moad-aw.yovj-8g7d.cloudera.site/cod--or6q9la130qm/cdp-proxy-api/avatica/;serialization=PROTOBUF;authentication=BASIC;avatica_user=rkandula;avatica_password=K@rthi24300k;")
		        .setUsername("rkandula")
		        .setPassword("K@rthi24300k")
		        .setQuery("SELECT country_code, country_name FROM country_lookup")
		        .setRowTypeInfo(rowTypeInfo)
		        .finish();
		DataStream<Row> country_lkp = env.createInput(jdbcInputFormat);
		country_lkp.print();
       env.execute("Flink Kafka testing");
    }

}