package com.raywenderlich.android.jetnotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.jetnotes.data.repository.Repository
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View model used for storing the global app state.
 *
 * This view model is used for all screens.
 */
class MainViewModel(private val repository: Repository) : ViewModel() {

    val notesNotInTrash: LiveData<List<NoteModel>> by lazy{ repository.getAllNotesNotInTrash() }

    val notesInTrash by lazy { repository.getAllNotesInTrash() }

    private var _noteEntry =  MutableLiveData(NoteModel())
    val noteEntry: LiveData<NoteModel> = _noteEntry

    val colors: LiveData<List<ColorModel>> by lazy { repository.getAllColors() }

    private var _selectedNotes = MutableLiveData<List<NoteModel>>(listOf())

    val selectedNotes: LiveData<List<NoteModel>> = _selectedNotes

    fun onCreateNewNoteClick(){
        _noteEntry.value = NoteModel()
        JetNotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteClick(note: NoteModel){
        _noteEntry.value = note
        JetNotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteCheckedChange(
        note: NoteModel
    ) {
        viewModelScope.launch(
            Dispatchers.Default
        ) {
            repository.insertNote(note)
        }
    }

    fun onNoteSelected(note: NoteModel) {
        _selectedNotes.value = _selectedNotes.value!!.toMutableList().apply {
            if (contains(note)) {
                remove(note)
            } else {
                add(note)
            }
        }
    }

    fun restoreNotes(notes: List<NoteModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restoreNotesFromTrash(notes.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedNotes.value = listOf()
            }
        }
    }

    fun permanentlyDeleteNotes(notes: List<NoteModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteNotes(notes.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedNotes.value = listOf()
            }
        }
    }

    fun onNoteEntryChange(note: NoteModel){
        _noteEntry.value = note
    }

    fun saveNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertNote(note)

            withContext(Dispatchers.Main) {
                JetNotesRouter.navigateTo(Screen.Notes)
                _noteEntry.value = NoteModel()
            }
        }
    }

    fun moveNoteToTrash(note: NoteModel){
        viewModelScope.launch(Dispatchers.Default){
            repository.moveNoteToTrash(note.id)

            withContext(Dispatchers.Main){
                JetNotesRouter.navigateTo(Screen.Notes)
            }
        }
    }
}