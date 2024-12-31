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
import androidx.appcompat.app.AlertDialog
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
            val title = editTextTitle.text.toString()
            val weight = editTextWeight.text.toString()
            val price = editTextPrice.text.toString()

            if (title.trim() == "" || weight.trim() == "" || price.trim() == "")
                return@setOnClickListener

            val product = Product(
                -1,
                title,
                weight.toFloat(),
                price.toFloat(),
            )

            product.id = databaseManager.insert(product, true)!!
            products.add(product)
            listViewAdapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.buttonLoad).setOnClickListener {
            loadProductsList()
        }

        listViewAdapter = object : ArrayAdapter<Product>(this, R.layout.product_item, products) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var view = convertView
                if (view == null) view = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.product_item, parent, false)!!
                view.run {
                    products[position].run {
                        findViewById<TextView>(R.id.textViewTitle).text = title + id
                        findViewById<TextView>(R.id.textViewWeight).text = weight.toString()
                        findViewById<TextView>(R.id.textViewPrice).text = price.toString()
                    }
                }
                return view
            }
        }
        listView.adapter = listViewAdapter
        listView.setOnItemClickListener { adapterView, view, position, id ->
            AlertDialog.Builder(this)
                .setTitle(R.string.what_do_you_want_to_do)
                .setPositiveButton("Обновить") { _, _ ->
                    val product = products[position]
                    val alertView = layoutInflater.inflate(
                        R.layout.product_data_input, adapterView, false
                    )
                    val editTextTitle = alertView.findViewById<EditText>(R.id.editTextTitle)
                    val editTextWeight = alertView.findViewById<EditText>(R.id.editTextWeight)
                    val editTextPrice = alertView.findViewById<EditText>(R.id.editTextPrice)
                    product.run {
                        editTextTitle.setText(title)
                        editTextWeight.setText(weight.toString())
                        editTextPrice.setText(price.toString())
                    }

                    AlertDialog.Builder(this)
                        .setView(alertView)
                        .setPositiveButton("Обновить") { _, _ ->
                            val title = editTextTitle.text.toString()
                            val weight = editTextWeight.text.toString()
                            val price = editTextPrice.text.toString()
                            if (title.trim() == "" || weight.trim() == "" || price.trim() == "")
                                return@setPositiveButton
                            product.title = title
                            product.weight = weight.toFloat()
                            product.price = price.toFloat()
                            databaseManager.update(product)
                            listViewAdapter.notifyDataSetChanged()
                        }
                        .setNegativeButton("Отмена") { _, _ -> }
                        .show()
                }
                .setNegativeButton("Удалить") { _, _ ->
                    databaseManager.delete(products[position].id)
                    products.removeAt(position)
                    listViewAdapter.notifyDataSetChanged()
                }
                .setNeutralButton("Отмена") { _, _ ->
                }
                .show()
        }

        databaseManager = DatabaseManager(this, null)

        loadProductsList()
    }

    @SuppressLint("Range")
    private fun loadProductsList() {
        products.clear()
        databaseManager.select {
            while (it.moveToNext()) {
                products.add(
                    Product(
                        it.getInt(it.getColumnIndex(DatabaseManager.Key.KEY_ID)),
                        it.getString(it.getColumnIndex(DatabaseManager.Key.KEY_TITLE)),
                        it.getFloat(it.getColumnIndex(DatabaseManager.Key.KEY_WEIGHT)),
                        it.getFloat(it.getColumnIndex(DatabaseManager.Key.KEY_PRICE))
                    )
                )
            }
        }
        listViewAdapter.notifyDataSetChanged()
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