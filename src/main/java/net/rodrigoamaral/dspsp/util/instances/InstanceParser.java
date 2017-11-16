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
            System.out.print("Parsing instance file: " + filename);
            di = mapper.readValue(new File(filename), DynamicInstance.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(" ...Done!\n");
//        return adjustIDs(di);
        return di;
    }

    private DynamicInstance adjustIDs(DynamicInstance di) {
        List<Integer> adjustedEmployeeIDs = new ArrayList<>();
        List<Integer> adjustedTaskIDs = new ArrayList<>();

        for (Integer e: di.getAvailable_employee()) {
            adjustedEmployeeIDs.add(e - 1);
        }

        for (Integer t: di.getAvailable_task()) {
            adjustedTaskIDs.add(t - 1);
        }

        di.setAvailable_employee(adjustedEmployeeIDs);
        di.setAvailable_task(adjustedTaskIDs);
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
