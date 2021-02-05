package com.cloudera.flink.examples;


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


import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;


import com.google.gson.Gson;

/*
 * ./bin/flink run <pathtojar> --read-topic <readtopiname> --write-topic <writetopicname> --bootstrap.servers '<bootstrapserviersinfoforkafka>' --jdbcThinUrl '<jdbcurlthinurlwithavaticauserandpassword' --saslusername <workloaduser> --saslpassword <workloadpassword>
 
 *
 *
 *./bin/flink-yarn-session -jm 2048mb  -tm 3048 -s 2 -d -D security.kerberos.login.keytab=<pathtoworkloaduserkeytab> -D security.kerberos.login.principal=<workloadprincipal@REALM>
 * /bin/flink run <pathtojar> --read-topic <readtopiname> --write-topic <writetopicname> --bootstrap.servers '<bootstrapserviersinfoforkafka>' --jdbcThinUrl '<jdbcurlthinurlwithavaticauserandpassword' --saslusername <workloaduser> --saslpassword <workloadpassword>

 * Make sure to include hbase-ste.xml in src/main/resources,otherwise it wont be able to connect to phoenix
 */
public class FlinkKafkaTable {
    public static void main(String[] args) throws Exception {
        // Read parameters from command line
        final ParameterTool params = ParameterTool.fromArgs(args);
        System.out.println(params.getNumberOfParameters());
        if(params.getNumberOfParameters() < 6) {
            return;
        }
        String sasl_username = params.getRequired("saslusername");
        String sasl_password = params.getRequired("saslpassword");
        String jaasTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
        		String jaasConfig = String.format(jaasTemplate, sasl_username, sasl_password);
        Properties kparams = params.getProperties();
        kparams.setProperty("auto.offset.reset", "earliest");
        kparams.setProperty("flink.starting-position", "earliest");
        kparams.setProperty("group.id", UUID.randomUUID().toString());

        
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        
        
        env.enableCheckpointing(5000);
        
        EnvironmentSettings tableSettings = EnvironmentSettings
				.newInstance()
				.useBlinkPlanner()
				.build();
        //EnvironmentSettings tableSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment tableEnvironment = StreamTableEnvironment
				.create(env, tableSettings);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", params.getRequired("bootstrap.servers"));
        properties.setProperty("sasl.kerberos.service.name", "kafka");
        //properties.setProperty("ssl.truststore.location", "/usr/lib/jvm/java-1.8.0/jre/lib/security/cacerts");
        properties.setProperty("ssl.truststore.location", "./src/main/resources/truststore.jks");
        properties.setProperty("security.protocol", "PLAIN");
        properties.setProperty("group.id", UUID.randomUUID().toString());
        
        Properties properties_producer = new Properties();
        properties_producer.setProperty("bootstrap.servers", params.getRequired("bootstrap.servers"));
        properties_producer.setProperty("sasl.kerberos.service.name", "kafka");
       // properties_producer.setProperty("ssl.truststore.location", "/usr/lib/jvm/java-1.8.0/jre/lib/security/cacerts");
        properties.setProperty("ssl.truststore.location", "./src/main/resources/truststore.jks");
        properties_producer.setProperty("security.protocol", "PLAIN");

        //properties.setProperty("sasl.mechanism", "PLAIN");
        properties.setProperty("sasl.jaas.config", jaasConfig);
        

        FlinkKafkaConsumer<String> myConsumer = new FlinkKafkaConsumer<>(
        		params.getRequired("read-topic"),							// input topic
        		new SimpleStringSchema(),			// serialization schema
        		properties);						// properties
        

        myConsumer.setStartFromEarliest();
        
		TypeInformation<?>[] fieldTypes = new TypeInformation<?>[] {
		    BasicTypeInfo.STRING_TYPE_INFO,
		    BasicTypeInfo.STRING_TYPE_INFO
		    };
		RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes);
		JDBCInputFormat jdbcInputFormat = JDBCInputFormat.buildJDBCInputFormat()
		        .setDrivername("org.apache.phoenix.queryserver.client.Driver")
		        .setDBUrl(params.getRequired("jdbcThinUrl"))
		        //.setUsername("<workloaduser>")
		        //.setPassword("<worloadpassord>")
		        .setQuery("SELECT country_code, country_name FROM country_lookup")
		        .setRowTypeInfo(rowTypeInfo)
		        .finish();
		DataStream<Row> country_lkp = env.createInput(jdbcInputFormat);
	       tableEnvironment.createTemporaryView("countryLkpTable", country_lkp);
        
    	DataStream<String> inputStream = env.addSource(myConsumer);
    DataStream<CovidData> messageStream = inputStream
        	    .map(json -> new Gson().fromJson(json, CovidData.class));
       

        
       DataStream<CovidData> stream = messageStream.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<CovidData>(){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final long maxOutOfOrderness = 3500; // 3.5 seconds

           private long currentMaxTimestamp;

           @Override
           public long extractTimestamp(CovidData element, long previousElementTimestamp) {
               long timestamp = element.getEventTimeLong();
               currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
               return timestamp;
           }

           @Override
           public Watermark getCurrentWatermark() {
               // return the watermark as current highest timestamp minus the out-of-orderness bound
               return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
           }
       });

       
       tableEnvironment.createTemporaryView("covidTable", stream);
       Table result = tableEnvironment.sqlQuery("SELECT ct.country_code, ct.latitude,ct.longitude,ct.location,ct.dead,ct.recovered,f1 as country_name  FROM covidTable ct JOIN countryLkpTable clt on ct.country_code=clt.f0");
// ,clt.country_name INNER JOIN countryLkpTable clt on ct.country_code=clt.country_code
      DataStream<CovidFilteredData> appendStream = tableEnvironment.toAppendStream(result, CovidFilteredData.class);
       appendStream.print();
       

       

       appendStream.addSink(new FlinkKafkaProducer<>(params.getRequired("write-topic"), new ObjSerializationSchema(params.getRequired("write-topic")), 
    		   properties_producer, FlinkKafkaProducer.Semantic.AT_LEAST_ONCE));

       env.execute("Flink Kafka testing");
    }

}