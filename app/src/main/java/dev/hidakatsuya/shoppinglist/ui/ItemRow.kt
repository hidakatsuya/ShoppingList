package dev.hidakatsuya.shoppinglist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.hidakatsuya.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ItemRow(
    name: String,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onComplete) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Complete"
            )
        }
        BasicTextField(
            modifier = Modifier.weight(1f),
            value = name,
            onValueChange = onNameChange,
            singleLine = true
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Delete"
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun ItemRowPreview() {
    ShoppingListTheme {
        ItemRow(
            name = "Milk",
            onComplete = {},
            onDelete = {},
            onNameChange = {}
        )
    }
}
