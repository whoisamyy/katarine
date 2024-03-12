package io.github.whoisamyy.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.github.whoisamyy.editor.Editor;
import io.github.whoisamyy.utils.render.shapes.CircleShape;

public class UiCircleShape extends UiObject {
    public float radius = 1f;

    UiCircle circle;
    public static class UiCircle extends CircleShape {
        Vector2 pos;

        public UiCircle(float r, Vector2 pos) {
            super(r);
            this.pos = pos;
        }

        public UiCircle(float r, Color color) {
            super(r, color);
        }

        public UiCircle(float x, float y, float r, Color color) {
            super(x, y, r, color);
        }

        public UiCircle(float r) {
            super(r);
        }

        public UiCircle(float x, float y, float r) {
            super(x, y, r);
        }

        @Override
        protected void draw() {
            super.draw();
            this.x = pos.x;
            this.y = pos.y;
        }
    }

    static boolean isEditor;

    static {
        isEditor = Editor.getInstance()!=null;
    }

    @Override
    public void start() {
        super.start();
        circle = new UiCircle(radius, uiPosition);
    }
}
