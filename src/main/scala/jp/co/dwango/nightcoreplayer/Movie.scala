package jp.co.dwango.nightcoreplayer

import javafx.scene.media.Media

class Movie:

  private var id: Long = _

  private var fileName: String = _

  private var time: String = _

  private var filePath: String = _

  private var media: Media = _

  def setId(id: Long): Unit = this.id = id

  def getId: Long = this.id

  def setFileName(fileName: String): Unit = this.fileName = fileName

  def getFileName: String = this.fileName

  def setTime(time: String): Unit = this.time = time

  def getTime: String = this.time

  def setFilePath(filePath: String): Unit = this.filePath = filePath

  def getFilePath: String = this.filePath

  def setMedia(media: Media): Unit = this.media = media

  def getMedia: Media = this.media

  private def canEqual(other: Any): Boolean = other.isInstanceOf[Movie]

  override def equals(other: Any): Boolean = other match
    case that: Movie =>
      (that canEqual this) &&
        id == that.id
    case _ => false

  override def hashCode(): Int =
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)

object Movie:

  def apply(id: Long, fileName: String, time: String, filePath: String, media: Media): Movie =
    val movie = new Movie
    movie.setId(id)
    movie.setFileName(fileName)
    movie.setTime(time)
    movie.setFilePath(filePath)
    movie.setMedia(media)
    movie
