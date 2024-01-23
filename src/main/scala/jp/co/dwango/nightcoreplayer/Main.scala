package jp.co.dwango.nightcoreplayer

import java.io.File
import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.{Label, TableColumn, TableView}
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.input.{DragEvent, TransferMode}
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration

object Main extends App:
  Application.launch(classOf[Main], args: _*)

class Main extends Application:

  private[this] val mediaViewFitWidth = 800
  private[this] val mediaViewFitHeight = 450
  private[this] val toolBarMinHeight = 50
  private[this] val tableMinWidth = 300

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

    mediaPlayer.currentTimeProperty().addListener(new ChangeListener[Duration] {
      override def changed(observable: ObservableValue[_ <: Duration], oldValue: Duration, newValue: Duration): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })

    timeLabel.setText("00:00:00/00:00:00")
    timeLabel.setTextFill(Color.WHITE)
    val toolBar = HBox(timeLabel)
    toolBar.setMinHeight(toolBarMinHeight)
    toolBar.setAlignment(Pos.CENTER)
    toolBar.setStyle("-fx-background-color: Black")

    val tableView = TableView[Movie]()
    tableView.setMinWidth(tableMinWidth)
    val movies = FXCollections.observableArrayList[Movie]()
    tableView.setItems(movies)

    val fileNameColumn = TableColumn[Movie, String]("ファイル名")
    fileNameColumn.setCellValueFactory(PropertyValueFactory("fileName"))
    fileNameColumn.setPrefWidth(160)
    val timeColumn = TableColumn[Movie, String]("時間")
    timeColumn.setCellValueFactory(PropertyValueFactory("time"))
    timeColumn.setPrefWidth(80)

    tableView.getColumns.setAll(fileNameColumn, timeColumn)

    val baseBorderPane = BorderPane()
    baseBorderPane.setStyle("-fx-background-color: Black")
    baseBorderPane.setCenter(mediaView)
    baseBorderPane.setBottom(toolBar)
    baseBorderPane.setRight(tableView)
    val scene = new Scene(baseBorderPane, mediaViewFitWidth + tableMinWidth, mediaViewFitHeight + toolBarMinHeight)
    scene.setFill(Color.BLACK)
    mediaView.fitWidthProperty().bind(scene.widthProperty().subtract(tableMinWidth))
    mediaView.fitHeightProperty().bind(scene.heightProperty().subtract(toolBarMinHeight))

    scene.setOnDragOver(new EventHandler[DragEvent]() {
      override def handle(event: DragEvent): Unit =
        if (event.getGestureSource != scene &&
          event.getDragboard.hasFiles) {
          event.acceptTransferModes(TransferMode.COPY_OR_MOVE: _*)
        }
        event.consume()
    })

    scene.setOnDragDropped(new EventHandler[DragEvent]() {
      override def handle(event: DragEvent): Unit =
        val db = event.getDragboard
        if (db.hasFiles) {
          db.getFiles.toArray(Array[File]()).toSeq.foreach { f =>
            val filePath = f.getAbsolutePath
            val fileName = f.getName
            val media = Media(f.toURI.toString)
            val time = formatTime(media.getDuration)

            val movie = Movie(System.currentTimeMillis(), fileName, time, filePath, media)
            while (movies.contains(movie)) {
              movie.setId(movie.getId + 1L)
            }
            movies.add(movie)
          }
        }
        event.consume()
    })

    primaryStage.setTitle("mp4ファイルをドラッグ&ドロップしてください")

    primaryStage.setScene(scene)
    primaryStage.show()

  private[this] def formatTime(duration: Duration): String =
  "%02d:%02d:%02d".format(
    duration.toHours.toInt,
    duration.toMinutes.toInt % 60,
    duration.toSeconds.toInt % 60)

  private[this] def formatTime(elapsed: Duration, duration: Duration): String =
      s"${formatTime(elapsed)}/${formatTime(duration)}"