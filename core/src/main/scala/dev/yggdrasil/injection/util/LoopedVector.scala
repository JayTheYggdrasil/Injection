package dev.yggdrasil.injection.util

import scala.annotation.tailrec

case class LoopedVector[A](s: Vector[A]) {
  def apply(i: Int): A = {
    val numSequences = s.length
    s(i % numSequences)
  }
}

object LoopedVector {
  implicit def loopedVector2Vector[A](l: LoopedVector[A]): Vector[A] = l.s

  private def gcd(values: Set[Int]): Int = {
    values.reduce((a, b) => gcd(a, b))
  }

  @tailrec
  private def gcd(v1: Int, v2: Int): Int = {
    if(v1 == 0 || v2 == 0) return v1 + v2

    val min = v1 min v2
    val max = v1 max v2

    gcd(max % min, min)
  }

  def combine[A](loopedVectors: LoopedVector[A]*): LoopedVector[A] = {
    val lengths = loopedVectors.map(_.length).toSet
    val LCM = lengths.product / gcd(lengths) // euclidean algorithm
    // LCM represents how many cycles of the top level sequence until we're reset.
    LoopedVector(Range(0, LCM).toVector.flatMap(i => loopedVectors.map(v => v(i))))
  }

  def main(args: Array[String]): Unit = {
    val loopedVector: LoopedVector[Int] = LoopedVector[Int](Vector(1, 2, 3, 4))
    val loopedVector2: LoopedVector[Int] = LoopedVector[Int](Vector(5, 6))
    val loopedVector3: LoopedVector[Int] = LoopedVector[Int](Vector(7, 8))

    val combined: LoopedVector[Int] = LoopedVector.combine(loopedVector, loopedVector2, loopedVector3)

    println(combined)
    print(combined.length)
  }
}
