package workflow.tutorial

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.workflow1.ui.ViewRegistry
import com.squareup.workflow1.ui.WorkflowLayout
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow1.ui.backstack.BackStackContainer
import com.squareup.workflow1.ui.renderWorkflowIn
import kotlinx.coroutines.flow.StateFlow

@OptIn(WorkflowUiExperimentalApi::class)
private val viewRegistry = ViewRegistry(
  BackStackContainer,
  WelcomeLayoutRunner,
  TodoListLayoutRunner,
  TodoEditLayoutRunner
)

class TutorialActivity : AppCompatActivity() {

  @OptIn(WorkflowUiExperimentalApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val tutorialViewModel: TutorialViewModel by viewModels()

    setContentView(
      WorkflowLayout(this).apply { start(tutorialViewModel.renderings, viewRegistry) }
    )
  }
}

class TutorialViewModel(savedState: SavedStateHandle) : ViewModel() {
  @OptIn(WorkflowUiExperimentalApi::class) val renderings: StateFlow<Any> by lazy {
    renderWorkflowIn(
      workflow = RootWorkflow,
      scope = viewModelScope,
      savedStateHandle = savedState
    )
  }
}
