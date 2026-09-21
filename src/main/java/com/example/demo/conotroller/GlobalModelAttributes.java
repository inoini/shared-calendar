package com.example.demo.conotroller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.entity.AppSetting;
import com.example.demo.repository.AppSettingRepository;

@ControllerAdvice
public class GlobalModelAttributes {

    private final AppSettingRepository settingRepository;

    public GlobalModelAttributes(AppSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @ModelAttribute
    public void addSystemSettings(Model model) {
        AppSetting setting = settingRepository.findById(1L).orElse(new AppSetting());
        model.addAttribute("farmName", setting.getFarmName());
        model.addAttribute("notificationsEnabled",
                !Boolean.FALSE.equals(setting.getNotificationsEnabled()));
        model.addAttribute("uiTheme", validTheme(setting.getUiTheme()));
        model.addAttribute("layoutMode", validLayout(setting.getLayoutMode()));
    }

    private String validTheme(String value) {
        return switch (value == null ? "" : value) {
            case "blue", "earth", "discord" -> value;
            default -> "green";
        };
    }

    private String validLayout(String value) {
        return switch (value == null ? "" : value) {
            case "compact", "wide" -> value;
            default -> "standard";
        };
    }
}
