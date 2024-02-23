package io.github.whoisamyy.utils.render.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.whoisamyy.editor.Editor;
import io.github.whoisamyy.katarine.Game;

public class FilledRectShape extends RectShape {
    private Color color1, color2, color3, color4;

    public FilledRectShape(float width, float height) {
        super(width, height);
        this.color1 = Color.WHITE;
        this.color2 = color1;
        this.color3 = color2;
        this.color4 = color3;
    }

    public FilledRectShape(float width, float height, Color color) {
        super(width, height);
        this.color1 = color;
        this.color2 = color1;
        this.color3 = color2;
        this.color4 = color3;
    }

    /**
     * Constructs a filled rectangle shape with color gradient (bottom to top) of color1 and color2
     * @param w
     * @param h
     * @param color1
     * @param color2
     */
    public FilledRectShape(float w, float h, Color color1, Color color2) {
        super(w, h);
        this.color1 = color1;
        this.color2 = color1;
        this.color3 = color2;
        this.color4 = color2;
    }

    public FilledRectShape(float width, float height, Color color1, Color color2, Color color3, Color color4) {
        super(width, height);
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
    }

    @Override
    public void draw() {
        getShapeRenderer().set(ShapeRenderer.ShapeType.Filled);
        float offsetX, offsetY;
        if (Editor.getInstance() != null) {
            offsetX = Editor.getInstance().getWidth() / 2;
            offsetY = Editor.getInstance().getHeight() / 2;
        } else {
            offsetX = Game.getInstance().getWidth()/2;
            offsetY = Game.getInstance().getHeight()/2;
        }

        shapeRenderer.rect((x-w/2*scaleX)+offsetX, (y-h/2*scaleY)+offsetY, w*scaleX, h*scaleY, color1, color2, color3, color4);
    }
}
