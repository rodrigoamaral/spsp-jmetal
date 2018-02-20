package net.rodrigoamaral.dspsp.project.events;


import java.util.HashMap;
import java.util.Map;

public enum EventType {

    NEW_URGENT_TASK(1),
    EMPLOYEE_LEAVE(2),
    EMPLOYEE_RETURN(3);

    private int value;
    private static Map<Integer, EventType> map = new HashMap<>();

    EventType(int value) {
        this.value = value;
    }

    static {
        for (EventType type : EventType.values()) {
            map.put(type.value, type);
        }
    }

    public static EventType valueOf(int value) {
        return map.get(value);
    }

    @Override
    public String toString() {
        return "EventType{" +
                "value=" + value +
                '}';
    }
}
