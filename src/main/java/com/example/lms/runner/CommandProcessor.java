package com.example.lms.runner;

import com.example.lms.controller.CommandHandler;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class CommandProcessor {
    private final CommandHandler handler;
    public CommandProcessor(CommandHandler handler) {this.handler = handler;}

    public void processStdin(){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
            String line;
            while ((line = br.readLine()) != null){
                if (line.trim().isEmpty()) continue;
                //Each input command excepts output on same line; handler returns output lines (possibly multiple)
                var outputs = handler.handle(line.trim());
                for (String out : outputs){
                    System.out.println(out);
                }
            }
        } catch (Exception e){
            // Unexpected errors print nothing (or you could print error)
            e.printStackTrace();
        }
    }
}
