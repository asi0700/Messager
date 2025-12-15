package com.example.first_project.network;

import com.example.first_project.network.dto.AuthRequest;
import com.example.first_project.network.dto.AuthResponse;
import com.example.first_project.network.dto.ChatMessageRequest;
import com.example.first_project.network.dto.ChatStartRequest;
import com.example.first_project.network.dto.ChatStartResponse;
import com.example.first_project.network.dto.ChatsResponse;
import com.example.first_project.network.dto.MessageResponse;
import com.example.first_project.network.dto.MessagesResponse;
import com.example.first_project.network.dto.StatusRequest;
import com.example.first_project.network.dto.UsersResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/register")
    Call<AuthResponse> register(@Body AuthRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @GET("user_chats/{userId}")
    Call<ChatsResponse> getUserChats(@Path("userId") String userId);

    @POST("chats/start")
    Call<ChatStartResponse> startChat(@Body ChatStartRequest request);

    @GET("chats/{chatId}/messages")
    Call<MessagesResponse> getMessages(@Path("chatId") String chatId, @Query("limit") int limit);

    @POST("chats/{chatId}/messages")
    Call<MessageResponse> sendTextMessage(@Path("chatId") String chatId, @Body ChatMessageRequest request);

    @Multipart
    @POST("chats/{chatId}/messages/image")
    Call<MessageResponse> sendImageMessage(@Path("chatId") String chatId,
                                           @Part MultipartBody.Part file,
                                           @Part("caption") String caption);

    @GET("users")
    Call<UsersResponse> searchUsers(@Query("q") String query);

    @PATCH("users/{id}/status")
    Call<MessageResponse> updateStatus(@Path("id") String userId, @Body StatusRequest request);
}

