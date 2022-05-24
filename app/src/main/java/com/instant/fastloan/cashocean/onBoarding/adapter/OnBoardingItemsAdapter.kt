package com.toploans.cashOcean.onBoarding.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.toploans.cashOcean.R
import com.toploans.cashOcean.onBoarding.dataClasses.OnBoardingItemDataClass
import kotlinx.android.synthetic.main.item_onboarding.view.*

class OnBoardingItemsAdapter(
    private val onBoardingItemsList: ArrayList<OnBoardingItemDataClass>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<OnBoardingItemsAdapter.ItemsOnBoardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsOnBoardViewHolder {
        return ItemsOnBoardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return onBoardingItemsList.size
    }

    override fun onBindViewHolder(holder: ItemsOnBoardViewHolder, position: Int) {
        holder.title.text = onBoardingItemsList[position].titleItem
        holder.description.text = onBoardingItemsList[position].description
        val image = onBoardingItemsList[position].image
        holder.image.setImageResource(image)

        if (position == 2) {
            holder.buttonStart.visibility = View.VISIBLE
        }

        holder.buttonStart.setOnClickListener {
            listener.onItemClick(position)
        }


    }

    class ItemsOnBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.title
        val image: ImageView = itemView.imageView
        var description: TextView = itemView.description
        var buttonStart: AppCompatButton = itemView.button_start
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}