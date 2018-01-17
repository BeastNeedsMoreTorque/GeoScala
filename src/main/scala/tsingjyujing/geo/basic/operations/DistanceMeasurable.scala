package tsingjyujing.geo.basic.operations

/**
  * Distance between type T and this
  *
  * @tparam T type of the object to compare
  */
trait DistanceMeasurable[T <: DistanceMeasurable[T]] {

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    def to(point: T): Double
}
