package eu.irrationalcharm.userservice.controller.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/identity")
@RequiredArgsConstructor
public class InternalUserIdResolverController {


    @PostMapping("/resolve-or-create")
    public ResponseEntity<String> resolveOrCreateInternalId() {

        return ResponseEntity.ok(UUID.randomUUID().toString());
    }
}
