package svcs

import java.io.File

class Command {

    val vcs = File("vcs")
    val configFile = File("vcs/config.txt")
    val indexFile = File("vcs/index.txt")
    val logFile = File("vcs/log.txt")
    val commitDir = File("vcs/commits")
    val lastCommitFile = File("lastCommit.txt")

    //initalize the directory "vcs" and the files "configFile", "indexFile" in the constructor
    init {
        vcs.mkdir()
        commitDir.mkdir()
        logFile.createNewFile()
        configFile.createNewFile()
        indexFile.createNewFile()
        lastCommitFile.createNewFile()
    }



    val commandMap = mapOf<String, String>(
        "config" to "Get and set a username.",
        "add" to "Add a file to the index.",
        "log" to "Show commit logs.",
        "commit" to "Save changes.",
        "checkout" to "Restore a file."
    )

    fun checkCommand(argument1: String, argument2: String) {
        if (!commandMap.containsKey(argument1)){
            println("'$argument1' is not a SVCS command.")
            return
        }
        when (argument1) {
            "config" -> {
                if (argument2.isEmpty()) {
                    if (configFile.readText().isEmpty())
                        println("Please, tell me who you are.")
                    else println("The username is ${configFile.readText()}.")
                }
                else configSearch(argument2)
            }
            "add" -> {
                if (indexFile.readLines().isEmpty()) {
                    indexFile.writeText("Tracked files:")
                }
                if (argument2.isEmpty()) {
                    if (indexFile.readLines().size == 1) {
                        println("Add a file to the index.")
                    }
                    else println(indexFile.readText())
                }
                else {
                    addFileToIndex(argument2)
                }
            }
            "log" -> {
                if (commitDir.listFiles().isEmpty()) println("No commits yet.")
                else {
                    logFile.forEachLine {
                        println(it)
                    }
                }
            }
            "commit" -> {
                if (argument2.isEmpty()) println("Message was not passed.")
                else {
                    if (commitDir.listFiles().isNotEmpty()){
                        if (checkCommit(lastCommitFile.readText(), indexFile)) println("Nothing to commit.")
                        else {
                            val commit = Commit()
                            commit.writeIntoLogFile(logFile, configFile, argument2)
                            commit.copyTheModifiedFilesIntoIDFolder(indexFile)
                            lastCommitFile.writeText(commit.id)
                            println("Changes are committed.")
                        }
                    }
                    else {
                        val commit = Commit()
                        commit.writeIntoLogFile(logFile, configFile, argument2)
                        commit.copyTheModifiedFilesIntoIDFolder(indexFile)
                        lastCommitFile.writeText(commit.id)
                        println("Changes are committed.")
                    }
                }
            }
            "checkout" -> {
                val checkout = Checkout()
                if (argument2.isEmpty()) println("Commit id was not passed.")
                else if (commitDir.listFiles().isEmpty()) println("Commit folder is empty.")
                else if(!checkout.checkCommitFolder(commitDir, argument2)) println("Commit does not exist.")
                else {
                    checkout.switchingCommit(commitDir, argument2,)
                    println("Switched to commit $argument2.")
                }

            }
        }
    }

    private fun checkCommit(lastCommit: String, indexFile: File): Boolean {

        var directoryOfLastCommit = lastCommit.substringAfterLast("/")
        /* generating substrings from the paths and implement an extra ArrayList for the substrings of the last commit file */
        val substringFromAbsolutePath = lastCommit.substringAfterLast("/")
        val fileOfCommit = File("vcs/commits/$substringFromAbsolutePath")
        val commitFiles = fileOfCommit.listFiles()
        val commitFilesString = ArrayList<String>()
        if (commitFiles != null) {
            for (i in commitFiles.indices) {
                commitFilesString.add(commitFiles[i].toString().substringAfterLast("/"))
            }
        }

        /*iterating through every file in the current working directory and compare them to our files in the last commit
        , which we saved in commitFilesString. If the file from the working directory and that one in the last commit
        got a different hash, we return false and can continue the commit. Otherwise we return true, and print a text
        to the console */

        File(System.getProperty("user.dir")).walkBottomUp().forEach {
            val substringFile = it.toString().substringAfterLast("/")
            var subStringRightDirectory = it.toString().substringBeforeLast("/")
            subStringRightDirectory = subStringRightDirectory.substringAfter("commits/")

            //Has some file changed since the last commit
            for (i in commitFiles.indices) {
                if (substringFile == commitFilesString[i] && (subStringRightDirectory == System.getProperty("user.dir"))) {
                    if (ownHashFuction(it) != ownHashFuction(commitFiles[i]))
                        return false
                }
            }
            //Was a new file added since the last commit
            val linesFromIndex = indexFile.readLines()
            for (i in 1 until linesFromIndex.size) {
                if (!commitFilesString.contains(linesFromIndex[i])) return false
            }
        }
        return true
    }

    private fun ownHashFuction(file: File): Int {

        val readText = file.readText()
        var hashValue = 0
        val pValue = 2
        for (i in readText.indices) {
            hashValue = hashValue * pValue + readText[i].code
        }
        return hashValue
    }


    //method to add a file to the index.txt
    private fun addFileToIndex(argument: String) {
        val fileWannaAdd = File("$argument")
        //if file is not in the src folder
        if (indexFile.readLines().contains(argument)) {
            println("The file is already tracked.")
            return
        }
        else if (!fileWannaAdd.isFile) {
            println("Can't find '$argument'.")
        }
        else {
            indexFile.appendText("\n$argument")
            println("The file '$argument' is tracked.")
        }
    }

    //method to handle the config input from the user
    private fun configSearch(argument: String) {

        //overwrite the username in config.txt
        configFile.writeText("$argument")
        println("The username is $argument.")
    }


    private fun searchRightCommand(argument: String) {
        for ((k,v) in commandMap) {
            if (k == argument) {
                println(v)
            }
        }
    }


    fun printHelpPage(){
        String.format("")
        println("These are SVCS commands:")
        for ((k,v) in commandMap) {
            println(String.format("%-9s %s", k, v))
        }
    }
}