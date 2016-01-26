package com.slimsim.mumet_test_merge;

/**
 * Created by SlimSim on 22/12/15.
 */
public class Metronome {

    private double bpm;
    private int beat;
//    private int noteValue;
    private int silence;

    private double beatSound;
    private double sound;

    private final int tick = 1000; // samples of tick
    private boolean play = false; // should ONLY be true if the audioGenerator "has a player"
    private AudioGenerator audioGenerator = new AudioGenerator(8000);

    public Metronome() {
 /*

        this(150, 4, 4, 700.0, 350.0);
        silence = 4;

        beatSound = 700;
        sound = 350;

        bpm = 150;
        beat = 4;
*/
/*
        int bpm = 150;
        int beat = 4;

        int silence = 4;

        double beatSound = 700;
        double sound = 900;
        */


        this(/*bpm*/150, /*beat*/4, /*silence*/4, /*beatSound*/600, /*sound*/700);


    }

    public Metronome(int bpm, int beat, int silence,
                     double beatSound, double sound){

        this.bpm = bpm;
        this.beat = beat;
        this.silence = silence;
        this.beatSound = beatSound;
        this.sound = sound;

    }
/*
    public Metronome(int bpm, int beat, int noteValue, int silence,
                     double beatSound, double sound){

        this.bpm = bpm;
        this.beat = beat;
        this.noteValue = noteValue;
        this.silence = silence;
        this.beatSound = beatSound;
        this.sound = sound;

    }
    */

    public void calcSilence() {
        silence = (int) (((60/bpm)*8000)-tick);
    }

    public void play(final int bpm, final int beat) {
        new Thread(new Runnable() {
            public void run() {
                setBpm(bpm);
                setBeat(beat);
                playLocal();
            }
        }).start();
    }

    private void playLocal() {
        calcSilence();

        if( this.play ) {
            return;
        }
        this.play = true;
        audioGenerator.createPlayer();
        double[] tick = audioGenerator.getSineWave(this.tick, 8000, beatSound);
        double[] tock = audioGenerator.getSineWave(this.tick, 8000, sound);
        double silence = 0;
        double[] sound = new double[8000];
        int t = 0,s = 0,b = 0;
        do {
            for(int i=0; i<sound.length && play; i++) {
                if(t<this.tick) {
                    if(b == 0)
                        sound[i] = tock[t];
                    else
                        sound[i] = tick[t];
                    t++;
                } else {
                    sound[i] = silence;
                    s++;
                    if(s >= this.silence) {
                        t = 0;
                        s = 0;
                        b++;
                        if(b > (this.beat-1))
                            b = 0;
                    }
                }
            }
            audioGenerator.writeSound(sound);
        } while(play);
    }

    public void stop() {
        if( !this.play ) {
            return;
        }
        play = false;
        audioGenerator.destroyAudioTrack();
    }

    /* Getters and Setters ... */

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public void setBeat(int beat) {
        this.beat = beat;
    }

}