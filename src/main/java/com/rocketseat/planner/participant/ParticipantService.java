package com.rocketseat.planner.participant;

import com.rocketseat.planner.trip.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public void registerParticipantsToTrip(List<String> participantsToInvite, Trip trip) {
        List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

        this.participantRepository.saveAll(participants);
    }

    public ParticipantCreateResponse registerParticipantToTrip(String email, Trip trip) {
        Participant participant = new Participant(email, trip);
        this.participantRepository.save(participant);


        return new ParticipantCreateResponse(participant.getId());
    }

    public List<ParticipantData> getAllParticipantsFromTrip(UUID tripId) {
        return this.participantRepository.findByTripId(tripId).stream().map(participant -> new ParticipantData(
                participant.getId(),
                participant.getName(),
                participant.getEmail(),
                participant.getIsConfirmed()
        )).toList();
    }

    public void triggerConfirmationEmailToTrip(UUID tripId) {
    }

    public void triggerConfirmationEmailToParticipant(String email) {

    }


}
