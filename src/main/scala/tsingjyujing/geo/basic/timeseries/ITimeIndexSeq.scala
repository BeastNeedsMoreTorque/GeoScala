package tsingjyujing.geo.basic.timeseries

import scala.collection.mutable

trait ITimeIndexSeq[T <: ITick] extends mutable.Iterable[T] {

    private val data = mutable.Buffer[T]()

    /**
      * Sort time series by time
      */
    def sortByTick(): Unit = {
        data.sortBy(_.getTick)
    }

    /**
      * Search in sorted array
      *
      * @param time query time
      * @return
      */
    def query(time: Double): (Int, Int) = {
        if (head.getTick > time) {
            (-1, 0)
        } else if (last.getTick < time) {
            (size - 1, size)
        } else {
            var startIndex: Int = 0
            var endIndex: Int = size - 1
            (1 until size).foreach(
                _ => {
                    val mid = (startIndex + endIndex) / 2
                    val currentTick = data(mid).getTick
                    if (currentTick < time) startIndex = mid
                    else if (currentTick > time) endIndex = mid
                    else {
                        return (mid, mid)
                    }
                    if ((endIndex - startIndex) == 1) {
                        return (startIndex, endIndex)
                    }
                }
            )
            (-1, -1)
        }
    }

    /**
      * Append unit to time series
      *
      * @param timeUnit
      */
    def append(timeUnit: T*): Unit = {
        data append (timeUnit: _*)
        sortByTick()
    }

    /**
      * Append many unit to time series
      *
      * @param timeSeries
      */
    def append(timeSeries: TraversableOnce[T]): Unit = {
        data appendAll timeSeries
        sortByTick()
    }

    override def iterator: Iterator[T] = data.iterator
}
