package com.example.mytodolist.adapter
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.databinding.WeatherLayoutBinding
import com.example.mytodolist.model.weatherList.weatherData

class WeatherListAdapter(private val context: Context,
                         private var weatherList: ArrayList<weatherData>
                       ): RecyclerView.Adapter<WeatherListAdapter.MyviewHolder>() {

    private var listposition = -1
    private var clickLisiner: onItemClickLisiner? = null

  inner class MyviewHolder(val binding: WeatherLayoutBinding) : View.OnClickListener,RecyclerView.ViewHolder(binding.root) {
       init {
           itemView.setOnClickListener(this)
       }

       override fun onClick(p0: View?) {

           val position: Int = adapterPosition
           clickLisiner?.OnClickLisiner(position)
       }
   }
    interface onItemClickLisiner {
        fun OnClickLisiner(position: Int)
    }

    fun setOnItemClick(clickLisiner: onItemClickLisiner?) {
        this.clickLisiner = clickLisiner!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {

        val binding = WeatherLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyviewHolder(binding)

    }

    fun setTaskList(list: ArrayList<weatherData>) {

        this.weatherList =list
        notifyDataSetChanged()
    }

    fun filterdList(filterList: ArrayList<weatherData>) {

        weatherList = filterList

        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = weatherList.size


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {

        val itemPosition = weatherList[position]

        holder.binding.textWetherCutyName.text =  itemPosition.name

        for (i in itemPosition.weather.toList()) {

            holder.binding.textWetherStatus.text =  i.description
        }

        //Program to convert Fahrenheit into Celsius
       val temp =(5 *(itemPosition.main.temp.toInt() -32.0)/  9.0)

       val value = temp.toString().split(".")

        holder.binding.textDegvalue.text =  value[0]

        setAnimation(holder.itemView, position)
    }



    private fun setAnimation(viewAnimition: View, position: Int) {
        if (position > listposition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            animation.duration = 1000
            viewAnimition.startAnimation(animation)
            listposition = position
        }
    }
}