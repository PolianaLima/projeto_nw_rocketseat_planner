package com.rocketseat.planner.trip;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripResponse(UUID id,
                           String destination,
                           @JsonProperty("starts_at")
                           String startsAt,
                           @JsonProperty("ends_at")
                           LocalDateTime endsAt,
                            @JsonProperty("is_confirmed")
                           Boolean isConfirmed,
                            @JsonProperty("owner_name")
                           String ownerName,
                            @JsonProperty("owner_email")
                           String ownerEmail){
}
