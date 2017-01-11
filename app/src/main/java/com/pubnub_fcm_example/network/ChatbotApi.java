package com.pubnub_fcm_example.network;

import com.pubnub_fcm_example.model.Message;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ramesh on 1/11/17.
 */
public interface ChatbotApi {

    @GET("/Service/GetSearchCard")
    Call<String> getSearchCard(String fullName, Message message, @Query("searchstring") String searchString, Map<String, RequestBody> requestBodyMap, @Query("domain") String domain);
}


