package jp.co.dwango

import javafx.util.Duration

package object nightcoreplayer {

  private[this] def formatTime(duration: Duration): String =
    "%02d:%02d:%02d".format(
      duration.toHours.toInt,
      duration.toMinutes.toInt % 60,
      duration.toSeconds.toInt % 60)

  private[this] def formatTime(elapsed: Duration, duration: Duration): String =
    s"${formatTime(elapsed)}/${formatTime(duration)}"

}