package dev.yggdrasil.injection.util

import dev.yggdrasil.injection.project.ecs.Components.Direction

class InfiniteGrid[A](private val default: (Int, Int) => A, private val location: (Int, Int) = (0, 0), i: Map[(Int, Int), A] = Map.empty[(Int, Int), A], v: Option[A] = None, cachedDefaults: Map[(Int, Int), A] = Map.empty[(Int, Int), A]) {
  val (_x, _y) = location

  private val defaults = {
    if(cachedDefaults contains(location))
      cachedDefaults
    else
      cachedDefaults.updated(location, default(_x, _y))
  }

  val allDefaults: Iterable[A] = cachedDefaults.values
  val value: A = v.getOrElse(defaults(location))
  private val index = i.updated(location, value)


  def updated(newValue: A): InfiniteGrid[A] = new InfiniteGrid[A](default, location, index, Some(newValue), cachedDefaults = defaults)
  def clear(): InfiniteGrid[A] = updated(defaults(_x, _y))

  def toLeft: InfiniteGrid[A] = toLocation(_x - 1, _y)
  def toRight: InfiniteGrid[A] = toLocation(_x + 1, _y)
  def toUp: InfiniteGrid[A] = toLocation(_x, _y + 1)
  def toDown: InfiniteGrid[A] = toLocation(_x, _y - 1)

  def toDirection(d: Direction): InfiniteGrid[A] = toLocation(_x + d.x, _y + d.y)

  def toLocation(x: Int, y: Int): InfiniteGrid[A] = new InfiniteGrid[A](default, (x, y), index, index.get((x, y)), defaults)


  override def toString: String = {
    val (xs, ys) = (index.keySet.map(_._1), index.keySet.map(_._2))
    println("TL: (" + xs.min + ", " + ys.max + "); BR: (" + xs.max + ", " + ys.min + ")")
    toLocation(xs.min, ys.max).stringAll(xs.max, ys.min)
  }

  private def stringAll(endX: Int, endY: Int): String =
    stringRow(endX) + (if(_y > endY) "\n" + toDown.stringAll(endX, endY) else "")

  private def stringRow(endX: Int): String =
    value.toString + (if(_x < endX) toRight.stringRow(endX) else "")
}

object InfiniteGrid {
  def fromJson[A](StringToA: String => A): InfiniteGrid[A] = ???

  def toJson[A](AToString: A => String): String = ???

  def main(args: Array[String]): Unit = {
    val infiniteGrid: InfiniteGrid[Int] = InfiniteGrid.empty((x, y) => 0)
    val infiniteGridWith1 = infiniteGrid.updated(1)
    val infiniteGridExpandedRight = infiniteGridWith1.clear().toRight.updated(1)
    println("thing: " + infiniteGridExpandedRight.location)
    val infiniteGridExpandedRightUpUp = infiniteGridExpandedRight.clear().toUp.toUp.updated(1)

    println(infiniteGrid.toString + "\n-----------------")
    println(infiniteGridWith1.toString + "\n-----------------")
    println(infiniteGridExpandedRight.toString + "\n-----------------")
    println(infiniteGridExpandedRightUpUp.toString + "\n-----------------")
  }

  def empty[A](default: (Int, Int) => A) = new InfiniteGrid[A](default, (0, 0))
}
