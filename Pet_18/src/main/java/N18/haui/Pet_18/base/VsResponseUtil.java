package N18.haui.Pet_18.base;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class VsResponseUtil {
    public static ResponseEntity<RestData<?>> success(Object data){
        return success(HttpStatus.OK,data);
    }

    public static ResponseEntity<RestData<?>> success(HttpStatus status, Object data){
        RestData<?> response = new RestData<>(data);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<RestData<?>> success(MultiValueMap<String, String> header, Object data){
        return success(header, HttpStatus.OK, data);
    }
    public static ResponseEntity<RestData<?>> success(MultiValueMap<String, String> header, HttpStatus status, Object data){
        RestData<?> response = new RestData<>(data);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.addAll((HttpHeaders)header);
        return ResponseEntity.status(status)
                .headers(responseHeaders)
                .body(response);
    }

    public static ResponseEntity<RestData<?>> error(HttpStatus status, Object message){
        RestData<?> response = RestData.error(message);
        return  ResponseEntity.status(status)
                .body(response);
    }


}
