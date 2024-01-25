package jp.co.dwango.nightcoreplayer

import java.io.File
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.input.DragEvent
import javafx.scene.media.{Media, MediaPlayer}

class MovieFileDragDroppedEventHandler(movies: ObservableList[Movie]) extends EventHandler[DragEvent]:
  override def handle(event: DragEvent): Unit =
    val db = event.getDragboard
    if (db.hasFiles)
      db.getFiles.toArray(Array[File]()).toSeq.foreach { f =>
        val filePath = f.getAbsolutePath
        val fileName = f.getName
        val media = Media(f.toURI.toString)
        val player = MediaPlayer(media)
        player.setOnReady(new Runnable() {
          override def run(): Unit =
            val time = formatTime(media.getDuration)
            val movie = Movie(System.currentTimeMillis(), fileName, time, filePath, media)
            while (movies.contains(movie)) {
              movie.setId(movie.getId + 1L)
            }
            movies.add(movie)
            player.dispose()
        })
      }
    event.consume()
