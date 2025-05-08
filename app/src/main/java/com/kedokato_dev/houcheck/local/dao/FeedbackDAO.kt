package com.kedokato_dev.houcheck.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kedokato_dev.houcheck.local.entity.FeedbackEntity
import com.kedokato_dev.houcheck.network.model.Feedback
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDAO {
    // Lắng nghe sự thay đổi của bảng feedback
    @Query("SELECT * FROM feedback WHERE email = :email ORDER BY createdAt DESC")
    fun getFeedbackByEmail(email: String): Flow<List<FeedbackEntity>>


    @Query("DELETE FROM feedback")
    suspend fun deleteAllFeedback()

    @Query("DELETE FROM feedback WHERE id = :id")
    suspend fun deleteFeedbackById(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeedback(entities: List<FeedbackEntity>)

    @Update
    suspend fun updateFeedback(feedback: FeedbackEntity): Int
}