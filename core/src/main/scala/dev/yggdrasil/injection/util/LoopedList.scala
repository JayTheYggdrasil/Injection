package dev.yggdrasil.injection.util

trait Looped[A] {
  def next: Looped[A]
  def get: A
}

case class LoopedList[A](val list: List[A], val used: List[A] = Nil) extends Looped[A] {
  override def next: LoopedList[A] = list match {
    case h :: Nil => LoopedList((h :: used).reverse)
    case h :: t => LoopedList(t, h :: used)
    case Nil if !isEmpty => LoopedList(used.reverse)
    case Nil => LoopedList(Nil)
  }

  override def get: A = list.head

  def updatedHead(value: A): LoopedList[A] = copy(list = value :: list.tail)
  def append(value: A): LoopedList[A] = copy(list = list.appended(value))
  def headRemoved: LoopedList[A] = copy(list = list.tail)
  def isEmpty: Boolean = list.isEmpty && used.isEmpty
}

case class LoopsOfLists[A] private (loopedList: LoopedList[Looped[A]]) extends Looped[A] {
  override def next: LoopsOfLists[A] = LoopsOfLists(loopedList.updatedHead(loopedList.get.next).next)

  override def get: A = loopedList.get.get
}

object Looped {
  def combine[A](list: List[Looped[A]]): Looped[A] = LoopsOfLists(LoopedList(list))
}