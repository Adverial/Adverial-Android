package com.application.adverial.remote.model

data class Review(
        val id: Int,
        val ad_id: Int,
        val user_id: Int,
        val rating: Int,
        val review: String,
        val status: Int,
        val created_at: String,
        val updated_at: String,
        val user: ReviewUser
)

data class ReviewResponse(val status: Boolean, val message: String, val data: ReviewResponseData)

data class ReviewResponseData(
        val average_rating: Float,
        val reviews_count: Int,
        val reviews: ReviewPagination
)

data class ReviewPagination(
        val current_page: Int,
        val data: List<Review>,
        val total: Int,
        val per_page: Int
)

data class ReviewSubmitResponse(val status: Boolean, val message: String, val data: Review?)
