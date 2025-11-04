package com.bombino.bank_service.deadLetter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/deadletters")
@RequiredArgsConstructor
public class DeadLetterAdminController {
    private final DeadLetterService deadLetterService;
    // todo: Добавить PreAuthorize hasRole ADMIN или конфиг секьюрити


    @GetMapping
    public List<DeadLetter> findAll(){
        return deadLetterService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<DeadLetter> getById(@PathVariable("id") UUID id){
        return deadLetterService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/{id}/requeue")
    public ResponseEntity<String> requeue(@PathVariable("id") UUID id){
        deadLetterService.requeueToOutbox(id);
        return ResponseEntity.accepted().body("Requeued");

    }
    @DeleteMapping()
    public ResponseEntity<Void> delete(UUID id){
        deadLetterService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
