package workflow.tutorial

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.WorkflowAction
import com.squareup.workflow1.action
import workflow.tutorial.WelcomeWorkflow.LoggedIn
import workflow.tutorial.WelcomeWorkflow.State

object WelcomeWorkflow : StatefulWorkflow<Unit, State, LoggedIn, WelcomeScreen>() {

  data class LoggedIn(val username: String)

  data class State(
    val username: String
  )

  override fun initialState(
    props: Unit,
    snapshot: Snapshot?
  ): State = State(username = "")

  override fun render(
    renderProps: Unit,
    renderState: State,
    context: RenderContext
  ): WelcomeScreen = WelcomeScreen(
    username = renderState.username,
    onUsernameChanged = { context.actionSink.send(onUsernameChanged(it)) },
    onLoginTapped = { context.actionSink.send(onLogin()) }
  )

  private fun onUsernameChanged(username: String) =
    object : WorkflowAction<Unit, State, Nothing>() {
      override fun Updater.apply() {
        state = state.copy(username = username)
      }
    }

  private fun onLogin() = action {
    setOutput(LoggedIn(state.username))
  }

  override fun snapshotState(state: State): Snapshot? = Snapshot.write {
    TODO("Save state")
  }
}
