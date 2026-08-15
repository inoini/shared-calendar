
package com.example.demo.conotroller;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Field;
import com.example.demo.repository.FieldRepository;
import com.example.demo.service.GeocodingService;
import com.example.demo.service.GeocodingService.Coordinates;

@Controller
@RequestMapping("/field")
public class FieldController {

    private final FieldRepository fieldRepository;
    private final GeocodingService geocodingService;

    public FieldController(
            FieldRepository fieldRepository,
            GeocodingService geocodingService) {
        this.fieldRepository = fieldRepository;
        this.geocodingService = geocodingService;
    }

    // 圃場一覧
    @GetMapping
    public String list(Model model) {
        model.addAttribute("fields", findAllFields());
        return "field_list";
    }

    // 登録画面
    @GetMapping("/new")
    public String newField(Model model) {
        model.addAttribute("field", new Field());
        return "field_add";
    }

    // 登録・更新
    @PostMapping("/save")
    public String save(
            @ModelAttribute Field field,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean isNew = field.getId() == null;
        Field storedField = null;

        if (!isNew) {
            storedField = fieldRepository.findById(field.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "圃場が見つかりません。"));
        }

        field.setName(trimToNull(field.getName()));
        field.setLocation(trimToNull(field.getLocation()));
        field.setCropName(trimToNull(field.getCropName()));
        field.setMemo(trimToNull(field.getMemo()));

        if (field.getName() == null || field.getLocation() == null) {
            model.addAttribute("field", field);
            model.addAttribute("errorMessage", "圃場名と住所は必ず入力してください。");
            return "field_add";
        }

        boolean addressUnchanged = storedField != null
                && Objects.equals(
                        normalizeAddress(storedField.getLocation()),
                        normalizeAddress(field.getLocation()));

        boolean reusedCoordinates = addressUnchanged
                && storedField.getLatitude() != null
                && storedField.getLongitude() != null;

        if (reusedCoordinates) {
            field.setLatitude(storedField.getLatitude());
            field.setLongitude(storedField.getLongitude());
        } else {
            Optional<Coordinates> coordinates = geocodingService.geocode(field.getLocation());
            if (coordinates.isPresent()) {
                field.setLatitude(coordinates.get().latitude());
                field.setLongitude(coordinates.get().longitude());
            } else {
                field.setLatitude(null);
                field.setLongitude(null);
            }
        }

        fieldRepository.save(field);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                isNew ? "圃場を登録しました。" : "圃場情報を更新しました。");

        if (field.getLatitude() == null || field.getLongitude() == null) {
            redirectAttributes.addFlashAttribute(
                    "warningMessage",
                    "住所は保存されましたが、地図上の位置を取得できませんでした。住所を詳しくして再度保存してください。");
        }

        return "redirect:/field";
    }

    // 削除（更新処理のためGETではなくPOSTを使用）
    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (!fieldRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "削除対象の圃場が見つかりません。");
            return "redirect:/field";
        }

        fieldRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "圃場を削除しました。");
        return "redirect:/field";
    }

    // 編集画面
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "圃場が見つかりません。"));

        model.addAttribute("field", field);
        return "field_add";
    }

    // 地図表示
    @GetMapping("/map")
    public String map(Model model) {
        List<Field> fields = findAllFields();
        long markerCount = fields.stream()
                .filter(field -> field.getLatitude() != null && field.getLongitude() != null)
                .count();

        model.addAttribute("fields", fields);
        model.addAttribute("mapMarkerCount", markerCount);
        return "field_map";
    }

    private List<Field> findAllFields() {
        return StreamSupport.stream(fieldRepository.findAll().spliterator(), false)
                .sorted(Comparator.comparing(
                        Field::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeAddress(String address) {
        return address == null ? null : address.strip().replaceAll("\\s+", "");
    }
}