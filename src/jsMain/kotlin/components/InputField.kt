package components

import dom.html.HTMLInputElement
import mui.material.Input
import react.FC
import react.Props
import react.dom.html.ReactHTML.p

external interface InputFieldProps : Props {
    var name: String
    var value: String
    var defaultValue: String
    var onChange: (String) -> Unit
}

val InputField = FC<InputFieldProps> { props ->
    p {
        + "${props.name}: "
    }
    Input {
        defaultValue = props.defaultValue
        onChange = {
            val newValue = (it.target as HTMLInputElement).value
            if (props.onChange != null)
                props.onChange(newValue)
        }
    }
}

val MultiLineInputField = FC<InputFieldProps> { props ->

    //var value by useState(props.defaultValue)

    p {
        + "${props.name}: "
    }
    Input {
        multiline = true
        rows = 10
        defaultValue = props.defaultValue

        onChange = {
            value = (it.target as HTMLInputElement).value
            if (props.onChange != null)
                props.onChange(value)
        }
    }
}