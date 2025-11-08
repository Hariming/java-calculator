import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * server_info.dat 파일에서 서버 IP와 포트 번호를 읽어오는 클래스.
 * 파일이 없을 경우 기본값(localhost, 1234)을 반환합니다[cite: 29].
 */
public class Config {

    /**
     * 설정 파일에서 서버 정보(IP, 포트)를 읽어옵니다.
     * @param filename 설정 파일 이름 (예: "server_info.dat")
     * @return String 배열: [0] = IP 주소, [1] = 포트 번호
     */
    public static String[] loadConfig(String filename) {
        String[] config = new String[2];
        File file = new File(filename);

        try (Scanner scanner = new Scanner(file)) {
            // 파일이 존재하는 경우
            System.out.println("'" + filename + "' 파일에서 설정을 읽어옵니다.");
            config[0] = scanner.nextLine(); // 첫 번째 줄: IP
            config[1] = scanner.nextLine(); // 두 번째 줄: Port
        } catch (FileNotFoundException e) {
            // 파일이 존재하지 않는 경우 [cite: 29]
            System.out.println("'" + filename + "' 파일을 찾을 수 없습니다. 기본값(localhost:1234)을 사용합니다.");
            config[0] = "localhost";
            config[1] = "5001";
        } catch (Exception e) {
            // 파일 형식 오류 등 기타 예외
            System.out.println("설정 파일 읽기 오류. 기본값(localhost:1234)을 사용합니다.");
            config[0] = "localhost";
            config[1] = "5001";
        }
        return config;
    }
}