package com.boxever.interview.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatOccupant {

    private Integer occupantId;
    private Integer satisfactionPercent;
    private boolean isWindowOccupant;
    private boolean isExtraOccupant;
}
