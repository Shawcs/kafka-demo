package com.kafka;

/**
 * Created by kafka on 18/04/17.
 */

import com.Serialiser.Ticket;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SimplePartitioner {


    public static int setKeyValue(String RecordValue) {
        int key = 0;
        ObjectMapper mapper = new ObjectMapper();
        Ticket t = null;

        try {

            t = mapper.readValue(RecordValue, Ticket.class);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }


    public static int SetPartitionID(String KeyValue) {
        int partitionId = 0;

        if (KeyValue == "erreur") {

            return partitionId = 0;
        }
        //...


        return partitionId;
    }

}