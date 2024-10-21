package api

import config.AppConfig

//class ApiServer(val appConfig: AppConfig) {
//
//    fun get(relativeApiUrl: String, timeout: Int): String {
//        val client = HttpClient()
//        val response = client.get(appConfig.apiUrl + relativeApiUrl, timeout)
//        return response
//    }
//}