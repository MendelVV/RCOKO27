package ru.mendel.apps.rcoko27.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.api.requests.*
import ru.mendel.apps.rcoko27.api.responses.*
import ru.mendel.apps.rcoko27.data.ActionData
import java.net.ConnectException
import java.net.SocketTimeoutException

object RcokoClient {
    //класс отправляющий запросы на сервер и получающий ответы

    private const val BASE_URL = "http://192.168.43.14/rcoko27/api/"
//    private const val BASE_URL = "http://10.0.0.72/rcoko27/api/"


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()!!

    private val service = retrofit.create<APIService>(APIService::class.java)!!

    fun reg(request: RegRequest){
        service.reg(request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: retrofit2.Response<RegResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        val action = ActionData(ActionData.ACTION_TO_NEXT)
                        ReactiveSubject.next(action)
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }
            }
        )
    }

    fun regCode(request: RegCodeRequest){
        service.regCode(request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: retrofit2.Response<RegResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        val action = ActionData(ActionData.ACTION_TO_NEXT)
                        ReactiveSubject.next(action)
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }
            }
        )
    }

    fun registration(request: RegistrationRequest){
        service.registration(request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: retrofit2.Response<RegResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        val action = ActionData(ActionData.ACTION_TO_MAIN)
                        ReactiveSubject.next(action)
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }
            }
        )
    }

    fun autoLogin(request: AutoLoginRequest){
        service.autoLogin(request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: Response<RegResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        val action = ActionData(ActionData.ACTION_TO_MAIN)
                        ReactiveSubject.next(action)
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun getData(request: GetDataRequest){
        service.getData(request).enqueue(
            object : Callback<GetDataResponse> {

                override fun onResponse(call: Call<GetDataResponse>, response: Response<GetDataResponse>) {
                    val res = response.body()!!
                    System.out.println(res.result)
                    if (res.result=="ok"){
//                        Log.d("MyTag","ok "+res.data.size)
                        ReactiveSubject.next(res)//отправили ответ
                    }else if (res.result=="error"){
//                        Log.d("MyTag","getData error")
                        baseError(res)
                    }else{
//                        Log.d("MyTag","getData unknowError")
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<GetDataResponse>, t: Throwable) {
//                    Log.i("MyTag","getData")
                    verifyError(t)
                }

            }
        )
    }

    fun getEvent(request: GetEventRequest){

        service.getEvent(request).enqueue(
            object : Callback<GetEventResponse> {

                override fun onResponse(call: Call<GetEventResponse>, response: Response<GetEventResponse>) {

                    val res = response.body()!!
                    if (res.result=="ok"){
                        ReactiveSubject.next(res)//отправили ответ
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<GetEventResponse>, t: Throwable) {
                    Log.i("MyTag","getEvent")
                    verifyError(t)
                }

            }
        )
    }

    fun sendMessage(request: SendMessageRequest){

        service.sendMessage(request).enqueue(
            object : Callback<SendMessageResponse> {

                override fun onResponse(call: Call<SendMessageResponse>, response: Response<SendMessageResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        ReactiveSubject.next(res)//отправили ответ
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<SendMessageResponse>, t: Throwable) {
                    Log.i("MyTag","sendMessage")
                    verifyError(t)
                }

            }
        )
    }

    fun vote(request: VoteRequest){

        service.vote(request).enqueue(
            object : Callback<VoteResponse> {

                override fun onResponse(call: Call<VoteResponse>, response: Response<VoteResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        ReactiveSubject.next(res)//отправили ответ
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<VoteResponse>, t: Throwable) {
                    Log.i("MyTag","vote")
                    verifyError(t)
                }

            }
        )
    }

    fun getActivities(request: ActivitiesRequest){

        service.getActivities(request).enqueue(
            object : Callback<ActivitiesResponse> {

                override fun onResponse(call: Call<ActivitiesResponse>, response: Response<ActivitiesResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        ReactiveSubject.next(res)//отправили ответ
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<ActivitiesResponse>, t: Throwable) {
                    System.err.println("on getActivities")
                    verifyError(t)
                }

            }
        )
    }

    fun updateEvents(request: UpdateEventsRequest){
        service.updateEvents(request).enqueue(
            object : Callback<UpdateEventsResponse> {

                override fun onResponse(call: Call<UpdateEventsResponse>, response: Response<UpdateEventsResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        ReactiveSubject.next(res)//отправили ответ
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<UpdateEventsResponse>, t: Throwable) {
                    Log.i("MyTag","updateEvents")
                    verifyError(t)
                }

            }
        )
    }

    fun updateMessages(request: UpdateMessagesRequest){
        service.updateMessages(request).enqueue(
            object : Callback<UpdateMessagesResponse> {

                override fun onResponse(call: Call<UpdateMessagesResponse>, response: Response<UpdateMessagesResponse>) {
                    val res = response.body()!!
                    if (res.result=="ok"){
                        ReactiveSubject.next(res)//отправили ответ
                    }else if (res.result=="error"){
                        baseError(res)
                    }else{
                        unknownError()
                    }
                }

                override fun onFailure(call: Call<UpdateMessagesResponse>, t: Throwable) {
//                    Log.i("MyTag","updateMessages")
                    verifyError(t)
                }

            }
        )
    }

    private fun baseError(response: BaseResponse){
        System.err.println("error type=${response.type}")
        val action = ActionData(ActionData.ACTION_ERROR)
        action.data[ActionData.ITEM_TYPE] = response.type!!
        ReactiveSubject.next(action)
    }

    private fun verifyError(t: Throwable){
        when (t){
            is ConnectException -> networkError()
            is SocketTimeoutException -> networkError()
            //else -> Log.e("MyTag","error",t)
        }
        System.err.println(t)
    }

    private fun networkError(){
        val action = ActionData(ActionData.ACTION_ERROR)
        action.data[ActionData.ITEM_TYPE] = "network"
        ReactiveSubject.next(action)
    }

    private fun unknownError(){
        val action = ActionData(ActionData.ACTION_ERROR)
        action.data[ActionData.ITEM_TYPE] = "unknown"
        ReactiveSubject.next(action)
    }
}