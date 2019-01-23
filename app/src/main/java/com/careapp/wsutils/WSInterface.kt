package com.careapp.wsutils

import com.google.gson.JsonElement
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface WSInterface {

    @FormUrlEncoded
    @POST("/api/v1/users/login.json")
    fun requestForLogin(@Field("username") username: String, @Field("password") password: String): Call<JsonElement>

    @GET("/api/v1/centers.json")
    fun getAnganwadiCenters(@Query("token") token: String): Call<JsonElement>

    @GET("/api/v1/questions/centers.json")
    fun getQuestionsList(
        @Query("token") token: String, @Query("offset") offset: String,
        @Query("limit") limit: String
    ): Call<JsonElement>

    @POST("/customerFamilyOrderByAgent/{orderId}")
    fun createNewOrder(@Path("orderId") orderId: String, @QueryMap(encoded = true) params: HashMap<String, String>): Call<JsonElement>

    @GET("/api/v1/categories.json")
    fun getCategories(@Query("token") token: String): Call<JsonElement>

    @GET("/api/v1/categories/{categoryId}/questions.json")
    fun getQuestionsOfCategory(@Path("categoryId") categoryId: String, @Query("token") token: String): Call<JsonElement>

    @DELETE("/orders/{orderId}/products/{orderProductId}")
    fun deleteProductFromOrder(@Path("orderId") orderId: String, @Path("orderProductId") orderProductId: String): Call<JsonElement>

    @POST("/api/v1/questions/{questionId}/answers.json")
    fun postAnswerToQuestion(@Path("questionId") questionId: String, @Body body: RequestBody, @Query("token") token: String): Call<JsonElement>

    @GET("/api/v1/districts.json")
    fun getDistricts(@Query("token") token: String): Call<JsonElement>

    @GET("/api/v1/districts/{districtName}/blocks.json")
    fun getBlocks(@Path("districtName") districtName: String, @Query("token") token: String): Call<JsonElement>

    @GET("/api/v1/blocks/{blockName}/centers.json")
    fun getListOfAWCs(@Path("blockName") blockName: String, @Query("token") token: String): Call<JsonElement>

    @GET("/api/v1/centers/{centerId}/add.json")
    fun addAWC(@Path("centerId") centerId: String, @Query("token") token: String): Call<JsonElement>

}
