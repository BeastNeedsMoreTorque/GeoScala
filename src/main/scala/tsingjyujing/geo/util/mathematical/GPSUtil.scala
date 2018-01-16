package tsingjyujing.geo.util.mathematical

import tsingjyujing.geo.basic.geounit.{GeodesicLine, GeometryPoint}
import tsingjyujing.geo.scala.basic.geounit.GeometryPoint
import tsingjyujing.geo.basic.timeseries.ITick
import tsingjyujing.geo.util.mathematical.VectorUtil.outerProduct

import scala.collection.{immutable, mutable}
import scala.util.control.Breaks

/**
  * @author tsingjyujing
  */
object GPSUtil {
    
    def steeringAngleGeodesic[T](
                                  p1: GeometryPoint[T],
                                  p2: GeometryPoint[T],
                                  p3: GeometryPoint[T]
                                ): Double = {
        
        val v1 = p1.get3DPos()
        val v2 = p2.get3DPos()
        val v3 = p3.get3DPos()
        GeometryUtil.angle(
            outerProduct(
                v1,
                v2
            ),
            outerProduct(
                v2,
                v3
            )
        )
    }
    
    /*
     * 近似欧氏空间求转弯角，如无特殊需求不再使用
     */
    @deprecated(message = "Don't using Euclidean angle algorithm no more, instead it of steeringAngleGeodesic")
    def steeringAngleEuclidean[T](
                                   p1: GeometryPoint[T],
                                   p2: GeometryPoint[T],
                                   p3: GeometryPoint[T]
                                 ): Double = {
        val latRatio = math.cos((p1.getLatitude + p2.getLatitude + p3.getLatitude) * math.Pi / 3.0 / 180.0)
        GeometryUtil.angle(
            Array((p2.getLongitude - p1.getLongitude) * latRatio, p2.getLatitude - p1.getLatitude),
            Array((p3.getLongitude - p2.getLongitude) * latRatio, p3.getLatitude - p2.getLatitude)
        )
    }
    
    def GPSMile[T](gpsIter: Iterator[GeometryPoint[T]]): Double = gpsIter.sliding(2, 1).map {
        case Seq(a, b) => a.distance(b)
    }.sum
    
    def sparsifyGPS[T <: ITick](
                                    gpsArray: IndexedSeq[GeometryPoint[T]],
                                    sparsityParam: Double,
                                    sparsitySearchParam: Int
                                  ) = sparsifyGPSIndexed(
        gpsArray, sparsityParam, sparsitySearchParam
    ).map(
        i => gpsArray(i)
    )
    
    def sparsifyGPSIndexed(
                              gpsArray: IndexedSeq[GeometryPoint[_ <: ITick]],
                              sparsityParam: Double,
                              sparsitySearchParam: Int
                          ): IndexedSeq[Int] = {
        if (gpsArray.size < 10) return gpsArray.indices
        val returnList = new mutable.MutableList[Int]
        returnList += 0
        var nowIndex = 0
        val getDistance = (startIndex: Int, endIndex: Int) => {
            val line = new GeodesicLine(
                gpsArray(startIndex),
                gpsArray(endIndex)
            )
            gpsArray.slice(startIndex + 1, endIndex).map((point) => line.distance(point)).max
        }
        
        val loop = Breaks
        loop.breakable(
            while (true) {
                val indexFound = SeqUtil.searchInSorted(
                    (i) => getDistance(nowIndex, i),
                    sparsityParam,
                    nowIndex + 2,
                    math.min(
                        nowIndex + sparsitySearchParam,
                        gpsArray.size - 2
                    )
                )._1
                if (indexFound >= gpsArray.size - 4) {
                    returnList += gpsArray.size - 1
                    loop.break
                } else {
                    returnList += indexFound
                    nowIndex = indexFound
                }
            }
        )
        returnList.toIndexedSeq
    }
    
    def getMaxDistanceToLine(
                              gpsArray: IndexedSeq[GeometryPoint[_ <: ITick]]
                            ): IndexedSeq[Double] = 3.to(gpsArray.size - 2).map(i => {
        val line = new GeodesicLine(
            gpsArray.head,
            gpsArray(i)
        )
        2.until(i).map(x => line.distance(gpsArray(x))).max
    })
    
    def getAverageDistanceToLine(
                                  gpsArray: IndexedSeq[GeometryPoint[_ <: ITick]]
                                ): IndexedSeq[Double] = {
        3.to(gpsArray.size).map(i => {
            val line = new GeodesicLine(
                gpsArray.head,
                gpsArray(i)
            )
            val distance_list = 2.until(i).map(x => line.distance(gpsArray(x)))
            distance_list.sum / distance_list.size
        })
    }
    
    def cleanGPS[T <: ITick](
                                 gpsArray: Iterable[GeometryPoint[T]],
                                 velocityLimit: Double
                               ): immutable.IndexedSeq[GeometryPoint[T]] = gpsArray.iterator.sliding(2, 1).filter(data => {
        (data.head.distance(data.last) / math.abs(data.head.getUserInfo.getTick - data.last.getUserInfo.getTick)) < velocityLimit
    }).map(_.head).toIndexedSeq
    
}
