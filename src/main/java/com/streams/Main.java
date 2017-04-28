package com.streams;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aturbillon on 27/04/2017.
 *
 * https://github.com/DataReply/kafka-connect-directory-source/tree/master/src/main/java/org/apache/kafka/connect/utils
 */
public class Main {

    public void main() {
        DirectorySourceTask dc = new DirectorySourceTask() {

            @Override
            public String version() {
                return null;
            }
        };

Map<String, String> props=new HashMap<>();
        props.put("SCHEMA_NAME", "");
        props.put("TOPIC","" );
        props.put("DIR_PATH", "");
        props.put("CHCK_DIR_MS","100");


      dc.start(props);
      dc.poll();

    }
}
