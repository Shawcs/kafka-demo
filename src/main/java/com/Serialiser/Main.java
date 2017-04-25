package com.Serialiser;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by aturbillon on 19/04/2017.
 */
public class Main {
    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();
        Ticket t = null;

        try {

            t = mapper.readValue(new File("/home/kafka/Téléchargements/KAFKA_demo/src/main/resources/User_data"), Ticket.class);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(t.toString());

    }

}
