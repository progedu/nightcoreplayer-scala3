package jp.co.dwango.nightcoreplayer

import java.io.File
import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.*
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{DragEvent, MouseEvent, TransferMode}
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.media.MediaPlayer.Status
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.{Callback, Duration}

object Main extends App:
  Application.launch(classOf[Main], args: _*)

class Main extends Application:

  private[this] val mediaViewFitWidth = 800
  private[this] val mediaViewFitHeight = 450
  private[this] val toolBarMinHeight = 50
  private[this] val tableMinWidth = 300

  override def start(primaryStage: Stage): Unit =
    val mediaView = new MediaView()

    val timeLabel = Label()
    timeLabel.setText("00:00:00/00:00:00")
    timeLabel.setTextFill(Color.WHITE)
    val toolBar = new HBox()
    toolBar.setMinHeight(toolBarMinHeight)
    toolBar.setAlignment(Pos.CENTER)
    toolBar.setStyle("-fx-background-color: Black")

    val tableView = new TableView[Movie]()
    tableView.setMinWidth(tableMinWidth)
    val movies = FXCollections.observableArrayList[Movie]()
    tableView.setItems(movies)
    tableView.setRowFactory(new Callback[TableView[Movie], TableRow[Movie]]() {
      override def call(param: TableView[Movie]): TableRow[Movie] = {
        val row = new TableRow[Movie]()
        row.setOnMouseClicked(new EventHandler[MouseEvent]() {
          override def handle(event: MouseEvent): Unit = {
            if (event.getClickCount >= 1 && !row.isEmpty) {
              playMovie(row.getItem, tableView, mediaView, timeLabel)
            }
          }
        })
        row
      }
    })

    val fileNameColumn = new TableColumn[Movie, String]("ファイル名")
    fileNameColumn.setCellValueFactory(PropertyValueFactory("fileName"))
    fileNameColumn.setPrefWidth(160)
    val timeColumn = new TableColumn[Movie, String]("時間")
    timeColumn.setCellValueFactory(PropertyValueFactory("time"))
    timeColumn.setPrefWidth(80)
    val deleteActionColumn = new TableColumn[Movie, Long]("削除")

    deleteActionColumn.setCellValueFactory(PropertyValueFactory("id"))
    deleteActionColumn.setPrefWidth(60)
    deleteActionColumn.setCellFactory(new Callback[TableColumn[Movie, Long], TableCell[Movie, Long]]() {
      override def call(param: TableColumn[Movie, Long]): TableCell[Movie, Long] =
        DeleteCell(movies, mediaView, tableView)
    })

    tableView.getColumns.setAll(fileNameColumn, timeColumn, deleteActionColumn)

    // first button
    val firstButtonImage = Image(getClass.getResourceAsStream("icon/first.png"))
    val firstButton = Button()
    firstButton.setGraphic(ImageView(firstButtonImage))
    firstButton.setStyle("-fx-background-color: Black")
    firstButton.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null)
          playPre(tableView, mediaView, timeLabel)
    })
    firstButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        firstButton.setStyle("-fx-body-color: Black")
    })
    firstButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        firstButton.setStyle("-fx-background-color: Black")
    })

    // back button
    val backButtonImage = Image(getClass.getResourceAsStream("icon/back.png"))
    val backButton = Button()
    backButton.setGraphic(ImageView(backButtonImage))
    backButton.setStyle("-fx-background-color: Black")
    backButton.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null)
          mediaView.getMediaPlayer.seek(
            mediaView.getMediaPlayer.getCurrentTime.subtract(new Duration(10000)))
    })
    backButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        backButton.setStyle("-fx-body-color: Black")
    })
    backButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        backButton.setStyle("-fx-background-color: Black")
    })

    // play button
    val playButtonImage = Image(getClass.getResourceAsStream("icon/play.png"))
    val playButton = Button()
    playButton.setGraphic(ImageView(playButtonImage))
    playButton.setStyle("-fx-background-color: Black")
    playButton.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        val selectionModel = tableView.getSelectionModel
        if (mediaView.getMediaPlayer != null && !selectionModel.isEmpty)
          val movie = selectionModel.getSelectedItem
          if (mediaView.getMediaPlayer.getStatus == Status.PAUSED)
            playMovie(movie, tableView, mediaView, timeLabel, true)
          else
            playMovie(movie, tableView, mediaView, timeLabel)
    })
    playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        playButton.setStyle("-fx-body-color: Black")
    })
    playButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        playButton.setStyle("-fx-background-color: Black")
    })

    // pause button
    val pauseButtonImage = Image(getClass.getResourceAsStream("icon/pause.png"))
    val pauseButton = Button()
    pauseButton.setGraphic(ImageView(pauseButtonImage))
    pauseButton.setStyle("-fx-background-color: Black")
    pauseButton.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null) mediaView.getMediaPlayer.pause()
    })
    pauseButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        pauseButton.setStyle("-fx-body-color: Black")
    })
    pauseButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        pauseButton.setStyle("-fx-background-color: Black")
    })

    // forward button
    val forwardButtonImage = Image(getClass.getResourceAsStream("icon/forward.png"))
    val forwardButton = Button()
    forwardButton.setGraphic(ImageView(forwardButtonImage))
    forwardButton.setStyle("-fx-background-color: Black")
    forwardButton.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null)
          mediaView.getMediaPlayer.seek(
            mediaView.getMediaPlayer.getCurrentTime.add(new Duration(10000)))
    })
    forwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        forwardButton.setStyle("-fx-body-color: Black")
    })
    forwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        forwardButton.setStyle("-fx-background-color: Black")
    })

    // last button
    val lastButtonImage = Image(getClass.getResourceAsStream("icon/last.png"))
    val lastButton = Button()
    lastButton.setGraphic(ImageView(lastButtonImage))
    lastButton.setStyle("-fx-background-color: Black")
    lastButton.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null)
          playNext(tableView, mediaView, timeLabel)
    })
    lastButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        lastButton.setStyle("-fx-body-color: Black")
    })
    lastButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        lastButton.setStyle("-fx-background-color: Black")
    })

    // fullscreen button
    val fullscreenButtonImage = Image(getClass.getResourceAsStream("icon/fullscreen.png"))
    val fullscreenButton = Button()
    fullscreenButton.setGraphic(ImageView(fullscreenButtonImage))
    fullscreenButton.setStyle("-fx-background-color: Black")
    fullscreenButton.setOnAction(new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        primaryStage.setFullScreen(true)
    })
    fullscreenButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        fullscreenButton.setStyle("-fx-body-color: Black")
    })
    fullscreenButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit =
        fullscreenButton.setStyle("-fx-background-color: Black")
    })

    toolBar.getChildren.addAll(
      firstButton, backButton, playButton, pauseButton, forwardButton, lastButton, fullscreenButton, timeLabel)

    val baseBorderPane = BorderPane()
    baseBorderPane.setStyle("-fx-background-color: Black")
    baseBorderPane.setCenter(mediaView)
    baseBorderPane.setBottom(toolBar)
    baseBorderPane.setRight(tableView)
    val scene = new Scene(baseBorderPane, mediaViewFitWidth + tableMinWidth, mediaViewFitHeight + toolBarMinHeight)
    scene.setFill(Color.BLACK)
    mediaView.fitWidthProperty().bind(scene.widthProperty())
    mediaView.fitWidthProperty().bind(scene.widthProperty().subtract(tableMinWidth))

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
            val player = MediaPlayer(media)
            player.setOnReady(new Runnable() {
              override def run(): Unit = {
                val time = formatTime(media.getDuration)
                val movie = Movie(System.currentTimeMillis(), fileName, time, filePath, media)
                while (movies.contains(movie)) {
                  movie.setId(movie.getId + 1L)
                }
                movies.add(movie)
                player.dispose()
              }
            })

          }
        }
        event.consume()
    })

    primaryStage.setTitle("mp4ファイルをドラッグ&ドロップしてください")

    primaryStage.setScene(scene)
    primaryStage.show()

  private[this] def playMovie(movie: Movie,tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label, resume: Boolean = false): Unit =
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
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })

    if (resume) oldCurrentDuration.tapEach(d => mediaPlayer.setStartTime(d))
    mediaView.setMediaPlayer(mediaPlayer)
    mediaPlayer.play()

  private[this] def playPre(tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit =
    val selectionModel = tableView.getSelectionModel
    if (selectionModel.isEmpty) return
    val index = selectionModel.getSelectedIndex
    val preIndex = (tableView.getItems.size() + index - 1) % tableView.getItems.size()
    selectionModel.select(preIndex)
    val movie = selectionModel.getSelectedItem
    playMovie(movie, tableView, mediaView, timeLabel)

  private[this] def playNext(tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit =
    val selectionModel = tableView.getSelectionModel
    if (selectionModel.isEmpty) return
    val index = selectionModel.getSelectedIndex
    val nextIndex = (index + 1) % tableView.getItems.size()
    selectionModel.select(nextIndex)
    val movie = selectionModel.getSelectedItem
    playMovie(movie, tableView, mediaView, timeLabel)

  private[this] def formatTime(elapsed: Duration): String =
    "%02d:%02d:%02d".format(
      elapsed.toHours.toInt,
      elapsed.toMinutes.toInt % 60,
      elapsed.toSeconds.toInt % 60)

  private[this] def formatTime(elapsed: Duration, duration: Duration): String =
    s"${formatTime(elapsed)}/${formatTime(duration)}"
