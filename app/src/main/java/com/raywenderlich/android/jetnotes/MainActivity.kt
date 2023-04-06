package com.raywenderlich.android.jetnotes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.theme.JetNotesTheme
import com.raywenderlich.android.jetnotes.util.screens.NotesScreen
import com.raywenderlich.android.jetnotes.util.screens.SaveNoteScreen
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModelFactory
import com.topic2.android.notes.ui.screens.TrashScreen

/**
 * Main activity приложения.
 */

class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by viewModels(factoryProducer = {
    MainViewModelFactory(
      this,
      (application as JetNotesApplication).dependencyInjector.repository
    )
  })

  @ExperimentalMaterialApi
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      JetNotesTheme {
        MainActivityScreen(viewModel = viewModel)
      }
    }
  }
}

@Composable
@ExperimentalMaterialApi
private fun MainActivityScreen(viewModel: MainViewModel) {
  Surface{
    when (JetNotesRouter.currentScreen){
      is Screen.Notes -> NotesScreen(viewModel)
      is Screen.SaveNote -> SaveNoteScreen(viewModel)
      is Screen.Trash -> TrashScreen(viewModel)
    }
  }
}
