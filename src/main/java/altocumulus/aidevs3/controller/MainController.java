package altocumulus.aidevs3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import altocumulus.aidevs3.service.S01e02Service;
import altocumulus.aidevs3.service.S01e03Service;
import altocumulus.aidevs3.service.S01e05Service;
import altocumulus.aidevs3.service.S02e01Service;
import altocumulus.aidevs3.service.S02e02Service;
import altocumulus.aidevs3.service.S02e03Service;
import altocumulus.aidevs3.service.S02e04Service;
import altocumulus.aidevs3.service.S02e05Service;

@RestController
@RequestMapping("/main")
public class MainController {

    private final S01e02Service s01e02Service;
    private final S01e03Service s01e03Service;
    private final S01e05Service s01e05Service;
    private final S02e01Service s02e01Service;
    private final S02e02Service s02e02Service;
    private final S02e03Service s02e03Service;
    private final S02e04Service s02e04Service;
    private final S02e05Service s02e05Service;

    @Autowired
    public MainController(S01e02Service s01e02Service, S01e03Service s01e03Service, S01e05Service s01e05Service, 
    S02e01Service s02e01Service, S02e02Service s02e02Service, S02e03Service s02e03Service, S02e04Service s02e04Service, S02e05Service s02e05Service) {
        this.s01e02Service = s01e02Service;
        this.s01e03Service = s01e03Service;
        this.s01e05Service = s01e05Service;
        this.s02e01Service = s02e01Service;
        this.s02e02Service = s02e02Service;
        this.s02e03Service = s02e03Service;
        this.s02e04Service = s02e04Service;
        this.s02e05Service = s02e05Service;
    }

    @GetMapping("/s01e02/flag")
    public String getS01e02Flag() {
        return s01e02Service.getFlag();
    }

    @GetMapping("/s01e03/flag")
    public String getS01e03Flag() {
        return s01e03Service.getFlag();
    }

    @GetMapping("/s01e05/flag")
    public String getS01e05Flag() {
        return s01e05Service.getFlag();
    }

    @GetMapping("/s02e01/flag")
    public String getS02e01Flag() {
        return s02e01Service.getFlag();
    }

    @GetMapping("/s02e02/flag")
    public String getS02e02Flag() {
        return s02e02Service.getFlag();
    }

    @GetMapping("/s02e03/flag")
    public String getS02e03Flag() {
        return s02e03Service.getFlag();
    }

    @GetMapping("/s02e04/flag")
    public String getS02e04Flag() {
        return s02e04Service.getFlag();
    }

    @GetMapping("/s02e05/flag")
    public String getS02e05Flag() {
        return s02e05Service.getFlag();
    }
}
