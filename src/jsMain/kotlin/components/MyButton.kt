package components

import config.AppConfig
import kotlinx.browser.window
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mui.material.Button
import react.*
import react.dom.html.ReactHTML.p

external interface ButtonProps : Props {
    var text: String
    var onClick: () -> Unit
}

val myButton = FC<ButtonProps> { props ->
    Button {
        onClick = { props.onClick() }
        + props.text
    }
}

