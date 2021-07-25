package dev.yggdrasil.injection.framework.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.{Actor, InputEvent}
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class ECSActor(val texture: TextureRegion, val onClick: () => Unit, val drawOrder: Int) extends Image(texture) {
  setWidth(texture.getRegionWidth)
  setHeight(texture.getRegionHeight)
  addListener(new ClickListener() {
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = onClick()
  })
}
