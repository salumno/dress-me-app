package ru.kpfu.itis.dressmeapp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.dressmeapp.form.ClothesItemAddForm;
import ru.kpfu.itis.dressmeapp.model.ClothesItemCategory;
import ru.kpfu.itis.dressmeapp.model.Sex;
import ru.kpfu.itis.dressmeapp.services.ClothesService;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Controller
public class ClothesController {

    private ClothesService clothesService;

    public ClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }

    @PostMapping("/clothes")
    @ResponseBody
    public ResponseEntity<String> addNewClothes(@ModelAttribute ClothesItemAddForm form) {
        clothesService.addClothesItems(form);
        return ResponseEntity.ok("Successfully added");
    }

    @GetMapping("/clothes")
    public String getAdminClothesPage(@ModelAttribute("model")ModelMap model) {
        model.addAttribute("sexValues", Sex.values());
        model.addAttribute("categories", ClothesItemCategory.values());
        return "admin-clothes-page";
    }
}
