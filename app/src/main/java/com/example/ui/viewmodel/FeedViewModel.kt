package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ServiceLocator
import com.example.data.database.Post
import com.example.data.database.Comment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ServiceLocator.getRepository(application)

    // Exposed posts flow automatically mapped with active user's like indicator
    val postsFlow: StateFlow<List<PostWithLikeState>> = combine(
        repository.allPosts,
        repository.currentUserFlow.flatMapLatest { user ->
            if (user != null) repository.getLikedPostIds(user.email) else flowOf(emptyList())
        }
    ) { posts, likedIds ->
        posts.map { post ->
            PostWithLikeState(
                post = post,
                isLiked = likedIds.contains(post.id)
            )
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
        }
    }

    fun publishPost(content: String, imageUrl: String?, onComplete: () -> Unit) {
        if (content.trim().isEmpty()) return
        viewModelScope.launch {
            val success = repository.createPost(content, imageUrl)
            if (success) {
                onComplete()
            }
        }
    }

    fun getComments(postId: Int): Flow<List<Comment>> {
        return repository.getCommentsForPost(postId)
    }

    fun submitComment(postId: Int, content: String) {
        viewModelScope.launch {
            repository.addComment(postId, content)
        }
    }
}

data class PostWithLikeState(
    val post: Post,
    val isLiked: Boolean
)
