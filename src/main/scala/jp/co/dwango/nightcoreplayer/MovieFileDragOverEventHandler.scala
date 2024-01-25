package jp.co.dwango.nightcoreplayer

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.{DragEvent, TransferMode}


class MovieFileDragOverEventHandler(scene: Scene) extends EventHandler[DragEvent]:
  override def handle(event: DragEvent): Unit =
    if (event.getGestureSource != scene && event.getDragboard.hasFiles)
      event.acceptTransferModes(TransferMode.COPY_OR_MOVE: _*)
    event.consume()