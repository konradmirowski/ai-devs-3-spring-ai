package altocumulus.aidevs3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import altocumulus.aidevs3.service.S01e02Service;
import altocumulus.aidevs3.service.S01e03Service;
import altocumulus.aidevs3.service.S01e05Service;

@RestController
@RequestMapping("/main")
public class MainController {

    private final S01e02Service s01e02Service;
    private final S01e03Service s01e03Service;
    private final S01e05Service s01e05Service;

    // @Autowired
    // public MainController(
    //     S01e02Service s01e02Service,
    //     S01e03Service s01e03Service,
    //     S01e05Service s01e05Service
    // ) {
    //     this.s01e02Service = s01e02Service;
    //     this.s01e03Service = s01e03Service;
    //     this.s01e05Service = s01e05Service;
    // }

        @Autowired
    public MainController(S01e02Service s01e02Service, S01e03Service s01e03Service, S01e05Service s01e05Service) {
        this.s01e02Service = s01e02Service;
        this.s01e03Service = s01e03Service;
        this.s01e05Service = s01e05Service;
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
}
