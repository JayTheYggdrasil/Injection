package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Systems.EventSystem
import dev.yggdrasil.injection.framework.ecs.{Entity, EntityStorage, GameState}
import dev.yggdrasil.injection.project.ecs.Components.{Direction, GridPosition, Sequence}
import dev.yggdrasil.injection.project.ecs.Entities.{arrow, clear, parentOf, putGridEntity, sequenceAppend}

object EditorSystems {
  case class IndexUp(entityID: Int) extends EventSystem("IndexUp") {
    override def handleEvent(gameState: GameState): GameState = gameState
  }

  case class IndexDown(entityID: Int) extends EventSystem("IndexDown") {
    override def handleEvent(gameState: GameState): GameState = gameState
  }

  case class MakeArrow(spaceID: Int) extends EventSystem("MakeArrow") {
    override def handleEvent(gameState: GameState): GameState = {
      val (storage, _) = gameState.unpack

      // Get all empty spaces
      val clickedSpace: Entity = storage(spaceID)

      val newArrow = arrow(Direction.UP)
      val onGrid = putGridEntity(newArrow, clickedSpace(classOf[GridPosition]), storage)
      val inSequence = sequenceAppend(onGrid(newArrow.id), 1, onGrid)

      gameState.copy(storage = inSequence)
    }
  }

  case class RotateEntityLeft(entityID: Int) extends EventSystem("RotateEntityLeft") {
    override def handleEvent(gameState: GameState): GameState = {
      val (storage, _) = gameState.unpack

      val entity = storage(entityID)
      val newStorage = entity(classOf[Direction]) match {
        case Direction.DOWN => storage.updated(entity.updated(Direction.RIGHT))
        case Direction.RIGHT => storage.updated(entity.updated(Direction.UP))
        case Direction.UP => storage.updated(entity.updated(Direction.LEFT))
        case Direction.LEFT => storage.updated(entity.updated(Direction.DOWN))
      }

      gameState.copy(storage = newStorage)
    }
  }

  case class RotateEntityRight(entityID: Int) extends EventSystem("RotateEntityLeft") {
    override def handleEvent(gameState: GameState): GameState = {
      val (storage, _) = gameState.unpack

      val entity = storage(entityID)
      val newStorage = entity(classOf[Direction]) match {
        case Direction.DOWN => storage.updated(entity.updated(Direction.LEFT))
        case Direction.RIGHT => storage.updated(entity.updated(Direction.DOWN))
        case Direction.UP => storage.updated(entity.updated(Direction.RIGHT))
        case Direction.LEFT => storage.updated(entity.updated(Direction.UP))
      }

      gameState.copy(storage = newStorage)
    }
  }

  case class RemoveArrow(entityID: Int) extends EventSystem("RemoveArrow") {
    override def handleEvent(gameState: GameState): GameState = {
      val (storage, _) = gameState.unpack

      val entity = storage(entityID)

      val withoutGrid = removeFromGrid(entity, storage)
      val withoutSequence = removeFromSequence(entity, withoutGrid)
      val newStorage = withoutSequence.remove(entity.id)

      gameState.copy(storage = newStorage)
    }

    def removeFromSequence(entity: Entity, entityStorage: EntityStorage): EntityStorage = {
      val sequence = entity.getInstance(classOf[Sequence]).getOrElse(return entityStorage)

      // Fix indexes impacted by deletion.
      def decreaseIndices(entityID: Int, storage: EntityStorage): EntityStorage = {
        val entity = storage(entityID)
        val seq = entity.getInstance(classOf[Sequence])
        val newE = seq.map(s => entity.updated(s.copy(index = s.index - 1)))
        val newStorage = newE.map(storage.updated).getOrElse(storage)
        seq.flatMap(_.next).map(decreaseIndices(_, newStorage)).getOrElse(newStorage)
      }

      val storage = decreaseIndices(entity.id, entityStorage)


      val next = sequence.next.map(storage(_))
      val previous = sequence.previous.map(storage(_))

      val nextID = next.map(_.id)
      val previousID = previous.map(_.id)

      val newP = previous.map(p => p.updated(p(classOf[Sequence]).copy(next = nextID)))
      val newN = next.map(n => n.updated(n(classOf[Sequence]).copy(previous = previousID)))

      val withP = newP match {
        case Some(p) => storage.updated(p)
        case None => storage
      }

      val withN = newN match {
        case Some(n) => withP.updated(n)
        case None => withP
      }

      withN
    }

    def removeFromGrid(entity: Entity, storage: EntityStorage): EntityStorage = {
      val parent = parentOf(entity, storage).getOrElse(return storage)
      storage.updated(clear(parent))
    }
  }
}
