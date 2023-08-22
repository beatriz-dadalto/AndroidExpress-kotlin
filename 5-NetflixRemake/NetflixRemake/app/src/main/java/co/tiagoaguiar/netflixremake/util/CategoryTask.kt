package co.tiagoaguiar.netflixremake.util

import android.util.Log
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask {

    // fazer uma chamada paralela
    fun execute(url: String) {
        // nesse momento estamos utilizando a UI-Thread (1)
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {

            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null

            try {
                // nesse momento estamos utilizando a NOVA THREAD [processo paralelo (2)]
                val requestURL = URL(url) // abrir uma URL
                urlConnection =
                    requestURL.openConnection() as HttpsURLConnection // abrir a conexão
                urlConnection.readTimeout = 2000 // tempo de leitura (2 segundos)
                urlConnection.connectTimeout = 2000 // tempo para conexão (2 segundos)

                val statusCode = urlConnection.responseCode

                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream // HTTP devolve uma sequência de bytes

                // forma 1: simples e rápida
//                    val jsonAsString =
//                        stream.bufferedReader().use { it.readText() } // bytes para String
//                    Log.i("TESTE", jsonAsString)

                // forma 2: bytes para string
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                // JSON pronto para ser convertido em um DATA CLASS!
                val categories = toCategories(jsonAsString)
                Log.i("TESTE", categories.toString())

            } catch (e: IOException) {
                Log.e("TESTE", e.message ?: " Erro desconhecido", e)
            } finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
        }
    }

    private fun toCategories(jsonAsString: String): List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")

        // buscar categorias
        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)
            val title = jsonCategory.getString("title")
            // buscar filmes
            val jsonMovies = jsonCategory.getJSONArray("movie")
            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("corver_url")
                movies.add(Movie(id, coverUrl))
            }
            categories.add(Category(title, movies))
        }
        return categories
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)
            if (read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }
        return String(baos.toByteArray())
    }
}