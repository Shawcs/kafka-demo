package com.recordGenerator.javafaker;

/**
 * Created by aturbillon on 28/04/2017.
 */
public class Main {


    public static void main(String[] args) {


        Faker faker = new Faker();

        for(int i=100;i<100;i++) {
            String name = faker.name().fullName();
            String streetAddress = faker.address().streetAddress();
            String emailAddress = faker.internet().emailAddress();

            System.out.println(name+"; "+streetAddress+"; "+emailAddress);
        }


    }
}
