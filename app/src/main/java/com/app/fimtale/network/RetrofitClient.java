package com.app.fimtale.network;
import com.app.fimtale.FimTaleApplication;
import com.app.fimtale.utils.UserPreferences;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashSet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
    private static Retrofit updateRetrofit = null;

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

    public static FimTaleApiService getUpdateService() {
        if (updateRetrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        })
                        .addInterceptor(loggingInterceptor)
                        .build();

                updateRetrofit = new Retrofit.Builder()
                        .baseUrl("https://ftapp.eqad.fun/")
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return updateRetrofit.create(FimTaleApiService.class);
    }
}
