package net.rodrigoamaral.dspsp.util.instances;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstanceParser {


    public DynamicInstance parse(String filename) {
        ObjectMapper mapper = new ObjectMapper();

        DynamicInstance di = null;

        try {
            di = mapper.readValue(new File(filename), DynamicInstance.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return di;
    }

    public static void main(String[] args) {
        InstanceParser parser = new InstanceParser();
        List<DynamicInstance> instances = new ArrayList<>();
        for (int i = 1; i <= 18; i++) {
            instances.add(parser.parse("project-conf/dynamic/dynamic_example_new" + i + ".json"));
        }
    }

}
