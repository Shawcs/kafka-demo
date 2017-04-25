package com.streams;

import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aturbillon on 24/04/2017.
 */
public abstract class Main {


    public static void main(String[] args) throws IOException {
          final String TOPIC = "test";

         File tempFile;
         Map<String, String> config;
         OffsetStorageReader offsetStorageReader;
         SourceTaskContext context;
         FileStreamSourceTask task;

        //TODO look how to launch it and see if world count stream app work

        //tempFile = File.createTempFile("file-stream-source-task-test", null);
        config = new HashMap<>();
        config.put(FileStreamSourceConnector.FILE_CONFIG,"C:\\projects\\kafka\\KAFKA_demo\\src\\main\\resources\\User_data");
        config.put(FileStreamSourceConnector.TOPIC_CONFIG, TOPIC);
        task = new FileStreamSourceTask();

        task.start(config);
        try {
            task.poll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
