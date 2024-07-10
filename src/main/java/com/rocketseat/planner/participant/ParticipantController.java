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

    private final ParticipantRepository participantRepository;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Optional<Participant> participant = this.participantRepository.findById(id);
        if(participant.isPresent()){
            Participant rawParticipant = participant.get();

            rawParticipant.setIsConfirmed(true);
            rawParticipant.setName(payload.name());

            this.participantRepository.save(rawParticipant);

            return ResponseEntity.ok(rawParticipant);
        }

        return ResponseEntity.notFound().build();
    }

}
