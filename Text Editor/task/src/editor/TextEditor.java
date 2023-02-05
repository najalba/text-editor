package editor;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TextEditor extends JFrame {
    private String currentSelectedFilePath;
    private Boolean useRegEx = false;
    private SearchEngine searchEngine;

    public TextEditor() {
        SwingUtilities.invokeLater(this::initUi);
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Text Editor");
        setSize(600, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea();
        textArea.setName("TextArea");
        JTextField searchFiled = new JTextField(15);
        searchFiled.setName("SearchField");
        searchEngine = new SearchEngine(textArea, searchFiled);
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setName("ScrollPane");
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setName("FileChooser");
        fileChooser.addActionListener(actionEvent -> this.selectFileChooser(actionEvent, textArea));
        JButton openButton = new JButton(new ImageIcon("open.png"));
        openButton.setName("OpenButton");
        openButton.addActionListener(actionEvent -> fileChooser.showOpenDialog(this));
        JButton saveButton = new JButton(new ImageIcon("save.png"));
        saveButton.setName("SaveButton");
        saveButton.addActionListener(actionEvent -> this.saveFileContent(textArea));
        JButton startSearchButton = new JButton(new ImageIcon("search.png"));
        startSearchButton.setName("StartSearchButton");
        startSearchButton.addActionListener(actionEvent -> searchEngine.search(this.useRegEx));
        JButton previousMatchButton = new JButton(new ImageIcon("prev.png"));
        previousMatchButton.setName("PreviousMatchButton");
        previousMatchButton.addActionListener(actionEvent -> searchEngine.previous());
        JButton nextMatchButton = new JButton(new ImageIcon("next.png"));
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.addActionListener(actionEvent -> searchEngine.next());
        JCheckBox useRegularExpressionsCheckBox = new JCheckBox("Use regex");
        useRegularExpressionsCheckBox.setName("UseRegExCheckbox");
        useRegularExpressionsCheckBox.addActionListener(actionEvent -> this.useRegEx = ((JCheckBox) actionEvent.getSource()).isSelected());

        JPanel northPanel = new JPanel();
        northPanel.add(openButton);
        northPanel.add(saveButton);
        northPanel.add(searchFiled);
        northPanel.add(startSearchButton);
        northPanel.add(previousMatchButton);
        northPanel.add(nextMatchButton);
        northPanel.add(useRegularExpressionsCheckBox);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setName("MenuOpen");
        openMenuItem.addActionListener(actionEvent -> fileChooser.showOpenDialog(this));
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(actionEvent -> this.saveFileContent(textArea));
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(actionEvent -> this.dispose());
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        searchMenu.setMnemonic(KeyEvent.VK_S);
        JMenuItem startSearchMenuItem = new JMenuItem("Starts search");
        startSearchMenuItem.setName("MenuStartSearch");
        startSearchMenuItem.addActionListener(actionEvent -> searchEngine.search(this.useRegEx));
        JMenuItem previousMatchMenuItem = new JMenuItem("Previous match");
        previousMatchMenuItem.setName("MenuPreviousMatch");
        previousMatchMenuItem.addActionListener(actionEvent -> searchEngine.previous());
        JMenuItem nextMatchMenuItem = new JMenuItem("Next match");
        nextMatchMenuItem.setName("MenuNextMatch");
        nextMatchMenuItem.addActionListener(actionEvent -> searchEngine.next());
        JMenuItem useRegularExpressionMenuItem = new JMenuItem("Use regular expressions");
        useRegularExpressionMenuItem.setName("MenuUseRegExp");
        useRegularExpressionMenuItem.addActionListener(actionEvent -> {
            this.useRegEx = !this.useRegEx;
            useRegularExpressionsCheckBox.setSelected(this.useRegEx);
        });
        searchMenu.add(startSearchMenuItem);
        searchMenu.add(previousMatchMenuItem);
        searchMenu.add(nextMatchMenuItem);
        searchMenu.add(useRegularExpressionMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        setJMenuBar(menuBar);

        add(northPanel, BorderLayout.NORTH);
        add(fileChooser, BorderLayout.CENTER);
        add(scrollableTextArea, BorderLayout.CENTER);

        setVisible(true);
    }

    private void selectFileChooser(ActionEvent actionEvent, JTextArea textArea) {
        try {
            var selectedFile = ((JFileChooser) actionEvent.getSource()).getSelectedFile();
            if (Objects.nonNull(selectedFile)) {
                this.currentSelectedFilePath = selectedFile.getPath();
                var fileContent = new String(Files.readAllBytes(Path.of(this.currentSelectedFilePath)));
                textArea.setText(fileContent);
                this.searchEngine.reset();
            }
        } catch (IOException e) {
            textArea.setText("");
            e.printStackTrace();
        }
    }

    private void saveFileContent(JTextArea textArea) {
        try {
            if (Objects.nonNull(this.currentSelectedFilePath)) {
                Files.write(Path.of(this.currentSelectedFilePath), textArea.getText().getBytes());
                this.searchEngine.search(this.useRegEx);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
