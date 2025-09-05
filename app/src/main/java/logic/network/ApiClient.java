package logic.network;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.Url;

public class ApiClient {

    private static final String BASE_URL = Url.url;
    private static Retrofit retrofit = null;
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIxODk2MDkzNTUwMCJ9.JV85gnurhGUCeK7D_DnG3NHznpABmSqtse3oNw1RDoc";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        // 构建新请求并添加请求头
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", TOKEN)
                                .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                                .header("Accept", "*/*")
                                .header("Host", Url.host)
                                .build();
                        return chain.proceed(newRequest);
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofit().create(ApiService.class);
    }
}
