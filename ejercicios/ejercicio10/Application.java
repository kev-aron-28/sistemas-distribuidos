/*
 *  MIT License
 *
 *  Copyright (c) 2019 Michael Pogrebinsky - Distributed Systems & Cloud Computing with Java
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Application {
    private static final String WORKER_ADDRESS_1 = "http://localhost:8080/task";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8080/task";

    public static void main(String[] args) {
        Aggregator aggregator = new Aggregator();
        List<String> listaTareas = new ArrayList<>();
        listaTareas.add("1");
        
        HashMap<String, Demo> results = aggregator.sendTasksToWorkersCont(Arrays.asList(WORKER_ADDRESS_1, WORKER_ADDRESS_2),
            listaTareas);

        results.forEach((task, demo) -> {
            System.out.println("Task: " + task);
            System.out.println("  Client sent at: " + demo.a);
            System.out.println("  Server responded at: " + demo.b);
        });
    }
}
