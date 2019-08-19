package ru.mendel.apps.rcoko27.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.event_item.view.*
import kotlinx.android.synthetic.main.events_list_fragment.view.*
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.activities.EventActivity
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.BaseRequest
import ru.mendel.apps.rcoko27.api.requests.GetDataRequest
import ru.mendel.apps.rcoko27.api.requests.UpdateEventsRequest
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.api.responses.GetDataResponse
import ru.mendel.apps.rcoko27.api.responses.UpdateEventsResponse
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver
import java.lang.IndexOutOfBoundsException


class EventsListFragment : BaseEventFragment() {

    companion object {
        const val SIZE = 5
    }

    private var mToken: String? = null
    private val mList : MutableList<EventData> = mutableListOf()
    private lateinit var mAdapter : EventAdapter
    private var mIsStartLoad = false
    private var mIsEnd = false

    override fun error(message: ActionData) {
        super.error(message)
        mUiHandler.post {
            view!!.refresh_layout.isRefreshing = false
        }
    }

    private fun getEvents(message: BaseResponse){
        Log.d("MyTag", "action=${BaseRequest.ACTION_GET_EVENTS}")
        val response = message as GetDataResponse
        val start = mList.size
        mIsStartLoad = false
        if (response.data.size==0 || response.data.size<SIZE){
            if (mAdapter.itemCount>mList.size){
                mIsEnd = true
                mAdapter.notifyItemRemoved(mList.size)
            }

            return
        }
        for (event in response.data) {
            mList.add(event)
        }

        mUiHandler.post {
            if (isResumed) {
                mAdapter.notifyItemRangeInserted(start,mList.size-start)

            } else {
                Log.e("MyTag", "not resumed")
            }
        }
    }

    private fun refreshEvents(message: BaseResponse){
        Log.d("MyTag", "action=${BaseRequest.ACTION_REFRESH_EVENTS}")
        val response = message as GetDataResponse
        mList.clear()
        for (event in response.data) {
            mList.add(event)
        }
        mIsEnd=false
        mIsStartLoad = false
        mUiHandler.post {
            if (isResumed) {
                mAdapter.notifyDataSetChanged()
                view!!.refresh_layout.isRefreshing = false
            } else {
                Log.e("MyTag", "not resumed")
            }
        }
    }

    private fun updateEvents(message: BaseResponse){
        Log.d("MyTag", "action=${BaseRequest.ACTION_UPDATE_EVENTS}")
        val response = message as UpdateEventsResponse
        var last = 0
        for (event in response.events){
            //ищем каждое событие и обновляем в нем сведения
            last = findEvent(event,last)
        }
        //обновили данные
        //обновляем видимую часть событий
        val layoutManager = view!!.recycler_view.layoutManager as LinearLayoutManager?
        val visibleItemCount = layoutManager!!.childCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        var start = firstVisibleItemPosition-visibleItemCount/2
        if (start<0) start=0
        var count = 2*visibleItemCount
        if (start+count>=mList.size){
            count = mList.size-start
        }
        mUiHandler.post {
            if (isResumed) {
                mAdapter.notifyItemRangeChanged(start, count)
            } else {
                Log.e("MyTag", "not resumed")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mToken = QueryPreference.getToken(activity!!)
    }

    override fun onStart() {
        super.onStart()
        subscribe()
        updateEvents()
    }

    override fun onStop() {
        super.onStop()
        unsubscribe()
    }

    override fun subscribe(){
        val observerGetEvent = ResponseObserver{x->getEvents(x)}
        ReactiveSubject.addSubscribe(observerGetEvent, BaseRequest.ACTION_GET_EVENTS)
        mObservers.add(observerGetEvent)

        val observerRefreshEvents = ResponseObserver{x->refreshEvents(x)}
        ReactiveSubject.addSubscribe(observerRefreshEvents, BaseRequest.ACTION_REFRESH_EVENTS)
        mObservers.add(observerRefreshEvents)

        val observerUpdateEvents = ResponseObserver{x->updateEvents(x)}
        ReactiveSubject.addSubscribe(observerUpdateEvents, BaseRequest.ACTION_UPDATE_EVENTS)
        mObservers.add(observerUpdateEvents)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.events_list_fragment,container,false)

        view.refresh_layout.setOnRefreshListener { refresh() }

        mAdapter = EventAdapter()
        //view.recycler_view.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.layoutManager = WrapContentLinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.adapter = mAdapter
        view.recycler_view.addOnScrollListener(scrollListener)

        getData(0)

        return view
    }

    private fun findEvent(event: UpdateEventsResponse.EventInfo, last: Int):Int{
        for (i in last until mList.size){
            if (mList[i].code==event.code){
                mList[i].messagescount=event.messagescount
                mList[i].votingcount=event.votingcount
                return i
            }
        }
        for (i in last-1 downTo 0){
            if (mList[i].code==event.code){
                mList[i].messagescount=event.messagescount
                mList[i].votingcount=event.votingcount
                return i
            }
        }
        Log.d("MyTag","no find ${event.code}")
        return 0
    }

    private fun updateEvents(){
        if (mList.size<=0) return

        APIHelper.updateEvents(appname = activity!!.packageName,
            token = mToken!!,
            start = mList.first().dateevent!!,
            end = mList.last().dateevent!!)
    }

    private fun refresh(){
        //собираем сообщение для обновления данных
        mIsStartLoad = true

        APIHelper.refreshEvents(appname = activity!!.packageName,
            token = mToken!!,
            start = 0,
            size = SIZE)
    }

    private fun getData(start: Int){
        if (mIsStartLoad)return
        mIsStartLoad = true

        APIHelper.getEvents(appname = activity!!.packageName,
            token = mToken!!,
            start = start,
            size = SIZE)

    }

    inner class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private var mEvent: EventData? = null
        private val mTitle = itemView.text_title
        private val mInfo = itemView.text_info
        private val mType = itemView.event_type
        private val mDate = itemView.event_news_date
        private val mEventMessages = itemView.event_messages
        private val mEventVoting = itemView.event_voitings
        private val mEventImage = itemView.event_image

        init {
            itemView.setOnClickListener {
                if (mEvent!=null){
                    val intent = EventActivity.newInstance(activity!!,mEvent!!.code)
                    startActivity(intent)
                }

            }
        }

        fun bindData(event: EventData){
            mEvent = event
            mTitle.text = event.title
            mInfo.text = event.text
            mType.text = event.type+" ("+EventData.convertDate(event.dateevent!!)+")"
            mDate.text = EventData.convertDate(event.datenews!!)
            mEventVoting.text = event.votingcount.toString()
            mEventMessages.text = event.messagescount.toString()

            val nm = event.getImageUrl()
            if (nm!=null){
                Picasso.get()
                    .load(nm)
                    .resize(300,300)
                    .centerCrop()
                    .error(R.drawable.rcoko27)
                    .into(mEventImage)
            }else{
                Picasso.get()
                    .load(R.drawable.rcoko27)
                    .resize(300,300)
                    .centerCrop()
                    .into(mEventImage)
            }
        }
    }

    inner class EventAdapter : RecyclerView.Adapter<EventHolder>(){

        override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): EventHolder {
            if (type==0){
                val inflater = LayoutInflater.from(activity)
                val view = inflater.inflate(R.layout.event_item, viewGroup, false)
                return EventHolder(view)
            }else{
                val inflater = LayoutInflater.from(activity)
                val view = inflater.inflate(R.layout.load_item, viewGroup, false)
                return EventHolder(view)
            }
        }

        override fun getItemCount(): Int {
            return if (mList.size>0){
                if (mIsEnd){
                    mList.size
                }else{
                    mList.size+1
                }
            }else{
                0
            }
        }

        override fun onBindViewHolder(holder: EventHolder, pos: Int) {
            if (pos<mList.size){
                holder.bindData(mList[pos])
                if (pos==mList.size-1){
//                    getData(mList.size)
                }
            }else{
                //если открываем обновляшку
//                getData(mList.size)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (position==mList.size){
                1
            }else{
                0
            }
//            return 0
        }



    }

    private val scrollListener= object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
            val visibleItemCount = layoutManager!!.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                getData(mList.size)
            }

        }
    }

    inner class WrapContentLinearLayoutManager(context: Context, orientation: Int, reverse: Boolean) :
        LinearLayoutManager(context,orientation,reverse) {

        override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("MyTag", "IndexOutOfBoundsException in RecyclerView happens")
            }

        }
    }

}