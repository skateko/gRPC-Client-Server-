package pl.edu.pwr.rsi.grpc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DownloadRequestDto {

    private String fileName;

    private int fileStatus;

}