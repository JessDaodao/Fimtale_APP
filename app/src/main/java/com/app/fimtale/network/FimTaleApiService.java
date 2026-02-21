package com.app.fimtale.network;

import com.app.fimtale.model.FavoritesResponse;
import com.app.fimtale.model.HistoryResponse;
import com.app.fimtale.model.MainPageResponse;
import com.app.fimtale.model.TagDetailResponse;
import com.app.fimtale.model.TopicDetailResponse;
import com.app.fimtale.model.TopicListResponse;
import com.app.fimtale.model.UpdateResponse;
import com.app.fimtale.model.UserDetailResponse;
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

    @GET("history")
    Call<HistoryResponse> getHistory(
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass,
            @Query("page") int page
    );

    @GET("favorites")
    Call<FavoritesResponse> getFavorites(
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass,
            @Query("page") int page
    );

    @GET("u/{username}")
    Call<UserDetailResponse> getUserDetail(
            @Path("username") String username,
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass
    );

    @GET("u/{username}/topics")
    Call<TopicListResponse> getUserTopics(
            @Path("username") String username,
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass,
            @Query("page") int page
    );

    @GET("tag/{tagName}")
    Call<TagDetailResponse> getTagTopics(
            @Path("tagName") String tagName,
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass,
            @Query("page") int page,
            @Query("sortby") String sortBy
    );

    @GET("save-reading-progress")
    Call<ResponseBody> saveReadingProgress(
            @Query("PostID") int postId,
            @Query("Progress") String progress,
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass
    );

    @GET
    Call<UpdateResponse> checkUpdate(@Url String url);

    @GET("tags")
    Call<com.app.fimtale.model.TagListResponse> getTags(
            @Query("APIKey") String apiKey,
            @Query("APIPass") String apiPass,
            @Query("page") int page
    );
}
