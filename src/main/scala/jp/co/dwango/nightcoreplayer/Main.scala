package jp.co.dwango.nightcoreplayer

import java.io.File
import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration

object Main extends App:
  Application.launch(classOf[Main], args: _*)

class Main extends Application:

  override def start(primaryStage: Stage): Unit =
    val path = "/Users/soichiro_yoshimura/Movies/video.mp4"
    val media = Media(File(path).toURI.toString)
    val mediaPlayer = MediaPlayer(media)
    val timeLabel = Label()
    mediaPlayer.setOnReady(new Runnable(){
      override def run(): Unit =
        mediaPlayer.setRate(1.25)
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })
    mediaPlayer.play()
    val mediaView = MediaView(mediaPlayer)
    mediaView.setFitWidth(800)
    mediaView.setFitHeight(450)

    mediaPlayer.currentTimeProperty().addListener(new ChangeListener[Duration] {
      override def changed(observable: ObservableValue[_ <: Duration], oldValue: Duration, newValue: Duration): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })

    timeLabel.setText("00:00:00/00:00:00")
      timeLabel.setTextFill(Color.WHITE)
      val toolBar = HBox(timeLabel)
      toolBar.setAlignment(Pos.CENTER)
      toolBar.setStyle("-fx-background-color: Black")
      val baseBorderPane = BorderPane()
      baseBorderPane.setStyle("-fx-background-color: Black")
      baseBorderPane.setCenter(mediaView)
      baseBorderPane.setBottom(toolBar)
      val scene = Scene(baseBorderPane, 800, 500)
      scene.setFill(Color.BLACK)
      primaryStage.setScene(scene)
      primaryStage.show()

  private[this] def formatTime(elapsed: Duration, duration: Duration): String = {
    "%02d:%02d:%02d/%02d:%02d:%02d".format(
      elapsed.toHours.toInt,
      elapsed.toMinutes.toInt % 60,
      elapsed.toSeconds.toInt % 60,
      duration.toHours.toInt,
      duration.toMinutes.toInt % 60,
      duration.toSeconds.toInt % 60)
  }
