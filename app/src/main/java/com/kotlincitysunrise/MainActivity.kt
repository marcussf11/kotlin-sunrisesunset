package com.kotlincitysunrise

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_getInfo.setOnClickListener { getSunset(et_cityName.text.toString()) }
    }

    private fun getSunset(city:String){

         var city  = city
         val url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+city+"%2C%20df%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
         WeatherAsyncTask().execute(url)
    }

    inner class WeatherAsyncTask:AsyncTask<String, String, String>(){

        override fun onPreExecute() {
        }

        override fun doInBackground(vararg params: String?): String {
            try{
                val url = URL(params[0])
                val urlConnetc = url.openConnection() as HttpURLConnection
                urlConnetc.connectTimeout=7000

                var inString = converteStreamToString(urlConnetc.inputStream)
                publishProgress(inString)

            }catch (ex:Exception){
                ex.printStackTrace()
            }

            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try{
                var json = JSONObject(values[0])
                val query = json.getJSONObject("query")
                val results = query.getJSONObject("results")
                val channel = results.getJSONObject("channel")
                val astronomy = channel.getJSONObject("astronomy")
                val sunrise = astronomy.getString("sunrise")
                val sunset = astronomy.getString("sunset")

                tx_sunrise.text = "Sunrise: "+ sunrise
                tx_sunset.text = "Sunset "+ sunset

            }catch (ex:Exception){
                ex.printStackTrace()
            }
        }

        override fun onPostExecute(result: String?) {

        }
    }

    private fun converteStreamToString(inputStream:InputStream):String{
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line:String
        var allString:String = ""

        try{
            line = bufferReader.readLine()
            while(line != null){
                if(line != null){
                    allString += line
                }
                line = bufferReader.readLine()
            }
            inputStream.close()

        }catch (ex:Exception){
            ex.printStackTrace()
        }

        return allString
    }
}
