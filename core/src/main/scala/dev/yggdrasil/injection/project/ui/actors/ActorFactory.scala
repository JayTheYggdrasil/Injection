package dev.yggdrasil.injection.project.ui.actors

import dev.yggdrasil.injection.framework.ecs.{Entity, GameState}
import dev.yggdrasil.injection.framework.ui.{ECSActor, ECSActorFactory}

object ActorFactory extends ECSActorFactory {
  override def makeOne(entity: Entity, gameState: GameState): ECSActor = entity match {
    case space if SpaceActor.makesFrom(space) => SpaceActor(space)
    case arrow if ArrowActor.makesFrom(arrow) => ArrowActor(arrow, gameState)
    case _ => throw new IllegalStateException("Entity had the visual component, but an actor could not be created.")
  }
}
