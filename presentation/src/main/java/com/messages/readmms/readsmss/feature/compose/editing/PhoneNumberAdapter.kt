
package com.messages.readmms.readsmss.feature.compose.editing

import android.view.LayoutInflater
import android.view.ViewGroup
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.base.QkAdapter
import com.messages.readmms.readsmss.common.base.QkViewHolder
import com.messages.readmms.readsmss.model.PhoneNumber
import kotlinx.android.synthetic.main.contact_number_list_item.*

class PhoneNumberAdapter : QkAdapter<PhoneNumber>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact_number_list_item, parent, false)
        return QkViewHolder(view)
    }

    override fun onBindViewHolder(holder: QkViewHolder, position: Int) {
        val number = getItem(position)

        holder.address.text = number.address
        holder.type.text = number.type
    }

    override fun areItemsTheSame(old: PhoneNumber, new: PhoneNumber): Boolean {
        return old.type == new.type && old.address == new.address
    }

    override fun areContentsTheSame(old: PhoneNumber, new: PhoneNumber): Boolean {
        return old.type == new.type && old.address == new.address
    }

}