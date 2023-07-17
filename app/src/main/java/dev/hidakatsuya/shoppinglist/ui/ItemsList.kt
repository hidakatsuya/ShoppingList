package dev.hidakatsuya.shoppinglist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.hidakatsuya.shoppinglist.data.Item
import dev.hidakatsuya.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ItemsList(
    modifier: Modifier = Modifier,
    list: List<Item>,
    onDeleteItem: (Item) -> Unit,
    onCompleteItem: (Item) -> Unit,
    onChangeItemName: (Item, String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(
            items = list,
            key = { item -> item.id }
        ) { item ->
            ItemRow(
                name = item.name,
                onDelete = { onDeleteItem(item) },
                onNameChange = { changedName -> onChangeItemName(item, changedName) },
                onComplete = { onCompleteItem(item) }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun ItemsListPreview() {
    ShoppingListTheme {
        ItemsList(
            list = List(3) { i -> Item(i, "Task #$i", false) },
            onDeleteItem = {},
            onChangeItemName = { _, _ -> },
            onCompleteItem = {}
        )
    }
}
