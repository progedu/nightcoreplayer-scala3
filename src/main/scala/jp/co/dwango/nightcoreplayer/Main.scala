package jp.co.dwango.nightcoreplayer

import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.media.MediaPlayer.Status
import javafx.scene.media.{MediaPlayer, MediaView}
import javafx.scene.input.MouseEvent
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
    val toolBar = HBox()
    toolBar.setMinHeight(toolBarMinHeight)
    toolBar.setAlignment(Pos.CENTER)
    toolBar.setStyle("-fx-background-color: Black")

    val tableView = TableView[Movie]()
    tableView.setMinWidth(tableMinWidth)
    val movies = FXCollections.observableArrayList[Movie]()
    tableView.setItems(movies)
    tableView.setRowFactory(new Callback[TableView[Movie], TableRow[Movie]]() {
      override def call(param: TableView[Movie]): TableRow[Movie] = {
        val row = TableRow[Movie]()
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

    val fileNameColumn = TableColumn[Movie, String]("ファイル名")
    fileNameColumn.setCellValueFactory(PropertyValueFactory("fileName"))
    fileNameColumn.setPrefWidth(160)
    val timeColumn = TableColumn[Movie, String]("時間")
    timeColumn.setCellValueFactory(PropertyValueFactory("time"))
    timeColumn.setPrefWidth(80)
    val deleteActionColumn = TableColumn[Movie, Long]("削除")
    deleteActionColumn.setCellValueFactory(PropertyValueFactory("id"))
    deleteActionColumn.setPrefWidth(60)
    deleteActionColumn.setCellFactory(new Callback[TableColumn[Movie, Long], TableCell[Movie, Long]]() {
      override def call(param: TableColumn[Movie, Long]): TableCell[Movie, Long] =
        new DeleteCell(movies, mediaView, tableView)
    })

    tableView.getColumns.setAll(fileNameColumn, timeColumn, deleteActionColumn)

    // first button
    val firstButton = createButton("icon/first.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null)
          playPre(tableView, mediaView, timeLabel)
    })

    // back button
    val backButton = createButton("icon/back.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null)
          mediaView.getMediaPlayer.seek(
            mediaView.getMediaPlayer.getCurrentTime.subtract(Duration(10000)))
    })

    // play button
    val playButton = createButton("icon/play.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        val selectionModel = tableView.getSelectionModel
        if (mediaView.getMediaPlayer != null && !selectionModel.isEmpty)
          val movie = selectionModel.getSelectedItem
          if (mediaView.getMediaPlayer.getStatus == Status.PAUSED)
            playMovie(movie, tableView, mediaView, timeLabel, true)
          else
            playMovie(movie, tableView, mediaView, timeLabel)
    })

    // pause button
    val pauseButton = createButton("icon/pause.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null) mediaView.getMediaPlayer.pause()
    })

    // forward button
    val forwardButton = createButton("icon/forward.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null)
          mediaView.getMediaPlayer.seek(
            mediaView.getMediaPlayer.getCurrentTime.add(Duration(10000)))
    })

    // last button
    val lastButton = createButton("icon/last.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null)
          playNext(tableView, mediaView, timeLabel)
    })

    // fullscreen button
    val fullscreenButton = createButton("icon/fullscreen.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        primaryStage.setFullScreen(true)
    })

    toolBar.getChildren.addAll(firstButton,
      backButton, playButton, pauseButton, forwardButton, lastButton, fullscreenButton, timeLabel)

    val baseBorderPane = BorderPane()
    baseBorderPane.setStyle("-fx-background-color: Black")
    baseBorderPane.setCenter(mediaView)
    baseBorderPane.setBottom(toolBar)
    baseBorderPane.setRight(tableView)
    val scene = Scene(baseBorderPane, mediaViewFitWidth + tableMinWidth, mediaViewFitHeight + toolBarMinHeight)
    scene.setFill(Color.BLACK)
    mediaView.fitWidthProperty().bind(scene.widthProperty().subtract(tableMinWidth))
    mediaView.fitHeightProperty().bind(scene.heightProperty().subtract(toolBarMinHeight))

    scene.setOnDragOver(MovieFileDragOverEventHandler(scene))

    scene.setOnDragDropped(MovieFileDragDroppedEventHandler(movies))

    primaryStage.setTitle("mp4ファイルをドラッグ&ドロップしてください")

    primaryStage.setScene(scene)
    primaryStage.show()

  private[this] def createButton(imagePath: String, eventHandler: EventHandler[ActionEvent]): Button = {
    val buttonImage = new Image(getClass.getResourceAsStream(imagePath))
    val button = Button()
    button.setGraphic(ImageView(buttonImage))
    button.setStyle("-fx-background-color: Black")
    button.setOnAction(eventHandler)
    button.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit = {
        button.setStyle("-fx-body-color: Black")
      }
    })
    button.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit = {
        button.setStyle("-fx-background-color: Black")
      }
    })
    button
  }

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

  private[this] def playPre(tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit =
    playAt(Pre, tableView, mediaView, timeLabel)

  private[this] def playNext(tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit =
    playAt(Next, tableView, mediaView, timeLabel)
