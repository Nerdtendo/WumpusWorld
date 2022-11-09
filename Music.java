import javax.sound.sampled.*;
import javax.xml.crypto.Data;

public class Music extends Thread {
    static Thread[] myThreads = new Thread[2];
    private static boolean mixing = false;
    protected Clip currentTrack; //Allows for control of the currently playing music outside the "playSound()" method.

    /* This method takes in a string which is a path to a .WAV audio file. It then plays that file on the user's
    computer at the volume given by the "volume" parameter (between 0 and 1). If the "repeat" parameter is true,
    the music track will loop indefinitely. Otherwise, it will play just once.
     */

    public synchronized void playSound(final String url, boolean repeat, float volume) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            Main.class.getResourceAsStream("/" + url));
                    clip.open(inputStream);
                    currentTrack = clip;
                    Music.this.setVolume(volume);
                    if (repeat == true)
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                    else
                        clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        });
        t1.start();
    }


    //Overloaded playSound() method that can specify a certain number of times to play a sound file. Currently unused.
    public synchronized void playSound(final String url, boolean repeat, int numTimes, float volume) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            Main.class.getResourceAsStream("/" + url));
                    clip.open(inputStream);
                    Music.this.setVolume(volume);
                    if (repeat == true)
                        clip.loop(numTimes);
                    else
                        clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    //Returns the current volume of sound file.
    public float getVolume() {
        FloatControl gainControl = (FloatControl) currentTrack.getControl(FloatControl.Type.MASTER_GAIN);
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }

    //Sets the volume of the current sound file to the value of "volume".
    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f)
            return;
        FloatControl gainControl = (FloatControl) currentTrack.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    //Sets the volume to the value given in "value" by rapidly decreasing the volume in small steps.
    synchronized public void shiftVolumeTo(double value) {
        double newValue = (value <= 0.0) ? 0.0001 : ((value > 1.0) ? 1.0 : value); //converts value of not between 0-1
        float targetDB = (float) newValue;
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //Decrease volume if current volume > target volume
                if (getVolume() > newValue) {
                    while (getVolume() > newValue) {
                        try {
                            setVolume(getVolume() - getVolume()/5);
                        } catch (Exception e) {
                            currentTrack.stop();
                            break;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (Exception e) {
                        }
                    }
                    currentTrack.stop();

                    //Increase volume if current volume > target volume
                }
                else if (getVolume() < newValue) {
                    while (getVolume() < newValue) {
                        setVolume(getVolume() + getVolume() / 10);
                        try {
                            Thread.sleep(25);
                        } catch (Exception e) {
                            setVolume((float) newValue);
                            break;
                        }
                    }
                    setVolume((float)newValue);
                }
            }

        });
        if (myThreads[0] == null) {
            myThreads[0] = t1;
        }
        else {
            myThreads[1] = t1;
        }
        t1.start();
    }

    synchronized public static void mixTracks(Music first, Music second) {
        if (!mixing) {
            mixing = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    first.shiftVolumeTo(0);      //Thread A
                    second.shiftVolumeTo(.5);    //Thread B

                    for (int i = 0; i < myThreads.length; i++) {
                        try {
                            myThreads[i].join();
                        } catch (Exception e) {
                        }  //Above waits for thread A and thread B to finish. They finish when volume is set.
                    }
                    first.currentTrack = second.currentTrack;
                    myThreads[0] = myThreads[1] = null;
                    mixing = false;
                }
            }).start();
        }
        else {
            mixing = false;
            myThreads[0] = myThreads[1] = null;
            first.currentTrack.stop();
            second.currentTrack.stop();
            mixTracks(first, second);
        }
    }
}