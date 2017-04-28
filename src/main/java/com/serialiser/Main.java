package com.serialiser;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by aturbillon on 19/04/2017.
 */
//just to test the serialization process

public class Main {
    public static void main(String[] args) {


        final Logger logger = LoggerFactory.getLogger(Main.class);


        Faker faker = new Faker();
        for(int i=0;i<100;i++) {
            String name = faker.name().fullName();
            String streetAddress = faker.address().streetAddress();
            String emailAddress = faker.internet().emailAddress();

            System.out.println(name + "; " + streetAddress + "; " + emailAddress+"; ");
        }


        ObjectMapper mapper = new ObjectMapper();
        Ticket t = null;

        try {
            t = mapper.readValue(new File("/home/kafka/Téléchargements/KAFKA_demo/src/main/resources/User_data"), Ticket.class);
        } catch (JsonGenerationException|JsonMappingException e) {
    logger.debug("Json error "+e);
        }
        catch (IOException e){
        logger.debug("io beug "+e);
        }
        System.out.println(t.toString());
    }
}
