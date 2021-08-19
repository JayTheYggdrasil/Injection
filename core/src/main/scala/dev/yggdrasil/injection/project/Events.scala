package dev.yggdrasil.injection.project

import dev.yggdrasil.injection.framework.events.Event

object Events {
  case class Clicked(entityID: Int) extends Event
  case class MakeArrow(spaceID: Int) extends Event
  case class TurnArrowLeft(arrowID: Int) extends Event
  case class TurnArrowRight(arrowID: Int) extends Event
  case class IndexUp(entityID: Int) extends Event
  case class IndexDown(entityID: Int) extends Event
  case class Select(entityID: Int) extends Event
}
