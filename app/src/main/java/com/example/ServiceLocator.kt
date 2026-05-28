package com.example

import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.repository.SocialRepository

object ServiceLocator {
    private var repository: SocialRepository? = null

    fun getRepository(context: Context): SocialRepository {
        return repository ?: synchronized(this) {
            val db = AppDatabase.getDatabase(context)
            val repo = SocialRepository(db.socialDao())
            repository = repo
            repo
        }
    }
}
