import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by allen on 2016/11/28.
 */
public interface ShanBeiApi {

    // https://api.shanbay.com/bdc/search/?word={}
    @GET("bdc/search")
    Call<Translation> listInfos(@Query("word") String query);
}
