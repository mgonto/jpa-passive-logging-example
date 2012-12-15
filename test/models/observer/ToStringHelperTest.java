package models.observer;

import static org.junit.Assert.*;

import org.junit.Test;

import play.test.UnitTest;

public class ToStringHelperTest extends UnitTest {

    public static final String TO_STRING_TEST = "TestMeBaby";

    @Test
    public void toStringOfNull() {
        assertEquals("null", ToStringHelper.toString(null));
    }

    @Test
    public void toStringOfTestObject() {
        assertEquals(TO_STRING_TEST, ToStringHelper.toString(new TestObject()));
    }

    private class TestObject {
        @Override
        public String toString() {
            return TO_STRING_TEST;
        }
    }

}
