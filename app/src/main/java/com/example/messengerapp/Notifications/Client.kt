package com.example.messengerapp.Notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    object Client {
        private var retroFit: Retrofit? = null
        fun getClient(url: String?): Retrofit? {
            if (retroFit == null) retroFit =
                Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retroFit
        }

    }
}