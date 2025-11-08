import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    public static final int PORT = 5001; // 서버 기본 포트
    public static final int THREAD_POOL_SIZE = 10; // 스레드 풀 크기

    public static void main(String[] args) {
        // 고정된 크기의 스레드 풀 생성 
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT + "...");

            while (true) {
                // 클라이언트의 연결을 기다림 (accept)
                Socket clientSocket = serverSocket.accept();
                System.out.println("새 클라이언트 연결됨: " + clientSocket.getInetAddress().getHostAddress());

                // 연결된 클라이언트를 스레드 풀의 작업 큐에 제출
                threadPool.submit(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("서버 소켓 오류: " + e.getMessage());
            // 스레드 풀 종료
            threadPool.shutdown();
        }
    }
}
