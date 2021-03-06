package musicpractice.com.coeeter.clicktoeat.ui.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.models.Comment
import musicpractice.com.coeeter.clicktoeat.data.models.LikeOrDislike
import musicpractice.com.coeeter.clicktoeat.data.models.Restaurant
import musicpractice.com.coeeter.clicktoeat.data.models.User
import musicpractice.com.coeeter.clicktoeat.databinding.DialogUpdateCommentFormBinding
import musicpractice.com.coeeter.clicktoeat.databinding.DialogUserListBinding
import musicpractice.com.coeeter.clicktoeat.databinding.RecyclerCommentItemBinding
import musicpractice.com.coeeter.clicktoeat.utils.getStringFromSharedPref
import musicpractice.com.coeeter.clicktoeat.utils.isVisible

class CommentAdapter(
    private val context: Context,
    private val restaurant: Restaurant
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private var commentList = ArrayList<Comment>()
    private var userList = ArrayList<User>()
    private var likesAndDislikesList = ArrayList<LikeOrDislike>()
    var dataSetChangeListener: DataToChangeListener? = null

    fun setCommentList(commentList: ArrayList<Comment>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    fun setUserList(userList: ArrayList<User>) {
        this.userList = userList
        notifyDataSetChanged()
    }

    fun setLikesAndDislikesList(likesAndDislikesList: ArrayList<LikeOrDislike>) {
        this.likesAndDislikesList = likesAndDislikesList
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: RecyclerCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val binding =
            RecyclerCommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        val comment = commentList[position]
        if (userList.isEmpty()) return
        val commentedUser = userList.filter { user -> user.username == comment.username }[0]

        if (commentedUser.imagePath != null) {
            Picasso.with(context)
                .load(
                    context.getString(R.string.base_url) +
                            "upload/" +
                            commentedUser.imagePath
                )
                .into(holder.binding.profileImage)
        }

        holder.binding.username.text = commentedUser.username
        holder.binding.datePosted.text = comment.datePosted
        holder.binding.review.text = comment.review

        val starArray = arrayOf(
            holder.binding.star1,
            holder.binding.star2,
            holder.binding.star3,
            holder.binding.star4,
            holder.binding.star5
        )

        for (i in 0 until comment.rating) {
            starArray[i].setImageResource(R.drawable.ic_star)
        }

        if ((5 - comment.rating) != 0) {
            for (i in comment.rating until 5) {
                starArray[i].setImageResource(R.drawable.ic_star_outline)
            }
        }

        val loggedInUserProfile = (context as Activity).getStringFromSharedPref(
            context.getString(R.string.sharedPrefName),
            context.getString(R.string.sharedPrefProfile)
        )
        val loggedInUserAccount = Gson().fromJson(loggedInUserProfile, User::class.java)

        holder.binding.edit.isVisible(false)
        if (loggedInUserAccount.username == commentedUser.username) {
            holder.binding.edit.isVisible(true)
            holder.binding.edit.setOnClickListener {
                val popup = PopupMenu(context, it)
                popup.menuInflater.inflate(R.menu.dropdown_comment, popup.menu)

                popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        R.id.miEdit -> editComment(comment)
                        R.id.miDelete -> dataSetChangeListener!!.deleteComment(comment.id!!)
                    }
                    true
                }

                popup.show()
            }
        }

        var likeCount = 0
        var dislikeCount = 0
        for (like in likesAndDislikesList) {
            if (like.commentId == comment.id) {
                when (like.isLiked) {
                    0 -> dislikeCount++
                    1 -> likeCount++
                }
            }
        }
        holder.binding.likeCount.text = likeCount.toString()
        holder.binding.dislikeCount.text = dislikeCount.toString()
        holder.binding.likeBtn.setImageResource(R.drawable.ic_thumb_up_empty)
        holder.binding.dislikeBtn.setImageResource(R.drawable.ic_thumb_down_empty)

        for (like in likesAndDislikesList) {
            if (like.username == loggedInUserAccount.username && like.commentId == comment.id) {
                when (like.isLiked) {
                    0 -> holder.binding.dislikeBtn.setImageResource(R.drawable.ic_thumb_down_filled)
                    1 -> holder.binding.likeBtn.setImageResource(R.drawable.ic_thumb_up_filled)
                }
            }
        }

        holder.binding.likeBtn.setOnClickListener {
            changeLikeAndDislikeValues(comment, loggedInUserAccount, 1)
        }
        holder.binding.likeCount.setOnClickListener {
            changeLikeAndDislikeValues(comment, loggedInUserAccount, 1)
        }

        holder.binding.dislikeBtn.setOnClickListener {
            changeLikeAndDislikeValues(comment, loggedInUserAccount, 0)
        }
        holder.binding.dislikeCount.setOnClickListener {
            changeLikeAndDislikeValues(comment, loggedInUserAccount, 0)
        }
    }

    private fun changeLikeAndDislikeValues(comment: Comment, user: User, isLiked: Int) {
        val isDisliked = if (isLiked == 0) 1 else 0
        if (comment.username == user.username) return showUsersWhoLikedOrDisliked(
            isLiked,
            comment.id!!
        )
        val like = likesAndDislikesList.find {
            it.username == user.username && it.commentId == comment.id
        } ?: return dataSetChangeListener!!.addLike(
            LikeOrDislike(
                commentId = comment.id!!,
                isLiked = isLiked,
                isDisliked = isDisliked
            )
        )
        when (like.isLiked) {
            isDisliked -> dataSetChangeListener!!.apply {
                removeLike(like.id!!)
                addLike(
                    LikeOrDislike(
                        commentId = comment.id!!,
                        isLiked = isLiked,
                        isDisliked = isDisliked
                    )
                )
            }
            isLiked -> dataSetChangeListener!!.removeLike(like.id!!)
        }
    }

    private fun showUsersWhoLikedOrDisliked(isLiked: Int, commentId: Int) {
        val dialog = Dialog(context)
        val binding = DialogUserListBinding.inflate(LayoutInflater.from(context))
        dialog.apply {
            setCancelable(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            val width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
            window?.setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            show()
        }

        val likeList = likesAndDislikesList.filter {
            it.commentId == commentId && it.isLiked == isLiked
        }.map { it.username }
        val userList = userList.filter { it.username in likeList }

        binding.closeBtn.setOnClickListener { dialog.dismiss() }
        binding.noneLike.isVisible(false)
        if (userList.isEmpty())
            return binding.noneLike.run {
                text = context.getString(
                    if (isLiked == 1) R.string.no_like
                    else R.string.no_dislike
                )
                isVisible(true)
            }
        binding.userList.apply {
            adapter = UserAdapter(context, userList)
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun editComment(comment: Comment) {
        val dialog = Dialog(context)
        val width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()

        val binding = DialogUpdateCommentFormBinding.inflate(
            LayoutInflater.from(context)
        )

        dialog.apply {
            setCancelable(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            window?.setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        }

        var rating = comment.rating
        var review = comment.review

        val starArray = arrayOf(
            binding.star1,
            binding.star2,
            binding.star3,
            binding.star4,
            binding.star5
        )
        for (i in 0 until rating) {
            starArray[i].setImageResource(R.drawable.ic_star)
            starArray[i].tag = "Checked"
        }

        if ((5 - rating) != 0) {
            for (i in rating until 5) {
                starArray[i].setImageResource(R.drawable.ic_star_outline)
                starArray[i].tag = "Unchecked"
            }
        }

        for (star in starArray) {
            star.setOnClickListener {
                for (toBeCheckedStar in starArray) {
                    toBeCheckedStar.setImageResource(R.drawable.ic_star_outline)
                    toBeCheckedStar.tag = "Unchecked"
                }

                val index = getStarIndex(star)
                for (i in 0 until index + 1) {
                    starArray[i].setImageResource(R.drawable.ic_star)
                    starArray[i].tag = "Checked"
                }

                rating = index + 1
                if (rating == comment.rating && review == comment.review) {
                    binding.submitBtn.isEnabled = false
                    return@setOnClickListener
                }
                binding.submitBtn.isEnabled = true
            }
        }

        binding.submitBtn.isEnabled = false

        binding.editComment.setText(review)
        binding.editComment.addTextChangedListener {
            review = it.toString()
            if (review == comment.review && rating == comment.rating)
                return@addTextChangedListener binding.submitBtn.run { isEnabled = false }
            binding.submitBtn.isEnabled = true
        }

        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.submitBtn.setOnClickListener {
            dataSetChangeListener!!.editComment(
                Comment(
                    id = comment.id,
                    restaurantName = restaurant.name,
                    restaurantId = restaurant._id,
                    review = review,
                    rating = rating
                )
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    private fun getStarIndex(star: ImageView): Int {
        return when (star.id) {
            R.id.star1 -> 0
            R.id.star2 -> 1
            R.id.star3 -> 2
            R.id.star4 -> 3
            R.id.star5 -> 4
            else -> -1
        }
    }

    interface DataToChangeListener {
        fun editComment(comment: Comment)
        fun deleteComment(commentId: Int)
        fun addLike(likeOrDislike: LikeOrDislike)
        fun removeLike(likeId: Int)
    }
}