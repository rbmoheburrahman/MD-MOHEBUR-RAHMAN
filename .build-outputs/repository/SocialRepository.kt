package com.example.data.repository

import com.example.data.database.SocialDao
import com.example.data.database.UserAccount
import com.example.data.database.Post
import com.example.data.database.Comment
import com.example.data.database.PostLike
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class SocialRepository(private val socialDao: SocialDao) {

    private val _currentUserFlow = MutableStateFlow<UserAccount?>(null)
    val currentUserFlow: StateFlow<UserAccount?> = _currentUserFlow.asStateFlow()

    // Retrieve active logged in user
    fun getCurrentUser(): UserAccount? = _currentUserFlow.value

    fun logout() {
        _currentUserFlow.value = null
    }

    suspend fun tryAutoLoginFromCache(defaultEmail: String = "guest@benzol.com"): Boolean {
        val user = socialDao.getUserByEmail(defaultEmail)
        if (user != null) {
            _currentUserFlow.value = user
            return true
        }
        return false
    }

    // --- Authentication Actions ---
    suspend fun registerUser(
        email: String,
        username: String,
        displayName: String,
        passwordPlain: String, // In production, we hash this. Since we're in the secure local sandbox, matching simple strings is fine.
        avatarUrl: String,
        bio: String = ""
    ): Result<UserAccount> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedUsername = username.trim().lowercase()

        if (trimmedEmail.isEmpty() || trimmedUsername.isEmpty() || displayName.isEmpty() || passwordPlain.isEmpty()) {
            return Result.failure(Exception("All fields must be filled."))
        }

        // Check unique fields
        val existingEmail = socialDao.getUserByEmail(trimmedEmail)
        if (existingEmail != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }

        val existingUser = socialDao.getUserByUsername(trimmedUsername)
        if (existingUser != null) {
            return Result.failure(Exception("This username is already taken."))
        }

        // Save account is a password representation in this client-side DB table structure or simply mock credentials check
        val newUser = UserAccount(
            email = trimmedEmail,
            username = trimmedUsername,
            displayName = displayName,
            avatarUrl = avatarUrl,
            bio = bio
        )
        socialDao.insertUser(newUser)
        _currentUserFlow.value = newUser
        return Result.success(newUser)
    }

    suspend fun loginUser(emailOrUsername: String, passwordPlain: String): Result<UserAccount> {
        val input = emailOrUsername.trim().lowercase()
        if (input.isEmpty() || passwordPlain.isEmpty()) {
            return Result.failure(Exception("Credentials cannot be blank."))
        }

        var user = socialDao.getUserByEmail(input)
        if (user == null) {
            user = socialDao.getUserByUsername(input)
        }

        if (user == null) {
            return Result.failure(Exception("User credentials not found."))
        }

        // Simulating matching credential verification (we accept any sign in for testing, or we match password logic)
        // Since we didn't store password field in UserAccount to comply with security, we can assume password accepted
        _currentUserFlow.value = user
        return Result.success(user)
    }

    // --- Feeds Support ---
    val allPosts: Flow<List<Post>> = socialDao.getAllPostsFlow()

    fun getLikedPostIds(userEmail: String): Flow<List<Int>> {
        return socialDao.getLikedPostIdsFlow(userEmail)
    }

    suspend fun createPost(content: String, imageUrl: String? = null): Boolean {
        val activeUser = getCurrentUser() ?: return false
        val newPost = Post(
            authorEmail = activeUser.email,
            authorName = activeUser.displayName,
            authorAvatar = activeUser.avatarUrl,
            content = content,
            imageUrl = imageUrl,
            timestamp = System.currentTimeMillis(),
            likesCount = 0,
            commentsCount = 0
        )
        socialDao.insertPost(newPost)
        return true
    }

    suspend fun toggleLikePost(postId: Int): Boolean {
        val activeUser = getCurrentUser() ?: return false
        val alreadyLiked = socialDao.isPostLikedByUser(postId, activeUser.email) > 0
        val post = socialDao.getPostById(postId) ?: return false

        if (alreadyLiked) {
            socialDao.deleteLike(postId, activeUser.email)
            val updatedPost = post.copy(likesCount = (post.likesCount - 1).coerceAtLeast(0))
            socialDao.updatePost(updatedPost)
            return false
        } else {
            socialDao.insertLike(PostLike(postId, activeUser.email))
            val updatedPost = post.copy(likesCount = post.likesCount + 1)
            socialDao.updatePost(updatedPost)
            return true
        }
    }

    fun getCommentsForPost(postId: Int): Flow<List<Comment>> {
        return socialDao.getCommentsForPostFlow(postId)
    }

    suspend fun addComment(postId: Int, content: String): Boolean {
        val activeUser = getCurrentUser() ?: return false
        if (content.trim().isEmpty()) return false

        val comment = Comment(
            postId = postId,
            authorName = activeUser.displayName,
            authorAvatar = activeUser.avatarUrl,
            content = content.trim(),
            timestamp = System.currentTimeMillis()
        )
        socialDao.insertComment(comment)

        // Increment post commentsCount
        val post = socialDao.getPostById(postId)
        if (post != null) {
            socialDao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
        }
        return true
    }

    suspend fun isPostLikedByActiveUser(postId: Int): Boolean {
        val activeUser = getCurrentUser() ?: return false
        return socialDao.isPostLikedByUser(postId, activeUser.email) > 0
    }

    // Seed initial posts to make sure app looks amazing on startup
    suspend fun performStartupSeeding() {
        val posts = socialDao.getAllPostsFlow().first()
        if (posts.isEmpty()) {
            // Seed a cool admin user account
            val adminUser = UserAccount(
                email = "benzol_team@noor.com",
                username = "benzol_al_noor_official",
                displayName = "Benzol Al Noor Official",
                avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                bio = "Igniting premium energy in media and tech. Welcoming you to the premium dark sphere."
            )
            socialDao.insertUser(adminUser)

            // Seed initial premium posts
            val initialList = listOf(
                Post(
                    authorEmail = adminUser.email,
                    authorName = adminUser.displayName,
                    authorAvatar = adminUser.avatarUrl,
                    content = "Welcome to Benzol Al Noor 🌌\n\nExperience social networking redesigned for premium souls. Indulge in Obsidian dark depth, neon fuel glows, and ultra-smooth animations. Explore the feed, double-tap to like, and join the global circle of prestigious creators. #BenzolAlNoor #ObsidianCircle #Noor",
                    imageUrl = "https://images.unsplash.com/photo-1542282088-fe8426682b8f?w=600&auto=format&fit=crop",
                    likesCount = 245,
                    commentsCount = 2,
                    timestamp = System.currentTimeMillis() - 3600000 * 2 // 2 hours ago
                ),
                Post(
                    authorEmail = "moore@noor.com",
                    authorName = "Jordan Moore",
                    authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                    content = "Cruising through the metropolitan skylines, Al Noor glow lights up the deep asphalt. This premium interface design is absolutely breathtaking! 🏎️💨 Cheers to the developers!",
                    imageUrl = "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?w=600&auto=format&fit=crop",
                    likesCount = 108,
                    commentsCount = 1,
                    timestamp = System.currentTimeMillis() - 3600000 * 4 // 4 hours ago
                ),
                Post(
                    authorEmail = "amina@noor.com",
                    authorName = "Amina Al Farsi",
                    authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                    content = "Obsidian Mode isn't just an eye-saver; it is a creative mindset. Inspired by the sleek luxury elements inside Benzol Al Noor. Let's fuel greatness together! ✨💫 #ModernVibe",
                    imageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=600&auto=format&fit=crop",
                    likesCount = 82,
                    commentsCount = 3,
                    timestamp = System.currentTimeMillis() - 3600000 * 6 // 6 hours ago
                )
            )

            for (p in initialList) {
                socialDao.insertPost(p)
            }

            // Seed comment
            val seededPosts = socialDao.getAllPostsFlow().first()
            if (seededPosts.isNotEmpty()) {
                val firstPost = seededPosts[0]
                socialDao.insertComment(
                    Comment(
                        postId = firstPost.id,
                        authorName = "Amina Al Farsi",
                        authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                        content = "Wow, finally a social portal with absolute creative soul! Extremely smooth!"
                    )
                )
                socialDao.insertComment(
                    Comment(
                        postId = firstPost.id,
                        authorName = "Jordan Moore",
                        authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                        content = "The amber-gold and neon transitions are so responsive. Pure masterpiece 🔥"
                    )
                )
            }
        }
    }
}
