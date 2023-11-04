package com.example.python.test;

import org.json.simple.JSONObject;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    public String runPython(JSONObject data) {
        PythonInterpreter pyInterpreter = new PythonInterpreter();
        pyInterpreter.execfile("src/main/java/com/example/python/python/run.py");

        PyFunction pyFunction = pyInterpreter.get("execute", PyFunction.class);

        System.out.println(data);
        PyObject obj = pyFunction.__call__(new PyString(String.valueOf(data.get("data"))));
        System.out.println(obj.toString());

        return obj.toString();
    }
}