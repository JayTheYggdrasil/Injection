package dev.yggdrasil.injection.framework.ecs

import dev.yggdrasil.injection.framework.ecs.Component.ComponentMap

import scala.reflect.{ClassTag}

case class Entity(id: Int, componentMap: ComponentMap){
  override def equals(obj: Any): Boolean = obj match {
    case e: Entity => e.id == this.id
    case _ => false
  }

  override def hashCode(): Int = id.hashCode()

  override def toString: String = id.toString

  def getInstance[B <: Component](implicit tag: ClassTag[B]): Option[B] = componentMap.get(tag) match {
    case v: Option[B] => v
    case None => None
  }

  def updated[B <: Component](value: B)(implicit tag: ClassTag[B]): Entity = Entity(id, componentMap.updated(tag, value))
  def removed[B <: Component](implicit tag: ClassTag[B]): Entity = Entity(id, componentMap.removed(tag))

  def apply[B <: Component](implicit tag: ClassTag[B]): B = getInstance(tag).get

  def contains[B <: Component](implicit tag: ClassTag[B]): Boolean = getInstance(tag).nonEmpty

  def mapComponent[B <: Component](func: B => B)(implicit tag: ClassTag[B]): Entity = getInstance(tag) match {
    case Some(component) => updated[B](func(component))(tag)
    case None => this
  }

  def foreachComponent[B <: Component](func: B => Unit)(implicit tag: ClassTag[B]): Unit = getInstance(tag) match {
    case Some(component) => func(component)
    case None => ()
  }
}

object Entity {
  var id = 0
  def fromComponents(components: Component*): Entity = {
    id += 1
    Entity(id, components.map(c => (ClassTag(c.getClass), c)).toMap)
  }
}
