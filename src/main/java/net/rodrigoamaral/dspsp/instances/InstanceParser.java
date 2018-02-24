package net.rodrigoamaral.dspsp.instances;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

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


}
