package dev.yggdrasil.injection.framework.ecs

import dev.yggdrasil.injection.framework.ecs.Component.ComponentMap
import dev.yggdrasil.injection.project.ecs.Components.{Arrow, Direction}

case class Entity(id: Int, componentMap: ComponentMap){
  override def equals(obj: Any): Boolean = obj match {
    case e: Entity => e.id == this.id
    case _ => false
  }

  override def hashCode(): Int = id.hashCode()

  override def toString: String = id.toString

  def getInstance[B <: Component](c: Class[B]): Option[B] = componentMap.get(c) match {
    case v: Option[B] => v
    case None => None
  }

  def updated[B <: Component](value: B): Entity = Entity(id, componentMap.updated(value.getClass, value))
  def removed[B <: Component](c: Class[B]): Entity = Entity(id, componentMap.removed(c))

  def apply[B <: Component](c: Class[B]): B = getInstance(c).get
}

object Entity {
  var id = 0
  def fromComponents(components: Component*): Entity = {
    id += 1
    Entity(id, components.map(c => (c.getClass, c)).toMap)
  }

  def main(args: Array[String]): Unit = {
    val e = Entity.fromComponents(Arrow(), Direction.UP)

  }
}
