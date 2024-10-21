package api

import config.AppConfig
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.js.jso
import web.http.Headers
import web.http.Request
import web.http.RequestInit

// bad, but works
fun makeGetRequest(config: AppConfig, relativeUrl: String,
                   onError: (Throwable) -> Unit = defaultErrorCallback,
                   result: (String) -> Unit): Job {

    return MainScope().launch {
        window.fetch("${config.apiUrl}${relativeUrl}").then {
            it.text()
                .then { fetched -> result.invoke(fetched) }
                .catch { err -> onError.invoke(err) }
        }
    }
}

fun <T> makeRequestWithData(
    config: AppConfig,
    relativeUrl: String,
    queryData: T,
    onError: (Throwable) -> Unit = defaultErrorCallback,
    onFinally: () -> Unit = { },
    result: (String) -> Unit): Job {

    return MainScope().launch {
        val requestInit = jso<RequestInit> {
            body = JSON.stringify(queryData)
            method = "POST"
            headers = Headers().apply {
                set("Content-Type", "application/json")
            }
        }
        console.log("queryData=${queryData}")
        val request = Request(config.apiUrl + relativeUrl, requestInit)
        window.fetch(request)
            .then {
                it.text()
                    .then { fetched -> result.invoke(fetched) }
                    .catch { err -> onError.invoke(err) }
                    .finally { onFinally.invoke() }
        }
    }
}


val defaultErrorCallback: (Throwable) -> Unit = {
    val message = if (it.message == null) "Unknown error" else
        "message=${it.message}, cause=${it.cause}, stackTrace=${it.stackTraceToString()}"
    window.alert(message)
}
