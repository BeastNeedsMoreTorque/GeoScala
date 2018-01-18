package tsingjyujing.geo.element.immutable

import tsingjyujing.geo.basic.{IGeoPoint, IValue}

final class GeoPointValued[T](longitude: Double, latitude: Double, value: T) extends IGeoPoint with IValue[T] {
    override def getLongitude: Double = longitude

    override def getLatitude: Double = latitude

    override def getValue: T = value
}
