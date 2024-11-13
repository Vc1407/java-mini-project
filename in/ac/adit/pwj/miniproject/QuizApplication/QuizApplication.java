// package in.ac.adit.pwj.miniproject.quiz;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract class Question {
    protected String questionText;
    protected String answer;

    public Question(String questionText, String answer) {
        this.questionText = questionText;
        this.answer = answer;
    }

    public abstract boolean checkAnswer(String userAnswer);
}

class MultipleChoiceQuestion extends Question {
    private List<String> options;

    public MultipleChoiceQuestion(String questionText, List<String> options, String answer) {
        super(questionText, answer);
        this.options = options;
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        return userAnswer.equalsIgnoreCase(answer);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(questionText + "\n");
        for (int i = 0; i < options.size(); i++) {
            sb.append((i + 1) + ". " + options.get(i) + "\n");
        }
        return sb.toString();
    }
}

class TrueFalseQuestion extends Question {
    public TrueFalseQuestion(String questionText, String answer) {
        super(questionText, answer);
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        return userAnswer.equalsIgnoreCase(answer);
    }

    @Override
    public String toString() {
        return questionText + " (True/False)\n";
    }
}

class QuizManager {
    private List<Question> questions = new ArrayList<>();
    private Map<String, Integer> userScores = new HashMap<>();

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void startQuiz(String userName) {
        QuizSession quizSession = new QuizSession(userName);
        quizSession.start();
    }

    private class QuizSession {
        private String userName;

        public QuizSession(String userName) {
            this.userName = userName;
        }

        public void start() {
            Scanner sc = new Scanner(System.in);
            int score = 0;

            for (Question question : questions) {
                System.out.println(question);
                System.out.print("Your answer: ");
                String userAnswer = sc.nextLine();
                if (question.checkAnswer(userAnswer)) {
                    score++;
                }
            }

            userScores.put(userName, score);
            System.out.println(userName + ", your score is: " + score);
            saveResults(userName, score);
        }
    }

    public void displayScores() {
        for (Map.Entry<String, Integer> entry : userScores.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void saveResults(String userName, int score) {
        try (FileWriter writer = new FileWriter("quiz_results.txt", true)) {
            writer.write(userName + ": " + score + "\n");
        } catch (IOException e) {
            System.out.println("Error saving results.");
        }
    }
}

public class QuizApplication {
    public static void main(String[] args) {
        QuizManager quizManager = new QuizManager();

        List<String> options1 = Arrays.asList("Java", "Python", "C++", "JavaScript");
        quizManager.addQuestion(new MultipleChoiceQuestion("Which programming language is platform-independent?", options1, "Java"));

        quizManager.addQuestion(new TrueFalseQuestion("The sky is blue.", "True"));

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> quizManager.startQuiz("User1"));
        executor.submit(() -> quizManager.startQuiz("User2"));
        executor.submit(() -> quizManager.startQuiz("User3"));

        executor.shutdown();
    }
}