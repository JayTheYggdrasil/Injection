package dev.yggdrasil.injection.project.ecs

import dev.yggdrasil.injection.util.{InfiniteGrid, LoopedVector}
import dev.yggdrasil.injection.framework.ecs.Component
import dev.yggdrasil.injection.framework.ecs.Entity

object Components {
  case class Direction(x: Int, y: Int) extends Component
  object Direction {
    val UP: Direction = Direction(0, 1)
    val DOWN: Direction = Direction(0, -1)
    val LEFT: Direction = Direction(-1, 0)
    val RIGHT: Direction = Direction(1, 0)
  }

  case class Arrow() extends Component

//  case class Sequence(seq: LoopedVector[Int]) extends Component

  case class GridPosition(x: Int, y: Int, gridId: Int) extends Component

  case class Grid(origin: InfiniteGrid[Entity]) extends Component

  case class Pushable() extends Component

  case class Space() extends Component

  case class KeyHandler(keySet: Set[Int], hasInput: Boolean) extends Component {
    def use(code: Int): KeyHandler = KeyHandler(keySet - code, keySet.nonEmpty)

    def get(code: Int): Boolean = keySet.contains(code)
  }

  case class ClickHandler(clicked: Boolean, entityID: Int, x: Option[Int] = None, y: Option[Int] = None) extends Component {
    def click(): ClickHandler = ClickHandler(false, entityID, x, y)
  }
}
