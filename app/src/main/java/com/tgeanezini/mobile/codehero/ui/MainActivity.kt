package com.tgeanezini.mobile.codehero.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.tgeanezini.mobile.codehero.R
import com.tgeanezini.mobile.codehero.adapter.CharactersAdapter
import com.tgeanezini.mobile.codehero.adapter.ListFooterAdapter
import com.tgeanezini.mobile.codehero.model.Character
import com.tgeanezini.mobile.codehero.model.CharacterResponse
import com.tgeanezini.mobile.codehero.service.RetrofitInitializer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_characters.*
import kotlinx.android.synthetic.main.list_character_footer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var characters: List<Character>
    private lateinit var adapter: CharactersAdapter
    private lateinit var pages: List<List<Character>>
    private lateinit var charactersRecyclerView: RecyclerView
    private lateinit var footerRecyclerView: RecyclerView
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        remoteConfig = FirebaseRemoteConfig.getInstance()

        val remoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setFetchTimeoutInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings)

        if (checkInternetConnection(this)) {
            fetchConfigs()
        } else {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage(getString(R.string.dialog_falha_message))
                .setPositiveButton("OK") { dialog,_ ->
                    dialog.cancel()
                    finish()
                }

            val dialog = dialogBuilder.create()
            dialog.setTitle(getString(R.string.dialog_falha_title))
            dialog.show()
        }

        characterSearch.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Método não utilizado
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Método não utilizado
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    currentPage = 0
                    loadCharactersList(currentPage)
                    listFooter.visibility = View.VISIBLE
                    return
                }

                listFooter.visibility = View.GONE

                val query = s.toString().toLowerCase().trim()
                val filteredList = ArrayList<Character>()

                for (character in characters) {
                    val characterName = character.name.toLowerCase()
                    if (characterName.contains(query)) {
                        filteredList.add(character)
                    }
                }

                adapter = CharactersAdapter(filteredList, applicationContext)
                charactersRecyclerView = findViewById<RecyclerView>(R.id.charactersList).apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(applicationContext)
                }
                charactersRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })

        nextPage.setOnClickListener {
            if (currentPage < pages.size - 1) {
                currentPage++
                loadCharactersList(currentPage)
            }
        }

        previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                loadCharactersList(currentPage)
            }
        }
    }

    private fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    private fun fetchConfigs() {
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) {
            getRequestParameters()
        }
    }

    private fun getRequestParameters() {
        val ts = System.currentTimeMillis().toString()
        val privateApiKey = remoteConfig.getString("PRIVATE_KEY")
        val publicApiKey = remoteConfig.getString("PUBLIC_KEY")

        val input = ts + privateApiKey + publicApiKey
        val hash = input.md5()

        loadCharacters(ts, publicApiKey, hash)
    }

    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    private fun loadCharacters(ts: String, publicApiKey: String, hash: String) {
        val charactersCall = RetrofitInitializer(
            this
        )
            .characterService().getCharacters(ts, publicApiKey, hash)

        charactersCall.enqueue(object: Callback<CharacterResponse> {
            override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
                response.body()?.let {
                    charactersLoading.visibility = View.GONE
                    content.visibility = View.VISIBLE

                    characters = it.data.results

                    pages = characters.chunked(4)

//                    charactersRecyclerView = findViewById<RecyclerView>(R.id.charactersList).apply {
//                        setHasFixedSize(true)
//                        adapter = CharactersAdapter(pages[currentPage], applicationContext)
//                        layoutManager = LinearLayoutManager(applicationContext)
//                    }
                    loadCharactersList(currentPage)

                    footerRecyclerView = findViewById<RecyclerView>(R.id.footerPages).apply {
                        setHasFixedSize(true)
                        adapter = ListFooterAdapter(pages.size)
                    }
                }
            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                val dialogBuilder = AlertDialog.Builder(applicationContext)
                dialogBuilder.setMessage(getString(R.string.dialog_falha_message))
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.cancel()
                    }

                val dialog = dialogBuilder.create()
                dialog.setTitle(getString(R.string.dialog_falha_title))
                dialog.show()
            }
        })
    }

    private fun loadCharactersList(page: Int) {
        charactersRecyclerView = findViewById<RecyclerView>(R.id.charactersList).apply {
            setHasFixedSize(true)
            adapter = CharactersAdapter(pages[page], applicationContext)
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }
}
