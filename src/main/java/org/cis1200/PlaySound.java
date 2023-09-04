package org.cis1200;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Class that handles playing music and sound effects
 */
public class PlaySound {

    private Clip music;

    private Clip landEffect;

    private Clip clearEffect;

    private Clip tetrisEffect;

    public void playSound(
            String musicLocation, String landLocation, String clearLocation, String tetrisLocation
    ) {
        try {
            File musicPath = new File(musicLocation);
            File landPath = new File(landLocation);
            File clearPath = new File(clearLocation);
            File tetrisPath = new File(tetrisLocation);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                music = AudioSystem.getClip();
                music.open(audioInput);
                FloatControl gainControl = (FloatControl) music
                        .getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-25.0f);
                music.start();
                music.loop(Clip.LOOP_CONTINUOUSLY);
            }
            if (landPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(landPath);
                landEffect = AudioSystem.getClip();
                landEffect.open(audioInput);
                FloatControl gainControl = (FloatControl) landEffect
                        .getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
                landEffect.setMicrosecondPosition(landEffect.getMicrosecondLength());
            }
            if (clearPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(clearPath);
                clearEffect = AudioSystem.getClip();
                clearEffect.open(audioInput);
                FloatControl gainControl = (FloatControl) clearEffect
                        .getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
                clearEffect.setMicrosecondPosition(clearEffect.getMicrosecondLength());
            }
            if (tetrisPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(tetrisPath);
                tetrisEffect = AudioSystem.getClip();
                tetrisEffect.open(audioInput);
                FloatControl gainControl = (FloatControl) tetrisEffect
                        .getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
                tetrisEffect.setMicrosecondPosition(tetrisEffect.getMicrosecondLength());
            }
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopMusic() {
        if (music != null) {
            music.stop();
        }
    }

    public void startMusic() {
        if (music != null) {
            music.start();
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void playLandEffect() {
        if (landEffect != null) {
            landEffect.loop(1);
        }
    }

    public void playClearEffect() {
        if (clearEffect != null) {
            clearEffect.loop(1);
        }
    }

    public void playTetrisEffect() {
        if (tetrisEffect != null) {
            tetrisEffect.loop(1);
        }
    }
}
