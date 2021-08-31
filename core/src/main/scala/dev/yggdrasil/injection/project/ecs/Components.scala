package dev.yggdrasil.injection.project.ecs

import dev.yggdrasil.injection.framework.ecs.Component
import dev.yggdrasil.injection.util.LoopedList

object Components {
  case class Direction(x: Int, y: Int) extends Component
  object Direction {
    val UP: Direction = Direction(0, 1)
    val DOWN: Direction = Direction(0, -1)
    val LEFT: Direction = Direction(-1, 0)
    val RIGHT: Direction = Direction(1, 0)
  }

  case class Arrow() extends Component

  case class Sequence(loopID: Int, loop: LoopedList[Int]) extends Component

  case class GridPosition(x: Int, y: Int, gridId: Int) extends Component

  case class GridEntity(parentID: Int) extends Component

  case class Pushable() extends Component

  case class Space(entityRef: Option[Int]) extends Component {
    def clear: Space = Space(None)
  }
}
