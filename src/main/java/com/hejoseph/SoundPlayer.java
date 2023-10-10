package com.hejoseph;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundPlayer {


    public static void main(String[] args) throws InterruptedException {
        for(int i = 0;i < 60 ; i++){
            Thread.sleep(2000);
            play();
        }
    }


    public static void play() {

        // String audioFilePath = "AudioFileWithWavFormat.wav";
        // String audioFilePath = "AudioFileWithMpegFormat.mpeg";
        String audioFilePath = "water.mp3";
        SoundPlayer soundPlayerWithJavaFx = new SoundPlayer();

        try {
            com.sun.javafx.application.PlatformImpl.startup(() -> {
            });

            Media media = new Media(soundPlayerWithJavaFx.getClass()
                    .getClassLoader()
                    .getResource(audioFilePath)
                    .toExternalForm());

            MediaPlayer mp3Player = new MediaPlayer(media);
            mp3Player.setOnPlaying(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Playback started");
                }
            });

            mp3Player.play();

        } catch (Exception ex) {
            System.out.println("Error occured during playback process:" + ex.getMessage());
        }

    }
}
