package com.pubnub_fcm_example.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by Ramesh on 1/9/17.
 */

public class Message implements Comparator<Message>, Comparable<Message> {
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String FROM_ID = "from_id";
    private static final String TAG = Message.class.getSimpleName();

    @Expose
    @SerializedName("sender_name")
    private String senderName;
    @Expose
    @SerializedName("devise_token")
    private String deviseToken;
    @Expose
    @SerializedName("text")
    private String text;
    @SerializedName("channel")
    @Expose
    private String channel;
    @Expose
    @SerializedName("type")
    private String type;
    @SerializedName("from_id")
    @Expose
    private String fromId;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
    @SerializedName("encrypted_url")
    @Expose
    private String encryptedUrl;
    @SerializedName("video_url")
    @Expose
    private String videoUrl;
    @SerializedName("encrypted_video_url")
    @Expose
    private String encryptedVideoUrl;
    @SerializedName("encrypted_thumbnail_url")
    @Expose
    private String encryptedThumbnailUrl;

    private long timestamp;
    private String filePath;


    public Message(String deviseToken, String senderName, String text) {
        this.deviseToken = deviseToken;
        this.senderName = senderName;
        this.text = text;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getEncryptedVideoUrl() {
        return encryptedVideoUrl;
    }

    public void setEncryptedVideoUrl(String encryptedVideoUrl) {
        this.encryptedVideoUrl = encryptedVideoUrl;
    }

    public String getEncryptedThumbnailUrl() {
        return encryptedThumbnailUrl;
    }

    public void setEncryptedThumbnailUrl(String encryptedThumbnailUrl) {
        this.encryptedThumbnailUrl = encryptedThumbnailUrl;
    }

    public String getEncryptedUrl() {
        return encryptedUrl;
    }

    public void setEncryptedUrl(String encryptedUrl) {
        this.encryptedUrl = encryptedUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDeviseToken() {
        return deviseToken;
    }

    public void setDeviseToken(String deviseToken) {
        this.deviseToken = deviseToken;
    }

    public String getText() {
        return text;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public static Message fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, Message.class);
    }

    public JSONObject toJson() {
        String jsonRepresentation = new Gson().toJson(this, Message.class);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonRepresentation);
        } catch (JSONException e) {
            Log.e(TAG, "Error converting to JSON: " + e.getMessage());
        }
        return jsonObject;
    }

    public static JSONObject getPubnubMessage(String type, String text, String accountId, String channel) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", type);
            jsonObject.put("text", text);
            jsonObject.put("from_id", accountId);
            jsonObject.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public int compare(Message lhs, Message rhs) {
        if (lhs.getTimestamp() < rhs.getTimestamp()) {
            return -1;
        } else if (lhs.getTimestamp() > rhs.getTimestamp()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Message another) {
        if (getTimestamp() < another.getTimestamp()) {
            return -1;
        } else if (getTimestamp() > another.getTimestamp()) {
            return 1;
        } else {
            return 0;
        }
    }
}
