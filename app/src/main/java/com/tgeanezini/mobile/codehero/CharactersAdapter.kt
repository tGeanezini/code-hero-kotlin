package com.tgeanezini.mobile.codehero

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.list_item_character.view.*

class CharactersAdapter (private val characters: List<Character>, var context: Context) :
    RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {

    class CharactersViewHolder(characterView: View) : RecyclerView.ViewHolder(characterView) {
        val name = characterView.characterName
        val image = characterView.characterImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        val characterView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_character, parent, false)

        return CharactersViewHolder(characterView)
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val character = characters[position]
        val imagePath = "${character.thumbnail.path}/standard_medium.${character.thumbnail.extension}"

        Glide.with(context)
            .load(imagePath)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.image)

        holder.name.text = character.name
    }
}