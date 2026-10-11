package com.texthack.ui.components;

import com.texthack.core.ds.DynamicArray;
import com.texthack.core.model.StepEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Step Player control bar providing step-by-step playback,
 * speed adjustments, progress timeline, and narrative event status.
 */
public class StepPlayer extends VBox {

    @FunctionalInterface
    public interface StepListener {
        void onStep(int index, StepEvent step);
    }

    private DynamicArray<StepEvent> steps;
    private int currentStepIndex = 0;
    private StepListener listener;

    private final Button firstBtn;
    private final Button prevBtn;
    private final Button playPauseBtn;
    private final Button nextBtn;
    private final Button lastBtn;
    private final Slider progressSlider;
    private final Slider speedSlider;
    private final Label stepCountLabel;
    private final Label narrativeLabel;

    private Timeline timeline;
    private boolean isPlaying = false;

    public StepPlayer() {
        super(6);
        this.steps = new DynamicArray<>();

        getStyleClass().add("step-player-bar");

        // Narrative event label
        narrativeLabel = new Label("Ready. Run an algorithm to load execution trace.");
        narrativeLabel.getStyleClass().add("step-info-label");
        narrativeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        // Controls bar
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER);

        firstBtn = new Button("⏮");
        firstBtn.getStyleClass().add("icon-btn");
        firstBtn.setTooltip(new Tooltip("First Step"));

        prevBtn = new Button("◀");
        prevBtn.getStyleClass().add("icon-btn");
        prevBtn.setTooltip(new Tooltip("Step Back"));

        playPauseBtn = new Button("▶ Play");
        playPauseBtn.getStyleClass().add("primary-btn");
        playPauseBtn.setPrefWidth(90);

        nextBtn = new Button("▶");
        nextBtn.getStyleClass().add("icon-btn");
        nextBtn.setTooltip(new Tooltip("Step Forward"));

        lastBtn = new Button("⏭");
        lastBtn.getStyleClass().add("icon-btn");
        lastBtn.setTooltip(new Tooltip("Last Step"));

        progressSlider = new Slider(0, 0, 0);
        progressSlider.setPrefWidth(300);
        HBox.setHgrow(progressSlider, Priority.ALWAYS);

        stepCountLabel = new Label("Step 0 / 0");
        stepCountLabel.getStyleClass().add("step-info-label");

        Label speedLbl = new Label("Speed:");
        speedLbl.getStyleClass().add("step-info-label");
        speedSlider = new Slider(50, 800, 300);
        speedSlider.setPrefWidth(90);
        speedSlider.setTooltip(new Tooltip("Playback delay in ms"));

        controls.getChildren().addAll(
                firstBtn, prevBtn, playPauseBtn, nextBtn, lastBtn,
                progressSlider, stepCountLabel,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                speedLbl, speedSlider
        );

        getChildren().addAll(narrativeLabel, controls);

        initEventHandlers();
        updateControlsState();
    }

    public void setSteps(DynamicArray<StepEvent> steps) {
        pause();
        this.steps = (steps != null) ? steps : new DynamicArray<>();
        this.currentStepIndex = 0;

        if (this.steps.size() > 0) {
            progressSlider.setMin(0);
            progressSlider.setMax(this.steps.size() - 1);
            progressSlider.setValue(0);
            triggerStep(0);
        } else {
            progressSlider.setMin(0);
            progressSlider.setMax(0);
            progressSlider.setValue(0);
            stepCountLabel.setText("Step 0 / 0");
            narrativeLabel.setText("Trace empty.");
        }
        updateControlsState();
    }

    public void setStepListener(StepListener listener) {
        this.listener = listener;
    }

    public void play() {
        if (steps.size() <= 1) return;
        if (currentStepIndex >= steps.size() - 1) {
            currentStepIndex = 0;
        }
        isPlaying = true;
        playPauseBtn.setText("⏸ Pause");
        playPauseBtn.getStyleClass().remove("primary-btn");
        playPauseBtn.getStyleClass().add("accent-btn");

        double delayMs = speedSlider.getValue();
        timeline = new Timeline(new KeyFrame(Duration.millis(delayMs), e -> {
            if (currentStepIndex < steps.size() - 1) {
                stepForward();
            } else {
                pause();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void pause() {
        if (timeline != null) {
            timeline.stop();
        }
        isPlaying = false;
        playPauseBtn.setText("▶ Play");
        playPauseBtn.getStyleClass().remove("accent-btn");
        playPauseBtn.getStyleClass().add("primary-btn");
    }

    public void stepForward() {
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            triggerStep(currentStepIndex);
        }
    }

    public void stepBack() {
        if (currentStepIndex > 0) {
            currentStepIndex--;
            triggerStep(currentStepIndex);
        }
    }

    public void stepFirst() {
        if (steps.size() > 0) {
            currentStepIndex = 0;
            triggerStep(currentStepIndex);
        }
    }

    public void stepLast() {
        if (steps.size() > 0) {
            currentStepIndex = steps.size() - 1;
            triggerStep(currentStepIndex);
        }
    }

    private void triggerStep(int index) {
        if (index >= 0 && index < steps.size()) {
            StepEvent event = steps.get(index);
            progressSlider.setValue(index);
            stepCountLabel.setText("Step " + (index + 1) + " / " + steps.size());
            narrativeLabel.setText(event.getDescription());
            if (listener != null) {
                listener.onStep(index, event);
            }
        }
        updateControlsState();
    }

    private void updateControlsState() {
        boolean hasSteps = steps.size() > 0;
        firstBtn.setDisable(!hasSteps || currentStepIndex == 0);
        prevBtn.setDisable(!hasSteps || currentStepIndex == 0);
        nextBtn.setDisable(!hasSteps || currentStepIndex >= steps.size() - 1);
        lastBtn.setDisable(!hasSteps || currentStepIndex >= steps.size() - 1);
        playPauseBtn.setDisable(!hasSteps);
        progressSlider.setDisable(!hasSteps);
    }

    private void initEventHandlers() {
        playPauseBtn.setOnAction(e -> {
            if (isPlaying) pause();
            else play();
        });

        firstBtn.setOnAction(e -> { pause(); stepFirst(); });
        prevBtn.setOnAction(e -> { pause(); stepBack(); });
        nextBtn.setOnAction(e -> { pause(); stepForward(); });
        lastBtn.setOnAction(e -> { pause(); stepLast(); });

        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (progressSlider.isValueChanging() || Math.abs(newVal.intValue() - currentStepIndex) > 1) {
                int target = newVal.intValue();
                if (target != currentStepIndex && target >= 0 && target < steps.size()) {
                    currentStepIndex = target;
                    triggerStep(target);
                }
            }
        });

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isPlaying) {
                pause();
                play();
            }
        });
    }
}
