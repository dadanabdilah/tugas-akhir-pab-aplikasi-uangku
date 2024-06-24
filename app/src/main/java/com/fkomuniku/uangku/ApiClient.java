package com.fkomuniku.uangku;

import com.fkomuniku.uangku.model.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ApiClient instanceWithToken = null;
    private static ApiClient instanceWithoutToken = null;
    private ApiService apiService;

    private ApiClient(String token, boolean useToken) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (useToken) {
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String currentToken = TokenManager.getInstance(null).getToken(); // Get the current token dynamically
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "" + currentToken)
                            .method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://uangku.justnotes.my.id/api/") // Ganti dengan base URL Anda
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstanceWithToken(String token) {
        if (instanceWithToken == null) {
            instanceWithToken = new ApiClient(token, true);
        }
        return instanceWithToken;
    }

    public static synchronized ApiClient getInstanceWithoutToken() {
        if (instanceWithoutToken == null) {
            instanceWithoutToken = new ApiClient(null, false);
        }
        return instanceWithoutToken;
    }

    public ApiService getApiService() {
        return apiService;
    }
}
