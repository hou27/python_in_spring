package com.example.python.test;

import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    public String runPython() {
        PythonInterpreter pyInterpreter = new PythonInterpreter();
        pyInterpreter.execfile("src/main/java/com/example/python/python/run.py");

        PyFunction pyFunction = pyInterpreter.get("add", PyFunction.class);
        int a = 10, b = 20;
        PyObject obj = pyFunction.__call__(new PyInteger(a), new PyInteger(b));
        System.out.println(obj.toString());

        return obj.toString();
    }
}