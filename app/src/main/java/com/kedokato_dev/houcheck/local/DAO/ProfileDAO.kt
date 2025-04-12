package com.kedokato_dev.houcheck.local.DAO
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kedokato_dev.houcheck.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDAO {
    @Dao
    interface ProfileDAO {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertProfile(profile: ProfileEntity)

        @Update
        suspend fun updateProfile(profile: ProfileEntity)

        @Delete
        suspend fun deleteProfile(profile: ProfileEntity)
    }


//    @Query("SELECT * FROM profiles ORDER BY fullName ASC")
//    fun getAllProfile(): Flow<List<ProfileEntity>>

//    @Query("SELECT * FROM profiles WHERE studentId = :stuId")
//    suspend fun getProfileById(stuId: String): ProfileEntity?

}