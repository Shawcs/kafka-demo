package com.kafka;

/**
 * Created by kafka on 18/04/17.
 */

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serialiser.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * this is the class where we should move our partitioning method
 **/

public class SimplePartitioner {
    static final Logger logger = LoggerFactory.getLogger("SimplePartitioner.class");


    /**
     * This function is used to generate the message Key (it's a part of the meta data of a broker/message) based on the Type field
     **/
    public static String setKeyValue(String recordValue) throws JsonMappingException {
        String key;
        ObjectMapper mapper = new ObjectMapper();
        Ticket currentTicket = null;
        try {
            currentTicket = mapper.readValue(recordValue, Ticket.class);
        } catch (JsonGenerationException e) {
            logger.error("Json exception " + e);
        } catch (JsonMappingException e) {
            logger.error("Json map " + e);
        } catch (IOException e) {
            logger.error("Io exception " + e);
        }

        String type = currentTicket.getType();

        if ("error".equals(type.toLowerCase())) {
            key = "E";
        } else if ("warning".equals(type.toLowerCase())) {
            key = "W";
        } else if ("critical_error".equals(type.toLowerCase())) {
            key = "CE";
        } else {
            key = "ND";
        }
        return key;
    }

    /**
     * This function is used to check the key of the message and set the partition id linked to this key
     **/
    public static int setPartitionID(String keyvalue) {
        int partitionId;

        if ("E".equals(keyvalue)) {
            partitionId = 2;
        } else if ("W".equals(keyvalue)) {
            partitionId = 1;
        } else if ("CE".equals(keyvalue)) {
            partitionId = 0;
        } else {
            partitionId = 3;
        }
        return partitionId;
    }


}