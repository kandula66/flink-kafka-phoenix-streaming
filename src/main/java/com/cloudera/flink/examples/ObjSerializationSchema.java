package com.cloudera.flink.examples;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;


    public class ObjSerializationSchema implements KafkaSerializationSchema<CovidFilteredData>{

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String topic;   
        private ObjectMapper mapper;

        public ObjSerializationSchema(String topic) {
            super();
            this.topic = topic;
        }

        @Override
        public ProducerRecord<byte[], byte[]> serialize(CovidFilteredData obj, Long timestamp) {
            byte[] b = null;
            if (mapper == null) {
                mapper = new ObjectMapper();
            }
             try {
                b= mapper.writeValueAsBytes(obj);
            } catch (JsonProcessingException e) {
                // TODO 
            }
            return new ProducerRecord<byte[], byte[]>(topic, b);
        }

    }