package workflow.tutorial

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow1.ui.backstack.BackStackScreen
import com.squareup.workflow1.ui.backstack.toBackStackScreen
import workflow.tutorial.RootWorkflow.State
import workflow.tutorial.RootWorkflow.State.Todo
import workflow.tutorial.RootWorkflow.State.Welcome
import workflow.tutorial.TodoWorkflow.TodoProps

@OptIn(WorkflowUiExperimentalApi::class)
object RootWorkflow : StatefulWorkflow<Unit, State, Nothing, BackStackScreen<Any>>() {

  sealed class State {
    object Welcome : State()
    data class Todo(val username: String) : State()
  }

  override fun initialState(
    props: Unit,
    snapshot: Snapshot?
  ): State = Welcome

  override fun render(
    renderProps: Unit,
    renderState: State,
    context: RenderContext
  ): BackStackScreen<Any> {
    val backstackScreens = mutableListOf<Any>()
    val welcomeScreen = context.renderChild(WelcomeWorkflow, Unit) { output ->
      login(output.username)
    }
    backstackScreens += welcomeScreen

    when (renderState) {
      is Welcome -> {
      }
      is Todo -> {
        val todoListScreens = context.renderChild(
          TodoWorkflow,
          TodoProps(username = renderState.username)
        ) {
          logout()
        }
        backstackScreens.addAll(todoListScreens)
      }
    }

    return backstackScreens.toBackStackScreen()
  }

  override fun snapshotState(state: State): Snapshot? = null

  private fun login(username: String) = action {
    state = Todo(username)
  }

  private fun logout() = action {
    state = Welcome
  }
}
