package com.raywenderlich.android.jetnotes.util.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.android.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.util.components.AppDrawer
import com.raywenderlich.android.jetnotes.util.components.Note
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import com.raywenderlich.android.jetnotes.R

@Composable
fun rememberDrawerState(
    initialValue: DrawerValue,
    confirmStateChange: (DrawerValue) -> Boolean = { true }
): DrawerState
{
    return rememberSaveable(saver = DrawerState.Saver(confirmStateChange)) {
        DrawerState(initialValue,confirmStateChange)
    }
}
@Composable
fun rememberScaffoldState(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): ScaffoldState = remember{
    ScaffoldState(drawerState, snackbarHostState)
}

@Composable
fun NotesScreen(viewModel: MainViewModel) {
    val notes: List<NoteModel> by viewModel
        .notesNotInTrash
        .observeAsState(listOf())
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.notes),
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Drawer Button"
                        )
                    }
                }
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Notes,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateNewNoteClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Note Button"
                    )
                }
            )
        },
        content = {
            if (notes.isNotEmpty()){
                NotesList(
                    notes = notes,
                    onNoteCheckedChange = {
                        viewModel.onNoteCheckedChange(it) },
                    onNoteClick = { viewModel.onNoteClick(it) }
                )
            }
        }
    )
}

@Composable
private fun NotesList(
    notes: List<NoteModel>,
    onNoteCheckedChange: (NoteModel) -> Unit,
    onNoteClick: (NoteModel) -> Unit,
) {
    LazyColumn {
        items(count = notes.size){noteIndex->
            val note = notes[noteIndex]
            Note(
                note = note,
                onNoteClick = onNoteClick,
                onNoteCheckedChange = onNoteCheckedChange,
                isSelected = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesListPreview(){
    NotesList(
        notes = listOf(
            NoteModel(1, "Заголовок 1", "Описание 1", null),
            NoteModel(1, "Заголовок 2", "Описание 2", false),
            NoteModel(1, "Заголовок 3", "Описание 3", true)
        ),
        onNoteCheckedChange = {}, onNoteClick = {}
    )
}