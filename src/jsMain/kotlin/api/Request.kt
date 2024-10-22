package api

import data.AppConfig
import data.serializeJson
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.js.jso
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import web.http.Headers
import web.http.Request
import web.http.RequestInit

// bad, but works
fun makeGetRequest(config: AppConfig, relativeUrl: String,
                   onError: (Throwable) -> Unit = ::defaultErrorCallback,
                   result: (String) -> Unit): Job {

    return MainScope().launch {
        window.fetch("${config.apiUrl}${relativeUrl}").then {
            it.text()
                .then { fetched -> result.invoke(fetched) }
                .catch { err -> onError.invoke(err) }
        }
    }
}

inline fun <reified T> makeRequestWithData(
    config: AppConfig,
    relativeUrl: String,
    queryData: T,
    crossinline onError: (Throwable) -> Unit = ::defaultErrorCallback,
    crossinline onFinally: () -> Unit = { },
    crossinline result: (String) -> Unit): Job {

    return MainScope().launch {
        val requestInit = jso<RequestInit> {
            body = serializeJson(queryData)
            method = "POST"
            headers = Headers().apply {
                set("Content-Type", "application/json")
            }
        }

        val request = Request(config.apiUrl + relativeUrl)
        console.log("request=${request.url}")
        console.log("requestInit=${requestInit.body}")
        window.fetch(request, requestInit)
            .then {
                it.text()
                    .then { fetched -> result.invoke(fetched) }
                    .catch { err -> onError.invoke(err) }
                    .finally { onFinally.invoke() }
        }
    }
}

fun defaultErrorCallback(it: Throwable) {
    val message = if (it.message == null) "Unknown error" else
        "message=${it.message}, cause=${it.cause}, stackTrace=${it.stackTraceToString()}"
    window.alert(message)
}

