package dev.yggdrasil.injection.framework.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import dev.yggdrasil.injection.framework.ecs.{Entity, GameState}
import dev.yggdrasil.injection.framework.ui.Components.Visual

import scala.reflect.classTag

abstract class ECSActor protected(val id: Int, texture: TextureRegion) extends EventActor(texture)
{
  def update(gameState: GameState): Unit

  override def remove(): Boolean = {
    super.getStage match {
      case fancy: EntityStage => fancy.removeActor(this)
      case _ => ()
    }

    super.remove()
  }

  def scaleMultiply(scaleX: Float, scaleY: Float): Unit =
    setScale(scaleX * getScaleX, scaleY * getScaleY)
}

trait ECSActorFactory {
  def appliesTo(gameState: GameState): Set[Entity] = gameState.storage.join(classTag[Visual])
  def makeOne(entity: Entity, gameState: GameState): ECSActor
  def makeActors(gameState: GameState): Set[ECSActor] = appliesTo(gameState).map(makeOne(_, gameState))
}
