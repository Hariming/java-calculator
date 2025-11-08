import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Runnable 인터페이스를 구현하여 각 클라이언트의 요청을 개별 스레드에서 처리합니다.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        // try-with-resources: 소켓과 스트림을 자동으로 닫아줌
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true) // true: autoFlush
        ) {
            // 클라이언트로부터 한 줄의 요청을 읽음
            String requestLine = reader.readLine();
            
            if (requestLine != null) {
                System.out.println(clientSocket.getInetAddress().getHostAddress() + "로부터 요청 받음: " + requestLine);
                
                // 요청을 처리하고 응답을 생성
                String response = processRequest(requestLine);
                
                // 클라이언트에게 응답 전송
                writer.println(response);
                System.out.println(clientSocket.getInetAddress().getHostAddress() + "에게 응답 보냄: " + response);
            }

        } catch (IOException e) {
            System.err.println("클라이언트 핸들러 오류: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // 소켓 닫기 오류
            }
        }
    }

    /**
     * 클라이언트의 요청 문자열을 파싱하고 계산하여 프로토콜 응답 문자열을 생성합니다.
     * @param requestLine 클라이언트가 보낸 원본 요청 (예: "ADD 10 20")
     * @return 프로토콜 형식의 응답 (예: "200 30" 또는 "500 DIVIDE_BY_ZERO")
     */
    private String processRequest(String requestLine) {
        // 부록의 split 메소드 활용 [cite: 127]
        String[] tokens = requestLine.trim().split("\\s+"); // 공백 기준으로 분리

        try {
            // 인수 개수 확인 (명령어, 피연산자1, 피연산자2)
            if (tokens.length != 3) {
                return "400 INVALID_ARGUMENTS"; // "too many arguments" 예외 처리 
            }

            String command = tokens[0].toUpperCase();
            double operand1 = Double.parseDouble(tokens[1]);
            double operand2 = Double.parseDouble(tokens[2]);
            double result;

            // 사칙연산 수행 [cite: 23]
            switch (command) {
                case "ADD":
                    result = operand1 + operand2;
                    break;
                case "SUB":
                    result = operand1 - operand2;
                    break;
                case "MUL":
                    result = operand1 * operand2;
                    break;
                case "DIV":
                    if (operand2 == 0) {
                        return "500 DIVIDE_BY_ZERO"; // "divided by zero" 예외 처리 
                    }
                    result = operand1 / operand2;
                    break;
                default:
                    // 정의되지 않은 명령어
                    return "400 UNKNOWN_COMMAND";
            }

            // 계산 성공
            return "200 " + result;

        } catch (NumberFormatException e) {
            // 피연산자가 숫자가 아닌 경우
            return "400 BAD_OPERAND";
        } catch (Exception e) {
            // 그 외 예상치 못한 서버 오류
            return "500 SERVER_ERROR";
        }
    }
}