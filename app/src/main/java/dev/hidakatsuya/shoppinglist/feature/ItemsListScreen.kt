package dev.hidakatsuya.shoppinglist.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hidakatsuya.shoppinglist.ItemsViewModel
import dev.hidakatsuya.shoppinglist.ui.ItemsList
import dev.hidakatsuya.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    itemsViewModel: ItemsViewModel = viewModel()
) {
    Column(modifier = modifier) {
        ItemsList(
            list = itemsViewModel.items,
            onDeleteItem = { item -> itemsViewModel.remove(item) },
            onCompleteItem = { item -> itemsViewModel.complete(item) },
            onChangeItemName = { item, newName -> itemsViewModel.changeName(item, newName) }
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun ShoppingListScreenPreview() {
    ShoppingListTheme {
        ShoppingListScreen()
    }
}
