package dev.hidakatsuya.shoppinglist.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hidakatsuya.shoppinglist.ItemsViewModel
import dev.hidakatsuya.shoppinglist.R
import dev.hidakatsuya.shoppinglist.ui.ItemsList
import dev.hidakatsuya.shoppinglist.ui.theme.ShoppingListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    itemsViewModel: ItemsViewModel = viewModel()
) {
    ShoppingListTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(stringResource(id = R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More"
                            )
                        }
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Add, "Delete")
                }
            },
            content = { innerPadding ->
                Surface(modifier = Modifier.padding(innerPadding)) {
                    Content(modifier, itemsViewModel)
                }
            }
        )
    }
}

@Composable
private fun Content(modifier: Modifier, itemsViewModel: ItemsViewModel) {
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
