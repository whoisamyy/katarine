package io.github.whoisamyy.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.whoisamyy.components.Resizable;
import io.github.whoisamyy.components.Sprite;
import io.github.whoisamyy.editor.Editor;
import io.github.whoisamyy.katarine.Game;
import io.github.whoisamyy.utils.Utils;
import io.github.whoisamyy.utils.math.shapes.Rect;
import io.github.whoisamyy.utils.render.RectOwner;

public class CheckBox extends UiObject implements RectOwner, Resizable {
    Rect checkBoxRect;

    boolean isActive = false;
    public Color primaryColor = new Color(0.8f, 0.8f, 0.8f, 1);
    public Color textColor = Color.WHITE;
    public Color secondaryColor = Color.WHITE;
    Text checkBoxText;
    public float fontSize = 2;
    public String font = "fonts/Roboto-Medium.ttf";
    public String text = "Button";
    public Sprite checkBox;

    public final Vector2 textPadding = new Vector2(0.05f, 0.05f);
    public Anchor anchor = Anchor.CENTER;

    @Override
    public void awake() {
        checkBoxText = new Text(font, fontSize, Color.BLACK, 1 / Utils.PPU, Color.BLACK, true);
    }

    @Override
    public void start() {
        checkBoxText.text = text;
        checkBoxText.setSizeXY(fontSize);
        checkBoxRect = new Rect(transform.pos.x, transform.pos.y, transform.scale.x, transform.scale.y);
        checkBox = gameObject.addComponent(new Sprite(new Texture(Gdx.files.internal("whitepx.png")), transform.scale.y/2, transform.scale.y/2));
        gameObject.addComponent(checkBoxText);

        checkBox.updateOrder = checkBoxText.updateOrder+1;

        //if (transform.scale.x/2 < checkBoxText.getTextWidth() + transform.scale.y/2) {
        //    transform.scale.x = (checkBoxText.getTextWidth() + transform.scale.y)/2;
        //    checkBoxRect.w = transform.scale.x;
        //}
        checkBox.relativePosition.sub(transform.scale.x/2 - transform.scale.y/2, 0);

        if (transform.scale.x < checkBoxText.getTextWidth() + transform.scale.y) {
            transform.scale.x = checkBoxText.getTextWidth() + transform.scale.y;
            checkBoxRect.w = transform.scale.x;
        }

        super.start();

        checkBoxText.setColor(textColor);

    }

    public final void setTextColor(Color textColor) {
        this.textColor = textColor;
        checkBoxText.setColor(textColor);
    }

    @Override
    public void update() {
        checkBoxText.setSizeXY(fontSize);
        super.update();
        checkBoxRect.x = transform.pos.x;
        checkBoxRect.y = transform.pos.y;
        checkBoxRect.w = transform.scale.x;
        checkBoxRect.h = transform.scale.y;

        switch (anchor) {
            case TOP_LEFT ->      checkBoxText.getPos().set(transform.pos.cpy().sub((transform.scale.x / 2) - checkBoxText.getTextWidth()/2 - textPadding.x - transform.scale.y, -transform.scale.y/2 + checkBoxText.getTextHeight()/2 + textPadding.y));
            case CENTER_LEFT ->   checkBoxText.getPos().set(transform.pos.cpy().sub((transform.scale.x / 2) - checkBoxText.getTextWidth()/2 - textPadding.x - transform.scale.y, 0));
            case BOTTOM_LEFT ->   checkBoxText.getPos().set(transform.pos.cpy().sub((transform.scale.x / 2) - checkBoxText.getTextWidth()/2 - textPadding.x - transform.scale.y, transform.scale.y/2 - checkBoxText.getTextHeight()/2 - textPadding.y));

            case CENTER ->        //noinspection SuspiciousNameCombination
                                  checkBoxText.getPos().set(new Vector2(transform.scale.y, 0));
            case TOP_CENTER ->    //noinspection SuspiciousNameCombination
                                  checkBoxText.getPos().set(transform.pos.cpy().add(transform.scale.y, transform.scale.y/2 - checkBoxText.getTextHeight()/2 + textPadding.y));
            case BOTTOM_CENTER -> checkBoxText.getPos().set(transform.pos.cpy().sub(-transform.scale.y, transform.scale.y/2 - checkBoxText.getTextHeight()/2 + textPadding.y));

            case TOP_RIGHT ->     checkBoxText.getPos().set(transform.pos.cpy().add((transform.scale.x / 2) - checkBoxText.getTextWidth()/2 - textPadding.x, transform.scale.y/2 - checkBoxText.getTextHeight()/2 - textPadding.y));
            case CENTER_RIGHT ->  checkBoxText.getPos().set(transform.pos.cpy().add((transform.scale.x / 2) - checkBoxText.getTextWidth()/2 - textPadding.x, 0));
            case BOTTOM_RIGHT ->  checkBoxText.getPos().set(transform.pos.cpy().add((transform.scale.x / 2) - checkBoxText.getTextWidth()/2 - textPadding.x, -transform.scale.y/2 + checkBoxText.getTextHeight()/2 + textPadding.y));
        }

        checkBox.relativePosition.set(-transform.scale.x/2 + transform.scale.y/2, 0);

        if (Editor.getInstance()!=null || Game.getInstance().isEditorMode()) return;
        if (getMouseMoveEvent()==null) return;

        // monstrocity
        //noinspection ConditionalExpressionWithIdenticalBranches
        if (checkBoxRect.isPointInRect(getMouseMoveEvent().getMousePosition().x,
                (Editor.getInstance()!=null?Editor.getInstance().getHeight():Game.getInstance().getHeight())-getMouseMoveEvent().getMousePosition().y)

                && isButtonJustPressed(Input.Buttons.LEFT)) {
            isActive = !isActive;
        }

        if (isActive) {
            checkBox.getSprite().setColor(secondaryColor);
        } else {
            checkBox.getSprite().setColor(primaryColor);
        }
    }

    public final boolean isActive() {
        return isActive;
    }

    public final void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public Rect getRect() {
        return checkBoxRect;
    }
}
