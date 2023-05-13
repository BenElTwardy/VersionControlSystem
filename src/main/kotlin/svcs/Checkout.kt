package svcs

import java.io.File

class Checkout {

    //checking if the UUID of a commitFolder is actually there
    fun checkCommitFolder(commitFolder: File, argument: String): Boolean {
        val commitDir = commitFolder.listFiles()
        for (i in commitDir.indices) {
            val commitFileString = commitDir[i].toString().substringAfterLast("/")
            if (commitFileString == argument) {
                return true
            }
        }
        return false
    }

    //
    fun switchingCommit(commitFolder: File, argument: String) {
        val filesFromCommitFolder = File("${commitFolder}/$argument").listFiles()

        File(System.getProperty("user.dir")).walkBottomUp().forEach {
    /* - justTheFileFromCommitFolder is a just a string of the commitfile to check if it is the same as the iterator over the
       files of the "user.dir".
       - justFileFromIteratorFile is also a substring from the it to check if it is the same file as in the commitFolder.
       - We check in the if statement, if the path from the iterator includes "commits", if so then iterator will not overwrite
       the files with the passed commit.
    */
            for (i in filesFromCommitFolder.indices) {
                val justTheFileofCommitFolder = filesFromCommitFolder[i].toString().substringAfterLast("/")
                val justTheFileFromIteratorFile = it.toString().substringAfterLast("/")
                if (justTheFileFromIteratorFile == justTheFileofCommitFolder && !(it.toString().contains("commits"))) {
                    it.writeText(filesFromCommitFolder[i].readText())
                }
            }
        }
    }


}