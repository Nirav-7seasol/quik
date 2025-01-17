
package com.messages.readmms.readsmss.repository

import android.net.Uri
import com.messages.readmms.readsmss.model.Contact
import com.messages.readmms.readsmss.model.ContactGroup
import io.reactivex.Observable
import io.reactivex.Single
import io.realm.RealmResults

interface ContactRepository {

    fun findContactUri(address: String): Single<Uri>

    fun getContacts(): RealmResults<Contact>

    fun getUnmanagedContact(lookupKey: String): Contact?

    fun getUnmanagedContacts(starred: Boolean = false): Observable<List<Contact>>

    fun getUnmanagedContactGroups(): Observable<List<ContactGroup>>

    fun setDefaultPhoneNumber(lookupKey: String, phoneNumberId: Long)

}
