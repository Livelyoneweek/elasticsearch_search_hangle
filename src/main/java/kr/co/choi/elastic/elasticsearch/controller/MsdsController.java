package kr.co.choi.elastic.elasticsearch.controller;

import kr.co.choi.elastic.elasticsearch.entity.Msds;
import kr.co.choi.elastic.elasticsearch.service.MsdsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/api/msds")
public class MsdsController {

    private final MsdsService msdsService;

    @PostMapping
    public Msds create(@RequestBody Msds msds) {
        log.info("### MsdsController.create");
        return msdsService.save(msds);
    }

    @PostMapping("/list")
    public List<Msds> createBulk(@RequestBody List<Msds> msdsList) {
        log.info("### MsdsController.createBulk");
        return msdsService.saveAll(msdsList);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Msds>> uploadCsv(@RequestParam("file") MultipartFile file) {
        log.info("### MsdsController.uploadCsv");
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<Msds> savedMsds = msdsService.uploadCsv(file);

        return ResponseEntity.ok(savedMsds);
    }

    @GetMapping("/{id}")
    public Optional<Msds> getById(@PathVariable String id) {
        log.info("### MsdsController.getById");
        return msdsService.findById(id);
    }

    @GetMapping
    public Iterable<Msds> getAll() {
        log.info("### MsdsController.getAll");
        return msdsService.findAll();
    }

    @GetMapping("/search")
    public List<Msds> searchByName(@RequestParam String name) {
        long startTime = System.currentTimeMillis(); // 시작 시간 기록

        List<Msds> byNameSimilar = msdsService.findByNameSimilar(name);
        long endTime = System.currentTimeMillis(); // 종료 시간 기록
        log.info("### MsdsController.uploadCsv - 완료 (실행 시간: {} ms)", endTime - startTime);

        return byNameSimilar;

    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        msdsService.deleteById(id);
    }
}
