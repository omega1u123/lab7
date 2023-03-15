package com.raywenderlich.android.jetnotes

import NotesScreen
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.raywenderlich.android.jetnotes.theme.JetNotesTheme
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModelFactory

/**
 * Main activity приложения.
 */
class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by viewModels(factoryProducer = {
    MainViewModelFactory(
      this,
      (application as NotesApplication).dependencyInjector.repository
    )
  })

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      JetNotesTheme {
        NotesScreen(viewModel = viewModel)
        /*val coroutineScope = rememberCoroutineScope()
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        Scaffold(
          scaffoldState = scaffoldState,
          drawerContent = {
            AppDrawer(
              currentScreen = Screen.Notes,
              closeDrawerAction = {
                coroutineScope.launch{
                  scaffoldState.drawerState.close()
                }
              }
            )
          },
          content = {
            Note()
          }
        )*/
      }
    }
  }
}
