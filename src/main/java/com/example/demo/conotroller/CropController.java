package com.example.demo.conotroller;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import com.example.demo.entity.Crop;
import com.example.demo.repository.CropRepository;



@Controller
public class CropController {



    @Autowired
    private CropRepository cropRepository;




    // 作物一覧
    @GetMapping("/crop")
    public String cropList(Model model){


        List<Crop> crops =
                cropRepository.findAll();


        model.addAttribute(
                "crops",
                crops
        );


        return "crop/list";

    }





    // 登録画面
    @GetMapping("/crop/add")
    public String cropAdd(Model model){


        model.addAttribute(
                "crop",
                new Crop()
        );


        return "crop/add";

    }





    // 保存
    @PostMapping("/crop/save")
    public String cropSave(
            @ModelAttribute Crop crop
    ){


        cropRepository.save(crop);


        return "redirect:/crop";

    }



}