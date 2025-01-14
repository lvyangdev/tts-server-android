package com.github.jing332.tts_server_android.ui.view.widget.spinner

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import com.github.jing332.tts_server_android.databinding.ItemMaterialSpinnerBinding

data class SpinnerItem(
    var displayText: String,
    var value: Any? = null,
    @DrawableRes var imageResId: Int = -1
) {
    override fun toString() = displayText
}

class MaterialSpinnerAdapter(val content: Context, items: List<SpinnerItem>) :
    BaseMaterialSpinnerAdapter<SpinnerItem>(content, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View

        val binding: ItemMaterialSpinnerBinding
        if (convertView == null) { // 新建
            binding = ItemMaterialSpinnerBinding.inflate(layoutInflater, parent, false)
            view = binding.root
            view.tag = binding
        } else { // 复用
            view = convertView
            binding = view.tag as ItemMaterialSpinnerBinding
        }

        val item = getItem(position)
        val isSelected = position == selectedItemPosition

        binding.radioButton.isGone = !isSelected
        binding.tv.text = item.displayText
        binding.tv.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
        if (item.imageResId != -1) binding.imageView.setImageResource(item.imageResId)

        return view
    }
}
