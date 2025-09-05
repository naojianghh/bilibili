package logic.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("user/video/recommend")
    Call<VideoResponse> getVideoData(
            @Query("page") int page,
            @Query("pagesize") int pageSize
    );
}
