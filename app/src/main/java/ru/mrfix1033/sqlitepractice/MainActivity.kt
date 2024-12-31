package ru.mrfix1033.sqlitepractice

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val products = mutableListOf<Product>()
    private lateinit var listViewAdapter: ArrayAdapter<Product>

    private lateinit var databaseManager: DatabaseManager
    private lateinit var editTextTitle: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var listView: ListView

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextWeight = findViewById(R.id.editTextWeight)
        editTextPrice = findViewById(R.id.editTextPrice)
        listView = findViewById(R.id.listView)

        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            val product = Product(
                editTextTitle.text.toString(),
                editTextWeight.text.toString().toFloat(),
                editTextPrice.text.toString().toFloat(),
            )
            databaseManager.insert(product)
            products.add(product)
            listViewAdapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.buttonLoad).setOnClickListener {
            products.clear()
            databaseManager.select {
                while (it.moveToNext()) {
                    products.add(
                        Product(
                            it.getString(it.getColumnIndex(DatabaseManager.Key.KEY_TITLE)),
                            it.getFloat(it.getColumnIndex(DatabaseManager.Key.KEY_WEIGHT)),
                            it.getFloat(it.getColumnIndex(DatabaseManager.Key.KEY_PRICE))
                        )
                    )
                }
            }
            listViewAdapter.notifyDataSetChanged()
        }

        listViewAdapter = object : ArrayAdapter<Product>(this, R.layout.product_item, products) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var view = convertView
                if (view == null) view = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.product_item, parent, false)!!
                view.run {
                    products[position].run {
                        findViewById<TextView>(R.id.textViewTitle).text = title
                        findViewById<TextView>(R.id.textViewWeight).text = weight.toString()
                        findViewById<TextView>(R.id.textViewPrice).text = price.toString()
                    }
                }
                return view
            }
        }
        listView.adapter = listViewAdapter

        databaseManager = DatabaseManager(this, null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_exit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemExit) finishAffinity()
        return super.onOptionsItemSelected(item)
    }
}