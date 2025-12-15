package com.example.first_project.network;

import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        @Nullable String token = sessionManager.getToken();
        if (token == null) {
            return chain.proceed(original);
        }
        Request authed = original.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(authed);
    }
}

