package com.app.fimtale.network;

import com.app.fimtale.model.MainPageResponse;
import com.app.fimtale.model.TopicDetailResponse;
import com.app.fimtale.model.TopicListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FimTaleApiService {
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
            @Query("q") String query
    );

    @GET("t/{topicId}")
    Call<TopicDetailResponse> getTopicDetail(
            @Path("topicId") int topicId,
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass,
            @Query("format") String format
    );
}