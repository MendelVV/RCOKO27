package ru.mendel.apps.rcoko27.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activities_fragment.view.*
import kotlinx.android.synthetic.main.activities_message_item.view.*
import kotlinx.android.synthetic.main.activities_vote_item.view.*
import ru.mendel.apps.rcoko27.QueryPreference
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.activities.EventActivity
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.responses.ActivitiesResponse
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.data.ActivitiesData
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.MessageData
import ru.mendel.apps.rcoko27.data.VotingData
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver

class ActivitiesFragment : BaseEventFragment() {

    companion object {
        const val TYPE_VOTE = 0
        const val TYPE_MESSAGES = 1
    }


    private var mLogin: String? = null
    private var mPassword: String? = null
    private val mActivities: MutableList<ActivitiesData> = mutableListOf()
    private val mData: MutableList<Any> = mutableListOf()
    private lateinit var mAdapter: ActivityAdapter
    private var mGmt:String? = null

    private fun receiveActivities(message: BaseResponse){
        val response = message as ActivitiesResponse
        //задаем список активностей
        mGmt = response.gmt
        mActivities.clear()
        mActivities.addAll(response.activities)
        //дальше задаем список всех данных
        var iEvents = 0
        var iVoting = 0
        mData.clear()
        for (act in mActivities){
            when (act.type){
                ActivitiesData.MESSAGES -> {
                    mData.add(response.events[iEvents])
                    iEvents++
                }
                ActivitiesData.VOTE -> {
                    mData.add(response.votings[iVoting])
                    iVoting++
                }
            }
        }
        mUiHandler.post {
            if (isResumed) {
                mAdapter.notifyDataSetChanged()
            } else {
                Log.e("MyTag", "not resumed")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLogin = QueryPreference.getLogin(activity!!)
        mPassword = QueryPreference.getPassword(activity!!)
    }

    override fun onStart() {
        super.onStart()
        subscribe()
    }

    override fun onStop() {
        super.onStop()
        unsubscribe()
    }

    override fun subscribe(){
        val observerGetActivities = ResponseObserver{x->receiveActivities(x)}
        ReactiveSubject.addSubscribe(observerGetActivities, APIHelper.ACTION_GET_ACTIVITIES)
        mObservers.add(observerGetActivities)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activities_fragment,container,false)

        mAdapter = ActivityAdapter()
        view.recycler_view.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.adapter = mAdapter

        loadActivities()
        return view
    }

    private fun loadActivities(){
        //загружаем последние активности
        APIHelper.getActivities(activity!!.packageName,mLogin!!,mPassword!!)
    }

    inner class ActivityHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

        var mCurrentData: Any? = null
        var mActivitiesData: ActivitiesData? = null

        init {
            itemView.setOnClickListener {openEvent()}
        }

        private fun openEvent(){
            if (mActivitiesData!=null){
                val intent = EventActivity.newInstance(activity!!,mActivitiesData!!.event)
                startActivity(intent)
            }

        }

        fun bindMessages(activitiesData: ActivitiesData, event: EventData){
            mActivitiesData = activitiesData
            mCurrentData = event
            itemView.message_event.text = event.title
            itemView.message_info.text = getString(R.string.message_info,mActivitiesData!!.number)
            itemView.message_date.text = MessageData.convertData(mActivitiesData!!.date!!,mGmt!!)
            itemView.message_event_type.text = event.type+" ("+EventData.convertDate(event.dateevent!!)+")"
        }

        fun bindVote(activitiesData: ActivitiesData, vote: VotingData){
            mActivitiesData = activitiesData
            mCurrentData = vote
            itemView.vote_question.text = vote.text
            itemView.voting_recycler.setAnswers(vote.answers)
            itemView.voting_recycler.setOnClickListener{openEvent()}
            itemView.vote_date.text = MessageData.convertData(mActivitiesData!!.date!!,mGmt!!)
        }
    }

    inner class ActivityAdapter : RecyclerView.Adapter<ActivityHolder>(){

        override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ActivityHolder {
            val inflater = LayoutInflater.from(activity)
            val view = when (type){
                TYPE_MESSAGES -> inflater.inflate(R.layout.activities_message_item, viewGroup, false)
                TYPE_VOTE -> inflater.inflate(R.layout.activities_vote_item, viewGroup, false)
                else -> View(activity!!)//пустая вьюшка (на всякий пожарный)
            }
            return ActivityHolder(view)
        }

        override fun getItemCount(): Int {
            return mActivities.size
        }

        override fun onBindViewHolder(holder: ActivityHolder, pos: Int) {
            when (mActivities[pos].type){
                ActivitiesData.MESSAGES -> holder.bindMessages(mActivities[pos], mData[pos] as EventData)
                ActivitiesData.VOTE -> holder.bindVote(mActivities[pos], mData[pos] as VotingData)
            }
        }

        override fun getItemViewType(position: Int): Int {
            when (mActivities[position].type){
                ActivitiesData.VOTE -> return TYPE_VOTE
                ActivitiesData.MESSAGES -> return TYPE_MESSAGES
            }
            return -1
        }
    }
}