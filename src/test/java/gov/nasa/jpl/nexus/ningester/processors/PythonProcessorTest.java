/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.junit.Test;
import org.python.core.PyFunction;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class PythonProcessorTest {

    @Test
    public void testHello(){
        PythonInterpreter interpreter = new PythonInterpreter();
        PythonProcessor processor = new PythonProcessor(interpreter, "pymodule", "capitalize");

        String expected = "HELLO JYTHON";
        String result = processor.processWithPython("hello jython");

        assertEquals(expected, result);
    }

    @Test
    public void testNumpySquare(){
        PythonInterpreter interpreter = new PythonInterpreter();
        PythonProcessor processor = new PythonProcessor(interpreter, "pymodule", "square");

        String expected = "4";
        String result = processor.processWithPython("2");

        assertEquals(expected, result);
    }

}
