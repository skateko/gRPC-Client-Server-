package pl.edu.pwr.rsi.grpc.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.rsi.grpc.interfaces.lib.*;
import pl.edu.pwr.rsi.grpc.interfaces.lib.File;
import java.io.*;

@GrpcService
@RequiredArgsConstructor
public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {


    private static final String SERVER_BASE_PATH = "grpc-server/src/main/resources/output/";
    private final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Override
    public StreamObserver<SaveRequest> saveFile(StreamObserver<SaveResponse> responseObserver) {
        return new StreamObserver<>() {

            private String fileName = "";
            private Status status = Status.IN_PROGRESS;

            @Override
            public void onNext(SaveRequest saveRequest) {
                try {
                    LOGGER.info("Received request from client: " + saveRequest.getFile().getName() + " progress: " + saveRequest.getMessage());
                    baos.write(saveRequest.getFile().getContent().toByteArray());
                    fileName = saveRequest.getFile().getName();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                status = Status.FAILED;
                this.onCompleted();
            }

            @Override
            public void onCompleted() {
                writeFile(File.newBuilder().setName(fileName)
                        .setContent(ByteString.copyFrom(baos.toByteArray()))
                        .build()
                );

                status = Status.IN_PROGRESS.equals(status) ? Status.SUCCESS : status;

                if(status == Status.FAILED){
                    responseObserver.onError(new RuntimeException("Could not save file"));
                    return;
                }

                var response = SaveResponse.newBuilder()
                        .setStatus(status)
                        .setMessage("Sent")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void downloadFile(DownloadRequest downloadRequest, StreamObserver<DownloadResponse> responseObserver) {
        if (downloadRequest == null) {
            LOGGER.error("Request is null, cannot download a file!");
            responseObserver.onError(new RuntimeException("Request is null"));
            return;
        }
        LOGGER.info("File with filename: " + downloadRequest.getFileName() + " is downloading");
        var result = readFile(downloadRequest.getFileName());
        if(result == null){
            LOGGER.error("There is no file with name: " + downloadRequest.getFileName());
            responseObserver.onError(new RuntimeException("There is no file with filename: " + downloadRequest.getFileName()));
            return;
        }

        if(downloadRequest.getFileStatus().equals(FileStatus.COMPLETED)) {
            LOGGER.info("Sending full file");
            responseObserver.onNext(
                    DownloadResponse.newBuilder()
                            .setFile(result)
                            .setMessage("Full File")
                            .build()
            );
        }
        else {
            try {
                var fileStream = new BufferedInputStream(new ByteArrayInputStream(result.getContent().toByteArray()));
                int bufferSize = result.getContent().toByteArray().length / 2;
                byte[] buffer = new byte[bufferSize];
                int length;
                int i = 1;
                while ((length = fileStream.read(buffer, 0, bufferSize)) != -1) {
                    var downloadResponse = DownloadResponse.newBuilder()
                            .setFile(
                                    File.newBuilder()
                                            .setName(result.getName())
                                            .setContent(ByteString.copyFrom(buffer, 0, length))
                                            .build()
                            ).setMessage(repeat(i, "*")).build();
                    i++;
                    responseObserver.onNext(downloadResponse);
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
        LOGGER.info("File was downloaded");
    }

    private void writeFile(File grpcFile){
        try {
            LOGGER.info("File with FileName: " + grpcFile.getName() + " is saving");
            java.io.File file = new java.io.File(SERVER_BASE_PATH + grpcFile.getName());
            OutputStream os = new FileOutputStream(file);

            os.write(grpcFile.getContent().toByteArray());

            os.close();
        } catch (IOException e) {
            LOGGER.error("There was an error with saving file " + e.getMessage());
        }
    }

    private File readFile(String fileName){
        try{
            java.io.File file = new java.io.File("grpc-server/src/main/resources/output/" + fileName);
            InputStream is = new FileInputStream(file);
            var result = is.readAllBytes();
            return File.newBuilder()
                    .setName(fileName)
                    .setContent(ByteString.copyFrom(result))
                    .build();
        }catch (IOException e){
            e.printStackTrace();
            LOGGER.error("There was an error with reading file " + e.getMessage());
            return null;
        }
    }

    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }

    public static String repeat(int count) {
        return repeat(count, " ");
    }
}