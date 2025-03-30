package com.application.adverial.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.Review

class ReviewAdapter(private val context: Context, private var reviews: List<Review>) :
        RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.review_username)
        val date: TextView = view.findViewById(R.id.review_date)
        val content: TextView = view.findViewById(R.id.review_content)
        val stars: List<ImageView> =
                listOf(
                        view.findViewById(R.id.star1),
                        view.findViewById(R.id.star2),
                        view.findViewById(R.id.star3),
                        view.findViewById(R.id.star4),
                        view.findViewById(R.id.star5)
                )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]

        // Set user name
        holder.username.text = review.user.name

        // Format and set date (only show the date part of timestamp)
        val formattedDate = review.created_at.split("T")[0]
        holder.date.text = formattedDate

        // Set review content
        holder.content.text = review.review

        // Set star rating
        setStarRating(holder.stars, review.rating)
    }

    override fun getItemCount(): Int = reviews.size

    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }

    fun addMoreReviews(additionalReviews: List<Review>) {
        val currentSize = reviews.size
        val newList = reviews.toMutableList()
        newList.addAll(additionalReviews)
        reviews = newList
        notifyItemRangeInserted(currentSize, additionalReviews.size)
    }

    private fun setStarRating(stars: List<ImageView>, rating: Int) {
        for (i in stars.indices) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.ic_star_filled)
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty)
            }
        }
    }
}
