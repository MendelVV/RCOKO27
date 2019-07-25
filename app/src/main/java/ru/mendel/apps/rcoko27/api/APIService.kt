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
    fun registration(@Body req: RegistrationRequest): Call<RegResponse>

    @POST("registration.php")
//    fun autoLogin(@Body req: AutoLoginRequest): Call<RegResponse>
    fun autoLogin(@Header("token") token:String,  @Body req: AutoLoginRequest): Call<RegResponse>

    @POST("get_data.php")
    fun getData(@Body req: GetDataRequest): Call<GetDataResponse>

    @POST("get_data.php")
    fun getEvent(@Body req: GetEventRequest): Call<GetEventResponse>

    @POST("send_message.php")
    fun sendMessage(@Body req: SendMessageRequest): Call<SendMessageResponse>

    @POST("vote.php")
    fun vote(@Body req: VoteRequest): Call<VoteResponse>

    @POST("get_data.php")
    fun updateEvents(@Body req: UpdateEventsRequest): Call<UpdateEventsResponse>

    @POST("get_data.php")
    fun updateMessages(@Body req: UpdateMessagesRequest): Call<UpdateMessagesResponse>

    @POST("activities.php")
    fun getActivities(@Body req: ActivitiesRequest) : Call<ActivitiesResponse>
}