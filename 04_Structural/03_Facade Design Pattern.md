# Why Didn't We Use the Adapter Pattern Here?

After learning the Facade Pattern, a common question is:

> **"In both Adapter and Facade, we introduce one extra class between the client and the existing classes. Aren't they the same?"**

No.

Although both patterns introduce an intermediate class, **the problems they solve are completely different.**

Let's understand using the examples we've already learned.

---

## Let's Recall the Adapter Pattern

Remember our **Smart Home Controller**.

Our controller wanted to control every smart device using the same interface.

```java
device.turnOn();
```

But the Air Conditioner didn't provide

```java
turnOn();
```

Instead, it had

```java
connectViaBluetooth();

startCooling();
```

The Smart Light had

```java
connectToWiFi();

switchOn();
```

Every manufacturer exposed a different API.

The client expected

```java
turnOn();
```

The devices understood something else.

The problem was:

> **The client and the device speak different languages.**

So we introduced an Adapter.

```
Client

↓

turnOn()

↓

Adapter

↓

connectViaBluetooth()

↓

startCooling()
```

The Adapter simply translated one interface into another.

Nothing else.

---

## Now Look at the Facade Pattern

Now consider our Multimedia Application.

Our subsystem already works perfectly.

To play a movie we simply need to execute

```java
audioPlayer.initializeAudioDriver();

audioPlayer.loadAudioFile();

audioPlayer.decodeAudio();

videoPlayer.initializeVideoDriver();

videoPlayer.loadVideoFile();

videoPlayer.decodeVideo();

subtitleManager.loadSubtitles();

subtitleManager.synchronizeSubtitles();

audioPlayer.startAudioPlayback();

videoPlayer.startVideoPlayback();
```

Nothing is incompatible.

Every class understands every method perfectly.

So what's the problem?

The client has to remember **too many classes** and **too many method calls**.

The problem is simply:

> **Using the subsystem is complicated.**

So instead of exposing all these steps,

we hide them behind

```java
mediaFacade.playMovie();
```

Internally,

the facade performs all those operations.

```
Client

↓

playMovie()

↓

MediaFacade

↓

Coordinates

↓

AudioPlayer

VideoPlayer

SubtitleManager
```

Notice something.

Nothing is translated.

Nothing is converted.

The facade simply hides the complexity.

---

# The Biggest Difference

The easiest way to identify these two patterns is to ask one question.

### Question 1

Are two classes unable to communicate because their interfaces are different?

```
YES
```

Think

```
Adapter
```

---

### Question 2

Can the classes already communicate, but using them is too complicated?

```
YES
```

Think

```
Facade
```

---

# Real-Life Analogy

## Adapter = Translator

Imagine you visit Japan.

You speak English.

The shopkeeper speaks Japanese.

Both of you are correct.

The only problem is that neither understands the other's language.

So you hire a translator.

```
You

↓

Translator

↓

Shopkeeper
```

The translator changes

```
English

↓

Japanese
```

Communication becomes possible.

That is exactly what an **Adapter** does.

---

## Facade = Receptionist

Now imagine you're staying in a hotel.

Behind the reception desk are many departments.

- Housekeeping
- Security
- Restaurant
- Billing
- Laundry

To request room service,

you don't call every department individually.

You simply call the receptionist.

```
You

↓

Receptionist

↓

Housekeeping

Restaurant

Billing

Security
```

The receptionist already knows whom to contact.

Nothing is translated.

The process is simply made easier.

That is exactly what a **Facade** does.

---

# Memory Trick

If you hear

> "These two classes cannot communicate."

Think

```
Adapter
```

If you hear

> "Using this subsystem is becoming too complicated."

Think

```
Facade
```

---

# One-Line Difference

> **Adapter changes the way you communicate.**

> **Facade changes how easy it is to use the system.**

# Quick Revision

## Intent

Provide a **simple and unified interface** to a complex subsystem.

The client doesn't need to know

- which classes are involved,
- how they interact,
- or in what order they should be called.

The Facade handles everything.

---

## Memory Trick

> **One button hides many steps.**

Instead of remembering

```text
Step 1

↓

Step 2

↓

Step 3

↓

...

↓

Step 10
```

the client simply calls

```java
playMovie();
```

---

## Thumb Rule

If you hear requirements like

- Hide subsystem complexity
- Simplify a complex API
- Reduce client dependencies
- Provide one entry point
- Client shouldn't know internal workflow

Think about the **Facade Pattern**.

---

## Thinking Process

```text
Need Multimedia Application

        ↓

Subsystems Grow

        ↓

Client Must Coordinate Everything

        ↓

Too Many Classes

        ↓

Too Many Method Calls

        ↓

Duplicate Workflow

        ↓

Need One Simple Interface

        ↓

Facade Coordinates Everything

        ↓

Facade Pattern
```

---

# Final Takeaway

The client should focus on **what** it wants to do, not **how** it should be done.

Instead of writing

```java
audioPlayer.initializeAudioDriver();

audioPlayer.loadAudioFile();

audioPlayer.decodeAudio();

videoPlayer.initializeVideoDriver();

videoPlayer.loadVideoFile();

videoPlayer.decodeVideo();

subtitleManager.loadSubtitles();

subtitleManager.synchronizeSubtitles();

audioPlayer.startAudioPlayback();

videoPlayer.startVideoPlayback();
```

the client simply writes

```java
mediaFacade.playMovie();
```

The Facade hides all the complexity and coordinates the subsystem internally.

This keeps the client code

- Cleaner
- Easier to understand
- Easier to maintain
- Less coupled to subsystem implementations

This is the core idea behind the **Facade Design Pattern**.

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## MusicPlayer.java

```java
// Handles music playback.
public class MusicPlayer {

    public void initializeAudioDrivers() {
        System.out.println("Audio drivers initialized.");
    }

    public void decodeAudio() {
        System.out.println("Audio decoded.");
    }

    public void startPlayback() {
        System.out.println("Music playback started.");
    }

}
```

---

## VideoPlayer.java

```java
// Handles video playback.
public class VideoPlayer {

    public void setupRenderingEngine() {
        System.out.println("Rendering engine initialized.");
    }

    public void loadVideoFile() {
        System.out.println("Video file loaded.");
    }

    public void playVideo() {
        System.out.println("Video playback started.");
    }

}
```

---

## ImageViewer.java

```java
// Handles image viewing.
public class ImageViewer {

    public void loadImageFile() {
        System.out.println("Image file loaded.");
    }

    public void applyScaling() {
        System.out.println("Image scaled.");
    }

    public void displayImage() {
        System.out.println("Image displayed.");
    }

}
```

---

## Client Code

```java
public class MultimediaApp {

    public static void main(String[] args) {

        // Play Music

        MusicPlayer musicPlayer = new MusicPlayer();

        musicPlayer.initializeAudioDrivers();
        musicPlayer.decodeAudio();
        musicPlayer.startPlayback();

        System.out.println();

        // Play Video

        VideoPlayer videoPlayer = new VideoPlayer();

        videoPlayer.setupRenderingEngine();
        videoPlayer.loadVideoFile();
        videoPlayer.playVideo();

        System.out.println();

        // View Image

        ImageViewer imageViewer = new ImageViewer();

        imageViewer.loadImageFile();
        imageViewer.applyScaling();
        imageViewer.displayImage();

    }

}
```

---

## Output

```text
Audio drivers initialized.
Audio decoded.
Music playback started.

Rendering engine initialized.
Video file loaded.
Video playback started.

Image file loaded.
Image scaled.
Image displayed.
```

</details>

# Applying the Facade Pattern

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## MusicPlayer.java (Subsystem)

```java
// Subsystem
// Handles all music-related operations.
public class MusicPlayer {

    public void initializeAudioDrivers() {
        System.out.println("Audio drivers initialized.");
    }

    public void decodeAudio() {
        System.out.println("Audio decoded.");
    }

    public void startPlayback() {
        System.out.println("Music playback started.");
    }

}
```

---

## VideoPlayer.java (Subsystem)

```java
// Subsystem
// Handles all video-related operations.
public class VideoPlayer {

    public void setupRenderingEngine() {
        System.out.println("Rendering engine initialized.");
    }

    public void loadVideoFile() {
        System.out.println("Video file loaded.");
    }

    public void playVideo() {
        System.out.println("Video playback started.");
    }

}
```

---

## ImageViewer.java (Subsystem)

```java
// Subsystem
// Handles all image-related operations.
public class ImageViewer {

    public void loadImageFile() {
        System.out.println("Image file loaded.");
    }

    public void applyScaling() {
        System.out.println("Image scaled.");
    }

    public void displayImage() {
        System.out.println("Image displayed.");
    }

}
```

---

## MediaFacade.java (Facade)

```java
// Facade
// Provides a simple interface to the multimedia subsystem.
public class MediaFacade {

    private final MusicPlayer musicPlayer;
    private final VideoPlayer videoPlayer;
    private final ImageViewer imageViewer;

    public MediaFacade() {

        musicPlayer = new MusicPlayer();
        videoPlayer = new VideoPlayer();
        imageViewer = new ImageViewer();

    }

    public void playMusic() {

        musicPlayer.initializeAudioDrivers();
        musicPlayer.decodeAudio();
        musicPlayer.startPlayback();

    }

    public void playVideo() {

        videoPlayer.setupRenderingEngine();
        videoPlayer.loadVideoFile();
        videoPlayer.playVideo();

    }

    public void viewImage() {

        imageViewer.loadImageFile();
        imageViewer.applyScaling();
        imageViewer.displayImage();

    }

}
```

<details>
<summary>💡 Why do we need a Facade?</summary>

A common question is:

> **Why can't the client directly use `MusicPlayer`, `VideoPlayer`, and `ImageViewer`?**

Technically, it can.

```java
MusicPlayer musicPlayer = new MusicPlayer();

musicPlayer.initializeAudioDrivers();
musicPlayer.decodeAudio();
musicPlayer.startPlayback();
```

The same is true for videos and images.

The problem isn't that this code is wrong.

The problem is that **every client now needs to understand how every subsystem works.**

Imagine there are many screens in your application.

- Home Screen
- Downloads Screen
- Recently Played
- Favorites
- Playlist

Every screen now contains the same subsystem calls.

If tomorrow the implementation changes, every screen must also change.

Instead, we move this knowledge into one class.

```java
mediaFacade.playMusic();

mediaFacade.playVideo();

mediaFacade.viewImage();
```

Now the client only knows the operations it wants to perform.

It doesn't know

- which subsystem classes are involved,
- how many methods are called,
- or in what order they execute.

The Facade hides all that complexity.

</details>

---

## Client Code

```java
public class MultimediaApp {

    public static void main(String[] args) {

        MediaFacade mediaFacade = new MediaFacade();

        System.out.println("========== Play Music ==========");

        mediaFacade.playMusic();

        System.out.println();

        System.out.println("========== Play Video ==========");

        mediaFacade.playVideo();

        System.out.println();

        System.out.println("========== View Image ==========");

        mediaFacade.viewImage();

    }

}
```

---

## Output

```text
========== Play Music ==========

Audio drivers initialized.
Audio decoded.
Music playback started.

========== Play Video ==========

Rendering engine initialized.
Video file loaded.
Video playback started.

========== View Image ==========

Image file loaded.
Image scaled.
Image displayed.
```

---

## Execution Flow

### Playing Music

```text
Client

        │

playMusic()

        │

        ▼

MediaFacade

        │

        ▼

MusicPlayer

        │

Initialize Audio Drivers

        │

Decode Audio

        │

Start Playback

        ▼

Music Starts Playing
```

---

### Playing Video

```text
Client

        │

playVideo()

        │

        ▼

MediaFacade

        │

        ▼

VideoPlayer

        │

Setup Rendering Engine

        │

Load Video File

        │

Play Video

        ▼

Video Starts Playing
```

---

### Viewing an Image

```text
Client

        │

viewImage()

        │

        ▼

MediaFacade

        │

        ▼

ImageViewer

        │

Load Image

        │

Apply Scaling

        │

Display Image

        ▼

Image Displayed
```

Notice something important.

The client never communicates directly with

- `MusicPlayer`
- `VideoPlayer`
- `ImageViewer`

It only communicates with

```java
MediaFacade
```

The Facade coordinates all subsystem interactions internally and exposes a much simpler API to the client.

</details>
