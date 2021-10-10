package com.example.bookbazar.database

import androidx.room.*

@Dao
interface BookDao {
    @Insert
    fun insertBook(BookEntity:BookEntities)
    @Delete
    fun deleteBook(BookEntity: BookEntities)

   @Query("Select * FROM books")
    fun getAllBook() : List<BookEntities>

    @Query("Select * FROM books WHERE book_id =:bookId")
    fun getBookById(bookId: String) : BookEntities

}