package jp.co.dwango.nightcoreplayer

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.{Label, TableView}
import javafx.scene.media.{MediaPlayer, MediaView}
import javafx.util.Duration

object MoviePlayer:

  def playMovie(movie: Movie,tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label, resume: Boolean = false): Unit =
    var oldCurrentDuration: Option[Duration] = None
    if (mediaView.getMediaPlayer != null) {
      val oldPlayer = mediaView.getMediaPlayer
      oldCurrentDuration = Some(oldPlayer.getCurrentTime)
      oldPlayer.stop()
      oldPlayer.dispose()
    }

    val mediaPlayer = new MediaPlayer(movie.getMedia)
    mediaPlayer.currentTimeProperty().addListener(new ChangeListener[Duration] {
      override def changed(observable: ObservableValue[_ <: Duration], oldValue: Duration, newValue: Duration): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })
    mediaPlayer.setOnEndOfMedia(new Runnable() {
      override def run(): Unit = playNext(tableView, mediaView, timeLabel)
    })
    mediaPlayer.setOnReady(new Runnable() {
      override def run(): Unit =
        mediaPlayer.setRate(1.25)
        if (resume) oldCurrentDuration.tapEach(d => mediaPlayer.seek(d))
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })

    mediaView.setMediaPlayer(mediaPlayer)
    mediaPlayer.play()

  sealed private trait Track
  private object Pre extends Track
  private object Next extends Track

  private[this] def playAt(track: Track, tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit =
    val selectionModel = tableView.getSelectionModel
    if (selectionModel.isEmpty) return
    val index = selectionModel.getSelectedIndex
    val changedIndex = track match
      case Pre => (tableView.getItems.size() + index - 1) % tableView.getItems.size()
      case Next => (index + 1) % tableView.getItems.size()
    selectionModel.select(changedIndex)
    val movie = selectionModel.getSelectedItem
    playMovie(movie, tableView, mediaView, timeLabel)

  def playPre(tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit =
    playAt(Pre, tableView, mediaView, timeLabel)

  def playNext(tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit =
    playAt(Next, tableView, mediaView, timeLabel)
