package ru.mendel.apps.rcoko27.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.answer_item.view.*
import kotlinx.android.synthetic.main.possible_item.view.*
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.data.AnswerData
import ru.mendel.apps.rcoko27.data.PossibleData
import ru.mendel.apps.rcoko27.images.ImageHelper


class PossibleRecyclerView : RecyclerView {

    private var mPossibles:MutableList<PossibleData>? = mutableListOf<PossibleData>()
    private var mAnswers:MutableList<AnswerData>? = mutableListOf<AnswerData>()
    private lateinit var mAdapter: PossibleAdapter
    var current = -1

    constructor(context: Context) : super(context,null){
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs,0){
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle){
        initialize()
    }

    private fun initialize(){
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mAdapter = PossibleAdapter()
        adapter = mAdapter
    }

    fun setPossibles(possibles : MutableList<PossibleData>){
        mAnswers = null
        mPossibles = possibles
        mAdapter.notifyDataSetChanged()
//        Log.d("MyTag","setPossibles count="+mAdapter.itemCount)
    }

    fun setAnswers(answers: MutableList<AnswerData>){
        mPossibles = null
        mAnswers = answers
        mAdapter.notifyDataSetChanged()

    }

    private fun totalAnswers():Int{
        if (mAnswers==null) return 0
        var sum = 0
        for (answer in mAnswers!!){
            sum+=answer.size
        }
        return sum
    }


    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if (e!!.action==MotionEvent.ACTION_DOWN){
            callOnClick()
        }
        return false
    }

    inner class PossibleHolder(itemView: View) : ViewHolder(itemView){

        private lateinit var mPossible : PossibleData
        private lateinit var mAnswer : AnswerData

        init {
            try {
                itemView.checkbox.setOnClickListener { select() }
            }catch (e: Exception){

            }

        }

        private fun select(){
            if (mPossibles==null) return
            val thisIndex = mPossibles!!.indexOf(mPossible)
            Log.d("MyTag","select $thisIndex")
            if (thisIndex==current){
                current = -1
                mAdapter.notifyItemChanged(thisIndex)
            }else{
                val prevIndex = current
                current = thisIndex
                if (prevIndex>=0){
                    mAdapter.notifyItemChanged(prevIndex)
                }

                mAdapter.notifyItemChanged(thisIndex)
            }
        }

        fun bind(possibleData: PossibleData){
            mPossible = possibleData
            val thisIndex = mPossibles!!.indexOf(mPossible)
            itemView.checkbox.text = mPossible.text
            itemView.checkbox.isChecked = (thisIndex==current)
        }

        fun bind(answerData: AnswerData){
            mAnswer = answerData
            itemView.text_answer.text = mAnswer.text
            itemView.image_view.setImageBitmap(ImageHelper.answerBitmap(mAnswer.size,totalAnswers()))
        }

    }

    inner class PossibleAdapter : Adapter<PossibleHolder>(){

        override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): PossibleHolder {
            val inflate = LayoutInflater.from(context)

            val view = if(type==0){
                inflate.inflate(R.layout.possible_item,viewGroup,false)
            }else{
                inflate.inflate(R.layout.answer_item,viewGroup,false)
            }

            return PossibleHolder(view)
        }

        override fun getItemCount(): Int {
            if (mPossibles!=null){
                return mPossibles!!.size
            }else if(mAnswers!=null){
                return mAnswers!!.size
            }else{
                return 0
            }

        }

        override fun onBindViewHolder(holder: PossibleHolder, pos: Int) {
            if (mPossibles!=null){
                holder.bind(mPossibles!![pos])
            }else if(mAnswers!=null){
                holder.bind(mAnswers!![pos])
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (mPossibles!=null){
                return 0
            }else if(mAnswers!=null){
                return 1
            }
            return 0
        }
    }

}