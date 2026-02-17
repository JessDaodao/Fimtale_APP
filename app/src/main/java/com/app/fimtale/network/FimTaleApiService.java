package com.app.fimtale.network;

import com.app.fimtale.model.MainPageResponse;
import com.app.fimtale.model.TopicDetailResponse;
import com.app.fimtale.model.TopicListResponse;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface FimTaleApiService {
    @POST
    @Multipart
    Call<ResponseBody> login(
            @Url String url,
            @Part("account") RequestBody account,
            @Part("password") RequestBody password,
            @Part("tencentCode") RequestBody tencentCode,
            @Part("tencentRand") RequestBody tencentRand
    );

    @GET
    Call<ResponseBody> checkLogin(@Url String url);

    @GET(".")
    Call<MainPageResponse> getHomePage(
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass
    );

    @GET("topics")
    Call<TopicListResponse> getTopicList(
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass,
            @Query("page") int page,
            @Query("q") String query,
            @Query("sortby") String sortBy
    );

    @GET("t/{topicId}")
    Call<TopicDetailResponse> getTopicDetail(
            @Path("topicId") int topicId,
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass,
            @Query("format") String format
    );
}