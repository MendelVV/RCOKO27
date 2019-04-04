package ru.mendel.apps.rcoko27.fragments

import android.content.Context
import android.os.*
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.event_messages_fragment.view.*
import kotlinx.android.synthetic.main.event_on_message_item.view.*
import kotlinx.android.synthetic.main.message_external_item.view.*
import kotlinx.android.synthetic.main.message_item.view.*
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.SendMessageRequest
import ru.mendel.apps.rcoko27.api.requests.UpdateMessagesRequest
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.api.responses.SendMessageResponse
import ru.mendel.apps.rcoko27.api.responses.UpdateMessagesResponse
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.MessageData
import ru.mendel.apps.rcoko27.database.RcokoDatabase
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver
import java.util.*
import kotlin.collections.ArrayList

class EventMessagesFragment : BaseEventFragment() {

    companion object {
        const val CODE = "code"

        fun newInstance(code: Int):EventMessagesFragment{
            val fragment = EventMessagesFragment()
            val args = Bundle()
            args.putInt(CODE,code)
            fragment.arguments = args
            return fragment
        }
    }

    private var mMessages: MutableList<MessageData> = mutableListOf()
    private var mEvent: EventData? = null
    private var mAdapter : MessageAdapter? = null
    private var mLogin: String? = null
    private var mPassword: String? = null
    private lateinit var mHandler: UpdateMessageHandler

    private fun actionSendMessage(message: BaseResponse){
        val response = message as SendMessageResponse
        val messageData = findMessage(response.uuid!!)
        if (messageData!=null){
            messageData.state = MessageData.STATE_DELIVERED
            messageData.date = response.date
            RcokoDatabase(activity!!)
            RcokoDatabase.updateMessage(messageData)
            val n = mMessages.indexOf(messageData)+1
            Log.d("MyTag","update item $n")
            mUiHandler.post { mAdapter!!.notifyItemChanged(n) }
        }else{
            Log.e("MyTag","not find message")
        }
    }

    private fun actionUpdateMessage(message: BaseResponse){
        val response = message as UpdateMessagesResponse
        //теперь можно пройтись по всем сообщениям, если что-то нужно обновить то обновляем и добавляем новые
        join(response.messages)
    }

    private fun join(messages: ArrayList<MessageData>){
        var i = 0
        for (message in messages){
            if (i<mMessages.size){
                var find = false
                for (j in i until mMessages.size){
                    if (message.uuid==mMessages[j].uuid){
                        find = true
                        i=j
//                        Log.d("MyTag","find ${message.text} i=$j")
                        if (mMessages[j].state!=MessageData.STATE_DELIVERED){
                            mMessages[j].state=MessageData.STATE_DELIVERED
                            mUiHandler.post { mAdapter!!.notifyItemChanged(i) }
                        }
                        break
                    }
                }
                if (!find){
                    //не нашли, добавляем после последнего найденного
                    mMessages.add(i+1,message)
                    mUiHandler.post { mAdapter!!.notifyItemInserted(i+1) }
                    i++
                }
            }else{
                //просто добаляем в конец
                i++
                mMessages.add(message)
                mUiHandler.post { mAdapter!!.notifyItemInserted(i) }
            }
        }
        mUiHandler.post { view!!.recycler_view.scrollToPosition(mMessages.size) }
    }

    private fun findMessage(uuid: String):MessageData?{
        val n = mMessages.size
        for (i in n-1 downTo 0){
            if (mMessages[i].uuid==uuid){
                return mMessages[i]
            }
        }
        return null
    }

    override fun onStart() {
        super.onStart()
        subscribe()

        val ht = HandlerThread("UpdateMessagesThread")
        ht.start()
        mHandler = UpdateMessageHandler(ht.looper)
        updateMessages()
    }

    override fun onStop() {
        super.onStop()
        unsubscribe()
        mHandler.looper.quit()
    }

    override fun subscribe() {
        val observerSendMessage = ResponseObserver{x->actionSendMessage(x)}
        ReactiveSubject.addSubscribe(observerSendMessage, APIHelper.ACTION_SEND_MESSAGE)
        mObservers.add(observerSendMessage)

        val observerUpdateMessage = ResponseObserver{x->actionUpdateMessage(x)}
        ReactiveSubject.addSubscribe(observerUpdateMessage, APIHelper.ACTION_UPDATE_MESSAGES)
        mObservers.add(observerUpdateMessage)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments!!.getInt(CODE)
        RcokoDatabase(activity!!)
        mEvent = RcokoDatabase.getEvent(code)
        mMessages = RcokoDatabase.getMessages(code)

        mLogin = QueryPreference.getLogin(activity!!)
        mPassword = QueryPreference.getPassword(activity!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_messages_fragment,container,false)

        view.send_button.setOnClickListener { sendMessage() }
        mAdapter = MessageAdapter()
//        view.recycler_view.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.layoutManager = WrapContentLinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        view.recycler_view.adapter = mAdapter
        view.recycler_view.scrollToPosition(mAdapter!!.itemCount-1)


        return view
    }

    private fun updateMessages(){
        try{
            APIHelper.updateMessages(appname = activity!!.packageName,
                email = mLogin!!,
                password = mPassword!!,
                event = mEvent!!.code)

            val msg = Message()
            msg.arg1=0
            mHandler.sendMessage(msg)
        }catch (e: KotlinNullPointerException){
            //просто вышли из активити, и ладно
        }

    }

    private fun sendMessage(){
        val text = view!!.message_text.text
        if (text.isNotEmpty()){

            val messageData = MessageData()
            messageData.author = mLogin
            messageData.recipient = ""
            messageData.text = text.toString()
            messageData.event = mEvent!!.code
            messageData.uuid = UUID.randomUUID().toString()
            messageData.state = MessageData.STATE_SEND
            messageData.date = "none"

            mMessages.add(messageData)
            mAdapter!!.notifyItemInserted(mMessages.size)
            view!!.recycler_view.scrollToPosition(mMessages.size)

            RcokoDatabase(activity!!)
            RcokoDatabase.insertMessage(messageData)

            APIHelper.sendMessage(appname = activity!!.packageName,
                password = mPassword!!,
                author = messageData.author!!,
                recipient = messageData.recipient!!,
                text = messageData.text!!,
                event = messageData.event,
                uuid = messageData.uuid!!)

            view!!.message_text.setText("")
        }
    }

    inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private var mMessageData: MessageData? = null

        fun bindMessage(message: MessageData){
            mMessageData = message

            if (mMessageData!!.author==mLogin){
                itemView.text_message.text = mMessageData!!.text
                itemView.text_time.text = mMessageData!!.date

                if (mMessageData!!.state==MessageData.STATE_SEND){
                    itemView.image_state.setImageResource(R.drawable.ic_send_message)
                }else{
                    itemView.image_state.setImageResource(R.drawable.ic_delivered_message)
                }
            }else{
                itemView.text_external_author.text = mMessageData!!.authorname
                if (mMessageData!!.recipient==""){
                    itemView.text_external_recipient.visibility = View.GONE
                }else{
                    itemView.text_external_recipient.visibility = View.VISIBLE
                    itemView.text_external_recipient.text = getString(R.string.to,mMessageData!!.recipientname)
                }
                itemView.text_external_message.text = mMessageData!!.text
                itemView.text_external_time.text = mMessageData!!.date
            }

        }

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

    inner class MessageAdapter : RecyclerView.Adapter<MessageHolder>(){

        override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): MessageHolder {
            val inflater = LayoutInflater.from(activity)

            val view = when (type){
                0->inflater.inflate(R.layout.event_on_message_item, viewGroup, false)
                1->inflater.inflate(R.layout.message_item, viewGroup, false)
                else->inflater.inflate(R.layout.message_external_item, viewGroup, false)
            }
            return  MessageHolder(view)
        }

        override fun getItemCount(): Int {
            if (mEvent!=null){
                return mMessages.size+1
            }else{
                return mMessages.size
            }
        }

        override fun onBindViewHolder(holder: MessageHolder, pos: Int) {
            if (pos==0){
                holder.bindEvent(mEvent!!)
            }else{
                holder.bindMessage(mMessages[pos-1])
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (position){
                0-> 0
                else -> return if (mMessages[position-1].author==mLogin){
                    1
                }else{
                    2
                }
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

    inner class UpdateMessageHandler(looper: Looper):Handler(looper){
        override fun handleMessage(msg: Message?) {
            Thread.sleep(30000)//раз в 30 секунд обновляем
            updateMessages()
        }
    }

}