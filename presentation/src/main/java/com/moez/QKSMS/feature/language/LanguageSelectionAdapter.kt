package com.moez.QKSMS.feature.language

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moez.QKSMS.common.SharedPrefs
import com.moez.QKSMS.common.getDrawable
import dev.octoshrimpy.quik.R
import dev.octoshrimpy.quik.common.util.Colors
import dev.octoshrimpy.quik.databinding.ItemLanguageBinding

class LanguageSelectionAdapter(
    private val languageList: MutableList<Language>,
    private val theme: Colors.Theme
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedLanguage: Language? = SharedPrefs.selectedLanguage

    fun getSelectedLanguage() = selectedLanguage

    inner class LanguageViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLanguageBinding.inflate(inflater, parent, false)
        return LanguageViewHolder(binding)
    }

    override fun getItemCount() = languageList.size

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        val holder = h as LanguageViewHolder
        val data = languageList[position]
        holder.binding.apply {
            tvLanguageName.text = data.languageName
            tvLanguageTranslation.text = data.translationText
            val isSelected = selectedLanguage?.code == data.code
            if (isSelected) {
                selectedMark.setImageResource(R.drawable.ic_radio_button_checked_black_24dp)
            } else {
                selectedMark.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp)
            }
            selectedMark.imageTintList = ColorStateList.valueOf(theme.theme)
            Glide
                .with(ivFlag.context)
                .load(ivFlag.context.getDrawable(data.flag))
                .into(ivFlag)
            layoutLanguage.setOnClickListener {
                val oldData = selectedLanguage
                selectedLanguage = data
                val newData = selectedLanguage
                if (oldData?.code != newData?.code) {
                    notifyItemChanged(languageList.indexOfFirst { it.code == oldData?.code })
                    notifyItemChanged(languageList.indexOfFirst { it.code == newData?.code })
                }
            }
        }
    }
}