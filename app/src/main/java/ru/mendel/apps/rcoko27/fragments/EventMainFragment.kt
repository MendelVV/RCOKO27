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
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.MessageData
import ru.mendel.apps.rcoko27.database.RcokoDatabase

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

    override fun onStart() {
        super.onStart()
        subscribe()
    }

    override fun onStop() {
        super.onStop()
        unsubscribe()
    }

    override fun subscribe() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments!!.getInt(CODE)
        RcokoDatabase(activity!!)
        mEvent = RcokoDatabase.getEvent(code)

        mToken = QueryPreference.getToken(activity!!)
        mLogin = QueryPreference.getLogin(activity!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_messages_fragment,container,false)

        mAdapter = MessageAdapter()
//        view.recycler_view.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.layoutManager = WrapContentLinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.adapter = mAdapter
        view.recycler_view.scrollToPosition(mAdapter!!.itemCount-1)

        val layout = view.findViewById<LinearLayout>(R.id.bottom_layout)
        layout.visibility = View.GONE

        return view
    }

    inner class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

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

    }

    inner class MessageAdapter : RecyclerView.Adapter<EventHolder>(){

        override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): EventHolder {
            val inflater = LayoutInflater.from(activity)

            val view = when (type){
                0->inflater.inflate(R.layout.event_on_message_item, viewGroup, false)
                else->View(activity!!)
            }
            return  EventHolder(view)
        }

        override fun getItemCount(): Int {
            return 1//пока только само событие
        }

        override fun onBindViewHolder(holder: EventHolder, pos: Int) {
            if (pos==0){
                holder.bindEvent(mEvent!!)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return 0
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