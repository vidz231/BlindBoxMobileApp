package com.vidz.data.server.retrofit.dto.map

import com.google.gson.annotations.SerializedName

data class AutoCompleteDto(
    val description: String? = null,
    @SerializedName("matched_substrings")
    val matchedSubstrings: List<Any>? = null,
    @SerializedName("place_id")
    val placeId: String? = null,
    val reference: String? = null,
    @SerializedName("structured_formatting")
    val structuredFormatting: StructuredFormatting? = null,
    val terms: List<Any>? = null,
    @SerializedName("has_children")
    val hasChildren: Boolean? = null,
    @SerializedName("display_type")
    val displayType: String? = null,
    val score: Double? = null,
    @SerializedName("plus_code")
    val plusCode: PlusCode? = null
) {
    data class StructuredFormatting(
        @SerializedName("main_text")
        val mainText: String? = null,
        @SerializedName("secondary_text")
        val secondaryText: String? = null
    )
    data class PlusCode(
        @SerializedName("compound_code")
        val compoundCode: String? = null,
        @SerializedName("global_code")
        val globalCode: String? = null
    )
} 