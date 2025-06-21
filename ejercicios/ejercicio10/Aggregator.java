import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Aggregator {
    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, Demo> sendTasksToWorkersCont(List<String> workers, List<String> tasks) {
        CompletableFuture<byte[]>[] futures = (CompletableFuture<byte[]>[]) new CompletableFuture<?>[workers.size()];
        HashMap<String, Demo> results = new HashMap<>();
        String[] delegatedTasks = new String[workers.size()];
        int nextWorker = -1;
        int taskCounter = 0;

        for(int i = 0; i < tasks.size() && i < workers.size(); i++) {
            String workerAddr = workers.get(i);
            String task = tasks.get(i);
            delegatedTasks[i] = task;
            
            Demo requestDemo = new Demo(++taskCounter, Instant.now().toString());
            System.out.println("[CLIENTE] Enviando a " + workerAddr + 
                             ": Tarea=" + task + 
                             ", Objeto=" + requestDemo);
            
            byte[] requestPayload = SerializationUtils.serialize(requestDemo);
            futures[i] = webClient.sendTask(workerAddr, requestPayload);
        }

        for(int taskLeft = workers.size(); taskLeft < tasks.size(); taskLeft++) {
            while(true) {
                for(int workID = 0; workID < futures.length; workID++) {
                    if(futures[workID] != null && futures[workID].isDone()) {
                        try {
                            byte[] responseBytes = futures[workID].get();
                            Demo response = (Demo) SerializationUtils.deserialize(responseBytes);
                            results.put(delegatedTasks[workID], response);
                            nextWorker = workID;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(nextWorker != -1) break;
            }
            
            String workerAddr = workers.get(nextWorker);
            String task = tasks.get(taskLeft);
            delegatedTasks[nextWorker] = task;
            
            Demo requestDemo = new Demo(++taskCounter, Instant.now().toString());
            System.out.println("[CLIENTE] Enviando a " + workerAddr + 
                             ": Tarea=" + task + 
                             ", Objeto=" + requestDemo);
            
            byte[] requestPayload = SerializationUtils.serialize(requestDemo);
            futures[nextWorker] = webClient.sendTask(workerAddr, requestPayload);
            nextWorker = -1;
        }

        for(int i = 0; i < workers.size(); i++) {
            try {
                if(futures[i] != null) {
                    byte[] responseBytes = futures[i].get();
                    Demo response = (Demo) SerializationUtils.deserialize(responseBytes);
                    results.put(delegatedTasks[i], response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}
