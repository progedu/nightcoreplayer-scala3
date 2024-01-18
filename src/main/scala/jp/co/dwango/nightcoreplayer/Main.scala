package jp.co.dwango.nightcoreplayer

import java.io.File
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.Color
import javafx.stage.Stage

object Main extends App:
  Application.launch(classOf[Main], args: _*)

class Main extends Application:

  override def start(primaryStage: Stage): Unit =
    val path = "/Users/soichiro_yoshimura/Movies/video.mp4"
    val media = Media(File(path).toURI.toString)
    val mediaPlayer = MediaPlayer(media)
    mediaPlayer.setOnReady(new Runnable(){
      override def run(): Unit = mediaPlayer.setRate(1.25)
    })
    mediaPlayer.play()
    val mediaView = MediaView(mediaPlayer)
    mediaView.setFitWidth(800)
    mediaView.setFitHeight(450)
    val timeLabel = Label()
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