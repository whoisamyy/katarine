package io.github.whoisamyy.objects;

import com.badlogic.gdx.math.Vector2;
import io.github.whoisamyy.components.Component;
import io.github.whoisamyy.components.RigidBody2D;
import io.github.whoisamyy.components.Transform2D;
import io.github.whoisamyy.editor.Editor;
import io.github.whoisamyy.editor.components.EditorObjectComponent;
import io.github.whoisamyy.katarine.Game;
import io.github.whoisamyy.logging.LogLevel;
import io.github.whoisamyy.logging.Logger;
import io.github.whoisamyy.utils.EditorObject;
import io.github.whoisamyy.utils.NotInstantiatable;
import io.github.whoisamyy.utils.input.AbstractInputHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;

@EditorObject
public class GameObject extends AbstractInputHandler {
    // game objects queued to be added in editor/game objects list
    public static ArrayDeque<GameObject> creationQueue = new ArrayDeque<>(32);
    public static ArrayDeque<GameObject> destroyQueue = new ArrayDeque<>(32);
    private static Editor game;
    private static Editor editor;
    private static long lastId = 1;
    private String name = toString();
    private long id;
    protected HashSet<Component> components = new HashSet<>();
    private HashSet<Class<? extends Component>> componentsClasses = new HashSet<>();
    protected HashSet<GameObject> children = new HashSet<>();
    protected GameObject parent;
    private boolean initialized = false;
    public Transform2D transform;

    public Vector2 relativePosition = new Vector2();

    private static Logger logger = new Logger(GameObject.class.getTypeName());

    protected GameObject(){}

    /**
     * Called on instantiation of an object. Can be used for example to initialize components.
     */
    protected void awake() {}
    protected void start() {}
    protected void update() {}
    protected void die() {} //what to do on dispose

    public final void destroy() {
        for (Component c : components) {
            c.die();
        }
        if (Game.getInstance() == null || Game.getInstance().isEditorMode()) {
            die();
            try {
                Editor.getInstance().getWorld().destroyBody(getComponent(RigidBody2D.class).body);
            } catch (NullPointerException ignored) {}
            // Editor.getInstance().getEditorObjects().remove(this);
        }
        if (Editor.getInstance() == null || !Editor.getInstance().isEditorMode()) {
            die();
            try {
                Game.getInstance().getWorld().destroyBody(getComponent(RigidBody2D.class).body);
            } catch (NullPointerException ignored) {}
            // Game.getInstance().getGameObjects().remove(this);
        }
        destroyQueue.addLast(this);
    }

    /**
     * Surely it is possible to use constructors, but this method is more safe because of check for {@link NotInstantiatable} annotation.
     * @param gameObjectClass
     * @return instance of {@code <T extends GameObject>}
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T extends GameObject> T instantiate(Class<T> gameObjectClass) {
        Class<?> caller;
        if ((caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass())==Editor.class) { // если будет не эффективно то я удалю
            if (!gameObjectClass.isAnnotationPresent(EditorObject.class))
                throw new RuntimeException("Cannot instantiate " + gameObjectClass + " in the editor because the class is not marked as editor object");
        }
        else {
            if (gameObjectClass.isAnnotationPresent(NotInstantiatable.class))
                throw new RuntimeException("Cannot instantiate " + gameObjectClass + " because the class is marked as not instantiatable");
        }

        try {
            T ret = gameObjectClass.getDeclaredConstructor().newInstance();
            ret.setId(lastId);
            lastId++;
            ret.init();

            if (caller.equals(Editor.class)) {
                ret.addComponent(new EditorObjectComponent());
            } else {
                if (Editor.instance != null) {
                    ret.addComponent(new EditorObjectComponent());
                }
            }
            creationQueue.addLast(ret);

            return ret;
        } catch (NoSuchMethodException e) {
            return instantiate((Class<T>) gameObjectClass.getSuperclass());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Surely it is possible to use constructors, but this method is more safe because of check for {@link NotInstantiatable} annotation.
     * @param gameObjectClass
     * @param constructorParams parameters of available constructor of {@code Class<T> gameObjectClass}. Order sensitive
     * @return instance of {@code <T extends GameObject>}
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T extends GameObject> T instantiate(Class<T> gameObjectClass, Object... constructorParams) {
        Class<?> caller;
        if ((caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass())==Editor.class) { // если будет не эффективно то я удалю
            if (!gameObjectClass.isAnnotationPresent(EditorObject.class))
                throw new RuntimeException("Cannot instantiate " + gameObjectClass + " in the editor because the class is not marked as editor object");
        }
        else {
            if (gameObjectClass.isAnnotationPresent(NotInstantiatable.class))
                throw new RuntimeException("Cannot instantiate " + gameObjectClass + " because the class is marked as not instantiatable");
        }
        Class<?>[] paramsTypes = new Class<?>[constructorParams.length];
        for (int i = 0; i < constructorParams.length; i++) {
            Class<?> sc = constructorParams[i].getClass().getSuperclass();
            if (Modifier.isAbstract(sc.getModifiers())) {
                paramsTypes[i] = sc;
                continue;
            }
            paramsTypes[i] = constructorParams[i].getClass();
        }

        try {
            T ret = gameObjectClass.getDeclaredConstructor(paramsTypes).newInstance(constructorParams);
            ret.setId(lastId);
            lastId++;
            ret.init();

            if (caller.equals(Editor.class)) {
                ret.addComponent(new EditorObjectComponent());
            } else {
                if (Editor.instance != null) {
                    ret.addComponent(new EditorObjectComponent());
                }
            }
            creationQueue.addLast(ret);

            return ret;
        } catch (NoSuchMethodException e) {
            return instantiate((Class<T>) gameObjectClass.getSuperclass(), constructorParams);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Surely it is possible to use constructors, but this method is more safe because of check for {@link NotInstantiatable} annotation.
     * @param gameObjectClass
     * @param parent parent {@code GameObject}
     * @param constructorParams parameters of available constructor of {@code Class<T> gameObjectClass}. Order sensitive
     * @return instance of {@code <T extends GameObject>}
     * @param <T>
     */
    public static <T extends GameObject> T instantiate(Class<T> gameObjectClass, GameObject parent, Object... constructorParams) {
        if (gameObjectClass.isAnnotationPresent(NotInstantiatable.class)) throw new RuntimeException("Cannot instantiate "+gameObjectClass+" because the class is marked as not instantiatable");
        T go = instantiate(gameObjectClass, constructorParams);
        parent.addChild(go);
        return go;
    }

    /**
     * Surely it is possible to use constructors, but this method is more safe because of check for {@link NotInstantiatable} annotation.
     * @param gameObjectClass
     * @param parent parent {@code GameObject}
     * @return instance of {@code <T extends GameObject>}
     * @param <T>
     */
    public static <T extends GameObject> T instantiate(Class<T> gameObjectClass, GameObject parent) {
        if (gameObjectClass.isAnnotationPresent(NotInstantiatable.class)) throw new RuntimeException("Cannot instantiate "+gameObjectClass+" because the class is marked as not instantiatable");
        T go;
        logger.debug("parent: "+parent);
        parent.addChild(go = instantiate(gameObjectClass));
        return go;
    }

    public static <T extends GameObject> T instantiate(T gameObject) {
        Class<?> caller;
        if ((caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass())==Editor.class) { // если будет не эффективно то я удалю
            if (!gameObject.getClass().isAnnotationPresent(EditorObject.class))
                throw new RuntimeException("Cannot instantiate " + gameObject.getClass() + " in the editor because the class is not marked as editor object");
        }
        else {
            if (gameObject.getClass().isAnnotationPresent(NotInstantiatable.class))
                throw new RuntimeException("Cannot instantiate " + gameObject.getClass() + " because the class is marked as not instantiatable");
        }

        gameObject.setId(lastId);
        lastId++;
        gameObject.init();

        if (caller.equals(Editor.class)) {
            gameObject.addComponent(new EditorObjectComponent());
        } else {
            if (Editor.instance != null) {
                gameObject.addComponent(new EditorObjectComponent());
            }
        }
        creationQueue.addLast(gameObject);

        return gameObject;
    }

    public static <T extends GameObject> T instantiate(T gameObject, GameObject parent) {
        parent.addChild(instantiate(gameObject));
        return gameObject;
    }

    /**
     * Game Object initializer.
     * @apiNote made public for using constructors being available.
     */
    public void init() {
        if (initialized) {
            return;
        }
        this.transform = new Transform2D(new Vector2(0, 0));
        awake();
        for (Component c : components) {
            // c.transform = this.transform;
            if (!c.isInitialized())
                c.init();
        }
        initialized = true;
    }

    /**
     * calls {@link GameObject#start()} on this object and its components
     */
    public void create() {
        if (!initialized) throw new IllegalStateException("Cannot create uninitialized GameObject");
        start();
        for (Component c : components) {
            c.start();
        }
        logger.debug("Created gameObject "+this);
        logger.setLogLevel(LogLevel.DEBUG);
        if (this.parent!=null) {
            relativePosition = parent.transform.pos.cpy().sub(this.transform.pos);
            logger.debug("relativePosition: "+relativePosition);
        } else {
            this.relativePosition.set(transform.pos);
        }
    }

    /**
     * calls {@link GameObject#update()} on this object and its components
     */
    public void render() {
        if (this.parent != null) {
            this.transform.pos.set(parent.transform.pos.cpy().add(relativePosition));
        } else {
            this.transform.pos.set(relativePosition);
        }
        update();
        for (Component c : components) {
            c.update();
        }
    }

    /**
     * calls {@link GameObject#die()} on this object and its components, and then actually "dies". That means this object will not be updated or rendered by Game class, and will not be accessible anymore.
     */
    public void dispose() {
        die();
        destroy();
    }

    public final void addChild(GameObject child) {
        children.add(child);
        child.parent = this;
    }

    @SuppressWarnings("unchecked")
    public final <T extends Component> T addComponent(T component) {
        if (components.contains(component) || componentsClasses.contains(component.getClass())) return (T) getComponent(component.getClass());
        components.add(component);
        component.setGameObject(this);
        component.gameObject=this;
        componentsClasses.add(component.getClass());
        if (!component.isInitialized())
            component.init();
        logger.debug("added component "+component.getClass().getName() + " to "+ component.gameObject.getClass().getName());
        return component;
    }

    public final boolean removeComponent(Component component) {
        return removeComponent(component.getClass());
    }

    /**
     * Removes component from this object
     * @param componentClass
     * @return true if component was removed from this object and false otherwise
     */
    public final boolean removeComponent(Class<? extends Component> componentClass) {
        for (Iterator<Component> itr = components.iterator(); itr.hasNext();) {
            if (itr.next().getClass()==componentClass) {
                itr.remove();
                componentsClasses.remove(componentClass);
                return true;
            }
        }
        return false;
    }

    public final <T extends Component> T getComponentExtender(Class<T> componentClass) throws NullPointerException {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                return (T) c;
            }
        }
        throw new NullPointerException(this.getClass().getName() + " does not have "+componentClass.getName()+ " component");
    }

    public final <T extends Component> T getComponent(Class<T> componentClass) throws NullPointerException {
        for (Component c : components) {
            if (c.getClass() == componentClass) {
                return (T) c;
            }
        }
        throw new NullPointerException(this.getClass().getName() + " does not have "+componentClass.getName()+ " component");
    }

    public final HashSet<Component> getComponents() {
        return components;
    }

    void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public static long getLastId() {
        return lastId;
    }

    public String getName() {
        return name;
    }

    public HashSet<GameObject> getChildren() {
        return children;
    }

    public GameObject getParent() {
        return parent;
    }

    public Vector2 getRelativePosition() {
        return relativePosition;
    }

    private void setName(String name) {
        this.name = name;
    }
}