package com.tgeanezini.mobile.codehero.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tgeanezini.mobile.codehero.R
import kotlinx.android.synthetic.main.page_item_footer.view.*

class ListFooterAdapter(private val page: Int) :
    RecyclerView.Adapter<ListFooterAdapter.FooterViewHolder>() {

    var selectedPosition = RecyclerView.NO_POSITION
    var previousPosition = RecyclerView.NO_POSITION

    class FooterViewHolder(page: View) : RecyclerView.ViewHolder(page) {
        val page = page.pageLabel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        val footer = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_item_footer, parent, false)

        return FooterViewHolder(
            footer
        )
    }

    override fun getItemCount(): Int {
        return page
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) {
        holder.page.isSelected = (selectedPosition == position)
        holder.page.text = (position + 1).toString()
    }


}