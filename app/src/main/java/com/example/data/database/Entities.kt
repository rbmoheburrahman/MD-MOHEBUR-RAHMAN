package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccount(
    @PrimaryKey val email: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val bio: String = "",
    val joinedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorEmail: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "post_likes", primaryKeys = ["postId", "userEmail"])
data class PostLike(
    val postId: Int,
    val userEmail: String
)
