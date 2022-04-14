package ru.kremlev.ncsrecognitonmanager.manager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import ru.kremlev.ncsrecognitonmanager.R
import ru.kremlev.ncsrecognitonmanager.databinding.ItemManagerRecyclerBinding


class RecyclerViewManagerAdapter(
    val context: Context
) : RecyclerView.Adapter<RecyclerViewManagerAdapter.ViewManagerHolder>() {

    class ViewManagerHolder(
        val binding: ItemManagerRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewManagerHolder {
        val binding = ItemManagerRecyclerBinding.inflate(LayoutInflater.from(parent.context))
        binding.itemManagerRecycler.setCardBackgroundColor(context.resources.getColor(R.color.alpha_100))
        binding.itemManagerRecycler.cardElevation = 0.0f

        return ViewManagerHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewManagerHolder, position: Int) {
        holder.binding.tvRaspId.text = "raspId $position"
    }

    override fun getItemCount(): Int {
        return 35
    }
}