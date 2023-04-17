package workflow.tutorial

import com.squareup.workflow1.StatelessWorkflow
import com.squareup.workflow1.action
import workflow.tutorial.TodoListWorkflow.ListProps
import workflow.tutorial.TodoListWorkflow.Output
import workflow.tutorial.TodoListWorkflow.Output.Back
import workflow.tutorial.TodoListWorkflow.Output.SelectTodo
import workflow.tutorial.TodoWorkflow.TodoModel

object TodoListWorkflow : StatelessWorkflow<ListProps, Output, TodoListScreen>() {

  data class ListProps(
    val username: String,
    val todos: List<TodoModel>
  )

  sealed class Output {
    object Back : Output()
    data class SelectTodo(val index: Int) : Output()
  }

  override fun render(
    renderProps: ListProps,
    context: RenderContext
  ) = TodoListScreen(
      username = renderProps.username,
      todoTitles = renderProps.todos.map { it.title },
      onTodoSelected = { context.actionSink.send(selectTodo(it)) },
      onBack = { context.actionSink.send(onBack()) }
    )

  private fun selectTodo(index: Int) = action {
    setOutput(SelectTodo(index))
  }

  private fun onBack() = action {
    setOutput(Back)
  }
}
