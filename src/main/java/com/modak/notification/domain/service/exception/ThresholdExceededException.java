package com.modak.notification.domain.service.exception;

public class ThresholdExceededException extends Exception {

    public ThresholdExceededException() {
    }

    public ThresholdExceededException(String msg) {
        super(msg);
    }
}
