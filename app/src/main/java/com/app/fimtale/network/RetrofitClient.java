package com.app.fimtale.network;
import com.app.fimtale.FimTaleApplication;
import com.app.fimtale.utils.UserPreferences;
import java.io.IOException;
import java.util.HashSet;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://fimtale.com/api/v1/";
    private static Retrofit retrofit = null;

    public static FimTaleApiService getInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Cookie 相关
            Interceptor addCookiesInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder();
                    String cookies = UserPreferences.getCookies(FimTaleApplication.getInstance());
                    if (!cookies.isEmpty()) {
                        builder.addHeader("Cookie", cookies);
                    }
                    return chain.proceed(builder.build());
                }
            };

            // Cookie 相关
            Interceptor receivedCookiesInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                        StringBuilder cookieBuffer = new StringBuilder();
                        for (String header : originalResponse.headers("Set-Cookie")) {
                            if (cookieBuffer.length() > 0) {
                                cookieBuffer.append("; ");
                            }
                            cookieBuffer.append(header);
                        }
                        UserPreferences.saveCookies(FimTaleApplication.getInstance(), cookieBuffer.toString());
                    }
                    return originalResponse;
                }
            };

            // 创建 OkHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(addCookiesInterceptor)
                    .addInterceptor(receivedCookiesInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // 创建 Retrofit 实例，并使用配置好的 OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(FimTaleApiService.class);
    }
}