package com.example.dbs_room_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val newWordActivityRequestCode = 1
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = WordListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        wordViewModel.allWords.observe(this) { words ->
            try {
                Log.e("MainActivity", "Observed words: $words")
                words.let {
                    Log.e("MainActivity", "Submitting list to adapter")
                    adapter.submitList(it)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error observing words: ${e.message}", e)
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            Log.e("NewWordActivity", "resultcode is : ${resultCode}")
            Log.e("NewWordActivity", "requestcode is : ${requestCode}")
            Log.e("NewWordActivity", "intent is : ${intentData}")
            intentData?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let { reply ->
                Log.e(
                    "NewWordActivity",
                    "NewWordActivity.EXTRA_REPLY is : ${NewWordActivity.EXTRA_REPLY}"
                )
                val word = Word(reply)
                Log.e("NewWordActivity", "word is : : $word")
                wordViewModel.insert(word)
              //  Log.e("NewWordActivity", "wordviewmodel is : : ${wordViewModel.insert(word)}")
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}