package com.kedokato_dev.houcheck.data.repository

import android.util.Log
import com.kedokato_dev.houcheck.data.api.FetchListScoreService
import com.kedokato_dev.houcheck.data.model.CourseResult
import com.kedokato_dev.houcheck.data.model.CourseResultResponse
import com.kedokato_dev.houcheck.data.model.Data
import com.kedokato_dev.houcheck.database.dao.CourseResultDAO
import com.kedokato_dev.houcheck.database.entity.CourseResultEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchListScoreRepository(
    private val api: FetchListScoreService,
    private val dao : CourseResultDAO
) {

    suspend fun fetchAndSaveListScore(sessionId: String): Result<CourseResultResponse> = withContext(Dispatchers.IO) {
        try {
            val localData = dao.getAllCourseResults()
            if (localData.isNotEmpty()) {
                val converted = localData.map {
                    CourseResult(
                        semester = it.semester,
                        academicYear = it.academicYear,
                        courseCode = it.courseCode,
                        courseName = it.courseName,
                        credits = it.credits,
                        score10 = it.score10,
                        score4 = it.score4,
                        letterGrade = it.letterGrade,
                        notCounted = it.notCounted,
                        note = it.note,
                        detailLink = it.detailLink
                    )
                }
                return@withContext Result.success(CourseResultResponse(success = true, data = Data(scores = converted)))
            }else{
                Log.d("FetchListScoreRepository", "Local data is empty, fetching from API...")
            }

            // Fetch data from API if local data is empty
            val response = api.fetchListScore(sessionId)
            Log.d("FetchListScoreRepository", "API response: $response")
            val courseResults = response.data
            if (courseResults.scores.isNotEmpty()) {
                // Save fetched data to the database
                courseResults.scores.forEach { courseResult ->
                    dao.insertCourseResult(
                        CourseResultEntity(
                            semester = courseResult.semester,
                            academicYear = courseResult.academicYear,
                            courseCode = courseResult.courseCode,
                            courseName = courseResult.courseName,
                            credits = courseResult.credits,
                            score10 = courseResult.score10,
                            score4 = courseResult.score4,
                            letterGrade = courseResult.letterGrade,
                            notCounted = courseResult.notCounted,
                            note = courseResult.note,
                            detailLink = courseResult.detailLink
                        ))
                }
                return@withContext Result.success(response)
            } else {
                return@withContext Result.failure(Exception("No course results found"))
            }
        } catch (e: Exception) {
            Log.e("FetchListScoreRepository", "Error fetching data: ${e.message}")
            return@withContext Result.failure(e)
        }
    }

}