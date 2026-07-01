package com.app.fimtale.network;

import com.app.fimtale.model.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * FT-Go 阅读器客户端 API 接口
 * 对齐 ft-go 后端：/api/work, /api/user, /api/search, /api/channel, /api/tag
 */
public interface FimTaleApiService {

    // ==================== 认证 ====================

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

    // ==================== 作品阅读 ====================

    /** 获取作品详情（元信息 + 章节目录 + 前传/续集 + 用户操作/收藏） */
    @GET("work/get_work")
    Call<ApiResponse<WorkDetailResponse.Data>> getWorkDetail(
            @Query("work_id") int workId
    );

    /** 获取章节正文 */
    @GET("work/get_chapter")
    Call<ApiResponse<ChapterData>> getChapter(
            @Query("chapter_id") int chapterId
    );

    /** 批量获取作品全部章节（用于导出） */
    @GET("work/get_work_chapters")
    Call<ApiResponse<WorkChaptersData>> getWorkChapters(
            @Query("work_id") int workId
    );

    /** 轻量作品标题卡 */
    @GET("work/get_work_title")
    Call<ApiResponse<WorkDetailResponse.Work>> getWorkTitle(
            @Query("work_id") int workId
    );

    // ==================== 阅读追踪 ====================

    /** 上报浏览（打开作品或章节时调用） */
    @POST("work/track_view")
    Call<ApiResponse<Long>> trackView(
            @Body TrackViewRequest body
    );

    /** 上报下载 */
    @POST("work/track_download")
    Call<ApiResponse<Long>> trackDownload(
            @Body TrackDownloadRequest body
    );

    // ==================== 阅读进度 ====================

    /** 更新阅读进度 */
    @POST("user/update_read_progress")
    Call<ApiResponse<ReadProgress>> updateReadProgress(
            @Body UpdateReadProgressRequest body
    );

    /** 列出阅读进度（阅读历史） */
    @GET("user/list_read_progress")
    Call<ApiResponse<ListResponse<ReadProgress>>> listReadProgress(
            @Query("work_id") int workId,
            @Query("duration_days") int durationDays,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    // ==================== 评论 ====================

    /** 获取作品评论列表 */
    @GET("work/get_comments")
    Call<ApiResponse<CommentsData>> getComments(
            @Query("work_id") int workId,
            @Query("chapter_id") Integer chapterId,
            @Query("order_by") String orderBy,
            @Query("order_option") String orderOption,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    /** 获取单条评论 */
    @GET("work/get_comment")
    Call<ApiResponse<CommentDetailData>> getComment(
            @Query("comment_id") int commentId,
            @Query("order_by") String orderBy,
            @Query("order_option") String orderOption,
            @Query("chapter_id") Integer chapterId,
            @Query("per_page") Integer perPage
    );

    // ==================== 评论互动 ====================

    /** 评论点赞/踩 */
    @POST("work/do_comment_vote")
    Call<ApiResponse<Object>> doCommentVote(
            @Body CommentVoteRequest body
    );

    /** 评论 SH（精选荣誉） */
    @POST("work/do_comment_star_honor")
    Call<ApiResponse<Object>> doCommentStarHonor(
            @Body CommentStarHonorRequest body
    );

    /** 收藏评论 */
    @POST("work/favorite_comment")
    Call<ApiResponse<Object>> favoriteComment(
            @Body FavoriteCommentRequest body
    );

    // ==================== 作品互动 ====================

    /** 点赞/踩作品 */
    @POST("work/do_work_vote")
    Call<ApiResponse<Object>> doWorkVote(
            @Body WorkVoteRequest body
    );

    /** 高度评价 (HP) */
    @POST("work/do_work_high_praise")
    Call<ApiResponse<Object>> doWorkHighPraise(
            @Body WorkHighPraiseRequest body
    );

    // ==================== 收藏 ====================

    /** 收藏作品 */
    @POST("work/add_favorite_work")
    Call<ApiResponse<Object>> addFavoriteWork(
            @Body AddFavoriteWorkRequest body
    );

    /** 取消收藏 */
    @POST("work/remove_favorite_work")
    Call<ApiResponse<Object>> removeFavoriteWork(
            @Body RemoveFavoriteWorkRequest body
    );

    /** 我的收藏列表 */
    @GET("work/get_favorite_works")
    Call<ApiResponse<ListResponse<WorkDetailResponse.Work>>> getFavoriteWorks(
            @Query("folder_id") Integer folderId,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    /** 列出收藏夹 */
    @GET("user/get_favorite_folders")
    Call<ApiResponse<java.util.List<UserFavFolder>>> getFavoriteFolders();

    /** 创建收藏夹 */
    @POST("user/create_favorite_folder")
    Call<ApiResponse<UserFavFolder>> createFavoriteFolder(
            @Body CreateFavoriteFolderRequest body
    );

    /** 删除收藏夹 */
    @POST("user/delete_favorite_folder")
    Call<ApiResponse<Object>> deleteFavoriteFolder(
            @Body DeleteFavoriteFolderRequest body
    );

    // ==================== 标签（旧版兼容） ====================

    /** 获取标签详情（兼容 TagArticlesActivity） */
    @GET("tag/{tagName}")
    Call<TagDetailResponse> getTagTopics(
            @Path("tagName") String tagName,
            @Query("page") int page,
            @Query("sortby") String sortBy
    );

    /** 标签列表（兼容 TagListActivity） */
    @GET("tags")
    Call<TagListResponse> getTags(
            @Query("page") int page,
            @Query("sortby") String sortBy
    );

    // ==================== 话题/文章列表（旧版兼容） ====================

    /** 话题列表（兼容 ArticleFragment） */
    @GET("topic/list")
    Call<TopicListResponse> getTopicList(
            @Query("page") int page,
            @Query("query") String query,
            @Query("sortby") String sortBy
    );

    // ==================== 阅读历史（旧版兼容） ====================

    /** 旧版阅读历史（兼容 ProfileFragment） */
    @GET("history")
    Call<HistoryResponse> getHistory(
            @Query("page") int page
    );

    // ==================== 用户信息（旧版兼容） ====================

    /** 用户详情（兼容 UserDetailActivity） */
    @GET("u/{username}")
    Call<UserDetailResponse> getUserDetail(
            @Path("username") String username
    );

    /** 用户发布的话题列表（兼容 UserDetailActivity） */
    @GET("u/{username}/topics")
    Call<TopicListResponse> getUserTopics(
            @Path("username") String username,
            @Query("page") int page
    );

    // ==================== 搜索与发现 ====================

    /** 旧版推荐流（兼容 HomeFragment） */
    @GET("search/work_feed")
    Call<WorkFeedResponse> getWorkFeed(
            @Query("page") int page
    );

    @GET("search/work_feed")
    Call<WorkFeedResponse> getWorkFeedOld(
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    /** 旧版精选作品（兼容 HomeFragment） */
    @GET("work/get_curated_works")
    Call<CuratedWorksResponse> getCuratedWorks(
            @Query("count") int count
    );

    /** 搜索作品 */
    @GET("search/search_works")
    Call<ApiResponse<SearchWorksData>> searchWorks(
            @Query("query") String queryJson,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    /** 相关作品推荐 */
    @GET("search/search_related_works")
    Call<ApiResponse<java.util.List<WorkDetailResponse.Work>>> searchRelatedWorks(
            @Query("id") int workId
    );

    /** 推荐流（新 API） */
    @GET("search/work_feed")
    Call<ApiResponse<java.util.List<WorkDetailResponse.Work>>> getWorkFeedV2(
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    // ==================== 标签 ====================

    @GET("tag/get_tags_by_type_names")
    Call<ApiResponse<Object>> getTagsByTypeNames(
            @Query("type_names") String typeNames
    );

    @GET("tag/get_tag_types")
    Call<ApiResponse<Object>> getTagTypes();

    // ==================== 杂项 ====================

    /** 随机座右铭（启动页/空状态） */
    @GET("misc/get_random_motto")
    Call<ApiResponse<MottoData>> getRandomMotto();

    /** 检查更新（旧版兼容） */
    @GET
    Call<UpdateResponse> checkUpdate(@Url String url);

    // ==================== 请求体定义 ====================

    class TrackViewRequest {
        public String type;  // "work" 或 "chapter"
        public long id;

        public TrackViewRequest(String type, long id) {
            this.type = type;
            this.id = id;
        }
    }

    class TrackDownloadRequest {
        public long work_id;

        public TrackDownloadRequest(long workId) {
            this.work_id = workId;
        }
    }

    class UpdateReadProgressRequest {
        public long work_id;
        public long chapter_id;
        public float progress;  // 0.0 ~ 1.0

        public UpdateReadProgressRequest(long workId, long chapterId, float progress) {
            this.work_id = workId;
            this.chapter_id = chapterId;
            this.progress = progress;
        }
    }

    class WorkVoteRequest {
        public long work_id;
        public int operation;  // 1=赞, 2=踩

        public WorkVoteRequest(long workId, int operation) {
            this.work_id = workId;
            this.operation = operation;
        }
    }

    class WorkHighPraiseRequest {
        public long work_id;
        public int count;

        public WorkHighPraiseRequest(long workId, int count) {
            this.work_id = workId;
            this.count = count;
        }
    }

    class AddFavoriteWorkRequest {
        public long work_id;
        public Integer folder_id;

        public AddFavoriteWorkRequest(long workId, Integer folderId) {
            this.work_id = workId;
            this.folder_id = folderId;
        }
    }

    class RemoveFavoriteWorkRequest {
        public long work_id;

        public RemoveFavoriteWorkRequest(long workId) {
            this.work_id = workId;
        }
    }

    class CommentVoteRequest {
        public long comment_id;
        public int operation;  // 1=赞, 2=踩

        public CommentVoteRequest(long commentId, int operation) {
            this.comment_id = commentId;
            this.operation = operation;
        }
    }

    class CommentStarHonorRequest {
        public long comment_id;

        public CommentStarHonorRequest(long commentId) {
            this.comment_id = commentId;
        }
    }

    class FavoriteCommentRequest {
        public long comment_id;

        public FavoriteCommentRequest(long commentId) {
            this.comment_id = commentId;
        }
    }

    class CreateFavoriteFolderRequest {
        public String name;

        public CreateFavoriteFolderRequest(String name) {
            this.name = name;
        }
    }

    class DeleteFavoriteFolderRequest {
        public long folder_id;

        public DeleteFavoriteFolderRequest(long folderId) {
            this.folder_id = folderId;
        }
    }

    // ==================== 响应体定义 ====================

    /** getChapter 响应 */
    class ChapterData {
        private Chapter chapter;

        public Chapter getChapter() { return chapter; }
    }

    /** 章节模型（对应后端 po.Chapter） */
    class Chapter {
        private long id;
        @com.google.gson.annotations.SerializedName("work_id")
        private long workId;
        private String title;
        private String content;
        @com.google.gson.annotations.SerializedName("order_num")
        private int orderNum;
        @com.google.gson.annotations.SerializedName("count_character")
        private int countCharacter;
        @com.google.gson.annotations.SerializedName("count_image")
        private int countImage;
        @com.google.gson.annotations.SerializedName("count_view")
        private int countView;
        @com.google.gson.annotations.SerializedName("created_at")
        private String createdAt;
        @com.google.gson.annotations.SerializedName("edited_at")
        private String editedAt;
        @com.google.gson.annotations.SerializedName("status_del")
        private int statusDel;

        public long getId() { return id; }
        public long getWorkId() { return workId; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public int getOrderNum() { return orderNum; }
        public int getCountCharacter() { return countCharacter; }
        public int getCountImage() { return countImage; }
        public int getCountView() { return countView; }
        public String getCreatedAt() { return createdAt; }
        public String getEditedAt() { return editedAt; }
        public int getStatusDel() { return statusDel; }
    }

    /** getWorkChapters 响应 */
    class WorkChaptersData {
        @com.google.gson.annotations.SerializedName("chapters")
        private java.util.List<Chapter> chapters;

        public java.util.List<Chapter> getChapters() { return chapters; }
    }

    /** getComments 响应 */
    class CommentsData {
        @com.google.gson.annotations.SerializedName("comments")
        private java.util.List<CommentData> comments;
        @com.google.gson.annotations.SerializedName("user_operations")
        private java.util.List<CommentOperationEntry> userOperations;
        @com.google.gson.annotations.SerializedName("user_favs")
        private java.util.List<CommentFavEntry> userFavs;
        @com.google.gson.annotations.SerializedName("total_count")
        private long totalCount;

        public java.util.List<CommentData> getComments() { return comments; }
        public long getTotalCount() { return totalCount; }
    }

    class CommentOperationEntry {
        private long id;
        private long commentId;
        private int operation;
        public int getOperation() { return operation; }
    }

    class CommentFavEntry {
        private long id;
        private long commentId;
    }

    /** getComment 响应 */
    class CommentDetailData {
        private CommentData comment;
        private int pageNumber;
    }

    /** searchWorks 响应 */
    class SearchWorksData {
        private String query;
        @com.google.gson.annotations.SerializedName("works")
        private java.util.List<WorkDetailResponse.Work> works;
        private java.util.Map<String, Object> bins;
        private long total;

        public java.util.List<WorkDetailResponse.Work> getWorks() { return works; }
        public long getTotal() { return total; }
    }

    /** getRandomMotto 响应 */
    class MottoData {
        private String motto;
        private String author;

        public String getMotto() { return motto; }
        public String getAuthor() { return author; }
    }
}
