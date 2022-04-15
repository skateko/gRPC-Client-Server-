package pl.edu.pwr.rsi.grpc.gui;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pwr.rsi.grpc.dto.DownloadRequestDto;
import pl.edu.pwr.rsi.grpc.dto.FileDto;
import pl.edu.pwr.rsi.grpc.dto.SavedResponseDto;
import pl.edu.pwr.rsi.grpc.interfaces.lib.FileStatus;
import pl.edu.pwr.rsi.grpc.service.FoobarService;
import pl.edu.pwr.rsi.grpc.interfaces.lib.Author;
import pl.edu.pwr.rsi.grpc.interfaces.lib.Book;
import pl.edu.pwr.rsi.grpc.service.GrpcServiceImpl;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;

@Component
@Getter
@Setter
public class ClientGuiMy extends JFrame {
    private final FoobarService client;
    private final GrpcServiceImpl fileService;
    private final JFileChooser openFileChooser;

    private JPanel tab2;
    private JPanel tab3;
    private JPanel tab4;
    private JPanel tab5;
    private JTextField tab2AuthorIdText;
    private JTextPane textPane1;
    private JButton getBooksButton;
    private JPanel tab1;
    private JButton sendImageButton;
    private JTextField textField3;
    private JButton getImageButton;
    private JTextField tab1autorIdText;
    private JButton getAuthorButton;
    private JTextPane textPane2;
    private JButton getBooksButton1;
    private JTextPane sendBooksPane;
    private JPanel tab6;
    private JTextField tab6AuthotId;
    private JTextField tab6AuthotBookID;
    private JTextField tab6AuthorLastName;
    private JTextField tab6AuthorFirstName;
    private JTextField tab6AuthorGender;
    private JTextField authoridDescription;
    private JTextField bookidDescription;
    private JTextField authorFirstNameDescription;
    private JTextField authorLastNameDescription;
    private JTextField authorGenderDescription;
    private JTextField tab6responseDescripion;
    private JTextField sendFileInfo;
    private JButton sendAuthorButton;

    public ClientGuiMy(FoobarService clientIncoming, GrpcServiceImpl fileServiceIncoming) {
        this.client = clientIncoming;
        this.fileService = fileServiceIncoming;

        setTitle("Welcome");
        setSize(500,500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File(("D:\\SEMESTR 6\\ROZPROSZONE SYSTEMY INFORMATYCZNE\\LAB 3\\PROJEKT\\grpc-client\\src\\main\\resources")));
        openFileChooser.setFileFilter(new FileNameExtensionFilter("txt Files", "txt"));
        openFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("MS Word file(.docx)", "docx"));
        openFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("MS Word file(.doc)", "doc"));
        openFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Pdf file(.pdf)", "pdf"));
        openFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Jpg file(.jpg)", "jpg"));

        final JTabbedPane tabbedPane1 = new JTabbedPane();
        tab1 = new JPanel();
        tab1.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("1 to 1 get", tab1);
        tab1autorIdText = new JTextField();
        tab1autorIdText.setText("Type Author Id");
        tab1.add(tab1autorIdText, BorderLayout.NORTH);
        getAuthorButton = new JButton();
        getAuthorButton.setText("Get Author");
        tab1.add(getAuthorButton, BorderLayout.SOUTH);
        textPane2 = new JTextPane();
        tab1.add(textPane2, BorderLayout.CENTER);
        tab6 = new JPanel();
        tab6.setLayout(new GridLayout(0,2));
        tabbedPane1.addTab("1 to 1 save", tab6);

        authoridDescription = new JTextField();
        authoridDescription.setText("Author ID");
        authoridDescription.setEditable(false);
        tab6.add(authoridDescription);
        tab6AuthotId = new JTextField();
        tab6.add(tab6AuthotId);

        authorFirstNameDescription = new JTextField();
        authorFirstNameDescription.setText("First Name");
        authorFirstNameDescription.setEditable(false);
        tab6.add(authorFirstNameDescription);
        tab6AuthorFirstName = new JTextField();
        tab6.add(tab6AuthorFirstName);

        authorLastNameDescription = new JTextField();
        authorLastNameDescription.setText("Last Name");
        authorLastNameDescription.setEditable(false);
        tab6.add(authorLastNameDescription);
        tab6AuthorLastName = new JTextField();
        tab6.add(tab6AuthorLastName);

        authorGenderDescription = new JTextField();
        authorGenderDescription.setText("Gender");
        authorGenderDescription.setEditable(false);
        tab6.add(authorGenderDescription);
        tab6AuthorGender = new JTextField();
        tab6.add(tab6AuthorGender);

        bookidDescription = new JTextField();
        bookidDescription.setText("Book ID");
        bookidDescription.setEditable(false);
        tab6.add(bookidDescription);
        tab6AuthotBookID = new JTextField();
        tab6.add(tab6AuthotBookID);

        tab6responseDescripion = new JTextField();
        tab6responseDescripion.setEditable(false);
        tab6.add(tab6responseDescripion);

        sendAuthorButton = new JButton();
        sendAuthorButton.setText("Send Author");
        tab6.add(sendAuthorButton);

        tab2 = new JPanel();
        tab2.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("1 to many", tab2);
        tab2AuthorIdText = new JTextField();
        tab2AuthorIdText.setText("Type Author ID");
        tab2.add(tab2AuthorIdText, BorderLayout.NORTH);
        textPane1 = new JTextPane();
        tab2.add(textPane1, BorderLayout.CENTER);
        getBooksButton1 = new JButton();
        getBooksButton1.setText("Get Books");
        tab2.add(getBooksButton1, BorderLayout.SOUTH);
        tab3 = new JPanel();
        tab3.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("many to 1", tab3);
        getBooksButton = new JButton();
        getBooksButton.setText("Send Books");
        tab3.add(getBooksButton, BorderLayout.NORTH);
        sendBooksPane = new JTextPane();
        tab3.add(sendBooksPane, BorderLayout.CENTER);

        tab4 = new JPanel();
        tab4.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("File Send", tab4);
        sendImageButton = new JButton();
        sendImageButton.setText("Send File");
        tab4.add(sendImageButton, BorderLayout.CENTER);

        sendFileInfo = new JTextField();
        sendFileInfo.setEditable(false);
        tab4.add(sendFileInfo, BorderLayout.SOUTH);

        tab5 = new JPanel();
        tab5.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("File get", tab5);
        textField3 = new JTextField();
        tab5.add(textField3, BorderLayout.NORTH);
        getImageButton = new JButton();
        getImageButton.setText("Get File");
        tab5.add(getImageButton, BorderLayout.CENTER);

        getAuthorButton.addActionListener(new ActionListener() {    //tab1
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(tab1autorIdText.getText());
                textPane2.setText(client.getAuthor(id).toString());
            }
        });
        getBooksButton1.addActionListener(new ActionListener() {    //tab2
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(tab2AuthorIdText.getText());
                try {
                    textPane1.setText(client.getBooksByAuthor(id).toString());
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        getBooksButton.addActionListener(new ActionListener() {     //tab 3
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = openFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    try {
                        ArrayList<Book> books = new ArrayList<Book>();
                        BufferedReader reader = new BufferedReader(new FileReader(openFileChooser.getSelectedFile()));
                        sendBooksPane.setText("File succesfully loaded!");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("SENDING " + line);
                            String[] tokens = line.split(",");

                            int bookId = Integer.parseInt(tokens[0]);
                            int authorId = Integer.parseInt(tokens[1]);
                            String title = tokens[2];
                            float price = Float.parseFloat(tokens[3]);
                            int pages = Integer.parseInt(tokens[4]);

                            books.add(Book.newBuilder().setBookId(bookId).setAuthorId(authorId).setTitle(title).setPrice(price).setPages(pages).build());
                        }
                        reader.close();
                        String response = client.saveMultipleBooks(books);
                        System.out.println(response);
                        sendBooksPane.setText(response);
                    } catch(Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    sendBooksPane.setText("No file chosen!");
                }
            }
        });
        sendAuthorButton.addActionListener(new ActionListener() {    //tab6
            @Override
            public void actionPerformed(ActionEvent e) {
                int authorId = Integer.parseInt(tab6AuthotId.getText());
                String authorFirstName = tab6AuthorFirstName.getText();
                String authorLastName = tab6AuthorLastName.getText();
                String authorGender = tab6AuthorGender.getText();
                int authorBookId = Integer.parseInt(tab6AuthotBookID.getText());
                    Author request = Author.newBuilder()
                            .setAuthorId(authorId)
                            .setFirstName(authorFirstName)
                            .setLastName(authorLastName)
                            .setGender(authorGender)
                            .setBookId(authorBookId).build();
                    tab6responseDescripion.setText(client.saveAuthor(request));
            }
        });
        sendImageButton.addActionListener(new ActionListener() {    //tab4
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = openFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    try {
                        FileDto file = new FileDto();

                        var selectedfile = openFileChooser.getSelectedFile();
                        file.setByteArray(Files.readAllBytes(selectedfile.toPath()));
                        file.setName(selectedfile.getName());

                        var result = fileService.saveFile(file, FileStatus.forNumber(0)).getNumber();
                        String message = result == 2 ? "File was saved" : "There was a problem with saving file";
                        sendFileInfo.setText(message);
                    } catch(Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    sendBooksPane.setText("No file chosen!");
                }
            }
        });
        getImageButton.addActionListener(new ActionListener() {    //tab5
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadRequestDto downloadRequestDto = new DownloadRequestDto();
                downloadRequestDto.setFileName(textField3.getText());
                downloadRequestDto.setFileStatus(FileStatus.forNumber(0).getNumber());
                var result = fileService.downloadFile(downloadRequestDto).getNumber();
                String message = result == 2 ? "File was downloaded" : "There was a problem with downloading file";
                textField3.setText(message);
            }
        });

        setContentPane(tabbedPane1);
        System.out.println("Gui run successful");
        setVisible(true);
    }
}
