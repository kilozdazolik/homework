package com.nn.homework.Controllers;

import com.nn.homework.Services.BatchJobService;
import com.nn.homework.Services.ExportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class JobController {

    private final BatchJobService batchJobService;
    private final ExportService exportService;

    public JobController(BatchJobService batchJobService, ExportService exportService) {
        this.batchJobService = batchJobService;
        this.exportService = exportService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("jobType") String jobType,
                                   Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Kérlek válassz egy fájlt");
            return "index";
        }

        try {
            batchJobService.processFileImport(file, jobType);

            model.addAttribute("message", "Sikeres importálás: " + jobType);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Hiba történt: " + e.getMessage());
        }

        return "index";
    }

    @PostMapping("/export")
    public String triggerExport(Model model) {
        try {
            String result = exportService.exportAllData();
            model.addAttribute("message", "Sikeres Exportálás: " + result);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Export hiba: " + e.getMessage());
        }
        return "index";
    }
}