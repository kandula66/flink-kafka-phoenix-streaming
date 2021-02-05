# flink-kafka-phoenix-streaming
Flink Kafka Streaming Examples
to run stand alone mode use following cmd

./bin/flink run <pathtojar> --read-topic readtopiname --write-topic writetopicname --bootstrap.servers bootstrapserviersinfoforkafka --jdbcThinUrl jdbcurlthinurlwithavaticauserandpassword --saslusername workloaduser --saslpassword workloadpassword
 
to run in yarn mode

./bin/flink-yarn-session -jm 2048mb  -tm 3048 -s 2 -d -D security.kerberos.login.keytab=<pathtoworkloaduserkeytab> -D security.kerberos.login.principal=<workloadprincipal@REALM>
 /bin/flink run pathtojar --read-topic readtopiname --write-topic writetopicname --bootstrap.servers bootstrapserviersinfoforkafka --jdbcThinUrl jdbcurlthinurlwithavaticauserandpassword --saslusername workloaduser --saslpassword workloadpassword

 Make sure to include hbase-ste.xml in src/main/resources,otherwise it wont be able to connect to phoenix

