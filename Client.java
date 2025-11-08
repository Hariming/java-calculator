import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 계산기 클라이언트 프로그램.
 * 서버 정보를 Config 클래스에서 읽어와 연결합니다[cite: 27, 28].
 */
public class Client {

    private static final String CONFIG_FILE = "server_info.dat";

    public static void main(String[] args) {
        
        // 1. 설정 파일 로드
        String[] config = Config.loadConfig(CONFIG_FILE);
        String host = config[0];
        int port;
        try {
            port = Integer.parseInt(config[1]);
        } catch (NumberFormatException e) {
            System.err.println("포트 번호가 잘못되었습니다. 기본 포트 1234를 사용합니다.");
            port = 1234;
        }

        System.out.println("서버 " + host + ":" + port + "에 연결 시도 중...");

        try (
            // 2. 서버에 소켓 연결
            Socket socket = new Socket(host, port);
            
            // 3. 스트림 준비
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("서버에 연결되었습니다.");
            System.out.println("계산식을 입력하세요 (예: ADD 10 20)");
            System.out.println("종료하려면 'exit'을 입력하세요.");

            String userInput;
            while (true) {
                System.out.print("> ");
                userInput = consoleReader.readLine();

                if (userInput == null || "exit".equalsIgnoreCase(userInput.trim())) {
                    System.out.println("연결을 종료합니다.");
                    break;
                }

                // 4. 서버에 요청 전송
                writer.println(userInput);

                // 5. 서버로부터 응답 수신
                String serverResponse = serverReader.readLine();
                
                // 6. 응답 파싱 및 출력
                parseAndPrintResponse(serverResponse);
            }

        } catch (Exception e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        }
    }

    /**
     * 서버로부터 받은 프로토콜 응답을 해석하여 사용자에게 친화적인 형태로 출력합니다.
     * @param response 서버가 보낸 원본 응답 (예: "200 30")
     */
    private static void parseAndPrintResponse(String response) {
        if (response == null) {
            System.out.println("서버로부터 응답이 없습니다.");
            return;
        }

        // 응답을 "상태 코드"와 "데이터"로 분리 (공백 1개를 기준으로 최대 2개로 나눔)
        String[] parts = response.split(" ", 2);
        String statusCode = parts[0];
        String data = (parts.length > 1) ? parts[1] : "";

        switch (statusCode) {
            case "200":
                System.out.println("Answer: " + data);
                break;
            case "400":
                // 400번대 오류는 클라이언트 요청 오류
                System.out.println("Error: 잘못된 요청입니다. (" + data + ")");
                break;
            case "500":
                // 500번대 오류는 서버 계산 오류
                System.out.println("Error: 서버에서 계산 중 오류가 발생했습니다. (" + data + ")");
                break;
            default:
                System.out.println("Error: 알 수 없는 응답입니다. (" + response + ")");
        }
    }
}