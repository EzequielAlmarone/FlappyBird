package com.almarone.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.font.ShapeGraphicAttribute;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch; //classe utiliza para criar as animações.
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Random numeroRandomico;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle passaroCirculo;
	private Rectangle canoSuperior;
	private Rectangle canoInferior;
	//private ShapeRenderer shape;

	// Atributos de configuração

	private float larguraDispositivo;
	private float alturtaDispositivo;
	private int estadoJogo = 0;
	private int pontuacao = 0;

	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCano;
	private float deltaTime;
	private float alturaEntreCanosRandomica;
	private boolean marcouPonto = false;

	// Câmera

	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
		batch = new SpriteBatch();
		numeroRandomico = new Random();
		passaroCirculo = new Circle();
		/*
		canoSuperior = new Rectangle();
		canoInferior = new Rectangle();
		shape = new ShapeRenderer();

		 */

		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);


		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");

		// configiração da camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2 , 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		larguraDispositivo = VIRTUAL_WIDTH;
		alturtaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVertical = alturtaDispositivo / 2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCano = 300;
	}

	@Override
	public void render () {

		camera.update();

		//limpar frames anteriores

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;
		if (variacao > 2) {
			variacao = 0;
		}

		if(estadoJogo == 0){ // não iniciado
			if(Gdx.input.justTouched()){
				estadoJogo = 1;
			}

		}else  { // INICIADO

			velocidadeQueda++;
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
				posicaoInicialVertical -= velocidadeQueda;
			}

			if(estadoJogo == 1){
				posicaoMovimentoCanoHorizontal -= deltaTime * 200;
				if (Gdx.input.justTouched()) {
					velocidadeQueda = -15;
				}

				// verifica se o cano saiu fora da tela
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {

					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
					marcouPonto = false;
				}

				// verificar a pontuação

				if(posicaoMovimentoCanoHorizontal < 120 ){
					if(!marcouPonto){
						pontuacao ++;
						marcouPonto = true;
					}

				}

				posicaoMovimentoCanoHorizontal -= deltaTime * 200;

			} else{ // Tela Game Over
				if(Gdx.input.justTouched()){
					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = alturtaDispositivo / 2;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;

				}
			}



			//Gdx.app.log("Variação", "variação:" + Gdx.graphics.getDeltaTime());
		}

		// Configurar dados de projeção da câmera
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(fundo, 0, 0, larguraDispositivo, alturtaDispositivo);
		batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturtaDispositivo / 2 + espacoEntreCano/ 2 + alturaEntreCanosRandomica);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturtaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCano / 2 + alturaEntreCanosRandomica );
		batch.draw(passaros[(int)variacao], 120 , posicaoInicialVertical);
		fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturtaDispositivo - 50);

		if(estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturtaDispositivo / 2);
			mensagem.draw(batch, "Toque para reiniciar o jogo", larguraDispositivo / 2 - 200, alturtaDispositivo / 2 - gameOver.getHeight() / 2);
		}

		batch.end();

		passaroCirculo.set(120 + passaros[0].getWidth() / 2, posicaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
		canoInferior = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturtaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCano / 2 + alturaEntreCanosRandomica,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		canoSuperior = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturtaDispositivo / 2 + espacoEntreCano/ 2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		// desenhar as formas
		/*
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
		shape.rect(canoInferior.x, canoInferior.y, canoInferior.width, canoInferior.height);
		shape.rect(canoSuperior.x, canoSuperior.y, canoSuperior.width, canoSuperior.height);
		shape.setColor(Color.RED);
		shape.end();
		*/

		// teste de colisão

		if(Intersector.overlaps(passaroCirculo, canoInferior) || Intersector.overlaps(passaroCirculo, canoSuperior) || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturtaDispositivo){
			estadoJogo = 2;
			//Gdx.app.log("Colisão", "Colisões");
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
