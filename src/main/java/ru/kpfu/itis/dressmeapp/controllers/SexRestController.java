package ru.kpfu.itis.dressmeapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.dressmeapp.services.SexService;

import java.util.List;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@RestController
@RequestMapping("/api")
public class SexRestController {

    private SexService sexService;

    public SexRestController(SexService sexService) {
        this.sexService = sexService;
    }

    @GetMapping("/{sex}/body-types")
    public List<String> getBodyTypeBySex(@PathVariable("sex") String sex) {
        return sexService.getBodyTypeValuesBySexValue(sex);
    }
}
