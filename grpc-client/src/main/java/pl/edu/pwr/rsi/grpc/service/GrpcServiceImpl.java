package pl.edu.pwr.rsi.grpc.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pwr.rsi.grpc.dto.DownloadRequestDto;
import pl.edu.pwr.rsi.grpc.dto.FileDto;
import pl.edu.pwr.rsi.grpc.interfaces.lib.*;
import pl.edu.pwr.rsi.grpc.interfaces.lib.File;

import javax.swing.*;
import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GrpcServiceImpl {

    private static final String CLIENT_BASE_PATH = "grpc-server/src/main/resources/";
    private final Logger LOGGER = LoggerFactory.getLogger(GrpcServiceImpl.class);

//    @GrpcClient("FileService")
    private final FileServiceGrpc.FileServiceStub stub;

    private Status downloadStatus;
    private Status saveStatus;

    public Status downloadFile(DownloadRequestDto downloadRequestDto) {
        LOGGER.info("Start downloading file with filename: " + downloadRequestDto.getFileName() + " and status:" + downloadRequestDto.getFileStatus());
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        downloadStatus = Status.IN_PROGRESS;
        var downloadRequest = DownloadRequest.newBuilder()
                .setFileName(downloadRequestDto.getFileName())
                .setFileStatus(FileStatus.forNumber(downloadRequestDto.getFileStatus()))
                .build();

        this.stub.downloadFile(downloadRequest, new StreamObserver<>() {

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String fileName = "";

            @Override
            public void onNext(DownloadResponse downloadResponse) {
                try {
                    //System.out.println("x");
                    //textField.setText(downloadResponse.getMessage());
                    LOGGER.info("File is downloaded, message: " + downloadResponse.getMessage());
                    baos.write(downloadResponse.getFile().getContent().toByteArray());
                    fileName = downloadResponse.getFile().getName();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.error("There was a problem with downloading file from a server: " + throwable.getMessage());
                downloadStatus = Status.FAILED;
                onCompleted();
            }

            @Override
            public void onCompleted() {
                writeFile(File.newBuilder()
                        .setName(fileName)
                        .setContent(ByteString.copyFrom(baos.toByteArray())).build());
                downloadStatus = Status.IN_PROGRESS.equals(downloadStatus) ? Status.SUCCESS : downloadStatus;
                countDownLatch.countDown();
            }
        });

        try {
            boolean await = countDownLatch.await(1, TimeUnit.MINUTES);

            if (downloadStatus == Status.SUCCESS)
                LOGGER.info("Download status: " + downloadStatus);
            else
                LOGGER.error("Download status:" + downloadStatus);

            return downloadStatus;
        } catch (Exception e) {
            LOGGER.error("There was a problem with threat: " + e.getMessage());
            LOGGER.error("Download status:" + downloadStatus);
            return Status.FAILED;
        }
    }

    public Status saveFile(FileDto file, FileStatus fileStatus) {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            saveStatus = Status.IN_PROGRESS;
            var streamObserver = this.stub.saveFile(new StreamObserver<>() {
                @Override
                public void onNext(SaveResponse saveResponse) {
                    LOGGER.info("Received SaveResposne with status: " + saveResponse.getStatus() + " and message: " + saveResponse.getMessage());
                }

                @Override
                public void onError(Throwable throwable) {
                    LOGGER.error("Received Error with status: " + Status.FAILED + " and message: " + "/");
                    saveStatus = Status.FAILED;
                    onCompleted();
                }

                @Override
                public void onCompleted() {
                    saveStatus = Status.IN_PROGRESS.equals(saveStatus) ? Status.SUCCESS : saveStatus;
                    countDownLatch.countDown();
                }
            });

            if(fileStatus.equals(FileStatus.COMPLETED)) {
                var saveRequest = SaveRequest.newBuilder()
                        .setFile(
                                File.newBuilder()
                                        .setName(file.getName())
                                        .setContent(ByteString.copyFrom(file.getByteArray()))
                                        .build()
                        )
                        .setFileStatus(fileStatus)
                        .build();
                streamObserver.onNext(saveRequest);
            }
            else{
                var fileStream = new BufferedInputStream(new ByteArrayInputStream(file.getByteArray()));
                int bufferSize = file.getByteArray().length/2;
                byte[] buffer = new byte[bufferSize];
                int length;
                int i = 1;
                while ((length = fileStream.read(buffer, 0, bufferSize)) != -1) {
                    var saveRequest = SaveRequest.newBuilder()
                            .setFile(
                                    File.newBuilder()
                                            .setName(file.getName())
                                            .setContent(ByteString.copyFrom(buffer, 0, length))
                                            .build()
                            )
                            .setFileStatus(fileStatus)
                            .setMessage(repeat(i, "*"))
                            .build();
                    streamObserver.onNext(saveRequest);
                    i++;
                    Thread.sleep(1000);
                }
                fileStream.close();
            }
            streamObserver.onCompleted();
            boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
            return saveStatus;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Status.FAILED;
        }
    }

    private void writeFile(File grpcFile) {
        try {
            java.io.File file = new java.io.File(CLIENT_BASE_PATH + grpcFile.getName());
            OutputStream os = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(grpcFile.getContent().toByteArray());

            // Close the file
            os.close();
            LOGGER.info("File with filename: " + grpcFile.getName() + " was saved");
        } catch (IOException e) {
            LOGGER.error("There was a problem with saving file with filename: " + grpcFile.getName());
            e.printStackTrace();
        }
    }

    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }

    public static String repeat(int count) {
        return repeat(count, " ");
    }
}
