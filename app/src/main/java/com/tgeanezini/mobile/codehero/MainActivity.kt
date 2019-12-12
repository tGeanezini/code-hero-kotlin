package com.tgeanezini.mobile.codehero

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_characters.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var remoteConfig: FirebaseRemoteConfig
    lateinit var characters: List<Character>
    lateinit var adapter: CharactersAdapter

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
                val query = s.toString().toLowerCase().trim()
                val filteredList = ArrayList<Character>()

                for (character in characters) {
                    val characterName = character.name.toLowerCase()
                    if (characterName.contains(query)) {
                        filteredList.add(character)
                    }
                }

                adapter = CharactersAdapter(filteredList, applicationContext)
                recyclerView = findViewById<RecyclerView>(R.id.charactersList).apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(applicationContext)
                }
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
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
        val charactersCall = RetrofitInitializer(this)
            .characterService().getCharacters(
                ts,
                publicApiKey,
                hash)

        charactersCall.enqueue(object: Callback<CharacterResponse> {
            override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
                response.body()?.let {
                    charactersLoading.visibility = View.GONE
                    content.visibility = View.VISIBLE

                    characters = it.data.results

                    recyclerView = findViewById<RecyclerView>(R.id.charactersList).apply {
                        setHasFixedSize(true)
                        adapter = CharactersAdapter(characters, applicationContext)
                        layoutManager = LinearLayoutManager(applicationContext)
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
}
