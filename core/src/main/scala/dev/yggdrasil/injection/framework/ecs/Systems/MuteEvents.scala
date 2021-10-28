package dev.yggdrasil.injection.framework.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.{GameState, System}

import scala.reflect.ClassTag

case class MuteEvents(override val name: String, filter: EventSystem => Boolean) extends System(name) {
  override def apply(delta: Float, gameState: GameState): GameState = {
    val (es, ss) = gameState.unpack
    GameState(es, ss.filterNot(_ match {
      case a: EventSystem => filter(a)
      case _ => false
    }))
  }
}
