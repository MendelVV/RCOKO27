package ru.mendel.apps.rcoko27.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.event_voting_fragment.view.*
import kotlinx.android.synthetic.main.voting_item.view.*
import ru.mendel.apps.rcoko27.QueryPreference
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.BaseRequest
import ru.mendel.apps.rcoko27.api.requests.VoteRequest
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.api.responses.VoteResponse
import ru.mendel.apps.rcoko27.data.VotingData
import ru.mendel.apps.rcoko27.database.RcokoDatabase
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver

class EventVotingFragment : BaseEventFragment(){

    companion object {
        const val CODE = "code"

        fun newInstance(code: Int) : EventVotingFragment{
            val fragment = EventVotingFragment()
            val args = Bundle()
            args.putInt(CODE, code)
            fragment.arguments = args
            return fragment
        }
    }

    private var mToken: String? = null
    private var mVoting : MutableList<VotingData> = mutableListOf()
    private var mEventCode = -1
    private lateinit var mAdapter : VotingAdapter

    private fun actionVote(message: BaseResponse){
        val response = message as VoteResponse
        //ищем голосование в списке
        val pos = findVote(response.voting)
        if (pos==-1) return
        val votingData = mVoting[pos]
        votingData.answers = response.answers

        mUiHandler.post { mAdapter.notifyItemChanged(pos) }
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
        val observerVote = ResponseObserver{x->actionVote(x)}
        ReactiveSubject.addSubscribe(observerVote, BaseRequest.ACTION_VOTE)
        mObservers.add(observerVote)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEventCode = arguments!!.getInt(CODE)
        RcokoDatabase(activity!!)
        mVoting = RcokoDatabase.getVoting(mEventCode)
        mToken = QueryPreference.getToken(activity!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_voting_fragment,container,false)

        mAdapter = VotingAdapter()
        view.recycler_view.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.adapter = mAdapter
        if (mVoting.size==0){
            view.text_none_voting.visibility = View.VISIBLE
        }
        return view
    }

    private fun findVote(votingCode: Int):Int{
        var i = 0
        for (vote in mVoting){
            if (vote.code==votingCode){
                return i
            }
            i++
        }
        return -1
    }

    inner class VotingHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private lateinit var mVotingData: VotingData

        fun bind(voting: VotingData){
            mVotingData = voting
            Log.d("MyTag","total answers="+mVotingData.totalAnswers())
            itemView.voting_text.text = mVotingData.text
            if (mVotingData.totalAnswers()>0){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.voting_text.setCompoundDrawablesWithIntrinsicBounds(null,null, resources.getDrawable( R.drawable.ic_voted,null),null)
                }else{
                    itemView.voting_text.setCompoundDrawablesWithIntrinsicBounds(null,null, resources.getDrawable( R.drawable.ic_voted),null)
                }
                itemView.voting_recycler.setAnswers(mVotingData.answers)
                itemView.voting_button.visibility=View.GONE
            }else{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.voting_text.setCompoundDrawablesWithIntrinsicBounds(null,null, resources.getDrawable( R.drawable.ic_event_voitings,null),null)
                }else{
                    itemView.voting_text.setCompoundDrawablesWithIntrinsicBounds(null,null, resources.getDrawable( R.drawable.ic_event_voitings),null)
                }

                itemView.voting_recycler.setPossibles(mVotingData.possibles)
                itemView.voting_button.visibility=View.VISIBLE
            }

            itemView.voting_button.setOnClickListener { vote() }
        }

        private fun vote(){

            val current = itemView.voting_recycler.current
            if(current==-1) return

            APIHelper.vote(appname = activity!!.packageName,
                token = mToken!!,
                voting = mVotingData.code,
                answer = mVotingData.possibles[current].code)
        }
    }

    inner class VotingAdapter : RecyclerView.Adapter<VotingHolder>(){
        override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): VotingHolder {
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.voting_item, viewGroup, false)
            return VotingHolder(view)
        }

        override fun getItemCount(): Int {
            return mVoting.size
        }

        override fun onBindViewHolder(holder: VotingHolder, pos: Int) {
            holder.bind(mVoting[pos])
        }

    }
}