package workflow.tutorial

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action
import workflow.tutorial.TodoListWorkflow.Back
import workflow.tutorial.TodoListWorkflow.ListProps
import workflow.tutorial.TodoListWorkflow.State

object TodoListWorkflow : StatefulWorkflow<ListProps, State, Back, TodoListScreen>() {

  data class ListProps(val username: String)

  data class TodoModel(
    val title: String,
    val note: String
  )

  object Back

  data class State(
    val todos: List<TodoModel>
  )

  override fun initialState(
    props: ListProps,
    snapshot: Snapshot?
  ): State = State(
    listOf(
      TodoModel(
        title = "Take the cat for a walk",
        note = "Cats really need their outside sunshine time. Don't forget to walk the cat."
      )
    )
  )

  override fun render(
    renderProps: ListProps,
    renderState: State,
    context: RenderContext
  ): TodoListScreen {
    val titles = renderState.todos.map { it.title }
    val username = renderProps.username
    return TodoListScreen(
      username = username,
      todoTitles = titles,
      onTodoSelected = {},
      onBack = { context.actionSink.send(onBack()) }
    )
  }

  private fun onBack() = action {
    setOutput(Back)
  }

  override fun snapshotState(state: State): Snapshot? = null
}
