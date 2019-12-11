package com.tgeanezini.mobile.codehero

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        remoteConfig = FirebaseRemoteConfig.getInstance()

        val remoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setFetchTimeoutInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings)

        fetchConfigs()

        // Mover para o fetchConfigs()
        val ts = System.currentTimeMillis().toString()
        val privateApiKey = remoteConfig.getString(PRIVATE_API_KEY)
        val publicApiKey = remoteConfig.getString(PUBLIC_API_KEY)
        val hash = createRequestHash(ts, privateApiKey, publicApiKey)


        // Mover para outro local
        val charactersCall = RetrofitInitializer(this)
            .characterService().getCharacters(
                ts,
                publicApiKey,
                hash)

        charactersCall.enqueue(object: Callback<CharacterResponse> {
            override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
                response.body()?.let {
                    val characters = it.data.result

                    recyclerView = findViewById<RecyclerView>(R.id.charactersList).apply {
                        setHasFixedSize(true)
                        adapter = CharactersAdapter(characters)
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

    private fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    private fun fetchConfigs() {
        remoteConfig.fetch().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                remoteConfig.activate()

                // Chamar funções de acesso ao server aqui dentro
            }
            else {
                Toast.makeText(this, "Falha ao obter configurações do app", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createRequestHash(ts: String, publicKey: String, privateKey: String) : String {
        val input = ts + privateKey + publicKey

        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(input.toByteArray())
            val bytes = md.digest()

            val md5 = StringBuffer()
            for (i in 0 .. bytes.size) {
                md5.append(Integer.toHexString(0xFF and bytes[i].toInt()))
            }

            return md5.toString()
        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()
        }

        return ""
    }
}
