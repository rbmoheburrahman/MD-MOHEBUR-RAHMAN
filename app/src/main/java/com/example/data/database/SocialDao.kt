package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    // --- User Auth ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccount?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccount?


    // --- Post Feeds ---
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPostsFlow(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: Int): Post?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: Int)


    // --- Comments ---
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPostFlow(postId: Int): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)


    // --- Likes ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: PostLike)

    @Query("DELETE FROM post_likes WHERE postId = :postId AND userEmail = :userEmail")
    suspend fun deleteLike(postId: Int, userEmail: String)

    @Query("SELECT COUNT(*) FROM post_likes WHERE postId = :postId AND userEmail = :userEmail")
    suspend fun isPostLikedByUser(postId: Int, userEmail: String): Int

    @Query("SELECT postId FROM post_likes WHERE userEmail = :userEmail")
    fun getLikedPostIdsFlow(userEmail: String): Flow<List<Int>>
}
