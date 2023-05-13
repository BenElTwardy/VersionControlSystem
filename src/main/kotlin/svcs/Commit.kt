package svcs

import java.io.File
import java.util.*

class Commit {
    val id = UUID.randomUUID().toString()

    init {
        val newCommit = File("vcs/commits/$id")
        newCommit.mkdir()
    }

    fun copyTheModifiedFilesIntoIDFolder(indexFile: File) {
        val linesFromIndexFile = indexFile.readLines()
        var files = mutableListOf<File>()

        File(System.getProperty("user.dir")).walkBottomUp().forEach {
            for (i in 1 until linesFromIndexFile.size) {
                val substringPath = it.toString().substringAfterLast("/")
                var subStringtoControllIfCommitFolder = it.toString().substringBeforeLast("/")
                subStringtoControllIfCommitFolder = subStringtoControllIfCommitFolder.substringBeforeLast("/")
                subStringtoControllIfCommitFolder = subStringtoControllIfCommitFolder.substringAfter("vcs/")
                if (substringPath == linesFromIndexFile[i] && subStringtoControllIfCommitFolder != "commits") {
                    files.add(it)
                }
            }
        }
        //getting a substring from the absolute file and copy the files from the main directory to the commit folder
        for (i in 1 until linesFromIndexFile.size) {
            for (j in 0 until files.size) {
                val substringFile = files[j].toString().substringAfterLast("/")
                if (linesFromIndexFile[i] == substringFile) {
                    val newFile = File("vcs/commits/$id/$substringFile")
                    files[j].copyTo(newFile)
                }
            }
        }
    }

    fun writeIntoLogFile(logFile: File, configFile: File, commitText: String) {

        val logFileTemp = logFile.readText()
        logFile.writeText(
            "commit $id\n" +
                    "Author: ${configFile.readText()}\n" +
                    "$commitText\n\n"
        )
        logFile.appendText(logFileTemp)
    }
}
