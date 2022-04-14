package ru.kremlev.ncsrecognitonmanager.manager.adapters

import androidx.recyclerview.widget.DiffUtil

import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData

class ListDiffUtils(
    private val oldList: MutableList<RecognitionSystemData>,
    private val newList: MutableList<RecognitionSystemData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].type == (newList[newItemPosition].type))
    }
}