package com.studybuddy.controller;

import com.elo.web.MatchingRestController;
import com.studybuddy.dto.response.StudyBuddyMatchingResponseDTO;
import com.studybuddy.service.matching.StudyBuddyMatchingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/study-buddy/matching")
public class StudyBuddyMatchingController extends MatchingRestController<StudyBuddyMatchingResponseDTO> {

    private final StudyBuddyMatchingService matchingService;

    public StudyBuddyMatchingController(StudyBuddyMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @Override
    protected StudyBuddyMatchingResponseDTO calcularMatches(UUID solicitanteId, int topN) {
        return matchingService.buscarEstudantesCompativeis(solicitanteId, topN);
    }
}