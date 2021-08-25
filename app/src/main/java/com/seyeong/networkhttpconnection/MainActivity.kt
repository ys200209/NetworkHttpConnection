package com.seyeong.networkhttpconnection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seyeong.networkhttpconnection.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonRequest.setOnClickListener { // 버튼을 클릭하면
            CoroutineScope(Dispatchers.IO).launch { // 네트워크 작업을 요청하고 이를 백그라운드에서 처리하기 위해 디스패처 IO를 사용해서 CoroutineScope를 생성한다.
                try {
                    var urlText = binding.editUrl.text.toString()
                    if (!urlText.startsWith("https")) { // 입력된 url주소가 "https"로 시작하지 않는다면
                        urlText = "https://${urlText}" // "https://" 뒤에 url주소를 붙여 해당 주소로 시작하도록 설정한다.
                    }
                    var url = URL(urlText) // 주소를 URL 객체로 변환하고 변수에 저장.
                    val urlConnection = url.openConnection() as HttpURLConnection // openConnetion을 이용하여 서버와의 연결을 생성한다
                    // 그리고나서 as HttpURLConnection 으로 HttpURLConnection으로 형변환해줍니다. (반드시 필요)
                    // openConnection() 메서드는 URLConnection 이라는 추상 클래스를 반환하는데 이를 사용하기 위해서는
                    // 실제 구현 클래스인 HttpURLConnection 으로 변환하는 과정이 필요합니다.
                    urlConnection.requestMethod = "GET" // GET방식으로 요청 방식을 설정합니다.

                    if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) { // 응답이 정상적으로 연결되었다면 응답 데이터를 처리합니다.
                        val streamReader = InputStreamReader(urlConnection.inputStream) // 입력 스트림을 연결하고 버퍼에 담아서
                        val buffered = BufferedReader(streamReader) // 데이터를 읽을 준비를 합니다.

                        val content = StringBuilder()
                        while (true) {
                            val line = buffered.readLine()?: break // 더이상 읽어올 라인이 없으면 중지.
                            content.append(line) // 한줄씩 읽어 content에 더해준다.
                        }

                        buffered.close() // 연결 해제
                        urlConnection.disconnect() // 연결 해제

                        launch(Dispatchers.Main) { // textContent UI에 content값을 세팅해주기 위해서 Dispatchers.Main 으로 설정해준다.
                            binding.textContent.text = content.toString()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
}