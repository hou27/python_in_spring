package com.example.python.test;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/test")
public class TestController {
    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

//    @PostMapping("/run")
//    public String runPython(@RequestBody JSONObject data) {
//        return this.testService.runPython(data);
//    }

}