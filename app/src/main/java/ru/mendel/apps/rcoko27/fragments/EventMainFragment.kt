package ru.mendel.apps.rcoko27.fragments

import android.content.Context
import android.os.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.event_messages_fragment.view.*
import kotlinx.android.synthetic.main.event_on_message_item.view.*
import kotlinx.android.synthetic.main.information_item.view.*
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.requests.BaseRequest
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.api.responses.GetInformationResponse
import ru.mendel.apps.rcoko27.api.responses.SendInformationResponse
import ru.mendel.apps.rcoko27.data.DataValidator
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.InformationData
import ru.mendel.apps.rcoko27.data.MessageData
import ru.mendel.apps.rcoko27.database.RcokoDatabase
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver

class EventMainFragment : BaseEventFragment() {

    companion object {
        const val CODE = "code"

        fun newInstance(code: Int):EventMainFragment{
            val fragment = EventMainFragment()
            val args = Bundle()
            args.putInt(CODE,code)
            fragment.arguments = args
            return fragment
        }
    }

    private var mEvent: EventData? = null
    private var mAdapter : MessageAdapter? = null
    private var mToken: String? = null
    private var mLogin: String? = null
    private var mInformation = arrayListOf<InformationData>()

    private fun actionGetInformation(message: BaseResponse){
        val response = message as GetInformationResponse
        mInformation.clear()
        mInformation.addAll(response.information)
        mUiHandler.post{mAdapter!!.notifyDataSetChanged()}
    }

    private fun actionSendInformation(message: BaseResponse){
        val response = message as SendInformationResponse
        val information = InformationData()
        information.code = response.code
        information.event = mEvent!!.code
        information.text = response.text
        information.date = response.date
        information.gmt = response.gmt
        mInformation.add(information)
        mUiHandler.post { mAdapter!!.notifyItemInserted(mInformation.size-1) }
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
        val observerGetInformation = ResponseObserver{x -> actionGetInformation(x)}
        ReactiveSubject.addSubscribe(observerGetInformation, BaseRequest.ACTION_GET_INFORMATION)
        mObservers.add(observerGetInformation)

        val observerSendInformation = ResponseObserver{x -> actionSendInformation(x)}
        ReactiveSubject.addSubscribe(observerSendInformation, BaseRequest.ACTION_SEND_INFORMATION)
        mObservers.add(observerSendInformation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments!!.getInt(CODE)
        RcokoDatabase(activity!!)
        mEvent = RcokoDatabase.getEvent(code)

        mToken = QueryPreference.getToken(activity!!)
        mLogin = QueryPreference.getLogin(activity!!)

        //запускаем поиск объявлений
        getInformation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_messages_fragment,container,false)

        mAdapter = MessageAdapter()
        view.recycler_view.layoutManager = WrapContentLinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.adapter = mAdapter
        view.recycler_view.scrollToPosition(mAdapter!!.itemCount-1)

        val layout = view.findViewById<LinearLayout>(R.id.bottom_layout)
        if (QueryPreference.getVerification(activity!!)==1){
            layout.visibility = View.VISIBLE
            view.send_button.setOnClickListener { sendInformation() }
        }else{
            layout.visibility = View.GONE
        }

        return view
    }

    private fun getInformation(){
        APIHelper.getInformation(appname = activity!!.packageName,
            token = QueryPreference.getToken(activity!!)!!,
            event = mEvent!!.code)
    }

    private fun sendInformation(){
        val text = view!!.message_text.text.toString()
        if (DataValidator.isTrivialString(text)){
            //если это тривиальная строка, то ничего не делаем
            return
        }
        view!!.message_text.setText("")
        APIHelper.sendInformation(appname = activity!!.packageName,
            token = QueryPreference.getToken(activity!!)!!,
            event = mEvent!!.code,
            text = text)
    }

    inner class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private var mInfo: InformationData? = null

        fun bindEvent(event: EventData){
            itemView.event_type.text = event.type+" ("+EventData.convertDate(event.dateevent!!)+")"
            itemView.event_title.text = event.title
            itemView.event_text.text = event.text
            itemView.event_news_date.text = EventData.convertDate(event.datenews!!)

            val nm = event.getImageUrl()
            if (nm!=null){
                Picasso.get()
                    .load(nm)
                    .resize(300,300)
                    .centerCrop()
                    .error(R.drawable.rcoko27)
                    .into(itemView.event_image)
            }
        }

        fun bindInformation(info: InformationData){
            mInfo = info
            itemView.text_information.text = mInfo!!.text
            itemView.text_information_time.text = MessageData.convertDate(mInfo!!.date!!, mInfo!!.gmt!!)
        }

    }

    inner class MessageAdapter : RecyclerView.Adapter<EventHolder>(){

        override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): EventHolder {
            val inflater = LayoutInflater.from(activity)

            val view = when (type){
                0->inflater.inflate(R.layout.event_on_message_item, viewGroup, false)
                1->inflater.inflate(R.layout.information_item, viewGroup, false)
                else->View(activity!!)
            }
            return  EventHolder(view)
        }

        override fun getItemCount(): Int {
            return mInformation.size+1//пока только само событие
        }

        override fun onBindViewHolder(holder: EventHolder, pos: Int) {
            if (pos==0){
                holder.bindEvent(mEvent!!)
            }else{
                holder.bindInformation(mInformation[pos-1])
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (position==0){
                0
            }else{
                1
            }

        }
    }

    inner class WrapContentLinearLayoutManager(context: Context, orientation: Int, reverse: Boolean) :
        LinearLayoutManager(context,orientation,reverse) {

        override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens")
            }
        }
    }

}