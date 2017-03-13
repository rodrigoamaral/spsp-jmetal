package net.rodrigoamaral.spsp.solution;

import net.rodrigoamaral.spsp.adapters.SolutionConverter;
import net.rodrigoamaral.spsp.project.Project;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.solution.DoubleSolution;

import static org.junit.Assert.*;

/**
 * Created by rodrigo on 08/10/16.
 */
public class SolutionConverterTest {

    private SolutionConverter s;

    @Before
    public void setUp() {
        s = new SolutionConverter(15, 20);
    }

    @Test
    public void decode_0() {
        int index = 0;
        assertEquals(0, s.decodeEmployee(index));
        assertEquals(0, s.decodeTask(index));
    }

    @Test
    public void decode_14() {
        int index = 14;
        assertEquals(0, s.decodeEmployee(index));
        assertEquals(14, s.decodeTask(index));
    }

    @Test
    public void decode_15() {
        int index = 15;
        assertEquals(0, s.decodeEmployee(index));
        assertEquals(15, s.decodeTask(index));
    }

    @Test
    public void decode_19() {
        int index = 19;
        assertEquals(0, s.decodeEmployee(index));
        assertEquals(19, s.decodeTask(index));
    }

    @Test
    public void decode_20() {
        int index = 20;
        assertEquals(1, s.decodeEmployee(index));
        assertEquals(0, s.decodeTask(index));
    }

    @Test
    public void decode_299() {
        int index = 299;
        assertEquals(14, s.decodeEmployee(index));
        assertEquals(19, s.decodeTask(index));
    }

    @Test
    public void encode_0_0() {
        assertEquals(0, s.encode(0, 0));
    }

    @Test
    public void encode_0_14() {
        assertEquals(14, s.encode(0, 14));
    }

    @Test
    public void encode_1_0() {
        assertEquals(20, s.encode(1, 0));
    }

    @Test
    public void encode_1_1() {
        assertEquals(21, s.encode(1, 1));
    }

    @Test
    public void encode_13_0() {
        assertEquals(260, s.encode(13, 0));
    }


    @Test
    public void encode_14_0() {
        assertEquals(280, s.encode(14, 0));
    }

    @Test
    public void encode_14_19() {
        assertEquals(299, s.encode(14, 19));
    }


}