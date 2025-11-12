package com.example.lms.controller;

import com.example.lms.service.LmsService;
import jakarta.persistence.Cache;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandHandler {
    private final LmsService service;
    public CommandHandler(LmsService service){this.service = service;}

    public List<String> handle(String raw){
        String[] tokens = raw.split("\\s+");
        if(tokens.length == 0) return List.of("INPUT_DATA_ERROR");
        String cmd = tokens[0].trim();
        try{
            switch (cmd){
                case "ADD-COURDE-OFFERING":
                    if(tokens.length != 6) return List.of("INPUT_DATA_ERROR");
                    return List.of(service.addCourseOffering(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]));
                case "REGISTER":
                    if(tokens.length != 3) return List.of("INPUT_DATA_ERROR");
                    return service.register(tokens[1], tokens[2]);
                case "CANCEL":
                    if(tokens.length != 2) return List.of("INPUT_DATA_ERROR");
                    return List.of(service.cancel(tokens[1]));
                case "ALLOT":
                    if(tokens.length != 2) return service.allot(tokens[1]);
                default:
                    return List.of("INPUT_DATA_ERROR");
            }
        } catch (IllegalArgumentException ex){
            return List.of("INPUT_DATA_ERROR");
        } catch (Exception ex){
            // for safety, return INPUT_DATA_ERROR on unexpected
            return List.of("INPUT_DATA_ERROR");
        }
    }
}
