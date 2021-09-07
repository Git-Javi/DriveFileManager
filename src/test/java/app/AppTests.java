package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class AppTests {

	@Test
    public void lessThan2ArgsFunctionManagerNoOkTest() {
        assertFalse(App.functionManager("param1"));
    }
	
	@Test
    public void moreThan3ArgsFunctionManagerNoOkTest() {
        assertFalse(App.functionManager("param1", "param2", "param3","param4"));
    }
	
}
