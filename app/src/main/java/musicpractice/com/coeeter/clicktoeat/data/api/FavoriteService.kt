package musicpractice.com.coeeter.clicktoeat.data.api

import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponse
import musicpractice.com.coeeter.clicktoeat.data.models.Favorite
import retrofit2.Response
import retrofit2.http.*

interface FavoriteService {
    //get favorites of user
    @GET("favorites/{token}")
    suspend fun getUserFavorites(@Path("token") token: String): Response<ArrayList<Favorite>>

    //create favorite of user
    @FormUrlEncoded
    @POST("favorites/{token}")
    suspend fun createFavorites(
        @Path("token") token: String,
        @Field("restaurantId") restaurantId: Int
    ): Response<DefaultResponse>

    //delete favorite of user
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "favorites/{token}", hasBody = true)
    suspend fun deleteFavorites(
        @Path("token") token: String,
        @Field("favoriteId") favoriteId: Int
    ): Response<DefaultResponse>
}
