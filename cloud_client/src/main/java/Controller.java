import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {


    public TableView<FileInfo> tv;
    public TableView<FileInfo> tv2;
    public Button send;
    public Button upload;
    public Button delete;
    public ComboBox<String> disk;
    public TextField textField;
    private Connect connect;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disk.getItems().clear();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            disk.getItems().add(path.toString());
        }
        disk.getSelectionModel().select(0);

        TableColumn<FileInfo, String> columnName = getNameColumn();
        TableColumn<FileInfo, Long> columnSize = getLongColumn();
        tv.getColumns().addAll(columnName, columnSize);
        TableColumn<FileInfo, String> columnName2 = getNameColumn();
        TableColumn<FileInfo, Long> columnSize2 = getLongColumn();
        tv2.getColumns().addAll(columnName2, columnSize2);
        tv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(textField.getText()).resolve(tv.getSelectionModel().getSelectedItem().getFileName());
                    if (Files.isDirectory(path)) {
                        updateClient(path);
                    }
                }
            }
        });
        connect = Connect.getInstance();
        String clientFilesPath = "./common/src/main/resources/clientFiles";
        updateClient(Paths.get(clientFilesPath));
        updateServer();
    }

    public String getSelectedFileName(TableView<FileInfo> info) {
        if (!info.isFocused()) {
            return null;
        }
        FileInfo fileInfo = info.getSelectionModel().getSelectedItem();
        if (fileInfo == null){
            return null;
        }
        return info.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath() {
        return textField.getText();
    }

    private void updateServer() {
        FileList fl = new FileList();
        connect.sendMessage(fl);
        Object o = connect.readMessage();
        if (o instanceof FileList) {
            fl = (FileList) o;
        }
        tv2.getItems().clear();
        tv2.getItems().addAll(fl.getList());
    }

    public void updateClient(Path path) {
        textField.setText(path.normalize().toAbsolutePath().toString());
        try {
            tv.getItems().clear();
            tv.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectDisk(ActionEvent actionEvent) {
        updateClient(Paths.get(disk.getSelectionModel().getSelectedItem()));
    }

    public void btnUp(ActionEvent actionEvent) {
        Path path = Paths.get(textField.getText()).getParent();
        if (path != null) {
            updateClient(path);
        }
    }

    public void sendDownload() {
        String fileName = getSelectedFileName(tv2);
        if (fileName != null) {
            FileRequest fileRequest = new FileRequest(fileName);
            connect.sendMessage(fileRequest);
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        Object o = connect.readMessage();
                        if (o instanceof FileMessage) {
                            FileMessage fileMessage = (FileMessage) o;
                            Path fp = Paths.get(getCurrentPath(), fileMessage.getFilename());
                            if (fileMessage.getPart() == 1) {
                                if (Files.exists(fp)) {
                                    Files.delete(fp);
                                }
                                Files.write(fp, fileMessage.getData(), StandardOpenOption.CREATE);
                            }
                            if (fileMessage.getPart() > 1) {
                                Files.write(fp, fileMessage.getData(), StandardOpenOption.APPEND);
                            }
                            if (fileMessage.getPart() == fileMessage.getCount()) {
                                break;
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateClient(Paths.get(getCurrentPath()));
        } else {
            TextHelper.printAlertText("Файл не выбран");
        }
    }

    public void sendUpload(ActionEvent actionEvent) {
        if (getSelectedFileName(tv) != null) {
            String fileName = getCurrentPath() + File.separator + getSelectedFileName(tv);
            File file = new File(fileName);
            int bufferSize = 1024 * 1024 * 10;
            byte[] b = new byte[bufferSize];
            int count = (int) (file.length() / bufferSize);
            if (file.length() % bufferSize != 0) {
                count++;
            }
            FileMessage fm = new FileMessage(file.getName(), b, count);
            try (FileInputStream fis = new FileInputStream(file)) {
                for (int i = 1; i <= count; i++) {
                    int read = fis.read(b);
                    fm.setPart(i);
                    if (read < bufferSize) {
                        fm.setData(Arrays.copyOfRange(b, 0, read));
                    } else {
                        fm.setData(b);
                    }
                    connect.sendMessage(fm);
                }
                updateServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            TextHelper.printAlertText("Файл не выбран");
        }
    }

    public void sendDelete(ActionEvent actionEvent) {
        String fileName = getSelectedFileName(tv2);
        if (fileName != null) {
            FileDelete fileDelete = new FileDelete(fileName);
            connect.sendMessage(fileDelete);
            updateServer();
        } else {
            fileName = getSelectedFileName(tv);
            if (fileName != null) {
                try {
                    Files.delete(Paths.get(getCurrentPath(), getSelectedFileName(tv)));
                    updateClient(Paths.get(getCurrentPath()));
                } catch (IOException e) {
                    TextHelper.printAlertText("Удалить текущий файл не удалось. ");
                }
            } else TextHelper.printAlertText("Ни один файл не выбран для удаления");
        }
    }

    private static TableCell<FileInfo, Long> call(TableColumn<FileInfo, Long> column) {
        return new TableCell<FileInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) {
                        text = "[DIR]";
                    }
                    setText(text);
                }
            }
        };
    }

    private TableColumn<FileInfo, Long> getLongColumn() {
        TableColumn<FileInfo, Long> columnLng = new TableColumn<>("размер файла");
        columnLng.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        columnLng.setPrefWidth(150);
        columnLng.setCellFactory(Controller::call);
        return columnLng;
    }

    private TableColumn<FileInfo, String> getNameColumn() {
        TableColumn<FileInfo, String> name = new TableColumn<>("имя файла");
        name.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        name.setPrefWidth(250);
        return name;
    }
    }
