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


}
