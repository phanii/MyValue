package com.myprice.value.ui.myrequests.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductBean(
    val id: String? = null,
    val location: String? = null,
    val name: String? = null,
    val quantity: String? = null,
    val requestedPrice: String? = null,
    var requestStatus: Boolean? = null
) : Parcelable