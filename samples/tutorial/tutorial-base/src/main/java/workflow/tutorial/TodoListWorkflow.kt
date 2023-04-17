package workflow.tutorial

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action
import workflow.tutorial.TodoEditWorkflow.EditProps
import workflow.tutorial.TodoEditWorkflow.Output.Discard
import workflow.tutorial.TodoEditWorkflow.Output.Save
import workflow.tutorial.TodoListWorkflow.Back
import workflow.tutorial.TodoListWorkflow.ListProps
import workflow.tutorial.TodoListWorkflow.State
import workflow.tutorial.TodoListWorkflow.State.Step

object TodoListWorkflow : StatefulWorkflow<ListProps, State, Back, List<Any>>() {

  data class ListProps(val username: String)

  data class TodoModel(
    val title: String,
    val note: String
  )

  object Back

  data class State(
    val todos: List<TodoModel>,
    val step: Step
  ) {
    sealed class Step {
      object List : Step()
      data class Edit(val index: Int) : Step()
    }
  }

  override fun initialState(
    props: ListProps,
    snapshot: Snapshot?
  ) = State(
    todos = listOf(
      TodoModel(
        title = "Take the cat for a walk",
        note = "Cats really need their outside sunshine time. Don't forget to walk the cat."
      )
    ),
    step = Step.List
  )

  override fun render(
    renderProps: ListProps,
    renderState: State,
    context: RenderContext
  ): List<Any> {
    val titles = renderState.todos.map { it.title }
    val todoListScreen = TodoListScreen(
      username = renderProps.username,
      todoTitles = titles,
      onTodoSelected = { context.actionSink.send(selectTodo(it)) },
      onBack = { context.actionSink.send(onBack()) }
    )

    return when (val step = renderState.step) {
      Step.List -> listOf(todoListScreen)
      is Step.Edit -> {
        val todoEditScreen = context.renderChild(
          TodoEditWorkflow,
          props = EditProps(renderState.todos[step.index])
        ) { output ->
          when (output) {
            Discard -> discardChanges()
            is Save -> saveChanges(output.todo, step.index)
          }
        }
        return listOf(todoListScreen, todoEditScreen)
      }
    }
  }

  private fun selectTodo(index: Int) = action {
    state = state.copy(step = Step.Edit(index))
  }

  private fun onBack() = action {
    setOutput(Back)
  }

  private fun discardChanges() = action {
    state = state.copy(step = Step.List)
  }

  private fun saveChanges(todo: TodoModel, index: Int) = action {
    state = state.copy(
      todos = state.todos.toMutableList().also { it[index] = todo },
      step = Step.List
    )
  }

  override fun snapshotState(state: State): Snapshot? = null
}
