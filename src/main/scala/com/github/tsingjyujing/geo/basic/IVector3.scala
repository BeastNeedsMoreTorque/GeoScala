package com.github.tsingjyujing.geo.basic

import com.github.tsingjyujing.geo.basic.operations._
import com.github.tsingjyujing.geo.element.immutable.Vector3

/**
  * vector in 2d Euclid space
  */
trait IVector3
    extends IVector
        with InnerProductable[IVector3]
        with Angleable[IVector3]
        with DistanceMeasurable[IVector3]
        with OuterProductable[IVector3, IVector3]
        with Addable[IVector3] {

    def getX: Double

    def getY: Double

    def getZ: Double

    override def norm(n: Double): Double = {
        math.pow(math.pow(getX, n) + math.pow(getY, n) + math.pow(getZ, n), 1.0 / n)
    }

    override def norm2: Double = math.sqrt(IVector3.getPow2Sum(getX, getY, getZ))

    /**
      * Get distance from this to point or point to this (should be same)
      *
      * @param point geo point
      * @return
      */
    override def to(point: IVector3): Double = math.sqrt(IVector3.getPow2Sum(getX - point.getX, getY - point.getY, getZ - point.getZ))

    /**
      * Get inner product of two vectors
      *
      * @param point
      * @return
      */
    override def innerProduct(point: IVector3): Double = IVector3.innerProduct3(this, point)

    /**
      * Get cosed value of two points
      *
      * @param x compare unit
      * @return
      */
    override def cosAngle(x: IVector3): Double = IVector3.cosAngle(this, x)

    /**
      * Get array format value
      * very useful in Matlab
      *
      * @return
      */
    def getVector: Array[Double] = Array(getX, getY, getZ)

    override def iterator: Iterator[Double] = getVector.iterator

    override def outProduct(x: IVector3): IVector3 = Vector3(
        0.0D + this.getY * x.getZ - this.getZ * x.getY,
        0.0D - this.getX * x.getZ + this.getZ * x.getX,
        0.0D + this.getX * x.getY - this.getY * x.getX
    )

    override def +(v: IVector3): IVector3 = Vector3(v.getX + getX, v.getY + getY, v.getZ + getZ)

    def -(v: IVector3): IVector3 = Vector3(-v.getX + getX, -v.getY + getY, -v.getZ + getZ)

    def /(v: Double): IVector3 = Vector3(getX / v, getY / v, getZ / v)

    def *(v: Double): IVector3 = Vector3(getX * v, getY * v, getZ * v)
}

object IVector3 {
    def getPow2Sum(x: Double, y: Double, z: Double): Double = x * x + y * y + z * z

    def innerProduct3(point1: IVector3, point2: IVector3): Double = point1.getX * point2.getX + point1.getY * point2.getY + point1.getZ * point2.getZ

    def cosAngle(point1: IVector3, point2: IVector3): Double = (point1 innerProduct point2) / (point1.norm2 * point2.norm2)
}
