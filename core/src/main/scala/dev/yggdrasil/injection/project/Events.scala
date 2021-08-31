package dev.yggdrasil.injection.project

import dev.yggdrasil.injection.framework.events.Event

object Events {
  case class MakeArrow(spaceID: Int) extends Event
  case class TurnEntityLeft(entityID: Int) extends Event
  case class TurnEntityRight(entityID: Int) extends Event
  case class IndexUp(entityID: Int) extends Event
  case class IndexDown(entityID: Int) extends Event
  case class Play() extends Event
}
