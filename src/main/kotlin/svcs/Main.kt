package svcs

fun main(args: Array<String>) {

    val command = Command()
    if (args.isEmpty() || args[0] == "--help") {
        command.printHelpPage()
        return
    }

    if (args.size == 1) command.checkCommand(args[0], "")
    else command.checkCommand(args[0], args[1])
}

