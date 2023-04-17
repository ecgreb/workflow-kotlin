package workflow.tutorial

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action
import workflow.tutorial.TodoEditWorkflow.EditProps
import workflow.tutorial.TodoListWorkflow.ListProps
import workflow.tutorial.TodoWorkflow.Back
import workflow.tutorial.TodoWorkflow.State
import workflow.tutorial.TodoWorkflow.State.Step
import workflow.tutorial.TodoWorkflow.TodoProps

object TodoWorkflow : StatefulWorkflow<TodoProps, State, Back, List<Any>>() {

  data class TodoProps(val username: String)

  data class TodoModel(
    val title: String,
    val note: String
  )

  data class State(
    val todos: List<TodoModel>,
    val step: Step
  ) {
    sealed class Step {
      object List : Step()
      data class Edit(val index: Int) : Step()
    }
  }

  object Back

  override fun initialState(
    props: TodoProps,
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
    renderProps: TodoProps,
    renderState: State,
    context: RenderContext
  ): List<Any> {
    val todoListScreen = context.renderChild(
      TodoListWorkflow,
      props = ListProps(
        username = renderProps.username,
        todos = renderState.todos
      )
    ) { output ->
      when (output) {
        TodoListWorkflow.Output.Back -> onBack()
        is TodoListWorkflow.Output.SelectTodo -> editTodo(output.index)
      }
    }

    return when (val step = renderState.step) {
      Step.List -> listOf(todoListScreen)
      is Step.Edit -> {
        val todoEditScreen = context.renderChild(
          TodoEditWorkflow,
          EditProps(renderState.todos[step.index])
        ) { output ->
          when (output) {
            TodoEditWorkflow.Output.Discard -> discardChanges()
            is TodoEditWorkflow.Output.Save -> saveChanges(output.todo, step.index)
          }

        }
        return listOf(todoListScreen, todoEditScreen)
      }
    }
  }

  private fun onBack() = action {
    setOutput(Back)
  }

  private fun editTodo(index: Int) = action {
    // When a todo item is selected, edit it.
    state = state.copy(step = Step.Edit(index))
  }

  private fun discardChanges() = action {
    state = state.copy(step = Step.List)
  }

  private fun saveChanges(
    todo: TodoModel,
    index: Int
  ) = action {
    state = state.copy(
      todos = state.todos.toMutableList().also { it[index] = todo },
      step = Step.List
    )
  }

  override fun snapshotState(state: State): Snapshot? = Snapshot.write {
    TODO("Save state")
  }
}
