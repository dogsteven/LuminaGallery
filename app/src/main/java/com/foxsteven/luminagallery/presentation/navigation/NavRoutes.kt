package com.foxsteven.luminagallery.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object GalleryRoute

@Serializable
object TagsRoute

@Serializable
data class ImageDetailRoute(val imageId: Long)
