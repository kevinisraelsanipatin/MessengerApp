package com.example.messengerapp.Fragments;

import com.example.messengerapp.Notifications.Sender;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAlhyEHxk:APA91bFQzUcX3q-mV45mwAsoMM9asw_zOKYHz5-5Z-WMHwmFB1EIEyDVW4kGF-djcamZ8hIFRs1iFutMai35v-v7Ks5bvAkgBmpWMU0WH6jq2-BCpHoAhFZUIusDUjnjb6-OHhSBfYek"
    })

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body Sender body);
}
