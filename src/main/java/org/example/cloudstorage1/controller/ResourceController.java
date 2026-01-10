package org.example.cloudstorage1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    @GetMapping
    public void getResourceData(){}

    @DeleteMapping
    public void deleteResource(){}

    @PostMapping
    public void uploadResource(){}

    @GetMapping("/delete")
    public void downloadResource(){}

    @GetMapping("/move")
    public void moveResource(){}

    @GetMapping("/search")
    public void findResource(){}


}
