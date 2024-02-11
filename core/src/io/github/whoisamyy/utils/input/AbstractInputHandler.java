package io.github.whoisamyy.utils.input;

import com.badlogic.gdx.Gdx;
import io.github.whoisamyy.core.KObject;

public abstract class AbstractInputHandler extends KObject {
    private static MouseClickEvent touchDownEvent;
    private static MouseClickEvent touchUpEvent;
    private static MouseClickEvent dragEvent;
    private static MouseClickEvent moveEvent;
    private static MouseClickEvent scrollEvent;

    protected final boolean isKeyPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    protected final boolean isKeysPressed(int... keycodes) {
        boolean result = true;
        for (int i : keycodes) {
            if (!isKeyPressed(i)) {
                result = false;
                break;
            }
        }
        return result;
    }

    protected final boolean isKeyJustPressed(int keycode) {
        return Gdx.input.isKeyJustPressed(keycode);
    }

    protected final void onKeyPressed(int keycode, Action action) {
        if (isKeyPressed(keycode)) action.execute();
    }

    protected final void onKeyJustPressed(int keycode, Action action) {
        if (isKeyJustPressed(keycode)) action.execute();
    }

    protected final boolean isButtonPressed(int button) {
        return Gdx.input.isButtonPressed(button);
    }

    protected final boolean isButtonJustPressed(int button) {
        return Gdx.input.isButtonJustPressed(button);
    }

    protected final MouseClickEvent getMouseClickEvent() {
        return touchDownEvent;
    }

    protected final MouseClickEvent getMouseUnClickEvent() {
        return touchUpEvent;
    }

    protected final MouseClickEvent getMouseDragEvent() {
        return dragEvent;
    }
    protected final MouseClickEvent getMouseMoveEvent() {
        return moveEvent;
    }

    protected final MouseClickEvent getMouseScrollEvent() {
        return scrollEvent;
    }
}
