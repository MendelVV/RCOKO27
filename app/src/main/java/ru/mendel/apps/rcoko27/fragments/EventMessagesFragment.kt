package ru.mendel.apps.rcoko27.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.*
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
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
import ru.mendel.apps.rcoko27.api.requests.BaseRequest
import ru.mendel.apps.rcoko27.api.requests.SendMessageRequest
import ru.mendel.apps.rcoko27.api.requests.UpdateMessagesRequest
import ru.mendel.apps.rcoko27.api.responses.*
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.MessageData
import ru.mendel.apps.rcoko27.database.RcokoDatabase
import ru.mendel.apps.rcoko27.dialogs.EditMessageDialog
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
    private var mToken: String? = null
    private var mLogin: String? = null
    private lateinit var mHandler: UpdateMessageHandler

    private fun actionSendMessage(message: BaseResponse){
        val response = message as SendMessageResponse
        val messageData = findMessage(response.uuid!!)
        if (messageData!=null){
            messageData.state = MessageData.STATE_DELIVERED
            messageData.date = response.date
            messageData.gmt = response.gmt
            RcokoDatabase(activity!!)
            RcokoDatabase.updateMessage(messageData)
            val n = mMessages.indexOf(messageData)+1
            Log.d("MyTag","update item $n")
            mUiHandler.post { mAdapter!!.notifyItemChanged(n) }
        }else{
            Log.e("MyTag","not find message")
        }
    }

    private fun actionRemoveMessage(message: BaseResponse){
        val response = message as RemoveMessageResponse
        //берем теперь id сообщения которое нужно удалить
        val messageData = findMessage(response.uuid!!)
        val pos = mMessages.indexOf(messageData)
        mMessages.removeAt(pos)
        mUiHandler.post { mAdapter!!.notifyItemRemoved(pos+1) }
    }

    private fun actionEditMessage(message: BaseResponse){
        val response = message as EditMessageResponse
        val messageData = findMessage(response.uuid!!)
        val pos = mMessages.indexOf(messageData)
        messageData!!.text=response.text
        Log.d("MyTag","update pos=$pos text=${response.text}")
        mUiHandler.post { mAdapter!!.notifyItemChanged(pos+1) }
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
        ReactiveSubject.addSubscribe(observerSendMessage, BaseRequest.ACTION_SEND_MESSAGE)
        mObservers.add(observerSendMessage)

        val observerUpdateMessage = ResponseObserver{x->actionUpdateMessage(x)}
        ReactiveSubject.addSubscribe(observerUpdateMessage, BaseRequest.ACTION_UPDATE_MESSAGES)
        mObservers.add(observerUpdateMessage)

        val observerRemoveMessage = ResponseObserver{x->actionRemoveMessage(x)}
        ReactiveSubject.addSubscribe(observerRemoveMessage, BaseRequest.ACTION_REMOVE_MESSAGE)
        mObservers.add(observerRemoveMessage)

        val observerEditMessage = ResponseObserver{x->actionEditMessage(x)}
        ReactiveSubject.addSubscribe(observerEditMessage, BaseRequest.ACTION_EDIT_MESSAGE)
        mObservers.add(observerEditMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments!!.getInt(CODE)
        RcokoDatabase(activity!!)
        mEvent = RcokoDatabase.getEvent(code)
        mMessages = RcokoDatabase.getMessages(code)

        mToken = QueryPreference.getToken(activity!!)
        mLogin = QueryPreference.getLogin(activity!!)

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
                token = mToken!!,
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
            messageData.parentmessage = -1//пока нет возможности написать ответ
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
                token = mToken!!,
                parentmessage = messageData.parentmessage,
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
                //свое сообщение
                itemView.setOnLongClickListener {
                    showMenu()
                    return@setOnLongClickListener true
                }
            }else{
                itemView.text_external_author.text = mMessageData!!.authorname

                itemView.text_external_recipient.visibility = View.GONE

                itemView.text_external_message.text = mMessageData!!.text
                itemView.text_external_time.text = mMessageData!!.date

                if (mMessageData!!.verification==1){
                    itemView.view_external_verification.visibility=View.VISIBLE
                }else{
                    itemView.view_external_verification.visibility=View.GONE
                }

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

        private fun showMenu(){
            //показываем меню в зависимости от того какой пользователь
            val ver = QueryPreference.getVerification(activity!!)
            val arr = if (ver==0){
                R.array.actions_delete
            }else{
                R.array.actions_delete_edit
            }
            val ad = AlertDialog.Builder(activity!!)
            ad.setItems(arr) { d: DialogInterface, n:Int ->
                if (ver==0){
                    when(n){
                        0->removeMessage()
                    }
                }else{
                    when(n){
                        0->editMessage()
                        1->removeMessage()
                    }
                }
                d.dismiss()}
            ad.show()
        }

        private fun removeMessage(){
            //собираем сообщение на удаление сообщения

            APIHelper.removeMessage(appname = activity!!.packageName,
                token = QueryPreference.getToken(activity!!)!!,
                uuid = mMessageData!!.uuid.toString()!!)
        }

        private fun editMessage(){
            //показываем диалог редактирования сообщения
            val dialog = EditMessageDialog.newInstance(mMessageData!!.uuid!!, mMessageData!!.text!!)
//            dialog.setTargetFragment(this@EncryptListFragment, REQUEST_ADD_EMPTY)
            dialog.show(fragmentManager,"edit message")
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