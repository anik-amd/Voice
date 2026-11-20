package voice.features.bookmark

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class BookmarkMarkdownTest {

  @Test
  fun `bookmarks are grouped by chapter mark in story order`() {
    val chapter =
      testChapter(
        duration = 10.minutes,
        marks = listOf(0.seconds to "Wake up", 5.minutes to "The signal"),
      )
    val book = testBook(listOf(chapter))
    val bookmarks =
      listOf(
        testBookmark(chapter, 6.minutes + 30.seconds, title = "second"),
        testBookmark(chapter, 1.minutes, title = "first"),
      )

    assertEquals(
      """
      # Echoes of Tomorrow

      ## Wake up

      [00:01:00] first

      ## The signal

      [00:01:30] second
      """
        .trimIndent() + "\n",
      bookmarksMarkdown(book, bookmarks),
    )
  }

  @Test
  fun `a chapter without a name uses its number`() {
    val chapter = testChapter()
    val book = testBook(listOf(chapter))

    assertEquals(
      """
      # Echoes of Tomorrow

      ## Chapter 1

      [00:01:00]
      """
        .trimIndent() + "\n",
      bookmarksMarkdown(book, listOf(testBookmark(chapter, 1.minutes))),
    )
  }

  @Test
  fun `hours count past one`() {
    val chapter = testChapter(duration = 2.hours)
    val book = testBook(listOf(chapter))

    assertEquals(
      """
      # Echoes of Tomorrow

      ## Chapter 1

      [01:02:03]
      """
        .trimIndent() + "\n",
      bookmarksMarkdown(
        book,
        listOf(testBookmark(chapter, 1.hours + 2.minutes + 3.seconds)),
      ),
    )
  }

  @Test
  fun `a book without bookmarks only has its title`() {
    val book = testBook(listOf(testChapter()))

    assertEquals(
      """
      # Echoes of Tomorrow
      """
        .trimIndent() + "\n",
      bookmarksMarkdown(book, emptyList()),
    )
  }

  @Test
  fun `a note with a line break stays on the bookmark line`() {
    val chapter = testChapter()
    val book = testBook(listOf(chapter))

    assertEquals(
      """
      # Echoes of Tomorrow

      ## Chapter 1

      [00:00:10] First line second line
      """
        .trimIndent() + "\n",
      bookmarksMarkdown(
        book,
        listOf(testBookmark(chapter, 10.seconds, title = "First line\n  second line")),
      ),
    )
  }

  @Test
  fun `the file name drops characters that a file system rejects`() {
    assertEquals(
      "A_B_ C Bookmarks.md",
      bookmarksFileName("A/B: C"),
    )
  }
}
