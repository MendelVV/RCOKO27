package ru.mendel.apps.rcoko27.api

import android.util.Log
import com.google.gson.stream.MalformedJsonException
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
    const val IMAGE_URL = "http://192.168.43.14/feedback/resources/"
    private const val BASE_URL = "http://192.168.43.14/feedback/api/"

//    const val IMAGE_URL = "https://feedback.rcoko27.ru/feedback/resources/"
//    private const val BASE_URL = "https://feedback.rcoko27.ru/feedback/api/"

//    const val IMAGE_URL = "http://10.0.0.74/feedback/resources/"
//    private const val BASE_URL = "http://10.0.0.74/feedback/api/"


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()!!

    private val service = retrofit.create(APIService::class.java)!!

    fun reg(request: RegRequest){
        service.reg(request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: Response<RegResponse>) {
                    try {
                        val res = response.body()!!
                        if (res.result=="ok"){
                            val action = ActionData(ActionData.ACTION_TO_NEXT)
                            ReactiveSubject.next(action)
                        }else if (res.result=="error"){
                            baseError(res)
                        }else{
                            unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
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

                override fun onResponse(call: Call<RegResponse>, response: Response<RegResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> {
                                val action = ActionData(ActionData.ACTION_TO_NEXT)
                                ReactiveSubject.next(action)
                            }
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }

                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }
            }
        )
    }

    fun registration(request: RegistrationRequest, token: String){
        service.registration(token, request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: Response<RegResponse>) {
                    try {
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> {
                                val action = ActionData(ActionData.ACTION_TO_MAIN)
                                ReactiveSubject.next(action)
                            }
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }

                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }
            }
        )
    }

    fun resetPassword(request: ResetPasswordRequest){
        service.resetPassword(request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: Response<RegResponse>) {
                    try {
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> {
                                val action = ActionData(ActionData.ACTION_TO_NEXT)
                                ReactiveSubject.next(action)
                            }
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }

                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }
            }
        )
    }

    fun autoLogin(request: AutoLoginRequest, token: String){
//        service.autoLogin(request).enqueue(
        service.autoLogin(token, request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: Response<RegResponse>) {
                    try {
                        val res = response.body()!!
                        when {
                            res.result == "empty" -> {
                            }
                            res.result == "ok" -> {
                                val action = ActionData(ActionData.ACTION_TO_MAIN)
                                action.data[ActionData.ITEM_VERIFICATION] = res.verification.toString()
                                ReactiveSubject.next(action)
                            }
                            res.result == "error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun newPassword(request: NewPasswordRequest, token: String){
//        service.autoLogin(request).enqueue(
        service.newPassword(token, request).enqueue(
            object : Callback<RegResponse> {

                override fun onResponse(call: Call<RegResponse>, response: Response<RegResponse>) {
                    try {
                        val res = response.body()!!
                        when {
                            res.result == "empty" -> {
                            }
                            res.result == "ok" -> {
                                val action = ActionData(ActionData.ACTION_TO_MAIN)
                                action.data[ActionData.ITEM_VERIFICATION] = res.verification.toString()
                                ReactiveSubject.next(action)
                            }
                            res.result == "error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<RegResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun getData(request: GetDataRequest, token: String){
        service.getData(token, request).enqueue(
            object : Callback<GetDataResponse> {

                override fun onResponse(call: Call<GetDataResponse>, response: Response<GetDataResponse>) {
                    try {
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }

                }

                override fun onFailure(call: Call<GetDataResponse>, t: Throwable) {
//                    Log.i("MyTag","getData")
                    verifyError(t)
                }

            }
        )
    }

    fun getEvent(request: GetEventRequest, token: String){

        service.getEvent(token, request).enqueue(
            object : Callback<GetEventResponse> {

                override fun onResponse(call: Call<GetEventResponse>, response: Response<GetEventResponse>) {
                    try {
                        val res = response.body()!!
                        //показываем все что пришло
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }

                }

                override fun onFailure(call: Call<GetEventResponse>, t: Throwable) {
                    Log.i("MyTag","getEvent")
                    verifyError(t)
                }

            }
        )
    }

    fun sendMessage(request: SendMessageRequest, token:String){

        service.sendMessage(token, request).enqueue(
            object : Callback<SendMessageResponse> {

                override fun onResponse(call: Call<SendMessageResponse>, response: Response<SendMessageResponse>) {
                    try {
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }

                }

                override fun onFailure(call: Call<SendMessageResponse>, t: Throwable) {
                    Log.i("MyTag","sendMessage")
                    verifyError(t)
                }

            }
        )
    }

    fun vote(request: VoteRequest, token:String){

        service.vote(token, request).enqueue(
            object : Callback<VoteResponse> {

                override fun onResponse(call: Call<VoteResponse>, response: Response<VoteResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<VoteResponse>, t: Throwable) {
                    Log.i("MyTag","vote")
                    verifyError(t)
                }

            }
        )
    }

    fun getActivities(request: ActivitiesRequest, token:String){

        service.getActivities(token, request).enqueue(
            object : Callback<ActivitiesResponse> {

                override fun onResponse(call: Call<ActivitiesResponse>, response: Response<ActivitiesResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<ActivitiesResponse>, t: Throwable) {
                    System.err.println("on getActivities")
                    verifyError(t)
                }

            }
        )
    }

    fun updateEvents(request: UpdateEventsRequest, token:String){
        service.updateEvents(token, request).enqueue(
            object : Callback<UpdateEventsResponse> {

                override fun onResponse(call: Call<UpdateEventsResponse>, response: Response<UpdateEventsResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<UpdateEventsResponse>, t: Throwable) {
                    Log.i("MyTag","updateEvents")
                    verifyError(t)
                }
            }
        )
    }

    fun updateMessages(request: UpdateMessagesRequest, token:String){
        service.updateMessages(token, request).enqueue(
            object : Callback<UpdateMessagesResponse> {

                override fun onResponse(call: Call<UpdateMessagesResponse>, response: Response<UpdateMessagesResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }

                }

                override fun onFailure(call: Call<UpdateMessagesResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun getSettings(request: GetSettingsRequest, token:String){
        service.getSettings(token, request).enqueue(
            object : Callback<GetSettingsResponse> {

                override fun onResponse(call: Call<GetSettingsResponse>, response: Response<GetSettingsResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<GetSettingsResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun editSettings(request: EditSettingsRequest, token:String){
        service.editSettings(token, request).enqueue(
            object : Callback<EditSettingsResponse> {

                override fun onResponse(call: Call<EditSettingsResponse>, response: Response<EditSettingsResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<EditSettingsResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun removeMessage(request: RemoveMessageRequest, token:String){
        service.removeMessage(token, request).enqueue(
            object : Callback<RemoveMessageResponse> {

                override fun onResponse(call: Call<RemoveMessageResponse>, response: Response<RemoveMessageResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<RemoveMessageResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun editMessage(request: EditMessageRequest, token:String){
        service.editMessage(token, request).enqueue(
            object : Callback<EditMessageResponse> {

                override fun onResponse(call: Call<EditMessageResponse>, response: Response<EditMessageResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<EditMessageResponse>, t: Throwable) {
                    verifyError(t)
                }
            }
        )
    }

    fun removeAlienMessage(request: RemoveAlienMessageRequest, token:String){
        service.removeAlienMessage(token, request).enqueue(
            object : Callback<RemoveMessageResponse> {

                override fun onResponse(call: Call<RemoveMessageResponse>, response: Response<RemoveMessageResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<RemoveMessageResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun getInformation(request: GetInformationRequest, token:String){
        service.getInformation(token, request).enqueue(
            object : Callback<GetInformationResponse> {

                override fun onResponse(call: Call<GetInformationResponse>, response: Response<GetInformationResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<GetInformationResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    fun sendInformation(request: SendInformationRequest, token:String){
        service.sendInformation(token, request).enqueue(
            object : Callback<SendInformationResponse> {

                override fun onResponse(call: Call<SendInformationResponse>, response: Response<SendInformationResponse>) {
                    try{
                        val res = response.body()!!
                        when {
                            res.result=="empty" ->{}
                            res.result=="ok" -> ReactiveSubject.next(res)//отправили ответ
                            res.result=="error" -> baseError(res)
                            else -> unknownError()
                        }
                    }catch (e: NullPointerException){
                        verifyError(e)
                    }
                }

                override fun onFailure(call: Call<SendInformationResponse>, t: Throwable) {
                    verifyError(t)
                }

            }
        )
    }

    private fun baseError(response: BaseResponse){
        Log.e("MyTag","error type=${response.type}")
        System.err.println("error type=${response.type}")
        val action = ActionData(ActionData.ACTION_ERROR)
        action.data[ActionData.ITEM_TYPE] = response.type!!
        ReactiveSubject.next(action)
    }

    private fun verifyError(t: Throwable){
        Log.e("MyTag","error!",t)
        when (t) {
            is ConnectException -> networkError()
            is SocketTimeoutException -> networkError()
            is MalformedJsonException -> formatError()
            else -> unknownError()
        }
    }

    private fun formatError(){
        val action = ActionData(ActionData.ACTION_ERROR)
        action.data[ActionData.ITEM_TYPE] = "format"
        ReactiveSubject.next(action)
    }

    private fun networkError(){
        val action = ActionData(ActionData.ACTION_ERROR)
        action.data[ActionData.ITEM_TYPE] = "network"
        ReactiveSubject.next(action)
    }

    private fun unknownError(){
        Log.d("MyTag","send unknown error")
        val action = ActionData(ActionData.ACTION_ERROR)
        action.data[ActionData.ITEM_TYPE] = "unknown"
        ReactiveSubject.next(action)
    }

}