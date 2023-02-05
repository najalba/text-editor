package editor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SearchEngine {
    private final java.util.List<SearchResult> foundResults = new ArrayList<>();
    private int currentPosition = -1;
    private final JTextArea textContentField;
    private final JTextField searchCriteriaField;

    SearchEngine(JTextArea textContentField, JTextField searchCriteriaField) {
        this.textContentField = textContentField;
        this.searchCriteriaField = searchCriteriaField;
    }

    void search(boolean useRegEx) {
        this.reset();
        var searchWorker = new Thread(() -> {
            if (!this.textContentField.getText().isEmpty() && !this.searchCriteriaField.getText().isEmpty()) {
                if (useRegEx) {
                    var pattern = Pattern.compile("\\b\\w*" + this.searchCriteriaField.getText() + "\\w*\\b");
                    var matcher = pattern.matcher(this.textContentField.getText());
                    while (matcher.find()) {
                        this.foundResults.add(new SearchResult(matcher.start(), matcher.group()));
                    }
                } else {
                    var textContent = this.textContentField.getText();
                    var searchContent = this.searchCriteriaField.getText();
                    int index = 0;
                    while ((index = textContent.indexOf(searchContent, index)) > -1) {
                        this.foundResults.add(new SearchResult(index, searchContent));
                        index += searchContent.length();
                    }
                }
                this.next();
            }
        });
        searchWorker.start();
    }

    void next() {
        if (this.foundResults.size() > 0) {
            this.currentPosition = ++this.currentPosition % this.foundResults.size();
            var current = this.foundResults.get(this.currentPosition);
            textContentField.setCaretPosition(current.index() + current.foundText().length());
            textContentField.select(current.index(), current.index() + current.foundText().length());
            textContentField.grabFocus();
        }
    }

    void previous() {
        if (this.foundResults.size() > 0) {
            this.currentPosition = (--this.currentPosition + this.foundResults.size()) % this.foundResults.size();
            var current = this.foundResults.get(this.currentPosition);
            textContentField.setCaretPosition(current.index() + current.foundText().length());
            textContentField.select(current.index(), current.index() + current.foundText().length());
            textContentField.grabFocus();
        }
    }

    void reset() {
        this.foundResults.clear();
        this.currentPosition = -1;
        textContentField.setCaretPosition(0);
        textContentField.select(0, 0);
        textContentField.grabFocus();
    }
}
