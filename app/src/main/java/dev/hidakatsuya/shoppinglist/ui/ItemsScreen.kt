package dev.hidakatsuya.shoppinglist.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dev.hidakatsuya.shoppinglist.R
import dev.hidakatsuya.shoppinglist.data.Item
import dev.hidakatsuya.shoppinglist.extension.copyWithApplyingComposition
import dev.hidakatsuya.shoppinglist.extension.copyWithMovingCursorToEndOf
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun ItemsScreen(
    modifier: Modifier = Modifier,
    viewModel: ItemsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val itemListUiState by viewModel.itemListUiState.collectAsStateWithLifecycle()
    val editItemUiState by viewModel.editItemUiState.collectAsStateWithLifecycle()

    val itemListScrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = { TopBar() },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            NewItemButton { viewModel.newItem() }
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            ItemsBody(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                isItemEmpty = itemListUiState.items.isEmpty()
            ) {
                ItemList(
                    list = itemListUiState.items,
                    state = itemListScrollState,
                    onDeleteItem = {
                        scope.launch { viewModel.removeItem(it) }
                    },
                    onCompleteItem = {
                        scope.launch { viewModel.changeItemBought(it) }
                    },
                    onEditItem = { viewModel.editItem(it) }
                )
            }
        }
    }

    if (editItemUiState.isEditing) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.finishItemEditing() },
            sheetState = bottomSheetState,
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            EditItemName(
                name = editItemUiState.details.name,
                canSave = editItemUiState.isValid,
                onSave = {
                    scope.launch {
                        viewModel.saveItem()

                        if (editItemUiState.isNew) {
                            itemListScrollState.animateScrollToItem(index = 0)
                        }
                    }
                },
                onNameChange = {
                    viewModel.updateEditItemUiState(editItemUiState.details.copy(name = it))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    val context = LocalContext.current

    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(id = R.string.app_name),
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
            )
        },
        actions = {
            var menuExpanded by remember { mutableStateOf(false) }

            IconButton(
                onClick = { menuExpanded = true },
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.menu_more_description)
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.open_source_licenses)) },
                    onClick = {
                        context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                        menuExpanded = false
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditItemName(
    name: String,
    canSave: Boolean,
    onSave: () -> Unit,
    onNameChange: (newName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var nameValue by remember { mutableStateOf(TextFieldValue(name)) }
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier.padding(
            vertical = 16.dp,
            horizontal = 24.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            singleLine = true,
            value = nameValue,
            onValueChange = {
                val completedName = it.text.replace("\n", "")
                onNameChange(completedName)
                nameValue = it.copy(text = completedName)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSave() }),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = nameValue.text,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    placeholder = {
                        Text(stringResource(R.string.item_name_placeholder))
                    },
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                        start = 0.dp,
                        end = 0.dp,
                    )
                ) {
                    Box(Modifier)
                }
            }
        )
        Button(
            enabled = canSave,
            onClick = {
                // Apply text composition before saving.
                // Otherwise, the text composition will be applied on save
                // and the onValueChange event will be fired,
                // and the bottom sheet will remain visible by setting isEditing to true.
                nameValue = nameValue.copyWithApplyingComposition()
                onSave()
            }
        ) {
            Text(stringResource(R.string.save_item))
        }
    }

    LaunchedEffect(Unit) {
        if (name.isNotEmpty()) {
            nameValue = nameValue.copyWithMovingCursorToEndOf(name)
        }
        focusRequester.requestFocus()
        keyboard?.show()
    }
}

@Composable
private fun NewItemButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Icon(
            Icons.Filled.Add,
            stringResource(R.string.button_item_delete_label)
        )
    }
}

@Composable
private fun ItemsBody(
    isItemEmpty: Boolean,
    modifier: Modifier = Modifier,
    listContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isItemEmpty) {
            Text(
                text = "No items",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            listContent()
        }
    }
}

@Composable
private fun ItemList(
    modifier: Modifier = Modifier,
    state: LazyListState,
    list: List<Item>,
    onDeleteItem: (Item) -> Unit,
    onCompleteItem: (Item) -> Unit,
    onEditItem: (Item) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
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
    onNameClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IconButton(onClick = onCompleteClick) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = stringResource(R.string.button_complete_description)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .clickable(
                    onClick = onNameClick,
                    indication = null,
                    interactionSource = interactionSource
                ),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.button_delete_description)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun ItemListPreview() {
    ItemList(
        state = rememberLazyListState(),
        list = listOf(
            Item(id = 1, name = "Milk", bought = 0),
            Item(id = 2, name = "MilkMilkMilkMilkMilkMilkMilkMilkMilkMilkMilk", bought = 0)
        ),
        onDeleteItem = {},
        onCompleteItem = {},
        onEditItem ={}
    )
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun EditItemNamePreview() {
    EditItemName(
        name = "Milk",
        canSave = true,
        onSave = {},
        onNameChange = {}
    )
}
