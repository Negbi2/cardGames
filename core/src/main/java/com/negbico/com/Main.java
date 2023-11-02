package com.negbico.com;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Main extends ApplicationAdapter {

    private final int VIEWPORT_WIDTH = 800;
    private final int VIEWPORT_HEIGHT = 480;

    private final int BUCKET_DIMENTIONS = 64;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Texture dropImage;
    private Texture bucketImage;

    private Rectangle bucket;

    private Array<Rectangle> raindrops;
    private long lastDropTime;

    private Sound dropSound;
    private Music rainMusic;

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();

        raindrop.setX(MathUtils.random(0, VIEWPORT_WIDTH - BUCKET_DIMENTIONS));
        raindrop.setY(VIEWPORT_HEIGHT);
        raindrop.setWidth(64);
        raindrop.setHeight(64);
        raindrops.add(raindrop);

        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void create() {
        dropImage = new Texture(Gdx.files.internal("drop.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        bucket = new Rectangle();
        bucket.setX(VIEWPORT_WIDTH / 2f - BUCKET_DIMENTIONS / 2f);
        bucket.setY(20);
        bucket.setWidth(BUCKET_DIMENTIONS);
        bucket.setHeight(BUCKET_DIMENTIONS);

        raindrops = new Array<Rectangle>();
        spawnRaindrop();

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        batch = new SpriteBatch();

        rainMusic.setLooping(true);
        rainMusic.play();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();

        Vector3 clickPose = new Vector3();
        clickPose.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(clickPose);

        // Changes inner position to mouse
        bucket.setX(clickPose.x - BUCKET_DIMENTIONS / 2f);
        // Renders object based on inner position
        //
        batch.setProjectionMatrix(camera.combined);

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRaindrop();

        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext();) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();

            if (raindrop.y + 64 < 0)
                iter.remove();

            if(raindrop.overlaps(bucket)) {
                dropSound.play();
                iter.remove();
            }
        }

        batch.begin();
        batch.draw(bucketImage, bucket.getX(), bucket.getY());
        for (Rectangle raindrop : raindrops)
            batch.draw(dropImage, raindrop.getX(), raindrop.getY());
        batch.end();

        // if (Gdx.input.isKeyPressed(Input.Keys.A))
        // bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        // if (Gdx.input.isKeyPressed(Input.Keys.D))
        // bucket.x += 200 * Gdx.graphics.getDeltaTime();
        //
        // if (bucket.getX() < 0)
        // bucket.x = 0;
        // if (bucket.getX() > VIEWPORT_WIDTH - BUCKET_DIMENTIONS)
        // bucket.x = VIEWPORT_WIDTH - BUCKET_DIMENTIONS;
    }

    @Override
    public void dispose() {
        dropSound.dispose();
        dropImage.dispose();
        rainMusic.dispose();
        bucketImage.dispose();
        batch.dispose();
    }
}
