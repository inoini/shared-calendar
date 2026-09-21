package com.example.demo.conotroller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.AppSetting;
import com.example.demo.entity.HarvestShipment;
import com.example.demo.entity.Schedule;
import com.example.demo.entity.Worker;
import com.example.demo.repository.AppSettingRepository;
import com.example.demo.repository.CropRepository;
import com.example.demo.repository.ExpenseRepository;
import com.example.demo.repository.FieldRepository;
import com.example.demo.repository.HarvestShipmentRepository;
import com.example.demo.repository.SalesRepository;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.WorkerRepository;

@Controller
public class OperationsController {

    private final ScheduleRepository scheduleRepository;
    private final WorkerRepository workerRepository;
    private final HarvestShipmentRepository harvestRepository;
    private final AppSettingRepository settingRepository;
    private final CropRepository cropRepository;
    private final FieldRepository fieldRepository;
    private final SalesRepository salesRepository;
    private final ExpenseRepository expenseRepository;

    public OperationsController(
            ScheduleRepository scheduleRepository,
            WorkerRepository workerRepository,
            HarvestShipmentRepository harvestRepository,
            AppSettingRepository settingRepository,
            CropRepository cropRepository,
            FieldRepository fieldRepository,
            SalesRepository salesRepository,
            ExpenseRepository expenseRepository) {
        this.scheduleRepository = scheduleRepository;
        this.workerRepository = workerRepository;
        this.harvestRepository = harvestRepository;
        this.settingRepository = settingRepository;
        this.cropRepository = cropRepository;
        this.fieldRepository = fieldRepository;
        this.salesRepository = salesRepository;
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/work-history")
    public String workHistory(Model model) {
        List<Schedule> schedules = scheduleRepository.findAllByOrderByDateDescStartTimeDesc();
        model.addAttribute("schedules", schedules);
        model.addAttribute("totalCount", schedules.size());
        model.addAttribute("completedCount", schedules.stream()
                .filter(schedule -> "完了".equals(schedule.getStatus())).count());
        return "operations/work-history";
    }

    @PostMapping("/work-history/status/{id}")
    public String updateWorkStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            RedirectAttributes redirect) {
        Schedule work = scheduleRepository.findById(id).orElse(null);
        if (work == null) {
            redirect.addFlashAttribute("error", "対象の作業が見つかりません。");
        } else if (!validWorkStatus(status)) {
            redirect.addFlashAttribute("error", "進捗状態を選び直してください。");
        } else {
            work.setStatus(status);
            scheduleRepository.save(work);
            redirect.addFlashAttribute("message", "進捗を更新しました。");
        }
        return "redirect:/work-history";
    }

    @GetMapping("/work-history/edit/{id}")
    public String editWork(@PathVariable("id") Long id, Model model,
            RedirectAttributes redirect) {
        Schedule work = scheduleRepository.findById(id).orElse(null);
        if (work == null) {
            redirect.addFlashAttribute("error", "対象の作業が見つかりません。");
            return "redirect:/work-history";
        }
        model.addAttribute("editWork", work);
        return workHistory(model);
    }

    @PostMapping("/work-history/update/{id}")
    public String updateWork(@PathVariable("id") Long id,
            @RequestParam("date") String date,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam("userName") String userName,
            @RequestParam("schedule") String description,
            @RequestParam(value = "fieldName", defaultValue = "") String fieldName,
            @RequestParam(value = "cropName", defaultValue = "") String cropName,
            @RequestParam(value = "workType", defaultValue = "") String workType,
            @RequestParam("status") String status,
            @RequestParam(value = "memo", defaultValue = "") String memo,
            Model model, RedirectAttributes redirect) {
        Schedule work = scheduleRepository.findById(id).orElse(null);
        if (work == null) {
            redirect.addFlashAttribute("error", "対象の作業が見つかりません。");
            return "redirect:/work-history";
        }
        // 入力内容は別オブジェクトで保持し、検証エラー時はDBを変更しない。
        Schedule input = new Schedule();
        input.setId(id);
        input.setDate(date);
        input.setStartTime(startTime);
        input.setEndTime(endTime);
        input.setUserName(userName);
        input.setSchedule(description);
        input.setFieldName(fieldName);
        input.setCropName(cropName);
        input.setWorkType(workType);
        input.setStatus(status);
        input.setMemo(memo);
        String error = null;
        try {
            LocalDate.parse(date);
            if (!LocalTime.parse(endTime).isAfter(LocalTime.parse(startTime))) {
                error = "終了時間は開始時間より後にしてください。";
            }
        } catch (DateTimeParseException ex) {
            error = "日付・開始時間・終了時間を正しく入力してください。";
        }
        if (userName.isBlank() || description.isBlank() || !validWorkStatus(status)) {
            error = "担当者・作業内容・進捗状態を入力してください。";
        }
        if (userName.length() > 50 || description.length() > 500
                || fieldName.length() > 100 || cropName.length() > 100
                || workType.length() > 100 || memo.length() > 1000) {
            error = "入力文字数が上限を超えています。";
        }
        if (error != null) {
            model.addAttribute("editWork", input);
            model.addAttribute("error", error);
            return workHistory(model);
        }
        work.setDate(date);
        work.setStartTime(startTime);
        work.setEndTime(endTime);
        work.setUserName(userName);
        work.setSchedule(description);
        work.setFieldName(fieldName);
        work.setCropName(cropName);
        work.setWorkType(workType);
        work.setStatus(status);
        work.setMemo(memo);
        // 使用資材の記録を保持し、編集で在庫を再度減らさない。
        scheduleRepository.save(work);
        redirect.addFlashAttribute("message", "作業内容を更新しました。");
        return "redirect:/work-history";
    }

    @PostMapping("/work-history/delete/{id}")
    public String deleteWork(@PathVariable("id") Long id, RedirectAttributes redirect) {
        if (scheduleRepository.findById(id).isEmpty()) {
            redirect.addFlashAttribute("error", "対象の作業が見つかりません。");
        } else {
            scheduleRepository.deleteById(id);
            redirect.addFlashAttribute("message", "作業を消去しました。");
        }
        return "redirect:/work-history";
    }

    private boolean validWorkStatus(String status) {
        return "未着手".equals(status) || "進行中".equals(status) || "完了".equals(status);
    }

    @GetMapping("/workers")
    public String workers(Model model) {
        model.addAttribute("workers", workerRepository.findAllByOrderByNameAsc());
        model.addAttribute("worker", new Worker());
        return "operations/workers";
    }

    @GetMapping("/workers/edit/{id}")
    public String editWorker(@PathVariable Long id, Model model) {
        model.addAttribute("workers", workerRepository.findAllByOrderByNameAsc());
        model.addAttribute("worker", workerRepository.findById(id).orElse(new Worker()));
        return "operations/workers";
    }

    @PostMapping("/workers/save")
    public String saveWorker(
            @ModelAttribute Worker worker,
            @RequestParam(defaultValue = "false") boolean active) {
        worker.setActive(active);
        workerRepository.save(worker);
        return "redirect:/workers";
    }

    @PostMapping("/workers/delete/{id}")
    public String deleteWorker(@PathVariable Long id) {
        workerRepository.deleteById(id);
        return "redirect:/workers";
    }

    @GetMapping("/harvests")
    public String harvests(Model model) {
        model.addAttribute("records", harvestRepository.findAllByOrderByWorkDateDescIdDesc());
        model.addAttribute("record", new HarvestShipment());
        return "operations/harvests";
    }

    @GetMapping("/harvests/edit/{id}")
    public String editHarvest(@PathVariable Long id, Model model) {
        model.addAttribute("records", harvestRepository.findAllByOrderByWorkDateDescIdDesc());
        model.addAttribute("record", harvestRepository.findById(id).orElse(new HarvestShipment()));
        return "operations/harvests";
    }

    @PostMapping("/harvests/save")
    public String saveHarvest(@ModelAttribute HarvestShipment record) {
        if (record.getWorkDate() == null) {
            record.setWorkDate(LocalDate.now());
        }
        if (record.getStatus() == null || record.getStatus().isBlank()) {
            record.setStatus("収穫済");
        }
        harvestRepository.save(record);
        return "redirect:/harvests";
    }

    @PostMapping("/harvests/delete/{id}")
    public String deleteHarvest(@PathVariable Long id) {
        harvestRepository.deleteById(id);
        return "redirect:/harvests";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<HarvestShipment> harvests = harvestRepository.findAll();
        long completed = schedules.stream()
                .filter(schedule -> "完了".equals(schedule.getStatus())).count();

        Map<String, Long> workTypeCounts = new LinkedHashMap<>();
        schedules.forEach(schedule -> {
            String type = schedule.getWorkType();
            if (type == null || type.isBlank()) {
                type = "その他";
            }
            workTypeCounts.merge(type, 1L, Long::sum);
        });

        double harvestedKg = harvests.stream()
                .map(HarvestShipment::getHarvestKg)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue).sum();
        double shippedKg = harvests.stream()
                .map(HarvestShipment::getShippedKg)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue).sum();
        double sales = salesRepository.findAll().stream()
                .map(item -> item.getAmount())
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue).sum();
        double expenses = expenseRepository.findAll().stream()
                .map(item -> item.getAmount())
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue).sum();

        model.addAttribute("scheduleCount", schedules.size());
        model.addAttribute("completedCount", completed);
        model.addAttribute("completionRate", schedules.isEmpty() ? 0 : Math.round(completed * 100.0 / schedules.size()));
        model.addAttribute("cropCount", cropRepository.count());
        model.addAttribute("fieldCount", fieldRepository.count());
        model.addAttribute("harvestedKg", harvestedKg);
        model.addAttribute("shippedKg", shippedKg);
        model.addAttribute("salesTotal", sales);
        model.addAttribute("expenseTotal", expenses);
        model.addAttribute("profitTotal", sales - expenses);
        model.addAttribute("workTypeCounts", workTypeCounts);
        model.addAttribute("workTypeMax", workTypeCounts.values().stream()
                .mapToLong(Long::longValue).max().orElse(1L));
        return "operations/reports";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        AppSetting setting = settingRepository.findById(1L).orElse(new AppSetting());
        setting.setUiTheme(validTheme(setting.getUiTheme()));
        setting.setLayoutMode(validLayout(setting.getLayoutMode()));
        model.addAttribute("setting", setting);
        return "operations/settings";
    }

    @PostMapping("/settings/save")
    public String saveSettings(
            @ModelAttribute AppSetting setting,
            @RequestParam(defaultValue = "false") boolean notificationsEnabled) {
        setting.setId(1L);
        setting.setNotificationsEnabled(notificationsEnabled);
        setting.setUiTheme(validTheme(setting.getUiTheme()));
        setting.setLayoutMode(validLayout(setting.getLayoutMode()));
        settingRepository.save(setting);
        return "redirect:/settings?saved";
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
