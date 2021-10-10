package com.example.bookbazar.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.bookbazar.R
import com.example.bookbazar.adapter.FavouriteRecyclerAdapter
import com.example.bookbazar.database.BookDatabase
import com.example.bookbazar.database.BookEntities

class FavouriteFragment : Fragment() {
    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    var dbBookList = listOf<BookEntities>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = GridLayoutManager(activity as Context,2)

        dbBookList = RetrieveFavourite(activity as Context).execute().get()

        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbBookList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager



        }
        return  view
    }

    class RetrieveFavourite(val context: Context) : AsyncTask<Void, Void, List<BookEntities>>() {
        override fun doInBackground(vararg params: Void?): List<BookEntities> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()
            return db.bookDao().getAllBook()
        }

    }
}
