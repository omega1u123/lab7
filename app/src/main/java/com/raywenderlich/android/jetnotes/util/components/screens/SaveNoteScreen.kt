package com.raywenderlich.android.jetnotes.util.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raywenderlich.android.jetnotes.R
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
import com.raywenderlich.android.jetnotes.domain.model.NEW_NOTE_ID
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.util.components.NoteColor
import com.raywenderlich.android.jetnotes.util.fromHex
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
@ExperimentalMaterialApi
fun SaveNoteScreen(viewModel: MainViewModel){
    val noteEntry: NoteModel by viewModel.noteEntry.observeAsState(NoteModel())

    val colors: List<ColorModel> by viewModel.colors
        .observeAsState(listOf())

    val bottomDrawerState: BottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val moveNoteToTrashDialogShownState: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(
        onBack = {
            if(bottomDrawerState.isOpen){
                coroutineScope.launch{ bottomDrawerState.close()}
            } else{
                JetNotesRouter.navigateTo(Screen.Notes)
            }
        }
    )
    Scaffold(topBar = {
        val isEditingMode: Boolean = noteEntry.id != NEW_NOTE_ID
        SaveNoteTopAppBar(
            isEditingMode = isEditingMode,
            onBackClick = {
                JetNotesRouter.navigateTo(Screen.Notes)
            },
            onSaveNoteClick = {
                viewModel.saveNote(noteEntry)
            },
            onOpenColorPickerClick = {
                coroutineScope.launch{ bottomDrawerState.open() }
            },
            onDeleteNoteClick = {
                moveNoteToTrashDialogShownState.value = true
            }
        )
    },
        content = {
            BottomDrawer(
                drawerState = bottomDrawerState,
                drawerContent = {
                    ColorPicker(
                        color = colors,
                        onColorSelect = { color ->
                            val newNoteEntry = noteEntry.copy(color = color)
                            viewModel.onNoteEntryChange(newNoteEntry)
                        }
                    )
                },
                content = {
                    SaveNoteContent(
                        note = noteEntry ,
                        onNoteChange = { updateNoteEntry ->
                            viewModel.onNoteEntryChange(updateNoteEntry)
                        }
                    )
                },
            )
            if (moveNoteToTrashDialogShownState.value){
                AlertDialog(
                    onDismissRequest = {
                        moveNoteToTrashDialogShownState.value = false
                    },
                    title = {
                        Text(text = stringResource(id = R.string.move))
                    },
                    text = {
                        Text(text = stringResource(id = R.string.sure) + " " +
                                stringResource(id = R.string.move_2)
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.moveNoteToTrash(noteEntry)
                        }) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            moveNoteToTrashDialogShownState.value = false
                        }) {
                            Text(text = stringResource(id = R.string.dismiss))
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun SaveNoteTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSaveNoteClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onDeleteNoteClick: () -> Unit
){
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.save_note) ,
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Save Note Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveNoteClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = "Save Note"
                )
            }
            IconButton(onClick = onOpenColorPickerClick){
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_color_lens_24
                    ),
                    contentDescription = "Open Color Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            if (isEditingMode){
                IconButton(
                    onClick = onDeleteNoteClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Note Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label)},
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}

@Composable
private fun NoteCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.checked), modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PickedColor(color: ColorModel){
    Row (
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.picked_color), modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        NoteColor(
            color = Color.fromHex(color.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onColorSelect(color)
                }
            )
    ) {
        NoteColor(
            modifier = Modifier
                .padding(10.dp),
            color = Color.fromHex(color.hex) ,
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun ColorPicker(
    color: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.picked_color_2),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ){
            items(
                color.size
            ){
                    itemIndex ->
                val color = color[itemIndex]
                ColorItem(
                    color = color,
                    onColorSelect = onColorSelect
                )
            }
        }

    }

}

@Composable
private fun SaveNoteContent(
    note: NoteModel,
    onNoteChange: (NoteModel) -> Unit
){
    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = stringResource(id = R.string.header),
            text = note.title ,
            onTextChange = { newTitle ->
                onNoteChange.invoke(note.copy(title = newTitle))
            }
        )
        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = stringResource(id = R.string.body),
            text = note.content,
            onTextChange = { newContent ->
                onNoteChange.invoke(note.copy(content = newContent))

            }
        )
        val canBeCheckedOff: Boolean = note.isCheckedOff != null

        NoteCheckOption(
            isChecked = canBeCheckedOff,
            onCheckedChange = { canBeCheckedOffNewValue ->
                onNoteChange.invoke(note.copy(isCheckedOff = canBeCheckedOffNewValue))
            }
        )
        PickedColor(color = note.color)
    }
}

@Preview(showBackground = true)
@Composable
fun SaveNoteTopAppBarPreview(){
    SaveNoteTopAppBar(
        isEditingMode = true,
        onBackClick = {  },
        onSaveNoteClick = { },
        onOpenColorPickerClick = {  },
        onDeleteNoteClick = {  }
    )
}

@Preview(showBackground = true)
@Composable
fun ColorItemPreview(){
    ColorItem(ColorModel.DEFAULT){
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPickerPreview(){
    ColorPicker(
        color = listOf(
            ColorModel.DEFAULT,
            ColorModel.DEFAULT,
            ColorModel.DEFAULT
        )
    ){

    }
}
@Preview(showBackground = true)
@Composable
fun PickedColorPreview(){
    PickedColor(ColorModel.DEFAULT)
}

@Preview(showBackground = true)
@Composable
fun NoteCheckOptionPreview(){
    NoteCheckOption(false){}
}

@Preview(showBackground = true)
@Composable
fun ContentTextFieldPreview(){
    ContentTextField(
        label = stringResource(id = R.string.header),
        text = "",
        onTextChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SaveNoteContentPreview(){
    SaveNoteContent(
        note = NoteModel(
            title = stringResource(id = R.string.header),
            content = stringResource(id = R.string.body)),
        onNoteChange = {}
    )
}