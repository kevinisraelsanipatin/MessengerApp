package com.example.messengerapp.Fragments;

import com.example.messengerapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=aa"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
