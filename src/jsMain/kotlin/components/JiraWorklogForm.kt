package components

import api.makeRequestWithData
import config.AppConfig
import csstype.*
import emotion.react.css
import mui.material.CircularProgress
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.h1
import react.useEffect
import react.useState


// region data

// server models
data class InputParameters(val expectedCount: Int, val worklogText: String)
data class OutputParameters(val rowsCount: Int)

data class OutputError(val message: String, val error: Throwable?)

// endregion


// region components

external interface FormParametersProps : Props {
    var defaultParameters: InputParameters?
    var appConfig: AppConfig
}


val JiraEnterWorklogForm = FC<FormParametersProps>("InputParametersForm") { rootProps ->

    var inputParameters by useState(rootProps.defaultParameters ?: InputParameters(0, ""))
    var outputParameters by useState(null as OutputParameters?)
    var errorParameters by useState(null as OutputError?)
    var queryActive by useState(false)
    var inputParamsUpdated by useState(false)

    useEffect(inputParamsUpdated) {
        // если обновлено - это вводятся значения
        if (inputParamsUpdated)
            return@useEffect

        // если "не обновлено" - значит сброшено значение флага и нужно обновить

        queryActive = true
        console.log("useEffect: inputParameters=${inputParameters}")

        val job = makeRequestWithData(rootProps.appConfig, "/text/check", inputParameters,
            onError = {
                errorParameters = OutputError(it.message ?: "Unknown error", it)
            },
            onFinally = {
                queryActive = false
            }) {
            outputParameters = JSON.parse(it)
            errorParameters = null
        }

        cleanup {
            job.cancel()
        }
    }

    ParametersInputs {
        onChangeExpectedCount = {
            inputParameters = inputParameters.copy(expectedCount = it)
            inputParamsUpdated = true
            console.log("onChangeExpectedCount: inputParameters=${inputParameters}")
        }
        onChangeWorklogText = {
            inputParameters = inputParameters.copy(worklogText = it)
            inputParamsUpdated = true
            console.log("onChangeWorklogText: inputParameters=${inputParameters}")
        }
    }

    QueryButtons {
        parameters = inputParameters
        appConfig = rootProps.appConfig
        onCheck = {
            inputParamsUpdated = false
        }
        onSubmit = {
            inputParamsUpdated = false
        }
    }

   ResultFields {
       this.queryActive = queryActive
       this.outputParameters = outputParameters
       this.errorParameters = errorParameters
   }

}

external interface InputParametersProps : Props {
    var onChangeExpectedCount: (Int) -> Unit
    var onChangeWorklogText: (String) -> Unit
}

val ParametersInputs = FC<InputParametersProps> { props ->
    div {
        css {
            // на всю ширину экрана
        }

        div {
            css {
                display = Display.flex
            }

            div {
                css {
                    margin = 20.px
                }
                // справа ввод параметра

                InputField {
                    name = "Введите ожидаемое количество записей"
                    defaultValue = 0.toString()
                    onChange = {
                        props.onChangeExpectedCount.invoke(it.toInt())
                    }
                }
            }
            div {
                css {
                    margin = 20.px
                }
                // слева ввод ворклога
                MultiLineInputField {
                    name = "Введите текст ворклога"
                    defaultValue = "---"
                    onChange = {
                        props.onChangeWorklogText.invoke(it)
                    }
                }
            }
        }
    }
}

external interface QueryParametersProps : Props {
    var parameters: InputParameters
    var appConfig: AppConfig
    var onCheck: () -> Unit
    var onSubmit: () -> Unit
}

val QueryButtons = FC<QueryParametersProps> { props ->

    div {
        css {
            // на всю ширину экрана
            margin = 20.px
            display = Display.flex
        }

        div {
            css {

            }
            // справа кнопка "проверить"
            myButton {
                text = "Проверить"
                onClick = {
                    props.onCheck()
                }

            }
        }
        div {
            // слева кнопка "отправить"
            myButton {
                text = "Отправить"
                onClick = {
                    props.onSubmit()
                }
            }
        }
    }
}

external interface ResultFieldsProps : Props {
    var errorParameters: OutputError?
    var outputParameters: OutputParameters?
    var queryActive: Boolean
}

val ResultFields = FC<ResultFieldsProps> { props ->

    div {
        css {
            // на всю ширину экрана
        }
        h1 {
            + "Результаты расчета:"
        }
        if (props.errorParameters != null) {
            div {
                css {
                    color = NamedColor.red
                }
                p {
                    + props.errorParameters!!.message
                }
                p {
                    + props.errorParameters!!.error!!.stackTraceToString()
                }
            }
        }
        div {
            // список параметров с результатами
            p {
                val resultsString = props.outputParameters?:"Нет данных"
                + "${resultsString}"
            }
        }
        if (props.queryActive) {
            div {
                // невидимый спиннер
                CircularProgress {
                    //variant = CircularProgressVariant.determinate
                    //value = 75
                }
            }
        } else {
            p {
                + "Спиннер скрыт"
            }
        }
    }
}

// endregion