package ru.mendel.apps.rcoko27.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.mendel.apps.rcoko27.api.requests.*
import ru.mendel.apps.rcoko27.api.responses.*

interface APIService {
    //класс в котором будут все запросы

    @POST("registration.php")
    fun reg(@Body req: RegRequest): Call<RegResponse>

    @POST("registration.php")
    fun regCode(@Body req: RegCodeRequest): Call<RegResponse>

    @POST("registration.php")
    fun registration(@Header("token") token:String, @Body req: RegistrationRequest): Call<RegResponse>

    @POST("registration.php")
    fun autoLogin(@Header("token") token:String,  @Body req: AutoLoginRequest): Call<RegResponse>

    @POST("get_data.php")
    fun getData(@Header("token") token:String, @Body req: GetDataRequest): Call<GetDataResponse>

    @POST("get_data.php")
    fun getEvent(@Header("token") token:String, @Body req: GetEventRequest): Call<GetEventResponse>

    @POST("send_message.php")
    fun sendMessage(@Header("token") token:String, @Body req: SendMessageRequest): Call<SendMessageResponse>

    @POST("vote.php")
    fun vote(@Header("token") token:String, @Body req: VoteRequest): Call<VoteResponse>

    @POST("get_data.php")
    fun updateEvents(@Header("token") token:String, @Body req: UpdateEventsRequest): Call<UpdateEventsResponse>

    @POST("get_data.php")
    fun updateMessages(@Header("token") token:String, @Body req: UpdateMessagesRequest): Call<UpdateMessagesResponse>

    @POST("activities.php")
    fun getActivities(@Header("token") token:String, @Body req: ActivitiesRequest) : Call<ActivitiesResponse>
}