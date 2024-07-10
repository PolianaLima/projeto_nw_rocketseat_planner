package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponse;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.exceptions.ErrorDataExcpetion;
import com.rocketseat.planner.exceptions.RecursoNaoEncontradoException;
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.ParticipantCreateResponse;
import com.rocketseat.planner.participant.ParticipantData;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final ParticipantService participantService;
    private final ActivityService activityService;
    private final LinkService linkService;

    public TripCreateResponse saveTrip(TripRequestPayload payload) {

        validaFormatoData(payload.starts_at());
        validaFormatoData(payload.ends_at());

        Trip newTrip = new Trip(payload);

        if (newTrip.getStartsAt().isAfter(newTrip.getEndsAt())) {
            throw new ErrorDataExcpetion("A data de início não pode ser maior que a data de fim.");
        }

        this.tripRepository.save(newTrip);
        this.participantService.registerParticipantsToTrip(payload.emails_to_invite(), newTrip);

        return new TripCreateResponse(newTrip.getId());
    }

    public TripResponse getTripDetails(UUID tripId) {
        return this.tripRepository.findById(tripId)
                .map(this::to)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem não encontrada."));
    }

    public TripResponse updateTrip(UUID tripId, TripRequestPayload payload) {

        validaFormatoData(payload.starts_at());
        validaFormatoData(payload.ends_at());

        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem não encontrada."));
        updateDataTrip(trip, payload);

        if (trip.getStartsAt().isAfter(trip.getEndsAt())) {
            throw new ErrorDataExcpetion("A data de início não pode ser maior que a data de fim.");
        }

        this.tripRepository.save(trip);
        return to(trip);

    }

    public TripResponse confirmTrip(UUID tripId) {
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorDataExcpetion("Viagem não encontrada."));
        trip.setIsConfirmed(true);
        this.tripRepository.save(trip);

        return to(trip);
    }

    public ActivityResponse saveActivity(UUID tripId, ActivityRequestPayload payload) {
        validaFormatoData(payload.occurs_at());
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem não encontrada."));
        verificaDataActivity(payload.occurs_at(), trip);
        return this.activityService.registerActivity(payload, trip);
    }

    public List<ActivityData> getAllActivitiesTrip(UUID tripId){
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem não encontrada."));
        return this.activityService.getAllActivitiesFromTrip(trip.getId());
    }

    public ParticipantCreateResponse inviteParticipant(UUID tripId, ParticipantRequestPayload participantRequestPayload){
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem não encontrada."));

        ParticipantCreateResponse participantCreateResponse = this.participantService.registerParticipantToTrip(
                participantRequestPayload.email(),
                trip);

        if(Boolean.TRUE.equals(trip.getIsConfirmed())){
            this.participantService.triggerConfirmationEmailToParticipant(participantRequestPayload.email());
        }

        return participantCreateResponse;
    }

    public List<ParticipantData> getAllParticipantsTrip(UUID tripId){
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem não encontrada."));

        return this.participantService.getAllParticipantsFromTrip(trip.getId());
    }

    public LinkResponse saveLink(UUID tripId, LinkRequestPayload payload){
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem não encontrada."));

        return this.linkService.registerLink(payload, trip);
    }

    public List<LinkData> getAllLinksTrip(UUID tripId){

        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem não encontrada."));

        return this.linkService.getAllLinksFromTrip(trip.getId());
    }


    private void validaFormatoData(String data) {
        try {
            LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeException exception) {
            throw new ErrorDataExcpetion("Formato de data invalido. Use o formato ISO_DATE_TIME. Ex: 2021-08-01T10:00:00");
        }
    }

    private void updateDataTrip(Trip trip, TripRequestPayload payload) {
        trip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
        trip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
        trip.setDestination(payload.destination());
    }

    private void verificaDataActivity(String occurs_at, Trip trip){

        LocalDateTime data = LocalDateTime.parse(occurs_at, DateTimeFormatter.ISO_DATE_TIME);

        if(data.isBefore(trip.getStartsAt()) || data.isAfter(trip.getEndsAt())){
            throw new ErrorDataExcpetion("A data da atividade deve estar entre a data de início e fim da viagem.");
        }
    }


    private TripResponse to(Trip trip) {
        return new TripResponse(trip.getId(),
                trip.getDestination(),
                trip.getStartsAt().toString(),
                trip.getEndsAt(),
                trip.getIsConfirmed(),
                trip.getOwnerName(),
                trip.getOwnerEmail());
    }
}
