package io.github.whoisamyy.editor.objects;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import io.github.whoisamyy.components.TriggerBox;
import io.github.whoisamyy.editor.components.EditorObjectComponent;
import io.github.whoisamyy.objects.GameObject;
import io.github.whoisamyy.utils.EditorObject;
import io.github.whoisamyy.utils.Utils;
import io.github.whoisamyy.utils.input.MouseClickEvent;

@EditorObject
public class MouseCursor extends GameObject {
    @Override
    protected void awake() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f / Utils.PPU, 0.5f / Utils.PPU);
        TriggerBox tb;
        (tb = addComponent(new TriggerBox(shape))).getBody().setType(BodyDef.BodyType.DynamicBody);
        tb.getBody().setGravityScale(0);
    }

    @Override
    protected void start() {
        removeComponent(EditorObjectComponent.EditorTriggerBox.class);
        removeComponent(EditorObjectComponent.class);
    }

    @Override
    protected void update() {
        MouseClickEvent event = getMouseMoveEvent();
        if (event!=null) {
            transform.pos.set(event.getMousePosition());
        }
    }
}
