/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.python.core.PyFunction;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;

public class PythonProcessor {

    private PythonInterpreter interpreter;
    private PyFunction pythonFunction;

    @Autowired
    public PythonProcessor(PythonInterpreter interpreter, String pythonModule, String pythonMethod) {
        this.interpreter = interpreter;

        String importedAs = pythonModule + Character.toUpperCase(pythonMethod.charAt(0)) + pythonMethod.substring(1);
        interpreter.exec("from " + pythonModule + " import " + pythonMethod + " as " + importedAs);
        this.pythonFunction = (PyFunction) this.interpreter.get(importedAs);
    }


    public String processWithPython(String item) {

        return pythonFunction.__call__(new PyString(item)).asString();
    }

}
