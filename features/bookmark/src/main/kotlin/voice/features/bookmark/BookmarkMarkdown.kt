package voice.features.bookmark

import java.util.Locale
import voice.core.data.Book
import voice.core.data.Bookmark

internal fun bookmarksMarkdown(
  book: Book,
  bookmarks: List<Bookmark>,
): String {
  val index = BookIndex(book)
  val chapters =
    bookmarks
      .mapNotNull { bookmark ->
        index.locate(bookmark.chapterId, bookmark.time)?.let { bookmark to it }
      }
      .sortedWith(compareBy({ it.second.bookPosition }, { it.first.addedAt }))
      .groupBy { it.second.chapterNumber }

  return buildString {
    append("# ")
    append(book.name.trim().ifEmpty { "Bookmarks" })
    append("\n")
    chapters.forEach { (chapterNumber, entries) ->
      append("\n## ")
      append(entries.first().second.chapterName ?: "Chapter $chapterNumber")
      append("\n")
      entries.forEach { (bookmark, location) ->
        append("\n[")
        append(timestamp(location.timeInChapter))
        append("]")
        bookmark.title
          ?.oneLine()
          ?.takeIf { it.isNotEmpty() }
          ?.let { note ->
            append(" ")
            append(note)
          }
        append("\n")
      }
    }
  }
}

internal fun bookmarksFileName(bookTitle: String): String {
  val name = bookTitle.replace(Regex("""[\\/:*?"<>|\p{Cntrl}]"""), "_").trim()
  return "${name.ifEmpty { "Bookmarks" }} Bookmarks.md"
}

private fun String.oneLine(): String = trim().replace(Regex("\\s+"), " ")

private fun timestamp(timeMs: Long): String {
  val seconds = (timeMs / 1000).coerceAtLeast(0)
  return String.format(
    Locale.ROOT,
    "%02d:%02d:%02d",
    seconds / 3600,
    (seconds / 60) % 60,
    seconds % 60,
  )
}
