data class Note(val name: String, val content: String)

data class Archive(val name: String, val noteList: MutableList<Note>)