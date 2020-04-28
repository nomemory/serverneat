package net.andreinc.serverneat.utils

fun temporaryFileWithContent(fileName: String, content: String, suffix : String = ".tmp") : String {
    val file = createTempFile(fileName, suffix)
    file.deleteOnExit()
    file.writeText(content)
    return file.absolutePath
}