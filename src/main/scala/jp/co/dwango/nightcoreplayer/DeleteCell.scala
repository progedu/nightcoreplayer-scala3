package jp.co.dwango.nightcoreplayer

import javafx.collections.ObservableList
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Pos
import javafx.scene.control.{Button, TableCell, TableView}
import javafx.scene.layout.HBox
import javafx.scene.media.MediaView

class DeleteCell(movies: ObservableList[Movie], mediaView: MediaView, tableView: TableView[Movie]) extends TableCell[Movie, Long]():
  val hBox = HBox()
  hBox.setAlignment(Pos.CENTER)
  val button = Button("X")
  hBox.getChildren.addAll(button)

  override def updateItem(id: Long, empty: Boolean): Unit =
    super.updateItem(id, empty)
    if (empty)
      setGraphic(null)
    else
      button.setOnAction(new EventHandler[ActionEvent]() {
        override def handle(event: ActionEvent): Unit =
          val deleted = movies.toArray(Array[Movie]()).toSeq.find(m => m.getId == id)
          if (tableView.getSelectionModel.getSelectedItem != null
            && tableView.getSelectionModel.getSelectedItem.getId == id) {
            mediaView.getMediaPlayer.stop()
          }
          deleted.map(m => movies.removeAll(m))
      })
      setGraphic(hBox)
    setText(null)

