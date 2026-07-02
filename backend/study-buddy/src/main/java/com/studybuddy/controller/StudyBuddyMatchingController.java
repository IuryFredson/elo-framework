package com.studybuddy.controller;

import com.studybuddy.dto.response.StudyBuddyMatchingResponseDTO;
import com.studybuddy.service.matching.StudyBuddyMatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/study-buddy/matching")
public class StudyBuddyMatchingController {

    private final StudyBuddyMatchingService matchingService;

    public StudyBuddyMatchingController(StudyBuddyMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping
    public ResponseEntity<StudyBuddyMatchingResponseDTO> buscarEstudantesCompativeis(
            @RequestParam UUID estudanteId,
            @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(matchingService.buscarEstudantesCompativeis(estudanteId, topN));
    }
}
