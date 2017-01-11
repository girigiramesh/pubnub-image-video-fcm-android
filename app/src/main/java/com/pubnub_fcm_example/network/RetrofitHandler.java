package com.pubnub_fcm_example.network;

import com.pubnub_fcm_example.model.Message;
import com.pubnub_fcm_example.util.StringConverterFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ramesh on 1/11/17.
 */
public class RetrofitHandler {
    private static RetrofitHandler ourInstance = new RetrofitHandler();

    public static RetrofitHandler getInstance() {
        return ourInstance;
    }

    private RetrofitHandler() {
    }

    final OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.MINUTES)
            .connectTimeout(3, TimeUnit.MINUTES)
            .build();

    private Retrofit chatbotRetrofit = new Retrofit.Builder()
            .baseUrl("https://chatbot.sikkasoft.com")
            .addConverterFactory(StringConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();
    private ChatbotApi chatbotApi = chatbotRetrofit.create(ChatbotApi.class);

    public Call<String> createMessage(String fullName, Message message, String resourceName, Map<String, RequestBody> requestBodyMap, String requestKey) {
        return chatbotApi.getSearchCard(fullName, message, resourceName, requestBodyMap, requestKey);
    }
}
