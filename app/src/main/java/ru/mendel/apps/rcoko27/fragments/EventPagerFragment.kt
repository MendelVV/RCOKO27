package ru.mendel.apps.rcoko27.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.event_pager_fragment.view.*
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.BaseRequest
import ru.mendel.apps.rcoko27.api.requests.GetEventRequest
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.api.responses.GetEventResponse
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.database.RcokoDatabase
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver

class EventPagerFragment : BaseEventFragment() {

    companion object {

        const val CODE = "code"

        fun newInstance(code: Int):EventPagerFragment{
            val fragment = EventPagerFragment()
            val args = Bundle()
            args.putInt(CODE, code)
            fragment.arguments = args
            return fragment
        }

    }

    private var mToken: String? = null
    private lateinit var mPagerAdapter: PagerAdapter
    private var mEventCode : Int = -1
    private var mEvent: EventData?=null


    private fun getEvent(message: BaseResponse) {
        val response = message as GetEventResponse
        mEvent = response.event
        RcokoDatabase(activity!!)
        RcokoDatabase.setEvent(mEvent!!)
        RcokoDatabase.clearVoting(mEventCode)
        for (votingData in response.voting){
            RcokoDatabase.insertVoting(votingData)
        }
        RcokoDatabase.clearMessages(mEventCode)
        for (messageData in response.messages){
            RcokoDatabase.insertMessage(messageData)
        }
        mUiHandler.post { updatePages() }

    }

    private fun updatePages(){
        view!!.refresh_layout.isRefreshing = false
        if (isAdded){
            mPagerAdapter.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        subscribe()
    }

    override fun onStop() {
        super.onStop()
        unsubscribe()
    }

    override fun subscribe() {
        val observerGetEvent = ResponseObserver{ x->getEvent(x)}
        ReactiveSubject.addSubscribe(observerGetEvent, BaseRequest.ACTION_GET_EVENT)
        mObservers.add(observerGetEvent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEventCode = arguments!!.getInt(CODE)

        mToken = QueryPreference.getToken(activity!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_pager_fragment,container, false)
        mPagerAdapter = PagerAdapter(activity!!.supportFragmentManager)
        view.view_pager.adapter=mPagerAdapter
        view.sliding_tabs.setupWithViewPager(view.view_pager)
        //начинаем загружать данные

        view.refresh_layout.setOnRefreshListener { refresh() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadEvent()
    }

    private fun refresh(){
        loadEvent()
    }

    private fun loadEvent(){
        APIHelper.getEvent(appname = activity!!.packageName,
            token = mToken!!,
            code = mEventCode)
    }

    inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm){

        override fun getItem(pos: Int): Fragment {
            if (mEvent!=null){
                return when (pos){
                    0-> EventMainFragment.newInstance(mEventCode)
                    1-> EventMessagesFragment.newInstance(mEventCode)
                    2-> EventVotingFragment.newInstance(mEventCode)
                    else -> Fragment()
                }
            }else{
                return Fragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(pos: Int): CharSequence? {
            return when (pos){
                0-> getString(R.string.event)
                1-> getString(R.string.discussion)
                2-> getString(R.string.voting)
                else -> ""
            }
        }

        override fun getItemPosition(`object`: Any): Int {
            return android.support.v4.view.PagerAdapter.POSITION_NONE
        }
    }
}