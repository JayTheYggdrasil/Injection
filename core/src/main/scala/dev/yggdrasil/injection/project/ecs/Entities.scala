package dev.yggdrasil.injection.project.ecs

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.EntityStorage
import dev.yggdrasil.injection.project.ecs.Components.{Arrow, Direction, GridEntity, GridPosition, Pushable, Space}
import dev.yggdrasil.injection.project.ui

object Entities {
  def emptySpace(x: Int, y: Int, gridID: Int): Entity = Entity.fromComponents(
    ui.Global.textures.space,
    ui.Global.SPACE_SHAPE,
    Space(None),
    GridPosition(x, y, gridID)
  )

  def arrow(direction: Direction): Entity = Entity.fromComponents(
    Arrow(),
    direction,
    Pushable(),
    ui.Global.SPACE_SHAPE,
    ui.Global.textures.arrow
  )

  def parentOf(entity: Entity, storage: EntityStorage): Option[Entity] =
    entity.getInstance(classOf[GridEntity]).map(g => storage(g.parentID))

  def onGrid(entity: Entity): Boolean =
    entity.componentMap.contains(classOf[GridEntity])

  def neighborOf(entity: Entity, direction: Direction, storage: EntityStorage): Option[Entity] =
    parentOf(entity, storage).flatMap(e => {
      val pos = e(classOf[GridPosition])
      val targetPos = pos.copy(x = pos.x + direction.x, y = pos.y + direction.y)

      // Would prefer a smarter lookup method.
      spaceAt(targetPos, storage)
    })

  def childOf(entity: Entity, storage: EntityStorage): Option[Entity] =
    entity(classOf[Space]).entityRef.map(storage(_))

  def spaceAt(position: GridPosition, storage: EntityStorage): Option[Entity] =
    storage.join(classOf[Space], classOf[GridPosition]).find(e =>
      e(classOf[GridPosition]) == position
    )

  def emptyGrid(height: Int, width: Int, gridID: Int): (IndexedSeq[Entity], Map[GridPosition, Int]) = {
    val entities = (0 to height).flatMap(y =>
      (0 to width).map(x => {
        emptySpace(x, y, gridID)
      })
    )

    val lookup: Map[GridPosition, Int] = entities.map(e => e(classOf[GridPosition]) -> e.id).toMap

    entities -> lookup
  }

  def putGridEntity(entity: Entity, position: GridPosition, storage: EntityStorage): EntityStorage = {
    spaceAt(position, storage) match {
      case Some(e) => storage
        .updated(e.updated(Space(Some(entity.id)))) // Set the spaces child ID
        .updated(entity.updated(GridEntity(e.id))) // Set the entities parent ID
      case None => storage
    }
  }


  def clear(space: Entity): Entity = space.updated(Space(None))
}
