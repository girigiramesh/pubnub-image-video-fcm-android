package com.pubnub_fcm_example.manager;

/**
 * Created by Hari Prasad on 1/30/17.
 */

public class EventMessage<T> {
    public static final String REPORT_S3_FILE_UPLOAD = "type_report_s3_file_upload";

    private T mResult;
    private String mEvent, mMessage;
    private boolean mIsSuccess;

    //error event
    public EventMessage(String event, boolean isSuccess, String errorMessage) {
        this.mEvent = event;
        this.mIsSuccess = isSuccess;
        this.mMessage = errorMessage;
    }

    public EventMessage(String event, boolean isSuccess, T result) {
        this.mEvent = event;
        this.mIsSuccess = isSuccess;
        this.mResult = result;
    }

    public String getEvent() {
        return mEvent;
    }

    public T getResult() {
        return mResult;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isSuccess() {
        return mIsSuccess;
    }
}
