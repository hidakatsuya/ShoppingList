package dev.hidakatsuya.shoppinglist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hidakatsuya.shoppinglist.R
import dev.hidakatsuya.shoppinglist.data.Item
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ItemsScreen(
    modifier: Modifier = Modifier,
    viewModel: ItemsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val itemListUiState by viewModel.itemListUiState.collectAsState()
    val editItemUiState = viewModel.editItemUiState
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = if (viewModel.editItemUiState.isEditing) {
            ModalBottomSheetValue.Expanded
        } else {
            ModalBottomSheetValue.Hidden
        },
        confirmValueChange = {
            if (it == ModalBottomSheetValue.Hidden) {
                viewModel.finishItemEditing()
            }
            true
        }
    )

    ModalBottomSheetLayout(
        modifier = modifier.padding(),
        sheetState = bottomSheetState,
        sheetContent = {
            Row(
                modifier = modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val saveItem = {
                    scope.launch {
                        viewModel.saveItem()
                        viewModel.finishItemEditing()
                    }
                }

                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    value = editItemUiState.details.name,
                    onValueChange = {
                        viewModel.updateEditItemUiState(editItemUiState.details.copy(name = it))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { saveItem() })
                )
                Button(onClick = { saveItem() }) {
                    Text("保存")
                }
            }

            LaunchedEffect(viewModel.editItemUiState.isEditing) {
                if (viewModel.editItemUiState.isEditing) {
                    focusRequester.requestFocus()
                    keyboard?.show()
                } else {
                    focusManager.clearFocus()
                    keyboard?.hide()
                }
            }
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                FloatingActionButton(
                    onClick = { viewModel.newItem() }
                ) {
                    Icon(Icons.Filled.Add, "Delete")
                }
            }
        ) { innerPadding ->
            ItemsBody(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                itemList = itemListUiState.items,
                onDeleteItem = {
                    scope.launch { viewModel.removeItem(it) }
                },
                onBuyItem = {
                    scope.launch { viewModel.changeItemBought(it) }
                },
                onEditItem = { viewModel.editItem(it) }
            )
        }
    }
}

@Composable
private fun ItemsBody(
    itemList: List<Item>,
    modifier: Modifier = Modifier,
    onDeleteItem: (Item) -> Unit,
    onBuyItem: (Item) -> Unit,
    onEditItem: (Item) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (itemList.isEmpty()) {
            Text(
                text = "No items",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            ItemList(
                list = itemList,
                onDeleteItem = onDeleteItem,
                onCompleteItem = onBuyItem,
                onEditItem = onEditItem
            )
        }
    }
}

@Composable
private fun ItemList(
    modifier: Modifier = Modifier,
    list: List<Item>,
    onDeleteItem: (Item) -> Unit,
    onCompleteItem: (Item) -> Unit,
    onEditItem: (Item) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(
            items = list,
            key = { item -> item.id }
        ) { item ->
            ItemRow(
                name = item.name,
                onDeleteClick = { onDeleteItem(item) },
                onCompleteClick = { onCompleteItem(item) },
                onNameClick = { onEditItem(item) }
            )
        }
    }
}

@Composable
private fun ItemRow(
    name: String,
    onDeleteClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onNameClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCompleteClick) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Complete"
            )
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onNameClick),
            text = name
        )
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Delete"
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun ItemsPreview() {
    ItemsBody(
        itemList = listOf(Item(id = 1, name = "Milk", bought = 0)),
        onDeleteItem = {},
        onBuyItem = {},
        onEditItem = {}
    )
}
