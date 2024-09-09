package com.example.versionetwohorizontales.data.database;

import androidx.room.Dao;


/**
 * Data Access Object (DAO) that provides methods that can be used to query,
 * update, insert, and delete data in the database.
 * https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
public interface NewsDao {
    /*@Query("SELECT * FROM news ORDER BY published_at DESC")
    List<News> getAll();

    @Query("SELECT * FROM news WHERE id = :id")
    News getNews(long id);

    @Query("SELECT * FROM news WHERE is_favorite = 1 ORDER BY published_at DESC")
    List<News> getFavoriteNews();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertNewsList(List<News> newsList);

    @Insert
    void insertAll(News... news);

    @Update
    int updateSingleFavoriteNews(News news);

    @Update
    int updateListFavoriteNews(List<News> news);

    @Delete
    void delete(News news);

    @Delete
    void deleteAllWithoutQuery(News... news);

    @Query("DELETE FROM news")
    int deleteAll();

    @Query("DELETE FROM news WHERE is_favorite = 0")
    void deleteNotFavoriteNews();*/
}
