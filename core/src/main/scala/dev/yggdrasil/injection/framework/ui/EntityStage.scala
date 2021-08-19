package dev.yggdrasil.injection.framework.ui

import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}

class EntityStage extends Stage {
  def removeActor(actor: ECSActor): Unit = actors = actors.removed(actor.id)

  private var actors: Map[Int, ECSActor] = Map.empty

  override def addActor(actor: Actor): Unit = {
    actor match {
      case a: ECSActor => actors = actors.updated(a.id, a)
      case _ => ()
    }

    super.addActor(actor)
  }

  def getActor(id: Int): Option[ECSActor] = actors.get(id)
}
