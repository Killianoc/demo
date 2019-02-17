package com.boxever.interview.exception;

import lombok.Data;

@Data
public class SeatAllocationException extends RuntimeException {

    public static final long serialVersionUID = 8519103641237709684L;

    private String message;

    public SeatAllocationException(String message) {
        this.message = message;
    }

}
