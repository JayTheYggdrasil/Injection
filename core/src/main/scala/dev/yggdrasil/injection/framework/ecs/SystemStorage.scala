package dev.yggdrasil.injection.framework.ecs

case class SystemStorage protected(protected val list: List[System] = List.empty) {
  def updatedByName(system: System): SystemStorage = {
    require(list.count(_.name == system.name) == 1, "Invalid state multiple systems have the same name")
    val l = list.map(s => if(s.name == system.name) system else s)
    SystemStorage(l)
  }
  def removedByName(system: System): SystemStorage = {
    require(list.count(_.name == system.name) == 1, "Invalid state multiple systems have the same name")
    val l = list.filterNot(_.name == system.name)
    SystemStorage(l)
  }
  def updated(system: System): SystemStorage = {
    val l = list.map(s => if(s == system) system else s)
    SystemStorage(l)
  }
  def removed(system: System): SystemStorage = {
    val l = list.filterNot(_ == system)
    SystemStorage(l)
  }

  def insertLast(system: System): SystemStorage = SystemStorage(list.appended(system))

  def toList: List[System] = list
}

object SystemStorage {
  def empty: SystemStorage = SystemStorage()
  implicit def fromList(list: List[System]): SystemStorage = SystemStorage(list)
  implicit def toList(systemStorage: SystemStorage): List[System] = systemStorage.toList
}
