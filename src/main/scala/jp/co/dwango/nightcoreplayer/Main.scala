package jp.co.dwango.nightcoreplayer

import javafx.application.Application
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

import jp.co.dwango.nightcoreplayer.MoviePlayer._
import jp.co.dwango.nightcoreplayer.SizeConstants._

object Main extends App:
  Application.launch(classOf[Main], args: _*)

class Main extends Application:

  override def start(primaryStage: Stage): Unit =
    val mediaView = new MediaView()

    val timeLabel = Label()
    timeLabel.setText("00:00:00/00:00:00")
    timeLabel.setTextFill(Color.WHITE)

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

    val toolBar = ToolbarCreator.create(mediaView, tableView, timeLabel, primaryStage)

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
