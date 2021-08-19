package dev.yggdrasil.injection.framework.util

case class ClassMap[Super] protected(classStore: Map[String, Set[_ <: Super]] = Map.empty) {

  private def getWithName[C](clssName: String): Option[Set[C]] = classStore.get(clssName).asInstanceOf[Option[Set[C]]]

  def apply[C <: Super](clss: Class[C]): Set[C] = getWithName[C](clss.getName).getOrElse(Set.empty)

  def remove[C <: Super](value: C): ClassMap[Super] = {
    val _set: Option[Set[C]] = getWithName[C](value.getClass.getName)
    val _store = _set.map(set => classStore.updated(value.getClass.getName, set - value))
    ClassMap(_store.getOrElse(classStore))
  }

  def add[C <: Super](value: C): ClassMap[Super] = {
    val set: Set[C] = getWithName[C](value.getClass.getName).getOrElse(Set.empty)
    val store = classStore.updated(value.getClass.getName, set + value)
    ClassMap(store)
  }
}

object ClassMap {
  def empty[S]: ClassMap[S] = ClassMap[S](Map.empty)
}