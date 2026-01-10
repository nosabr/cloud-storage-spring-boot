package org.example.cloudstorage1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    @GetMapping
    public void getDirectory() {
        // path checked through @VALID in record
    }

    @PostMapping
    public void createNewDirectory() {}
}
