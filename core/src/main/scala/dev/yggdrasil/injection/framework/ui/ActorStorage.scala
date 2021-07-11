package dev.yggdrasil.injection.framework.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import dev.yggdrasil.injection.framework.ecs.Entity

class ActorStorage private(var map: Map[Int, Actor]) {
  def addActor(id: Int, actor: Actor): Unit = map = map.updated(id, actor)
  def fromEntity(ent: Entity): Option[Actor] = map.get(ent.id)
  def removeEntity(ent: Entity): Option[Actor] = {
    val ret = fromEntity(ent)
    map = map.removed(ent.id)
    ret
  }
}

object ActorStorage {
  val empty = new ActorStorage(Map.empty)
}