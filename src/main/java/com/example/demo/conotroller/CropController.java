package com.example.demo.conotroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Crop;
import com.example.demo.repository.CropRepository;

@Controller
public class CropController {

    @Autowired
    private CropRepository cropRepository;

    // 作物一覧
    @GetMapping("/crop")
    public String cropList(Model model) {

        List<Crop> crops = cropRepository.findAll();

        model.addAttribute("crops", crops);

        return "crop/list";
    }

    // 新規登録画面
    @GetMapping("/crop/add")
    public String cropAdd(Model model) {

        model.addAttribute("crop", new Crop());

        return "crop/add";
    }

    // 編集画面
    @GetMapping("/crop/edit/{id}")
    public String editCrop(@PathVariable Long id, Model model) {

        Crop crop = cropRepository.findById(id).orElse(new Crop());

        model.addAttribute("crop", crop);

        return "crop/add";
    }

    // 新規保存・更新
    @PostMapping("/crop/save")
    public String saveCrop(@ModelAttribute Crop crop) {

        cropRepository.save(crop);

        return "redirect:/crop";
    }

    // 削除
    @GetMapping("/crop/delete/{id}")
    public String deleteCrop(@PathVariable Long id) {

        cropRepository.deleteById(id);

        return "redirect:/crop";
    }

}