package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.operations.IValue
import tsingjyujing.geo.basic.timeseries.ITick

class TimeElement[T](val time: Double, val value: T) extends ITick with IValue[T] {
    override def getTick: Double = time

    override def setTick(tick: Double): Unit = throw new Exception("Unsupported method")

    override def getValue: T = value
}
