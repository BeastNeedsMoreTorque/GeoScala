package tsingjyujing.geo.basic


import tsingjyujing.geo.basic.operations._

trait IVector2
    extends InnerProductable[IVector2]
        with Normable
        with Angleable[IVector2]
        with DistanceMeasurable[IVector2]
        with Iterable[Double] {

    def getX: Double

    def getY: Double


    override def norm(n: Double): Double = {
        math.pow(math.pow(getX, n) + math.pow(getY, n), 1.0 / n)
    }

    override def norm2: Double = math.sqrt(IVector2.getPow2Sum(getX, getY))

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def to(point: IVector2): Double = math.sqrt(IVector2.getPow2Sum(getX - point.getX, getY - point.getY))

    override def innerProduct(point: IVector2): Double = IVector2.innerProduct2(this, point)

    override def conAngle(x: IVector2): Double = IVector2.cosAngle(this, x)

    def getVector: Array[Double] = Array(getX, getY)

    override def iterator: Iterator[Double] = getVector.iterator


}

object IVector2 {
    def getPow2Sum(x: Double, y: Double): Double = x * x + y * y

    def innerProduct2(point1: IVector2, point2: IVector2): Double = point1.getX * point2.getX + point1.getY * point2.getY

    def cosAngle(point1: IVector2, point2: IVector2): Double = (point1 innerProduct point2) / (point1.norm2 * point2.norm2)
}
