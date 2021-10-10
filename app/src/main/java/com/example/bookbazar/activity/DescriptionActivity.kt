package com.example.bookbazar.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookbazar.R
import com.example.bookbazar.adapter.DashboardRecyclerAdapter
import com.example.bookbazar.database.BookDatabase
import com.example.bookbazar.database.BookEntities
import com.example.bookbazar.model.Book
import com.example.bookbazar.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar

    var bookId: String? = "100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        imgBookImage = findViewById(R.id.imgBookImage)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        }
        else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occured!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occured!",
                Toast.LENGTH_SHORT
            ).show()

        }
        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {


                    try {
                        val success = it.getBoolean("success")

                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE
                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntities = BookEntities(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )
                            val checkFav = DBAsyncTask(applicationContext,bookEntities,1).execute()
                            val isFav = checkFav.get()
                            if (isFav){
                                btnAddToFav.text = "Remove from Favourites"
                                val favColor = ContextCompat.getColor(applicationContext,R.color.colorFavorites)
                                btnAddToFav.setBackgroundColor(favColor)
                            }else
                            {
                                btnAddToFav.text = "Add To Favourite"
                                val noFavColor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }
                            btnAddToFav.setOnClickListener{
                                if (!DBAsyncTask(applicationContext,bookEntities,1).execute().get()){
                                    val async = DBAsyncTask(applicationContext,bookEntities,2).execute()
                                    val result = async.get()
                                    if (result){
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book added to Favourite",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFav.text = "Remove from Favourites"
                                        val favColor = ContextCompat.getColor(applicationContext,R.color.colorFavorites)
                                        btnAddToFav.setBackgroundColor(favColor)
                                    }else{
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Some unexpected error occured!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }else{
                                    val async = DBAsyncTask(applicationContext,bookEntities,3).execute()
                                    val result = async.get()
                                    if (result){
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book removed from Favourite",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFav.text = "Add To Favourites"
                                        val noFavColor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                        btnAddToFav.setBackgroundColor(noFavColor)
                                    }else
                                    {
                                        Toast.makeText(
                                        this@DescriptionActivity,
                                        "Some  error occured!",
                                        Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some unexpected error occured!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some unexpected error occured!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {

                    Toast.makeText(this@DescriptionActivity, "Volley Error $it", Toast.LENGTH_SHORT)
                        .show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "2d4493d057bead"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        }
        else{
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){ text, listener->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

   class DBAsyncTask(val context: Context, val bookEntities: BookEntities,val mode:Int) : AsyncTask<Void,Void,Boolean>() {

      val db = Room.databaseBuilder(context,BookDatabase::class.java,"book-db").build()
       override fun doInBackground(vararg params: Void?): Boolean {
           when(mode){
               1->{
                   val book: BookEntities? = db.bookDao().getBookById(bookEntities.book_id.toString())
                   db.close()
                   return book != null
               }
               2->{
                   db.bookDao().insertBook(bookEntities)
                   db.close()
                   return true

               }
               3->{
                    db.bookDao().deleteBook(bookEntities)
                   db.close()
                   return true
               }
           }
          return false
       }
   }
}