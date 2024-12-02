abstract class Menu {

    abstract val menuItems: List<String>
    abstract val actions: List<() -> Unit>
    abstract val screenName: String

    fun printMenu() {
        println("\n$screenName")
        println("Выберите действие, введя его номер без пробелов:")
        menuItems.forEachIndexed { ind, str ->
            println("${ind + 1} $str")
        }
        actions[(takeInput(menuItems.size)) - 1].invoke()
    }

    fun takeInput(size: Int): Int {
        var input: Int? = null
        var correctInpt = false
        while (!correctInpt) {
            input = readln().toIntOrNull()
            if (input == null) {
                println("Некорректный ввод - для ввода допустимы только цифры! \n Повторите ввод.")
            } else {
                if (input !in 1..size) {
                    println("Данный пункт меню отсутствует.\n Повторите ввод.")
                } else {
                    correctInpt = true
                }
            }
        }
        return input!!.toInt()
    }
}

class MainMenu(private val navigator: Navigator) : Menu() {

    override val menuItems: List<String> =
        listOf("Создать архив", "Просмотреть уже созданный архив", "Выйти из программы")

    override val screenName = "Главный экран приложения"

    val archiveList: MutableList<Archive> = mutableListOf()

    override val actions: List<() -> Unit> = listOf(
        { addArchive() },
        { showArchiveList() },
        { navigator.back() }
    )

    private fun addArchive() {
        println("Введите название для создаваемого архива:")
        var input = ""
        while (input.isBlank()) {
            input = readln()
            if (input.isBlank()) {
                println("Название архива должно состоять как минимум из одного символа помимо пробелов.\nПовторите ввод.")
            } else {
                archiveList.add(Archive(input, mutableListOf()))
                println("Архив '$input' успешно добавлен.")
                printMenu()
            }
        }
    }

    private fun showArchiveList() {
        if (archiveList.size > 0) {
            println("Выберите номер архива из списка:")
            archiveList.forEachIndexed { index, archive -> println("${index + 1} ${archive.name}") }
            println("${archiveList.size + 1} Назад")
            val pickedList = takeInput(archiveList.size + 1) - 1
            if (pickedList == archiveList.size) {
                printMenu()
            } else {
                navigator.openArchiveMenu(pickedList)
            }

        } else {
            println("Еще не создано ни одного архива.")
            printMenu()
        }

    }
}

class ArchiveMenu(
    private val navigator: Navigator,
    private val archiveList: MutableList<Archive>,
    private var currentArchive: Int
) : Menu() {
    override val menuItems: List<String> = listOf(
        "Просмотреть и выбрать заметки",
        "Добавить новую заметку в архив",
        "Выход на предыдущий экран"
    )

    override val actions: List<() -> Unit>
        get() = listOf({ showNotes() }, { addNote() }, { navigator.back() })
    override val screenName: String = "Меню архива"

    private fun showNotes() {
        if (archiveList[currentArchive].noteList.size > 0) {
            println("Выберите номер заметки для просмотра из списка:")
            archiveList[currentArchive].noteList.forEachIndexed { index, note -> println("${index + 1} ${note.name}") }
            println("${archiveList[currentArchive].noteList.size + 1} Назад")
            val pickedNote = takeInput(archiveList[currentArchive].noteList.size + 1) - 1
            if (pickedNote != archiveList[currentArchive].noteList.size) {
                with(archiveList[currentArchive].noteList[pickedNote]) {
                    println("Название заметки: $name")
                    println("Текст заметки: \n${content}")
                }
            }
        } else {
            println("В данном архиве нет ни одной заметки.")
        }
        printMenu()
    }

    private fun addNote() {
        println("Введите название для заметки:")
        var input = ""
        while (input.isBlank()) {
            input = readln()
            if (input.isBlank()) {
                println("Название заметки должно состоять как минимум из одного символа помимо пробелов.\nПовторите ввод.")
            } else {
                var input2 = ""
                while (input2.isBlank()) {
                    println("Введите текст заметки:")
                    input2 = readln()
                    if (input2.isBlank()) println("Текст заметки должен состоять как минимум из одного символа помимо пробелов.\nПовторите ввод.")
                }
                archiveList[currentArchive].noteList.add(Note(input, input2))
                println("Заметка '$input' успешно добавлена.")
                printMenu()
            }
        }
    }
}

class Navigator {
    private val menus: MutableList<Menu> =
        mutableListOf(/*MainMenu(), ArchiveMenu(), NoteListMenu()*/)
    private val menuMap = mutableMapOf<String, Menu>()

    fun openMainMenu() {
        try {
            menuMap["mainMenu"]!!.printMenu()
        } catch (e: Exception) {
            menuMap["mainMenu"] = MainMenu(this)
            menus.add(menuMap["mainMenu"]!!)
        }
        menus.last().printMenu()
    }

    fun openArchiveMenu(pickedArchive: Int) {
        menus.add(
            menuMap["archive"] ?: ArchiveMenu(
                this,
                (menuMap["mainMenu"] as MainMenu).archiveList,
                pickedArchive
            )
        )
        menus.last().printMenu()
    }

    fun back() {
        if (menus.size == 1) {
            menus.clear()
            println("До свидания!")
        } else {
            menus.removeLast()
            menus.last().printMenu()
        }
    }
}

class ArchiveApp {

    private val navigator = Navigator()

    fun start() {
        println("Программа для заметок приветствует тебя!")
        navigator.openMainMenu()
    }
}