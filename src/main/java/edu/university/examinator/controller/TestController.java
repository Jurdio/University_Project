package edu.university.examinator.controller;

import edu.university.examinator.content.fx.scene.Menu;
import edu.university.examinator.service.QuestionService;
import edu.university.examinator.service.ResultService;
import edu.university.examinator.service.TimerService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TestController implements Initializable, Runnable {
    TimerService timerService;
    QuestionService questionService;
    ResultService resultService;
    @FXML
    private ImageView testImage;
    @FXML
    private Text questionText;
    @FXML
    private RadioButton answer1;
    @FXML
    private RadioButton answer2;
    @FXML
    private RadioButton answer3;
    @FXML
    private RadioButton answer4;
    @FXML
    private Text correctCountText;
    @FXML
    private Text incorrectCountText;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private ProgressBar timerProgressBar;
    @FXML
    private Button startButton;
    @FXML
    private Button answerButton;
    @FXML
    private Text timerText;
    private List<RadioButton> radioButtonList;

    @FXML
    private void switchToMenu(ActionEvent event) throws Exception {
        stopTest();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Menu.getInstance().start(stage);
    }

    private void startTest() {
        if (questionService.hasQuestions()) {
            timerService.startTestTime();
            questionService.showQuestion();
        } else {
            System.out.println("Test completed");
            stopTest();
        }
    }

    private void stopTest() {
        timerService.stopTestTime();
        startButton.setDisable(false);
        disableQuestions();
    }

    private void handleTimerFinish() {
        System.out.println("Час на питання вийшов!");
        updateTestCounters();
    }

    private void updateTestCounters() {
        resultService.validateAnswer(questionService.getToggleGroup(), questionService.getQuestion());
        questionService.incrementIndex();
        timerService.resetTestTime();
        startTest();
    }

    private void disableQuestions() {
        for (RadioButton radioButton : radioButtonList) {
            radioButton.setDisable(true);
        }
        answerButton.setDisable(true);
    }

    private void enableQuestions() {
        for (RadioButton radioButton : radioButtonList) {
            radioButton.setDisable(false);
        }
        answerButton.setDisable(false);
    }

    private void initializeRadioButtonList() {
        toggleGroup = new ToggleGroup();
        radioButtonList = List.of(answer1, answer2, answer3, answer4);

        for (RadioButton radioButton : radioButtonList) {
            radioButton.setToggleGroup(toggleGroup);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializePrimaryState();
        initializeServices();
        initializeEventHandlers();
    }

    public void initializePrimaryState() {
        questionText.setWrappingWidth(600);
        startButton.setDisable(false);

        initializeRadioButtonList();
        disableQuestions();
    }

    public void initializeServices() {
        timerService = new TimerService(timerText, timerProgressBar);
        questionService = new QuestionService(questionText, radioButtonList, testImage, toggleGroup);
        resultService = new ResultService(correctCountText, incorrectCountText);
    }

    public void initializeEventHandlers() {
        startButton.setOnAction(actionEvent -> {
            startButton.setDisable(true);
            questionService.resetIndex();
            enableQuestions();
            startTest();
        });

        answerButton.setOnAction((actionEvent -> updateTestCounters()));

        timerService.setTimelineFinishedHandler(() -> run());
    }

    @Override
    public void run() {
        handleTimerFinish();
    }
}

