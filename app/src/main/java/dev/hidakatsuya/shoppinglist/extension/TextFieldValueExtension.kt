package dev.hidakatsuya.shoppinglist.extension

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun TextFieldValue.copyWithMovingCursorToEndOf(text: String): TextFieldValue {
    return copy(selection = TextRange(text.length))
}

fun TextFieldValue.copyWithApplyingComposition(): TextFieldValue {
    return if (composition == null) {
        this
    } else {
        copy(composition = null)
    }
}
