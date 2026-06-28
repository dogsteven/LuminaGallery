package com.foxsteven.luminagallery.data.model

data class FilterCriteria(
    val query: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val tagNames: Set<String> = emptySet()
) {
    fun isEmpty(): Boolean = query.isNullOrBlank() && startDate == null && endDate == null && tagNames.isEmpty()
}
