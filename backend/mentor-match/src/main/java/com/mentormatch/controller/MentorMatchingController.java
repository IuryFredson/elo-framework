package com.mentormatch.controller;

import com.elo.web.MatchingRestController;
import com.mentormatch.dto.response.MentorMatchingResponseDTO;
import com.mentormatch.service.matching.MentorMatchingService;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/mentor-match/matching")
public class MentorMatchingController extends MatchingRestController<MentorMatchingResponseDTO> {
    private final MentorMatchingService service;
    public MentorMatchingController(MentorMatchingService service) { this.service = service; }
    @Override protected MentorMatchingResponseDTO calcularMatches(UUID solicitanteId, int topN) { return service.buscarMentoresCompativeis(solicitanteId, topN); }
}
