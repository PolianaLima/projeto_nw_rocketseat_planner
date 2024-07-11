package com.rocketseat.planner.participant;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ParticipantData> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        ParticipantData participant = participantService.confirmParticipant(id, payload);
        return ResponseEntity.ok(participant);
    }

}
