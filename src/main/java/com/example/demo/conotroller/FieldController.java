package com.example.demo.conotroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Field;
import com.example.demo.repository.FieldRepository;

@Controller
@RequestMapping("/field")
public class FieldController {

    private final FieldRepository fieldRepository;

    public FieldController(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }


    // 圃場一覧
    @GetMapping
    public String list(Model model) {

        model.addAttribute("fields", fieldRepository.findAll());

        return "field_list";
    }


    // 登録画面
    @GetMapping("/new")
    public String newField(Model model) {

        model.addAttribute("field", new Field());

        return "field_add";
    }


    // 保存
    @PostMapping("/save")
    public String save(@ModelAttribute Field field) {

        fieldRepository.save(field);

        return "redirect:/field";
    }


    // 削除
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        fieldRepository.deleteById(id);

        return "redirect:/field";
    }


    // 編集
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        Field field = fieldRepository.findById(id).orElseThrow();

        model.addAttribute("field", field);

        return "field_add";
    }
 // 地図表示
    @GetMapping("/map")
    public String map(Model model) {

        model.addAttribute("fields",
            fieldRepository.findAll());

        return "field_map";
    }
}