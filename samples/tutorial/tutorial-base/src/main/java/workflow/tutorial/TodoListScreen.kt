package workflow.tutorial

data class TodoListScreen(
  val username: String,
  val todoTitles: List<String>,
  val onTodoSelected: (Int) -> Unit,
  val onBack: () -> Unit
)
