package dev.yggdrasil.injection.project.ecs

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.EntityStorage
import dev.yggdrasil.injection.project.ecs.Components.Grid

object Entities {
  def updateStorageWithGrid(storage: EntityStorage, entity: Entity): EntityStorage =
    entity.getInstance(classOf[Grid]).getOrElse(return storage).origin.
      allDefaults.foldLeft(storage)((s, e) => s.updated(e))
}
