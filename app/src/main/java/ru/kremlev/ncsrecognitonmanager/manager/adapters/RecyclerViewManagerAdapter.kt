package ru.kremlev.ncsrecognitonmanager.manager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import ru.kremlev.ncsrecognitonmanager.R
import ru.kremlev.ncsrecognitonmanager.databinding.ItemManagerRecyclerBinding
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemType
import ru.kremlev.ncsrecognitonmanager.manager.model.Navigation


class RecyclerViewManagerAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerViewManagerAdapter.ViewManagerHolder>() {
    private var itemList = mutableListOf<RecognitionSystemData>()
    private var selectedItem: Int = -1

    fun setData(newList: ArrayList<RecognitionSystemData>) {
        val utils = ListDiffUtils(itemList, newList)
        val diffResult = DiffUtil.calculateDiff(utils)

        diffResult.dispatchUpdatesTo(this)

        itemList = newList
    }

    class ViewManagerHolder(
        val binding: ItemManagerRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewManagerHolder {
        val binding = ItemManagerRecyclerBinding.inflate(LayoutInflater.from(parent.context))
        binding.itemManagerRecycler.setCardBackgroundColor(ContextCompat.getColor(context, R.color.alpha_100))
        binding.itemManagerRecycler.cardElevation = 0.0f

        return ViewManagerHolder(binding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewManagerHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = itemList[position]
        if (position == selectedItem) {
            holder.binding.tvItemId.alpha = 1f
        } else {
            holder.binding.tvItemId.alpha = 0.5f
        }
        holder.itemView.setOnClickListener {
            selectedItem = position
            Navigation.selectedSystem.value = position
            notifyDataSetChanged()
        }
        holder.binding.tvItemId.text = current.id
        holder.binding.ivIconItem.setImageDrawable(
            when (current.type) {
                RecognitionSystemType.RASPBERRY -> context.getDrawable(R.drawable.ic_raspberrypi)
                RecognitionSystemType.X86 -> context.getDrawable(R.drawable.ic_settings)
            }
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}