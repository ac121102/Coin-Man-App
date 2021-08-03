package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture dizzy;
	int frame_no = 0;
	int pause = 0;
	Random random;

	// Score
	int score = 0;
	BitmapFont scoreFont;
	int gameState = 0;

	// Screen Resolution
	int screenHeight;
	int screenWidth;
	int coinHeight;
	int coinWidth;
	int bombHeight;
	int bombWidth;

	// Motion variables
	int manX = 0;
	int manY = 0;
	float gravity = 0.2f;
	float velocity = 0;
	Rectangle manRectangle;

	// Coin position Arrays
	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;

	// Bomb position Arrays
	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {
		/********** Creation part ************/
		batch = new SpriteBatch();

		// Creating texture for background
		background = new Texture("bg.png");

		// Creating texture for man
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		// Dizzy man image texture
		dizzy = new Texture("dizzy-1.png");

		// Creating texture for coins and bombs
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		// Saving image resolutions
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();
		coinHeight = coin.getHeight();
		coinWidth = coin.getWidth();
		bombHeight = bomb.getHeight();
		bombWidth = bomb. getWidth();

		// Setting man's initial position coordinates
		manX = (screenWidth - man[0].getWidth()) / 2;
		manY = (screenHeight - man[0].getHeight()) / 2;

		// Setting score font
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(8);
	}

	// Function to make a new coin
	public void makeCoin(){
		float height = random.nextFloat() * screenHeight;
		coinYs.add((int)height);
		coinXs.add(screenWidth);
	}

	// Function to make a new bomb
	public void makeBomb(){
		float height = random.nextFloat() * screenHeight;
		bombYs.add((int)height);
		bombXs.add(screenWidth);
	}

	@Override
	public void render () {
		/********** Rendering part ************/
		batch.begin();
		batch.draw(background, 0, 0, screenWidth, screenHeight);

		/********** Game States ************/

		// Game is live.
		if (gameState == 1){
			// Showing coins
			coinCount = (coinCount + 1) % 100;
			if (coinCount == 0){
				makeCoin();
			}

			coinRectangles.clear();
			for (int i = 0; i < coinXs.size(); i++){
				int X = coinXs.get(i);
				int Y = coinYs.get(i);
				batch.draw(coin, X, Y);
				coinXs.set(i, X - 4);
				coinRectangles.add(new Rectangle(X, Y, coinWidth, coinHeight));
			}

			// Showing bombs
			bombCount = (bombCount + 1) % 300;
			if (bombCount == 0){
				makeBomb();
			}

			bombRectangles.clear();
			for (int i = 0; i < bombXs.size(); i++){
				int X = bombXs.get(i);
				int Y = bombYs.get(i);
				batch.draw(bomb, X, Y);
				bombXs.set(i, X - 4);
				bombRectangles.add(new Rectangle(X, Y, bombWidth, bombHeight));
			}

			// Jump on touch
			if (Gdx.input.justTouched()){
				velocity = -12;
			}

			// Updating frames
			pause = (pause + 1) % 8;
			if (pause == 0) {
				frame_no = (frame_no + 1) % 4;
			}

			// Updating man's falling velocity
			velocity += gravity;

			// Updating vertical position of man
			manY -= velocity;
			if (manY < 0){
				manY = 0;
			}
		}
		// Waiting to start.
		else if (gameState == 0){
			if (Gdx.input.justTouched()){
				gameState = 1;
			}
		}
		else if (gameState == 2){
			// Game is over.
			if (Gdx.input.justTouched()){
				gameState = 1;

				// Resetting game state
				manY = (screenHeight - man[0].getHeight()) / 2;
				score = 0;
				velocity = 0;

				coinCount = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();

				bombCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
			}
		}

		// Drawing frames of moving man one by one
		if (gameState == 2) {
			batch.draw(dizzy, manX, manY);
		}
		else {
			batch.draw(man[frame_no], manX, manY);
		}

		manRectangle = new Rectangle(manX, manY, man[frame_no].getWidth(), man[frame_no].getHeight());

		// Checking man and coin collision
		for (int i = 0; i < coinRectangles.size(); i++){
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))){
				score++;

				// Removing overlapped coin
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		// Checking man and bomb collision
		for (int i = 0; i < bombRectangles.size(); i++){
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))){
				gameState = 2;
			}
		}

		// Displaying score
		scoreFont.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}